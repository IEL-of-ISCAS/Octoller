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
import cn.ac.iscas.iel.csdtp.channel.OutputChannel;
import cn.ac.iscas.iel.csdtp.channel.SocketOutputChannel;
import cn.ac.iscas.iel.csdtp.controller.AccelerometersSensor;
import cn.ac.iscas.iel.csdtp.controller.Device;
import cn.ac.iscas.iel.csdtp.controller.MagnetometersSensor;
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

	private static final String SERVER_IP = "10.0.0.96";
	private static final int SERVER_PORT = 6666;

	private Device mDevice;
	private OutputChannel mOutputChannel;
	private AccelerometersSensor mAccSensor;
	private MagnetometersSensor mMagSensor;
	private RotationSensor mRotSensor;

	private SensorManager mSensorManager;
	private Sensor mPhyAccSensor;
	private Sensor mPhyMagSensor;
	private Sensor mPhyGyroSensor;

	// Used to calc rotation vector
	private static final float EPSILON = 0.0001f;
	private static final float NS2S = 1.0f / 1000000000.0f;
	private final float[] mDeltaRotationVector = new float[4];
	private float mTimestamp;

	private MainSensorListener mSensorListener;
	private ChannelResponseCallback mChannelResponse;

	private Handler mMsgHandler;

	private static final int MSG_CONNECT_ERROR = 0;
	private static final int MSG_REQUEST_ERROR = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// setup helper class
		ControlMessageUtils.setActivty(this);

		mDevice = new Device("android");
		mChannelResponse = new ChannelResponseCallback();

		mOutputChannel = new SocketOutputChannel(SERVER_IP, SERVER_PORT);
		mOutputChannel.setCallback(mChannelResponse);
		mDevice.setOutputChannel(mOutputChannel);
		mDevice.startSending();

		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		mPhyAccSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mPhyMagSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mPhyGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		mAccSensor = new AccelerometersSensor();
		mMagSensor = new MagnetometersSensor();
		mRotSensor = new RotationSensor();

		try {
			mDevice.registerSensor(mAccSensor);
			mDevice.registerSensor(mMagSensor);
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

		mOutputChannel.setCallback(null);
		mDevice.setOutputChannel(mOutputChannel);
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
		mSensorManager.registerListener(mSensorListener, mPhyAccSensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(mSensorListener, mPhyMagSensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(mSensorListener, mPhyGyroSensor,
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
			if (event.sensor == mPhyAccSensor) {
				mAccSensor.updateSnapshot(data);
			} else if (event.sensor == mPhyMagSensor) {
				mMagSensor.updateSnapshot(data);
			} else if (event.sensor == mPhyGyroSensor) {
				if (mTimestamp != 0) {
					final float dT = (event.timestamp - mTimestamp) * NS2S;
					float axisX = event.values[0];
					float axisY = event.values[1];
					float axisZ = event.values[2];

					float omegaMagnitude = (float) Math.sqrt(axisX * axisX
							+ axisY * axisY + axisZ * axisZ);

					if (omegaMagnitude > EPSILON) {
						axisX /= omegaMagnitude;
						axisY /= omegaMagnitude;
						axisZ /= omegaMagnitude;
					}

					float thetaOverTwo = omegaMagnitude * dT / 2.0f;
					float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
					float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
					mDeltaRotationVector[0] = sinThetaOverTwo * axisX;
					mDeltaRotationVector[1] = sinThetaOverTwo * axisY;
					mDeltaRotationVector[2] = sinThetaOverTwo * axisZ;
					mDeltaRotationVector[3] = cosThetaOverTwo;

					data = new SensorData<float[]>(Arrays.copyOf(
							mDeltaRotationVector, mDeltaRotationVector.length));
					mRotSensor.updateSnapshot(data);
				}
				mTimestamp = event.timestamp;
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
					msg.what = MSG_CONNECT_ERROR;
					msg.arg1 = data.getMsgType();
					msg.obj = data.getError();
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
					msg.what = MSG_REQUEST_ERROR;
					msg.arg1 = data.getMsgType();
					msg.obj = data.getError();
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
