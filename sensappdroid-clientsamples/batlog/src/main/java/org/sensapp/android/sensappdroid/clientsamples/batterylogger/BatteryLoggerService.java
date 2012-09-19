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
package org.sensapp.android.sensappdroid.clientsamples.batterylogger;

import org.sensapp.android.sensappdroid.api.SensAppHelper;
import org.sensapp.android.sensappdroid.api.SensAppUnit;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Fabien Fleurey
 * This class presents a minimalist service which use the SensApp android API to log the battery level.
 * It is started by the alarm manager and self stopped as soon it has inserted a new measure.
 */
public class BatteryLoggerService extends Service {

	private static final String TAG = BatteryLoggerService.class.getSimpleName();
	
	private String sensorName;
	
	@Override
	public void onCreate() {
		super.onCreate();
		if (SensAppHelper.isSensAppInstalled(getApplicationContext())) {
			sensorName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.pref_sensorname_key), "MyDevice_battery");
			registerSensor();
			Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);	
			insertMeasure(level);
		} else {
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(MainActivity.SERVICE_RUNNING, false).commit();
			AlarmHelper.cancelAlarm(getApplicationContext());
			Log.e(TAG, "SensApp has been uninstalled");
		}
		stopSelf();
	}
	
	private void registerSensor() {
		try {
			Uri sensorUri = SensAppHelper.registerNumericalSensor(getApplicationContext(), sensorName, "Battery level", SensAppUnit.BATTERY_LEVEL, R.drawable.ic_launcher);
			if (sensorUri == null) {
				// The sensor is already registered.
				Log.w(TAG, sensorName + " is already registered");
			} else {
				// The sensor is newly inserted.
				Log.i(TAG, sensorName + " available at " + sensorUri);
			}
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void insertMeasure(int value) {
		try {
		Uri measureUri = SensAppHelper.insertMeasure(getApplicationContext(), sensorName, value);
		Log.i(TAG, "New measure (" + value + ") available at " + measureUri);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
