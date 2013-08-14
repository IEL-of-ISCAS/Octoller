/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:TouchPointVisualizer
 * Author: voidmain
 * Create Date: 2013-8-9下午3:24:36
 */
package cn.ac.iscas.iel.vr.octoller.view;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * 
 * @Project TouchPointVisualizer
 * @Package me.voidmain.ui.controls.touchpointvisualizer
 * @Class TouchPointVisualizer
 * @Date 2013-8-9
 * @author voidmain
 * @version
 * @since
 */
public class TouchPointVisualizer extends View {
	protected static final int SLEEP_TIME = 1000 / 60;
	protected ConcurrentLinkedQueue<TouchPoint> mTouchPointQueue = null;
	protected Thread mDrawingThread = null;
	protected Paint mPaint = null;
	protected Date now = null;
	protected MotionEvent mCurrentEvent = null;
	protected TRZGestureDetector mDetector = null;
	protected static final int[] COLOR_CANDIDATES = { Color.BLACK, Color.BLUE,
			Color.CYAN, Color.DKGRAY, Color.GRAY, Color.GREEN, Color.LTGRAY,
			Color.MAGENTA, Color.RED, Color.YELLOW };

	public TouchPointVisualizer(Context context) {
		super(context);
		initialize();
	}

	public TouchPointVisualizer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public TouchPointVisualizer(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	protected void initialize() {
		mTouchPointQueue = new ConcurrentLinkedQueue<TouchPointVisualizer.TouchPoint>();
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		now = new Date();

		mDrawingThread = new Thread() {

			@Override
			public void run() {
				while (!interrupted()) {
					if (mCurrentEvent != null) {
						TouchPoint point = new TouchPoint(mCurrentEvent);
						if(point.getMotionEvent() != null) {
							mTouchPointQueue.add(point);
						}
					}
					postInvalidate();
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}

		};
		mDrawingThread.start();
	}

	public void setTRZDetector(TRZGestureDetector detector) {
		mDetector = detector;
	}
	
	public TRZGestureDetector getTRZDetector() {
		return mDetector;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = MotionEventCompat.getActionMasked(event);

		if (action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_POINTER_DOWN
				|| action == MotionEvent.ACTION_MOVE) {
			mCurrentEvent = eventRelativeToView(event);
		} else {
			mCurrentEvent = null;
		}

		super.dispatchTouchEvent(event);
		
		if (mDetector != null) {
			mDetector.onTouchEvent(event);
		}
		return true;
	}

	protected MotionEvent eventRelativeToView(MotionEvent originEvent) {
		MotionEvent event = MotionEvent.obtain(originEvent);
		event.setLocation(event.getX() - getLeft(), event.getY() - getTop());
		return event;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		now.setTime(System.currentTimeMillis());
		for (TouchPoint point : mTouchPointQueue) {
			double percent = 1 - 1.0 * point.getAge(now)
					/ TouchPoint.LIFE_LENGTH;
			if (percent < 0) {
				percent = 0;
			}

			MotionEvent motionEvent = point.getMotionEvent();
			for (int idx = 0; idx < motionEvent.getPointerCount(); idx++) {
				int pointerId = motionEvent.getPointerId(idx);
				mPaint.setColor(COLOR_CANDIDATES[pointerId]);
				mPaint.setAlpha((int) (255 * percent));
				canvas.drawCircle(motionEvent.getX(idx), motionEvent.getY(idx),
						30, mPaint);
			}
		}

		for (TouchPoint tp : mTouchPointQueue) {
			if (tp.isDead(now)) {
				mTouchPointQueue.remove(tp);
			}
		}
	}

	protected interface AgableItem {
		public long getAge(Date now);

		public boolean isDead(Date now);

		public void suicide();
	}

	protected class TouchPoint implements AgableItem, Comparable<TouchPoint> {
		public static final long LIFE_LENGTH = 300;

		protected MotionEvent mEvent;
		protected Date mBornDate;

		public TouchPoint(MotionEvent event) {
			if(event == null) return;
			mEvent = MotionEvent.obtain(event);
			mBornDate = new Date();
		}

		public MotionEvent getMotionEvent() {
			return mEvent;
		}

		@Override
		public long getAge(Date now) {
			return now.getTime() - getBornTime();
		}

		@Override
		public boolean isDead(Date now) {
			return getAge(now) >= LIFE_LENGTH;
		}

		@Override
		public void suicide() {
			mEvent.recycle();
		}

		public long getBornTime() {
			return mBornDate.getTime();
		}

		@Override
		public int compareTo(TouchPoint another) {
			return (int) (getBornTime() - another.getBornTime());
		}

	}

}
