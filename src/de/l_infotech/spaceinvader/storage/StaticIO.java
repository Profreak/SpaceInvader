package de.l_infotech.spaceinvader.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;

public class StaticIO {

	public static final String HIGHSCORE_FILENAME = "highscore";

	/**
	 * save a score as highscore if score is higher than the old highscore
	 * 
	 * @param score the new score
	 * @param context needed for saving data
	 */
	public static void saveScore(int score, Context context) {
		int old = loadScore(context);

		if (old < score) {
			String out = Integer.toString(score);
			FileOutputStream fOut;
			try {
				fOut = context.openFileOutput(HIGHSCORE_FILENAME,
						Activity.MODE_PRIVATE);
				fOut.write(out.getBytes());
				fOut.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * loads the current Highscore
	 * 
	 * @param context needed to load
	 * @return the current Highscore
	 */
	public static int loadScore(Context context) {
		int in = 0;
		try {
			FileInputStream fin = context.openFileInput(HIGHSCORE_FILENAME);
			
			int c;
			String tmp = "";
			while ((c = fin.read()) != -1) {
				tmp = tmp + Character.toString((char) c);
			}

			in = Integer.parseInt(tmp);
			fin.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return in;
	}

}
