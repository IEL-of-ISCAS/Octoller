package cn.ac.iscas.iel.vr.octoller.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;

public class VoiceRecongnitionUtils {

	public static boolean isSupportVoiceRecongnition(Context context) {
		// Disable button if no recognition service is present
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		return activities.size() != 0;
	}

}
