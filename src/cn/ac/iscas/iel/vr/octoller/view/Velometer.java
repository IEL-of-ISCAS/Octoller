/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:TouchToPopup
 * Author: voidmain
 * Create Date: 2013-8-7下午2:30:21
 */
package cn.ac.iscas.iel.vr.octoller.view;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cn.ac.iscas.iel.vr.octoller.Constants;

/**
 * Draws velometer
 * 
 * @Project TouchToPopup
 * @Package me.voidmain.sample.touchtopopup
 * @Class VelometerDrawer
 * @Date 2013-8-7 下午2:30:21
 * @author voidmain
 * @version
 * @since
 */
public class Velometer extends View {
	protected static final double PI_TO_DEGREE = Math.PI / 180;
	protected static final double DEGREE_TO_PI = 180 / Math.PI;
	protected static final float BORDER_PERCENT = 0.03f;

	public static final int INVALID_LEVEL = -10000;
	public static final int ANGLE_NOT_MOVED = -1;

	protected MotionEvent mStartEvent = null;

	protected int mMeasuredWidth = 0;
	protected int mMeasuredHeight = 0;

	protected float mStartX = 0;
	protected float mStartY = 0;
	protected float mCurX = 0;
	protected float mCurY = 0;
	protected float mOutRadius = 0;
	protected float mInnerRadius = 0;
	protected int mLastLevel = INVALID_LEVEL;

	protected Paint mGradientPaint = null;
	protected Paint mInnerPaint = null;
	protected Paint mLinePaint = null;
	protected Paint mTextPaint = null;
	protected Paint mBorderPaint = null;
	protected Paint mTransparentPaint = null;

	protected List<VelometerSlot> mSlotList;
	protected VelometerSlot mCurrentSlot = null;

	protected IVelometerLevelListener mVeloLevelListener;

	public Velometer(Context context) {
		super(context);

		this.initialize();
	}

	public Velometer(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.initialize();
	}

