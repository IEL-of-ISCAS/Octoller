package cn.ac.iscas.iel.vr.octoller.view;

import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * Translate-Rotation-Zoom Gesture Detector
 * 
 * 
 * @Project TouchPointVisualizer
 * @Package me.voidmain.ui.controls.touchpointvisualizer
 * @Class TRZGestureDetector
 * @Date 2013-8-13 上午9:26:11
 * @author voidmain
 * @version
 * @since
 */

public class TRZGestureDetector {

	public interface MultiTouchEventListener {
		public static final int TYPE_UNKNOWN = 0;
		public static final int TYPE_ROTATE = 1;
		public static final int TYPE_PAN = 2;
		public static final int TYPE_ZOOM = 3;
		public static final int TYPE_RESET = 4;

		public static final int PHASE_PUSH = 1;
		public static final int PHASE_RELEASE = 2;
		public static final int PHASE_DRAG = 8;
		public static final int PHASE_KEYDOWN = 32;

		public void onEventChanged(int phase, int type, float x, float y);
	}

	private static final float RESET_THRESHOLD = 20.0f;

	private static final int NONE = 0;
	private static final int ROTATE = 1;
	private static final int PAN = 2;
	private static final int ZOOM = 3;
	private static final int RESET = 4;

	private int mMode = NONE;

	private PointF mStart = new PointF();
	private PointF mMid = new PointF();
	private PointF mLast = new PointF();

	private int mViewWidth = 0;
	private int mViewHeight = 0;

	private MultiTouchEventListener mMatrixChangedListener = null;

	public void setMultiTouchEventListener(MultiTouchEventListener listener) {
		mMatrixChangedListener = listener;
	}

	public TRZGestureDetector() {
		mViewWidth = 0;
		mViewHeight = 0;
	}

	public void setViewWidth(int width) {
		mViewWidth = width;
	}

	public void setViewHeight(int height) {
		mViewHeight = height;
	}

	public void onTouchEvent(MotionEvent event) {
		if (event == null || mMatrixChangedListener == null)
			return;

		float normalizedX = normalizeX(event.getX());
		float normalizedY = normalizeY(event.getY());
		int pointerCount = event.getPointerCount();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mLast.set(event.getX(), event.getY());
			mMode = ROTATE;
			mMatrixChangedListener.onEventChanged(
					MultiTouchEventListener.PHASE_PUSH,
					MultiTouchEventListener.TYPE_UNKNOWN, normalizedX,
					normalizedY);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (pointerCount == 2) {
				mMode = PAN; // guess it's pan
			} else if (pointerCount == 3) {
				mMode = ZOOM;
			} else if (pointerCount == 4) {
				mMode = RESET;
				PointF midPoint = new PointF();
				midPoint(midPoint, event);
				mStart.set(midPoint.x, midPoint.y);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mMatrixChangedListener.onEventChanged(
					MultiTouchEventListener.PHASE_RELEASE,
					MultiTouchEventListener.TYPE_UNKNOWN, normalizedX,
					normalizedY);
		case MotionEvent.ACTION_POINTER_UP:
			if (mMode != RESET) {
				if (pointerCount == 2) {
					mMode = PAN;
				} else if (pointerCount == 1) {
					mMode = ROTATE;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mMode == ROTATE) {
				mMatrixChangedListener.onEventChanged(
						MultiTouchEventListener.PHASE_DRAG,
						MultiTouchEventListener.TYPE_ROTATE, normalizedX,
						normalizedY);
			} else if (mMode == PAN) {
				midPoint(mMid, event);
				normalizedX = normalizeX(mMid.x);
				normalizedY = normalizeY(mMid.y);
				mMatrixChangedListener.onEventChanged(
						MultiTouchEventListener.PHASE_DRAG,
						MultiTouchEventListener.TYPE_PAN, normalizedX,
						normalizedY);
			} else if (mMode == ZOOM) {
				midPoint(mMid, event);
				normalizedX = normalizeX(mMid.x);
				normalizedY = normalizeY(mMid.y);
				mMatrixChangedListener.onEventChanged(
						MultiTouchEventListener.PHASE_DRAG,
						MultiTouchEventListener.TYPE_ZOOM, normalizedX,
						normalizedY);
			} else if (mMode == RESET) {
				midPoint(mMid, event);

				if (Math.abs(mMid.y - mStart.y) > RESET_THRESHOLD) {
					mMatrixChangedListener.onEventChanged(
							MultiTouchEventListener.PHASE_KEYDOWN,
							MultiTouchEventListener.TYPE_RESET, normalizedX,
							normalizedY);
				}
			}
			break;
		}
	}

	private void midPoint(PointF point, MotionEvent event) {
		float sumX = 0.0f;
		float sumY = 0.0f;
		int pointerCount = event.getPointerCount();
		for (int idx = 0; idx < pointerCount; idx++) {
			sumX += event.getX(idx);
			sumY += event.getY(idx);
		}
		point.set(sumX / pointerCount, sumY / pointerCount);
	}

	private float normalizeX(float rawX) {
		float normX = rawX / mViewWidth;
		if (normX < 0)
			normX = 0;
		if (normX > 1)
			normX = 1;
		return normX;
	}

	private float normalizeY(float rawY) {
		float normY = rawY / mViewHeight;
		if (normY < 0)
			normY = 0;
		if (normY > 1)
			normY = 1;
		return normY;
	}

}
