/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 10, 20139:24:08 PM
 */
package cn.ac.iscas.iel.vr.octoller.fragments;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import cn.ac.iscas.iel.vr.octoller.R;

/**
 * A fragment that does the dirty for preference for us
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.fragments
 * @Class PrefsFragment
 * @Date May 10, 2013 9:24:08 PM
 * @author voidmain
 * @version
 * @since
 */
public class PrefsFragment extends PreferenceFragment {
	// Member fields
	private BluetoothAdapter mBtAdapter;
	
	private ListPreference mPairedDevicePreference;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.pref_connection);
		
		mPairedDevicePreference = (ListPreference) findPreference(getString(R.string.prefs_key_bt_target_device));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		CharSequence[] deviceList = new CharSequence[pairedDevices.size()];
		CharSequence[] deviceAddressList = new CharSequence[pairedDevices.size()];
		int idx = 0;
		for(BluetoothDevice device : pairedDevices) {
			deviceList[idx] = device.getName() + "\n" + device.getAddress();
			deviceAddressList[idx] = device.getAddress();
			idx++;
		}
		
		mPairedDevicePreference.setDefaultValue("");
		mPairedDevicePreference.setEntries(deviceList);
		mPairedDevicePreference.setEntryValues(deviceAddressList);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
	}

}
