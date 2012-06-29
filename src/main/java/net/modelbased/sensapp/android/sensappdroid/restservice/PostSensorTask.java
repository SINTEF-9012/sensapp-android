package net.modelbased.sensapp.android.sensappdroid.restservice;

import net.modelbased.sensapp.android.sensappdroid.jsondatamodel.JsonParser;
import net.modelbased.sensapp.android.sensappdroid.models.Sensor;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

public class PostSensorTask extends AsyncTask<Sensor, Integer, Uri> {
	
	private Context context;
	private String server;
	private int port;
	
	public PostSensorTask(Context context, String server, int port) {
		super();
		this.context = context;
		this.server = server;
		this.port = port;
	}

	@Override
	protected Uri doInBackground(Sensor... params) {
		String response = null;
		try {
			response = RestRequest.postSensor(server, port, JsonParser.sensorToJson(params[0]));
		} catch (RequestErrorException e) {
			e.printStackTrace();
			RequestTask.uploadFailure(context, RequestTask.CODE_POST_SENSOR, response);
			return null;
		}
		RequestTask.uploadSuccess(context, RequestTask.CODE_POST_SENSOR, response);
		return Uri.parse(response);
	}
}
