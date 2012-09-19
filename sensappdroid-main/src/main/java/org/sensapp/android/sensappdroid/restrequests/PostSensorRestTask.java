/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.restrequests;

import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.datarequests.DatabaseRequest;
import org.sensapp.android.sensappdroid.models.Sensor;
import org.sensapp.android.sensappdroid.preferences.GeneralPrefFragment;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class PostSensorRestTask extends AsyncTask<Void, Void, Uri> {
	
	private static final String TAG = PostSensorRestTask.class.getSimpleName();
	
	private Context context;
	private String sensorName;
	private String errorMessage;
	
	public PostSensorRestTask(Context context, String sensorName) {
		super();
		this.context = context;
		this.sensorName = sensorName;
	}

	@Override
	protected Uri doInBackground(Void... params) {
		// Update uri with current preference
		try {
			ContentValues values = new ContentValues();
			values.put(SensAppContract.Sensor.URI, GeneralPrefFragment.buildUri(PreferenceManager.getDefaultSharedPreferences(context), context.getResources()));
			context.getContentResolver().update(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + sensorName), values, null, null);
		} catch (IllegalStateException e) {
			errorMessage = e.getMessage();
			Log.e(TAG, errorMessage);
			return null;
		}

		Sensor sensor = DatabaseRequest.SensorRQ.getSensor(context, sensorName);
		String response = null;
		try {
			response = RestRequest.postSensor(sensor);
		} catch (RequestErrorException e) {
			errorMessage = e.getMessage();
			Log.e(TAG, errorMessage);
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
			Log.e(TAG, "Post sensor error");
			if (errorMessage == null) {
				Toast.makeText(context, "Post sensor " + sensorName +  " failed", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "Post sensor " + sensorName +  " failed:\n" + errorMessage, Toast.LENGTH_LONG).show();
			}
		} else {
			ContentValues values = new ContentValues();
			values.put(SensAppContract.Sensor.UPLOADED, 1);
			DatabaseRequest.SensorRQ.updateSensor(context, sensorName, values);
			Log.i(TAG, "Post sensor succed: " + result.toString());
			Toast.makeText(context, "Sensor " + sensorName + " registred", Toast.LENGTH_LONG).show();
		}
	}
}
