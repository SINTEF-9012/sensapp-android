package net.modelbased.sensapp.android.sensappdroid;

import net.modelbased.sensapp.android.sensappdroid.restservice.RestAPI;
import net.modelbased.sensapp.android.sensappdroid.restservice.RestRequestTask;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SensAppService extends Service {

	static final private String TAG = SensAppService.class.getName();
	
	private RestAPI restAPI;
	
	@Override
	public void onCreate() {
		Log.d(TAG, "__ON_CREATE__");
		super.onCreate();
		Toast.makeText(getApplicationContext(), R.string.toast_service_started, Toast.LENGTH_LONG).show();
		restAPI = new RestAPI(this, "46.51.169.123", 80);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction().equals(SensAppActivity.ACTION_UPLOAD)) {
			Log.d(TAG, "Receive: ACTION_UPLOAD");
			restAPI.pushNotUploadedMeasure();
		} else if (intent.getAction().equals(SensAppActivity.ACTION_FLUSH_ALL)) {
			Log.d(TAG, "Receive: ACTION_FLUSH_ALL");
			restAPI.deleteAllMeasure();
		} else if (intent.getAction().equals(SensAppActivity.ACTION_FLUSH_UPLOADED)) {
			Log.d(TAG, "Receive: ACTION_FLUSH_UPLOADED");
			restAPI.deleteUploaded();
		} else if (intent.getAction().equals(RestRequestTask.ACTION_REQUEST_SUCCEED)) {
			Log.d(TAG, "Receive: ACTION_REQUEST_SUCCEED");
			//int mode = intent.getExtras().getInt(RestRequestTask.EXTRA_MODE);
			Toast.makeText(getApplicationContext(), "Upload succeed", Toast.LENGTH_LONG).show();
			restAPI.setMeasureUploaded();
		} 
		return START_NOT_STICKY;
	}
	
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		Toast.makeText(getApplicationContext(), R.string.toast_service_stopped, Toast.LENGTH_LONG).show();
		super.onDestroy();
	}
	
//	private Hashtable<Integer, Measure> getMeasureNotUploaded() {
//		Hashtable<Integer, Measure> measures = null;
//		String selection = MeasureTable.COLUMN_UPLOADED + " = ?";
//		String[] selectionArgs = {"0"};
//		Cursor cursor = getContentResolver().query(SensAppMeasureProviderContract.CONTENT_URI, null, selection, selectionArgs, null);
//		if (cursor != null) {
//			Log.e(TAG, "Cursor count: " + cursor.getCount());
//			measures = buildMeasuresFromCursor(cursor);
//			cursor.close();
//		}
//		return measures;
//	}
//	
//	private void updateMeasureUploaded(Measure m) {
//		ContentValues newValues = new ContentValues();
//		newValues.put(MeasureTable.COLUMN_UPLOADED, 1);
//		String selection = MeasureTable.COLUMN_ID + " = " + m.getId();
//		getContentResolver().update(SensAppMeasureProviderContract.CONTENT_URI, newValues, selection, null);
//	}
//	
//	private Hashtable<Integer, Measure> buildMeasuresFromCursor(Cursor cursor) {
//		Hashtable<Integer, Measure> measures = new Hashtable<Integer, Measure>();
//		while (cursor.moveToNext()) {
//			Measure m = new Measure();
//			m.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_ID)));
//			m.setSensor(cursor.getString(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_SENSOR)));
//			m.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_VALUE)));
//			m.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_TIME)));
//			if (cursor.getInt(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_UPLOADED)) != 0) {
//				m.setUploaded(true);
//			} else {
//				m.setUploaded(false);
//			}
//			measures.put(m.getId(), m);
//		}
//		return measures;
//	}
}
