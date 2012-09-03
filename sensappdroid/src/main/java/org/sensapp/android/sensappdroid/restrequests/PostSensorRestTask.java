package org.sensapp.android.sensappdroid.restrequests;

import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.DatabaseRequest;
import org.sensapp.android.sensappdroid.models.Sensor;
import org.sensapp.android.sensappdroid.preferences.GeneralPrefFragment;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class PostSensorRestTask extends AsyncTask<Void, Void, Uri> {
	
	private static final String TAG = PostSensorRestTask.class.getSimpleName();
	
	private Context context;
	private String sensorName;
	private String errorMessage;
	
	public PostSensorRestTask(Context context, String sensorName) {
		super();
		this.context = context;
		this.sensorName = sensorName;
	}

	@Override
	protected Uri doInBackground(Void... params) {
		// Update uri with current preference
		try {
			ContentValues values = new ContentValues();
			values.put(SensAppCPContract.Sensor.URI, GeneralPrefFragment.buildUri(PreferenceManager.getDefaultSharedPreferences(context), context.getResources()));
			context.getContentResolver().update(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + sensorName), values, null, null);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		Sensor sensor = DatabaseRequest.SensorRQ.getSensor(context, sensorName);
		String response = null;
		try {
			response = RestRequest.postSensor(sensor);
		} catch (RequestErrorException e) {
			errorMessage = e.getMessage();
			Log.e(TAG, errorMessage);
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
			if (errorMessage == null) {
				Toast.makeText(context, "Post sensor " + sensorName +  " failed", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "Post sensor " + sensorName +  " failed:\n" + errorMessage, Toast.LENGTH_LONG).show();
			}
		} else {
			ContentValues values = new ContentValues();
			values.put(SensAppCPContract.Sensor.UPLOADED, 1);
			DatabaseRequest.SensorRQ.updateSensor(context, sensorName, values);
			Log.i(TAG, "Post sensor succed: " + result.toString());
			Toast.makeText(context, "Sensor " + sensorName + " registred", Toast.LENGTH_LONG).show();
		}
	}
}
