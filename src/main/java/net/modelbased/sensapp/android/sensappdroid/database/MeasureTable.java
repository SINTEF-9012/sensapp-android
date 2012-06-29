package net.modelbased.sensapp.android.sensappdroid.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MeasureTable {

	public static final String TABLE_MEASURE = "MEASURE";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SENSOR = "sensor";
	public static final String COLUMN_VALUE = "value";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_BASETIME = "basetime";
	public static final String COLUMN_UPLOADED ="uploaded";
	
	private static final String TAG = MeasureTable.class.getName();
	private static final String DATABASE_CREATE_TABLE = "CREATE TABLE " 
			+ TABLE_MEASURE + " (" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_SENSOR + " TEXT NOT NULL, "
			+ COLUMN_VALUE + " INTEGER NOT NULL, "
			+ COLUMN_TIME + " INTEGER NOT NULL, "
			+ COLUMN_BASETIME + " INTEGER NOT NULL, "
			+ COLUMN_UPLOADED + " INTEGER NOT NULL);";
	private static final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_MEASURE;
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE);
		Log.i(TAG, "Table " + TABLE_MEASURE + " created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading table " + TABLE_MEASURE + " from version " + oldVersion + " to version " + newVersion);
		database.execSQL(DATABASE_DROP_TABLE);
		Log.i(TAG, "Table " + TABLE_MEASURE + " dropped");
		onCreate(database);
	}
}
