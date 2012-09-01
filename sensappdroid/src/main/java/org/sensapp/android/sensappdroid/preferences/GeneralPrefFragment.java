package org.sensapp.android.sensappdroid.preferences;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class GeneralPrefFragment extends PreferenceFragment {
	
	private static final String TAG = GeneralPrefFragment.class.getSimpleName(); 
	
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
	
	public static void updateUri(SharedPreferences preferences, Resources resources, ContentResolver resolver) {
		String server = preferences.getString(resources.getString(R.string.pref_default_server_key), "");
		String port = preferences.getString(resources.getString(R.string.pref_default_port_key), "");
		if (server.isEmpty() || port.isEmpty()) {
			Log.e(TAG, "Error to read uri");
			return;
		}
		String uri = server + ":" + port;
		ContentValues values = new ContentValues();
		values.put(SensAppCPContract.Sensor.URI, uri);
		int updated = resolver.update(SensAppCPContract.Sensor.CONTENT_URI, values, SensAppCPContract.Sensor.URI + " IS NULL", null);
		Log.i(TAG, updated + " sensors updated");
		updated = resolver.update(SensAppCPContract.Composite.CONTENT_URI, values, SensAppCPContract.Composite.URI + " IS NULL", null);
		Log.i(TAG, updated + " composites updated");
	}
	
	SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {	
			if (key.equals(server.getKey())) {
				server.setSummary(sharedPreferences.getString(server.getKey(), ""));
				updateUri(preferences, getResources(), getActivity().getContentResolver());
			} else if (key.equals(port.getKey())) {
				port.setSummary(sharedPreferences.getString(port.getKey(), ""));
				updateUri(preferences, getResources(), getActivity().getContentResolver());
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