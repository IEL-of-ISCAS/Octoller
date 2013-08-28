package cn.ac.iscas.iel.vr.octoller.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import cn.ac.iscas.iel.csdtp.controller.TouchScreenSensor;
import cn.ac.iscas.iel.csdtp.data.Frame;
import cn.ac.iscas.iel.csdtp.exception.MultipleSampleThreadException;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;
import cn.ac.iscas.iel.vr.octoller.view.TRZGestureDetector;
import cn.ac.iscas.iel.vr.octoller.view.TouchPointVisualizer;

/**
 * Fragment that handles multitouch events
 * 
 * @Project Octoller
 * @Package cn.ac.iscas.iel.vr.octoller.fragments
 * @Class ServantFragment
 * @Date May 6, 2013 8:52:03 AM
 * @author voidmain
 * @version
 * @since
 */
public class MultiTouchFragment extends Fragment {
	protected MainActivity mMainActivity;
	protected TouchPointVisualizer mTouchPointVisualizer = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_multitouch, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mMainActivity = (MainActivity) this.getActivity();
		View view = this.getView();
		mTouchPointVisualizer = (TouchPointVisualizer) view
				.findViewById(R.id.tp_visualizer);
		
		TRZGestureDetector detector = new TRZGestureDetector();
		detector.setMultiTouchEventListener(mMainActivity.getMatrixCallback());
		mTouchPointVisualizer.setTRZDetector(detector);
		mTouchPointVisualizer.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTouchPointVisualizer.getTRZDetector().setViewWidth(v.getMeasuredWidth());
				mTouchPointVisualizer.getTRZDetector().setViewHeight(v.getMeasuredHeight());
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					try {
						mMainActivity.resumeSensor();
						mMainActivity.getDevice().setCurrentMsgType(
								Frame.MSG_TYPE_MULTITOUCHMANIPULATOR);
						mMainActivity.getDevice().startSampling(TouchScreenSensor.getMyName());
					} catch (MultipleSampleThreadException e) {
						e.printStackTrace();
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					mMainActivity.pauseSensor();
					mMainActivity.getDevice().stopSampling();
					break;
				}
				return true;
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.multitouch, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_go_back:
			FragmentTransactionHelper.transTo(mMainActivity, new MasterFragment(),
					"masterFragment", false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
