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

import java.util.Random;

import org.sensapp.android.sensappdroid.api.SensAppHelper;
import org.sensapp.android.sensappdroid.contentprovider.SensAppContentProvider;
import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

public class ContentProviderTest extends ProviderTestCase2<SensAppContentProvider> {
	
	public ContentProviderTest() {
		super(SensAppContentProvider.class, SensAppContract.AUTHORITY);
	}
	
	
	public void testStressInsert() {
		final int nbInserts = 100;
		ContentResolver resolver = getContext().getContentResolver();
		for (int i = 0 ; i < nbInserts ; i ++) {
			SensAppHelper.insertMeasureForced(resolver, "sensor1", "300");
			SensAppHelper.insertMeasureForced(resolver, "sensor2", "300");
			SensAppHelper.insertMeasureForced(resolver, "sensor3", "300");
			SensAppHelper.insertMeasureForced(resolver, "sensor4", "300");
			SensAppHelper.insertMeasureForced(resolver, "sensor5", "300");
		}
	}
	
	public void testStressBatchInsert() {
		final int nbInserts = 1000;
		ContentResolver resolver = getMockContentResolver();
		for (int i = 0 ; i < nbInserts ; i ++) {
			SensAppHelper.insertMeasure(resolver, "sensor1", "300");
			SensAppHelper.insertMeasure(resolver, "sensor2", "300");
			SensAppHelper.insertMeasure(resolver, "sensor3", "300");
			SensAppHelper.insertMeasure(resolver, "sensor4", "300");
			SensAppHelper.insertMeasure(resolver, "sensor5", "300");
		}
	}
	
	/////***** Uri tests *****//////
	
	public void testMeasuresUri() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppContract.Measure.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testMeasureIdUri() {
		int id = new Random().nextInt();
		ContentResolver resolver = getMockContentResolver();
		Uri uri = Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + String.valueOf(id));
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testMeasureSensorNameUri() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = Uri.parse(SensAppContract.Measure.CONTENT_URI + "/sensorName");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testSensorsUri() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppContract.Sensor.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testSensorIdUri() {
		int id = new Random().nextInt();
		ContentResolver resolver = getMockContentResolver();
		Uri uri = Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + String.valueOf(id));
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testSensorNameUri() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/sensorName");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		cursor.close();
	}

	public void testBadUri() {
		Uri uri = Uri.parse("badUri");
		try {
			Cursor cursor = getProvider().query(uri, null, null, null, null);
			assertNotNull(cursor);
			cursor.close();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
	
	//////***** ----- *****//////
	
	/////***** CRUD tests *****//////
	
	private final int sensorsInsertNb = 2;
	private final int measuresInsertNb = 10;
	
	public void testInsertMeasures() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppContract.Measure.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(0, cursor.getCount());
		cursor.close();
		DataManager.insertMeasures(resolver, measuresInsertNb, sensorsInsertNb);
		cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(measuresInsertNb, cursor.getCount());
		cursor.close();
	}
	
	public void testInsertSensors() {
		ContentResolver resolver = getMockContentResolver();
		Uri uri = SensAppContract.Sensor.CONTENT_URI;
		Cursor cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(0, cursor.getCount());
		cursor.close();
		DataManager.insertSensors(resolver, sensorsInsertNb);
		cursor = resolver.query(uri, null, null, null, null);
		assertNotNull(cursor);
		assertEquals(sensorsInsertNb, cursor.getCount());
		cursor.close();
	}
	
	public void testQueryMeasuresBySensorNamesUri() {
		ContentResolver resolver = getMockContentResolver();
		DataManager.insertMeasures(resolver, measuresInsertNb, sensorsInsertNb);
		int nbMeasure = 0;
		Uri uri = SensAppContract.Measure.CONTENT_URI;
		Cursor cursor = null;
		for (int sensorId = 0 ; sensorId < sensorsInsertNb ; sensorId ++) {
			cursor = resolver.query(Uri.parse(uri + "/testSensor" + String.valueOf(sensorId)), null, null, null, null);
			assertNotNull(cursor);
			nbMeasure += cursor.getCount();
			cursor.close();
		}
		assertEquals(measuresInsertNb, nbMeasure);
	}
}
