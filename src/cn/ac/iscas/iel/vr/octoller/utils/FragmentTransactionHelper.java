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
		if(activity.isFinishing()) return;
		
		FragmentTransaction ft = activity.getFragmentManager()
				.beginTransaction();
		if (forward) {
			ft.setCustomAnimations(R.animator.rotate_in_left_to_right,
					R.animator.rotate_out_left_to_right);
		} else {
			ft.setCustomAnimations(R.animator.rotate_in_right_to_left,
					R.animator.rotate_out_right_to_left);
		}

		ft.replace(R.id.fragment_container, newFragment, name);

		ft.commit();
	}

}
