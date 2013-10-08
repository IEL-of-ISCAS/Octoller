/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 6, 20138:52:03 AM
 */
package cn.ac.iscas.iel.vr.octoller.fragments;

import java.util.Arrays;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import cn.ac.iscas.iel.csdtp.controller.RotationSensor;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.csdtp.data.SensorData;
import cn.ac.iscas.iel.csdtp.exception.MultipleSampleThreadException;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.utils.ControlMessageUtils;
import cn.ac.iscas.iel.vr.octoller.utils.SPenUtils;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenTouchListener;

/**
 * Servant's fragment, functionality to be determined
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.fragments
 * @Class ServantFragment
 * @Date May 6, 2013 8:52:03 AM
 * @author voidmain
 * @version
 * @since
 */
public class SlaveryFragment extends Fragment {
	protected MainActivity mMainActivity;
	protected View mRoot;
	protected View mBtnPhonePick;
	protected View mTvText;

	// Pen stuff
	protected boolean mSupportSPen = false;
	protected SPenEventLibrary mSPenEventLibrary;
	protected boolean mPicked = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		mPicked = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_slavery, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMainActivity = (MainActivity) this.getActivity();
		mSupportSPen = SPenUtils.isSPenSupported(mMainActivity);

		setupView(getView());
	}

	private void setupView(View view) {
		mRoot = view.findViewById(R.id.root);

		mTvText = view.findViewById(R.id.tv_welcome);
		mBtnPhonePick = view.findViewById(R.id.btn_lock_view);
		mBtnPhonePick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ControlMessageUtils.doPick();
			}
		});

		if (mSupportSPen) {
			mTvText.setVisibility(View.GONE);
			mBtnPhonePick.setVisibility(View.GONE);

			mSPenEventLibrary = new SPenEventLibrary();
			mSPenEventLibrary.setSPenTouchListener(mRoot,
					new SPenTouchListener() {

						@Override
						public boolean onTouchPenEraser(View arg0,
								MotionEvent arg1) {
							return false;
						}

						@Override
						public boolean onTouchPen(View view, MotionEvent event) {
							if (mPicked) {
								return false;
							}
							if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
								mMainActivity.getDevice().setCurrentMsgType(
										Frame.MSG_TYPE_PHONEMOVE);

								float x = event.getX()
										/ view.getMeasuredWidth();
								float y = event.getY()
										/ view.getMeasuredHeight();
								y = 1 - y;

								float[] values = { x, y, 2, 0 };
								SensorData<float[]> data = new SensorData<float[]>(
										Arrays.copyOf(values, values.length));
								mMainActivity.getRotationSensor()
										.updateSnapshot(data);
							}

							return true;
						}

						@Override
						public boolean onTouchFinger(View view,
								MotionEvent event) {
							return true;
						}

						@Override
						public void onTouchButtonUp(View arg0, MotionEvent arg1) {
						}

						@Override
						public void onTouchButtonDown(View view,
								MotionEvent event) {
							mMainActivity.getDevice().setCurrentMsgType(
									Frame.MSG_TYPE_PICK);

							float x = event.getX() / view.getMeasuredWidth();
							float y = event.getY() / view.getMeasuredHeight();
							y = 1 - y;

							float[] values = { x, y, 2, 0 };
							SensorData<float[]> data = new SensorData<float[]>(
									Arrays.copyOf(values, values.length));
							mMainActivity.getRotationSensor().updateSnapshot(
									data);

							mPicked = true;
						}
					});
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			if (!mSupportSPen) {
				mMainActivity.resumeSensor();
			}

			mMainActivity.getDevice().setCurrentMsgType(
					Frame.MSG_TYPE_PHONEMOVE);
			mMainActivity.getDevice().startSampling(RotationSensor.getMyName());
		} catch (MultipleSampleThreadException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		mMainActivity.getDevice().stopSampling();

		if (!mSupportSPen) {
			mMainActivity.pauseSensor();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.slavery, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_disconnect:
			ControlMessageUtils.disconnect();
			return true;
		case R.id.action_request_master:
			ControlMessageUtils.requestControl();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
