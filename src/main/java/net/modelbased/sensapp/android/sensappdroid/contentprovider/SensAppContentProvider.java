package net.modelbased.sensapp.android.sensappdroid.contentprovider;

import net.modelbased.sensapp.android.sensappdroid.database.SensAppDatabaseHelper;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class SensAppContentProvider extends ContentProvider {

	protected static final String AUTHORITY = "net.modelbased.sensapp.android.sensappdroid.contentprovider";
	
	private static final int MEASURE = 10;
	private static final UriMatcher sensAppURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sensAppURIMatcher.addURI(AUTHORITY, "measures", MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, "measures" + "/#", MEASURE);
	}

	private MeasureCP measureCP;
	
	@Override
	public boolean onCreate() {
		SensAppDatabaseHelper database = new SensAppDatabaseHelper(getContext());
		measureCP = new MeasureCP(getContext(), database);
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.query(uri, projection, selection, selectionArgs, sortOrder);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.getType(uri);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.update(uri, values, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.insert(uri, values);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.delete(uri, selection, selectionArgs);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
}
