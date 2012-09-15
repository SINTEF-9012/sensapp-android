package org.sensapp.android.sensappdroid.api;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class InsertionManager {

	private final static int LOWEST_INTERVAL = 5000;
	
	private static Hashtable<String, ArrayList<ContentValues>> buffers = new Hashtable<String, ArrayList<ContentValues>>();
	
	private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	
	public static void storeMeasure(final Context context, ContentValues values) {
		final String sensor = (String) values.get(SensAppContract.Measure.SENSOR);
		Log.w("DEBUG", "__Store__");
		synchronized(buffers) {
			if (!buffers.containsKey(sensor)) { 
				buffers.put(sensor, new ArrayList<ContentValues>());
				worker.schedule(new Runnable() {
					public void run() {
						flushSensorMeasures(context, sensor);
					}
				}, LOWEST_INTERVAL, TimeUnit.MILLISECONDS);
			}
			buffers.get(sensor).add(values);
		}
	}
	
	private static void flushSensorMeasures(Context context, String name) {
		synchronized(buffers) {
			for (ContentValues values : buffers.get(name)) {
				context.getContentResolver().insert(SensAppContract.Measure.CONTENT_URI, values);
			}
		Log.e("DEBUG", "__Flush__");
		buffers.remove(name);
		}
	}
}
