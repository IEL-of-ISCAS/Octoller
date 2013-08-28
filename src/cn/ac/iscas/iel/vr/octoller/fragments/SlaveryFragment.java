/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 6, 20138:52:03 AM
 */
package cn.ac.iscas.iel.vr.octoller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cn.ac.iscas.iel.csdtp.controller.RotationSensor;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.csdtp.exception.MultipleSampleThreadException;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.utils.ControlMessageUtils;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
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
	}

	@Override
	public void onResume() {
		super.onResume();
		
		try {
			mMainActivity.resumeSensor();
			mMainActivity.getDevice().setCurrentMsgType(Frame.MSG_TYPE_PHONEMOVE);
			mMainActivity.getDevice().startSampling(RotationSensor.getMyName());
		} catch (MultipleSampleThreadException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		mMainActivity.pauseSensor();
		mMainActivity.getDevice().stopSampling();
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
