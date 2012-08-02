package org.sensapp.android.sensappdroid.test;

import junit.framework.Assert;

import org.sensapp.android.sensappdroid.activities.SensorsActivity;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

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
	
	public void testPreferenceIsSaved() throws Exception {
		Assert.assertTrue(solo.searchText("Upload"));
	}
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
