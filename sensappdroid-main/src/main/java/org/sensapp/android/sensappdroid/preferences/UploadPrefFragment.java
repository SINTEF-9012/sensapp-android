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
package org.sensapp.android.sensappdroid.preferences;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.activities.SensAppService;
import org.sensapp.android.sensappdroid.activities.TabsActivity;
import org.sensapp.android.sensappdroid.connectivity.ConnectivityReceiver;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.datarequests.UpdateMeasuresTask;
import org.sensapp.android.sensappdroid.datarequests.UpdateSensorsTask;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class UploadPrefFragment extends PreferenceFragment {
	
	private static final int AUTO_UPLOAD_NOTIFICATION = 974;
	
	private EditTextPreference delay;
	private CheckBoxPreference active;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_upload_fragment);

		delay = (EditTextPreference) findPreference(getString(R.string.pref_autoupload_delay_key));
		active = (CheckBoxPreference) findPreference(getString(R.string.pref_auto_upload_key));
		
		active.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ((Boolean) newValue) {
					startAutoUpload(getActivity(), Integer.valueOf(delay.getSharedPreferences().getString(delay.getKey(), "10")));
					if (!ConnectivityReceiver.isDataAvailable(getActivity())) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("The auto upload will start when a data connection is available")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						}).create().show();
					}
				} else {
					stopAutoUpload(getActivity());
				}
				return true;
			}
		});

		delay.setDependency(active.getKey());
		delay.setSummary(delay.getSharedPreferences().getString(delay.getKey(), "10") + " minutes");
		delay.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ("1".equals((String) newValue)) {
					delay.setSummary((String) newValue + " minute");
				} else {
					delay.setSummary((String) newValue + " minutes");
				}
				stopAutoUpload(getActivity());
				startAutoUpload(getActivity(), Integer.valueOf((String) newValue));
				return true;
			}
		});
		
		Preference sensors = findPreference(getString(R.string.pref_choose_sensor_autoupload_key));
		sensors.setDependency(active.getKey());
		sensors.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new AutoUploadSensorDialog().show(getFragmentManager(), "choose_sensor");
				return true;
			}
		});
		
		findPreference(getString(R.string.pref_undo_upload_key)).setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ContentValues values = new ContentValues();
				values.put(SensAppContract.Sensor.UPLOADED, 0);
				new UpdateSensorsTask(getActivity().getApplicationContext(), SensAppContract.Sensor.UPLOADED + " = 1", values).execute();
				values.clear();
				values.put(SensAppContract.Measure.UPLOADED, 0);
				new UpdateMeasuresTask(getActivity().getApplicationContext(), SensAppContract.Measure.UPLOADED + " = 1", values).execute();
				return true;
			}
		});
	}
	
	private static void startAutoUpload(Context context, int period) {
		Intent startService = new Intent(context, SensAppService.class).setAction(SensAppService.ACTION_AUTO_UPLOAD);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, startService, PendingIntent.FLAG_CANCEL_CURRENT);
		
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), period * 60000L, pendingIntent);
		
		Notification notification = new Notification.Builder(context)
			.setContentTitle("SensApp")
			.setContentText("Auto upload is running")
			.setTicker("Auto upload started")
		    .setSmallIcon(R.drawable.ic_launcher)
		    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, TabsActivity.class), 0))
		    .getNotification();
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(AUTO_UPLOAD_NOTIFICATION, notification);
	}
	
	private static void stopAutoUpload(Context context) {
		Intent startService = new Intent(context, SensAppService.class).setAction(SensAppService.ACTION_AUTO_UPLOAD);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, startService, PendingIntent.FLAG_CANCEL_CURRENT);
		
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(AUTO_UPLOAD_NOTIFICATION);
	}

	public static class BootReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if (preferences.getBoolean(context.getString(R.string.pref_auto_upload_key), false)) {
				String delay = preferences.getString(context.getString(R.string.pref_autoupload_delay_key), "10");
				startAutoUpload(context, Integer.valueOf(delay));
			}
		}
	}
}