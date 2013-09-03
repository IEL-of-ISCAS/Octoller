/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 6, 20138:51:16 AM
 */
package cn.ac.iscas.iel.vr.octoller.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import cn.ac.iscas.iel.csdtp.controller.RotationSensor;
import cn.ac.iscas.iel.csdtp.controller.VelometerSensor;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.csdtp.exception.MultipleSampleThreadException;
import cn.ac.iscas.iel.vr.octoller.Constants;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.utils.ControlMessageUtils;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;
import cn.ac.iscas.iel.vr.octoller.view.Velometer;
import cn.ac.iscas.iel.vr.octoller.view.VelometerSlot;

/**
 * The master controller's fragment
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.fragments
 * @Class MasterFragment
 * @Date May 6, 2013 8:51:16 AM
 * @author voidmain
 * @version
 * @since
 */
public class MasterFragment extends Fragment {

	protected MainActivity mMainActivity;

	protected Button mBtnManiFlight;
	protected Button mBtnDriver;
	protected Button mBtnMultiTouch;
	protected Button mBtnMaps;

	protected ViewGroup mLayoutRoot;
	protected PopupWindow mPopupWindow;
	protected View mPopupView;
	protected Velometer mVelometer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_master, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mMainActivity = (MainActivity) this.getActivity();
		setupViews(this.getView());

		mLayoutRoot = (ViewGroup) this.getView().findViewById(R.id.layout_root);

		LayoutInflater inflater = (LayoutInflater) mMainActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopupView = inflater.inflate(R.layout.popup, null);
		mVelometer = (Velometer) mPopupView.findViewById(R.id.velometer);
		mVelometer.setVelometerLevelListener(mMainActivity.getVeloCallback());

		VelometerSlot level1 = new VelometerSlot(-1, 0, 60, "减速1", Color.RED);
		VelometerSlot level2 = new VelometerSlot(-2, 60, 120, "减速2",
				Color.MAGENTA);
		VelometerSlot level3 = new VelometerSlot(-3, 120, 180, "减速3", Color.BLUE);
		VelometerSlot level4 = new VelometerSlot(3, 180, 240, "加速3",
				Color.CYAN);
		VelometerSlot level5 = new VelometerSlot(2, 240, 300, "加速2",
				Color.DKGRAY);
		VelometerSlot level6 = new VelometerSlot(1, 300, 360, "加速1",
				Color.GREEN);
		List<VelometerSlot> slots = new ArrayList<VelometerSlot>();
		slots.add(level1);
		slots.add(level2);
		slots.add(level3);
		slots.add(level4);
		slots.add(level5);
		slots.add(level6);
		mVelometer.setVelometerSlots(slots);

		mPopupWindow = new PopupWindow(mPopupView, Constants.OUTER_SIZE,
				Constants.OUTER_SIZE, true);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * Helper functions
	 */
	protected void setupViews(View view) {

		mBtnManiFlight = (Button) view.findViewById(R.id.btn_mani_flight);
		mBtnManiFlight.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = MotionEventCompat.getActionMasked(event);

				switch (action) {
				case MotionEvent.ACTION_DOWN:
					try {
						mMainActivity.resumeSensor();
						mMainActivity.getDevice().setCurrentMsgType(
								Frame.MSG_TYPE_FLIGHTMANIPULATOR);
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} // sleep 100 ms
						
						mMainActivity.getDevice().startSampling(RotationSensor.getMyName());
					} catch (MultipleSampleThreadException e) {
						e.printStackTrace();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					ControlMessageUtils.resetManipulator();
					mMainActivity.getDevice().stopSampling();
					mMainActivity.pauseSensor();
					break;
				}
				return true;
			}
		});

		mBtnDriver = (Button) view.findViewById(R.id.btn_mani_drive);
		mBtnDriver.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = MotionEventCompat.getActionMasked(event);

				switch (action) {
				case MotionEvent.ACTION_DOWN:
					int x = (int) event.getRawX();
					int y = (int) event.getRawY();

					mPopupWindow.showAtLocation(mLayoutRoot,
							Gravity.NO_GRAVITY, x - Constants.OUTER_SIZE / 2, y
									- Constants.OUTER_SIZE / 2);
					mPopupWindow.update();
					mVelometer.onShow(event);

					try {
						mMainActivity.resumeSensor();
						mMainActivity.getDevice().setCurrentMsgType(
								Frame.MSG_TYPE_DRIVEMANIPULATOR);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} // sleep 100 ms
						mMainActivity.getDevice().startSampling(RotationSensor.getMyName(), VelometerSensor.getMyName());
					} catch (MultipleSampleThreadException e) {
						e.printStackTrace();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					mVelometer.onUpdate(event);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					mVelometer.onDisappear();
					mPopupWindow.dismiss();
					ControlMessageUtils.resetManipulator();
					mMainActivity.getDevice().stopSampling();
					mMainActivity.pauseSensor();
					break;
				}
				return true;
			}
		});

		mBtnMultiTouch = (Button) view.findViewById(R.id.btn_mani_multitouch);
		mBtnMultiTouch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransactionHelper.transTo(mMainActivity,
						new MultiTouchFragment(), "multiTouchFragment", true);
			}
		});
		
		mBtnMaps = (Button) view.findViewById(R.id.btn_mani_map);
		mBtnMaps.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentTransactionHelper.transTo(mMainActivity,
						new MapsFragment(), "mapsFragment", true);
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.master, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_disconnect:
			ControlMessageUtils.disconnect();
			return true;
		case R.id.action_release_master:
			ControlMessageUtils.releaseControl();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
