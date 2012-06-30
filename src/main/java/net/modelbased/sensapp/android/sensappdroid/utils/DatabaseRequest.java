package net.modelbased.sensapp.android.sensappdroid.utils;

import java.util.Hashtable;

import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.models.Sensor;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DatabaseRequest {
	
	private static final Uri S_CONTENT_URI = SensAppCPContract.Sensor.CONTENT_URI;
	private static final String S_NAME = SensAppCPContract.Sensor.NAME;
	private static final String S_URI = SensAppCPContract.Sensor.URI;
	private static final String S_DESCRIPTION = SensAppCPContract.Sensor.DESCRIPTION;
	private static final String S_BACKEND = SensAppCPContract.Sensor.BACKEND;
	private static final String S_TEMPLATE = SensAppCPContract.Sensor.TEMPLATE;
	private static final String S_UNIT = SensAppCPContract.Sensor.UNIT;
	private static final String S_UPLOADED = SensAppCPContract.Sensor.UPLOADED;
	
	private static final Uri M_CONTENT_URI = SensAppCPContract.Measure.CONTENT_URI;
	private static final String M_ID = SensAppCPContract.Measure.ID;
	private static final String M_SENSOR = SensAppCPContract.Measure.SENSOR;
	private static final String M_VALUE = SensAppCPContract.Measure.VALUE;
	private static final String M_TIME = SensAppCPContract.Measure.TIME;
	private static final String M_BASETIME = SensAppCPContract.Measure.BASETIME;
	private static final String M_UPLOADED = SensAppCPContract.Measure.UPLOADED;
	
	private static final String TAG = DatabaseRequest.class.getSimpleName();
	
	public static class MeasureRQ {
		
		public static int deleteMeasure(Context context, int id) {
			int rows = context.getContentResolver().delete(Uri.parse(M_CONTENT_URI + "/" + id), null, null);
			Log.i(TAG, "TABLE_MEASURE: " + rows + " rows deleted");
			return rows;
		}

		public static int deleteMeasures(Context context, String selection) {
			int rows = context.getContentResolver().delete(M_CONTENT_URI, selection, null);
			Log.i(TAG, "TABLE_MEASURE: " + rows + " rows deleted");
			return rows;
		}

		public static int updateMeasures(Context context, String selection, ContentValues values) {
			int rows = context.getContentResolver().update(M_CONTENT_URI, values, selection, null);
			Log.i(TAG, "TABLE_MEASURE: " + rows + " rows updated");
			return rows;
		}

		public static Hashtable<Integer, ContentValues> getMeasuresValues(Context context, String selection) {
			Hashtable<Integer, ContentValues> measures = new Hashtable<Integer, ContentValues>();
			Cursor cursor = context.getContentResolver().query(M_CONTENT_URI, null, selection, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContentValues cv = new ContentValues();
					cv.put(M_ID, cursor.getInt(cursor.getColumnIndexOrThrow(M_ID)));
					cv.put(M_SENSOR, cursor.getString(cursor.getColumnIndexOrThrow(M_SENSOR)));
					cv.put(M_VALUE, cursor.getInt(cursor.getColumnIndexOrThrow(M_VALUE)));
					cv.put(M_TIME, cursor.getLong(cursor.getColumnIndexOrThrow(M_TIME)));
					cv.put(M_BASETIME, cursor.getLong(cursor.getColumnIndexOrThrow(M_BASETIME)));
					cv.put(M_UPLOADED, cursor.getInt(cursor.getColumnIndexOrThrow(M_UPLOADED)));
					measures.put((Integer) cv.get(M_ID), cv);
				}
				cursor.close();
			}
			return measures;
		}
	}
	
	public static class SensorRQ {
		
		public static int deleteSensor(Context context, String name) {
			int rowMeasures = MeasureRQ.deleteMeasures(context, M_SENSOR + " = \"" + name + "\"");
			int rowSensors = context.getContentResolver().delete(Uri.parse(S_CONTENT_URI + "/" + name), null, null);
			Log.i(TAG, "TABLE_SENSOR: " + rowSensors + " rows deleted");
			return rowMeasures + rowSensors;
		}

		public static int updateSensors(Context context, String selection, ContentValues values) {
			int rows = context.getContentResolver().update(S_CONTENT_URI, values, selection, null);
			Log.i(TAG, "TABLE_SENSOR: " + rows + " rows updated");
			return rows;
		}

		public static Hashtable<String, ContentValues> getSensorsValues(Context context, String selection) {
			Hashtable<String, ContentValues> sensors = new Hashtable<String, ContentValues>();
			Cursor cursor = context.getContentResolver().query(S_CONTENT_URI, null, selection, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContentValues cv = new ContentValues();
					cv.put(S_NAME, cursor.getString(cursor.getColumnIndexOrThrow(S_NAME)));
					cv.put(S_URI, cursor.getString(cursor.getColumnIndexOrThrow(S_URI)));
					cv.put(S_DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(S_DESCRIPTION)));
					cv.put(S_BACKEND, cursor.getString(cursor.getColumnIndexOrThrow(S_BACKEND)));
					cv.put(S_TEMPLATE, cursor.getString(cursor.getColumnIndexOrThrow(S_TEMPLATE)));
					cv.put(S_UNIT, cursor.getString(cursor.getColumnIndexOrThrow(S_UNIT)));
					cv.put(S_UPLOADED, cursor.getInt(cursor.getColumnIndexOrThrow(S_UPLOADED)));
					sensors.put((String) cv.get(S_NAME), cv);
				}
				cursor.close();
			}
			return sensors;
		}
		
		public static Sensor getSensor(Context context, String name) {
			String[] projection = {SensAppCPContract.Sensor.URI, SensAppCPContract.Sensor.DESCRIPTION, SensAppCPContract.Sensor.BACKEND, SensAppCPContract.Sensor.TEMPLATE, SensAppCPContract.Sensor.UNIT, SensAppCPContract.Sensor.UPLOADED};
			Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + name), projection, null, null, null);
			Sensor sensor = null;
			if (cursor != null) {
				cursor.moveToFirst();
				Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.URI)));
				String description = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.DESCRIPTION));
				String backend = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.BACKEND));
				String template = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.TEMPLATE));
				String unit = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UNIT));
				int uploaded = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UPLOADED));
				cursor.close();
				sensor = new Sensor(name, uri, description, backend, template, unit, uploaded == 0 ? false : true);
			}
			return sensor;
		}
	}
}
