package org.sensapp.android.sensappdroid.datarequests;

import java.util.ArrayList;
import java.util.Hashtable;

import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.models.Composite;
import org.sensapp.android.sensappdroid.models.Sensor;
import org.sensapp.android.sensappdroid.restrequests.RestRequest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DatabaseRequest {
	
	private static final Uri S_CONTENT_URI = SensAppContract.Sensor.CONTENT_URI;
	private static final String S_NAME = SensAppContract.Sensor.NAME;
	private static final String S_URI = SensAppContract.Sensor.URI;
	private static final String S_DESCRIPTION = SensAppContract.Sensor.DESCRIPTION;
	private static final String S_BACKEND = SensAppContract.Sensor.BACKEND;
	private static final String S_TEMPLATE = SensAppContract.Sensor.TEMPLATE;
	private static final String S_UNIT = SensAppContract.Sensor.UNIT;
	private static final String S_UPLOADED = SensAppContract.Sensor.UPLOADED;
	
	private static final Uri M_CONTENT_URI = SensAppContract.Measure.CONTENT_URI;
	private static final String M_ID = SensAppContract.Measure.ID;
	private static final String M_SENSOR = SensAppContract.Measure.SENSOR;
	private static final String M_VALUE = SensAppContract.Measure.VALUE;
	private static final String M_TIME = SensAppContract.Measure.TIME;
	private static final String M_BASETIME = SensAppContract.Measure.BASETIME;
	private static final String M_UPLOADED = SensAppContract.Measure.UPLOADED;
	
	private static final Uri C_CONTENT_URI = SensAppContract.Compose.CONTENT_URI;
	private static final String C_COMPOSITE = SensAppContract.Compose.COMPOSITE;
	
	private static final Uri CTE_CONTENT_URI = SensAppContract.Composite.CONTENT_URI;
	
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

		public static int updateMeasure(Context context, int id, ContentValues values) {
			int rows = context.getContentResolver().update(Uri.parse(M_CONTENT_URI + "/" + id), values, null, null);
			Log.i(TAG, "TABLE_MEASURE: " + rows + " rows updated");
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

		public static int updateSensor(Context context, String name, ContentValues values) {
			int rows = context.getContentResolver().update(Uri.parse(S_CONTENT_URI + "/" + name), values, null, null);
			Log.i(TAG, "TABLE_SENSOR: " + rows + " rows updated");
			return rows;
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
			String[] projection = {SensAppContract.Sensor.URI, SensAppContract.Sensor.DESCRIPTION, SensAppContract.Sensor.BACKEND, SensAppContract.Sensor.TEMPLATE, org.sensapp.android.sensappdroid.contract.SensAppContract.Sensor.UNIT, SensAppContract.Sensor.UPLOADED};
			Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + name), projection, null, null, null);
			Sensor sensor = null;
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					String uriString = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.URI));
					Uri uri = null;
					if (uriString != null) {
						uri = Uri.parse(uriString);
					}
					String description = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.DESCRIPTION));
					String backend = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.BACKEND));
					String template = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.TEMPLATE));
					String unit = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.UNIT));
					int uploaded = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.UPLOADED));
					sensor = new Sensor(name, uri, description, backend, template, unit, uploaded == 1);
				}
				cursor.close();
			}
			return sensor;
		}
	}
	
	public static class ComposeRQ {
		public static int deleteCompose(Context context, String selection) {
			int rows = context.getContentResolver().delete(C_CONTENT_URI, selection, null);
			Log.i(TAG, "TABLE_COMPOSE: " + rows + " rows deleted");
			return rows;
		}
	}
	
	public static class CompositeRQ {

		public static Composite getComposite(Context context, String name) {
			String description = null;
			Uri uri = null;
			String[] projection = {SensAppContract.Composite.DESCRIPTION, SensAppContract.Composite.URI};
			// Get composite description and uri
			Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppContract.Composite.CONTENT_URI + "/" + name), projection, null, null, null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					description = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Composite.DESCRIPTION));
					uri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Composite.URI)));
				}
				cursor.close();
			}
			// Get composite sensor names
			ArrayList<String> sensors = new ArrayList<String>();
			cursor = context.getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite/" + name), new String[]{SensAppContract.Sensor.NAME}, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					sensors.add(cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME)));
				}
				cursor.close();
			}
			// Get composite sensor addresses
			ArrayList<Uri> sensorUris = new ArrayList<Uri>();
			projection = new String[]{SensAppContract.Sensor.NAME, SensAppContract.Sensor.URI};
			String selection = SensAppContract.Sensor.NAME + " IN " + sensors.toString().replace("[", "(\"").replace(", ", "\", \"").replace("]", "\")");
			cursor = context.getContentResolver().query(SensAppContract.Sensor.CONTENT_URI, projection, selection, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					String sensorUri = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.URI));
					sensorUri += RestRequest.SENSOR_PATH + "/" + cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
					sensorUris.add(Uri.parse(sensorUri));
				}
				cursor.close();
			}
			return new Composite(name, description, uri, sensorUris);
		}
		
		public static int deleteComposite(Context context, String name) {
			int rowCompose = ComposeRQ.deleteCompose(context, C_COMPOSITE + " = \"" + name + "\"");
			int rowComposite = context.getContentResolver().delete(Uri.parse(CTE_CONTENT_URI + "/" + name), null, null);
			Log.i(TAG, "TABLE_COMPOSITE: " + rowComposite + " rows deleted");
			return rowCompose + rowComposite;
		}
	}
}
