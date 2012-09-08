package org.sensapp.android.sensappdroid.activities;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.connectivity.Connectivity;
import org.sensapp.android.sensappdroid.connectivity.Connectivity.ConnectivityListenner;
import org.sensapp.android.sensappdroid.contentprovider.SensAppContract;
import org.sensapp.android.sensappdroid.restrequests.PutMeasuresTask;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class AutoUploadTimer extends Timer implements ConnectivityListenner {

	private static final String TAG = AutoUploadTimer.class.getSimpleName();
	
	private Context context;
	private Connectivity connectivity;
	private UploadTimerTask task;
	private int delay;
	
	public AutoUploadTimer(Context context, int delay) {
		this.context = context;
		this.delay = delay;
		connectivity = new Connectivity(context, this);
		context.registerReceiver(connectivity, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		if (Connectivity.isDataAvailable(context)) {
			activate();
		}
	}
	
	public void activate() {
		task = new UploadTimerTask();
		scheduleAtFixedRate(task, 0, delay * 1000);
		Log.i(TAG, "Timer scheduled every " + delay + " seconds");	
	}
	
	public void desactivate() {
		if (task != null) {
			task.cancel();
		}
		purge();
	}
	
	private class UploadTimerTask extends TimerTask {
		@Override
        public void run() {
			Log.i(TAG, "Auto upload");
			Set<String> names = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(context.getString(R.string.pref_list_autoupload_sensor_key), null);
			if (names != null && !names.isEmpty()) {
				for (final String name : names) {
					Log.d(TAG, "Put " + name + " sensor");
					new PutMeasuresTask(context, Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + name)).execute();
				}
			}
        }
    }
	
	@Override
	public void cancel() {
		connectivity.removeListenner(this);
		context.unregisterReceiver(connectivity);
		super.cancel();
	}

	@Override
	public void connectionFound() {
		Log.i(TAG, "Activating");
		activate();
	}

	@Override
	public void connectionLost() {
		Log.i(TAG, "Desactivating");
		desactivate();
	}  
}
