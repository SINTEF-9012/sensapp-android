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

import org.sensapp.android.sensappdroid.datarequests.DatabaseRequest;
import org.sensapp.android.sensappdroid.models.Sensor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DeleteSensorRestTask extends AsyncTask<String, Void, String> {
	
	private static final String TAG = DeleteSensorRestTask.class.getSimpleName();
	
	private Context context;
	private String name;
	
	public DeleteSensorRestTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		name = params[0];
		Sensor sensor = DatabaseRequest.SensorRQ.getSensor(context, name);
		if (sensor == null) {
			return null;
		}
		String response = null;
		try {
			response = RestRequest.deleteSensor(sensor.getUri(), sensor);
		} catch (RequestErrorException e) {
			Log.e(TAG, e.getMessage());
			if (e.getCause() != null) {
				Log.e(TAG, e.getCause().getMessage());
			}
			return null;
		}
		return response;
	}

	@Override
	protected void onPostExecute(String response) {
		super.onPostExecute(response);
		boolean deleted = false;
		if (response != null) {
			deleted = "true".equals(response.trim());
		}
		if (!deleted) {
			Log.e(TAG, "Delete sensor error (" + name + ")");
			Toast.makeText(context, "Delete " + name + " failed", Toast.LENGTH_SHORT).show();
		} else {
			Log.i(TAG, "Delete sensor succeed (" + name + ")");
			Toast.makeText(context, "Delete " + name + " succeed", Toast.LENGTH_SHORT).show();
		}
	}
}
