package net.modelbased.sensapp.android.sensappdroid.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

public class UpdateMeasuresTask extends AsyncTask<Void, Void, Integer> {

	private Context context;
	private String selection;
	private ContentValues values;
	
	public UpdateMeasuresTask(Context context, String selection, ContentValues values) {
		this.context = context;
		this.selection = selection;
		this.values = values;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		return DatabaseRequest.MeasureRQ.updateMeasures(context, selection, values);
	}
}
