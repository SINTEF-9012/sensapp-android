package org.sensapp.android.sensappdroid.connectivity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class Connectivity extends BroadcastReceiver { 
	
	public interface ConnectivityListenner {
		public void connectionFound();
		public void connectionLost();
	}
	
	private static final String TAG = Connectivity.class.getSimpleName();
	
	private boolean dataAvailable;
	private ArrayList<ConnectivityListenner> listenners = new ArrayList<ConnectivityListenner>();

	public Connectivity(Context context) {
		dataAvailable = isDataAvailable(context);
	}
	
	public Connectivity(Context context, ConnectivityListenner listenner) {
		dataAvailable = isDataAvailable(context);
		listenners.add(listenner);
	}

	public void addListenner(ConnectivityListenner listenner) {
		listenners.add(listenner);
	}
	
	public void removeListenner(ConnectivityListenner listenner) {
		listenners.remove(listenner);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			if (dataAvailable && intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				Log.i(TAG, "Data connection lost");
				dataAvailable = false;
				for (ConnectivityListenner listenner : listenners) {
					listenner.connectionLost();
				}
			} else if (!dataAvailable && isDataAvailable(context)) {
				Log.i(TAG, "Data connection found");
				dataAvailable = true;
				for (ConnectivityListenner listenner : listenners) {
					listenner.connectionFound();
				}
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
