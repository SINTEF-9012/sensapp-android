package net.modelbased.sensapp.android.sensappdroid.restservice;

import net.modelbased.sensapp.android.sensappdroid.models.Sensor;
import net.modelbased.sensapp.android.sensappdroid.utils.DatabaseRequest;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PostSensorTask extends AsyncTask<String, Void, Uri> {
	
	private static final String TAG = PostSensorTask.class.getSimpleName();
	
	private Context context;
	
	public PostSensorTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected Uri doInBackground(String... params) {
		String sensorName = params[0];
		Sensor sensor = DatabaseRequest.SensorRQ.getSensor(context, sensorName);
		String response = null;
		try {
			response = RestRequest.postSensor(sensor.getUri(), sensor);
		} catch (RequestErrorException e) {
			Log.e(TAG, e.getMessage());
			if (e.getCause() != null) {
				Log.e(TAG, e.getCause().getMessage());
			}
			RequestTask.uploadFailure(context, RequestTask.CODE_POST_SENSOR, response);
			return null;
		}
		RequestTask.uploadSuccess(context, RequestTask.CODE_POST_SENSOR, response);
		return Uri.parse(response);
	}

	@Override
	protected void onPostExecute(Uri result) {
		super.onPostExecute(result);
		if (result == null) {
			Log.e(TAG, "Post sensor error");
			Toast.makeText(context, "Upload failed", Toast.LENGTH_LONG).show();
		} else {
			Log.i(TAG, "Post sensor succed: " + result.toString());
			Toast.makeText(context, result.getLastPathSegment() + " uploaded", Toast.LENGTH_LONG).show();
		}
	}
}