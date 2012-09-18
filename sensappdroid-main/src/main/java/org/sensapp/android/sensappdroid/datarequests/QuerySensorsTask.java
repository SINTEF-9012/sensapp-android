package org.sensapp.android.sensappdroid.datarequests;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

public class QuerySensorsTask extends AsyncTask<Void, Void, Hashtable<String, ContentValues>> {

	private Context context;
	private String selection;
	
	public QuerySensorsTask(Context context, String selection) {
		this.context = context;
		this.selection = selection;
	}

	@Override
	protected Hashtable<String, ContentValues> doInBackground(Void... params) {
		return DatabaseRequest.SensorRQ.getSensorsValues(context, selection);
	}
}
