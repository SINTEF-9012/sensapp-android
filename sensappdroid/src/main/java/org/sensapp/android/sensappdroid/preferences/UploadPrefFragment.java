package org.sensapp.android.sensappdroid.preferences;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.activities.SensAppService;
import org.sensapp.android.sensappdroid.connectivity.Connectivity;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.UpdateMeasuresTask;
import org.sensapp.android.sensappdroid.datarequests.UpdateSensorsTask;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class UploadPrefFragment extends PreferenceFragment {
	
	private SharedPreferences preferences;
	private CheckBoxPreference active;
	private EditTextPreference delay;
	private MultiSelectListPreference sensors;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		addPreferencesFromResource(R.xml.pref_upload_fragment);
		active = (CheckBoxPreference) findPreference(getString(R.string.pref_auto_upload_key));
		delay = (EditTextPreference) findPreference(getString(R.string.pref_autoupload_delay_key));
		sensors = (MultiSelectListPreference) findPreference(getString(R.string.pref_list_autoupload_sensor_key));
		delay.setDependency(active.getKey());
		sensors.setDependency(active.getKey());
		
		active.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ((Boolean) newValue) {
					getActivity().startService(new Intent(getActivity(), SensAppService.class));
					if (!Connectivity.isDataAvailable(getActivity())) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("The auto upload will start when a data connection is available")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						}).create().show();
					}
				}
				return true;
			}
		});
		
		findPreference(getString(R.string.pref_undo_upload_key)).setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ContentValues values = new ContentValues();
				values.put(SensAppCPContract.Sensor.UPLOADED, 0);
				new UpdateSensorsTask(getActivity().getApplicationContext(), SensAppCPContract.Sensor.UPLOADED + " = 1", values).execute();
				values.clear();
				values.put(SensAppCPContract.Measure.UPLOADED, 0);
				new UpdateMeasuresTask(getActivity().getApplicationContext(), SensAppCPContract.Measure.UPLOADED + " = 1", values).execute();
				return true;
			}
		});
	}
	
	SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {	
			if (key.equals(delay.getKey())) {
				delay.setSummary(sharedPreferences.getString(delay.getKey(), ""));
			} 		
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		preferences.registerOnSharedPreferenceChangeListener(spChanged);
		delay.setSummary(preferences.getString(delay.getKey(), ""));
		Cursor c = getActivity().getContentResolver().query(SensAppCPContract.Sensor.CONTENT_URI, new String[]{SensAppCPContract.Sensor.NAME}, null, null, null);
		if (c != null && c.moveToFirst()) {
			String[] names = new String[c.getCount()];
			for (int i = 0 ; i < names.length ; i ++) {
				names[i] = c.getString(c.getColumnIndexOrThrow(SensAppCPContract.Sensor.NAME));
				c.moveToNext();
			}
			c.close();
			Log.e("DEBUG PREF", "reset entries!");
			sensors.setEntries(names);
			sensors.setEntryValues(names);
		}	
	}
	
	@Override
	public void onPause() {
		super.onPause();
		preferences.unregisterOnSharedPreferenceChangeListener(spChanged);
	}
}