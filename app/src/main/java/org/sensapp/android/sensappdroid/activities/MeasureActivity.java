package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.database.MeasureTable;

import org.sensapp.android.sensappdroid.R;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MeasureActivity extends Activity {

	private static String TAG = MeasureActivity.class.getName();
	
	private TextView tvId;
	private TextView tvSensor;
	private TextView tvValue;
	private TextView tvTime;
	private TextView tvStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "__ON_CREATE__");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure_view);
		tvId = (TextView) findViewById(R.id.measure_id_value);
		tvSensor = (TextView) findViewById(R.id.measure_sensor_value);
		tvValue = (TextView) findViewById(R.id.measure_value_value);
		tvTime = (TextView) findViewById(R.id.measure_time_value);
		tvStatus = (TextView) findViewById(R.id.measure_status_value);
		Uri uri = getIntent().getData();		
		Log.e(TAG, uri.toString());
		fillData(uri);
	}

	private void fillData(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			tvId.setText(cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.ID)));
			tvSensor.setText(cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.SENSOR)));
			tvValue.setText(cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.VALUE)));
			tvTime.setText(cursor.getString(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_TIME)));
			if (cursor.getInt(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_UPLOADED)) != 0) {
				tvStatus.setText(R.string.measure_uploaded);
			} else {
				tvStatus.setText(R.string.measure_notuploaded);
			}
			cursor.close();
		}
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}
}
