package cn.ac.iscas.iel.vr.octoller.utils;

import java.util.Locale;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.os.Build;

public class SPenUtils {
	private static final String NOTE = "GT-N";
	private static final String SPEN_FEATURE = "com.sec.feature.spen_usp";

	public static boolean isSPenSupported(Context context) {
		FeatureInfo[] infos = context.getPackageManager().getSystemAvailableFeatures();
		for (FeatureInfo info : infos) {
			if (SPEN_FEATURE.equalsIgnoreCase(info.name)) {
				return true;
			}
		}

		if (Build.MODEL.toUpperCase(Locale.ENGLISH).startsWith(NOTE)) {
			return true;
		}

		return false;
	}
}
