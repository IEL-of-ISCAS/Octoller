/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 11, 20133:13:59 PM
 */
package cn.ac.iscas.iel.vr.octoller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.connection.BluetoothCommandService;
import cn.ac.iscas.iel.vr.octoller.constants.Messages;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;

/**
 * 
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.fragments
 * @Class PickingFragment
 * @Date May 11, 2013 3:13:59 PM
 * @author voidmain
 * @version
 * @since
 */
public class PickingFragment extends Fragment implements OnGestureListener,
		OnScaleGestureListener {

	protected MainActivity mMainActivity;
	protected BluetoothCommandService mBluetoothService;

	private Button mBtnRotating;
	private View mOperation;

	private GestureDetectorCompat mDetector;
	private ScaleGestureDetector mScaleDetector;

	private float mCurrentScale;
	private boolean mScaling;
	private boolean mScrolling;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mScaling = false;
		mScrolling = false;
		mCurrentScale = 1.0f;

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_picking, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mMainActivity = (MainActivity) this.getActivity();
		mBluetoothService = mMainActivity.getBluetoothService();
		setupViews(getView());

		mDetector = new GestureDetectorCompat(mMainActivity, this);
		mScaleDetector = new ScaleGestureDetector(mMainActivity, this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		mMainActivity.setIsLock(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		mMainActivity.setIsLock(false);
	}

	private void setupViews(View view) {
		mBtnRotating = (Button) view.findViewById(R.id.btn_model_rotate);
		mBtnRotating.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = MotionEventCompat.getActionMasked(event);

				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mMainActivity.setCurrentMsg(Messages.ROTATE);
					mMainActivity.setIsSendRotData(true);
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					mBluetoothService.write(("{\"phoneID\":"
							+ mMainActivity.getPhoneID() + ",\"msgType\":20}\n")
							.getBytes());
					mMainActivity.setIsSendRotData(false);
					break;
				}
				return true;
			}
		});

		mOperation = view.findViewById(R.id.v_operation);
		mOperation.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!mScaling) {
					mDetector.onTouchEvent(event);
				}
				mScaleDetector.onTouchEvent(event);

				int action = MotionEventCompat.getActionMasked(event);
				if (action == MotionEvent.ACTION_UP
						|| action == MotionEvent.ACTION_CANCEL) {
					if (mScrolling) {
						mBluetoothService.write(("{\"phoneID\":"
								+ mMainActivity.getPhoneID() + ",\"msgType\":"
								+ Messages.RELEASE + "}\n").getBytes());
						mScrolling = false;
					}
				}
				return true;
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.picking, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_unlock:
			FragmentTransactionHelper.transTo(getActivity(),
					new MasterFragment(), "masterFragment", false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		mCurrentScale *= detector.getScaleFactor();
		mBluetoothService.write(("{\"phoneID\":" + mMainActivity.getPhoneID()
				+ ",\"msgType\":" + Messages.PINCH + ",\"value\":"
				+ mCurrentScale + "}\n").getBytes());
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		mCurrentScale = 1.0f;
		mScaling = true;
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		mScaling = false;
	}

	@Override
	public boolean onDown(MotionEvent event) {
		float screenX = event.getX();
		float screenY = event.getY();
		float viewX = screenX - mOperation.getLeft();
		float viewY = screenY - mOperation.getTop();
		float ratioX = viewX / mOperation.getWidth();
		float ratioY = viewY / mOperation.getHeight();
		mBluetoothService.write(("{\"phoneID\":" + mMainActivity.getPhoneID()
				+ ",\"msgType\":" + Messages.PUSH + ",\"x\":" + ratioX
				+ ",\"y\":" + ratioY + "}\n").getBytes());
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		mScrolling = true;

		float screenX = e2.getX();
		float screenY = e2.getY();
		float viewX = screenX - mOperation.getLeft();
		float viewY = screenY - mOperation.getTop();
		float ratioX = viewX / mOperation.getWidth();
		float ratioY = viewY / mOperation.getHeight();
		ratioX = ratioX * 2 - 1;
		ratioY = ratioY * 2 - 1;
		ratioY *= -1;

		if (ratioX > 1)
			ratioX = 1;
		if (ratioX < -1)
			ratioX = -1;
		if (ratioY > 1)
			ratioY = 1;
		if (ratioY < -1)
			ratioY = -1;
		mBluetoothService.write(("{\"phoneID\":" + mMainActivity.getPhoneID()
				+ ",\"msgType\":" + Messages.DRAG + ",\"x\":" + ratioX
				+ ",\"y\":" + ratioY + "}\n").getBytes());
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		mBluetoothService.write(("{\"phoneID\":" + mMainActivity.getPhoneID()
				+ ",\"msgType\":" + Messages.RELEASE + "}\n").getBytes());
		return true;
	}

}
