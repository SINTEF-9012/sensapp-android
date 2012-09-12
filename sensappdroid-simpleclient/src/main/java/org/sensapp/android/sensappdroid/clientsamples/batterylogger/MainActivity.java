package org.sensapp.android.sensappdroid.clientsamples.batterylogger;

import org.sensapp.android.sensappdroid.api.SensAppHelper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Fabien Fleurey
 * This activity is a minimalist UI to enable or disable the battery logging.
 */
public class MainActivity extends Activity {

	private static final String SERVICE_RUNNING = "pref_service_is_running"; // Use a persistent preference to know if the alarm is set.
	private static final int refreshRate = 60; // Polling interval to start the service.
	
	private Button buttonService;
	private TextView tvStatus;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonService = (Button) findViewById(R.id.b_status);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        // Update button and text view.
        updateLabels();
        
        buttonService.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Get the alarm manager.
				AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				// Prepare the pending intent to start the service.
				Intent intent = new Intent(MainActivity.this.getApplicationContext(), BatteryLoggerService.class);
				PendingIntent pintent = PendingIntent.getService(MainActivity.this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				// Get the shared preferences.
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				if (!preferences.getBoolean(SERVICE_RUNNING, false)) {
					// Request for activation.
					
					// Check if SensApp is installed.
					if (!SensAppHelper.isSensAppInstalled(getApplicationContext())) {
						// If not suggest to install and return.
						SensAppHelper.getInstallationDialog(MainActivity.this).show();
						return;
					}
					// Update the preference.
					preferences.edit().putBoolean(SERVICE_RUNNING, true).commit();
					// Schedule a repeating alarm to start the service.
					alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), refreshRate * 1000, pintent);
				} else {
					// Update the preference.
					preferences.edit().putBoolean(SERVICE_RUNNING, false).commit();
					// Request for disable, cancel the alarm.
					alarm.cancel(pintent);
				}
				// Update button and text view.
				updateLabels();
			}
		});
    }
    
    private void updateLabels() {
    	if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(SERVICE_RUNNING, false)) {
        	buttonService.setText(R.string.button_service_stop);
        	tvStatus.setText(R.string.tv_status_running);
        } else {
        	buttonService.setText(R.string.button_service_start);
        	tvStatus.setText(R.string.tv_status_stoped);
        }
    }
}
