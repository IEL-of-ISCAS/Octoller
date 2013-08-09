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
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.PopupWindow;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.csdtp.exception.MultipleSampleThreadException;
import cn.ac.iscas.iel.vr.octoller.Constants;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.PickingActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.utils.ControlMessageUtils;
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

	protected ImageButton mBtnLock;
	protected Button mBtnManiFlight;
	protected Button mBtnDriver;
	
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
		
		LayoutInflater inflater = (LayoutInflater) mMainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopupView = inflater.inflate(R.layout.popup, null);
		mVelometer = (Velometer) mPopupView.findViewById(R.id.velometer);
		mVelometer.setVelometerLevelListener(mMainActivity.getVeloCallback());
		
		VelometerSlot level1 = new VelometerSlot(1, 0, 60, "1", Color.RED);
		VelometerSlot level2 = new VelometerSlot(2, 60, 120, "2", Color.MAGENTA);
		VelometerSlot level3 = new VelometerSlot(3, 120, 180, "3", Color.BLUE);
		VelometerSlot level4 = new VelometerSlot(4, 180, 240, "4", Color.CYAN);
		VelometerSlot level5 = new VelometerSlot(5, 240, 300, "5", Color.DKGRAY);
		VelometerSlot level6 = new VelometerSlot(6, 300, 360, "6", Color.GREEN);
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
		mBtnLock = (ImageButton) view.findViewById(R.id.btn_lock_view);
		mBtnLock.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO change to fragment
				Intent gotoPickIntent = new Intent(mMainActivity,
						PickingActivity.class);
				mMainActivity.startActivity(gotoPickIntent);
			}
		});

		mBtnManiFlight = (Button) view.findViewById(R.id.btn_mani_flight);
		mBtnManiFlight.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = MotionEventCompat.getActionMasked(event);

				switch (action) {
				case MotionEvent.ACTION_DOWN:
					int x = (int) event.getRawX();
					int y = (int) event.getRawY();
					
					mPopupWindow.showAtLocation(mLayoutRoot, Gravity.NO_GRAVITY, x
							- Constants.OUTER_SIZE / 2, y - Constants.OUTER_SIZE / 2);
					mPopupWindow.update();
					mVelometer.onShow(event);
					
					try {
						mMainActivity.resumeSensor();
						mMainActivity.getDevice().setCurrentMsgType(Frame.MSG_TYPE_FLIGHTMANIPULATOR);
						mMainActivity.getDevice().startSampling();
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
					mMainActivity.pauseSensor();
					mMainActivity.getDevice().stopSampling();
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
					
					mPopupWindow.showAtLocation(mLayoutRoot, Gravity.NO_GRAVITY, x
							- Constants.OUTER_SIZE / 2, y - Constants.OUTER_SIZE / 2);
					mPopupWindow.update();
					mVelometer.onShow(event);
					
					try {
						mMainActivity.resumeSensor();
						mMainActivity.getDevice().setCurrentMsgType(Frame.MSG_TYPE_DRIVEMANIPULATOR);
						mMainActivity.getDevice().startSampling();
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
					mMainActivity.pauseSensor();
					mMainActivity.getDevice().stopSampling();
					break;
				}
				return true;
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
