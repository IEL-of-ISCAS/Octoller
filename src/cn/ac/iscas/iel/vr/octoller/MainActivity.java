package cn.ac.iscas.iel.vr.octoller;

import android.app.Activity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import cn.ac.iscas.iel.csdtp.channel.SocketOutputChannel;
import cn.ac.iscas.iel.csdtp.controller.AccelerometersSensor;
import cn.ac.iscas.iel.csdtp.controller.Device;
import cn.ac.iscas.iel.csdtp.controller.MagnetometersSensor;
import cn.ac.iscas.iel.csdtp.controller.RotationSensor;
import cn.ac.iscas.iel.csdtp.data.SensorData;
import cn.ac.iscas.iel.csdtp.exception.ChangeSensorWhileCollectingDataException;
import cn.ac.iscas.iel.vr.octoller.fragments.WelcomeFragment;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;

public class MainActivity extends Activity {

	private static final String SERVER_IP = "10.0.0.96";
	private static final int SERVER_PORT = 6666;

	private Device mDevice;
	private AccelerometersSensor mAccSensor;
	private MagnetometersSensor mMagSensor;
	private RotationSensor mRotSensor;

	private SensorManager mSensorManager;
	private Sensor mPhyAccSensor;
	private Sensor mPhyMagSensor;
	private Sensor mPhyRotSensor;

	private MainSensorListener mSensorListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDevice = new Device("android");
		mDevice.setOutputChannel(new SocketOutputChannel(SERVER_IP, SERVER_PORT));

		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		mPhyAccSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mPhyMagSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mPhyRotSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

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
		
		FragmentTransactionHelper.transTo(this, new WelcomeFragment(), "welcomeFragment", true);
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
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mSensorListener, mPhyMagSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mSensorListener, mPhyRotSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
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
			SensorData<float[]> data = new SensorData<float[]>(event.values);
			if (event.sensor == mPhyAccSensor) {
				mAccSensor.updateSnapshot(data);
			} else if (event.sensor == mPhyMagSensor) {
				mMagSensor.updateSnapshot(data);
			} else if (event.sensor == mPhyRotSensor) {
				mRotSensor.updateSnapshot(data);
			}
		}
	}

}
