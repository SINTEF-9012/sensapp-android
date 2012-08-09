package org.sensapp.android.sensappdroid.restrequests;

import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.DatabaseRequest;
import org.sensapp.android.sensappdroid.models.Sensor;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PostSensorRestTask extends AsyncTask<Void, Void, Uri> {
	
	private static final String TAG = PostSensorRestTask.class.getSimpleName();
	
	private Context context;
	private String sensorName;
	
	public PostSensorRestTask(Context context, String sensorName) {
		super();
		this.context = context;
		this.sensorName = sensorName;
	}

	@Override
	protected Uri doInBackground(Void... params) {
		Sensor sensor = DatabaseRequest.SensorRQ.getSensor(context, sensorName);
		String response = null;
		try {
			response = RestRequest.postSensor(sensor.getUri(), sensor);
		} catch (RequestErrorException e) {
			Log.e(TAG, e.getMessage());
			if (e.getCause() != null) {
				Log.e(TAG, e.getCause().getMessage());
			}
			return null;
		}
		return Uri.parse(response);
	}

	@Override
	protected void onPostExecute(Uri result) {
		super.onPostExecute(result);
		if (result == null) {
			Log.e(TAG, "Post sensor error");
			Toast.makeText(context, "Post sensor: " + sensorName +  " failed", Toast.LENGTH_LONG).show();
		} else {
			ContentValues values = new ContentValues();
			values.put(SensAppCPContract.Sensor.UPLOADED, 1);
			DatabaseRequest.SensorRQ.updateSensor(context, sensorName, values);
			Log.i(TAG, "Post sensor succed: " + result.toString());
			Toast.makeText(context, "Sensor " + sensorName + " registred", Toast.LENGTH_LONG).show();
		}
	}
}
