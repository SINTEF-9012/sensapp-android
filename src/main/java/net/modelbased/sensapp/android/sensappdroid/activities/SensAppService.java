package net.modelbased.sensapp.android.sensappdroid.activities;

import net.modelbased.sensapp.android.sensappdroid.R;
import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.restservice.PostSensorTask;
import net.modelbased.sensapp.android.sensappdroid.restservice.PushDataTest;
import net.modelbased.sensapp.android.sensappdroid.restservice.PutMeasuresTask;
import net.modelbased.sensapp.android.sensappdroid.restservice.RequestTask;
import net.modelbased.sensapp.android.sensappdroid.utils.DeleteMeasuresTask;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SensAppService extends Service {

	// "46.51.169.123" 80
	
	static final private String TAG = SensAppService.class.getSimpleName();
	
	@Override
	public void onCreate() {
		Log.d(TAG, "__ON_CREATE__");
		super.onCreate();
		PushDataTest.pushSensor(this);
		//new PostSensorTask(this).execute("sa_test_unregistred_sensor8080");
		Toast.makeText(getApplicationContext(), R.string.toast_service_started, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction().equals(SensorsActivity.ACTION_UPLOAD)) {
			Log.d(TAG, "Receive: ACTION_UPLOAD");
			new PutMeasuresTask(this).execute("sa_test_unregistred_sensor8080");
		} else if (intent.getAction().equals(SensorsActivity.ACTION_FLUSH_ALL)) {
			Log.d(TAG, "Receive: ACTION_FLUSH_ALL");
			new DeleteMeasuresTask(this, null).execute();
		} else if (intent.getAction().equals(SensorsActivity.ACTION_FLUSH_UPLOADED)) {
			Log.d(TAG, "Receive: ACTION_FLUSH_UPLOADED");
			new DeleteMeasuresTask(this, SensAppCPContract.Measure.UPLOADED + " = 1").execute();
		} else if (intent.getAction().equals(RequestTask.ACTION_REQUEST_SUCCEED)) {
			Log.d(TAG, "Receive: ACTION_REQUEST_SUCCEED");
			int mode = intent.getExtras().getInt(RequestTask.EXTRA_REQUEST_CODE);
			if (mode == RequestTask.CODE_POST_SENSOR) {
				Log.i(TAG, "Post sensor succed");
				Toast.makeText(getApplicationContext(), "Sensor registred", Toast.LENGTH_LONG).show();
			} else if (mode == RequestTask.CODE_PUT_MEASURE) {
				Log.i(TAG, "Put data succed");
				Toast.makeText(getApplicationContext(), "Upload succeed", Toast.LENGTH_LONG).show();
			}
		} else if (intent.getAction().equals(RequestTask.ACTION_REQUEST_FAILURE)) {
			Log.d(TAG, "Receive: ACTION_REQUEST_FAILURE");
			int mode = intent.getExtras().getInt(RequestTask.EXTRA_REQUEST_CODE);
			if (mode == RequestTask.CODE_POST_SENSOR) {
				Log.e(TAG, "Post sensor error");
				Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();
			} else if (mode == RequestTask.CODE_PUT_MEASURE) {
				Log.e(TAG, "Put data error");
				Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();
			}
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
