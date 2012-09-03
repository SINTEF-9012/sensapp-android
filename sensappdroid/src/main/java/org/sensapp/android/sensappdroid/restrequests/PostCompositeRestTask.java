package org.sensapp.android.sensappdroid.restrequests;

import java.util.concurrent.ExecutionException;

import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.DatabaseRequest;
import org.sensapp.android.sensappdroid.models.Composite;
import org.sensapp.android.sensappdroid.models.Sensor;
import org.sensapp.android.sensappdroid.preferences.GeneralPrefFragment;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class PostCompositeRestTask extends AsyncTask<Void, Void, Uri> {
	
	private static final String TAG = PostCompositeRestTask.class.getSimpleName();
	
	private Context context;
	private String compositeName;
	
	public PostCompositeRestTask(Context context, String compositeName) {
		super();
		this.context = context;
		this.compositeName = compositeName;
	}

	@Override
	protected Uri doInBackground(Void... params) {
		// Update uri with current preference
		try {
			ContentValues values = new ContentValues();
			values.put(SensAppCPContract.Composite.URI, GeneralPrefFragment.buildUri(PreferenceManager.getDefaultSharedPreferences(context), context.getResources()));
			context.getContentResolver().update(Uri.parse(SensAppCPContract.Composite.CONTENT_URI + "/" + compositeName), values, null, null);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		
		Composite composite = DatabaseRequest.CompositeRQ.getComposite(context, compositeName);
		Sensor sensor;
		for (Uri uri : composite.getSensors()) {
			 sensor = DatabaseRequest.SensorRQ.getSensor(context, uri.getLastPathSegment());
			try {
				if (!RestRequest.isSensorRegistred(sensor)) {
					Uri postSensorResult = null;
					try {
						postSensorResult = new PostSensorRestTask(context, sensor.getName()).executeOnExecutor(THREAD_POOL_EXECUTOR).get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					if (postSensorResult == null) {
						Log.e(TAG, "Post sensor failed");
						return null;
					}
				}
			} catch (RequestErrorException e) {
				Log.e(TAG, e.getMessage());
				return null;
			}
		}
		String response = null;
		try {
			response = RestRequest.postComposite(composite);
		} catch (RequestErrorException e) {
			Log.e(TAG, e.getMessage());
			if (e.getCause() != null) {
				Log.e(TAG, e.getCause().getMessage());
			}
			return null;
		}
		return Uri.parse(response);
	}

	@Override
	protected void onPostExecute(Uri result) {
		super.onPostExecute(result);
		if (result == null) {
			Log.e(TAG, "Post composite error");
			Toast.makeText(context, "Post composite: " + compositeName +  " failed", Toast.LENGTH_LONG).show();
		} else {
			Log.i(TAG, "Post composite succeed: " + result.toString());
			Toast.makeText(context, "Composite " + compositeName + " registred", Toast.LENGTH_LONG).show();
		}
	}
}
