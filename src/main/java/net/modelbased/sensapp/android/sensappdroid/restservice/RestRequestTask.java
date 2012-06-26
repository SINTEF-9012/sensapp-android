package net.modelbased.sensapp.android.sensappdroid.restservice;

import net.modelbased.sensapp.android.sensappdroid.SensAppService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class RestRequestTask extends AsyncTask<String, Void, Boolean> {
	
	public static final String ACTION_REQUEST_SUCCEED = "net.modelbased.sensapp.android.sensappdroid.restrequesttask.ACTION_REQUEST_SUCCEED";
	public static final String EXTRA_MODE = "net.modelbased.sensapp.android.sensappdroid.restrequesttask.EXTRA_MODE";
	public static final String EXTRA_RESPONSE = "net.modelbased.sensapp.android.sensappdroid.restrequesttask.EXTRA_RESPONSE";
	public static final String POST_SENSOR = "10";
	public static final String PUT_DATA = "20";
	public static final int MODE_POST_SENSOR = 10;
	public static final int MODE_PUT_DATA = 20;
	
	private Context context;
	private String server;
	private int port;
	
	public RestRequestTask(Context context, String server, int port) {
		super();
		this.context = context;
		this.server = server;
		this.port = port;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		int mode = Integer.parseInt(params[0]);
		String jsonString = params[1];
		String response = null;
		switch (mode) {
		case MODE_POST_SENSOR:
			response = RestRequest.postSensor(server, port, jsonString);
			return true;
		case MODE_PUT_DATA:
			response = RestRequest.putData(server, port, jsonString);
			if (response.trim().length() > 2) {
				throw new IllegalArgumentException("Sensor is not registred: " + response);
			} 
			dataUploadSuccess(response);
			return true;
		default:
			throw new IllegalArgumentException("Unknown mode: " + mode); 
		}
	}
	
	private void dataUploadSuccess(String response) {
		uploadSuccess(MODE_PUT_DATA, response);
	}
	
	private void uploadSuccess(int mode, String response) {
		Intent i = new Intent(context, SensAppService.class);
		i.setAction(ACTION_REQUEST_SUCCEED);
		i.putExtra(EXTRA_MODE, mode);
		i.putExtra(EXTRA_RESPONSE, response);
		context.startService(i);
	}
}
