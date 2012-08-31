package org.sensapp.android.sensappdroid.preferences;

import org.sensapp.android.sensappdroid.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class GeneralPrefFragment extends PreferenceFragment {
	
	private SharedPreferences preferences;
	private EditTextPreference server;
	private EditTextPreference port;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		addPreferencesFromResource(R.xml.pref_general_fragment);
		server = (EditTextPreference) findPreference(getString(R.string.pref_default_server_key));
		port = (EditTextPreference) findPreference(getString(R.string.pref_default_port_key));
	}
	
	SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {	
			if (key.equals(server.getKey())) {
				server.setSummary(sharedPreferences.getString(server.getKey(), ""));
			} else if (key.equals(port.getKey())) {
				port.setSummary(sharedPreferences.getString(port.getKey(), ""));
			} 			
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		preferences.registerOnSharedPreferenceChangeListener(spChanged);
		server.setSummary(preferences.getString(server.getKey(), ""));
		port.setSummary(preferences.getString(port.getKey(), ""));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		preferences.unregisterOnSharedPreferenceChangeListener(spChanged);
	}
}