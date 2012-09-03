package org.sensapp.android.sensappdroid.fragments;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.activities.SensAppService;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.database.SensorTable;
import org.sensapp.android.sensappdroid.datarequests.DeleteSensorsTask;
import org.sensapp.android.sensappdroid.restrequests.PostSensorRestTask;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

public class SensorListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static final int MENU_DELETE_ID = Menu.FIRST + 1;
	private static final int MENU_UPLOAD_ID = Menu.FIRST + 2;

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
		menu.add(0, MENU_UPLOAD_ID, 0, "Upload measures");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Cursor c = adapter.getCursor();
		c.moveToPosition(info.position);
		String sensorName = c.getString(c.getColumnIndexOrThrow(SensAppCPContract.Sensor.NAME));
		switch (item.getItemId()) {
		case MENU_DELETE_ID:
			new DeleteSensorsTask(getActivity()).execute(sensorName);
			return true;
		case MENU_UPLOAD_ID:
			//new PostSensorRestTask(getActivity(), sensorName);
			Intent i = new Intent(getActivity(), SensAppService.class);
			i.setAction(SensAppService.ACTION_UPLOAD);
			i.setData(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + sensorName));
			getActivity().startService(i);
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
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = {SensorTable.COLUMN_NAME};
		Uri uri = getActivity().getIntent().getData();
		if (uri == null) {
			uri = SensAppCPContract.Sensor.CONTENT_URI;
		} else {
			((TextView) getActivity().findViewById(android.R.id.empty)).setText(getString(R.string.no_sensors) + " in " + uri.getLastPathSegment() + " composite");
		}
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
