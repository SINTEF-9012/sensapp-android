package org.sensapp.android.sensappdroid.datarequests;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class UpdateSensorsTask extends AsyncTask<Void, Void, Integer> {

	private Context context;
	private String selection;
	private ContentValues values;
	
	public UpdateSensorsTask(Context context, String selection, ContentValues values) {
		this.context = context;
		this.selection = selection;
		this.values = values;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		return DatabaseRequest.SensorRQ.updateSensors(context, selection, values);
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
			Toast.makeText(context, result + " sensors reverted", Toast.LENGTH_SHORT).show();
	}
}
