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
package org.sensapp.android.sensappdroid.contentprovider;

import org.sensapp.android.sensappdroid.database.SensAppDatabaseHelper;
import org.sensapp.android.sensappdroid.database.SensorTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public abstract class TableContentProvider {

	private SensAppDatabaseHelper database;
	private Context context;
	
	public TableContentProvider(Context context, SensAppDatabaseHelper database) {
		this.context = context;
		this.database = database;
	}
	
	protected Context getContext() {
		return context;
	}
	
	protected SensAppDatabaseHelper getDatabase() {
		return database;
	}
	
	protected boolean isSensAppUID(int uid) {
		if (uid != 0) {
			for (String name : getContext().getPackageManager().getPackagesForUid(uid)) {
				if ("org.sensapp.android.sensappdroid".equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected int getSensorUID(String name) {
		Cursor c = getContext().getContentResolver().query(Uri.parse("content://" + SensAppContentProvider.AUTHORITY + "/" + SensorCP.BASE_PATH + "/uid/" + name), null, null, null, null);
		int uid = 0;
		if (c != null) {
			if (c.moveToFirst()) {
				uid = c.getInt(c.getColumnIndex(SensorTable.COLUMN_CLIENT_UID));
			}
			c.close();
		}
		return uid;
	}
	
	abstract public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int uid) throws IllegalStateException;
	abstract public Uri insert(Uri uri, ContentValues values, int uid);
	abstract public int delete(Uri uri, String selection, String[] selectionArgs, int uid) throws IllegalStateException;
	abstract public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs, int uid) throws IllegalStateException;
	
	abstract protected void checkColumns(String[] projection);
	
	public String getType(Uri uri) {
		return null;
	}
}
