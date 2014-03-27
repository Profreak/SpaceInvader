package de.l_infotech.spaceinvader.game.sound;

import de.l_infotech.spaceinvader.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * This class play´s three different sound´s
 * 
 * @author Ludwig Biermann
 * @version 1.0
 *
 */
public class Soundboard {
	
	public final static int ENEMYFIRE = 1; 
	public final static int EXPLOSION = 2; 
	public final static int PLAYERFIRE = 3; 
	
	private SoundPool soundpool;
	private int sound_1;
	private int sound_2;
	private int sound_3;
	private Context context;

	/**
	 * Creates a new Soundboard
	 * 
	 * @param context in this Context
	 */
	public Soundboard(Context context) {
		this.context = context;
		soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

		// Sound references
		sound_1 = soundpool.load(context, R.raw.enemyfire, 1);
		sound_2 = soundpool.load(context, R.raw.playerfire, 1);
		sound_3 = soundpool.load(context, R.raw.explosion, 1);
	}

	/**
	 * Play´s sounds by a ID
	 * @param id the of the sound
	 */
	public void playSound(int id) {

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;

		if(id == ENEMYFIRE) {
			soundpool.play(sound_1, volume, volume, 1, 0, 1f);
		}
		if(id == EXPLOSION) {
			soundpool.play(sound_3, volume, volume, 1, 0, 1f);
		}
		if(id == PLAYERFIRE) {
			soundpool.play(sound_2, volume, volume, 1, 0, 1f);
		}

	}

}
