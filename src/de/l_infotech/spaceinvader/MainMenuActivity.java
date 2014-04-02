package de.l_infotech.spaceinvader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.l_infotech.spaceinvader.connection.BluetoothConnector;
import de.l_infotech.spaceinvader.connection.DisplayConnection;
import de.l_infotech.spaceinvader.storage.StaticIO;

/**
 * This class holds the Main Menu of the game.
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public class MainMenuActivity extends Activity {

	private String TAG = MainMenuActivity.class.getSimpleName();
	/**
	 * Buttons of main menu
	 */
	private Button newgame;
	private Button help;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		newgame = (Button) this.findViewById(R.id.newgame);
		help = (Button) this.findViewById(R.id.help);
		TextView highscore = (TextView) this.findViewById(R.id.highscore);
		highscore.setText("Highscore: " + StaticIO.loadScore(getApplicationContext()));
		
		newgame.setOnClickListener(new NewGameListener());
		help.setOnClickListener(new HelpListener());
		
	
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "Check Connection");
		DisplayConnection connection = new BluetoothConnector();
		if (!connection.isSupported()) {
			Toast.makeText(getApplicationContext(),
					"buy a new Device .... with Bluetooth!", Toast.LENGTH_LONG)
					.show();
		}
		if (!connection.isEnable()) {
			if (!connection.startAdapter(this)) {
				Toast.makeText(getApplicationContext(), "unexpected error",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);

		return true;
	}

	/**
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	private class NewGameListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			Log.d(TAG, "starts a new Game");
			Intent nextScreen = new Intent(getApplicationContext(),
					GameActivity.class);
			startActivity(nextScreen);
		}

	}

	/**
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	private class HelpListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			Log.d(TAG, "Starts the Help Display");
			Intent nextScreen = new Intent(getApplicationContext(),
					HelpActivity.class);
			startActivity(nextScreen);
		}

	}

}
