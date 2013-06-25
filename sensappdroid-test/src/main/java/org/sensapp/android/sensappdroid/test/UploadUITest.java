/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.test;

import org.sensapp.android.sensappdroid.activities.TabsActivity;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class UploadUITest extends ActivityInstrumentationTestCase2<TabsActivity> {

	private Solo solo;
	private final int nbMeasures = 20;
	private final int nbSensors = 3;
	
	public UploadUITest() {
		super(TabsActivity.class);
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
		DataManager.cleanAll(getActivity());
	}
}
