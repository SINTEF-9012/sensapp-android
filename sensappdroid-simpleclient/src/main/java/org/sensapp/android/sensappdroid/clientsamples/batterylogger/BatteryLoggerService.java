package org.sensapp.android.sensappdroid.clientsamples.batterylogger;

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
	private static final String sensorName = android.os.Build.MODEL + "_battery";
	
	@Override
	public void onCreate() {
		super.onCreate();
		registerSensor();
		Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);	
		Log.d(TAG, "Battery level: " + level);
		insertMeasure(level);
		stopSelf();
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
	
	private void insertMeasure(int value) {
		Uri measureUri = SensAppHelper.insertMeasure(getApplicationContext(), sensorName, value);
		Log.i(TAG, "New measure (" + value + ") available at " + measureUri);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
