package org.sensapp.android.sensappdroid.contentprovider;

import org.sensapp.android.sensappdroid.database.ComposeTable;
import org.sensapp.android.sensappdroid.database.CompositeTable;
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

public class ComposeCP {
	
	protected static final String BASE_PATH = "composes";
	
	private static final int COMPOSES = 10;
	private static final int COMPOSE = 20;
	private static final int COMPOSE_SENSOR = 30;
	private static final int COMPOSE_COMPOSITE = 40;
	private static final UriMatcher measureURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, COMPOSES);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/#", COMPOSE);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/sensor/*", COMPOSE_SENSOR);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/composite/*", COMPOSE_COMPOSITE);
	}

	private SensAppDatabaseHelper database;
	private Context context;
	
	public ComposeCP(Context context, SensAppDatabaseHelper database) {
		this.context = context;
		this.database = database;
	}
	
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = database.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch (measureURIMatcher.match(uri)) {
		case COMPOSES:
			queryBuilder.setTables(ComposeTable.TABLE_COMPOSE);
			break;
		case COMPOSE:
			queryBuilder.setTables(ComposeTable.TABLE_COMPOSE);
			queryBuilder.appendWhere(ComposeTable.COLUMN_ID + " = " + uri.getLastPathSegment());
			break;
		case COMPOSE_SENSOR:
			//db.rawQuery("SELECT ", selectionArgs)
			queryBuilder.setTables(ComposeTable.TABLE_COMPOSE + ", " + CompositeTable.TABLE_COMPOSITE);
			queryBuilder.appendWhere(ComposeTable.COLUMN_SENSOR + " = \"" + uri.getLastPathSegment() + "\" AND " + ComposeTable.COLUMN_COMPOSITE + " = " + CompositeTable.COLUMN_NAME);
			break;
		case COMPOSE_COMPOSITE:
			queryBuilder.setTables(ComposeTable.TABLE_COMPOSE + ", " + SensorTable.TABLE_SENSOR);
			queryBuilder.appendWhere(ComposeTable.COLUMN_COMPOSITE + " = \"" + uri.getLastPathSegment() + "\" AND " + ComposeTable.COLUMN_SENSOR + " = " + SensorTable.COLUMN_NAME);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
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
		case COMPOSES:
			id = db.insert(ComposeTable.TABLE_COMPOSE, null, values);
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
		case COMPOSES:
			rowsDeleted = db.delete(ComposeTable.TABLE_COMPOSE, selection, selectionArgs);
			break;
		case COMPOSE:
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(ComposeTable.TABLE_COMPOSE, ComposeTable.COLUMN_ID + " = " + uri.getLastPathSegment(), null);
			} else {
				rowsDeleted = db.delete(ComposeTable.TABLE_COMPOSE, ComposeTable.COLUMN_ID + " = " + uri.getLastPathSegment() + " and " + selection, selectionArgs);
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
		case COMPOSES:
			rowsUpdated = db.update(ComposeTable.TABLE_COMPOSE, values, selection, selectionArgs);
			break;
		case COMPOSE:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(ComposeTable.TABLE_COMPOSE, values, MeasureTable.COLUMN_ID + " = " + uri.getLastPathSegment(), null);
			} else {
				rowsUpdated = db.update(ComposeTable.TABLE_COMPOSE, values, MeasureTable.COLUMN_ID + " = " + uri.getLastPathSegment() + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("[2] Unknown URI: " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
}
