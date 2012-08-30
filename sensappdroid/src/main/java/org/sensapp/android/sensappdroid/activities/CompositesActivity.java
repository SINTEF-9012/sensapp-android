package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.datarequests.UpdateMeasuresTask;
import org.sensapp.android.sensappdroid.datarequests.UpdateSensorsTask;
import org.sensapp.android.sensappdroid.fragments.CompositeListFragment.OnCompositeSelectedListener;
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

public class CompositesActivity extends Activity implements OnCompositeSelectedListener {
	
	private static final String TAG = CompositesActivity.class.getSimpleName();
	private static final int DIALOG_ADD_SENSORS_TO_COMPOSITE = 1;
	private static final int DIALOG_NEW_COMPOSITE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "__ON_CREATE__");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.composites);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.composites_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.new_composite:
			showDialog(DIALOG_NEW_COMPOSITE, null);
			return true;
		case R.id.sensors:
			i = new Intent(this, SensorsActivity.class);
			i.setData(SensAppCPContract.Sensor.CONTENT_URI);
			startActivity(i);
			return true;
		case R.id.upload_all:
			i = new Intent(this, SensAppService.class);
			i.setAction(SensAppService.ACTION_UPLOAD);
			i.setData(SensAppCPContract.Measure.CONTENT_URI);
			startService(i);
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
	protected Dialog onCreateDialog(int id, final Bundle args) {
		switch (id) {
		case DIALOG_ADD_SENSORS_TO_COMPOSITE:
			final String compositeName = args.getString(SensAppCPContract.Composite.NAME);
			final Cursor cursor = getContentResolver().query(Uri.parse(SensAppCPContract.Composite.CONTENT_URI + "/managesensors/" + compositeName), null, null, null, null);
			String[] sensorNames = new String[cursor.getCount()];
			boolean[] sensorStatus = new boolean[cursor.getCount()];
			for (int i = 0 ; cursor.moveToNext() ; i ++) {
				sensorNames[i] = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.NAME));
				sensorStatus[i] = cursor.getInt(cursor.getColumnIndexOrThrow("status")) == 1;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(CompositesActivity.this)
			.setTitle("Add sensors to " + compositeName + " composite")
			.setMultiChoiceItems(sensorNames, sensorStatus, 
					new DialogInterface.OnMultiChoiceClickListener() {
				public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
					cursor.moveToPosition(whichButton);
					String sensorName = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.NAME));
					if (isChecked) {
						ContentValues values = new ContentValues();
						values.put(SensAppCPContract.Compose.SENSOR, sensorName);
						values.put(SensAppCPContract.Compose.COMPOSITE, compositeName);
						getContentResolver().insert(SensAppCPContract.Compose.CONTENT_URI, values);
					} else {
						String where = SensAppCPContract.Compose.SENSOR + " = \"" + sensorName + "\" AND " + SensAppCPContract.Compose.COMPOSITE + " = \"" + compositeName + "\"";
						getContentResolver().delete(SensAppCPContract.Compose.CONTENT_URI, where, null);	
					}
				}
			})
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					removeDialog(CompositesActivity.DIALOG_ADD_SENSORS_TO_COMPOSITE);
					cursor.close();
				}
			});
			return builder.create();
		case DIALOG_NEW_COMPOSITE:
			LayoutInflater factory = LayoutInflater.from(this);
			final View newCompositeView = factory.inflate(R.layout.alert_dialog_new_composite, null);
			return new AlertDialog.Builder(CompositesActivity.this)
			.setTitle("New composite")
			.setView(newCompositeView)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
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
	public void onCompositeSelected(Uri uri) {
		Intent i = new Intent(this, SensorsActivity.class);
		i.setData(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/composite/" + uri.getLastPathSegment()));
		startActivity(i);
	}

	@Override
	public void onCompositeSensorsManagement(Uri uri) {
		Bundle bundle = new Bundle();
		bundle.putString(SensAppCPContract.Composite.NAME, uri.getLastPathSegment());
		showDialog(DIALOG_ADD_SENSORS_TO_COMPOSITE, bundle);
	}
}