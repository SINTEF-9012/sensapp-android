package net.modelbased.sensapp.android.sensappdroid.contentprovider;

import android.content.ContentResolver;
import android.net.Uri;

public final class SensAppMeasureProviderContract {
	
	public static final String AUTHORITY = "net.modelbased.sensapp.android.sensappdroid.contentprovider";
	public static final String BASE_PATH = "measures";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/measures";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/measure";
	
	public static final String ID = "_id";
	public static final String SENSOR = "sensor";
	public static final String VALUE = "value";
	public static final String TIME = "time";
	public static final String UPLOADED ="uploaded";
	
}
