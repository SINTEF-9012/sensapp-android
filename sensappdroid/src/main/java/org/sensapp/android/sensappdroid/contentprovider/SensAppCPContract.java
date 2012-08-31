package org.sensapp.android.sensappdroid.contentprovider;

import android.content.ContentResolver;
import android.net.Uri;

public final class SensAppCPContract {
	
	public static final String AUTHORITY = "org.sensapp.android.sensappdroid.contentprovider";
	
	public static class Measure {
		public static final String BASE_PATH = "measures";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/measures";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/measure";

		public static final String ID = "_id";
		public static final String SENSOR = "sensor";
		public static final String VALUE = "value";
		public static final String TIME = "time";
		public static final String BASETIME = "basetime";
		public static final String UPLOADED ="uploaded";
	}
	
	public static class Sensor {
		public static final String BASE_PATH = "sensors";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/sensors";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/sensor";
		
		public static final String NAME = "_id";
		public static final String URI = "uri";
		public static final String DESCRIPTION = "desc";
		public static final String BACKEND = "backend";
		public static final String TEMPLATE = "template";
		public static final String UNIT = "unit";
		public static final String UPLOADED = "uploaded";
	}
	
	public static class Composite {
		public static final String BASE_PATH = "composites";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/composites";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/composite";

		public static final String NAME = "_id";
		public static final String DESCRIPTION = "desc";
		public static final String URI = "uri";
	}
	
	public static class Compose {
		public static final String BASE_PATH = "composes";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/composes";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/compose";

		public static final String ID = "_id";
		public static final String COMPOSITE = "composite";
		public static final String SENSOR = "sensor";
	}
	
//	public static class Metadata {
//		public static final String BASE_PATH = "metadata";
//		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
//		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/metadata";
//		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/metadata_id";
//
//		public static final String ID = "_id";
//		public static final String SENSOR = "sensor";
//		public static final String KEY = "key";
//		public static final String VALUE = "value";
//	}
}
