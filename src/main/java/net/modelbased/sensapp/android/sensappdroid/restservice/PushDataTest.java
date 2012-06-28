package net.modelbased.sensapp.android.sensappdroid.restservice;

import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.database.DatabaseException;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;



public class PushDataTest {
	
	public static Uri pushData(Context context) {
		ContentValues values = new ContentValues();
		values.put(SensAppCPContract.Measure.SENSOR, "sa_test");
		values.put(SensAppCPContract.Measure.VALUE, 25);
		values.put(SensAppCPContract.Measure.TIME, 256000041);
		values.put(SensAppCPContract.Measure.UPLOADED, 0);
		return context.getContentResolver().insert(SensAppCPContract.Measure.CONTENT_URI, values);
	}
	
	public static void pushSensor(Context context) throws DatabaseException {
		if (!new RestAPI(context, null, 0).isRegister("sa_test")) {
			ContentValues values = new ContentValues();
			values.put(SensAppCPContract.Sensor.NAME, "sa_test");
			values.put(SensAppCPContract.Sensor.DESCRIPTION, "First test");
			values.put(SensAppCPContract.Sensor.BACKEND, "raw");
			values.put(SensAppCPContract.Sensor.TEMPLATE, "Numerical");
			values.put(SensAppCPContract.Sensor.UNIT, "count");
			values.put(SensAppCPContract.Sensor.UPLOADED, 0);
			context.getContentResolver().insert(SensAppCPContract.Sensor.CONTENT_URI, values);
		}
	}
}
