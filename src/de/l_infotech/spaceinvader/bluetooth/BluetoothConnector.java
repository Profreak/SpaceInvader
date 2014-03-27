package de.l_infotech.spaceinvader.bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import de.l_infotech.spaceinvader.DisplayConnection;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class BluetoothConnector implements DisplayConnection {

	private final String TAG = BluetoothConnector.class.getSimpleName();

	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private int REQUEST_ENABLE_BT = 10;
	private int port = 16;
	private OutputStream outStream = null;
	private String address;

	private int maxRes = 24;

	public BluetoothConnector() {
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * Checks if Bluetooth is possible
	 * 
	 * @return true} if device does support Bluetooth
	 */
	public boolean isSupported() {
		if (adapter != null) {
			return true;
		}
		return false;
	}

	/**
	 * Try whether the Adapter is enable
	 * 
	 * @return true} if Adapter is enable
	 */
	public boolean isEnable() {
		return adapter.isEnabled();
	}

	/**
	 * Starts the Bluetooth Adpater
	 * 
	 * @param activity
	 *            the current Activity
	 * @return true if success
	 */
	public boolean startAdapter(Activity activity) {
		Intent enableBtIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		return true;
	}

	

	/**
	 * connects to a adress
	 * 
	 * @param address
	 *            the mac of the bluethooth device
	 * @return true if success
	 */
	public boolean connect(String address) {

		this.address = address;
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

		try {
			// Blocking connect() call.
			socket.connect();
			Log.d(TAG, "Connected to " + address + ". Sending data...");
		} catch (IOException e) {
			Log.e(TAG, "Failed to open socket.", e);
		}

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
		synchronized(outStream) {
		synchronized(matrixInMessage) {
		try {
			outStream.write(matrixInMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		}
	}
}
