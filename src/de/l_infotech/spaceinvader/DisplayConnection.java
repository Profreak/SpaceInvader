package de.l_infotech.spaceinvader;

import android.app.Activity;
public interface DisplayConnection {

	
	public boolean isSupported();
	
	public boolean isEnable();
	
	public boolean startAdapter(Activity activity);
	
	public boolean connect(String address);

	public void send(byte[] matrixInMessage);
}
