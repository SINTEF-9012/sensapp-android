package net.modelbased.sensapp.android.sensappdroid.restservice;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.database.DatabaseException;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.JsonParser;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.MeasureJsonModel;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.SensorJsonModel;
import net.modelbased.sensapp.android.sensappdroid.models.Sensor;

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
	
	public void deleteAllMeasure() {
		context.getContentResolver().delete(SensAppCPContract.Measure.CONTENT_URI, null, null);
		context.getContentResolver().delete(SensAppCPContract.Sensor.CONTENT_URI, null, null);
	}
	
	public void deleteUploaded() {
		String selection = SensAppCPContract.Measure.UPLOADED + " = 1";
		context.getContentResolver().delete(SensAppCPContract.Measure.CONTENT_URI, selection, null);
	}
	
	public boolean isRegister(String sensorName) throws DatabaseException {
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + sensorName), null, null, null, null);
		if (cursor == null) {
			throw new DatabaseException("Null cursor");
		} 
		boolean result = cursor.getCount() > 0;
		cursor.close();
		return result;
	}
	
	private boolean isSensorUploaded(String name) throws DatabaseException {
		String[] projection = {SensAppCPContract.Sensor.UPLOADED};
		String selection = SensAppCPContract.Sensor.NAME + " = \"" + name + "\"";
		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Sensor.CONTENT_URI, projection,  selection, null, null);
		if (cursor == null) {
			throw new DatabaseException("Null cursor");
		} 
		if (!cursor.moveToFirst()) {
			throw new DatabaseException("Sensor is not registred: " + name);
		}
		boolean result = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UPLOADED)) == 1;
		cursor.close();
		return result;
	}
	
	public void putMeasures(MeasureJsonModel measures) throws DatabaseException {
		if (!isSensorUploaded(measures.getBn())) {
			postSensor(measures.getBn());
		}
		String jsonString = JsonParser.measureToJson(measures);
		String[] params = {RestRequestTask.PUT_DATA, jsonString};
		new RestRequestTask(context, server, port).execute(params);
	}
	
	public void postSensor(SensorJsonModel sensor) {
		String jsonString = JsonParser.sensorToJson(sensor);
		String[] params = {RestRequestTask.POST_SENSOR, jsonString};
		new RestRequestTask(context, server, port).execute(params);
	}
	
	private void postSensor(String name) throws DatabaseException {
		Sensor sensor = Sensor.fromDatabase(context, name);
		SensorJsonModel.Schema schema = new SensorJsonModel.Schema(sensor.getBackend(), sensor.getTemplate());
		SensorJsonModel jsonSensor = new SensorJsonModel(name, sensor.getDescription(), schema);
		postSensor(jsonSensor);
	}
	
	synchronized public void setMeasureUploaded() {
		ContentValues cv = new ContentValues();
		cv.put(SensAppCPContract.Measure.UPLOADED, 1);
		for (int id : ids) {
			context.getContentResolver().update(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + id), cv, null, null);
		}
		ids.clear();
	}
	
	private String resolveUnit(String sensorName) throws DatabaseException {
		String projection[] = {SensAppCPContract.Sensor.UNIT};
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + sensorName), projection, null, null, null);
		if (cursor == null) {
			throw new DatabaseException("Null cursor");
		} else if (!cursor.moveToFirst()) {
			throw new DatabaseException("Sensor is not registred: " + sensorName);
		}
		String unit = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UNIT));
		cursor.close();
		return unit;
	}
	
	synchronized private void putMeasureFromSensor(String sensorName) throws DatabaseException {
		String projection[] = {SensAppCPContract.Measure.ID, SensAppCPContract.Measure.VALUE, SensAppCPContract.Measure.TIME};
		String selection = SensAppCPContract.Measure.UPLOADED + " = 0 AND " + SensAppCPContract.Measure.SENSOR + " = \"" + sensorName + "\"";
		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, projection, selection, null, null);
		if (cursor == null) {
			throw new DatabaseException("Null cursor");
		}
		Log.d(TAG, "Cursor count: " + cursor.getCount());
		MeasureJsonModel model = new MeasureJsonModel(sensorName, 0, resolveUnit(sensorName));
		while (cursor.moveToNext()) {
			ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.ID)));
			int value = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.VALUE));
			long time = cursor.getLong(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.TIME));
			model.appendMeasure(value, time);
		}
		cursor.close();
		putMeasures(model);	
	}
	
	public void pushNotUploadedMeasure() throws DatabaseException {
		ArrayList<String> sensorNames = new ArrayList<String>();
		String projection[] = {"DISTINCT " + SensAppCPContract.Measure.SENSOR};
		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, projection, null, null, null);
		if (cursor == null) {
			throw new DatabaseException("Null cursor");
		}
		Log.d(TAG, "Cursor count: " + cursor.getCount());
		while (cursor.moveToNext()) {
			sensorNames.add(cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.SENSOR)));
		}
		cursor.close();
		for (String name : sensorNames) {
			putMeasureFromSensor(name);
		}
	}
}
