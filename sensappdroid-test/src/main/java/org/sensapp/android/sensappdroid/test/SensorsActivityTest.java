package org.sensapp.android.sensappdroid.test;

import org.sensapp.android.sensappdroid.activities.SensorsActivity;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class SensorsActivityTest extends ActivityInstrumentationTestCase2<SensorsActivity> {

	private Solo solo;
	
	public SensorsActivityTest() {
		super(SensorsActivity.class);
	}
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testActivityTestCaseSetUpProperly() {
        assertNotNull("activity should be launched successfully", getActivity());
    }
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
