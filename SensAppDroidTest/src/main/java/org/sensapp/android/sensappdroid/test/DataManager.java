package org.sensapp.android.sensappdroid.test;

import java.util.Random;

import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

public class DataManager {

	protected static void insertMeasures(ContentResolver resolver, int nbMeasure, int nbSensors) {
		ContentValues values;
		for (int count = 0 ; count < nbMeasure ; count ++) {
			values = new ContentValues();
			values.put(SensAppCPContract.Measure.SENSOR, "testSensor" + String.valueOf(new Random().nextInt(nbSensors)));
			values.put(SensAppCPContract.Measure.BASETIME, new Random().nextLong());
			values.put(SensAppCPContract.Measure.TIME, new Random().nextLong());
			values.put(SensAppCPContract.Measure.VALUE, new Random().nextInt());
			values.put(SensAppCPContract.Measure.UPLOADED, 0);
			resolver.insert(SensAppCPContract.Measure.CONTENT_URI, values);
		}
	}
	
	protected static void insertSensors(ContentResolver resolver, int nbSensors) {
		ContentValues values;
		for (int count = 0 ; count < nbSensors ; count ++) {
			values = new ContentValues();
			values.put(SensAppCPContract.Sensor.NAME, "testSensor" + String.valueOf(count));
			values.put(SensAppCPContract.Sensor.DESCRIPTION, "Test description " + String.valueOf(count));
			values.put(SensAppCPContract.Sensor.BACKEND, "raw");
			values.put(SensAppCPContract.Sensor.TEMPLATE, "Numerical");
			values.put(SensAppCPContract.Sensor.URI, "http://sensapp.fleurey.com:80");
			values.put(SensAppCPContract.Sensor.UNIT, "count");
			values.put(SensAppCPContract.Sensor.UPLOADED, 0);
			resolver.insert(SensAppCPContract.Sensor.CONTENT_URI, values);
		}
	}
	
	protected static void cleanMeasures(ContentResolver resolver, int nbSensors) {
		for (int sensorId = 0 ; sensorId < nbSensors ; sensorId ++) {
			resolver.delete(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/testSensor" + String.valueOf(sensorId)), null, null);
		}
	}
	
	protected static void cleanSensors(ContentResolver resolver, int nbSensors) {
		for (int sensorId = 0 ; sensorId < nbSensors ; sensorId ++) {
			resolver.delete(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/testSensor" + String.valueOf(sensorId)), null, null);
		}
	}
	
	protected static void cleanAll(ContentResolver resolver, int nbSensors) {
		cleanMeasures(resolver, nbSensors);
		cleanSensors(resolver, nbSensors);
	}
}
