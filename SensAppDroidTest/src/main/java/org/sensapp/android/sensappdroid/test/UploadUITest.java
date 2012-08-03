package org.sensapp.android.sensappdroid.test;

import org.sensapp.android.sensappdroid.activities.SensorsActivity;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;

import android.database.Cursor;
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
		DataManager.insertMeasures(getActivity().getContentResolver(), nbMeasures, nbSensors);
		DataManager.insertSensors(getActivity().getContentResolver(), nbSensors);
	}

	public void testActivityTestCaseSetUpProperly() {
        assertNotNull(getActivity());
        Cursor cursor = getActivity().getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertEquals(nbMeasures, cursor.getCount());
        cursor.close();
        cursor = getActivity().getContentResolver().query(SensAppCPContract.Sensor.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertEquals(nbSensors, cursor.getCount());
        cursor.close();
    }
	
	public void testMeasuresViews() {
		solo.clickOnText("Upload");
	}
	
	@Override
	public void tearDown() throws Exception {
		DataManager.cleanAll(getActivity().getContentResolver(), nbSensors);
		solo.finishOpenedActivities();
	}
}
