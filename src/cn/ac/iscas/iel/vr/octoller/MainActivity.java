package cn.ac.iscas.iel.vr.octoller;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import cn.ac.iscas.iel.vr.octoller.connection.BluetoothCommandService;
import cn.ac.iscas.iel.vr.octoller.constants.BTConstants;
import cn.ac.iscas.iel.vr.octoller.constants.Messages;
import cn.ac.iscas.iel.vr.octoller.fragments.MasterFragment;
import cn.ac.iscas.iel.vr.octoller.fragments.SlaveryFragment;
import cn.ac.iscas.iel.vr.octoller.fragments.WelcomeFragment;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {

	// Intent request codes
	private static final int REQUEST_ENABLE_BT = 1001;

	private SensorManager mSensorManager;
	private Sensor mPhyAccSensor;
	private Sensor mPhyMagSensor;
	private Sensor mPhyRotSensor;

	private MainSensorListener mSensorListener;

	private static final String phoneID = UUID.randomUUID().toString();

	private boolean mIsSendRotData;
	private boolean mIsConnect;
	private boolean mIsControl;
	private boolean mIsLock;
	private int mCurrentMsg;

	private float[] mQuaternion = new float[4];

	public void setCurrentMsg(int msg) {
		mCurrentMsg = msg;
	}

	public int getCurrentMsg() {
		return mCurrentMsg;
	}

	public boolean isSendRotData() {
		return mIsSendRotData;
	}

	public void setIsSendRotData(boolean isSendRotData) {
		this.mIsSendRotData = isSendRotData;
	}

	public void setIsLock(boolean isLock) {
		mIsLock = isLock;
	}

	public String getPhoneID() {
		return phoneID;
	}

	private String mConnectedDeviceName = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothCommandService mCommandService = null;

	public BluetoothCommandService getBluetoothService() {
		return mCommandService;
	}

	public void setBluetoothService(BluetoothCommandService service) {
		mCommandService = service;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
		mPhyAccSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mPhyMagSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mPhyRotSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		mIsSendRotData = false;
		mIsConnect = false;
		mIsControl = false;
		mIsLock = false;

		mSensorListener = new MainSensorListener();

		FragmentTransactionHelper.transTo(this, new WelcomeFragment(),
				"welcomeFragment", true);
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

		case R.id.action_connect_bluetooth:
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

			if (mBluetoothAdapter == null) {
				Toast.makeText(this, R.string.error_bluetooth_not_supported,
						Toast.LENGTH_LONG).show();
				return true;
			}

			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			} else {
				if (mCommandService == null) {
					setupCommand();
				}
			}

			return false;
		case R.id.action_connect_wifi:
			// TODO
			return false;
		case R.id.action_disconnect:
			mCommandService
					.write(("{\"phoneID\":\"" + phoneID + "\",\"msgType\":2}\n")
							.getBytes());
			return false;
		case R.id.action_request_master:
			mCommandService
					.write(("{\"phoneID\":\"" + phoneID + "\",\"msgType\":4}\n")
							.getBytes());
			return false;
		case R.id.action_release_master:
			mCommandService
					.write(("{\"phoneID\":\"" + phoneID + "\",\"msgType\":3}\n")
							.getBytes());
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mCommandService
				.write(("{\"phoneID\":\"" + phoneID + "\",\"msgType\":2}\n")
						.getBytes());

		if (mCommandService != null)
			mCommandService.stop();
	}

	public void resumeSensor() {
		mSensorManager.registerListener(mSensorListener, mPhyAccSensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(mSensorListener, mPhyMagSensor,
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(mSensorListener, mPhyRotSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	public void pauseSensor() {
		mSensorManager.unregisterListener(mSensorListener);
	}

	private void setupCommand() {
		mCommandService = new BluetoothCommandService(this, mHandler);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String address = prefs.getString(
				getString(R.string.prefs_key_bt_target_device), "");
		if (TextUtils.isEmpty(address)) {
			// Need to choose the paired device
			Toast.makeText(this,
					R.string.error_bluetooth_target_device_not_set,
					Toast.LENGTH_SHORT).show();
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
		} else {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
			mCommandService.connect(device);
		}
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
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
				if (mIsConnect) {
					if (mIsSendRotData) {
						if (mIsControl || mIsLock) {
							StringBuffer stringBuffer = new StringBuffer(
									"{\"phoneID\":\"" + phoneID
											+ "\",\"msgType\":" + mCurrentMsg
											+ ",\"rotation\":[");
							SensorManager.getQuaternionFromVector(mQuaternion,
									event.values);

							stringBuffer.append(mQuaternion[0] + ","
									+ mQuaternion[1] + "," + mQuaternion[2]
									+ "," + mQuaternion[3] + "]}\n");

							mCommandService.write(String.valueOf(stringBuffer)
									.getBytes());
						}
					} else {
						if (mIsControl) {
							StringBuffer stringBuffer = new StringBuffer(
									"{\"phoneID\":\""
											+ phoneID
											+ "\",\"msgType\":22,\"rotation\":[");
							SensorManager.getQuaternionFromVector(mQuaternion,
									event.values);

							stringBuffer.append(mQuaternion[0] + ","
									+ mQuaternion[1] + "," + mQuaternion[2]
									+ "," + mQuaternion[3] + "]}\n");

							mCommandService.write(String.valueOf(stringBuffer)
									.getBytes());
						}
					}
				}
			}
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BTConstants.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothCommandService.STATE_CONNECTED:
					// send newconnect message to server
					mCommandService
							.write(("{\"phoneID\":\"" + phoneID + "\",\"msgType\":1}\n")
									.getBytes());
					break;
				case BluetoothCommandService.STATE_CONNECTING:
					break;
				case BluetoothCommandService.STATE_LISTEN:
				case BluetoothCommandService.STATE_NONE:
					Toast.makeText(getApplication(),
							R.string.error_bluetooth_not_connected,
							Toast.LENGTH_SHORT).show();
					break;
				}
				break;
			case BTConstants.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(
						BTConstants.KEY_DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case BTConstants.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(BTConstants.KEY_TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			case BTConstants.MESSAGE_READ:
				switch (msg.arg1) {
				case Messages.NEWCONNECT:
					if (msg.arg2 == 1) {
						FragmentTransactionHelper.transTo(MainActivity.this,
								new SlaveryFragment(), "slaveryFragment", true);
						Toast.makeText(getApplicationContext(), "连接成功",
								Toast.LENGTH_SHORT).show();
						mIsConnect = true;
					} else {
						Toast.makeText(getApplicationContext(), "连接失败",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case Messages.DISCONNECT:
					if (msg.arg2 == 1) {
						Toast.makeText(getApplicationContext(), "断开成功",
								Toast.LENGTH_SHORT).show();
						if (!MainActivity.this.isFinishing()) {
							FragmentTransactionHelper.transTo(
									MainActivity.this, new WelcomeFragment(),
									"welcomeFragment", true);
							mIsConnect = false;
						}
					} else {
						Toast.makeText(getApplicationContext(), "断开失败",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case Messages.REQUESTCONTROL:
					if (msg.arg2 == 1) {
						FragmentTransactionHelper.transTo(MainActivity.this,
								new MasterFragment(), "masterFragment", true);
						Toast.makeText(getApplicationContext(), "申请控制成功:)",
								Toast.LENGTH_SHORT).show();
						mIsControl = true;
					} else {
						Toast.makeText(getApplicationContext(), "申请控制失败:(",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case Messages.GIVEUPCONTROL:
					if (msg.arg2 == 1) {
						FragmentTransactionHelper.transTo(MainActivity.this,
								new SlaveryFragment(), "slaveryFragment", true);
						Toast.makeText(getApplicationContext(), "放弃控制成功:)",
								Toast.LENGTH_SHORT).show();
						mIsControl = false;
					} else {
						Toast.makeText(getApplicationContext(), "放弃控制失败:(",
								Toast.LENGTH_SHORT).show();
					}
					break;

				default:
					break;
				}
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				setupCommand();
			} else {
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
