package cn.ac.iscas.iel.vr.octoller;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import cn.ac.iscas.iel.csdtp.channel.IChannelCallback;
import cn.ac.iscas.iel.csdtp.controller.Device;
import cn.ac.iscas.iel.csdtp.controller.RotationSensor;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.csdtp.data.ResponseData;
import cn.ac.iscas.iel.csdtp.data.SensorData;
import cn.ac.iscas.iel.csdtp.exception.ChangeSensorWhileCollectingDataException;
import cn.ac.iscas.iel.vr.octoller.fragments.MasterFragment;
import cn.ac.iscas.iel.vr.octoller.fragments.SlaveryFragment;
import cn.ac.iscas.iel.vr.octoller.fragments.WelcomeFragment;
import cn.ac.iscas.iel.vr.octoller.utils.ControlMessageUtils;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;

public class MainActivity extends Activity {
	private Device mDevice;
	private RotationSensor mRotSensor;

	private SensorManager mSensorManager;
	private Sensor mPhyRotSensor;

	private MainSensorListener mSensorListener;
	private ChannelResponseCallback mChannelResponse;

	private Handler mMsgHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// setup helper class
		ControlMessageUtils.setActivty(this);

		mDevice = new Device("android");
		mChannelResponse = new ChannelResponseCallback();

		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		mPhyRotSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		mRotSensor = new RotationSensor();

		try {
			mDevice.registerSensor(mRotSensor);
		} catch (ChangeSensorWhileCollectingDataException e) {
			e.printStackTrace();
		}

		mSensorListener = new MainSensorListener();

		FragmentTransactionHelper.transTo(this, new WelcomeFragment(),
				"welcomeFragment", true);

		mMsgHandler = new ChannelMessageHandler();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mDevice.setOutputChannel(null);
		ControlMessageUtils.disconnect();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mDevice.stopSending();
	}

	public Device getDevice() {
		return mDevice;
	}
	
	public IChannelCallback getCallback() {
		return mChannelResponse;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settingsIntent = new Intent(MainActivity.this,
					SettingsActivity.class);
			MainActivity.this.startActivity(settingsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		pauseSensor();
	}

	@Override
	protected void onResume() {
		super.onResume();
		resumeSensor();
	}

	public void resumeSensor() {
		mSensorManager.registerListener(mSensorListener, mPhyRotSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	public void pauseSensor() {
		mSensorManager.unregisterListener(mSensorListener);
	}

	/**
	 * Implements the sensor listener to update sensor data
	 * 
	 * 
	 * @Project Octoller
	 * @Package cn.ac.iscas.iel.vr.octoller
	 * @Class MainSensorListener
	 * @Date May 6, 2013 9:33:29 AM
	 * @author voidmain
	 * @version
	 * @since
	 */
	protected class MainSensorListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// Don't care
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			SensorData<float[]> data = new SensorData<float[]>(Arrays.copyOf(
					event.values, event.values.length));
			if (event.sensor == mPhyRotSensor) {
				float[] quaternion = new float[4];
				SensorManager.getQuaternionFromVector(quaternion, event.values);
				data = new SensorData<float[]>(Arrays.copyOf(quaternion,
						quaternion.length));
				mRotSensor.updateSnapshot(data);
			}
		}
	}

	protected class ChannelResponseCallback implements IChannelCallback {

		@Override
		public void onResponse(ResponseData data) {
			if (data.getMsgType() == Frame.MSG_TYPE_NEWCONNECT) {
				if (data.getStatus() == Frame.STATUS_SUCCESS) {
					FragmentTransactionHelper.transTo(MainActivity.this,
							new SlaveryFragment(), "slaveryFragment", true);
				} else {
					Message msg = new Message();
					msg.what = Constants.MSG_CONNECT_ERROR;
					msg.arg1 = data.getMsgType();
					msg.obj = data.getErrorMsg();
					mMsgHandler.sendMessage(msg);
				}
			} else if (data.getMsgType() == Frame.MSG_TYPE_DISCONNECT) {
				FragmentTransactionHelper.transTo(MainActivity.this,
						new WelcomeFragment(), "welcomeFragment", false);
			} else if (data.getMsgType() == Frame.MSG_TYPE_REQUESTCONTROL) {
				if (data.getStatus() == Frame.STATUS_SUCCESS) {
					FragmentTransactionHelper.transTo(MainActivity.this,
							new MasterFragment(), "masterFragment", true);
				} else {
					Message msg = new Message();
					msg.what = Constants.MSG_REQUEST_ERROR;
					msg.arg1 = data.getMsgType();
					msg.obj = data.getErrorMsg();
					mMsgHandler.sendMessage(msg);
				}
			} else if (data.getMsgType() == Frame.MSG_TYPE_GIVEUPCONTROL) {
				FragmentTransactionHelper.transTo(MainActivity.this,
						new SlaveryFragment(), "slaveryFragment", false);
			}
		}

	}

	@SuppressLint("HandlerLeak")
	protected class ChannelMessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getApplicationContext(), msg.obj.toString(),
					Toast.LENGTH_LONG).show();
		}

	}

}
