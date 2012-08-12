package org.sensapp.android.sensappdroid.test;

import java.util.Random;

import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.contentprovider.SensAppContentProvider;

import android.content.ContentResolver;
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
	
	/////***** CRUD tests *****//////
	
	private final int sensorsInsertNb = 2;
	private final int measuresInsertNb = 10;
	
	public void testInsertMeasures() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppCPContract.Measure.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(0, cursor.getCount());
		cursor.close();
		DataManager.insertMeasures(resolver, measuresInsertNb, sensorsInsertNb);
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
		DataManager.insertSensors(resolver, sensorsInsertNb);
		cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(sensorsInsertNb, cursor.getCount());
		cursor.close();
	}
	
	public void testQueryMeasuresBySensorNamesUri() {
		ContentResolver resolver = getMockContentResolver();
		DataManager.insertMeasures(resolver, measuresInsertNb, sensorsInsertNb);
		int nbMeasure = 0;
		Uri uri = SensAppCPContract.Measure.CONTENT_URI;
		Cursor cursor = null;
		for (int sensorId = 0 ; sensorId < sensorsInsertNb ; sensorId ++) {
			cursor = resolver.query(Uri.parse(uri + "/testSensor" + String.valueOf(sensorId)), null, null, null, null);
			assertNotNull(cursor);
			nbMeasure += cursor.getCount();
			cursor.close();
		}
		assertEquals(measuresInsertNb, nbMeasure);
	}
}
