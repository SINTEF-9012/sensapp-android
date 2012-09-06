package org.sensapp.android.sensappdroid.preferences;

import org.sensapp.android.sensappdroid.R;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class GeneralPrefFragment extends PreferenceFragment {
	
	private SharedPreferences preferences;
	private EditTextServerPreference server;
	private EditTextPreference port;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		addPreferencesFromResource(R.xml.pref_general_fragment);
		server = (EditTextServerPreference) findPreference(getString(R.string.pref_default_server_key));
		port = (EditTextPreference) findPreference(getString(R.string.pref_default_port_key));
	}
	
	public static String buildUri(SharedPreferences preferences, Resources resources) throws IllegalStateException {
		String server = preferences.getString(resources.getString(R.string.pref_default_server_key), resources.getString(R.string.pref_server_default_value));
		String port = preferences.getString(resources.getString(R.string.pref_default_port_key), "80");
		if (server.isEmpty() || port.isEmpty()) {
			throw new IllegalStateException("Error to read uri");
		}
		return server + ":" + port;
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