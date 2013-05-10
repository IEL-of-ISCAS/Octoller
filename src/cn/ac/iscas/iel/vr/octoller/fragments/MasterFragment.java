/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 6, 20138:51:16 AM
 */
package cn.ac.iscas.iel.vr.octoller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.connection.BluetoothCommandService;
import cn.ac.iscas.iel.vr.octoller.constants.Messages;

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
	protected BluetoothCommandService mBluetoothService;

	protected ImageButton mBtnLock;
	protected Button mBtnManiFlight;
	protected Button mBtnDriver;

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
		mBluetoothService = mMainActivity.getBluetoothService();
		setupViews(this.getView());
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
			}
		});

		mBtnManiFlight = (Button) view.findViewById(R.id.btn_mani_flight);
		mBtnManiFlight.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = MotionEventCompat.getActionMasked(event);

				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mMainActivity.setCurrentMsg(Messages.FLIGHTMANIPULATOR);
					mMainActivity.setIsSendRotData(true);
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					mBluetoothService.write(("{\"phoneID\":" + mMainActivity.getPhoneID() + ",\"msgType\":20,\"rotation\":}\n").getBytes());
					mMainActivity.setIsSendRotData(false);
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
					mMainActivity.setCurrentMsg(Messages.DRIVEMANIPULATOR);
					mMainActivity.setIsSendRotData(true);
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					mBluetoothService.write(("{\"phoneID\":" + mMainActivity.getPhoneID() + ",\"msgType\":20,\"rotation\":}\n").getBytes());
					mMainActivity.setIsSendRotData(false);
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
			return true;
		case R.id.action_release_master:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
