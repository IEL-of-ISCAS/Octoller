/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 5, 201310:31:40 AM
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import cn.ac.iscas.iel.vr.octoller.R;

/**
 * The fragment that display the welcome page
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.fragments
 * @Class WelcomeFragment
 * @Date May 5, 2013 10:31:40 AM
 * @author voidmain
 * @version
 * @since
 */
public class WelcomeFragment extends Fragment {

	protected ImageView mIvOctopus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_welcome, container,
				false);
		mIvOctopus = (ImageView) view.findViewById(R.id.iv_octopus);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		mIvOctopus.clearAnimation();
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
		inflater.inflate(R.menu.welcome, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_connect_bluetooth:
		case R.id.action_connect_wifi:
			Animation animation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.loading);
			mIvOctopus.startAnimation(animation);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
