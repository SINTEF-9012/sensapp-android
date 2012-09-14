package org.sensapp.android.sensappdroid.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SensAppDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = SensAppDatabaseHelper.class.getName();
	private static final String DATABASE_NAME = "sensapp.db";
	private static final int DATABASE_VERSION = 12;
	
	public SensAppDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.i(TAG, "Creating DATABASE " + DATABASE_NAME);
		MeasureTable.onCreate(database);
		SensorTable.onCreate(database);
		CompositeTable.onCreate(database);
		ComposeTable.onCreate(database);
		//MetadataTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.i(TAG, "Updating DATABASE " + DATABASE_NAME + " from " + oldVersion + " to " + newVersion);
		MeasureTable.onUpgrade(database, oldVersion, newVersion);
		SensorTable.onUpgrade(database, oldVersion, newVersion);
		CompositeTable.onUpgrade(database, oldVersion, newVersion);
		ComposeTable.onUpgrade(database, oldVersion, newVersion);
		//MetadataTable.onUpgrade(database, oldVersion, newVersion);
	}
}
