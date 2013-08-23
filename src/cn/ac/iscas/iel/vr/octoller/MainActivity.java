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
import cn.ac.iscas.iel.csdtp.controller.TouchScreenSensor;
import cn.ac.iscas.iel.csdtp.controller.VelometerSensor;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.csdtp.data.ResponseData;
import cn.ac.iscas.iel.csdtp.data.SensorData;
import cn.ac.iscas.iel.csdtp.exception.ChangeSensorWhileCollectingDataException;
import cn.ac.iscas.iel.vr.octoller.fragments.MasterFragment;
import cn.ac.iscas.iel.vr.octoller.fragments.SlaveryFragment;
import cn.ac.iscas.iel.vr.octoller.fragments.WelcomeFragment;
import cn.ac.iscas.iel.vr.octoller.utils.ControlMessageUtils;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;
import cn.ac.iscas.iel.vr.octoller.view.IOverlayTouchListener;
import cn.ac.iscas.iel.vr.octoller.view.IVelometerLevelListener;
import cn.ac.iscas.iel.vr.octoller.view.TRZGestureDetector.MultiTouchEventListener;
import cn.ac.iscas.iel.vr.octoller.view.Velometer;

public class MainActivity extends Activity {
	private Device mDevice;
	private RotationSensor mRotSensor;
	private VelometerSensor mVeloSensor;
	private TouchScreenSensor mTouchSensor;

	private SensorManager mSensorManager;
	private Sensor mPhyRotSensor;

	private MainSensorListener mSensorListener;
	private ChannelResponseCallback mChannelResponse;
	private VeloLevelCallback mVeloCallback;
	private TRZGestureCallback mGestureCallback;
	private CameraRegionCallback mCameraCallback;

	private Handler mMsgHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// setup helper class
		ControlMessageUtils.setActivty(this);

		mDevice = new Device("android");
		mDevice.setSampleRate(20);
		mChannelResponse = new ChannelResponseCallback();

		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		mPhyRotSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		mRotSensor = new RotationSensor();
		mVeloSensor = new VelometerSensor();
		mTouchSensor = new TouchScreenSensor();

		try {
			mDevice.registerSensor(mRotSensor);
			mDevice.registerSensor(mVeloSensor);
			mDevice.registerSensor(mTouchSensor);
		} catch (ChangeSensorWhileCollectingDataException e) {
			e.printStackTrace();
		}

		mSensorListener = new MainSensorListener();

		FragmentTransactionHelper.transTo(this, new WelcomeFragment(),
				"welcomeFragment", true);

		mMsgHandler = new ChannelMessageHandler();

		SensorData<Integer> data = new SensorData<Integer>();
		data.setD(Velometer.INVALID_LEVEL);
		mVeloSensor.updateSnapshot(data);

		float[] emptyArray = { 0f, 0f, 0f, 0f };
		SensorData<float[]> touchData = new SensorData<float[]>(emptyArray);
		mTouchSensor.updateSnapshot(touchData);

		mVeloCallback = new VeloLevelCallback();
		mGestureCallback = new TRZGestureCallback();
		mCameraCallback = new CameraRegionCallback();
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

	public IVelometerLevelListener getVeloCallback() {
		return mVeloCallback;
	}

	public MultiTouchEventListener getMatrixCallback() {
		return mGestureCallback;
	}
	
	public IOverlayTouchListener getCameraCallback() {
		return mCameraCallback;
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
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void pauseSensor() {
		float[] array = { 0f, 0f, 0f, 1.0f };
		SensorData<float[]> originData = new SensorData<float[]>(Arrays.copyOf(
				array, array.length));

		mRotSensor.updateSnapshot(originData);

		float[] emptyArray = { 0f, 0f, 0f, 0f };
		SensorData<float[]> touchData = new SensorData<float[]>(emptyArray);
		mTouchSensor.updateSnapshot(touchData);
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

	protected class VeloLevelCallback implements IVelometerLevelListener {

		@Override
		public void onLevelChanged(int level) {
			SensorData<Integer> data = new SensorData<Integer>();
			data.setD(level);
			mVeloSensor.updateSnapshot(data);
		}

	}

	protected class TRZGestureCallback implements MultiTouchEventListener {

		@Override
		public void onEventChanged(int phase, int type, float x, float y) {
			float[] touchArray = { phase, type, x, y };
			SensorData<float[]> touchData = new SensorData<float[]>(touchArray);
			mTouchSensor.updateSnapshot(touchData);
		}

	}
	
	protected class CameraRegionCallback implements IOverlayTouchListener {

		@Override
		public void onOverlayRegionChanged(float centerX, float centerY) {
			float[] touchArray = { centerX, centerY };
			SensorData<float[]> touchData = new SensorData<float[]>(touchArray);
			mTouchSensor.updateSnapshot(touchData);
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
