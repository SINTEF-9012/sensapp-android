package org.sensapp.android.sensappdroid.fragments;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import org.sensapp.android.sensappdroid.database.MeasureTable;
import org.sensapp.android.sensappdroid.datarequests.DeleteMeasuresTask;

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

public class MeasureListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static String TAG = MeasureListFragment.class.getSimpleName();
	private static final int MENU_DELETE_ID = Menu.FIRST + 1;

	private SimpleCursorAdapter adapter;
	private OnMesureSelectedListener measureSelectedListener;
	
	public interface OnMesureSelectedListener {
		public void onMeasureSelected(Uri uri);
	}
	
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "__ON_ATTACH__");
		super.onAttach(activity);
		try {
			measureSelectedListener = (OnMesureSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnMeasureSelectedListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "__ON_CREATE__");
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.measure_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.measure_row, null, new String[]{SensAppCPContract.Measure.VALUE}, new int[]{R.id.label}, 0);
		getLoaderManager().initLoader(0, null, this);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		measureSelectedListener.onMeasureSelected(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + id));
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
			new DeleteMeasuresTask(getActivity(), SensAppCPContract.Measure.CONTENT_URI).execute(SensAppCPContract.Measure.ID + " = " + info.id);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "__ON_DESTROY__");
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "__ON_CREATE_LOADER__");
		String[] projection = {MeasureTable.COLUMN_ID, MeasureTable.COLUMN_VALUE};
		CursorLoader cursorLoader = new CursorLoader(getActivity(), getActivity().getIntent().getData(), projection, null, null, null);
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
