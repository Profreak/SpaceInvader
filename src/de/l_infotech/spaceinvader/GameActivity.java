package de.l_infotech.spaceinvader;

import de.l_infotech.spaceinvader.bluetooth.BluetoothConnector;
import de.l_infotech.spaceinvader.game.ScoreListener;
import de.l_infotech.spaceinvader.game.SpaceEngine;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the game themself
 * 
 * @author Ludwig Biermannn
 * @version 1.0
 * 
 */
public class GameActivity extends Activity implements ScoreListener {

	private DisplayConnection connection;
	private SpaceEngine game;
	private TextView scoreView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		connection = new BluetoothConnector();
		scoreView = (TextView) this.findViewById(R.id.score);

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

		game = new SpaceEngine(connection);

		SensorManager sm = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		Sensor s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(game, s, SensorManager.SENSOR_DELAY_GAME);
		this.findViewById(R.id.fireButton).setOnTouchListener(game);

		game.addScoreListener(this);

		game.start();
	}

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
		Log.d("test", " test " + (scoreView == null));
		runOnUiThread(new Runnable() {
			public void run() {
				
				scoreView.setText("Score: " + score);
			}
		});
	}
	
}
