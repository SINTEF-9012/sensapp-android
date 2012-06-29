package net.modelbased.sensapp.android.sensappdroid.utils;

import android.content.Context;
import android.os.AsyncTask;

public class DeleteSensorsTask extends AsyncTask<Void, Void, Integer> {

	private Context context;
	private String name;
	
	public DeleteSensorsTask(Context context, String name) {
		this.context = context;
		this.name = name;
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		return DatabaseRequest.SensorRQ.deleteSensor(context, name);
	}
}
