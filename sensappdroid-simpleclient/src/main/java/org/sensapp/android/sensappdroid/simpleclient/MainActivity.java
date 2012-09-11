package org.sensapp.android.sensappdroid.simpleclient;

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

public class MainActivity extends Activity {

	private static final String SERVICE_RUNNING = "pref_service_is_running";
	private static final int refreshRate = 60; 
	
	private Button buttonService;
	private TextView tvStatus;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonService = (Button) findViewById(R.id.b_status);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        updateLabels();
        buttonService.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(MainActivity.this.getApplicationContext(), BatteryLoggerService.class);
				PendingIntent pintent = PendingIntent.getService(MainActivity.this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				if (!preferences.getBoolean(SERVICE_RUNNING, false)) {
					preferences.edit().putBoolean(SERVICE_RUNNING, true).commit();
					alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), refreshRate * 1000, pintent);
				} else {
					preferences.edit().putBoolean(SERVICE_RUNNING, false).commit();
					alarm.cancel(pintent);
				}
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
