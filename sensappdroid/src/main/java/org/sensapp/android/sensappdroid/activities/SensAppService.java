package org.sensapp.android.sensappdroid.activities;

import java.util.Set;

import org.sensapp.android.sensappdroid.connectivity.Connectivity;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.datarequests.DeleteMeasuresTask;
import org.sensapp.android.sensappdroid.preferences.AutoUploadSensorDialog;
import org.sensapp.android.sensappdroid.restrequests.PutMeasuresTask;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SensAppService extends Service {
	
	public static final String ACTION_UPLOAD = SensAppService.class.getName() + ".ACTION_UPLOAD";
	public static final String ACTION_AUTO_UPLOAD = SensAppService.class.getName() + ".ACTION_AUTO_UPLOAD";
	public static final String ACTION_DELETE_LOCAL = SensAppService.class.getName() + ".ACTION_DELETE_LOCAL";
	public static final String EXTRA_UPLOADED_FILTER = SensAppService.class.getName() + ".EXTRA_UPLOADED_FILTER";
	
	private static final String TAG = SensAppService.class.getSimpleName();
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction() != null) {
			if (intent.getAction().equals(ACTION_UPLOAD)) {
				Log.d(TAG, "Receive: ACTION_UPLOAD");
				uploadMeasureUri(intent.getData());
			} else if (intent.getAction().equals(ACTION_AUTO_UPLOAD)) {
				Log.d(TAG, "Receive: ACTION_AUTO_UPLOAD");
				Set<String> names = PreferenceManager.getDefaultSharedPreferences(this).getStringSet(AutoUploadSensorDialog.SENSOR_MAINTAINED, null);
				if (names != null && !names.isEmpty()) {
					for (final String name : names) {
						Log.d(TAG, "Put " + name + " sensor (Auto upload)");
						new PutMeasuresTask(getApplicationContext(), Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + name)).execute();
					}
				}
			} else if (intent.getAction().equals(ACTION_DELETE_LOCAL)) {
				Log.d(TAG, "Receive: ACTION_DELETE_LOCAL");
				Bundle extra = intent.getExtras();
				if (extra == null) {
					new DeleteMeasuresTask(this, intent.getData()).execute();
				} else if (extra.getBoolean(EXTRA_UPLOADED_FILTER)) {
					new DeleteMeasuresTask(this, intent.getData()).execute(SensAppContract.Measure.UPLOADED + " = 1");
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
}
