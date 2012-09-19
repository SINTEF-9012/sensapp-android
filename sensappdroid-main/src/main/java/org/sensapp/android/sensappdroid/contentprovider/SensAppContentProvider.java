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

import org.sensapp.android.sensappdroid.database.MeasureTable;
import org.sensapp.android.sensappdroid.database.SensAppDatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;

public class SensAppContentProvider extends ContentProvider {

	protected static final String AUTHORITY = "org.sensapp.android.sensappdroid.contentprovider";
	
	private static final int SENSOR = 10;
	private static final int MEASURE = 20;
	private static final int COMPOSITE = 30;
	private static final int COMPOSE = 40;
	
	private static final UriMatcher sensAppURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sensAppURIMatcher.addURI(AUTHORITY, SensorCP.BASE_PATH, SENSOR);
		sensAppURIMatcher.addURI(AUTHORITY, SensorCP.BASE_PATH + "/composite/*", SENSOR);
		sensAppURIMatcher.addURI(AUTHORITY, SensorCP.BASE_PATH + "/uid/*", SENSOR);
		sensAppURIMatcher.addURI(AUTHORITY, SensorCP.BASE_PATH + "/*", SENSOR);
		sensAppURIMatcher.addURI(AUTHORITY, MeasureCP.BASE_PATH, MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, MeasureCP.BASE_PATH + "/#", MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, MeasureCP.BASE_PATH + "/composite/*", MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, MeasureCP.BASE_PATH + "/*", MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, CompositeCP.BASE_PATH, COMPOSITE);
		sensAppURIMatcher.addURI(AUTHORITY, CompositeCP.BASE_PATH + "/managesensors/*", COMPOSITE);
		sensAppURIMatcher.addURI(AUTHORITY, CompositeCP.BASE_PATH + "/sensor/*", COMPOSITE);
		sensAppURIMatcher.addURI(AUTHORITY, CompositeCP.BASE_PATH + "/*", COMPOSITE);
		sensAppURIMatcher.addURI(AUTHORITY, ComposeCP.BASE_PATH, COMPOSE);
		sensAppURIMatcher.addURI(AUTHORITY, ComposeCP.BASE_PATH + "/#", COMPOSE);
	}

	private MeasureCP measureCP;
	private SensorCP sensorCP;
	private CompositeCP compositeCP;
	private ComposeCP composeCP;
	private SensAppDatabaseHelper databaseHelper;
	
	@Override
	public boolean onCreate() {
		databaseHelper = new SensAppDatabaseHelper(getContext());
		measureCP = new MeasureCP(getContext(), databaseHelper);
		sensorCP = new SensorCP(getContext(), databaseHelper);
		compositeCP = new CompositeCP(getContext(), databaseHelper);
		composeCP = new ComposeCP(getContext(), databaseHelper);
		return false;
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
		if (sensAppURIMatcher.match(uri) == MEASURE) {
			sqlDB.beginTransaction();
			try {
				for (ContentValues cv : values) {
					cv.put(MeasureTable.COLUMN_UPLOADED, 0);
					long newID = sqlDB.insertOrThrow(MeasureTable.TABLE_MEASURE, null, cv);
					if (newID <= 0) {
						throw new SQLException("Failed to insert row into " + uri);
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
			return super.bulkInsert(uri, values);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int uid = Binder.getCallingUid();
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.query(uri, projection, selection, selectionArgs, sortOrder, uid);
		case SENSOR:
			return sensorCP.query(uri, projection, selection, selectionArgs, sortOrder, uid);
		case COMPOSITE:
			return compositeCP.query(uri, projection, selection, selectionArgs, sortOrder);
		case COMPOSE:
			return composeCP.query(uri, projection, selection, selectionArgs, sortOrder);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.getType(uri);
		case SENSOR:
			return sensorCP.getType(uri);
		case COMPOSITE:
			return compositeCP.getType(uri);
		case COMPOSE:
			return composeCP.getType(uri);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uid = Binder.getCallingUid();
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.update(uri, values, selection, selectionArgs, uid);
		case SENSOR:
			return sensorCP.update(uri, values, selection, selectionArgs, uid);
		case COMPOSITE:
			return compositeCP.update(uri, values, selection, selectionArgs);
		case COMPOSE:
			return composeCP.update(uri, values, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uid = Binder.getCallingUid();
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.insert(uri, values, uid);
		case SENSOR:
			return sensorCP.insert(uri, values, uid);
		case COMPOSITE:
			return compositeCP.insert(uri, values);
		case COMPOSE:
			return composeCP.insert(uri, values);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uid = Binder.getCallingUid();
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.delete(uri, selection, selectionArgs, uid);
		case SENSOR:
			return sensorCP.delete(uri, selection, selectionArgs, uid);
		case COMPOSITE:
			return compositeCP.delete(uri, selection, selectionArgs);
		case COMPOSE:
			return composeCP.delete(uri, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
}
