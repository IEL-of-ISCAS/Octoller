/*
 * Copyright (C) 2013 Void Main Studio 
 * Project:Octoller
 * Author: voidmain
 * Create Date: May 5, 201310:31:40 AM
 */
package cn.ac.iscas.iel.vr.octoller.fragments;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
import android.widget.Toast;
import cn.ac.iscas.iel.csdtp.channel.ConnectionChannel;
import cn.ac.iscas.iel.csdtp.channel.SocketChannel;
import cn.ac.iscas.iel.vr.octoller.Constants;
import cn.ac.iscas.iel.vr.octoller.DeviceListActivity;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.utils.ControlMessageUtils;

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
	protected MainActivity mMainActivity;

	protected ImageView mIvOctopus;
	protected ConnectionChannel mOutputChannel;
	protected BluetoothAdapter mBluetoothAdapter;

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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mMainActivity = (MainActivity) getActivity();

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			Toast.makeText(mMainActivity, R.string.bt_not_avaliable,
					Toast.LENGTH_LONG).show();
			mMainActivity.finish();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		
		mIvOctopus.clearAnimation();
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
		if (item.getItemId() == R.id.action_connect_wifi
				|| item.getItemId() == R.id.action_connect_bt) {
			if (item.getItemId() == R.id.action_connect_bt) {
				if (!mBluetoothAdapter.isEnabled()) {
					Intent enableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent,
							Constants.REQUEST_ENABLE_BT);
				} else {
					tryToConnect();
				}
			} else if (item.getItemId() == R.id.action_connect_wifi) {
				Animation animation = AnimationUtils.loadAnimation(
						getActivity(), R.anim.loading);
				mIvOctopus.startAnimation(animation);
				
				

				new Thread() {
					public void run() {
						Socket socket = null;
						DataInputStream inStream = null;
						DataOutputStream outStream = null;
						
						try {
							socket = new Socket(Constants.SERVER_IP,
									Constants.SERVER_PORT);
							socket.setTcpNoDelay(true);
							socket.setKeepAlive(true);
							
							outStream = new DataOutputStream(socket.getOutputStream());
							inStream = new DataInputStream(socket.getInputStream());
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						mOutputChannel = new SocketChannel(inStream, outStream);
						mOutputChannel.setCallback(mMainActivity.getCallback());
						
						mMainActivity.getDevice().setOutputChannel(mOutputChannel);
						mMainActivity.getDevice().startSending();
						
						ControlMessageUtils.connect();
					}
				}.start();
			}

			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.REQUEST_CONNECT_DEVICE:
			if (resultCode == Activity.RESULT_OK) {
				Animation animation = AnimationUtils.loadAnimation(
						getActivity(), R.anim.loading);
				mIvOctopus.startAnimation(animation);
				
				mBluetoothAdapter.cancelDiscovery();
				
				String address = data.getExtras().getString(
						Constants.DEVICE_NAME);
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				
				BluetoothSocket btSocket = null;
				try {
					btSocket = device
							.createRfcommSocketToServiceRecord(Constants.BT_UUID);
					btSocket.connect();
					
				} catch (IOException e3) {
					e3.printStackTrace();
				}

				DataInputStream inStream = null;
				DataOutputStream outStream = null;
				try {
					outStream = new DataOutputStream(btSocket.getOutputStream());
					inStream = new DataInputStream(btSocket.getInputStream());
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				mOutputChannel = new SocketChannel(inStream, outStream);
				mOutputChannel.setCallback(mMainActivity.getCallback());

				mMainActivity.getDevice().setOutputChannel(mOutputChannel);
				mMainActivity.getDevice().startSending();

				ControlMessageUtils.connect();
			}
			break;
		case Constants.REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				tryToConnect();
			} else {
				Toast.makeText(mMainActivity, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				mMainActivity.finish();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void tryToConnect() {
		Intent listBTDevice = new Intent(getActivity(),
				DeviceListActivity.class);
		startActivityForResult(listBTDevice, Constants.REQUEST_CONNECT_DEVICE);
	}

}
