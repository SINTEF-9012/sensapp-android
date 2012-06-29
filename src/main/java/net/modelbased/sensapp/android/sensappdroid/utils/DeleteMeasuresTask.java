package net.modelbased.sensapp.android.sensappdroid.utils;

import android.content.Context;
import android.os.AsyncTask;

public class DeleteMeasuresTask extends AsyncTask<Void, Void, Integer> {

	private Context context;
	private int id;
	private String selection;
	private boolean idMode;
	
	public DeleteMeasuresTask(Context context, int id) {
		this.context = context;
		this.id = id;
		idMode = true;
	}
	
	public DeleteMeasuresTask(Context context, String selection) {
		this.context = context;
		this.selection = selection;
		idMode = false;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		if (idMode) {
			return DatabaseRequest.MeasureRQ.deleteMeasure(context, id);
		} else {
			return DatabaseRequest.MeasureRQ.deleteMeasures(context, selection);
		}
	}
}
