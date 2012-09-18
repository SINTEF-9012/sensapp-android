package org.sensapp.android.sensappdroid.contentprovider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import org.sensapp.android.sensappdroid.database.ComposeTable;
import org.sensapp.android.sensappdroid.database.SensAppDatabaseHelper;
import org.sensapp.android.sensappdroid.database.SensorTable;


import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class SensorCP extends TableContentProvider {
	
	protected static final String BASE_PATH = "sensors";
	
	private static final int SENSORS = 10;
	private static final int COMPOSITE_SENSORS = 20;
	private static final int SENSOR_UID = 30;
	private static final int SENSOR_ID = 40;
	
	private static final UriMatcher sensorURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sensorURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, SENSORS);
		sensorURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/composite/*", COMPOSITE_SENSORS);
		sensorURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/uid/*", SENSOR_UID);
		sensorURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/*", SENSOR_ID);
	}
	
	public SensorCP(Context context, SensAppDatabaseHelper database) {
		super(context, database);
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int uid) throws IllegalStateException {
		checkColumns(projection);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch (sensorURIMatcher.match(uri)) {
		case SENSORS:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			queryBuilder.setTables(SensorTable.TABLE_SENSOR);
			break;
		case COMPOSITE_SENSORS:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			Hashtable<String, String> columnMap = new Hashtable<String, String>();
			columnMap.put(SensorTable.COLUMN_NAME, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_NAME);
			columnMap.put(SensorTable.COLUMN_BACKEND, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_BACKEND);
			columnMap.put(SensorTable.COLUMN_DESCRIPTION, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_DESCRIPTION);
			columnMap.put(SensorTable.COLUMN_TEMPLATE, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_TEMPLATE);
			columnMap.put(SensorTable.COLUMN_UNIT, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_UNIT);
			columnMap.put(SensorTable.COLUMN_UPLOADED, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_UPLOADED);
			columnMap.put(SensorTable.COLUMN_URI, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_URI);
			columnMap.put(SensorTable.COLUMN_ICON, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_ICON);
			columnMap.put(SensorTable.COLUMN_CLIENT_UID, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_CLIENT_UID);
			queryBuilder.setProjectionMap(columnMap);
			queryBuilder.setTables(SensorTable.TABLE_SENSOR + ", " + ComposeTable.TABLE_COMPOSE);
			queryBuilder.appendWhere(SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_NAME + " = " + ComposeTable.COLUMN_SENSOR
					 + " AND " + ComposeTable.COLUMN_COMPOSITE + " = \"" + uri.getLastPathSegment() + "\"");
			break;
		case SENSOR_UID:
			queryBuilder.setTables(SensorTable.TABLE_SENSOR);
			queryBuilder.appendWhere(SensorTable.COLUMN_NAME + " = \"" + uri.getLastPathSegment() + "\"");
			projection = new String[]{SensorTable.COLUMN_CLIENT_UID};
			selection = null;
			break;
		case SENSOR_ID:
			int sensorUID = getSensorUID(uri.getLastPathSegment());
			if (!isSensAppUID(uid) && sensorUID != 0 && sensorUID != uid) {
				throw new IllegalStateException("Forbiden uri");
			}
			queryBuilder.setTables(SensorTable.TABLE_SENSOR);
			queryBuilder.appendWhere(SensorTable.COLUMN_NAME + " = \"" + uri.getLastPathSegment() + "\"");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values, int uid) {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		long id = 0;
		switch (sensorURIMatcher.match(uri)) {
		case SENSORS:
			values.put(SensorTable.COLUMN_UPLOADED, 0);
			values.put(SensorTable.COLUMN_CLIENT_UID, uid);
			id = db.insert(SensorTable.TABLE_SENSOR, null, values);
			break;
		default:
			throw new IllegalArgumentException("Bad URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse("content://" + SensAppContentProvider.AUTHORITY + "/" + BASE_PATH + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs, int uid) throws IllegalStateException {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		int rowsDeleted = 0;
		switch (sensorURIMatcher.match(uri)) {
		case SENSORS:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			rowsDeleted = db.delete(SensorTable.TABLE_SENSOR, selection, selectionArgs);
			// Clean composites
			db.delete(ComposeTable.TABLE_COMPOSE, null, null);
			break;
		case SENSOR_ID:
			String name = uri.getLastPathSegment();
			if (!isSensAppUID(uid) && getSensorUID(name) != uid) {
				throw new IllegalStateException("Forbiden uri");
			}
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(SensorTable.TABLE_SENSOR, SensorTable.COLUMN_NAME + " = \"" + name + "\"", null);
			} else {
				rowsDeleted = db.delete(SensorTable.TABLE_SENSOR, SensorTable.COLUMN_NAME + " = \"" + name + "\" and " + selection, selectionArgs);
			}
			// Clean composites
			db.delete(ComposeTable.TABLE_COMPOSE, ComposeTable.COLUMN_SENSOR + " = \"" + name + "\"", null);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		getContext().getContentResolver().notifyChange(Uri.parse("content://" + SensAppContentProvider.AUTHORITY + "/" + BASE_PATH + "/composite"), null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs, int uid) throws IllegalStateException {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		int rowsUpdated = 0;
		switch (sensorURIMatcher.match(uri)) {
		case SENSORS:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			rowsUpdated = db.update(SensorTable.TABLE_SENSOR, values, selection, selectionArgs);
			break;
		case SENSOR_ID:
			String name = uri.getLastPathSegment();
			if (!isSensAppUID(uid) && getSensorUID(name) != uid) {
				throw new IllegalStateException("Forbiden uri");
			}
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(SensorTable.TABLE_SENSOR, values, SensorTable.COLUMN_NAME + " = \"" + name + "\"", null);
			} else {
				rowsUpdated = db.update(SensorTable.TABLE_SENSOR, values, SensorTable.COLUMN_NAME + " = \"" + name + "\" and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	@Override
	protected void checkColumns(String[] projection) {
		String[] available = {SensorTable.COLUMN_NAME, SensorTable.COLUMN_URI, SensorTable.COLUMN_DESCRIPTION,SensorTable.COLUMN_BACKEND, SensorTable.COLUMN_UNIT, SensorTable.COLUMN_TEMPLATE, SensorTable.COLUMN_UPLOADED, SensorTable.COLUMN_ICON, SensorTable.COLUMN_CLIENT_UID};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}