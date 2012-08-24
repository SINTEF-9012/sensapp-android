package org.sensapp.android.sensappdroid.datarequests;

import android.content.Context;
import android.os.AsyncTask;

public class DeleteCompositeTask extends AsyncTask<String, Void, Integer> {

	private Context context;
	
	public DeleteCompositeTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		return DatabaseRequest.CompositeRQ.deleteComposite(context, params[0]);
	}
}
