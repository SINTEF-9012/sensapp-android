package net.modelbased.sensapp.android.sensappdroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SensAppService extends Service {

	static final private String TAG = SensAppService.class.getName();
	
	@Override
	public void onCreate() {
		Log.d(TAG, "__ON_CREATE__");
		super.onCreate();
		RESTExchangeThread t = new RESTExchangeThread();
		t.start();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "__ON_START__");
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}
	
}
