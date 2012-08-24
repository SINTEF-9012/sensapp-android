package org.sensapp.android.sensappdroid.activities;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.fragments.CompositeListFragment.OnCompositeSelectedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
		inflater.inflate(R.menu.sensors_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_composite:
			showDialog(DIALOG_NEW_COMPOSITE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
        case DIALOG_NEW_COMPOSITE:
            LayoutInflater factory = LayoutInflater.from(this);
            final View newCompositeView = factory.inflate(R.layout.alert_dialog_new_composite, null);
            return new AlertDialog.Builder(CompositesActivity.this)
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
	protected Dialog onCreateDialog(int id, final Bundle args) {
		switch (id) {
		case DIALOG_ADD_SENSORS_TO_COMPOSITE:
			final String name = args.getString(SensAppCPContract.Composite.NAME);
			final Cursor cursor = getContentResolver().query(Uri.parse(SensAppCPContract.Composite.CONTENT_URI + "/managesensors/" + name), null, null, null, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(CompositesActivity.this)
                .setTitle("Add sensors to " + name + " composite")
                .setMultiChoiceItems(cursor,
                		"status", 
                        SensAppCPContract.Sensor.NAME,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                            	cursor.moveToPosition(whichButton);
                            	String sensorName = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.NAME));
                            	if (isChecked) {
                            		ContentValues values = new ContentValues();
                            		values.put(SensAppCPContract.Compose.SENSOR, sensorName);
                            		values.put(SensAppCPContract.Compose.COMPOSITE, name);
                            		getContentResolver().insert(SensAppCPContract.Compose.CONTENT_URI, values);
                            		removeDialog(CompositesActivity.DIALOG_ADD_SENSORS_TO_COMPOSITE);
                            		showDialog(CompositesActivity.DIALOG_ADD_SENSORS_TO_COMPOSITE, args);
                            	} else {
                            		String where = SensAppCPContract.Compose.SENSOR + " = \"" + sensorName + "\" AND " + SensAppCPContract.Compose.COMPOSITE + " = \"" + name + "\"";
                            		getContentResolver().delete(SensAppCPContract.Compose.CONTENT_URI, where, null);
                            		removeDialog(CompositesActivity.DIALOG_ADD_SENSORS_TO_COMPOSITE);
                            		showDialog(CompositesActivity.DIALOG_ADD_SENSORS_TO_COMPOSITE, args);
                            	}
                            }
                        })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	removeDialog(CompositesActivity.DIALOG_ADD_SENSORS_TO_COMPOSITE);
                    }
                });
			return builder.create();
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
//		Intent i = new Intent(this, CompositeActivity.class);
//		i.setData(Uri.parse(SensAppCPContract.Composite.CONTENT_URI + "/" + uri.getLastPathSegment()));
//		startActivity(i);
	}

	@Override
	public void onCompositeSensorsManagement(Uri uri) {
		Bundle bundle = new Bundle();
		bundle.putString(SensAppCPContract.Composite.NAME, uri.getLastPathSegment());
		showDialog(DIALOG_ADD_SENSORS_TO_COMPOSITE, bundle);
	}
}