/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 6, 20136:51:32 PM
 */
package cn.ac.iscas.iel.vr.octoller.utils;

import cn.ac.iscas.iel.csdtp.controller.Device;
import cn.ac.iscas.iel.csdtp.data.ControlFrame;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.vr.octoller.MainActivity;

/**
 * Wraps some helper functions for control messages
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.utils
 * @Class ControlMessageUtils
 * @Date May 6, 2013 6:51:32 PM
 * @author voidmain
 * @version
 * @since
 */
public class ControlMessageUtils {

	protected static MainActivity mActivity;

	public static void setActivty(MainActivity activity) {
		mActivity = activity;
	}

	public static void connect() {
		Device mainDevice = mActivity.getDevice();
		ControlFrame frame = new ControlFrame(mainDevice,
				Frame.MSG_TYPE_NEWCONNECT);
		mainDevice.pushToSendQueue(frame);
	}

	public static void disconnect() {
		Device mainDevice = mActivity.getDevice();
		ControlFrame frame = new ControlFrame(mainDevice,
				Frame.MSG_TYPE_DISCONNECT);
		mainDevice.pushToSendQueue(frame);
	}
	
	public static void requestControl() {
		Device mainDevice = mActivity.getDevice();
		ControlFrame frame = new ControlFrame(mainDevice,
				Frame.MSG_TYPE_REQUESTCONTROL);
		mainDevice.pushToSendQueue(frame);
	}
	
	public static void releaseControl() {
		Device mainDevice = mActivity.getDevice();
		ControlFrame frame = new ControlFrame(mainDevice,
				Frame.MSG_TYPE_GIVEUPCONTROL);
		mainDevice.pushToSendQueue(frame);
	}

}
