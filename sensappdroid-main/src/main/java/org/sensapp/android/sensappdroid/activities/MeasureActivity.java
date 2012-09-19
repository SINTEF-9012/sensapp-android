/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.database.MeasureTable;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class MeasureActivity extends Activity {

	private TextView tvId;
	private TextView tvSensor;
	private TextView tvValue;
	private TextView tvTime;
	private TextView tvStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure_view);
		tvId = (TextView) findViewById(R.id.measure_id_value);
		tvSensor = (TextView) findViewById(R.id.measure_sensor_value);
		tvValue = (TextView) findViewById(R.id.measure_value_value);
		tvTime = (TextView) findViewById(R.id.measure_time_value);
		tvStatus = (TextView) findViewById(R.id.measure_status_value);
		Uri uri = getIntent().getData();		
		fillData(uri);
	}

	private void fillData(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			tvId.setText(cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Measure.ID)));
			tvSensor.setText(cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Measure.SENSOR)));
			tvValue.setText(cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Measure.VALUE)));
			tvTime.setText(cursor.getString(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_TIME)));
			if (cursor.getInt(cursor.getColumnIndexOrThrow(MeasureTable.COLUMN_UPLOADED)) != 0) {
				tvStatus.setText(R.string.measure_uploaded);
			} else {
				tvStatus.setText(R.string.measure_notuploaded);
			}
			cursor.close();
		}
	}
}
