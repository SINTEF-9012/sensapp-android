package org.sensapp.android.sensappdroid.test;

import java.util.concurrent.TimeUnit;

import org.sensapp.android.sensappdroid.restrequests.DeleteSensorRestTask;
import org.sensapp.android.sensappdroid.restrequests.PostSensorRestTask;

import android.test.AndroidTestCase;

public class DeleteSensorRestTaskTest extends AndroidTestCase {

	public void testSensorDontExists() {
		try {
			assertNull(new DeleteSensorRestTask(getContext()).execute("testSensor").get(5, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testSensorNotUploaded() {
		try {
			assertNull(new DeleteSensorRestTask(getContext()).execute("testSensor0").get(5, TimeUnit.SECONDS));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testSensorEmptyUploaded() {
		DataManager.insertSensors(getContext().getContentResolver(), 1);
		try {
		assertNotNull(new PostSensorRestTask(getContext(), "testSensor0").execute().get(15, TimeUnit.SECONDS));
		String result = new DeleteSensorRestTask(getContext()).execute("testSensor0").get(5, TimeUnit.SECONDS);
		assertNotNull(result);
		assertTrue(result.trim().equals("true"));
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
