package de.l_infotech.spaceinvader.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import de.l_infotech.spaceinvader.WirelessConnection;

/**
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public class SpaceEngine extends Thread implements SensorEventListener,
		OnTouchListener {

	private final String TAG = SpaceEngine.class.getSimpleName();
	public final String TAG_SENSOR = "Sensor";
	public final String TAG_LASER = "Laser";
	// Mac of Lenovo
	public final String address = "20:16:D8:0F:8E:B0";
	// TeCo Rasperry Pi Bluetooth MAC
	// private static String address = "5C:F3:70:02:D7:C7";

	public static final int START_PLAYER_X = 20;
	public static final int START_PLAYER_Y = 6;
	public static final int START_PLAYER_LIVES = 1;

	private static final int ENEMY_SPEED = 500;

	public static final int SHIP_WIDTH = 3;
	public static final int SHIP_HEIGHT = 3;

	public static final int LASER_SPEED = 100;
	public static final int LASER_THICKNESS = 1;

	public static final byte SHINE = (byte) 255;

	public static final int GAME_SPEED = 100;

	public static final int SENSITIVITY = 100;

	public static final int MAX_RESOLUTION = 24;
	public static final int BOTTOM_BORDER = 10;

	private PlayerShip player;
	private List<EnemyShip> enemys;
	private List<Laser> lasers;
	// private List<SpaceObject> hitables;

	private int score;

	private double angleZ = 0.0;

	private WirelessConnection connection;

	private boolean run;

	private byte[][] field;

	/**
	 * 
	 * @param connection
	 *            the way of Connection to the Display
	 */
	public SpaceEngine(WirelessConnection connection) {
		Log.d(TAG, "set up the game");

		this.connection = connection;

		field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];
		lasers = new LinkedList<Laser>();

		score = 0;

		player = new PlayerShip(START_PLAYER_X, START_PLAYER_Y, SHIP_WIDTH,
				SHIP_HEIGHT, START_PLAYER_LIVES);
		// hitables = new LinkedList<SpaceObject>();
		enemys = new LinkedList<EnemyShip>();
		/*
		 * // Enemy Hack first 6 enemys.add(new EnemyShip(1, 0, SHIP_WIDTH,
		 * SHIP_HEIGHT)); enemys.add(new EnemyShip(1, 4, SHIP_WIDTH,
		 * SHIP_HEIGHT)); enemys.add(new EnemyShip(1, 8, SHIP_WIDTH,
		 * SHIP_HEIGHT));
		 * 
		 * enemys.add(new EnemyShip(1, 12, SHIP_WIDTH, SHIP_HEIGHT));
		 * enemys.add(new EnemyShip(1, 16, SHIP_WIDTH, SHIP_HEIGHT)); //
		 * enemys.add(new EnemyShip(1, 20, SHIP_WIDTH, SHIP_HEIGHT));
		 * 
		 * // next 6
		 * 
		 * enemys.add(new EnemyShip(6, 0, SHIP_WIDTH, SHIP_HEIGHT));
		 * enemys.add(new EnemyShip(6, 4, SHIP_WIDTH, SHIP_HEIGHT));
		 * enemys.add(new EnemyShip(6, 8, SHIP_WIDTH, SHIP_HEIGHT));
		 * 
		 * enemys.add(new EnemyShip(6, 12, SHIP_WIDTH, SHIP_HEIGHT));
		 * enemys.add(new EnemyShip(6, 16, SHIP_WIDTH, SHIP_HEIGHT)); //
		 * enemys.add(new EnemyShip(6, 20, SHIP_WIDTH, SHIP_HEIGHT));
		 */

		// initEnemy();
		EnemyShip s = new EnemyShip(1, 0, SHIP_WIDTH, SHIP_HEIGHT);
		EnemyShip s2 = new EnemyShip(1, 4, SHIP_WIDTH, SHIP_HEIGHT);

		enemys.add(s);
		// hitables.add(s);

		enemys.add(s2);
		// hitables.add(s2);

		// hitables.add(player);
		lasers = new LinkedList<Laser>();

		connection.connect(address);

	}

	private void initEnemy() {
		for (int x = 1; x < BOTTOM_BORDER; x = x + 5) {
			for (int y = 0; y < MAX_RESOLUTION; y = y + 4) {
				EnemyShip enemy = new EnemyShip(x, y, SHIP_WIDTH, SHIP_HEIGHT);
				enemys.add(enemy);
				// hitables.add(enemy);
			}
		}
	}

	public void insertGamefield(byte[][] grafik, int coor_x, int coor_y) {

		for (int x = 0; x < grafik.length; x++) {
			int tmp_y = coor_y;
			for (int y = 0; y < grafik[0].length; y++) {
				Log.d(TAG, "x: " + coor_x + " y:" + tmp_y + " byte:"
						+ grafik[x][y]);
				if (coor_x < MAX_RESOLUTION && coor_y < MAX_RESOLUTION) {
					field[coor_x][tmp_y] = grafik[x][y];
				}
				tmp_y++;
			}
			coor_x++;
		}
	}

	/**
	 * Sends the game-field to the Connection
	 * 
	 * @param led
	 */
	public void sendField(byte[][] led) {
		synchronized (led) {
			byte[] matrixInMessage = new byte[MAX_RESOLUTION * MAX_RESOLUTION];

			Arrays.fill(matrixInMessage, (byte) 0);

			for (int x = 0; x < led.length; x++) {
				for (int y = 0; y < led[0].length; y++) {
					matrixInMessage[x * MAX_RESOLUTION + y] = led[x][y];
				}
			}

			connection.send(matrixInMessage);

		}

	}

	@Override
	public void run() {

		EnemyThread t = new EnemyThread();
		// t.start();

		while (true) {
			field = new byte[MAX_RESOLUTION][MAX_RESOLUTION];

			this.insertGamefield(player.getGrafik(),
					player.getCoordinates().x0, player.getCoordinates().y0);
			Log.d(TAG_SENSOR,
					"AngelZ: " + ((int) -Math.round(angleZ / SENSITIVITY)));
			int move_y = (int) -Math.round(angleZ / SENSITIVITY);
			this.player.getCoordinates().move(0, move_y);

			Log.d(TAG, "Lasers size: " + lasers.size());

			// Display lasers

			for (int z = 0; z < lasers.size(); z++) {
				Laser value = lasers.get(z);
				if (value.isAlive()) {
					insertGamefield(value.getGrafik(),
							value.getCoordinates().x0,
							value.getCoordinates().y0);
				} else {
					lasers.remove(z);
				}
			}

			// Display Enemys

			synchronized (enemys) {
				for (int z = 0; z < enemys.size(); z++) {
					EnemyShip value = enemys.get(z);
					if (value.isAlive()) {
						insertGamefield(value.getGrafik(),
								value.getCoordinates().x0,
								value.getCoordinates().y0);
					} else {
						insertGamefield(value.getDestroyGrafik(),
								value.getCoordinates().x0,
								value.getCoordinates().y0);
						enemys.remove(z);
					}
				}
			}
			
			Log.d("Status", "Gegner Anzahl: " + enemys.size());
			sendField(field);
			try {
				sleep(GAME_SPEED);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		angleZ = Math.atan2(x, y) / (Math.PI / 180);

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && !justShot) {
			Log.d(TAG_LASER, "Fire laser");
			justShot = true;
			DelayThread d = new DelayThread();
			LaserThread l = new LaserThread(-1, player.getCoordinates().x0,
					(player.getCoordinates().y0 + 1));// grafik hack
			l.start();
			d.start();
			return true;
		}
		return false;
	}

	private boolean justShot = false;

	private class DelayThread extends Thread {

		private static final int SHOOT_DELAY = 4 * LASER_SPEED;

		@Override
		public void run() {
			try {
				sleep(SHOOT_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			justShot = false;
		}
	}

	private class LaserThread extends Thread {

		private int direction = 1;
		private Laser laser;

		public LaserThread(int direction, int x, int y) {
			this.direction = direction;
			laser = new Laser(x, y, LASER_THICKNESS);
			lasers.add(laser);
		}

		@Override
		public void run() {
			Log.d(TAG_LASER, "Start Laser Thread");
			while (laser.isAlive()) {

				if (!laser.getCoordinates().move(direction, 0)) {
					laser.destroy();
				} else {
					synchronized (enemys) {
						for (int x = 0; x < enemys.size(); x++) {
							SpaceObject value = enemys.get(x);
							if (value.getCoordinates().isHit(
									laser.getCoordinates().x0,
									laser.getCoordinates().y0)) {
								// enemys.remove(x);
								laser.destroy();
								value.destroy();
							}
						}
					}
				}

				Log.d(TAG_LASER, "Move Laser x: " + laser.getCoordinates().x0
						+ " y: " + laser.getCoordinates().y0);

				try {
					sleep(LASER_SPEED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class EnemyThread extends Thread {

		private int direction_y;
		private int direction_x;

		public EnemyThread() {
			direction_y = 1;
			direction_x = 1;
		}

		@Override
		public void run() {
			Log.d(TAG_LASER, "Start Laser Thread");
			while (true) {
				boolean change = false;
				boolean changeX = false;

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

				if (change) {

					if (changeX) {
						if (direction_x == 1) {
							direction_x = -1;
						} else {
							direction_x = 1;
						}
					}

					for (EnemyShip value : enemys) {
						Log.d(TAG_LASER,
								"Move Laser x: " + value.getCoordinates().x0
										+ " y: " + value.getCoordinates().y0);
						value.getCoordinates().move(direction_x, 0);
					}

					if (direction_y == 1) {
						direction_y = -1;
					} else {
						direction_y = 1;
					}

				} else {
					for (EnemyShip value : enemys) {
						Log.d(TAG_LASER,
								"Move Laser x: " + value.getCoordinates().x0
										+ " y: " + value.getCoordinates().y0);
						value.getCoordinates().move(0, direction_y);
					}
				}

				try {
					sleep(ENEMY_SPEED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
