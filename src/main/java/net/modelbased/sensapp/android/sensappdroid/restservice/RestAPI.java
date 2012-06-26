package net.modelbased.sensapp.android.sensappdroid.restservice;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppMeasureProviderContract;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.JsonParser;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.MeasureJsonModel;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.SensorJsonModel;

public class RestAPI {
	
	private static final String TAG = RestAPI.class.getName();
	
	private Context context;
	private String server;
	private int port;
	private ArrayList<Integer> ids;
	
	public RestAPI(Context context, String server, int port) {
		this.context = context;
		this.server = server;
		this.port = port;
		ids = new ArrayList<Integer>();
	}
	
	public void putMeasures(MeasureJsonModel measures) {
		String jsonString = JsonParser.measureToJson(measures);
		String[] params = {RestRequestTask.PUT_DATA, jsonString};
		new RestRequestTask(context, server, port).execute(params);
	}
	
	public void postSensor(SensorJsonModel sensor) {
		
	}
	
	synchronized public void setMeasureUploaded() {
		ContentValues cv = new ContentValues();
		cv.put(SensAppMeasureProviderContract.UPLOADED, 1);
		for (int id : ids) {
			context.getContentResolver().update(Uri.parse(SensAppMeasureProviderContract.CONTENT_URI + "/" + id), cv, null, null);
		}
		ids.clear();
	}
	
	synchronized public void pushNotUploadedMeasure() {
		Hashtable<String, MeasureJsonModel> jsonModels = new Hashtable<String, MeasureJsonModel>();
		String projection[] = {SensAppMeasureProviderContract.ID, SensAppMeasureProviderContract.SENSOR, SensAppMeasureProviderContract.VALUE, SensAppMeasureProviderContract.TIME};
		String selection = SensAppMeasureProviderContract.UPLOADED + " = 0";
		Cursor cursor = context.getContentResolver().query(SensAppMeasureProviderContract.CONTENT_URI, projection, selection, null, null);
		if (cursor != null) {
			Log.d(TAG, "Cursor count: " + cursor.getCount());
			while (cursor.moveToNext()) {
				ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(SensAppMeasureProviderContract.ID)));
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
