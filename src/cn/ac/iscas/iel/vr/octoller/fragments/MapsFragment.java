package cn.ac.iscas.iel.vr.octoller.fragments;

import me.voidmain.ui.controls.atily.ATily;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cn.ac.iscas.iel.vr.octoller.MainActivity;
import cn.ac.iscas.iel.vr.octoller.R;
import cn.ac.iscas.iel.vr.octoller.utils.FragmentTransactionHelper;

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
public class MapsFragment extends Fragment {
	protected MainActivity mMainActivity;
	protected ATily mTily;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_maps, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mMainActivity = (MainActivity) this.getActivity();
		View view = this.getView();
		mTily = (ATily) view.findViewById(R.id.tily);
//		mOverlayImageView.setOverlayTouchListener(mMainActivity.getCameraCallback());
//		mOverlayImageView.setOnTouchListener(new View.OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction() & MotionEvent.ACTION_MASK) {
//				case MotionEvent.ACTION_DOWN:
//					try {
//						mMainActivity.resumeSensor();
//						mMainActivity.getDevice().setCurrentMsgType(
//								Frame.MSG_TYPE_MINIMAP);
//						mMainActivity.getDevice().startSampling();
//					} catch (MultipleSampleThreadException e) {
//						e.printStackTrace();
//					}
//					break;
//				case MotionEvent.ACTION_UP:
//				case MotionEvent.ACTION_CANCEL:
//					mMainActivity.pauseSensor();
//					mMainActivity.getDevice().stopSampling();
//					break;
//				}
//				return false;
//			}
//		});
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
