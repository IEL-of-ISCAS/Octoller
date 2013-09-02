/**
 * VR.IEL @ ISCAS
 * Copyright reserved.
 */
package cn.ac.iscas.iel.vr.octoller;

import java.util.UUID;

/**
 * Constants
 * 
 * @author VoidMain
 * 
 */
public class Constants {
	// Server config
	// TODO change to auto-detection
	public static final String SERVER_IP = "192.168.0.168";
	public static final int SERVER_PORT = 6666;

	public static final UUID BT_UUID = UUID
			.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;

	// Key names received from the BluetoothCommandService Handler
	public static final String DEVICE_NAME = "device_name";

	// Channel output callback
	public static final int MSG_CONNECT_ERROR = 0;
	public static final int MSG_REQUEST_ERROR = 1;
	
	// Velometer view config
	public static final int OUTER_SIZE = 500;
	public static final int INNER_SIZE = 200;
}
