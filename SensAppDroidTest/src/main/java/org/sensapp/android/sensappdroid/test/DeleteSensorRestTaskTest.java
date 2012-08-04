package org.sensapp.android.sensappdroid.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.sensapp.android.sensappdroid.restrequests.DeleteSensorRestTask;
import org.sensapp.android.sensappdroid.restrequests.PostSensorRestTask;

import android.test.AndroidTestCase;

public class DeleteSensorRestTaskTest extends AndroidTestCase {

	public void testSensorDontExists() throws InterruptedException, ExecutionException, TimeoutException {
		String result = new DeleteSensorRestTask(getContext()).execute("testSensor").get(5, TimeUnit.SECONDS);
		assertNull(result);
	}
	
	public void testSensorNotUploaded() throws InterruptedException, ExecutionException, TimeoutException {
		DataManager.insertSensors(getContext().getContentResolver(), 1);
		String result = new DeleteSensorRestTask(getContext()).execute("testSensor0").get(5, TimeUnit.SECONDS);
		assertNull(result);
	}
	
	public void testSensorEmptyUploaded() throws InterruptedException, ExecutionException, TimeoutException {
		DataManager.insertSensors(getContext().getContentResolver(), 1);
		assertNotNull(new PostSensorRestTask(getContext()).execute("testSensor0").get(15, TimeUnit.SECONDS));
		String result = new DeleteSensorRestTask(getContext()).execute("testSensor0").get(5, TimeUnit.SECONDS);
		assertNotNull(result);
		assertTrue(result.trim().equals("true"));
	}
	
	@Override
	public void tearDown() throws Exception {
		DataManager.cleanAll(getContext().getContentResolver(), 1);
	}
	
}
