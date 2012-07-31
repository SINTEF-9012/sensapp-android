package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.fragments.MeasureListFragment.OnMesureSelectedListener;

import net.modelbased.sensapp.android.sensappdroid.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MeasuresActivity extends Activity implements OnMesureSelectedListener {
	
	public static final String ACTION_UPLOAD = "net.modelbased.sensapp.android.sensappdroid.measuresactivity.ACTION_UPLOAD";
	public static final String ACTION_FLUSH_ALL = "net.modelbased.sensapp.android.sensappdroid.measuresactivity.ACTION_FLUSH_ALL";
	public static final String ACTION_FLUSH_UPLOADED = "net.modelbased.sensapp.android.sensappdroid.measuresactivity.ACTION_FLUSH_UPLOADED";
	
	private static final String TAG = MeasuresActivity.class.getSimpleName();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "__ON_CREATE__");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measures);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.measures_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(this, SensAppService.class);
		i.setData(getIntent().getData());
		switch (item.getItemId()) {
//		case R.id.insert_measure:
//			PushDataTest.pushData(this);
//			return true;
		case R.id.upload:
			i.setAction(ACTION_UPLOAD);
			startService(i);
			return true;
		case R.id.delete_all_measures:
			i.setAction(ACTION_FLUSH_ALL);
			startService(i);
			return true;
		case R.id.delete_uploaded_measures:
			i.setAction(ACTION_FLUSH_UPLOADED);
			startService(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}

	@Override
	public void onMeasureSelected(Uri uri) {
		Log.i(TAG, "Selected uri: " + uri.toString());
		Intent i = new Intent(this, MeasureActivity.class);
		i.setData(uri);
		startActivity(i);
	}
}