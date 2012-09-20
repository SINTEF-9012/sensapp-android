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

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public final class MetadataTable {

	public static final String TABLE_METADATA = "METADATA";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SENSOR = "sensor";
	public static final String COLUMN_KEY = "key";
	public static final String COLUMN_VALUE = "value";
	
	private static final String TAG = MetadataTable.class.getName();
	private static final String DATABASE_CREATE_TABLE = "CREATE TABLE " 
			+ TABLE_METADATA + " (" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_SENSOR + " TEXT NOT NULL, "
			+ COLUMN_KEY + " TEXT NOT NULL, "
			+ COLUMN_VALUE + " TEXT NOT NULL);";
	private static final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_METADATA;
	
	private MetadataTable() {}
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE);
		Log.i(TAG, "Table " + TABLE_METADATA + " created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading table " + TABLE_METADATA + " from version " + oldVersion + " to version " + newVersion);
		database.execSQL(DATABASE_DROP_TABLE);
		Log.i(TAG, "Table " + TABLE_METADATA + " dropped");
		onCreate(database);
	}
}
