package org.sensapp.android.sensappdroid.api;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.content.ContentResolver;
import android.content.ContentValues;

public class InsertionManager {

	private final static int LOWEST_INTERVAL = 1;
	
	private static final ArrayDeque<ContentValues> buffers = new ArrayDeque<ContentValues>();
	private static Boolean empty = true;
	private static final ArrayList<ContentValues> valuesBatch = new ArrayList<ContentValues>();
	private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	
	public static void storeMeasure(final ContentResolver cresolver, ContentValues values) {
		//Log.w("DEBUG", "__Store__");
		buffers.add(values);
		if (empty) {
			scheduleFlush(cresolver);
			empty = false;
		}
	}

	private static void flushSensorMeasures(ContentResolver cresolver) {
		//Log.e("DEBUG", "__Flush__");
		valuesBatch.clear();
		ContentValues values = buffers.pollFirst(); 
		while (values != null) {
			valuesBatch.add(values);
			values = buffers.pollFirst();
			empty = true;
		}
		cresolver.bulkInsert(SensAppContract.Measure.CONTENT_URI, valuesBatch.toArray(new ContentValues[valuesBatch.size()]));
	}
	
	private static void scheduleFlush(final ContentResolver cresolver) {
		worker.schedule(new Runnable() {
			public void run() {
				flushSensorMeasures(cresolver);
			}
		}, LOWEST_INTERVAL, TimeUnit.SECONDS);
	}
}
