package net.modelbased.sensapp.android.sensappdroid.restservice;

import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppMeasureProviderContract;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.JsonParser;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.MeasureJsonModel;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.SensorJsonModel;

public class RestAPI {
	
	private static final String TAG = RestAPI.class.getName();
	
	private Context context;
	private RestRequestTask task;
	
	public RestAPI(Context context, String server, int port) {
		this.context = context;
		task = new RestRequestTask(server, port);
	}
	
	public void putMeasures(MeasureJsonModel measures) {
		String jsonString = JsonParser.measureToJson(measures);
		String[] params = {RestRequestTask.PUT_DATA, jsonString};
		task.execute(params);
	}
	
	public void postSensor(SensorJsonModel sensor) {
		
	}
	
	public void pushNotUploadedMeasure() {
		Hashtable<String, MeasureJsonModel> jsonModels = new Hashtable<String, MeasureJsonModel>();
		String projection[] = {SensAppMeasureProviderContract.ID, SensAppMeasureProviderContract.SENSOR, SensAppMeasureProviderContract.VALUE, SensAppMeasureProviderContract.TIME};
		String selection = SensAppMeasureProviderContract.UPLOADED + " = 0";
		Cursor cursor = context.getContentResolver().query(SensAppMeasureProviderContract.CONTENT_URI, projection, selection, null, null);
		if (cursor != null) {
			Log.e(TAG, "Cursor count: " + cursor.getCount());
			while (cursor.moveToNext()) {
				String sensor = cursor.getString(cursor.getColumnIndexOrThrow(SensAppMeasureProviderContract.SENSOR));
				int value = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppMeasureProviderContract.VALUE));
				long time = cursor.getLong(cursor.getColumnIndexOrThrow(SensAppMeasureProviderContract.TIME));
				if (!jsonModels.containsKey(sensor)) {
					jsonModels.put(sensor, new MeasureJsonModel(sensor, 0, "m"));
				}
				jsonModels.get(sensor).appendMeasure(value, time);
			}
			cursor.close();
		}
		for (MeasureJsonModel model : jsonModels.values()) {
			putMeasures(model);
		}
	}
}
