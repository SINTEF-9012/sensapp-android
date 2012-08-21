package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.UpdateMeasuresTask;
import org.sensapp.android.sensappdroid.datarequests.UpdateSensorsTask;
import org.sensapp.android.sensappdroid.fragments.SensorListFragment.OnSensorSelectedListener;
import org.sensapp.android.sensappdroid.preferences.PreferencesActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SensorsActivity extends Activity implements OnSensorSelectedListener {
	
	private static final String TAG = SensorsActivity.class.getSimpleName();
	private static final int DIALOG_ADD_SENSORS_TO_COMPOSITE = 1;
	private static final int DIALOG_NEW_COMPOSITE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "__ON_CREATE__");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors);
        startService(new Intent(this, SensAppService.class));
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sensors_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.composites:
			startActivity(new Intent(this, CompositesActivity.class));
			return true;
		case R.id.new_composite:
			showDialog(DIALOG_NEW_COMPOSITE);
			return true;
		case R.id.upload_all:
			i = new Intent(this, SensAppService.class);
			i.setAction(SensAppService.ACTION_UPLOAD);
			i.setData(SensAppCPContract.Measure.CONTENT_URI);
			startService(i);
			return true;
		case R.id.change_view_to_measures:
			i = new Intent(this, MeasuresActivity.class);
			i.setData(SensAppCPContract.Measure.CONTENT_URI);
			startActivity(i);
			return true;
		case R.id.set_not_uploaded:
			ContentValues valuesS = new ContentValues();
			valuesS.put(SensAppCPContract.Sensor.UPLOADED, 0);
			new UpdateSensorsTask(this, SensAppCPContract.Sensor.UPLOADED + " = 1", valuesS).execute();
			ContentValues valuesM = new ContentValues();
			valuesM.put(SensAppCPContract.Measure.UPLOADED, 0);
			new UpdateMeasuresTask(this, SensAppCPContract.Measure.UPLOADED + " = 1", valuesM).execute();
			return true;
		case R.id.preferences:
			startActivity(new Intent(this, PreferencesActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ADD_SENSORS_TO_COMPOSITE:
			final Cursor cursor = getContentResolver().query(SensAppCPContract.Sensor.CONTENT_URI, new String[]{SensAppCPContract.Sensor.NAME, SensAppCPContract.Sensor.UPLOADED}, null, null, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(SensorsActivity.this)
                .setTitle("Add sensors to the composite")
                .setMultiChoiceItems(cursor,
                		SensAppCPContract.Sensor.UPLOADED, 
                        SensAppCPContract.Sensor.NAME,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                            	
                            }
                        })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
              
                	}
                });
			return builder.create();
        case DIALOG_NEW_COMPOSITE:
            LayoutInflater factory = LayoutInflater.from(this);
            final View newCompositeView = factory.inflate(R.layout.alert_dialog_new_composite, null);
            return new AlertDialog.Builder(SensorsActivity.this)
                .setTitle("New composite")
                .setView(newCompositeView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                       //showDialog(DIALOG_ADD_SENSORS_TO_COMPOSITE);
                    	String name = ((EditText) newCompositeView.findViewById(R.id.composite_name_edit)).getText().toString();
                    	String description = ((EditText) newCompositeView.findViewById(R.id.composite_description_edit)).getText().toString();
                    	ContentValues values = new ContentValues();
                    	values.put(SensAppCPContract.Composite.NAME, name);
                    	values.put(SensAppCPContract.Composite.DESCRIPTION, description);
                    	getContentResolver().insert(SensAppCPContract.Composite.CONTENT_URI, values);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
		}
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}

	@Override
	public void onSensorSelected(Uri uri) {
		Intent i = new Intent(this, MeasuresActivity.class);
		i.setData(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + uri.getLastPathSegment()));
		startActivity(i);
	}
}