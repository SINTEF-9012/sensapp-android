package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.fragments.CompositeListFragment.OnCompositeSelectedListener;
import org.sensapp.android.sensappdroid.preferences.GeneralPrefFragment;
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
import android.preference.PreferenceManager;
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
        startService(new Intent(this, SensAppService.class));
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
//		case R.id.sensors:
//			startActivity(new Intent(this, SensorsActivity.class));
//			return true;
//		case R.id.measures:
//			i = new Intent(this, MeasuresActivity.class);
//			i.setData(SensAppContract.Measure.CONTENT_URI);
//			startActivity(i);
//			return true;
		case R.id.upload_all:
			i = new Intent(this, SensAppService.class);
			i.setAction(SensAppService.ACTION_UPLOAD);
			i.setData(SensAppContract.Measure.CONTENT_URI);
			startService(i);
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
			final String compositeName = args.getString(SensAppContract.Composite.NAME);
			final Cursor cursor = getContentResolver().query(Uri.parse(SensAppContract.Composite.CONTENT_URI + "/managesensors/" + compositeName), null, null, null, null);
			String[] sensorNames = new String[cursor.getCount()];
			boolean[] sensorStatus = new boolean[cursor.getCount()];
			for (int i = 0 ; cursor.moveToNext() ; i ++) {
				sensorNames[i] = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
				sensorStatus[i] = cursor.getInt(cursor.getColumnIndexOrThrow("status")) == 1;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(CompositesActivity.this)
			.setTitle("Add sensors to " + compositeName + " composite")
			.setMultiChoiceItems(sensorNames, sensorStatus, 
					new DialogInterface.OnMultiChoiceClickListener() {
				public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
					cursor.moveToPosition(whichButton);
					String sensorName = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
					if (isChecked) {
						ContentValues values = new ContentValues();
						values.put(SensAppContract.Compose.SENSOR, sensorName);
						values.put(SensAppContract.Compose.COMPOSITE, compositeName);
						getContentResolver().insert(SensAppContract.Compose.CONTENT_URI, values);
					} else {
						String where = SensAppContract.Compose.SENSOR + " = \"" + sensorName + "\" AND " + SensAppContract.Compose.COMPOSITE + " = \"" + compositeName + "\"";
						getContentResolver().delete(SensAppContract.Compose.CONTENT_URI, where, null);	
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
					values.put(SensAppContract.Composite.NAME, name);
					values.put(SensAppContract.Composite.DESCRIPTION, description);
					try {
						String uri = GeneralPrefFragment.buildUri(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()), getResources());
						values.put(SensAppContract.Composite.URI, uri);
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}	
					getContentResolver().insert(SensAppContract.Composite.CONTENT_URI, values);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
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
		Intent i = new Intent(this, CompositeActivity.class);
		i.setData(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/composite/" + uri.getLastPathSegment()));
		startActivity(i);
	}

	@Override
	public void onCompositeSensorsManagement(Uri uri) {
		Bundle bundle = new Bundle();
		bundle.putString(SensAppContract.Composite.NAME, uri.getLastPathSegment());
		showDialog(DIALOG_ADD_SENSORS_TO_COMPOSITE, bundle);
	}
}