package net.modelbased.sensapp.android.sensappdroid.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SensorTable {

	public static final String TABLE_SENSOR = "SENSOR";
	public static final String COLUMN_NAME = "_id";
	public static final String COLUMN_URI = "uri";
	public static final String COLUMN_DESCRIPTION = "desc";
	public static final String COLUMN_BACKEND = "backend";
	public static final String COLUMN_TEMPLATE = "template";
	public static final String COLUMN_UNIT = "unit";
	public static final String COLUMN_UPLOADED = "uploaded";
	
	private static final String TAG = SensorTable.class.getSimpleName();
	private static final String DATABASE_CREATE_TABLE = "CREATE TABLE " 
			+ TABLE_SENSOR + " (" 
			+ COLUMN_NAME + " STRING PRIMARY KEY, "
			+ COLUMN_URI + " TEXT NOT NULL, "
			+ COLUMN_DESCRIPTION + " TEXT, "
			+ COLUMN_BACKEND + " TEXT NOT NULL, "
			+ COLUMN_TEMPLATE + " TEXT NOT NULL, "
			+ COLUMN_UNIT + " TEXT NOT NULL, "
			+ COLUMN_UPLOADED + " INTEGER NOT NULL);"; 
	private static final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_SENSOR;
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE);
		Log.i(TAG, "Table " + TABLE_SENSOR + " created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading table " + TABLE_SENSOR + " from version " + oldVersion + " to version " + newVersion);
		database.execSQL(DATABASE_DROP_TABLE);
		Log.i(TAG, "Table " + TABLE_SENSOR + " dropped");
		onCreate(database);
	}
}
