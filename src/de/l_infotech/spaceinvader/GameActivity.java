package de.l_infotech.spaceinvader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.l_infotech.spaceinvader.connection.BluetoothConnector;
import de.l_infotech.spaceinvader.connection.DisplayConnection;
import de.l_infotech.spaceinvader.game.GameStatusListener;
import de.l_infotech.spaceinvader.game.SpaceEngine;
import de.l_infotech.spaceinvader.game.sound.Soundboard;
import de.l_infotech.spaceinvader.storage.StaticIO;

/**
 * This is the game themself
 * 
 * @author Ludwig Biermannn
 * @version 1.0
 * 
 */
public class GameActivity extends Activity implements GameStatusListener {

	// Debugging Information
	private static final String TAG = GameActivity.class.getSimpleName();
	private static final String TAG_STATUS_UPDATE = "StatusUpdate";
	public static final String HIGHSCORE_FILENAME = "highscore";

	// Game Variables
	private DisplayConnection connection;
	private SpaceEngine game;

	// Views
	private TextView scoreView;
	private TextView stageView;
	private LinearLayout livesLayout;

	// Sensor
	private SensorManager sm;

	// Mac of my own Lenovo Bluetooth Adapter
	public final String address = "20:16:D8:0F:8E:B0";

	// TeCo Rasperry Pi Bluetooth MAC
	// private static String address = "5C:F3:70:02:D7:C7";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "Create Game Activity Views");
		setContentView(R.layout.activity_game);
		scoreView = (TextView) this.findViewById(R.id.score);
		stageView = (TextView) this.findViewById(R.id.stage);
		livesLayout = (LinearLayout) this.findViewById(R.id.lives);

		Log.d(TAG, "Set UP Connection");
		connection = new BluetoothConnector();
		if (!connection.isSupported()) {
			Toast.makeText(getApplicationContext(),
					"buy a new Device .... with Bluetooth!", Toast.LENGTH_LONG)
					.show();
			returnToMenu();
		}
		if (!connection.isEnable()) {
			if (!connection.startAdapter(this)) {
				Toast.makeText(getApplicationContext(), "unexpected error",
						Toast.LENGTH_LONG).show();
				returnToMenu();
			}
		}
		connection.connect(address);

		Log.d(TAG, "Set Up Sensor Manager");
		sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		Sensor s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		Log.d(TAG, "Set Up Audio");
		Soundboard sb = new Soundboard(this);

		Log.d(TAG, "Set Up Game");
		game = new SpaceEngine(connection, sb);
		this.findViewById(R.id.fireButton).setOnTouchListener(game);
		sm.registerListener(game, s, SensorManager.SENSOR_DELAY_GAME);
		game.addScoreListener(this);
		game.start();
	}

	/**
	 * return to Menu
	 */
	private void returnToMenu() {
		Intent nextScreen = new Intent(getApplicationContext(),
				MainMenuActivity.class);
		startActivity(nextScreen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public void setScore(final int score) {
		Log.d(TAG_STATUS_UPDATE, " Score " + (scoreView == null));
		runOnUiThread(new Runnable() {
			public void run() {
				scoreView.setText("Score: " + score);

			}
		});
	}

	@Override
	public void setPlayerLives(final int lives) {
		Log.d(TAG_STATUS_UPDATE, " Lives " + lives);
		runOnUiThread(new Runnable() {
			public void run() {
				livesLayout.removeAllViews();
				for (int x = 0; x < lives; x++) {
					ImageView ship = new ImageView(getApplicationContext());
					ship.setImageResource(R.drawable.spaceship_pic);
					LayoutParams l = new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					ship.setVisibility(ImageView.VISIBLE);
					ship.bringToFront();
					livesLayout.addView(ship, l);

				}
			}
		});
	}

	@Override
	public void setStage(final int stages) {
		Log.d(TAG_STATUS_UPDATE, " Stages " + stages);
		runOnUiThread(new Runnable() {
			public void run() {
				stageView.setText("Stage: " + stages);

			}
		});
	}

	@Override
	public void gameOver() {
		connection.close();
		sm.unregisterListener(game);
		StaticIO.saveScore(game.getScore(), this.getApplicationContext());
		this.returnToMenu();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.gameOver();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!connection.isEnable()) {
			gameOver();
		}else {
			game.continueGame();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		game.pause();
	}

}
