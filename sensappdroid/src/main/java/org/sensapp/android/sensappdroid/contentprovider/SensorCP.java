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

public class SensorCP {
	
	protected static final String BASE_PATH = "sensors";
	
	private static final int SENSORS = 10;
	private static final int COMPOSITE_SENSORS = 20;
	private static final int SENSOR_ID = 30;
	private static final UriMatcher measureURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, SENSORS);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/composite/*", COMPOSITE_SENSORS);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/*", SENSOR_ID);
	}

	private SensAppDatabaseHelper database;
	private Context context;
	
	public SensorCP(Context context, SensAppDatabaseHelper database) {
		this.context = context;
		this.database = database;
	}
	
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		checkColumns(projection);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch (measureURIMatcher.match(uri)) {
		case SENSORS:
			queryBuilder.setTables(SensorTable.TABLE_SENSOR);
			break;
		case COMPOSITE_SENSORS:
			Hashtable<String, String> columnMap = new Hashtable<String, String>();
			columnMap.put(SensorTable.COLUMN_NAME, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_NAME);
			columnMap.put(SensorTable.COLUMN_BACKEND, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_BACKEND);
			columnMap.put(SensorTable.COLUMN_DESCRIPTION, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_DESCRIPTION);
			columnMap.put(SensorTable.COLUMN_TEMPLATE, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_TEMPLATE);
			columnMap.put(SensorTable.COLUMN_UNIT, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_UNIT);
			columnMap.put(SensorTable.COLUMN_UPLOADED, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_UPLOADED);
			columnMap.put(SensorTable.COLUMN_URI, SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_URI);
			queryBuilder.setProjectionMap(columnMap);
			queryBuilder.setTables(SensorTable.TABLE_SENSOR + ", " + ComposeTable.TABLE_COMPOSE);
			queryBuilder.appendWhere(SensorTable.TABLE_SENSOR + "." + SensorTable.COLUMN_NAME + " = " + ComposeTable.COLUMN_SENSOR
					 + " AND " + ComposeTable.COLUMN_COMPOSITE + " = \"" + uri.getLastPathSegment() + "\"");
			break;
		case SENSOR_ID:
			queryBuilder.setTables(SensorTable.TABLE_SENSOR);
			queryBuilder.appendWhere(SensorTable.COLUMN_NAME + " = \"" + uri.getLastPathSegment() + "\"");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(context.getContentResolver(), uri);
		return cursor;
	}

	public String getType(Uri uri) {
		return null;
	}
	
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = database.getWritableDatabase();
		long id = 0;
		switch (measureURIMatcher.match(uri)) {
		case SENSORS:
			values.put(SensorTable.COLUMN_UPLOADED, 0);
			id = db.insert(SensorTable.TABLE_SENSOR, null, values);
			break;
		default:
			throw new IllegalArgumentException("Bad URI: " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}
	
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (measureURIMatcher.match(uri)) {
		case SENSORS:
			rowsDeleted = db.delete(SensorTable.TABLE_SENSOR, selection, selectionArgs);
			// Clean composites
			db.delete(ComposeTable.TABLE_COMPOSE, null, null);
			break;
		case SENSOR_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(SensorTable.TABLE_SENSOR, SensorTable.COLUMN_NAME + " = \"" + id + "\"", null);
			} else {
				rowsDeleted = db.delete(SensorTable.TABLE_SENSOR, SensorTable.COLUMN_NAME + " = \"" + id + "\" and " + selection, selectionArgs);
			}
			// Clean composites
			db.delete(ComposeTable.TABLE_COMPOSE, ComposeTable.COLUMN_SENSOR + " = \"" + id + "\"", null);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		context.getContentResolver().notifyChange(Uri.parse("content://" + SensAppContentProvider.AUTHORITY + "/" + BASE_PATH + "/composite"), null);
		return rowsDeleted;
	}

	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (measureURIMatcher.match(uri)) {
		case SENSORS:
			rowsUpdated = db.update(SensorTable.TABLE_SENSOR, values, selection, selectionArgs);
			break;
		case SENSOR_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(SensorTable.TABLE_SENSOR, values, SensorTable.COLUMN_NAME + " = \"" + id + "\"", null);
			} else {
				rowsUpdated = db.update(SensorTable.TABLE_SENSOR, values, SensorTable.COLUMN_NAME + " = \"" + id + "\" and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = {SensorTable.COLUMN_NAME, SensorTable.COLUMN_URI, SensorTable.COLUMN_DESCRIPTION,SensorTable.COLUMN_BACKEND, SensorTable.COLUMN_UNIT, SensorTable.COLUMN_TEMPLATE, SensorTable.COLUMN_UPLOADED, SensorTable.COLUMN_ICON};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
