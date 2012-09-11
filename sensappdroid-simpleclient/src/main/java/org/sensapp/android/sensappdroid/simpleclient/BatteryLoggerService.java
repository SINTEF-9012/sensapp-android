package org.sensapp.android.sensappdroid.simpleclient;

import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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
		// Check if the sensor is already registered.
		Cursor c = getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + sensorName), null, null, null, null);
		boolean isRegistered = c.getCount() > 0;
		c.close();
		if (isRegistered) {
			// The sensor is already registered.
			Log.w(TAG, sensorName + " is already registered");
		} else {
			// The sensor is not known, register it.
			ContentValues values = new ContentValues();
			values.put(SensAppContract.Sensor.NAME, sensorName);
			values.put(SensAppContract.Sensor.UNIT, "%");
			values.put(SensAppContract.Sensor.BACKEND, "raw");
			values.put(SensAppContract.Sensor.TEMPLATE, "numerical");
			values.put(SensAppContract.Sensor.UPLOADED, 0);
			getContentResolver().insert(SensAppContract.Sensor.CONTENT_URI, values);
		}
	}
	
	private void registerMeasure(int value) {
		ContentValues values = new ContentValues();
		values.put(SensAppContract.Measure.SENSOR, sensorName);
		values.put(SensAppContract.Measure.VALUE, String.valueOf(value));
		values.put(SensAppContract.Measure.BASETIME, 0);
		values.put(SensAppContract.Measure.TIME, System.currentTimeMillis());
		values.put(SensAppContract.Measure.UPLOADED, 0);
		getContentResolver().insert(SensAppContract.Measure.CONTENT_URI, values);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
