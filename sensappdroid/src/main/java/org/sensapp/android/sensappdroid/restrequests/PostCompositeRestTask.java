package org.sensapp.android.sensappdroid.restrequests;

import java.util.concurrent.ExecutionException;

import org.sensapp.android.sensappdroid.contentprovider.SensAppContract;
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
	private String errorMessage;
	
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
			values.put(SensAppContract.Composite.URI, GeneralPrefFragment.buildUri(PreferenceManager.getDefaultSharedPreferences(context), context.getResources()));
			context.getContentResolver().update(Uri.parse(SensAppContract.Composite.CONTENT_URI + "/" + compositeName), values, null, null);
		} catch (IllegalStateException e) {
			errorMessage = e.getMessage();
			e.printStackTrace();
			return null;
		}

		Composite composite = DatabaseRequest.CompositeRQ.getComposite(context, compositeName);
		Sensor sensor;
		for (Uri uri : composite.getSensors()) {
			// Update sensor uri with current preference
			try {
				ContentValues values = new ContentValues();
				values.put(SensAppContract.Sensor.URI, GeneralPrefFragment.buildUri(PreferenceManager.getDefaultSharedPreferences(context), context.getResources()));
				context.getContentResolver().update(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + uri.getLastPathSegment()), values, null, null);
			} catch (IllegalStateException e) {
				errorMessage = e.getMessage();
				e.printStackTrace();
				return null;
			}
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
				errorMessage = e.getMessage();
				Log.e(TAG, errorMessage);
				return null;
			}
		}
		String response = null;
		try {
			response = RestRequest.postComposite(composite);
		} catch (RequestErrorException e) {
			errorMessage = e.getMessage();
			Log.e(TAG, errorMessage);
			return null;
		}
		return Uri.parse(response);
	}

	@Override
	protected void onPostExecute(Uri result) {
		super.onPostExecute(result);
		if (result == null) {
			Log.e(TAG, "Post composite " + compositeName + " error: " + errorMessage);
			if (errorMessage == null) {
				Toast.makeText(context, "Post composite " + compositeName +  " failed", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "Post composite " + compositeName +  " failed:\n" + errorMessage, Toast.LENGTH_LONG).show();
			}
		} else {
			Log.i(TAG, "Post composite succeed: " + result.toString());
			Toast.makeText(context, "Composite " + compositeName + " registred", Toast.LENGTH_LONG).show();
		}
	}
}
