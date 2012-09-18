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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class UploadPrefFragment extends PreferenceFragment {
	
	private static final int AUTO_UPLOAD_NOTIFICATION = 974;
	
	private Intent startService;
	private PendingIntent pendingIntent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_upload_fragment);
		startService = new Intent(getActivity(), SensAppService.class).setAction(SensAppService.ACTION_AUTO_UPLOAD);
		pendingIntent = PendingIntent.getService(getActivity(), 0, startService, PendingIntent.FLAG_CANCEL_CURRENT);

		final EditTextPreference delay = (EditTextPreference) findPreference(getString(R.string.pref_autoupload_delay_key));
		final CheckBoxPreference active = (CheckBoxPreference) findPreference(getString(R.string.pref_auto_upload_key));
		
		active.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ((Boolean) newValue) {
					startAutoUpload(Integer.valueOf(delay.getSharedPreferences().getString(delay.getKey(), "10")));
					if (!ConnectivityReceiver.isDataAvailable(getActivity())) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("The auto upload will start when a data connection is available")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						}).create().show();
					}
				} else {
					stopAutoUpload();
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
				stopAutoUpload();
				startAutoUpload(Integer.valueOf((String) newValue));
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
	
	private void startAutoUpload(int period) {
		((AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE)).setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), period * 60000, pendingIntent);
		
		Notification notification = new Notification.Builder(getActivity())
			.setContentTitle("SensApp")
			.setContentText("Auto upload is running")
			.setTicker("Auto upload started")
		    .setSmallIcon(R.drawable.ic_launcher)
		    .setContentIntent(PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(), TabsActivity.class), 0))
		    .getNotification();
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE)).notify(AUTO_UPLOAD_NOTIFICATION, notification);
	}
	
	private void stopAutoUpload() {
		((AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
		((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(AUTO_UPLOAD_NOTIFICATION);
	}
}