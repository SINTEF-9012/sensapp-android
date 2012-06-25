package net.modelbased.sensapp.android.sensappdroid.restservice;

import android.os.AsyncTask;

public class RestRequestTask extends AsyncTask<String, Void, Boolean> {
	
	public static final String POST_SENSOR = "10";
	public static final String PUT_DATA = "20";
	private static final int MODE_POST_SENSOR = 10;
	private static final int MODE_PUT_DATA = 20;
	
	private String server;
	private int port;
	
	public RestRequestTask(String server, int port) {
		super();
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
			if (!response.equals("[]")) {
				throw new IllegalArgumentException("Sensor is not registred: " + response);
			}
			return true;
		default:
			throw new IllegalArgumentException("Unknown mode: " + mode); 
		}
	}
}
