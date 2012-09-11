package org.sensapp.android.sensappdroid.api;

import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SensAppHelper {
	
	public static Uri insertMeasure(Context context, String sensor, int value) throws IllegalArgumentException {
		return insertMeasure(context, sensor, String.valueOf(value));
	}
	 
	public static Uri insertMeasure(Context context, String sensor, float value) throws IllegalArgumentException {
		return insertMeasure(context, sensor, String.valueOf(value));
	}
	
	public static Uri insertMeasure(Context context, String sensor, String value) throws IllegalArgumentException {
		if (context == null) {
			throw new IllegalArgumentException("The context is null");
		} 
		if (sensor == null) {
			throw new IllegalArgumentException("The sensor is null");
		} else if (!isSensorRegistered(context, sensor)) {
			throw new IllegalArgumentException(sensor + " is not maintained");
		}
		ContentValues values = new ContentValues();
		values.put(SensAppContract.Measure.SENSOR, sensor);
		values.put(SensAppContract.Measure.VALUE, value);
		values.put(SensAppContract.Measure.BASETIME, 0);
		values.put(SensAppContract.Measure.TIME, System.currentTimeMillis());
		return context.getContentResolver().insert(SensAppContract.Measure.CONTENT_URI, values);
	}
	
	public static Uri registerNumericalSensor(Context context, String name, String description, SensAppUnit unit) throws IllegalArgumentException {
		return registerSensor(context, name, description, unit, SensAppTemplate.numerical);
	}
	
	public static Uri registerStringSensor(Context context, String name, String description, SensAppUnit unit) throws IllegalArgumentException {
		return registerSensor(context, name, description, unit, SensAppTemplate.string);
	}
	
	private static Uri registerSensor(Context context, String name, String description, SensAppUnit unit, SensAppTemplate template) throws IllegalArgumentException {
		if (context == null) {
			throw new IllegalArgumentException("The context is null");
		} 
		if (name == null) {
			throw new IllegalArgumentException("The sensor name is null");
		} else if (isSensorRegistered(context, name)) {
			return null;
		}
		if (unit == null) {
			throw new IllegalArgumentException("The sensor unit is null");
		}
		ContentValues values = new ContentValues();
		values.put(SensAppContract.Sensor.NAME, name);
		if (description == null) {
			values.put(SensAppContract.Sensor.DESCRIPTION, "");
		}
		values.put(SensAppContract.Sensor.UNIT, unit.getIANAUnit());
		values.put(SensAppContract.Sensor.BACKEND, SensAppBackend.raw.getBackend());
		values.put(SensAppContract.Sensor.TEMPLATE, template.getTemplate());
		return context.getContentResolver().insert(SensAppContract.Sensor.CONTENT_URI, values);
	}
	
	private static boolean isSensorRegistered(Context context, String name) {
		Cursor c = context.getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + name), null, null, null, null);
		boolean isRegistered = c.getCount() > 0;
		c.close();
		return isRegistered;
	}
	
	private static enum SensAppBackend {
		raw("raw");
		private String backend;
		private SensAppBackend(String backend) {
			this.backend = backend;
		}
		public String getBackend() {
			return backend;
		}
	}

	private static enum SensAppTemplate {
		string("String"), 
		numerical("Numerical");
		private String template;
		private SensAppTemplate(String template) {
			this.template = template;
		}
		public String getTemplate() {
			return template;
		}
	}
}