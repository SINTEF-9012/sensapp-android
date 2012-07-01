package net.modelbased.sensapp.android.sensappdroid.restservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.json.JsonPrinter;
import net.modelbased.sensapp.android.sensappdroid.json.MeasureJsonModel;
import net.modelbased.sensapp.android.sensappdroid.utils.DatabaseRequest;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class PutMeasuresTask extends AsyncTask<String, Integer, Void> {
	
	private static final String TAG = PutMeasuresTask.class.getSimpleName();
	
	private Context context;
	
	public PutMeasuresTask(Context context) {
		super();
		this.context = context;
	}

	private String getUnit(String sensorName) {
		String[] projection = {SensAppCPContract.Sensor.UNIT};
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + sensorName), projection, null, null, null); 
		if (cursor != null) {
			cursor.moveToFirst();
			String unit = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UNIT));
			cursor.close();
			return unit;
		} else {
			Log.e(TAG, "Null cursor");
			return null;
		}
	}
	
	private Uri getUri(String sensorName) {
		String[] projection = {SensAppCPContract.Sensor.URI};
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + sensorName), projection, null, null, null); 
		if (cursor != null) {
			cursor.moveToFirst();
			String uri = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.URI));
			cursor.close();
			return Uri.parse(uri);
		} else {
			Log.e(TAG, "Null cursor");
			return null;
		}
	}
	
	private boolean isSensorUploaded(String sensorName) {
		String[] projection = {SensAppCPContract.Sensor.UPLOADED};
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + sensorName), projection, null, null, null); 
		if (cursor != null) {
			cursor.moveToFirst();
			int uploaded = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UPLOADED));
			cursor.close();
			return uploaded == 1;
		} else {
			Log.e(TAG, "Null cursor");
			return false;
		}
	}
	
	private List<Long> getBasetimes(String sensorName) {
		String[] projection = {"DISTINCT " + SensAppCPContract.Measure.BASETIME};
		String selection = SensAppCPContract.Measure.SENSOR + " = \"" + sensorName + "\"";
		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, projection, selection, null, null);
		if (cursor != null) {
			List<Long> basetimes = new ArrayList<Long>();
			while (cursor.moveToNext()) {
				basetimes.add(cursor.getLong(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.BASETIME)));
			}
			cursor.close();
			return basetimes;	
		} else {
			Log.e(TAG, "Null cursor");
			return null;
		}
	}
	
	private List<Integer> fillMeasureJsonModel(MeasureJsonModel model) {
		List<Integer> ids = new ArrayList<Integer>();
		String[] projection = {SensAppCPContract.Measure.ID, SensAppCPContract.Measure.VALUE, SensAppCPContract.Measure.TIME};
		String selection = SensAppCPContract.Measure.SENSOR + " = \"" + model.getBn() + "\"" + " AND " + SensAppCPContract.Measure.BASETIME + " = " + model.getBt() + " AND " + SensAppCPContract.Measure.UPLOADED + " = 0";
		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, projection, selection, null, null);
		if (cursor == null) {
			Log.e(TAG, "Null cursor");
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
	protected Void doInBackground(String... params) {
		String sensorName = params[0];
		String response = null;
		if (!isSensorUploaded(sensorName)) {
			try {
				new PostSensorTask(context).executeOnExecutor(THREAD_POOL_EXECUTOR, sensorName).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				Log.e(TAG, e.getCause().getMessage());
				RequestTask.uploadFailure(context, RequestTask.CODE_PUT_MEASURE, response);
				e.printStackTrace();
				return null;
			}
			ContentValues values = new ContentValues();
			values.put(SensAppCPContract.Sensor.UPLOADED, 1);
			DatabaseRequest.SensorRQ.updateSensors(context, SensAppCPContract.Sensor.NAME + " = \"" + sensorName + "\"", values);
		}
		Log.e(TAG, "here");
		String unit = getUnit(sensorName);
		Uri uri = getUri(sensorName);
		for (Long basetime : getBasetimes(sensorName)) {
			MeasureJsonModel model = new MeasureJsonModel(sensorName, basetime, unit);
			List<Integer> ids = fillMeasureJsonModel(model);
			try {
				response = RestRequest.putData(uri, JsonPrinter.measuresToJson(model));
			} catch (RequestErrorException e) {
				Log.e(TAG, e.getMessage());
				RequestTask.uploadFailure(context, RequestTask.CODE_PUT_MEASURE, response);
				return null;
			}
			ContentValues values = new ContentValues();
			values.put(SensAppCPContract.Measure.UPLOADED, 1);
			for (int id : ids) {
				DatabaseRequest.MeasureRQ.updateMeasures(context, SensAppCPContract.Measure.ID + " = " + id, values);
				//context.getContentResolver().update(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + id), values, null, null);
			}
			RequestTask.uploadSuccess(context, RequestTask.CODE_PUT_MEASURE, response);
		}
		return null;
	}
}
