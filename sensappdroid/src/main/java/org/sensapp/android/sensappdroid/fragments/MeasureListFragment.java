package org.sensapp.android.sensappdroid.fragments;

import java.util.ArrayList;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.activities.SensAppService;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.database.MeasureTable;
import org.sensapp.android.sensappdroid.datarequests.DeleteMeasuresTask;
import org.sensapp.android.sensappdroid.preferences.PreferencesActivity;
import org.sensapp.android.sensappdroid.restrequests.PutMeasuresTask;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class MeasureListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static final int MENU_DELETE_ID = Menu.FIRST + 1;
	private static final int MENU_UPLOAD_ID = Menu.FIRST + 2;

	private MeasuresAdapter adapter;
	private OnMeasureSelectedListener measureSelectedListener;
	
	public interface OnMeasureSelectedListener {
		public void onMeasureSelected(Uri uri);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			measureSelectedListener = (OnMeasureSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnMeasureSelectedListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.measure_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		adapter = new MeasuresAdapter(getActivity(), null);
		getLoaderManager().initLoader(0, null, this);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.measures_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(getActivity(), SensAppService.class);
		Uri uri = getActivity().getIntent().getData();
		if (uri == null) {
			uri = SensAppContract.Measure.CONTENT_URI;
		}
		i.setData(uri);
		switch (item.getItemId()) {
		case R.id.upload:
			i.setAction(SensAppService.ACTION_UPLOAD);
			getActivity().startService(i);
			return true;
		case R.id.delete_all_measures:
			i.setAction(SensAppService.ACTION_DELETE_LOCAL);
			getActivity().startService(i);
			return true;
		case R.id.delete_uploaded_measures:
			i.setAction(SensAppService.ACTION_DELETE_LOCAL);
			i.putExtra(SensAppService.EXTRA_UPLOADED_FILTER, true);
			getActivity().startService(i);
			return true;
		case R.id.preferences:
			startActivity(new Intent(getActivity(), PreferencesActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MENU_DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, MENU_UPLOAD_ID, 0, "Upload measure");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case MENU_DELETE_ID:			
			new DeleteMeasuresTask(getActivity(), SensAppContract.Measure.CONTENT_URI).execute(SensAppContract.Measure.ID + " = " + info.id);
			return true;
		case MENU_UPLOAD_ID:			
			new PutMeasuresTask(getActivity(), Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + info.id)).execute();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		measureSelectedListener.onMeasureSelected(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + id));
	}
	
	@Override
	public void onDestroyView() {
		if (loadMeasure != null) {
			loadMeasure.terminate();
		}
		super.onDestroyView();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = {MeasureTable.COLUMN_ID, MeasureTable.COLUMN_VALUE, MeasureTable.COLUMN_ICON, MeasureTable.COLUMN_SENSOR};
		Uri uri = getActivity().getIntent().getData();
		if (uri == null) {
			uri = SensAppContract.Measure.CONTENT_URI;
		}
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, projection, null, null, SensAppContract.Measure.TIME + " DESC LIMIT 100");
		return cursorLoader;
	}
	
	private LoadMeasureIcons loadMeasure; 
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		loadMeasure = new LoadMeasureIcons(data);
		loadMeasure.start();
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
	
	public class LoadMeasureIcons extends Thread {
		private boolean terminate = false;
		private ArrayList<String> names = new ArrayList<String>();
		public LoadMeasureIcons(Cursor cursor) {
			for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {			
				names.add(cursor.getString(cursor.getColumnIndex(SensAppContract.Measure.SENSOR)));
			}
		}
		public void terminate() {
			terminate = true;
		}
		@Override
		public void run() {
			for (String name : names) {
				if (terminate) {
					return;
				}
				Cursor c = getActivity().getContentResolver().query(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + name), new String[]{SensAppContract.Sensor.ICON}, null, null, null);
				if (c != null) {
					if (!terminate && c.moveToFirst()) {
						adapter.getIcons().put(name, c.getBlob(c.getColumnIndex(SensAppContract.Sensor.ICON)));
					}
					c.close();
				}
			}
			if (terminate) {
				return;
			}
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();	
				}
			});
		}
	}
}
