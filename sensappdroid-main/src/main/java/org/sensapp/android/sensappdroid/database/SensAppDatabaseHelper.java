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
package org.sensapp.android.sensappdroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SensAppDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = SensAppDatabaseHelper.class.getName();
	private static final String DATABASE_NAME = "sensapp.db";
	private static final int DATABASE_VERSION = 14;
	
	public SensAppDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i(TAG, "Creating DATABASE " + DATABASE_NAME);
		MeasureTable.onCreate(database);
		SensorTable.onCreate(database);
		CompositeTable.onCreate(database);
		ComposeTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.i(TAG, "Updating DATABASE " + DATABASE_NAME + " from " + oldVersion + " to " + newVersion);
		MeasureTable.onUpgrade(database, oldVersion, newVersion);
		SensorTable.onUpgrade(database, oldVersion, newVersion);
		CompositeTable.onUpgrade(database, oldVersion, newVersion);
		ComposeTable.onUpgrade(database, oldVersion, newVersion);
	}
}
