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
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void testMeasuresViews() {
		DataManager.insertMeasures(getActivity().getContentResolver(), nbMeasures, nbSensors);
		DataManager.insertSensors(getActivity().getContentResolver(), nbSensors);
		solo.clickOnText("testSensor0");
		solo.clickOnButton("Upload");
		solo.waitForText("Upload", 2, 20000);
		assertTrue(solo.searchText("Upload succeed"));
		solo.goBack();
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
