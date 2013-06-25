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
