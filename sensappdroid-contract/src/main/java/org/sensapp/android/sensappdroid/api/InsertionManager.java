package org.sensapp.android.sensappdroid.api;

import java.util.ArrayList;
import java.util.Hashtable;

import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;

public class InsertionManager {

	private final static int LOWEST_INTERVAL = 5000;
	
	private static Hashtable<String, ArrayList<ContentValues>> buffers = new Hashtable<String, ArrayList<ContentValues>>();
	
	public static void storeMeasure(final Context context, ContentValues values) {
		final String sensor = (String) values.get(SensAppContract.Measure.SENSOR);
		if (!buffers.containsKey(sensor)) { 
			buffers.put(sensor, new ArrayList<ContentValues>());
			new Handler().postDelayed(new Runnable() {
				public void run() {
					flushSensorMeasures(context, sensor);
				}
			}, LOWEST_INTERVAL);
		}
		buffers.get(sensor).add(values);
	}
	
	private static void flushSensorMeasures(Context context, String name) {
		for (ContentValues values : buffers.get(name)) {
			context.getContentResolver().insert(SensAppContract.Measure.CONTENT_URI, values);
		}
		buffers.remove(name);
	}
}
