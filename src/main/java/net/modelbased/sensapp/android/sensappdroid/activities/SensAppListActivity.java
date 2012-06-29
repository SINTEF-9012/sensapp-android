package net.modelbased.sensapp.android.sensappdroid.activities;

import net.modelbased.sensapp.android.sensappdroid.R;
import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.database.MeasureTable;
import net.modelbased.sensapp.android.sensappdroid.database.SensorTable;
import net.modelbased.sensapp.android.sensappdroid.restservice.PushDataTest;
import net.modelbased.sensapp.android.sensappdroid.utils.DeleteMeasuresTask;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SensAppListActivity extends ListActivity implements LoaderCallbacks<Cursor> {
	
	public static final String ACTION_UPLOAD = "net.modelbased.sensapp.android.sensappdroid.ACTION_UPLOAD";
	public static final String ACTION_FLUSH_ALL = "net.modelbased.sensapp.android.sensappdroid.ACTION_FLUSH_ALL";
	public static final String ACTION_FLUSH_UPLOADED = "net.modelbased.sensapp.android.sensappdroid.ACTION_FLUSH_UPLOADED";
	
	private static final String TAG = SensAppListActivity.class.getSimpleName();
	private static final int MENU_DELETE_ID = Menu.FIRST + 1;
	private static final int MEASURE_LOADER = 10;
	private static final int SENSOR_LOADER = 20;
	
	private SimpleCursorAdapter adapterMeasure;
	private SimpleCursorAdapter adapterSensors;
	private int activeAdapter;
	private Intent intentService;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "__ON_CREATE__");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measure_list);
        initAdapters();
		registerForContextMenu(getListView());
		intentService = new Intent(this, SensAppService.class);
    }

    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MENU_DELETE_ID, 0, R.string.menu_delete);
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			new DeleteMeasuresTask(this, (int) info.id).execute();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (activeAdapter == MEASURE_LOADER) {
			Intent i = new Intent(this, MeasureActivity.class);
			Uri measureUri = Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + id);
			i.putExtra(SensAppCPContract.Measure.CONTENT_ITEM_TYPE, measureUri);
			startActivity(i);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(this, SensAppService.class);
		switch (item.getItemId()) {
		case R.id.insert:
			PushDataTest.pushData(this);
			return true;
		case R.id.upload:
			i.setAction(ACTION_UPLOAD);
			startService(i);
			return true;
		case R.id.stop_service:
			stopService(intentService);
			return true;
		case R.id.flush_database:
			i.setAction(ACTION_FLUSH_ALL);
			startService(i);
			return true;
		case R.id.flush_uploaded:
			i.setAction(ACTION_FLUSH_UPLOADED);
			startService(i);
			return true;
		case R.id.view:
			if (activeAdapter == MEASURE_LOADER) {
				getListView().setAdapter(adapterSensors);
				activeAdapter = SENSOR_LOADER;
			} else if (activeAdapter == SENSOR_LOADER) { 
				getListView().setAdapter(adapterMeasure);
				activeAdapter = MEASURE_LOADER;
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initAdapters() {
		adapterMeasure = new SimpleCursorAdapter(this, R.layout.measure_row, null, new String[]{SensAppCPContract.Measure.VALUE}, new int[]{R.id.label}, 0);
		adapterSensors = new SimpleCursorAdapter(this, R.layout.sensor_row, null, new String[]{SensAppCPContract.Sensor.NAME}, new int[]{R.id.label}, 0);
		getLoaderManager().initLoader(MEASURE_LOADER, null, this);
		getLoaderManager().initLoader(SENSOR_LOADER, null, this);
		setListAdapter(adapterMeasure);
		activeAdapter = MEASURE_LOADER;
	}
    
	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "__ON_CREATE_LOADER__ / id: " + id);
		if (id == MEASURE_LOADER) {
			String[] projection = {MeasureTable.COLUMN_ID, MeasureTable.COLUMN_VALUE};
			CursorLoader cursorLoader = new CursorLoader(this, SensAppCPContract.Measure.CONTENT_URI, projection, null, null, null);
			return cursorLoader;
		} else if (id == SENSOR_LOADER) {
			String[] projection = {SensorTable.COLUMN_NAME};
			CursorLoader cursorLoader = new CursorLoader(this, SensAppCPContract.Sensor.CONTENT_URI, projection, null, null, null);
			return cursorLoader;
		} else {
			throw new IllegalArgumentException("Bad cursor loader id: " + id);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.d(TAG, "__ON_LOAD_FINISHED__");
		switch (loader.getId()) {
		case MEASURE_LOADER:
			adapterMeasure.swapCursor(data);
			break;
		case SENSOR_LOADER:
			adapterSensors.swapCursor(data);
			break;
		default:
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, "__ON_LOAD_RESET__");
		switch (loader.getId()) {
		case MEASURE_LOADER:
			adapterMeasure.swapCursor(null);
			break;
		case SENSOR_LOADER:
			adapterSensors.swapCursor(null);
			break;
		default:
			break;
		}
	}
}