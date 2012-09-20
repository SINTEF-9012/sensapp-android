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
package org.sensapp.android.sensappdroid.api;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.content.ContentValues;
import android.content.Context;

public final class InsertionManager {

	private final static int LOWEST_INTERVAL = 1;
	
	private static final ArrayDeque<ContentValues> buffers = new ArrayDeque<ContentValues>();
	private static Boolean empty = true;
	private static final ArrayList<ContentValues> valuesBatch = new ArrayList<ContentValues>();
	private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	
	private InsertionManager() {}
	
	public static void storeMeasure(final Context context, ContentValues values) {
		//Log.w("DEBUG", "__Store__");
		buffers.add(values);
		if (empty) {
			scheduleFlush(context);
			empty = false;
		}
	}

	private static void flushSensorMeasures(Context context) {
		//Log.e("DEBUG", "__Flush__");
		valuesBatch.clear();
		ContentValues values = buffers.pollFirst(); 
		while (values != null) {
			valuesBatch.add(values);
			values = buffers.pollFirst();
			empty = true;
		}
		context.getContentResolver().bulkInsert(SensAppContract.Measure.CONTENT_URI, valuesBatch.toArray(new ContentValues[valuesBatch.size()]));
	}
	
	private static void scheduleFlush(final Context context) {
		worker.schedule(new Runnable() {
			public void run() {
				flushSensorMeasures(context);
			}
		}, LOWEST_INTERVAL, TimeUnit.SECONDS);
	}
}
