package org.sensapp.android.sensappdroid.test;

import java.util.Random;

import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.contentprovider.SensAppContentProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

public class ContentProviderTest extends ProviderTestCase2<SensAppContentProvider> {
	
	public ContentProviderTest() {
		super(SensAppContentProvider.class, SensAppCPContract.AUTHORITY);
	}
	
	/////***** Uri tests *****//////
	
	public void testMeasuresUri() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppCPContract.Measure.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testMeasureIdUri() {
		int id = new Random().nextInt();
		ContentResolver resolver = getMockContentResolver();
		Uri uri = Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + String.valueOf(id));
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testMeasureSensorNameUri() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/sensorName");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testSensorsUri() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppCPContract.Sensor.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testSensorIdUri() {
		int id = new Random().nextInt();
		ContentResolver resolver = getMockContentResolver();
		Uri uri = Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + String.valueOf(id));
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testSensorNameUri() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/sensorName");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testBadUri() {
		Uri uri = Uri.parse("badUri");
		try {
			Cursor cursor = getProvider().query(uri, null, null, null, null);
			assertNotNull(cursor);
			cursor.close();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	//////***** ----- *****//////
	
	/////***** Insert tests *****//////
	
	private final int sensorsInsertNb = 2;
	private final int measuresInsertNb = 10;
	
	public void testInsertMeasures() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppCPContract.Measure.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(0, cursor.getCount());
		cursor.close();
		insertMeasures(resolver, measuresInsertNb, sensorsInsertNb);
		cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(measuresInsertNb, cursor.getCount());
		cursor.close();
	}
	
	public void testInsertSensors() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppCPContract.Sensor.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(0, cursor.getCount());
		cursor.close();
		insertSensors(resolver, sensorsInsertNb);
		cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(sensorsInsertNb, cursor.getCount());
		cursor.close();
	}
	
	private void insertMeasures(ContentResolver resolver, int nbMeasure, int nbSensors) {
		ContentValues values;
		for (int count = 0 ; count < nbMeasure ; count ++) {
			values = new ContentValues();
			values.put(SensAppCPContract.Measure.SENSOR, "testSensor" + String.valueOf(new Random().nextInt(nbSensors)));
			values.put(SensAppCPContract.Measure.BASETIME, new Random().nextLong());
			values.put(SensAppCPContract.Measure.TIME, new Random().nextLong());
			values.put(SensAppCPContract.Measure.VALUE, new Random().nextInt());
			values.put(SensAppCPContract.Measure.UPLOADED, new Random().nextInt(2));
			resolver.insert(SensAppCPContract.Measure.CONTENT_URI, values);
		}
	}
	
	private void insertSensors(ContentResolver resolver, int nbSensors) {
		ContentValues values;
		for (int count = 0 ; count < nbSensors ; count ++) {
			values = new ContentValues();
			values.put(SensAppCPContract.Sensor.NAME, "testSensor" + String.valueOf(count));
			values.put(SensAppCPContract.Sensor.DESCRIPTION, "Test description " + String.valueOf(count));
			values.put(SensAppCPContract.Sensor.BACKEND, "raw");
			values.put(SensAppCPContract.Sensor.TEMPLATE, "numerical");
			values.put(SensAppCPContract.Sensor.URI, "http://sensapp.fleurey.com:80");
			values.put(SensAppCPContract.Sensor.UNIT, "count");
			values.put(SensAppCPContract.Sensor.UPLOADED, new Random().nextInt(2));
			resolver.insert(SensAppCPContract.Sensor.CONTENT_URI, values);
		}
	}
}
