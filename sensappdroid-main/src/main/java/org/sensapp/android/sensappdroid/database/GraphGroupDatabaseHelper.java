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

public class GraphGroupDatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = GraphGroupDatabaseHelper.class.getName();
	private static final String DATABASE_NAME = "sensapp_graph_group.db";
	private static final int DATABASE_VERSION = 15;

	public GraphGroupDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i(TAG, "Creating DATABASE " + DATABASE_NAME);
		GraphGroupTable.onCreate(database);
        GraphSensorTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.i(TAG, "Updating DATABASE " + DATABASE_NAME + " from " + oldVersion + " to " + newVersion);
		GraphGroupTable.onUpgrade(database, oldVersion, newVersion);
        GraphSensorTable.onUpgrade(database, oldVersion, newVersion);
	}
}
