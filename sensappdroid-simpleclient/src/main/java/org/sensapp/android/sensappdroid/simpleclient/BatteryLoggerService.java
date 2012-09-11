package org.sensapp.android.sensappdroid.simpleclient;

import org.sensapp.android.sensappdroid.api.SensAppHelper;
import org.sensapp.android.sensappdroid.api.SensAppUnit;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class BatteryLoggerService extends Service {

	private static final String TAG = BatteryLoggerService.class.getSimpleName();
	private static final String sensorName = "Battery";
	
	@Override
	public void onCreate() {
		Log.d(TAG, "__ON_CREATE__");
		super.onCreate();
		registerSensor();
		Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);	
		Log.d(TAG, "Battery level: " + level);
		registerMeasure(level);
		stopSelf();
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}
	
	private void registerSensor() {
		Uri sensorUri = SensAppHelper.registerNumericalSensor(getApplicationContext(), sensorName, null, SensAppUnit.percent);
		if (sensorUri == null) {
			// The sensor is already registered.
			Log.w(TAG, sensorName + " is already registered");
		} else {
			// The sensor is newly inserted.
			Log.i(TAG, sensorName + " available at " + sensorUri);
		}
	}
	
	private void registerMeasure(int value) {
		Uri measureUri = SensAppHelper.insertMeasure(getApplicationContext(), sensorName, value);
		Log.i(TAG, "New measure (" + value + "%) available at " + measureUri);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
