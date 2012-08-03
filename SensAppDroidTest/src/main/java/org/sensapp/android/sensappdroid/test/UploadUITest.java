package org.sensapp.android.sensappdroid.test;

import org.sensapp.android.sensappdroid.activities.SensorsActivity;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class UploadUITest extends ActivityInstrumentationTestCase2<SensorsActivity> {

	private Solo solo;
	private final int nbMeasures = 20;
	private final int nbSensors = 3;
	
	public UploadUITest() {
		super(SensorsActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testUploadMeasuresBySensors() {
		DataManager.insertMeasures(getActivity().getContentResolver(), nbMeasures, nbSensors);
		DataManager.insertSensors(getActivity().getContentResolver(), nbSensors);
		for (int id = 0 ; id < nbSensors ; id ++) {
			solo.clickOnText("testSensor" + id);
			solo.clickOnButton("Upload");
			solo.waitForText("Upload", 2, 20000);
			assertTrue(solo.searchText("Upload succeed"));
			solo.goBack();
		}
	}
	
	public void testUploadAllMeasures() {
		DataManager.insertMeasures(getActivity().getContentResolver(), nbMeasures, nbSensors);
		DataManager.insertSensors(getActivity().getContentResolver(), nbSensors);
		solo.clickOnButton("Upload");
		solo.waitForText("Upload", 2, 20000);
		assertTrue(solo.searchText("Upload succeed"));
	}
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		DataManager.cleanAll(getActivity().getContentResolver(), nbSensors);
	}
}
