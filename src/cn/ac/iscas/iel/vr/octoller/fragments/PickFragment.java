package cn.ac.iscas.iel.vr.octoller.fragments;

import java.util.Arrays;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import cn.ac.iscas.iel.csdtp.controller.RotationSensor;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.csdtp.data.SensorData;
import cn.ac.iscas.iel.csdtp.exception.MultipleSampleThreadException;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.utils.ControlMessageUtils;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;

public class PickFragment extends Fragment {
	public static final int IDX_IN_OUT = 0;
	public static final int IDX_LEFT_RIGHT = 1;
	public static final int IDX_UP_DOWN = 2;
	public static final int IDX_ROTATION = 3;
	public static final int DIFF_UNIT = 10;
	
	protected MainActivity mMainActivity;
	protected View mBtnMoveUp;
	protected View mBtnMoveDown;
	protected View mBtnMoveLeft;
	protected View mBtnMoveRight;
	protected View mBtnMoveIn;
	protected View mBtnMoveOut;
	protected View mBtnMoveClockwise;
	protected View mBtnMoveCounterClockwise;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_pick, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMainActivity = (MainActivity) this.getActivity();
		
		setupViews(getView());
	}
	
	private void setupViews(View view) {
		mBtnMoveUp = view.findViewById(R.id.btn_move_up);
		mBtnMoveDown = view.findViewById(R.id.btn_move_down);
		mBtnMoveLeft = view.findViewById(R.id.btn_move_left);
		mBtnMoveRight = view.findViewById(R.id.btn_move_right);
		mBtnMoveIn = view.findViewById(R.id.btn_move_in);
		mBtnMoveOut = view.findViewById(R.id.btn_move_out);
		mBtnMoveClockwise = view.findViewById(R.id.btn_move_clockwise);
		mBtnMoveCounterClockwise = view.findViewById(R.id.btn_move_counter_clockwise);
		
		mBtnMoveUp.setOnTouchListener(mListener);
		mBtnMoveDown.setOnTouchListener(mListener);
		mBtnMoveLeft.setOnTouchListener(mListener);
		mBtnMoveRight.setOnTouchListener(mListener);
		mBtnMoveIn.setOnTouchListener(mListener);
		mBtnMoveOut.setOnTouchListener(mListener);
		mBtnMoveClockwise.setOnTouchListener(mListener);
		mBtnMoveCounterClockwise.setOnTouchListener(mListener);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.picking, menu);
	}
	
	protected View.OnTouchListener mListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int action = MotionEventCompat.getActionMasked(event);

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				float[] values = {0, 0, 0, 0};
				
				switch (v.getId()) {
				case R.id.btn_move_up:
					values[IDX_UP_DOWN] = DIFF_UNIT; 
					break;
				case R.id.btn_move_down:
					values[IDX_UP_DOWN] = -DIFF_UNIT; 
					break;
				case R.id.btn_move_left:
					values[IDX_LEFT_RIGHT] = -DIFF_UNIT; 
					break;
				case R.id.btn_move_right:
					values[IDX_LEFT_RIGHT] = DIFF_UNIT; 
					break;
				case R.id.btn_move_in:
					values[IDX_IN_OUT] = -DIFF_UNIT; 
					break;
				case R.id.btn_move_out:
					values[IDX_IN_OUT] = DIFF_UNIT; 
					break;
				case R.id.btn_move_clockwise:
					values[IDX_ROTATION] = -DIFF_UNIT; 
					break;
				case R.id.btn_move_counter_clockwise:
					values[IDX_ROTATION] = DIFF_UNIT; 
					break;
				default:
					break;
				}
				
				SensorData<float[]> data = new SensorData<float[]>(Arrays.copyOf(
						values, values.length));
				mMainActivity.getRotationSensor().updateSnapshot(data);
				
				try {
					mMainActivity.getDevice().setCurrentMsgType(
							Frame.MSG_TYPE_MOVE_OBJECT);
					mMainActivity.getDevice().startSampling(
							RotationSensor.getMyName());
				} catch (MultipleSampleThreadException e) {
					e.printStackTrace();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mMainActivity.getDevice().stopSampling();
				break;
			}
			
			return true;
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_go_back:
			ControlMessageUtils.doUnpick();
			FragmentTransactionHelper.transTo(mMainActivity, new SlaveryFragment(),
					"slaveryFragment", false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
