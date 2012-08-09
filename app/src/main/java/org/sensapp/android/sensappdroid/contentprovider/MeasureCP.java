package org.sensapp.android.sensappdroid.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import org.sensapp.android.sensappdroid.database.MeasureTable;
import org.sensapp.android.sensappdroid.database.SensAppDatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MeasureCP {
	
	protected static final String BASE_PATH = "measures";
	
	private static final int MEASURES = 10;
	private static final int MEASURE_ID = 20;
	private static final int MEASURE_SENSOR = 30;
	private static final UriMatcher measureURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, MEASURES);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/#", MEASURE_ID);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/*", MEASURE_SENSOR);
	}

	private SensAppDatabaseHelper database;
	private Context context;
	
	public MeasureCP(Context context, SensAppDatabaseHelper database) {
		this.context = context;
		this.database = database;
	}
	
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		checkColumns(projection);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MeasureTable.TABLE_MEASURE);
		switch (measureURIMatcher.match(uri)) {
		case MEASURES:
			break;
		case MEASURE_ID:
			queryBuilder.appendWhere(MeasureTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case MEASURE_SENSOR:
			queryBuilder.appendWhere(MeasureTable.COLUMN_SENSOR + "= \"" + uri.getLastPathSegment() + "\"");
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
		case MEASURES:
			id = db.insert(MeasureTable.TABLE_MEASURE, null, values);
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
		case MEASURES:
			rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, selection, selectionArgs);
			break;
		case MEASURE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		case MEASURE_SENSOR:
			String name = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_SENSOR + "= \"" + name + "\"", null);
			} else {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_SENSOR + "= \"" + name + "\" and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (measureURIMatcher.match(uri)) {
		case MEASURES:
			rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, selection, selectionArgs);
			break;
		case MEASURE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		case MEASURE_SENSOR:
			String name = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_SENSOR + "= \"" + name + "\"", null);
			} else {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_SENSOR + "= \"" + name + "\" and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("[2] Unknown URI: " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = {MeasureTable.COLUMN_ID, MeasureTable.COLUMN_SENSOR, "DISTINCT " + MeasureTable.COLUMN_SENSOR, MeasureTable.COLUMN_VALUE, MeasureTable.COLUMN_TIME, MeasureTable.COLUMN_BASETIME, "DISTINCT " + MeasureTable.COLUMN_BASETIME, MeasureTable.COLUMN_UPLOADED};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
