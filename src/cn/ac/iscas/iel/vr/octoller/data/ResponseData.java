/*
 * Copyright (C) 2013 IEL of ISCAS
 * Project:CSDTP
 * Author: voidmain
 * Create Date: May 6, 20132:39:38 PM
 */
package cn.ac.iscas.iel.vr.octoller.data;

/**
 * Data returned from
 * 
 * @Project CSDTP
 * @Package cn.ac.iscas.iel.csdtp.data
 * @Class ResponseData
 * @Date May 6, 2013 2:39:38 PM
 * @author voidmain
 */
public class ResponseData {

	protected String mPhoneID;
	protected int mMsgType;
	protected int mStatus;
	protected String mErrorMsg;

	public ResponseData() {
		mErrorMsg = "";
	}

	public String getPhoneID() {
		return mPhoneID;
	}

	public void setPhoneID(String phoneID) {
		this.mPhoneID = phoneID;
	}

	public int getMsgType() {
		return mMsgType;
	}

	public void setMsgType(int msgType) {
		this.mMsgType = msgType;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		this.mStatus = status;
	}

	public String getError() {
		return mErrorMsg;
	}

	public void setError(String msg) {
		this.mErrorMsg = msg;
	}
	
	public String toString() {
        return String.format("phoneID:%s,msgType:%d,status:%d,errorMsg:%s", 
        		mPhoneID, mMsgType,mStatus,mErrorMsg);
    }

}
