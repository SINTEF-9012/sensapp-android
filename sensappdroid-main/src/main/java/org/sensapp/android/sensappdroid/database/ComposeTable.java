package org.sensapp.android.sensappdroid.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ComposeTable {

	public static final String TABLE_COMPOSE = "COMPOSE";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_COMPOSITE = "composite";
	public static final String COLUMN_SENSOR = "sensor";
	
	private static final String TAG = ComposeTable.class.getSimpleName();
	private static final String DATABASE_CREATE_TABLE = "CREATE TABLE " 
			+ TABLE_COMPOSE + " (" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_COMPOSITE + " TEXT NOT NULL, "
			+ COLUMN_SENSOR + " TEXT NOT NULL);"; 
	private static final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_COMPOSE;
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE);
		Log.i(TAG, "Table " + TABLE_COMPOSE + " created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading table " + TABLE_COMPOSE + " from version " + oldVersion + " to version " + newVersion);
		database.execSQL(DATABASE_DROP_TABLE);
		Log.i(TAG, "Table " + TABLE_COMPOSE + " dropped");
		onCreate(database);
	}
}
