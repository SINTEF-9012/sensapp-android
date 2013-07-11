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
import org.sensapp.android.sensappdroid.database.GraphGroupDatabaseHelper;
import org.sensapp.android.sensappdroid.database.GraphGroupTable;

import java.util.Arrays;
import java.util.HashSet;

public class GraphGroupCP extends TableContentProvider {

	protected static final String BASE_PATH = "graphs";

	private static final int GRAPHS = 10;
	private static final int GRAPH_ID = 20;
	private static final int GRAPH_TITLE = 30;
	private static final UriMatcher graphURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		graphURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH, GRAPHS);
		graphURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/#", GRAPH_ID);
		graphURIMatcher.addURI(SensAppContentProvider.AUTHORITY, BASE_PATH + "/title/*", GRAPH_TITLE);
	}

	public GraphGroupCP(Context context, GraphGroupDatabaseHelper database) {
		super(context, database);
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int uid) throws IllegalStateException {
		checkColumns(projection);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(GraphGroupTable.TABLE_GRAPHGROUP);
        if (!isSensAppUID(uid)) {
            throw new SensAppProviderException("Forbidden URI: " + uri);
        }
		switch (graphURIMatcher.match(uri)) {
		case GRAPHS:
			break;
		case GRAPH_ID:
			queryBuilder.appendWhere(GraphGroupTable.COLUMN_ID + "=" + uri.getLastPathSegment());
            break;
		case GRAPH_TITLE:
			queryBuilder.appendWhere(GraphGroupTable.COLUMN_TITLE + "= \"" + uri.getLastPathSegment() + "\"");
			break;
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
		switch (graphURIMatcher.match(uri)) {
		case GRAPHS:
			id = db.insert(GraphGroupTable.TABLE_GRAPHGROUP, null, values);
			break;
		default:
			throw new SensAppProviderException("Unknown insert URI: " + uri);
        }
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

		switch (graphURIMatcher.match(uri)) {
		case GRAPHS:
			rowsDeleted = db.delete(GraphGroupTable.TABLE_GRAPHGROUP, selection, selectionArgs);
			break;
		case GRAPH_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(GraphGroupTable.TABLE_GRAPHGROUP, GraphGroupTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(GraphGroupTable.TABLE_GRAPHGROUP, GraphGroupTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		case GRAPH_TITLE:
			String name = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(GraphGroupTable.TABLE_GRAPHGROUP, GraphGroupTable.COLUMN_TITLE + "= \"" + name + "\"", null);
			} else {
				rowsDeleted = db.delete(GraphGroupTable.TABLE_GRAPHGROUP, GraphGroupTable.COLUMN_TITLE + "= \"" + name + "\" AND " + selection, selectionArgs);
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
		switch (graphURIMatcher.match(uri)) {
		case GRAPHS:
			rowsUpdated = db.update(GraphGroupTable.TABLE_GRAPHGROUP, values, selection, selectionArgs);
			break;
		case GRAPH_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(GraphGroupTable.TABLE_GRAPHGROUP, values, GraphGroupTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(GraphGroupTable.TABLE_GRAPHGROUP, values, GraphGroupTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		case GRAPH_TITLE:
			String name = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(GraphGroupTable.TABLE_GRAPHGROUP, values, GraphGroupTable.COLUMN_TITLE + "= \"" + name + "\"", null);
			} else {
				rowsUpdated = db.update(GraphGroupTable.TABLE_GRAPHGROUP, values, GraphGroupTable.COLUMN_TITLE + "= \"" + name + "\" and " + selection, selectionArgs);
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
		String[] available = {GraphGroupTable.COLUMN_ID, GraphGroupTable.COLUMN_TITLE};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
