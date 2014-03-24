package de.l_infotech.spaceinvader.game;

import java.util.LinkedList;

import android.util.Log;

import de.l_infotech.spaceinvader.WirelessConnection;

/**
 * 
 * @author Ludwig Biermann
 * @version 1.0
 *
 */
public class SpaceEngine {

	private final String TAG = SpaceEngine.class.getSimpleName();
	// Mac of Lenovo 
	private final String address = "20:16:D8:0F:8E:B0";
	// TeCo Rasperry Pi Bluetooth MAC
	//private static String address = "5C:F3:70:02:D7:C7";
	
	private int MAX_X = 200;
	private int MAX_Y = 200;
	
	private int[][] field;
	private PlayerShip player;
	private LinkedList<EnemyShip> enemys;
	private LinkedList<Laser> lasers;
	
	private int score;
	
	private WirelessConnection connection;
	
	/**
	 * 
	 * @param connection the way of Connection to the Display
	 */
	public SpaceEngine(WirelessConnection connection) {
		Log.d(TAG, "set up the game");
		
		score = 0;
		
		player = new PlayerShip(0,0,0,0,0);
		
		enemys = new LinkedList<EnemyShip>();
		enemys.add(new EnemyShip(0, 0, 0, 0));
		enemys.add(new EnemyShip(0, 0, 0, 0));
		enemys.add(new EnemyShip(0, 0, 0, 0));
		enemys.add(new EnemyShip(0, 0, 0, 0));
		enemys.add(new EnemyShip(0, 0, 0, 0));
		
		
		lasers = new LinkedList<Laser>();
		
		connection.connect(address);
	}
	
	/**
	 * start the Game
	 */
	public void start() {
		
	}
	
	/**
	 * This handel the enemys
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 *
	 */
	class enemyThread extends Thread {
		
	}
	
	/**
	 * This handel the enemys
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	class playerThread extends Thread {
		
	}
	
}
