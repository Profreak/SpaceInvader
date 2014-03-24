package de.l_infotech.spaceinvader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This is the game them self
 * 
 * @author Ludwig Biermannn
 * @version 1.0
 *
 */
public class HelpActivity extends Activity{
	
	private Button back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		back = (Button) this.findViewById(R.id.back);
		back.setOnClickListener(new BackButtonListener());
		
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
	private class BackButtonListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			Intent nextScreen = new Intent(getApplicationContext(),
					MainMenuActivity.class);
			startActivity(nextScreen);
		}

	}
}
