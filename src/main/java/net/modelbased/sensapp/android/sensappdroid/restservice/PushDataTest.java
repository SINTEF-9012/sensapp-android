package net.modelbased.sensapp.android.sensappdroid.restservice;

import net.modelbased.sensapp.android.sensappdroid.contentprovider.MeasureCP;
import net.modelbased.sensapp.android.sensappdroid.database.MeasureTable;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;



public class PushDataTest {
	
	public static Uri push(Context context) {
		ContentValues values = new ContentValues();
		values.put(MeasureTable.COLUMN_SENSOR, "Corbys test");
		values.put(MeasureTable.COLUMN_VALUE, 25);
		values.put(MeasureTable.COLUMN_TIME, 256000041);
		values.put(MeasureTable.COLUMN_UPLOADED, 0);
		return context.getContentResolver().insert(MeasureCP.CONTENT_URI, values);
	}

}
