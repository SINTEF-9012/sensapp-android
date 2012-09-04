package org.sensapp.android.sensappdroid.activities;

import java.util.Timer;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.connectivity.Connectivity;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.DeleteMeasuresTask;
import org.sensapp.android.sensappdroid.preferences.PreferencesActivity;
import org.sensapp.android.sensappdroid.restrequests.PutMeasuresTask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
	
	private static final String TAG = SensAppService.class.getSimpleName();
	private static final int NOTIFICATION_AUTOUPLOAD_ID = 6987;
	
	private SharedPreferences preferences;
	private Timer timer;
	private Notification notificationAutoUpload;
	
	@Override
	public void onCreate() {
		super.onCreate();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(spChanged);
		setupNotification();
		if (preferences.getBoolean(getString(R.string.pref_auto_upload_key), false)) {
			startAutoUpload(Integer.valueOf(preferences.getString(getString(R.string.pref_autoupload_delay_key), "30")));
		}
	}
	
	private void setupNotification() {
		notificationAutoUpload = new Notification(R.drawable.ic_launcher, "Auto upload active", System.currentTimeMillis());
		notificationAutoUpload.flags |= Notification.FLAG_ONGOING_EVENT; 
		Intent notificationIntent = new Intent(this, PreferencesActivity.class);
		PendingIntent pNotificationIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		notificationAutoUpload.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), "Auto upload", pNotificationIntent);
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
		if (!Connectivity.isDataAvailable(getApplicationContext())) {
			Log.e(TAG, "No data connection available");
			Toast.makeText(this, "No data connection available", Toast.LENGTH_LONG).show();
		} else {
			new PutMeasuresTask(this, uri).execute();
		}
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
					startAutoUpload(Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_autoupload_delay_key), "30")));
				} else {
					stopAutoUpload();
				}
			} else if (getString(R.string.pref_autoupload_delay_key).equals(key)) {
				if (sharedPreferences.getBoolean(getString(R.string.pref_auto_upload_key), false)) {
					stopAutoUpload();
					startAutoUpload(Integer.valueOf(sharedPreferences.getString(key, "30")));
				}
			}
		}
	};

	private void startAutoUpload(int delay) {
		timer = new AutoUploadTimer(getApplicationContext(), delay);
		Log.i(TAG, "Auto upload timer started");
		startForeground(NOTIFICATION_AUTOUPLOAD_ID, notificationAutoUpload);
		Toast.makeText(getApplicationContext(), "Auto upload scheduled every " + delay + " seconds", Toast.LENGTH_SHORT).show();
	}
	
	private void stopAutoUpload() {
		if (timer != null) {
			timer.cancel();
		}
		Log.i(TAG, "Auto upload timer canceled");
		stopForeground(true);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		preferences.unregisterOnSharedPreferenceChangeListener(spChanged);
	}
}
