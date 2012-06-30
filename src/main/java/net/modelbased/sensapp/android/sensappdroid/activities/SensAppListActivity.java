package net.modelbased.sensapp.android.sensappdroid.activities;

import net.modelbased.sensapp.android.sensappdroid.R;
import net.modelbased.sensapp.android.sensappdroid.fragments.MeasureListFragment;
import net.modelbased.sensapp.android.sensappdroid.fragments.MeasureListFragment.OnMesureSelectedListener;
import net.modelbased.sensapp.android.sensappdroid.fragments.SensorListFragment;
import net.modelbased.sensapp.android.sensappdroid.fragments.SensorListFragment.OnSensorSelectedListener;
import net.modelbased.sensapp.android.sensappdroid.restservice.PushDataTest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SensAppListActivity extends Activity implements OnMesureSelectedListener, OnSensorSelectedListener {
	
	public static final String ACTION_UPLOAD = "net.modelbased.sensapp.android.sensappdroid.ACTION_UPLOAD";
	public static final String ACTION_FLUSH_ALL = "net.modelbased.sensapp.android.sensappdroid.ACTION_FLUSH_ALL";
	public static final String ACTION_FLUSH_UPLOADED = "net.modelbased.sensapp.android.sensappdroid.ACTION_FLUSH_UPLOADED";
	
	private static final String TAG = SensAppListActivity.class.getSimpleName();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "__ON_CREATE__");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_view, new SensorListFragment());
		ft.addToBackStack(null);
        ft.commit();
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
	public void onMeasureSelected(Uri uri) {
		Log.e(TAG, "Uri: " + uri.toString());
	}

	@Override
	public void onSensorSelected(Uri uri) {
		Log.e(TAG, "Uri: " + uri.toString());
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_view, new MeasureListFragment());
		ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
	}
}