/**
 * VR.IEL @ ISCAS
 * Copyright reserved.
 */
package cn.ac.iscas.iel.vr.octoller.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * WifiConnection 
 * 
 * @author VoidMain
 *
 */
public class WifiConnection extends BaseConnection {
	public void createStreamFromSocket(Socket socket) {
		try {
			mInStream = new DataInputStream(socket.getInputStream());
			mOutStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
