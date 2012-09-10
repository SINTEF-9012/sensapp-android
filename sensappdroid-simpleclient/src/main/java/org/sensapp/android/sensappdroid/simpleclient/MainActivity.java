package org.sensapp.android.sensappdroid.simpleclient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private boolean serviceRunning = true;
	private Button buttonService;
	private TextView tvStatus;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceRunning = (PendingIntent.getService(getApplicationContext(), 0, new Intent(getApplicationContext(), BatteryLoggerService.class), PendingIntent.FLAG_NO_CREATE) != null);
        buttonService = (Button) findViewById(R.id.b_status);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        updateLabels();
        buttonService.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(MainActivity.this.getApplicationContext(), BatteryLoggerService.class);
				PendingIntent pintent = PendingIntent.getService(MainActivity.this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				if (!serviceRunning) {
					serviceRunning = true;
					alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10*1000, pintent);
				} else {
					serviceRunning = false;
					alarm.cancel(pintent);
				}
				updateLabels();
			}
		});
    }
    
    private void updateLabels() {
    	if (serviceRunning) {
        	buttonService.setText(R.string.button_service_stop);
        	tvStatus.setText(R.string.tv_status_running);
        } else {
        	buttonService.setText(R.string.button_service_start);
        	tvStatus.setText(R.string.tv_status_stoped);
        }
    }
}
