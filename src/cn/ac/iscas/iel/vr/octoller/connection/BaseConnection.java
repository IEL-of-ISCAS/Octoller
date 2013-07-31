package cn.ac.iscas.iel.vr.octoller.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class BaseConnection {
	protected DataInputStream mInStream;
	protected DataOutputStream mOutStream;
	
	public DataInputStream getInStream() {
		return mInStream;
	}
	
	public void setInStream(DataInputStream inStream) {
		this.mInStream = inStream;
	}
	
	public DataOutputStream getOutStream() {
		return mOutStream;
	}
	
	public void setOutStream(DataOutputStream outStream) {
		this.mOutStream = outStream;
	}
}
