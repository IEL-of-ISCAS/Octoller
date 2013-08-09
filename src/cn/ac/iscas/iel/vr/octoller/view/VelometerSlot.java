/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:TouchToPopup
 * Author: voidmain
 * Create Date: 2013-8-8下午4:51:10
 */
package cn.ac.iscas.iel.vr.octoller.view;

/**   
 * 
 * 
 * @Project TouchToPopup
 * @Package me.voidmain.sample.touchtopopup
 * @Class VelometerSlot
 * @Date 2013-8-8 下午4:51:10
 * @author voidmain
 * @version 
 * @since 
 */
public class VelometerSlot {
	protected int mSlotID;
	protected int mLowerBound;
	protected int mUpperBound;
	protected String mLevelName;
	protected int mLevelColor;
	
	public VelometerSlot(int slotID, int lowerBound, int upperBound, String levelName,
			int levelColor) {
		super();
		this.mSlotID = slotID;
		this.mLowerBound = lowerBound;
		this.mUpperBound = upperBound;
		this.mLevelName = levelName;
		this.mLevelColor = levelColor;
	}
	
	public int getSlotID() {
		return mSlotID;
	}
	
	public void setSlotID(int slotID) {
		this.mSlotID = slotID;
	}

	public int getLowerBound() {
		return mLowerBound;
	}

	public void setLowerBound(int lowerBound) {
		this.mLowerBound = lowerBound;
	}

	public int getUpperBound() {
		return mUpperBound;
	}

	public void setUpperBound(int upperBound) {
		this.mUpperBound = upperBound;
	}

	public String getLevelName() {
		return mLevelName;
	}

	public void setLevelName(String levelName) {
		this.mLevelName = levelName;
	}

	public int getLevelColor() {
		return mLevelColor;
	}

	public void setLevelColor(int levelColor) {
		this.mLevelColor = levelColor;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof VelometerSlot) {
			return ((VelometerSlot) o).getSlotID() == this.getSlotID();
		}
		return false;
	}
	
}
