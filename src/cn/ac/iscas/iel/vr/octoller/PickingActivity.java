package cn.ac.iscas.iel.vr.octoller;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.capricorn.ArcMenu;

public class PickingActivity extends Activity {

	protected ArcMenu mActionArcMenu;

	protected static final int[] ITEM_DRAWABLES = {
			R.drawable.picking_change_color, R.drawable.picking_transit,
			R.drawable.picking_rotate, R.drawable.pick_scale };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picking);

		setupViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.picking, menu);
		return true;
	}

	protected void setupViews() {
		mActionArcMenu = (ArcMenu) findViewById(R.id.arc_menu);
		populateArcMenu();
	}

	protected void populateArcMenu() {
		final int itemCount = ITEM_DRAWABLES.length;
		for (int i = 0; i < itemCount; i++) {
			ImageView item = new ImageView(this);
			item.setImageResource(ITEM_DRAWABLES[i]);

			final int position = i;
			mActionArcMenu.addItem(item, new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(PickingActivity.this,
							"position:" + position, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

}
