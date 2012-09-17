package org.sensapp.android.sensappdroid.test;

import java.util.concurrent.TimeUnit;

import org.sensapp.android.sensappdroid.contentprovider.SensAppContentProvider;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.restrequests.PutMeasuresTask;

import android.content.ContentValues;
import android.net.Uri;
import android.test.ProviderTestCase2;

public class PutMeasuresRestTaskTest extends ProviderTestCase2<SensAppContentProvider> {

	public PutMeasuresRestTaskTest() {
		super(SensAppContentProvider.class, SensAppContract.AUTHORITY);
	}

	public void testNoMeasure() {
		try {
			int nbUploaded = new PutMeasuresTask(getContext(), SensAppContract.Measure.CONTENT_URI).execute().get(5000, TimeUnit.MILLISECONDS);
			assertEquals(0, nbUploaded);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testMeasureWithoutSensor() {
		ContentValues values = new ContentValues();
		values.put(SensAppContract.Measure.ID, 11);
		values.put(SensAppContract.Measure.BASETIME, 0);
		values.put(SensAppContract.Measure.TIME, 1000);
		values.put(SensAppContract.Measure.SENSOR, "testSensor0");
		values.put(SensAppContract.Measure.VALUE, 999);
		values.put(SensAppContract.Measure.UPLOADED, 0);
		getContext().getContentResolver().insert(SensAppContract.Measure.CONTENT_URI, values);
		try {
			assertNull(new PutMeasuresTask(getContext(), SensAppContract.Measure.CONTENT_URI).execute().get(5000, TimeUnit.MILLISECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testMeasureById() {
		ContentValues values = new ContentValues();
		values.put(SensAppContract.Measure.ID, 11);
		values.put(SensAppContract.Measure.BASETIME, 0);
		values.put(SensAppContract.Measure.TIME, 1000);
		values.put(SensAppContract.Measure.SENSOR, "testSensor0");
		values.put(SensAppContract.Measure.VALUE, 999);
		values.put(SensAppContract.Measure.UPLOADED, 0);
		getContext().getContentResolver().insert(SensAppContract.Measure.CONTENT_URI, values);
		DataManager.insertSensors(getContext().getContentResolver(), 1);
		try {
			int nbUploaded = new PutMeasuresTask(getContext(), Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + 11)).execute().get(10, TimeUnit.SECONDS);
			assertEquals(1, nbUploaded);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testMeasuresBySensor() {
		DataManager.insertMeasures(getContext().getContentResolver(), 15, 1);
		DataManager.insertSensors(getContext().getContentResolver(), 1);
		try {
			int nbUploaded = new PutMeasuresTask(getContext(), Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + "testSensor0")).execute().get(15, TimeUnit.SECONDS);
			assertEquals(15, nbUploaded);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testAllMeasures() {
		DataManager.insertMeasures(getContext().getContentResolver(), 20, 3);
		DataManager.insertSensors(getContext().getContentResolver(), 3);
		try {	
			int nbUploaded = new PutMeasuresTask(getContext(), SensAppContract.Measure.CONTENT_URI).execute().get(20, TimeUnit.SECONDS);
			assertEquals(20, nbUploaded);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Override
	public void tearDown() throws Exception {
		DataManager.cleanAll(getContext());
	}
}
