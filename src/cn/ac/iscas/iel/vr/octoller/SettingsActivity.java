package cn.ac.iscas.iel.vr.octoller;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

/**
 * The settings activity
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller
 * @Class SettingsActivity
 * @Date May 6, 2013 2:13:32 PM
 * @author voidmain
 * @version
 * @since
 */
public class SettingsActivity extends PreferenceActivity {
	
	// Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		updateActionBarDisplay();
	}

	protected void updateActionBarDisplay() {
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent backHome = new Intent(this, MainActivity.class);
			backHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(backHome);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
