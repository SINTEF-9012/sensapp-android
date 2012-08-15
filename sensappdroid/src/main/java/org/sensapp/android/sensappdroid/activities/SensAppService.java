package org.sensapp.android.sensappdroid.activities;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.DeleteMeasuresTask;
import org.sensapp.android.sensappdroid.restrequests.PutMeasuresTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SensAppService extends Service {
	
	public static final String ACTION_UPLOAD = SensAppService.class.getName() + ".ACTION_UPLOAD";
	public static final String ACTION_DELETE_LOCAL = SensAppService.class.getName() + ".ACTION_DELETE_LOCAL";
	public static final String EXTRA_UPLOADED_FILTER = SensAppService.class.getName() + ".EXTRA_UPLOADED_FILTER";
	
	static final private String TAG = SensAppService.class.getSimpleName();
	
	private SharedPreferences preferences;
	private Timer timer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(spChanged);
		if (preferences.getBoolean(getString(R.string.pref_auto_upload_key), false)) {
			startAutoUpload();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction() != null) {
			if (intent.getAction().equals(ACTION_UPLOAD)) {
				Log.d(TAG, "Receive: ACTION_UPLOAD");
				uploadMeasureUri(intent.getData());
			} else if (intent.getAction().equals(ACTION_DELETE_LOCAL)) {
				Log.d(TAG, "Receive: ACTION_DELETE_LOCAL");
				Bundle extra = intent.getExtras();
				if (extra == null) {
					new DeleteMeasuresTask(this, intent.getData()).execute();
				} else if (extra.getBoolean(EXTRA_UPLOADED_FILTER)) {
					new DeleteMeasuresTask(this, intent.getData()).execute(SensAppCPContract.Measure.UPLOADED + " = 1");
				}
			}
		}
		return START_NOT_STICKY;
	}
	
	private void uploadMeasureUri(Uri uri) {
		if (!isDataConnectionAvailable()) {
			Log.e(TAG, "No data connection available");
			Toast.makeText(this, "No data connection available", Toast.LENGTH_LONG).show();
		} else {
			new PutMeasuresTask(this, uri).execute();
		}
	}
	
	private boolean isDataConnectionAvailable() {
		ConnectivityManager connec = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    if (wifi.isConnected()) {
	        return true;
	    } else if (mobile.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (getString(R.string.pref_auto_upload_key).equals(key)) {
				if (sharedPreferences.getBoolean(key, false)) {
					startAutoUpload();
				} else {
					stopAutoUpload();
				}
			} else if (getString(R.string.pref_autoupload_delay_key).equals(key)) {
				if (sharedPreferences.getBoolean(getString(R.string.pref_auto_upload_key), false)) {
					stopAutoUpload();
					startAutoUpload();
				}
			}
		}
	};

	private void startAutoUpload() {
		Log.e(TAG, "Timer sceduled");
		timer = new Timer();
		int delay = Integer.valueOf(preferences.getString(getString(R.string.pref_autoupload_delay_key), "30")) * 1000;
		timer.scheduleAtFixedRate(new UploadTimerTask(), 0, delay);
	}
	
	private void stopAutoUpload() {
		Log.e(TAG, "Timer canceled");
		timer.cancel();
	}
	
	private class UploadTimerTask extends TimerTask {
		@Override
        public void run() {
			Log.e(TAG, "Upload...");
			Set<String> names = preferences.getStringSet(getString(R.string.pref_list_autoupload_sensor_key), null);
			if (names != null && !names.isEmpty()) {
				for (String name : names) {
					Log.e(TAG, name);
					uploadMeasureUri(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + name));
				}
			}
        }
    }  
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		preferences.unregisterOnSharedPreferenceChangeListener(spChanged);
	}
}
