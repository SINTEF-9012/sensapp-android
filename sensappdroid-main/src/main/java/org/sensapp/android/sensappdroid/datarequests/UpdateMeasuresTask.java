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
package org.sensapp.android.sensappdroid.datarequests;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		Toast.makeText(context, result + " measures reverted", Toast.LENGTH_SHORT).show();
	}
}
