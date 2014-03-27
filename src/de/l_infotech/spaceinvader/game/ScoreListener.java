package de.l_infotech.spaceinvader.game;

/**
 * This interface manages the exchange between Threads and UI
 * 
 * @author Ludwig Biermann
 * @version 1.0
 *
 */
public interface ScoreListener {

	/**
	 * sets a new Score
	 * @param score current score
	 */
	public void setScore(int score);
	
	/**
	 * sets a new Player lives
	 * @param lives current lives of the player
	 */
	public void setPlayerLives(int lives);

	/**
	 * sets a new Stage
	 * @param stages current Stage
	 */
	public void setStage(int stages);
}
