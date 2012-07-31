package net.modelbased.sensapp.android.sensappdroid.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MetadataTable {

	public static final String TABLE_METADATA = "METADATA";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SENSOR = "sensor";
	public static final String COLUMN_KEY = "key";
	public static final String COLUMN_VALUE = "value";
	
	private static final String TAG = MetadataTable.class.getName();
	private static final String DATABASE_CREATE_TABLE = "CREATE TABLE " 
			+ TABLE_METADATA + " (" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ COLUMN_SENSOR + " TEXT NOT NULL, "
			+ COLUMN_KEY + " TEXT NOT NULL, "
			+ COLUMN_VALUE + " TEXT NOT NULL);";
	private static final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_METADATA;
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE);
		Log.i(TAG, "Table " + TABLE_METADATA + " created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading table " + TABLE_METADATA + " from version " + oldVersion + " to version " + newVersion);
		database.execSQL(DATABASE_DROP_TABLE);
		Log.i(TAG, "Table " + TABLE_METADATA + " dropped");
		onCreate(database);
	}
}
