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

import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.database.ComposeTable;
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

public class ComposeCP {
	
	protected static final String BASE_PATH = "composes";
	
	private static final int COMPOSES = 10;
	private static final int COMPOSE = 20;
	private static final UriMatcher measureURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, COMPOSES);
		measureURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/#", COMPOSE);
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
		context.getContentResolver().notifyChange(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite"), null);
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
		context.getContentResolver().notifyChange(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite"), null);
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
