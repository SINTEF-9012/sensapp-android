package org.sensapp.android.sensappdroid.preferences;

import org.sensapp.android.sensappdroid.R;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class GeneralPrefFragment extends PreferenceFragment {
	
	//private static final String TAG = GeneralPrefFragment.class.getSimpleName(); 
	
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
	
//	public static void updateUri(SharedPreferences preferences, Resources resources, ContentResolver resolver) throws IllegalStateException {
//		String uri = buildUri(preferences, resources);
//		ContentValues values = new ContentValues();
//		values.put(SensAppCPContract.Sensor.URI, uri);
//		int updated = resolver.update(SensAppCPContract.Sensor.CONTENT_URI, values, null, null);
//		Log.i(TAG, updated + " sensors updated");
//		updated = resolver.update(SensAppCPContract.Composite.CONTENT_URI, values, null, null);
//		Log.i(TAG, updated + " composites updated");
//	}
	
	SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {	
			if (key.equals(server.getKey())) {
				server.setSummary(sharedPreferences.getString(server.getKey(), ""));
				//updateUri(preferences, getResources(), getActivity().getContentResolver());
			} else if (key.equals(port.getKey())) {
				port.setSummary(sharedPreferences.getString(port.getKey(), ""));
				//updateUri(preferences, getResources(), getActivity().getContentResolver());
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