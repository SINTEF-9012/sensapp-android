package org.sensapp.android.sensappdroid.contentprovider;

import org.sensapp.android.sensappdroid.database.SensAppDatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;

public class SensAppContentProvider extends ContentProvider {

	protected static final String AUTHORITY = "org.sensapp.android.sensappdroid.contentprovider";
	
	private static final int SENSOR = 10;
	private static final int MEASURE = 20;
	private static final int COMPOSITE = 30;
	private static final int COMPOSE = 40;
	
	private static final UriMatcher sensAppURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sensAppURIMatcher.addURI(AUTHORITY, SensorCP.BASE_PATH, SENSOR);
		sensAppURIMatcher.addURI(AUTHORITY, SensorCP.BASE_PATH + "/composite/*", SENSOR);
		sensAppURIMatcher.addURI(AUTHORITY, SensorCP.BASE_PATH + "/*", SENSOR);
		sensAppURIMatcher.addURI(AUTHORITY, MeasureCP.BASE_PATH, MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, MeasureCP.BASE_PATH + "/#", MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, MeasureCP.BASE_PATH + "/composite/*", MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, MeasureCP.BASE_PATH + "/*", MEASURE);
		sensAppURIMatcher.addURI(AUTHORITY, CompositeCP.BASE_PATH, COMPOSITE);
		sensAppURIMatcher.addURI(AUTHORITY, CompositeCP.BASE_PATH + "/managesensors/*", COMPOSITE);
		sensAppURIMatcher.addURI(AUTHORITY, CompositeCP.BASE_PATH + "/sensor/*", COMPOSITE);
		sensAppURIMatcher.addURI(AUTHORITY, CompositeCP.BASE_PATH + "/*", COMPOSITE);
		sensAppURIMatcher.addURI(AUTHORITY, ComposeCP.BASE_PATH, COMPOSE);
		sensAppURIMatcher.addURI(AUTHORITY, ComposeCP.BASE_PATH + "/#", COMPOSE);
	}

	private MeasureCP measureCP;
	private SensorCP sensorCP;
	private CompositeCP compositeCP;
	private ComposeCP composeCP;
	
	@Override
	public boolean onCreate() {
		SensAppDatabaseHelper database = new SensAppDatabaseHelper(getContext());
		measureCP = new MeasureCP(getContext(), database);
		sensorCP = new SensorCP(getContext(), database);
		compositeCP = new CompositeCP(getContext(), database);
		composeCP = new ComposeCP(getContext(), database);
		return false;
	}
	
	private String[] getCallingPackages() {
	     int caller = Binder.getCallingUid();
	     if (caller == 0) {
	         return null;
	     }
	     return getContext().getPackageManager().getPackagesForUid(caller);
	 }
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.query(uri, projection, selection, selectionArgs, sortOrder);
		case SENSOR:
			return sensorCP.query(uri, projection, selection, selectionArgs, sortOrder);
		case COMPOSITE:
			return compositeCP.query(uri, projection, selection, selectionArgs, sortOrder);
		case COMPOSE:
			return composeCP.query(uri, projection, selection, selectionArgs, sortOrder);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.getType(uri);
		case SENSOR:
			return sensorCP.getType(uri);
		case COMPOSITE:
			return compositeCP.getType(uri);
		case COMPOSE:
			return composeCP.getType(uri);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		boolean permission = false;
		for (String s : getCallingPackages()) {
			if ("org.sensapp.android.sensappdroid".equals(s)) {
				permission = true;
			}
		}
		if (permission) {
			switch (sensAppURIMatcher.match(uri)) {
			case MEASURE:
				return measureCP.update(uri, values, selection, selectionArgs);
			case SENSOR:
				return sensorCP.update(uri, values, selection, selectionArgs);
			case COMPOSITE:
				return compositeCP.update(uri, values, selection, selectionArgs);
			case COMPOSE:
				return composeCP.update(uri, values, selection, selectionArgs);
			default:
				throw new IllegalArgumentException("[0] Unknown URI: " + uri);
			}
		} else {
			throw new IllegalAccessError("Update requests are forbiden");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (sensAppURIMatcher.match(uri)) {
		case MEASURE:
			return measureCP.insert(uri, values);
		case SENSOR:
			return sensorCP.insert(uri, values);
		case COMPOSITE:
			return compositeCP.insert(uri, values);
		case COMPOSE:
			return composeCP.insert(uri, values);
		default:
			throw new IllegalArgumentException("[0] Unknown URI: " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		boolean permission = false;
		for (String s : getCallingPackages()) {
			if ("org.sensapp.android.sensappdroid".equals(s)) {
				permission = true;
			}
		}
		if (permission) {
			switch (sensAppURIMatcher.match(uri)) {
			case MEASURE:
				return measureCP.delete(uri, selection, selectionArgs);
			case SENSOR:
				return sensorCP.delete(uri, selection, selectionArgs);
			case COMPOSITE:
				return compositeCP.delete(uri, selection, selectionArgs);
			case COMPOSE:
				return composeCP.delete(uri, selection, selectionArgs);
			default:
				throw new IllegalArgumentException("[0] Unknown URI: " + uri);
			}
		} else {
			throw new IllegalAccessError("Delete requests are forbiden");
		}
	}
}
