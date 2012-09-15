package org.sensapp.android.sensappdroid.connectivity;

import org.sensapp.android.sensappdroid.activities.SensAppService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class ConnectivityReceiver extends BroadcastReceiver { 
	
	public static final String ACTION_CONNECTIVITY_FOUND = ConnectivityReceiver.class.getName() + ".ACTION_CONNECTIVITY_FOUND";
	
	private static final String TAG = ConnectivityReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				Log.i(TAG, "Data connection lost");
			} else if (isDataAvailable(context)) {
				Log.i(TAG, "Data connection found");
				context.startService(new Intent(context, SensAppService.class).setAction(ACTION_CONNECTIVITY_FOUND));
			}
		}
	}
	
	public static boolean isDataAvailable(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    if (wifi.isConnected()) {
	        return true;
	    } else if (mobile.isConnected()) {
	        return true;
	    }
	    return false;
	}
}
