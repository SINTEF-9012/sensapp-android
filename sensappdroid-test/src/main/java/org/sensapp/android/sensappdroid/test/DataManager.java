package org.sensapp.android.sensappdroid.test;

import java.util.Random;

import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.datarequests.DatabaseRequest;
import org.sensapp.android.sensappdroid.models.Sensor;
import org.sensapp.android.sensappdroid.restrequests.RequestErrorException;
import org.sensapp.android.sensappdroid.restrequests.RestRequest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DataManager {

	protected static void insertMeasures(ContentResolver resolver, int nbMeasure, int nbSensors) {
		ContentValues values;
		for (int count = 0 ; count < nbMeasure ; count ++) {
			values = new ContentValues();
			values.put(SensAppContract.Measure.SENSOR, "testSensor" + String.valueOf(new Random().nextInt(nbSensors)));
			values.put(SensAppContract.Measure.BASETIME, new Random().nextLong());
			values.put(SensAppContract.Measure.TIME, new Random().nextLong());
			values.put(SensAppContract.Measure.VALUE, new Random().nextInt());
			values.put(SensAppContract.Measure.UPLOADED, 0);
			resolver.insert(SensAppContract.Measure.CONTENT_URI, values);
		}
	}
	
	protected static void insertSensors(ContentResolver resolver, int nbSensors) {
		ContentValues values;
		for (int count = 0 ; count < nbSensors ; count ++) {
			values = new ContentValues();
			values.put(SensAppContract.Sensor.NAME, "testSensor" + String.valueOf(count));
			values.put(SensAppContract.Sensor.DESCRIPTION, "Test description " + String.valueOf(count));
			values.put(SensAppContract.Sensor.BACKEND, "raw");
			values.put(SensAppContract.Sensor.TEMPLATE, "Numerical");
			values.put(SensAppContract.Sensor.URI, "http://sensapp.fleurey.com:80");
			values.put(SensAppContract.Sensor.UNIT, "count");
			values.put(SensAppContract.Sensor.UPLOADED, 0);
			resolver.insert(SensAppContract.Sensor.CONTENT_URI, values);
		}
	}
	
	protected static void insertComposite(ContentResolver resolver, int nbComposite) {
		ContentValues values = new ContentValues();
		for (int count = 0 ; count < nbComposite ; count ++) {
			values.put(SensAppContract.Composite.NAME, "testComposite" + count);
			values.put(SensAppContract.Composite.DESCRIPTION, "Test composite description " + count);
			resolver.insert(SensAppContract.Composite.CONTENT_URI, values);
			values.clear();
		}
	}
	
	protected static void insertCompose(ContentResolver resolver, int nbCompose, int nbComposite, int nbSensor) {
		ContentValues values;
		for (int count = 0 ; count < nbCompose ; count ++) {
			values = new ContentValues();
			values.put(SensAppContract.Compose.COMPOSITE, "testComposite" + new Random().nextInt(nbComposite));
			values.put(SensAppContract.Compose.SENSOR, "testSensor" + new Random().nextInt(nbSensor));
			resolver.insert(SensAppContract.Compose.CONTENT_URI, values);
		}
	}
	
	protected static void cleanMeasure(Context context) {
		Cursor cursor = context.getContentResolver().query(SensAppContract.Measure.CONTENT_URI, new String[]{"DISTINCT " + SensAppContract.Measure.SENSOR}, null, null, null);
		if (cursor != null) {
			String name = null;
			while (cursor.moveToNext()) {
				name = cursor.getString(cursor.getColumnIndex(SensAppContract.Measure.SENSOR));
				if (name.startsWith("test")) {
					context.getContentResolver().delete(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + name), null, null);
				}
			}
			cursor.close();
		}
	}
	
	protected static void cleanSensors(Context context) throws IllegalArgumentException, RequestErrorException {
		Cursor cursor = context.getContentResolver().query(SensAppContract.Sensor.CONTENT_URI, new String[]{SensAppContract.Sensor.NAME, SensAppContract.Sensor.URI, SensAppContract.Sensor.UPLOADED}, null, null, null);
		if (cursor != null) {
			String name = null;
			while (cursor.moveToNext()) {
				name = cursor.getString(cursor.getColumnIndex(SensAppContract.Sensor.NAME));
				if (name.startsWith("test")) {
					if (cursor.getInt(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.UPLOADED)) == 1) {
						Sensor sensor = DatabaseRequest.SensorRQ.getSensor(context, name);
						RestRequest.deleteSensor(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.URI))), sensor);
					}
					context.getContentResolver().delete(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + name), null, null);
				}
			}
			cursor.close();
		}
	}
	
	protected static void cleanComposite(Context context) {
		Cursor cursor = context.getContentResolver().query(SensAppContract.Composite.CONTENT_URI, new String[]{SensAppContract.Composite.NAME}, null, null, null);
		if (cursor != null) {
			String name = null;
			while (cursor.moveToNext()) {
				name = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Composite.NAME));
				if (name.startsWith("test")) {
					context.getContentResolver().delete(Uri.parse(SensAppContract.Composite.CONTENT_URI + "/" + name), null, null);
				}
			}
			cursor.close();
		}
	}
	
	protected static void cleanCompose(Context context) {
		Cursor cursor = context.getContentResolver().query(SensAppContract.Compose.CONTENT_URI, new String[]{SensAppContract.Compose.ID, SensAppContract.Compose.COMPOSITE}, null, null, null);
		if (cursor != null) {
			String name = null;
			int id = 0;
			while (cursor.moveToNext()) {
				name = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Compose.COMPOSITE));
				id = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppContract.Compose.ID));
				if (name.startsWith("test")) {
					context.getContentResolver().delete(Uri.parse(SensAppContract.Compose.CONTENT_URI + "/" + String.valueOf(id)), null, null);
				}
			}
			cursor.close();
		}
	}
	
	protected static void cleanAll(Context context) throws IllegalArgumentException, RequestErrorException {
		cleanMeasure(context);
		cleanSensors(context);
		cleanComposite(context);
		cleanCompose(context);
	}
}
