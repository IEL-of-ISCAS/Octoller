/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 6, 201310:57:42 AM
 */
package cn.ac.iscas.iel.vr.octoller.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import cn.ac.iscas.iel.vr.octoller.R;

/**
 * Helper function to create the transcation
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.utils
 * @Class FragmentTransactionHelper
 * @Date May 6, 2013 10:57:42 AM
 * @author voidmain
 * @version
 * @since
 */
public class FragmentTransactionHelper {

	public static void transTo(Fragment fragment, Fragment newFragment,
			String name, boolean forward) {
		transTo(fragment.getActivity(), newFragment, name, forward);
	}

	public static void transTo(Activity activity, Fragment newFragment,
			String name, boolean forward) {
		FragmentTransaction ft = activity.getFragmentManager()
				.beginTransaction();
		if (forward) {
			ft.setCustomAnimations(R.animator.slide_in_right,
					R.animator.slide_out_left);
		} else {
			ft.setCustomAnimations(R.animator.slide_in_left,
					R.animator.slide_out_right);
		}

		ft.replace(R.id.fragment_container, newFragment, name).addToBackStack(name);

		ft.commit();
	}

}
