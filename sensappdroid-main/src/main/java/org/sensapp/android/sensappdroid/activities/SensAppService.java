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
package org.sensapp.android.sensappdroid.activities;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.connectivity.ConnectivityReceiver;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.datarequests.DeleteMeasuresTask;
import org.sensapp.android.sensappdroid.preferences.AutoUploadSensorDialog;
import org.sensapp.android.sensappdroid.restrequests.PostCompositeRestTask;
import org.sensapp.android.sensappdroid.restrequests.PutMeasuresTask;
import org.sensapp.android.sensappdroid.restrequests.PutMeasuresTask.PutMeasureCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SensAppService extends Service implements PutMeasureCallback {
	
	public static final String ACTION_UPLOAD = SensAppService.class.getName() + ".ACTION_UPLOAD";
	public static final String ACTION_AUTO_UPLOAD = SensAppService.class.getName() + ".ACTION_AUTO_UPLOAD";
    public static final String ACTION_AMOUNT_UPLOAD = SensAppService.class.getName() + ".ACTION_AMOUNT_UPLOAD";
	public static final String ACTION_DELETE_LOCAL = SensAppService.class.getName() + ".ACTION_DELETE_LOCAL";
	public static final String EXTRA_UPLOADED_FILTER = SensAppService.class.getName() + ".EXTRA_UPLOADED_FILTER";
	
	private static final String TAG = SensAppService.class.getSimpleName();
	
	private boolean waitForDataAuto = false;
    private boolean waitForDataAmount = false;
	
	private int lastTaskStarted = 0;
	private int lastConsecutiveTaskEnded = 0;
	private List<Integer> taskBuffer = new ArrayList<Integer>();
    static private int amountData;
    static private boolean dataSent = false;
	
	@Override
	public void onCreate() {
		super.onCreate();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        amountData = Integer.parseInt(sp.getString(getString(R.string.pref_autoupload_amount_key), "1000"));
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction() != null) {
			if (intent.getAction().equals(ACTION_UPLOAD)) {
				uploadMeasureUri(intent.getData());
			} else if (intent.getAction().equals(ACTION_AUTO_UPLOAD)) {
				autoUpload();
            } else if (intent.getAction().equals(ACTION_AMOUNT_UPLOAD)) {
                amountUpload();
			} else if (intent.getAction().equals(ACTION_DELETE_LOCAL)) {
				Bundle extra = intent.getExtras();
				if (extra == null) {
					new DeleteMeasuresTask(this, intent.getData()).execute();
				} else if (extra.getBoolean(EXTRA_UPLOADED_FILTER)) {
					new DeleteMeasuresTask(this, intent.getData()).execute(SensAppContract.Measure.UPLOADED + " = 1");
				}
			} else if (intent.getAction().equals(ConnectivityReceiver.ACTION_CONNECTIVITY_FOUND)) {
				if (waitForDataAuto || waitForDataAmount) {
					if(waitForDataAuto)
                        autoUpload();
                    if(waitForDataAmount)
                        amountUpload();
				} else {
					stopSelf();
				}
			}
		} else {
			stopSelf();
		}
		return START_NOT_STICKY;
	}
	
	private void uploadMeasureUri(Uri uri) {
		if (!ConnectivityReceiver.isDataAvailable(getApplicationContext())) {
			Log.e(TAG, "No data connection available");
			Toast.makeText(this, "No data connection available", Toast.LENGTH_LONG).show();
		} else {
			new PutMeasuresTask(this, taskIdGen(), getApplicationContext(), uri).execute();
		}
	}
	
	private void autoUpload() {
        if (!ConnectivityReceiver.isDataAvailable(getApplicationContext())) {
            Log.e(TAG, "No data connection available");
            waitForDataAuto = true;
        } else {
            waitForDataAuto = false;
            Set<String> names = PreferenceManager.getDefaultSharedPreferences(this).getStringSet(AutoUploadSensorDialog.SENSOR_MAINTAINED, null);
            if (names != null && !names.isEmpty()) {
                for (final String name : names) {
                    new PutMeasuresTask(this, taskIdGen(), getApplicationContext(), Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + name), PutMeasuresTask.FLAG_SILENT).execute();
                }
            }
        }
    }

    private void amountUpload() {
        if (!ConnectivityReceiver.isDataAvailable(getApplicationContext())) {
            Log.e(TAG, "Amount : No data connection available");
            waitForDataAmount = true;
        } else {
            waitForDataAmount = false;
            Cursor cursor = getContentResolver().query(SensAppContract.Measure.CONTENT_URI, null, null, null, null);

            // dataSent is kind of a semaphore here.
            if(amountData <= cursor.getCount() && !dataSent){
                dataSent = true;
                Cursor composites = getContentResolver().query(SensAppContract.Composite.CONTENT_URI, null, null, null, null);
                String compositeNames[] = new String[composites.getCount()];
                composites.moveToFirst();

                for(int i=0; i<composites.getCount(); i++){
                    compositeNames[i] = composites.getString(composites.getColumnIndex(SensAppContract.Composite.NAME));
                    composites.moveToNext();
                }
                new PutMeasuresTask(this, taskIdGen(), getApplicationContext(), SensAppContract.Measure.CONTENT_URI, PutMeasuresTask.FLAG_SILENT).execute();
                new DeleteMeasuresTask(this, SensAppContract.Measure.CONTENT_URI).execute(SensAppContract.Measure.UPLOADED + " = 1");
                for(String name : compositeNames)
                    new PostCompositeRestTask(this, name).execute();
            }
            if(cursor.getCount() < amountData)
                dataSent = false;
        }
    }

    static public void setAmountData(int set){
        amountData = set;
    }
	
	private int taskIdGen() {
		return ++ lastTaskStarted;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onTaskFinished(int id) {
		taskBuffer.add(id);
		while (taskBuffer.contains(lastConsecutiveTaskEnded + 1)) {
			lastConsecutiveTaskEnded ++;
			taskBuffer.remove((Integer) lastConsecutiveTaskEnded);
		}
		if (lastTaskStarted == lastConsecutiveTaskEnded) {
			stopSelf();
		}
	}
}
