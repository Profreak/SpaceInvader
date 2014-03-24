package de.l_infotech.spaceinvader;

import de.l_infotech.spaceinvader.bluetooth.BluetoothConnector;
import de.l_infotech.spaceinvader.game.SpaceEngine;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

/**
 * This is the game themself
 * 
 * @author Ludwig Biermannn
 * @version 1.0
 *
 */
public class GameActivity extends Activity{

	private WirelessConnection connection;
	private SpaceEngine game;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		connection = new BluetoothConnector();
		

		if (!connection.isSupported()) {
			Toast.makeText(getApplicationContext(),
					"buy a new Device .... with Bluetooth!",
					Toast.LENGTH_LONG).show();
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
}
