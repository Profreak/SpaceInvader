package de.l_infotech.spaceinvader.connection;

import android.app.Activity;

/**
 * This interface handels the connection between device and Display
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public interface DisplayConnection {

	/**
	 * Checks if the Connection is possible
	 * 
	 * @return {true} if device does support the Connection
	 */
	public boolean isSupported();

	/**
	 * Try whether the Connection is enable
	 * 
	 * @return {true} if Connection is enable
	 */
	public boolean isEnable();

	/**
	 * Starts the Adpater
	 * 
	 * @param activity
	 *            the current Activity
	 * @return true if success
	 */
	public boolean startAdapter(Activity activity);

	/**
	 * connects to a adress
	 * 
	 * @param address
	 *            the mac or ip of the device
	 * @return true if success
	 */
	public boolean connect(String address);

	/**
	 * sends the Matrix to the Display
	 * 
	 * @param matrixInMessage
	 *            to show
	 */
	public void send(byte[] matrixInMessage);
}
