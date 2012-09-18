package org.sensapp.android.sensappdroid.clientsamples.batterylogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("DEBUG", "Boot completed");
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.SERVICE_RUNNING, false)) {
			AlarmHelper.setAlarm(context);
		}
	}

}
