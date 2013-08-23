package cn.ac.iscas.iel.vr.octoller.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class OverlayImageView extends ImageView {
	
	protected float mCenterX = -1f;
	protected float mCenterY = -1f;
	protected static final float RECT_WIDTH = 200f;
	protected static final float RECT_HEIGHT = 100f;
	protected RectF mOverlayRect = null;
	
	protected Paint mOverlayPaint = null;
	
	protected IOverlayTouchListener mListener = null;
	
	public OverlayImageView(Context context) {
		super(context);
		initialize();
	}
	
	public OverlayImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public OverlayImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	
	public void setOverlayTouchListener(IOverlayTouchListener listener) {
		mListener = listener;
	}
	
	public void initialize() {
		mOverlayPaint = new Paint();
		mOverlayPaint.setColor(Color.RED);
		mOverlayPaint.setStrokeWidth(10.0f);
		mOverlayPaint.setStyle(Paint.Style.STROKE);
		
		mOverlayRect = new RectF();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(mCenterX == -1) {
			mCenterX = (this.getRight() - this.getLeft()) / 2.0f;
		}
		
		if(mCenterY == -1) {
			mCenterY = (this.getBottom() - this.getTop()) / 2.0f;
		}
		
		mOverlayRect.set(mCenterX - RECT_WIDTH / 2, mCenterY - RECT_HEIGHT / 2, mCenterX + RECT_WIDTH / 2, mCenterY + RECT_HEIGHT / 2);
		canvas.drawRect(mOverlayRect, mOverlayPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mCenterX = event.getX();
		mCenterY = event.getY();
		
		float ratioX = (mCenterX - this.getLeft()) / (this.getRight() - this.getLeft());
		float ratioY = (mCenterY - this.getTop()) / (this.getBottom() - this.getTop());
		
		if(mListener != null) {
			mListener.onOverlayRegionChanged(ratioX, ratioY);
		}
		
		invalidate();
		
		super.onTouchEvent(event);
		return true;
	}

}
