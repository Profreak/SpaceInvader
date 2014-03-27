package de.l_infotech.spaceinvader.game;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import de.l_infotech.spaceinvader.connection.DisplayConnection;
import de.l_infotech.spaceinvader.game.components.StaticMatrix;
import de.l_infotech.spaceinvader.game.sound.Soundboard;
import de.l_infotech.spaceinvader.game.spaceobjects.EnemyShip;
import de.l_infotech.spaceinvader.game.spaceobjects.Laser;
import de.l_infotech.spaceinvader.game.spaceobjects.PlayerShip;

/**
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public class SpaceEngine extends Thread implements SensorEventListener,
		OnTouchListener {

	// Debugging Information
	private final String TAG = SpaceEngine.class.getSimpleName();
	public final String TAG_SENSOR = "Sensor";
	public final String TAG_LASER = "Laser";
	public final String TAG_USER = "User";
	public final String TAG_ENEMY = "User";

	// Start Position
	// Player
	public static final int START_PLAYER_X = 20;
	public static final int START_PLAYER_Y = 5;
	public static final int START_PLAYER_LIVES = 8;

	// Ships
	public static final int SHIP_WIDTH = 3;
	public static final int SHIP_HEIGHT = 3;

	// Laser
	public static final int LASER_THICKNESS = 1;

	// Resolution
	public static final int MAX_RESOLUTION = 24;
	public static final int BOTTOM_BORDER = 7;

	// Graphics
	public static final byte SHINE = (byte) 255;

	// Score
	private static final int ENEMY_VALUE = 10;
	private static final int STAGE_VALUE = 10;

	// Speed
	private static final int ENEMY_SPEED_FAKTOR = 100; // higher -> faster per
														// enemy
	private static final int ENEMY_SHOOT_DELAY_FAKTOR = 200;

	public static final int LASER_SPEED = 100; // lower -> faster
	private static final int SHOOT_DELAY = 10 * LASER_SPEED;

	public static final int GAME_SPEED = 80; // lower -> faster

	public static final int GAME_INITIALISATION_WAIT_TIME = 1000; // lower ->
																	// faster
																	// start

	public static final int SENSOR_SENSITIVITY = 100; // lower -> more controll

	// Space Objects
	private PlayerShip player;

	private List<EnemyShip> enemys;
	private List<Laser> lasers;

	// Game Environment
	private byte[][] field;
	private int score;
	private int stage;
	private List<GameStatusListener> scoreListener;

	// Audio
	private Soundboard sb;

	// Sensor
	private double sensorDiff = 0.0;

	// Connection to the Display
	private DisplayConnection connection;

	// Helper Variables
	private AtomicBoolean justShote;
	private boolean isRunning = false;
	private boolean pause = false;

	/**
	 * Creats a new Space Engine
	 * 
	 * @param connection
	 *            the way of Connection to the Display
	 * @param sb
	 */
	public SpaceEngine(DisplayConnection connection, Soundboard sb) {
		Log.d(TAG, "set up the game");

		Log.d(TAG, "set up the display connection");
		this.connection = connection;

		Log.d(TAG, "set up the game field");
		field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];
		lasers = new LinkedList<Laser>();
		enemys = new LinkedList<EnemyShip>();

		justShote = new AtomicBoolean();
		this.sb = sb;
		score = 0;
		stage = 1;
		scoreListener = new LinkedList<GameStatusListener>();

		Log.d(TAG, "set up player and enemys");
		player = new PlayerShip(START_PLAYER_X, START_PLAYER_Y, SHIP_WIDTH,
				SHIP_HEIGHT, START_PLAYER_LIVES);
		initEnemy();
	}

	@Override
	public void run() {

		// starts the Game
		this.initGame();
		isRunning = true;

		// start the Enemy AI
		EnemyThread t = new EnemyThread();
		t.start();
		EnemyLaserThread t2 = new EnemyLaserThread();
		t2.start();

		while (isRunning) {
			if (!pause) {
				// clear the Field
				field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];

				// Display User

				Log.d(TAG, "move and paint the player");
				this.movePlayer();

				if (!player.isAlive()) {
					isRunning = false;
					destroyPlayer();
//					sb.playSound(Soundboard.EXPLOSION);

				}

				// Display lasers

				Log.d(TAG, "move and paint the player");
				this.moveLasers();

				// Display Enemys

				Log.d(TAG, "move and paint the enemy");
				this.moveEnemy();

				// PAINT THE DISPLAY
				sendField(field);

				// alle enemys killed-> next stage
				if (enemys.size() == 0) {
					stage++;
					score += STAGE_VALUE;
					notifyScoreListener();
					notifyStageListener();
					initEnemy();
				}
			}
			try {
				sleep(GAME_SPEED);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		// show game over
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.showGameOver();
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// return to Menu
		this.notifyGameOverListener();
	}

	/**
	 * This class handles Laser Shoots
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	private class LaserThread extends Thread {

		// Laser direction
		private int direction = 1;

		// the cur Laser
		private Laser laser;

		/**
		 * Creates a new Laser Thread
		 * 
		 * @param direction
		 *            start direction +1 against enemys -1 against player
		 * @param x
		 *            start x-position
		 * @param y
		 *            start y-position
		 */
		public LaserThread(int direction, int x, int y) {
			Log.d(TAG_LASER, "Set up an new Laser Thread");
			this.direction = direction;
			laser = new Laser(x, y, LASER_THICKNESS);
			lasers.add(laser);
		}

		@Override
		public void run() {
			Log.d(TAG_LASER, "Start Laser Thread");
			while (laser.isAlive()) {
				if (!pause) {

					// moves the Laser
					if (!laser.getCoordinates().move(direction, 0)) {
						laser.destroy();
						Log.d(TAG_LASER,
								"Move Laser x: " + laser.getCoordinates().x0
										+ " y: " + laser.getCoordinates().y0);
					}

					if (direction == -1) {
						// check wehter hits a enemy
						for (EnemyShip value : enemys) {
							if (value.getCoordinates().isHit(
									laser.getCoordinates().x0,
									laser.getCoordinates().y0)) {
								Log.d(TAG_LASER, "HIT");
								value.destroy();
								laser.destroy();
							}
						}
					} else {
						if (player.getCoordinates().isHit(
								laser.getCoordinates().x0,
								laser.getCoordinates().y0)) {
							Log.d(TAG_LASER, "HIT at Player");
							player.destroy();
							laser.destroy();
							sb.playSound(Soundboard.EXPLOSION);
							notifyLivesListener();
						}
					}
				}
				// wait for the next step
				try {
					sleep(LASER_SPEED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * This class handles enemy movement
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	private class EnemyThread extends Thread {

		// direction of movement
		private int direction_y;
		private int direction_x;

		/**
		 * set up a new enemy Thread
		 */
		public EnemyThread() {
			Log.d(TAG_ENEMY, "Set Up new Enemy Thread");
			direction_y = 1;
			direction_x = 1;
		}

		@Override
		public void run() {
			Log.d(TAG_ENEMY, "Start Enemy Movment Thread");
			while (true) {

				if (!pause) {
					// helper Variables
					boolean change = false;
					boolean changeX = false;

					// check if have to change direction x/y
					for (EnemyShip value : enemys) {
						int y0 = value.getCoordinates().y0 + direction_y;
						int y1 = value.getCoordinates().y1 + direction_y;

						if (direction_y == -1 && y0 < 0) {
							change = true;
							break;
						}

						if (direction_y == 1 && y1 > 23) {
							change = true;
							break;
						}

						int x0 = value.getCoordinates().x0 + direction_x;
						int x1 = value.getCoordinates().x1 + direction_x;

						if (direction_x == -1 && x0 <= 0) {
							changeX = true;
						}

						if (direction_x == 1 && x1 >= BOTTOM_BORDER) {
							changeX = true;
						}

					}

					// do the change
					if (change) {

						if (changeX) {
							if (direction_x == 1) {
								direction_x = -1;
							} else {
								direction_x = 1;
							}
						}
						// Move
						synchronized (enemys) {
							for (EnemyShip value : enemys) {
								Log.d(TAG_ENEMY,
										"Move Enemy x: "
												+ value.getCoordinates().x0
												+ " y: "
												+ value.getCoordinates().y0);
								value.getCoordinates().move(direction_x, 0);
							}
						}

						if (direction_y == 1) {
							direction_y = -1;
						} else {
							direction_y = 1;
						}

					} else {
						// Move
						for (EnemyShip value : enemys) {
							Log.d(TAG_ENEMY,
									"Move Enemy x: "
											+ value.getCoordinates().x0
											+ " y: "
											+ value.getCoordinates().y0);
							value.getCoordinates().move(0, direction_y);
						}
					}
				}
				// sleep a time. If there more enemy enemys will be slower
				try {
					sleep(ENEMY_SPEED_FAKTOR * enemys.size());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Listener

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[1];
		float y = event.values[0];
		sensorDiff = Math.atan2(x, y) / (Math.PI / 180);

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN && !justShote.get()) {
			DelayHelperThread t = new DelayHelperThread();
			t.start();
		}
		return false;
	}

	/**
	 * 
	 * A Helper Class to make a timespace between two shots
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	private class DelayHelperThread extends Thread {

		@Override
		public void run() {
			synchronized (justShote) {
				justShote.set(true);
				Log.d(TAG_LASER, "Fire laser");
				DelayThread d = new DelayThread();
				LaserThread l = new LaserThread(-1, player.getCoordinates().x0,
						(player.getCoordinates().y0 + 1));// grafik hack
				l.start();
				sb.playSound(Soundboard.PLAYERFIRE);
				d.start();
			}

		}
	}

	/**
	 * add a Score Listener
	 * 
	 * @param listener
	 *            the new Listener
	 */
	public void addScoreListener(GameStatusListener listener) {
		this.scoreListener.add(listener);
		this.notifyScoreListener();
		this.notifyStageListener();
		this.notifyLivesListener();
	}

	/**
	 * notify all Score listener
	 */
	private void notifyScoreListener() {
		for (GameStatusListener value : scoreListener) {
			value.setScore(score);
		}
	}

	/**
	 * notify all Stage listener
	 */
	private void notifyStageListener() {
		for (GameStatusListener value : scoreListener) {
			value.setStage(stage);
		}
	}

	/**
	 * notify all Live listener
	 */
	private void notifyLivesListener() {
		for (GameStatusListener value : scoreListener) {
			value.setPlayerLives(player.getLives());
		}
	}

	/**
	 * notify all Live listener
	 */
	private void notifyGameOverListener() {
		for (GameStatusListener value : scoreListener) {
			value.gameOver();
		}
	}

	// Helper methods
	/**
	 * does the Player turn
	 */
	private void movePlayer() {
		this.insertGamefield(player.getGraphics(), player.getCoordinates().x0,
				player.getCoordinates().y0);
		int move_y = (int) Math.round(sensorDiff / SENSOR_SENSITIVITY);
		this.player.getCoordinates().move(0, move_y);
	}

	/**
	 * does the Player turn
	 */
	private void destroyPlayer() {
		this.insertGamefield(player.getDestroyGraphics(),
				player.getCoordinates().x0, player.getCoordinates().y0);
	}

	/**
	 * does the Lasers turn
	 */
	private void moveLasers() {
		for (int z = 0; z < lasers.size(); z++) {
			Laser value = lasers.get(z);
			if (value.isAlive()) {
				insertGamefield(value.getGraphics(), value.getCoordinates().x0,
						value.getCoordinates().y0);
			} else {
				lasers.remove(z);
			}
		}
	}

	/**
	 * does the Enemys turn
	 */
	private void moveEnemy() {
		synchronized (enemys) {
			for (int z = 0; z < enemys.size(); z++) {
				EnemyShip value = enemys.get(z);
				if (value.isAlive()) {
					insertGamefield(value.getGraphics(),
							value.getCoordinates().x0,
							value.getCoordinates().y0);
				} else {
					insertGamefield(value.getDestroyGraphics(),
							value.getCoordinates().x0,
							value.getCoordinates().y0);
					enemys.remove(z);
					score += ENEMY_VALUE;
					notifyScoreListener();
					sb.playSound(Soundboard.EXPLOSION);
				}
			}
		}
	}

	/**
	 * shows GAME OVER
	 */
	private void showGameOver() {
		field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];
		this.insertGamefield(StaticMatrix.GAME_OVER, 0, 0);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];
		this.insertGamefield(StaticMatrix.GAME_OVER, 0, 0);
		this.sendField(field);

	}

	/**
	 * starts the countdown
	 */
	private void initGame() {

		field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];
		byte[][] cur;

		cur = StaticMatrix.three12;
		this.insertGamefield(cur, 0, 0);

		cur = StaticMatrix.three12;
		this.insertGamefield(cur, 12, 0);

		cur = StaticMatrix.three12;
		this.insertGamefield(cur, 0, 12);

		cur = StaticMatrix.three12;
		this.insertGamefield(cur, 12, 12);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		cur = StaticMatrix.two12;
		this.insertGamefield(cur, 0, 0);

		cur = StaticMatrix.two12;
		this.insertGamefield(cur, 12, 0);

		cur = StaticMatrix.two12;
		this.insertGamefield(cur, 0, 12);

		cur = StaticMatrix.two12;
		this.insertGamefield(cur, 12, 12);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cur = StaticMatrix.one12;
		this.insertGamefield(cur, 0, 0);

		cur = StaticMatrix.one12;
		this.insertGamefield(cur, 12, 0);

		cur = StaticMatrix.one12;
		this.insertGamefield(cur, 0, 12);

		cur = StaticMatrix.one12;
		this.insertGamefield(cur, 12, 12);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cur = StaticMatrix.null12;
		this.insertGamefield(cur, 0, 0);

		cur = StaticMatrix.null12;
		this.insertGamefield(cur, 12, 0);

		cur = StaticMatrix.null12;
		this.insertGamefield(cur, 0, 12);

		cur = StaticMatrix.null12;
		this.insertGamefield(cur, 12, 12);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];

	}

	/**
	 * initiates enemy ships
	 */
	private void initEnemy() {
		for (int x = 0; x < BOTTOM_BORDER; x = x + 5) {
			for (int y = 0; y < MAX_RESOLUTION; y = y + 4) {
				EnemyShip enemy = new EnemyShip(x, y, SHIP_WIDTH, SHIP_HEIGHT);
				enemys.add(enemy);
				// hitables.add(enemy);
			}
		}
	}

	/**
	 * 
	 * Insert graphics into the field
	 * 
	 * @param graphic
	 *            the graphics to write
	 * @param coor_x
	 *            the start x Coordinate
	 * @param coor_y
	 *            the start y Coordinate
	 */
	public void insertGamefield(byte[][] graphic, int coor_x, int coor_y) {

		for (int x = 0; x < graphic.length; x++) {
			int tmp_y = coor_y;
			for (int y = 0; y < graphic[0].length; y++) {
				Log.d(TAG, "x: " + coor_x + " y:" + tmp_y + " byte:"
						+ graphic[x][y]);
				if (coor_x < MAX_RESOLUTION && coor_y < MAX_RESOLUTION) {
					field[coor_x][tmp_y] = graphic[x][y];
				}
				tmp_y++;
			}
			coor_x++;
		}
	}

	/**
	 * Sends the game-field to the Connection
	 * 
	 * @param field
	 *            the game field
	 */
	public void sendField(byte[][] field) {
		synchronized (field) {
			byte[] matrixInMessage = new byte[MAX_RESOLUTION * MAX_RESOLUTION];

			Arrays.fill(matrixInMessage, (byte) 0);

			for (int x = 0; x < field.length; x++) {
				for (int y = 0; y < field[0].length; y++) {
					matrixInMessage[x * MAX_RESOLUTION + y] = field[x][y];
				}
			}

			connection.send(matrixInMessage);

		}

	}

	/**
	 * 
	 * A Helper Class to make a timespace between two shots
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	private class DelayThread extends Thread {

		@Override
		public void run() {
			synchronized (justShote) {
				try {
					sleep(SHOOT_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				justShote.set(false);
			}
		}
	}

	/**
	 * 
	 * A Helper Class to make a timespace between two shots of the Enemys
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	private class EnemyLaserThread extends Thread {

		int MAX = 100;
		int MIN = 0;
		int SHOOT_CHANCE = 20;

		@Override
		public void run() {
			while (isRunning) {
				if (!pause) {
					Random random = new Random();

					try {
						for (EnemyShip value : enemys) {
							int z = random.nextInt(MAX - MIN + 1) + MIN;
							if (z < SHOOT_CHANCE) {
								Log.d(TAG_LASER, "Fire Enemy laser");
								synchronized (justShote) {
									LaserThread l = new LaserThread(1,
											value.getCoordinates().x0,
											(value.getCoordinates().y0 + 1));// grafik
																				// hack
									l.start();
									sb.playSound(Soundboard.ENEMYFIRE);
								}
							}
						}
					} catch (ConcurrentModificationException e) {
					}
				}
				try {
					sleep(ENEMY_SHOOT_DELAY_FAKTOR * enemys.size());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * pause the game
	 */
	public void pause() {
		pause = true;
	}

	/**
	 * resume the game
	 */
	public void continueGame() {
		this.resumeGame();
		pause = false;
	}

	/**
	 * starts the countdown
	 */
	private void resumeGame() {

		byte[][] cur;

		cur = StaticMatrix.three12;
		this.insertGamefield(cur, 12, 0);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		cur = StaticMatrix.two12;
		this.insertGamefield(cur, 12, 0);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		cur = StaticMatrix.one12;
		this.insertGamefield(cur, 12, 0);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		cur = StaticMatrix.null12;
		this.insertGamefield(cur, 12, 0);
		this.sendField(field);

		try {
			sleep(GAME_INITIALISATION_WAIT_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gives the current score back
	 * 
	 * @return the current score
	 */
	public int getScore() {
		return score;
	}
}
