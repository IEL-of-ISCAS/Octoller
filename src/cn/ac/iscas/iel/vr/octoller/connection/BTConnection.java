/**
 * VR.IEL @ ISCAS
 * Copyright reserved.
 */
package cn.ac.iscas.iel.vr.octoller.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;

/**
 * BTConnection 
 * 
 * @author VoidMain
 *
 */
public class BTConnection extends BaseConnection {
	public void createStreamFromSocket(BluetoothSocket socket) {
		try {
			mInStream = new DataInputStream(socket.getInputStream());
			mOutStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
