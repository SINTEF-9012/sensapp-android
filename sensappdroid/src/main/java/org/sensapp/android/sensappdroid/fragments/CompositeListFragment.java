package org.sensapp.android.sensappdroid.fragments;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contentprovider.SensAppContract;
import org.sensapp.android.sensappdroid.datarequests.DeleteCompositeTask;
import org.sensapp.android.sensappdroid.restrequests.PostCompositeRestTask;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
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

public class CompositeListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static final int MENU_DELETE_ID = Menu.FIRST + 1;
	private static final int MENU_MANAGESENSORS_ID = Menu.FIRST + 2;
	private static final int MENU_UPLOAD_ID = Menu.FIRST + 3;

	private SimpleCursorAdapter adapter;
	private OnCompositeSelectedListener compositeSelectedListener;
	
	public interface OnCompositeSelectedListener {
		public void onCompositeSelected(Uri uri);
		public void onCompositeSensorsManagement(Uri uri);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			compositeSelectedListener = (OnCompositeSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnMeasureSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.composite_list, container, false);
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
		compositeSelectedListener.onCompositeSelected(Uri.parse(SensAppContract.Composite.CONTENT_URI + "/" + cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Composite.NAME))));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MENU_DELETE_ID, 0, "Delete composite");
		menu.add(0, MENU_MANAGESENSORS_ID, 0, "Manage sensors");
		menu.add(0, MENU_UPLOAD_ID, 0, "Upload composite");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Cursor c = adapter.getCursor();
		c.moveToPosition(info.position);
		String name = c.getString(c.getColumnIndexOrThrow(SensAppContract.Composite.NAME));
		switch (item.getItemId()) {
		case MENU_DELETE_ID:
			new DeleteCompositeTask(getActivity()).execute(name);
			return true;
		case MENU_MANAGESENSORS_ID:
			compositeSelectedListener.onCompositeSensorsManagement(Uri.parse(SensAppContract.Composite.CONTENT_URI + "/" + name));
			return false;
		case MENU_UPLOAD_ID:
			new PostCompositeRestTask(getActivity(), name).execute();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private void initAdapters() {
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.composite_row, null, new String[]{SensAppContract.Composite.NAME}, new int[]{R.id.label}, 0);
		getLoaderManager().initLoader(0, null, this);
		setListAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = {SensAppContract.Composite.NAME};
		CursorLoader cursorLoader = new CursorLoader(getActivity(), SensAppContract.Composite.CONTENT_URI, projection, null, null, null);
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
