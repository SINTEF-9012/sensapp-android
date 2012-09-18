package org.sensapp.android.sensappdroid.preferences;

import java.util.HashSet;
import java.util.Set;

import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class AutoUploadSensorDialog extends DialogFragment {

	public static final String SENSOR_MAINTAINED = "sensor_maintained_key";
	
	private Cursor cursor;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		cursor = getActivity().getContentResolver().query(SensAppContract.Sensor.CONTENT_URI, null, null, null, null);
		String[] names = new String[cursor.getCount()];
		boolean[] status = new boolean[cursor.getCount()];
		final Set<String> statusSaved = PreferenceManager.getDefaultSharedPreferences(getActivity()).getStringSet(SENSOR_MAINTAINED, new HashSet<String>());
		for (int i = 0 ; cursor.moveToNext() ; i ++) {
			names[i] = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
			status[i] = statusSaved.contains(names[i]);
		}
		return new AlertDialog.Builder(getActivity())
		.setTitle("Select sensors to auto upload")
		.setMultiChoiceItems(names, status, new DialogInterface.OnMultiChoiceClickListener() {
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				cursor.moveToPosition(which);
				String sensorName = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
				if (isChecked) {
					statusSaved.add(sensorName);
				} else {
					statusSaved.remove(sensorName);
				}
			}
		})
		.setPositiveButton("Done", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putStringSet(SENSOR_MAINTAINED, statusSaved).commit();
				cursor.close();
			}
		}).create();
    }

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (cursor != null) {
			cursor.close();
		}
	}    
}
