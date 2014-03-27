package de.l_infotech.spaceinvader.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * This class handels a Display Connection via bluetooth
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public class BluetoothConnector implements DisplayConnection {

	// debugging
	private final String TAG = BluetoothConnector.class.getSimpleName();

	// needed vars
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private int REQUEST_ENABLE_BT = 10;
	private int port = 16;
	private OutputStream outStream = null;

	/**
	 * creates a new BluetoothConnector
	 */
	public BluetoothConnector() {
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public boolean isSupported() {
		if (adapter != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isEnable() {
		return adapter.isEnabled();
	}

	@Override
	public boolean startAdapter(Activity activity) {
		Intent enableBtIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		return true;
	}

	@Override
	public boolean connect(String address) {

		Log.d(TAG, "Connect to adress");
		try {
			// Get device object for the address.
			BluetoothDevice devices = adapter.getRemoteDevice(address);
			Method m = devices.getClass().getMethod("createRfcommSocket",
					new Class[] { int.class });
			socket = (BluetoothSocket) m.invoke(devices, port);
			Log.d(TAG, "Socket creation success.");
		} catch (Exception e) {
			Log.e(TAG, "Socket creation failed.", e);
			return false;
		}

		// stop discover
		Log.d(TAG, "Cancel Discovery");
		adapter.cancelDiscovery();

		Log.d(TAG, "Block Connect");
		try {
			// Blocking connect() call.
			socket.connect();
			Log.d(TAG, "Connected to " + address + ". Sending data...");
		} catch (IOException e) {
			Log.e(TAG, "Failed to open socket.", e);
		}

		Log.d(TAG, "Create Output Stream");
		// Create output stream to send data to the server.
		try {
			outStream = socket.getOutputStream();
			Log.d(TAG, "Create optupt stream.");
		} catch (IOException e) {
			Log.e(TAG, "Failed to create optupt stream.", e);
		}

		return true;
	}

	@Override
	public void send(byte[] matrixInMessage) {
		Log.d(TAG, "Send Matrix");
		synchronized (outStream) {
			synchronized (matrixInMessage) {
				try {
					outStream.write(matrixInMessage);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "faild to send Matrix.", e);
				}
			}
		}
	}

	@Override
	public void close() {
		Log.d(TAG, "close Bluetooth.");
		
		// close bluetooth
		try {
			socket.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "faild to close Bluetooth.", e);
		}
	}
}
