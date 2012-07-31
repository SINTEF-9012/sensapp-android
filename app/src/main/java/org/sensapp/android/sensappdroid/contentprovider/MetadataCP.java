package org.sensapp.android.sensappdroid.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import org.sensapp.android.sensappdroid.database.MetadataTable;
import org.sensapp.android.sensappdroid.database.SensAppDatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MetadataCP {
	
	protected static final String BASE_PATH = "metadata";
	
	private static final int METADATA = 10;
	private static final int METADATA_ID = 20;
	private static final UriMatcher measureURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, METADATA);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/#", METADATA_ID);
	}

	private SensAppDatabaseHelper database;
	private Context context;
	
	public MetadataCP(Context context, SensAppDatabaseHelper database) {
		this.context = context;
		this.database = database;
	}
	
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		checkColumns(projection);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(MetadataTable.TABLE_METADATA);
		switch (measureURIMatcher.match(uri)) {
		case METADATA:
			break;
		case METADATA_ID:
			queryBuilder.appendWhere(MetadataTable.COLUMN_ID + "=" + uri.getLastPathSegment());
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
		case METADATA:
			id = db.insert(MetadataTable.TABLE_METADATA, null, values);
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
		case METADATA:
			rowsDeleted = db.delete(MetadataTable.TABLE_METADATA, selection, selectionArgs);
			break;
		case METADATA_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(MetadataTable.TABLE_METADATA, MetadataTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(MetadataTable.TABLE_METADATA, MetadataTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
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
		case METADATA:
			rowsUpdated = db.update(MetadataTable.TABLE_METADATA, values, selection, selectionArgs);
			break;
		case METADATA_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(MetadataTable.TABLE_METADATA, values, MetadataTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(MetadataTable.TABLE_METADATA, values, MetadataTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("[2] Unknown URI: " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = {MetadataTable.COLUMN_ID, MetadataTable.COLUMN_SENSOR, MetadataTable.COLUMN_KEY, MetadataTable.COLUMN_VALUE};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
