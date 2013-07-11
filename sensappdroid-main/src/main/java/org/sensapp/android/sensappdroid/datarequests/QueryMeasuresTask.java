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

import java.util.Hashtable;

public class QueryMeasuresTask extends AsyncTask<Void, Void, Hashtable<Integer, ContentValues>> {

	private Context context;
	private String selection;
	
	public QueryMeasuresTask(Context context, String selection) {
		this.context = context;
		this.selection = selection;
	}

	@Override
	protected Hashtable<Integer, ContentValues> doInBackground(Void... params) {
		return (Hashtable<Integer, ContentValues>) DatabaseRequest.MeasureRQ.getMeasuresValues(context, selection);
	}
}
