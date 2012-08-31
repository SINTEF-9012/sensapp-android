package org.sensapp.android.sensappdroid.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CompositeTable {

	public static final String TABLE_COMPOSITE = "COMPOSITE";
	public static final String COLUMN_NAME = "_id";
	public static final String COLUMN_DESCRIPTION = "desc";
	public static final String COLUMN_URI = "uri";
	
	private static final String TAG = CompositeTable.class.getSimpleName();
	private static final String DATABASE_CREATE_TABLE = "CREATE TABLE " 
			+ TABLE_COMPOSITE + " (" 
			+ COLUMN_NAME + " TEXT PRIMARY KEY, "
			+ COLUMN_DESCRIPTION + " TEXT, "
			+ COLUMN_URI + " TEXT);";
	private static final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_COMPOSITE;
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_TABLE);
		Log.i(TAG, "Table " + TABLE_COMPOSITE + " created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading table " + TABLE_COMPOSITE + " from version " + oldVersion + " to version " + newVersion);
		database.execSQL(DATABASE_DROP_TABLE);
		Log.i(TAG, "Table " + TABLE_COMPOSITE + " dropped");
		onCreate(database);
	}
}
