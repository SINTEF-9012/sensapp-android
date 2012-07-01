package net.modelbased.sensapp.android.sensappdroid.activities;

import net.modelbased.sensapp.android.sensappdroid.R;
import net.modelbased.sensapp.android.sensappdroid.fragments.SensorListFragment.OnSensorSelectedListener;
import net.modelbased.sensapp.android.sensappdroid.restservice.PushDataTest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SensorsActivity extends Activity implements OnSensorSelectedListener {
	
	public static final String ACTION_UPLOAD = "net.modelbased.sensapp.android.sensappdroid.ACTION_UPLOAD";
	public static final String ACTION_FLUSH_ALL = "net.modelbased.sensapp.android.sensappdroid.ACTION_FLUSH_ALL";
	public static final String ACTION_FLUSH_UPLOADED = "net.modelbased.sensapp.android.sensappdroid.ACTION_FLUSH_UPLOADED";
	
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
		inflater.inflate(R.menu.list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(this, SensAppService.class);
		switch (item.getItemId()) {
		case R.id.insert:
			PushDataTest.pushData(this);
			return true;
		case R.id.upload:
			i.setAction(ACTION_UPLOAD);
			startService(i);
			return true;
		case R.id.stop_service:
			stopService(i);
			return true;
		case R.id.flush_database:
			i.setAction(ACTION_FLUSH_ALL);
			startService(i);
			return true;
		case R.id.flush_uploaded:
			i.setAction(ACTION_FLUSH_UPLOADED);
			startService(i);
			return true;
		case R.id.view:
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
		Log.d(TAG, "Uri: " + uri.toString());
		startActivity(new Intent(this, MeasuresActivity.class));
	}
}