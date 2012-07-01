package net.modelbased.sensapp.android.sensappdroid.restservice;

import net.modelbased.sensapp.android.sensappdroid.models.Sensor;
import net.modelbased.sensapp.android.sensappdroid.utils.DatabaseRequest;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
			RequestTask.uploadFailure(context, RequestTask.CODE_POST_SENSOR, response);
			return null;
		}
		RequestTask.uploadSuccess(context, RequestTask.CODE_POST_SENSOR, response);
		return Uri.parse(response);
	}
}
