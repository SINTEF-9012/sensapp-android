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

public final class CompositeTable {

	public static final String TABLE_COMPOSITE = "COMPOSITE";
	public static final String COLUMN_NAME = "_id";
	public static final String COLUMN_DESCRIPTION = "desc";
	public static final String COLUMN_URI = "uri";
	
	private static final String TAG = CompositeTable.class.getSimpleName();
	private static final String DATABASE_CREATE_TABLE = "CREATE TABLE " 
			+ TABLE_COMPOSITE + " (" 
			+ COLUMN_NAME + " TEXT PRIMARY KEY, "
			+ COLUMN_DESCRIPTION + " TEXT, "
			+ COLUMN_URI + " TEXT);";
	private static final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_COMPOSITE;
	
	private CompositeTable() {}
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE);
		Log.i(TAG, "Table " + TABLE_COMPOSITE + " created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading table " + TABLE_COMPOSITE + " from version " + oldVersion + " to version " + newVersion);
		database.execSQL(DATABASE_DROP_TABLE);
		Log.i(TAG, "Table " + TABLE_COMPOSITE + " dropped");
		onCreate(database);
	}
}
