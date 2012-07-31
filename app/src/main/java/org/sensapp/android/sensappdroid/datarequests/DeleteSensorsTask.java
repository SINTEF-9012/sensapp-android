package org.sensapp.android.sensappdroid.datarequests;

import android.content.Context;
import android.os.AsyncTask;

public class DeleteSensorsTask extends AsyncTask<String, Void, Integer> {

	private Context context;
	
	public DeleteSensorsTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		return DatabaseRequest.SensorRQ.deleteSensor(context, params[0]);
	}
}
