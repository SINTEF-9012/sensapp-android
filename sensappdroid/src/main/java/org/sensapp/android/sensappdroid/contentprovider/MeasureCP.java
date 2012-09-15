package org.sensapp.android.sensappdroid.contentprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.database.MeasureTable;
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
import android.util.Log;

public class MeasureCP extends TableContentProvider {
	
	protected static final String BASE_PATH = "measures";
	
	private static final int MEASURES = 10;
	private static final int MEASURE_ID = 20;
	private static final int MEASURE_SENSOR = 30;
	private static final int MEASURE_COMPOSITE = 40;
	private static final UriMatcher measureURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, MEASURES);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/#", MEASURE_ID);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/composite/*", MEASURE_COMPOSITE);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/*", MEASURE_SENSOR);
	}
	
	public MeasureCP(Context context, SensAppDatabaseHelper database) {
		super(context, database);
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int uid) throws IllegalStateException {
		checkColumns(projection);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MeasureTable.TABLE_MEASURE);
		switch (measureURIMatcher.match(uri)) {
		case MEASURES:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			break;
		case MEASURE_ID:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			queryBuilder.appendWhere(MeasureTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case MEASURE_SENSOR:
			queryBuilder.appendWhere(MeasureTable.COLUMN_SENSOR + "= \"" + uri.getLastPathSegment() + "\"");
			break;
		case MEASURE_COMPOSITE:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
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
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	
	public class IconHolder {
		String sensor;
		byte[] icon;
	}
	
	private static IconHolder holder;
	
	private byte[] retrieveIcon(String sensor) {
		Log.e("DEBUG", "__NEW_QUERY__");
		Cursor cursor = getContext().getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + sensor), new String[]{SensorTable.COLUMN_ICON}, null, null, null);
		byte[] icon = null;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				icon = cursor.getBlob(cursor.getColumnIndex(SensorTable.COLUMN_ICON));
			}
			cursor.close();
		}
		return icon;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values, int uid) throws IllegalStateException {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		long id = 0;
		switch (measureURIMatcher.match(uri)) {
		case MEASURES:
			if (!isSensAppUID(uid) && getSensorUID(values.getAsString(MeasureTable.COLUMN_SENSOR)) != uid) {
				throw new IllegalStateException("Forbiden insertion");
			}
			values.put(MeasureTable.COLUMN_UPLOADED, 0);
			// Store the sensor icon
			String sensor = (String) values.get(MeasureTable.COLUMN_SENSOR);
			if (holder == null) {
				Log.e("DEBUG", "New holder");
				holder = new IconHolder();
				holder.sensor = sensor;
				holder.icon = retrieveIcon(holder.sensor);
			} else if (!holder.sensor.equals(sensor)) {
				Log.e("DEBUG", "Maj holder");
				holder.sensor = sensor;
				holder.icon = retrieveIcon(holder.sensor);
			} else {
				Log.e("DEBUG", "__NO_COMPUTATION__ !!");
			}
			if (holder.icon != null) {
				values.put(MeasureTable.COLUMN_ICON, holder.icon);
			}
			id = db.insert(MeasureTable.TABLE_MEASURE, null, values);
			break;
		default:
			throw new IllegalArgumentException("Bad URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse("content://" + SensAppContract.AUTHORITY + "/" + BASE_PATH + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs, int uid) throws IllegalStateException {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		int rowsDeleted = 0;
		switch (measureURIMatcher.match(uri)) {
		case MEASURES:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, selection, selectionArgs);
			break;
		case MEASURE_ID:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		case MEASURE_SENSOR:
			String name = uri.getLastPathSegment();
			if (!isSensAppUID(uid) && getSensorUID(name) != uid) {
				throw new IllegalStateException("Forbiden uri");
			}
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_SENSOR + "= \"" + name + "\"", null);
			} else {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_SENSOR + "= \"" + name + "\" AND " + selection, selectionArgs);
			}
			break;
		case MEASURE_COMPOSITE:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			Cursor c = getContext().getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite/" + uri.getLastPathSegment()), new String[]{SensorTable.COLUMN_NAME}, null, null, null);
			ArrayList<String> names = new ArrayList<String>();
			if (c != null) {
					while (c.moveToNext()) {
						names.add(c.getString(c.getColumnIndexOrThrow(SensorTable.COLUMN_NAME)));
				}
				c.close();
			} else {
				return 0;
			}
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_SENSOR + " IN " + names.toString().replace("[", "(\"").replace(", ", "\", \"").replace("]", "\")"), null);
			} else {
				rowsDeleted = db.delete(MeasureTable.TABLE_MEASURE, MeasureTable.COLUMN_SENSOR + " IN " + names.toString().replace("[", "(\"").replace(", ", "\", \"").replace("]", "\")") + " AND " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs, int uid) {
		SQLiteDatabase db = getDatabase().getWritableDatabase();
		int rowsUpdated = 0;
		switch (measureURIMatcher.match(uri)) {
		case MEASURES:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, selection, selectionArgs);
			break;
		case MEASURE_ID:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		case MEASURE_SENSOR:
			String name = uri.getLastPathSegment();
			if (!isSensAppUID(uid) && getSensorUID(name) != uid) {
				throw new IllegalStateException("Forbiden uri");
			}
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_SENSOR + "= \"" + name + "\"", null);
			} else {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_SENSOR + "= \"" + name + "\" and " + selection, selectionArgs);
			}
			break;
		case MEASURE_COMPOSITE:
			if (!isSensAppUID(uid)) {
				throw new IllegalStateException("Forbiden uri");
			}
			Cursor c = getContext().getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite/" + uri.getLastPathSegment()), new String[]{SensorTable.COLUMN_NAME}, null, null, null);
			ArrayList<String> names = new ArrayList<String>();
			if (c != null) {
					while (c.moveToNext()) {
					names.add(c.getString(c.getColumnIndexOrThrow(SensorTable.COLUMN_NAME)));
				}
				c.close();
			} else {
				return 0;
			}
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_SENSOR + " IN " + names.toString().replace("[", "(\"").replace(", ", "\", \"").replace("]", "\")"), null);
			} else {
				rowsUpdated = db.update(MeasureTable.TABLE_MEASURE, values, MeasureTable.COLUMN_SENSOR + " IN " + names.toString().replace("[", "(\"").replace(", ", "\", \"").replace("]", "\")") + " AND " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("[2] Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	@Override
	protected void checkColumns(String[] projection) {
		String[] available = {MeasureTable.COLUMN_ID, MeasureTable.COLUMN_SENSOR, "DISTINCT " + MeasureTable.COLUMN_SENSOR, MeasureTable.COLUMN_VALUE, MeasureTable.COLUMN_TIME, MeasureTable.COLUMN_BASETIME, "DISTINCT " + MeasureTable.COLUMN_BASETIME, MeasureTable.COLUMN_UPLOADED, MeasureTable.COLUMN_ICON};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
