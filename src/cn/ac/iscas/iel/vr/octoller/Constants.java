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
	public static final String SERVER_IP = "10.0.0.96";
	public static final int SERVER_PORT = 6666;

	public static final UUID BT_UUID = UUID
			.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

	// Intent request codes
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothCommandService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Channel output callback
	public static final int MSG_CONNECT_ERROR = 0;
	public static final int MSG_REQUEST_ERROR = 1;
}
