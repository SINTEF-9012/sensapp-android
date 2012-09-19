/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.clientsamples.batterylogger;

import org.sensapp.android.sensappdroid.api.SensAppHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Fabien Fleurey
 * This activity is a minimalist UI to enable or disable the battery logging.
 */
public class MainActivity extends Activity {

	protected static final String SERVICE_RUNNING = "pref_service_is_running"; // Use a persistent preference to know if the alarm is set.
	
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
					AlarmHelper.setAlarm(getApplicationContext());
				} else {
					// Update the preference.
					preferences.edit().putBoolean(SERVICE_RUNNING, false).commit();
					// Request for disable, cancel the alarm.
					AlarmHelper.cancelAlarm(getApplicationContext());
				}
				// Update button and text view.
				updateLabels();
			}
		});
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.preferences:
			startActivity(new Intent(getApplicationContext(), Preferences.class));
			return true;
		}
		return false;
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
