package cn.ac.iscas.iel.vr.octoller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	
	protected ImageButton mBtnLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void setupViews() {
		mBtnLock = (ImageButton) findViewById(R.id.btn_lock_view);
		mBtnLock.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent gotoPickIntent = new Intent(MainActivity.this, PickingActivity.class);
				MainActivity.this.startActivity(gotoPickIntent);
			}
		});
	}

}
