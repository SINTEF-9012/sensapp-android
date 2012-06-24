package net.modelbased.sensapp.android.sensappdroid;

import net.modelbased.sensapp.android.sensappdroid.contentprovider.MeasureCP;
import net.modelbased.sensapp.android.sensappdroid.database.MeasureTable;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "__ON_CREATE__");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure_view);
		tvId = (TextView) findViewById(R.id.measure_id_value);
		tvSensor = (TextView) findViewById(R.id.measure_sensor_value);
		tvValue = (TextView) findViewById(R.id.measure_value_value);
		Bundle extras = getIntent().getExtras();
		Uri measureUri = extras.getParcelable(MeasureCP.CONTENT_ITEM_TYPE);
		fillData(measureUri);
	}

	private void fillData(Uri uri) {
		String[] projection = {MeasureTable.COLUMN_ID, MeasureTable.COLUMN_SENSOR, MeasureTable.COLUMN_VALUE};
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			tvId.setText(cursor.getString(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_ID)));
			tvSensor.setText(cursor.getString(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_SENSOR)));
			tvValue.setText(cursor.getString(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_VALUE)));
			cursor.close();
		}
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}
}
