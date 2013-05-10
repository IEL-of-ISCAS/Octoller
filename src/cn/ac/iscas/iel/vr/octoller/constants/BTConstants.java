/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 10, 20138:57:52 PM
 */
package cn.ac.iscas.iel.vr.octoller.constants;

/**
 * Constants for Bluetooth
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.constants
 * @Class BTConstants
 * @Date May 10, 2013 8:57:52 PM
 * @author voidmain
 * @version
 * @since
 */
public class BTConstants {

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names
	public static final String KEY_DEVICE_NAME = "device_name";
	public static final String KEY_TOAST = "toast";

}
