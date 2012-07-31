package net.modelbased.sensapp.android.sensappdroid.utils;

import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

public class QueryMeasuresTask extends AsyncTask<Void, Void, Hashtable<Integer, ContentValues>> {

	private Context context;
	private String selection;
	
	public QueryMeasuresTask(Context context, String selection) {
		this.context = context;
		this.selection = selection;
	}

	@Override
	protected Hashtable<Integer, ContentValues> doInBackground(Void... params) {
		return DatabaseRequest.MeasureRQ.getMeasuresValues(context, selection);
	}
}