	public Velometer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.initialize();
	}

	protected void initialize() {
		mMeasuredWidth = Constants.OUTER_SIZE;
		mMeasuredHeight = Constants.OUTER_SIZE;

		mOutRadius = Math.min(mMeasuredWidth, mMeasuredHeight) / 2;
		mInnerRadius = Constants.INNER_SIZE / 2;

		mGradientPaint = new Paint();
		mInnerPaint = new Paint();
		mInnerPaint.setColor(Color.WHITE);
		mLinePaint = new Paint();
		mLinePaint.setStrokeWidth(20);
		mLinePaint.setColor(Color.WHITE);
		mTextPaint = new Paint();
		mTextPaint.setTextSize(40);
		mTextPaint.setColor(Color.WHITE);

		mBorderPaint = new Paint();
		mBorderPaint.setStrokeWidth(mOutRadius - mInnerRadius);
		mBorderPaint.setColor(Color.BLACK);
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setStrokeCap(Paint.Cap.BUTT);
		mBorderPaint.setAntiAlias(true);

		mTransparentPaint = new Paint();
		mTransparentPaint.setStrokeWidth((mOutRadius - mInnerRadius)
				* (1 - 2 * BORDER_PERCENT));
		mTransparentPaint.setColor(Color.WHITE);
		mTransparentPaint.setStyle(Paint.Style.STROKE);
		mTransparentPaint.setStrokeCap(Paint.Cap.BUTT);
		mTransparentPaint.setAntiAlias(true);
	}

	public void setVelometerLevelListener(IVelometerLevelListener levelListener) {
		mVeloLevelListener = levelListener;
	}

	public IVelometerLevelListener getVelometerLevelListener() {
		return mVeloLevelListener;
	}

	public void setVelometerSlots(List<VelometerSlot> slots) {
		mSlotList = slots;
	}

	public List<VelometerSlot> getVelometerSlots() {
		return mSlotList;
	}

	public void onShow(MotionEvent event) {
		mStartX = event.getRawX();
		mStartY = event.getRawY();

		updateCurPosition(event);

		invalidate();
	}

	public void onUpdate(MotionEvent event) {
		updateCurPosition(event);

		invalidate();
	}

	public void onDisappear() {
		if (mVeloLevelListener != null) {
			// TODO calc level
			mVeloLevelListener.onLevelChanged(INVALID_LEVEL);
		}
	}

	protected int calcAngle(MotionEvent event) {
		float diffX = event.getRawX() - mStartX;
		float diffY = event.getRawY() - mStartY;

		if (Math.sqrt(diffX * diffX + diffY * diffY) < mInnerRadius) {
			return ANGLE_NOT_MOVED;
		}

		double angle = Math.atan2(diffY, diffX);
		int result = (int) (angle / PI_TO_DEGREE);
		if (result < 0) {
			result += 360;
		}
		return result;
	}

	protected VelometerSlot lookupLevel(int angle) {
		VelometerSlot result = null;

		if (mSlotList == null)
			return result;

		for (VelometerSlot slot : mSlotList) {
			if (angle >= slot.getLowerBound() && angle < slot.getUpperBound()) {
				result = slot;
				break;
			}
		}

		return result;
	}

	protected void updateCurPosition(MotionEvent event) {
		mCurX = mMeasuredWidth / 2 - mStartX + event.getRawX();
		mCurY = mMeasuredHeight / 2 - mStartY + event.getRawY();

		mGradientPaint
				.setShader(new RadialGradient(mCurX, mCurY, mOutRadius * 2,
						Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP));

		int angle = calcAngle(event);
		int level = INVALID_LEVEL;
		VelometerSlot slot = lookupLevel(angle);
		if (slot != null) {
			level = slot.getSlotID();
			mCurrentSlot = slot;
			mInnerPaint.setColor(slot.getLevelColor());
		} else {
			level = INVALID_LEVEL;
			mCurrentSlot = null;
			mInnerPaint.setColor(Color.WHITE);
		}

		if (level != mLastLevel) {
			if (mVeloLevelListener != null) {
				// TODO calc level
				mVeloLevelListener.onLevelChanged(level);
			}

			mLastLevel = level;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		int centerX = mMeasuredWidth / 2;
		int centerY = mMeasuredHeight / 2;

		// Draw a circle, fill with gradient shader
		// canvas.drawCircle(centerX, centerY, mOutRadius, mGradientPaint);

		// Draw transparent arcs
		RectF circleRect = new RectF();
		float width = (mOutRadius + mInnerRadius) / 2;
		circleRect.set(centerX - width, centerY - width, centerX + width,
				centerY + width);
		for (VelometerSlot slot : mSlotList) {
			float angleDiff = slot.getUpperBound() - slot.getLowerBound();
			float startAngle = slot.getLowerBound()
					+ (angleDiff * BORDER_PERCENT);
			float sweepAngle = angleDiff * (1 - 2 * BORDER_PERCENT);

			canvas.drawArc(circleRect, startAngle, sweepAngle, false,
					mBorderPaint);

			if (slot.equals(mCurrentSlot)) {
				mTransparentPaint.setColor(Color.LTGRAY);
			} else {
				mTransparentPaint.setColor(Color.WHITE);
			}
			canvas.drawArc(circleRect, startAngle + angleDiff * BORDER_PERCENT,
					sweepAngle - 2 * angleDiff * BORDER_PERCENT, false,
					mTransparentPaint);
		}

		// Draw a circle to cover the line start points
		canvas.drawCircle(centerX, centerY,
				mInnerRadius * (1 - BORDER_PERCENT), mInnerPaint);

		// Draw a text
		Rect textBound = new Rect();
		String levelText = "";
		if (mCurrentSlot != null)
			levelText = mCurrentSlot.getLevelName();
		mTextPaint.getTextBounds(levelText, 0, levelText.length(), textBound);
		canvas.drawText(levelText, centerX - textBound.width() / 2, centerY
				- textBound.height() / 2, mTextPaint);
	}

}
