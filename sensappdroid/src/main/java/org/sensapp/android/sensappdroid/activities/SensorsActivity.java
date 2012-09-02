package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.fragments.SensorListFragment.OnSensorSelectedListener;
import org.sensapp.android.sensappdroid.preferences.PreferencesActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SensorsActivity extends Activity implements OnSensorSelectedListener {
	
	private static final String TAG = SensorsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "__ON_CREATE__");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sensors_menu, menu);
		if (getIntent().getData() == null) {
			menu.removeItem(R.id.sensors);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.upload_all:
			i = new Intent(this, SensAppService.class);
			i.setAction(SensAppService.ACTION_UPLOAD);
			i.setData(SensAppCPContract.Measure.CONTENT_URI);
			startService(i);
			return true;
		case R.id.sensors:
			startActivity(new Intent(this, SensorsActivity.class));
			return true;
		case R.id.measures:
			i = new Intent(this, MeasuresActivity.class);
			i.setData(SensAppCPContract.Measure.CONTENT_URI);
			startActivity(i);
			return true;
		case R.id.preferences:
			startActivity(new Intent(this, PreferencesActivity.class));
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
	public void onSensorSelected(Uri uri) {
		Intent i = new Intent(this, MeasuresActivity.class);
		i.setData(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + uri.getLastPathSegment()));
		startActivity(i);
	}
}