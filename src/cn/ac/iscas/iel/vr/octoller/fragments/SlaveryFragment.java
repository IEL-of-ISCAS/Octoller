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
import cn.ac.iscas.iel.vr.octoller.R;

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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.slavery, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_disconnect:
			return true;
		case R.id.action_request_master:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
