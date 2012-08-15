package org.sensapp.android.sensappdroid.preferences;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
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