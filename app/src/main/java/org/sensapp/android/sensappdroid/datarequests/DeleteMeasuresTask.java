package org.sensapp.android.sensappdroid.datarequests;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class DeleteMeasuresTask extends AsyncTask<String, Void, Integer> {

	private static final String TAG = DeleteMeasuresTask.class.getSimpleName(); 
	
	private Context context;
	private Uri uri;
	
	public DeleteMeasuresTask(Context context, Uri uri) {
		this.context = context;
		this.uri = uri;
	}

	@Override
	protected Integer doInBackground(String... params) {
		String selection = null;
		if (params.length > 0) {
			selection = params[0];
		}
		return context.getContentResolver().delete(uri, selection, null);
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		Log.i(TAG, "Uri: " + uri + " - " + result + " rows deleted");
	}
}
