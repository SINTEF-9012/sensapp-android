package net.modelbased.sensapp.android.sensappdroid.restservice;

import java.util.ArrayList;
import java.util.List;

import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.JsonParser;
import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.MeasureJsonModel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

public class PutMeasuresTask extends AsyncTask<Void, Integer, Void> {
	
	private Context context;
	private String server;
	private int port;
	private String sensorName;
	
	public PutMeasuresTask(Context context, String server, int port, String sensorName) {
		super();
		this.context = context;
		this.server = server;
		this.port = port;
		this.sensorName = sensorName;
	}

	private String getUnit(String sensorName) {
		String[] projection = {SensAppCPContract.Sensor.UNIT};
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + sensorName), projection, null, null, null); 
		if (cursor == null) {
			RequestTask.uploadFailure(context, RequestTask.CODE_PUT_MEASURE, null);
			return null;
		} 
		cursor.moveToFirst();
		String unit = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UNIT));
		cursor.close();
		return unit;
	}
	
	private List<Long> getBasetimes(String sensorName) {
		String[] projection = {"DISTINCT " + SensAppCPContract.Measure.BASETIME};
		String selection = SensAppCPContract.Measure.SENSOR + " = \"" + sensorName + "\"";
		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, projection, selection, null, null);
		if (cursor == null) {
			RequestTask.uploadFailure(context, RequestTask.CODE_PUT_MEASURE, null);
			return null;
		}
		List<Long> basetimes = new ArrayList<Long>();
		while (cursor.moveToNext()) {
			basetimes.add(cursor.getLong(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.BASETIME)));
		}
		cursor.close();
		return basetimes;
	}
	
	private List<Integer> fillMeasureJsonModel(MeasureJsonModel model) {
		List<Integer> ids = new ArrayList<Integer>();
		String[] projection = {SensAppCPContract.Measure.ID, SensAppCPContract.Measure.VALUE, SensAppCPContract.Measure.TIME};
		String selection = SensAppCPContract.Measure.SENSOR + " = \"" + model.getBn() + "\"" + " AND " + SensAppCPContract.Measure.BASETIME + " = " + model.getBt() + " AND " + SensAppCPContract.Measure.UPLOADED + " = 0";
		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, projection, selection, null, null);
		if (cursor == null) {
			RequestTask.uploadFailure(context, RequestTask.CODE_PUT_MEASURE, null);
			return null;
		}
		while (cursor.moveToNext()) {
			ids.add(cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.ID)));
			int value = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.VALUE));
			long time = cursor.getLong(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.TIME));
			model.appendMeasure(value, time);
		}
		cursor.close();
		return ids;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		String response = null;
		String unit = getUnit(sensorName);
		for (Long basetime : getBasetimes(sensorName)) {
			MeasureJsonModel model = new MeasureJsonModel(sensorName, basetime, unit);
			List<Integer> ids = fillMeasureJsonModel(model);
			try {
				response = RestRequest.putData(server, port, JsonParser.measuresToJson(model));
			} catch (RequestErrorException e) {
				e.printStackTrace();
				RequestTask.uploadFailure(context, RequestTask.CODE_PUT_MEASURE, response);
				return null;
			}
			ContentValues values = new ContentValues();
			values.put(SensAppCPContract.Measure.UPLOADED, 1);
			for (int id : ids) {
				context.getContentResolver().update(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + id), values, null, null);
			}
			RequestTask.uploadSuccess(context, RequestTask.CODE_PUT_MEASURE, response);
		}
		return null;
	}
}
