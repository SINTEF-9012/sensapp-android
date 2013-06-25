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

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import org.sensapp.android.sensappdroid.database.*;
import org.sensapp.android.sensappdroid.database.GraphSensorTable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

public class GraphSensorCP extends TableContentProvider {

	protected static final String BASE_PATH = "graphsensors";

	private static final int SENSORS = 10;
	private static final int SENSOR_ID = 20;
	private static final int SENSOR_TITLE = 30;
    private static final int GRAPH = 40;
	private static final UriMatcher SensorURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		SensorURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, SENSORS);
		SensorURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/#", SENSOR_ID);
		SensorURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/title/*", SENSOR_TITLE);
        SensorURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/graph/*", GRAPH);
	}

	public GraphSensorCP(Context context, GraphGroupDatabaseHelper database) {
		super(context, database);
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int uid) throws IllegalStateException {
		checkColumns(projection);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(GraphSensorTable.TABLE_GRAPHSENSOR);
        if (!isSensAppUID(uid)) {
            throw new SensAppProviderException("Forbidden URI: " + uri);
        }
		switch (SensorURIMatcher.match(uri)) {
		case SENSORS:
			break;
		case SENSOR_ID:
			queryBuilder.appendWhere(GraphSensorTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case SENSOR_TITLE:
			queryBuilder.appendWhere(GraphSensorTable.COLUMN_TITLE + "= \"" + uri.getLastPathSegment() + "\"");
			break;
        case GRAPH:
            /*Hashtable<String, String> columnMap = new Hashtable<String, String>();
            columnMap.put(GraphSensorTable.COLUMN_ID, GraphSensorTable.TABLE_GRAPHSENSOR + "." + GraphSensorTable.COLUMN_ID);
            columnMap.put(GraphSensorTable.COLUMN_TITLE, GraphSensorTable.TABLE_GRAPHSENSOR + "." + GraphSensorTable.COLUMN_TITLE);
            columnMap.put(GraphSensorTable.COLUMN_STYLE, GraphSensorTable.TABLE_GRAPHSENSOR + "." + GraphSensorTable.COLUMN_STYLE);
            columnMap.put(GraphSensorTable.COLUMN_COLOR, GraphSensorTable.TABLE_GRAPHSENSOR + "." + GraphSensorTable.COLUMN_COLOR);
            columnMap.put(GraphSensorTable.COLUMN_HIGHESTVALUE, GraphSensorTable.TABLE_GRAPHSENSOR + "." + GraphSensorTable.COLUMN_HIGHESTVALUE);
            columnMap.put(GraphSensorTable.COLUMN_LOWESTVALUE, GraphSensorTable.TABLE_GRAPHSENSOR + "." + GraphSensorTable.COLUMN_LOWESTVALUE);
            columnMap.put(GraphSensorTable.COLUMN_GRAPHGROUP, GraphSensorTable.TABLE_GRAPHSENSOR + "." + GraphSensorTable.COLUMN_GRAPHGROUP);
            columnMap.put(GraphSensorTable.COLUMN_SENSOR, GraphSensorTable.TABLE_GRAPHSENSOR + "." + GraphSensorTable.COLUMN_SENSOR);
            queryBuilder.setProjectionMap(columnMap);
            queryBuilder.setTables(GraphSensorTable.TABLE_GRAPHSENSOR);*/
            queryBuilder.appendWhere(/*GraphSensorTable.TABLE_GRAPHSENSOR + "." + */GraphSensorTable.COLUMN_GRAPHGROUP + " = \"" + uri.getLastPathSegment() + "\"");
            break;
		/*case SENSOR:
			Cursor c = getContext().getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite/" + uri.getLastPathSegment()), new String[]{SensorTable.COLUMN_NAME}, null, null, null);
			ArrayList<String> names = new ArrayList<String>();
			if (c != null) {
					while (c.moveToNext()) {
						names.add(c.getString(c.getColumnIndexOrThrow(SensorTable.COLUMN_NAME)));
				}
				c.close();
			} else {
				return null;
			}
			queryBuilder.appendWhere(MeasureTable.COLUMN_SENSOR + " IN " + names.toString().replace("[", "(\"").replace(", ", "\", \"").replace("]", "\")"));
			break;*/
		default:
			throw new SensAppProviderException("Unknown measure URI: " + uri);
		}
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values, int uid) throws IllegalStateException {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		long id = 0;
        if (!isSensAppUID(uid)) {
            throw new SensAppProviderException("Forbidden URI: " + uri);
        }
		switch (SensorURIMatcher.match(uri)) {
		case SENSORS:
			//values.put(MeasureTable.COLUMN_UPLOADED, 0);
			id = db.insert(GraphSensorTable.TABLE_GRAPHSENSOR, null, values);
            //GraphSensorTable.onCreate(db);
			break;
		default:
			throw new SensAppProviderException("Unknown insert URI: " + uri);
		}
		//getContext().getContentResolver().notifyChange(Uri.parse(uri + "/" + (String) values.get(MeasureTable.COLUMN_SENSOR)), null);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs, int uid) throws IllegalStateException {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		int rowsDeleted = 0;

        if (!isSensAppUID(uid)) {
            throw new SensAppProviderException("Forbidden URI: " + uri);
        }

		switch (SensorURIMatcher.match(uri)) {
		case SENSORS:
			rowsDeleted = db.delete(GraphSensorTable.TABLE_GRAPHSENSOR, selection, selectionArgs);
			break;
		case SENSOR_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(GraphSensorTable.TABLE_GRAPHSENSOR, GraphSensorTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(GraphSensorTable.TABLE_GRAPHSENSOR, GraphSensorTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		case SENSOR_TITLE:
			String name = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(GraphSensorTable.TABLE_GRAPHSENSOR, GraphSensorTable.COLUMN_TITLE + "= \"" + name + "\"", null);
			} else {
				rowsDeleted = db.delete(GraphSensorTable.TABLE_GRAPHSENSOR, GraphSensorTable.COLUMN_TITLE + "= \"" + name + "\" AND " + selection, selectionArgs);
			}
			break;
		default:
			throw new SensAppProviderException("Unknown delete URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs, int uid) {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		int rowsUpdated = 0;
        if (!isSensAppUID(uid)) {
            throw new SensAppProviderException("Forbidden URI: " + uri);
        }
		switch (SensorURIMatcher.match(uri)) {
		case SENSORS:
			rowsUpdated = db.update(GraphSensorTable.TABLE_GRAPHSENSOR, values, selection, selectionArgs);
			break;
		case SENSOR_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(GraphSensorTable.TABLE_GRAPHSENSOR, values, GraphSensorTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(GraphSensorTable.TABLE_GRAPHSENSOR, values, GraphSensorTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		case SENSOR_TITLE:
			String name = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(GraphSensorTable.TABLE_GRAPHSENSOR, values, GraphSensorTable.COLUMN_TITLE + "= \"" + name + "\"", null);
			} else {
				rowsUpdated = db.update(GraphSensorTable.TABLE_GRAPHSENSOR, values, GraphSensorTable.COLUMN_TITLE + "= \"" + name + "\" and " + selection, selectionArgs);
			}
			break;
		default:
			throw new SensAppProviderException("Unknown update URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	@Override
	protected void checkColumns(String[] projection) {
		String[] available = {GraphSensorTable.COLUMN_ID, GraphSensorTable.COLUMN_TITLE, GraphSensorTable.COLUMN_COLOR, GraphSensorTable.COLUMN_GRAPHGROUP, GraphSensorTable.COLUMN_SENSOR, GraphSensorTable.COLUMN_LOWESTVALUE, GraphSensorTable.COLUMN_HIGHESTVALUE, GraphSensorTable.COLUMN_STYLE};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
