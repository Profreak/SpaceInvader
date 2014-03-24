package de.l_infotech.spaceinvader;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
	private Button exit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		newgame = (Button) this.findViewById(R.id.newgame);
		help = (Button) this.findViewById(R.id.help);
		exit = (Button) this.findViewById(R.id.exit);

		newgame.setOnClickListener(new NewGameListener());
		help.setOnClickListener(new HelpListener());
		exit.setOnClickListener(new ExitListener());

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

	/**
	 * 
	 * @author Ludwig Biermann
	 * @version 1.0
	 * 
	 */
	private class ExitListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			Log.d(TAG, "bye bye");
			Dialog exit = createExitMessage();
			exit.show();
		}

	}

	/**
	 * create a new Exit Message
	 * 
	 * @return the new Dialog
	 */
	public Dialog createExitMessage() {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.quit_question)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								System.exit(0);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// do nothing
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();

	}

}
