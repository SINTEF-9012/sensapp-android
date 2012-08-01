package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.DeleteMeasuresTask;
import org.sensapp.android.sensappdroid.restservice.PutMeasuresTask;

import org.sensapp.android.sensappdroid.R;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SensAppService extends Service {
	
	static final private String TAG = SensAppService.class.getSimpleName();
	
	@Override
	public void onCreate() {
		Log.d(TAG, "__ON_CREATE__");
		super.onCreate();
		Toast.makeText(getApplicationContext(), R.string.toast_service_started, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction().equals(MeasuresActivity.ACTION_UPLOAD)) {
			Log.d(TAG, "Receive: ACTION_UPLOAD");
			new PutMeasuresTask(this, intent.getData()).execute();
		} else if (intent.getAction().equals(MeasuresActivity.ACTION_FLUSH_ALL)) {
			Log.d(TAG, "Receive: ACTION_FLUSH_ALL");
			new DeleteMeasuresTask(this, intent.getData()).execute();
		} else if (intent.getAction().equals(MeasuresActivity.ACTION_FLUSH_UPLOADED)) {
			Log.d(TAG, "Receive: ACTION_FLUSH_UPLOADED");
			new DeleteMeasuresTask(this, intent.getData()).execute(SensAppCPContract.Measure.UPLOADED + " = 1");
		}
		return START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		Toast.makeText(getApplicationContext(), R.string.toast_service_stopped, Toast.LENGTH_LONG).show();
		super.onDestroy();
	}
}
