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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import org.sensapp.android.sensappdroid.database.GraphGroupDatabaseHelper;
import org.sensapp.android.sensappdroid.database.MeasureTable;
import org.sensapp.android.sensappdroid.database.SensAppDatabaseHelper;

public class GraphContentProvider extends ContentProvider {

	protected static final String AUTHORITY = "org.sensapp.android.sensappdroid.contentprovider";
	
	private static final int GRAPH = 10;
	private static final int SENSOR = 20;
	
	private static final UriMatcher sensAppURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sensAppURIMatcher.addURI(AUTHORITY, GraphGroupCP.BASE_PATH, GRAPH);
		sensAppURIMatcher.addURI(AUTHORITY, GraphGroupCP.BASE_PATH + "/#", GRAPH);
		sensAppURIMatcher.addURI(AUTHORITY, GraphGroupCP.BASE_PATH + "/title/*", GRAPH);
		sensAppURIMatcher.addURI(AUTHORITY, GraphGroupCP.BASE_PATH + "/*", GRAPH);
        sensAppURIMatcher.addURI(AUTHORITY, GraphSensorCP.BASE_PATH, SENSOR);
        sensAppURIMatcher.addURI(AUTHORITY, GraphSensorCP.BASE_PATH + "/#", SENSOR);
        sensAppURIMatcher.addURI(AUTHORITY, GraphSensorCP.BASE_PATH + "/title/*", SENSOR);
        sensAppURIMatcher.addURI(AUTHORITY, GraphSensorCP.BASE_PATH + "/graph/*", SENSOR);
        sensAppURIMatcher.addURI(AUTHORITY, GraphSensorCP.BASE_PATH + "/*", SENSOR);
	}

	private GraphGroupCP graphCP;
    private GraphSensorCP graphSensorCP;
	private GraphGroupDatabaseHelper databaseHelper;

	@Override
	public boolean onCreate() {
		databaseHelper = new GraphGroupDatabaseHelper(getContext());
        graphCP = new GraphGroupCP(getContext(), databaseHelper);
		graphSensorCP = new GraphSensorCP(getContext(), databaseHelper);
		return true;
	}
	
	/*@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
		if (sensAppURIMatcher.match(uri) == MEASURE) {
			// Optimize measure insertions
			sqlDB.beginTransaction();
			try {
				for (ContentValues cv : values) {
					cv.put(MeasureTable.COLUMN_UPLOADED, 0);
					try {
						long newID = sqlDB.insertOrThrow(MeasureTable.TABLE_MEASURE, null, cv);
						if (newID <= 0) {
							throw new SensAppProviderException("Builk insert error at " + uri);
						}
					} catch (SQLException e) {
						throw new SensAppProviderException("Builk insert error at " + uri, e);
					}
				}
				sqlDB.setTransactionSuccessful();
				getContext().getContentResolver().notifyChange(uri, null);
				Log.d("SensAppProvider", "bulk insert: " + values.length);
			} finally {
				sqlDB.endTransaction();
			}
			return values.length;
		} else {
			// Keep the default implementation in other case
			return super.bulkInsert(uri, values);
		}
	}*/

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int uid = Binder.getCallingUid();
		switch (sensAppURIMatcher.match(uri)) {
        case GRAPH:
            return graphCP.query(uri, projection, selection, selectionArgs, sortOrder, uid);
		case SENSOR:
			return graphSensorCP.query(uri, projection, selection, selectionArgs, sortOrder, uid);
		default:
			throw new SensAppProviderException("Query error: unknown URI " + uri);
		}
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sensAppURIMatcher.match(uri)) {
        case GRAPH:
            return graphCP.getType(uri);
		case SENSOR:
			return graphSensorCP.getType(uri);
		default:
			throw new SensAppProviderException("getType error: unknown URI " + uri);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uid = Binder.getCallingUid();
		switch (sensAppURIMatcher.match(uri)) {
        case GRAPH:
            return graphCP.update(uri, values, selection, selectionArgs, uid);
		case SENSOR:
			return graphSensorCP.update(uri, values, selection, selectionArgs, uid);
		default:
			throw new SensAppProviderException("Update error: unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        Log.d("coucou", "GraphContentProvider Insert");
		int uid = Binder.getCallingUid();
		switch (sensAppURIMatcher.match(uri)) {
        case GRAPH:
            return graphCP.insert(uri, values, uid);
		case SENSOR:
			return graphSensorCP.insert(uri, values, uid);
		default:
			throw new SensAppProviderException("Insert error: unknown URI " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uid = Binder.getCallingUid();
		switch (sensAppURIMatcher.match(uri)) {
        case GRAPH:
            return graphCP.delete(uri, selection, selectionArgs, uid);
		case SENSOR:
			return graphSensorCP.delete(uri, selection, selectionArgs, uid);
		default:
			throw new SensAppProviderException("Delete error: unknown URI " + uri);
		}
	}
}
