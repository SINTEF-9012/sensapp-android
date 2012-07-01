package net.modelbased.sensapp.android.sensappdroid.fragments;

import net.modelbased.sensapp.android.sensappdroid.R;
import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.database.SensorTable;
import net.modelbased.sensapp.android.sensappdroid.utils.DeleteSensorsTask;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SensorListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static String TAG = SensorListFragment.class.getName();
	private static final int MENU_DELETE_ID = Menu.FIRST + 1;

	private SimpleCursorAdapter adapter;
	private OnSensorSelectedListener sensorSelectedListener;
	
	public interface OnSensorSelectedListener {
		public void onSensorSelected(Uri uri);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			sensorSelectedListener = (OnSensorSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnMeasureSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sensor_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initAdapters();
		registerForContextMenu(getListView());
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = adapter.getCursor();
		sensorSelectedListener.onSensorSelected(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.NAME))));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MENU_DELETE_ID, 0, "Delete sensor");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			Cursor c = adapter.getCursor();
			c.moveToPosition(info.position);
			new DeleteSensorsTask(getActivity()).execute(c.getString(c.getColumnIndexOrThrow(SensAppCPContract.Sensor.NAME)));
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private void initAdapters() {
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.sensor_row, null, new String[]{SensAppCPContract.Sensor.NAME}, new int[]{R.id.label}, 0);
		getLoaderManager().initLoader(0, null, this);
		setListAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "__ON_CREATE_LOADER__");
		String[] projection = {SensorTable.COLUMN_NAME};
		CursorLoader cursorLoader = new CursorLoader(getActivity(), SensAppCPContract.Sensor.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.d(TAG, "__ON_LOAD_FINISHED__");
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, "__ON_LOAD_RESET__");
		adapter.swapCursor(null);
	}

}
