/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.fragments;

import java.util.ArrayList;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.activities.SensAppService;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.datarequests.DeleteSensorsTask;
import org.sensapp.android.sensappdroid.preferences.PreferencesActivity;

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
import android.widget.TextView;

public class SensorListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static final int MENU_DELETE_ID = Menu.FIRST + 1;
	private static final int MENU_UPLOAD_ID = Menu.FIRST + 2;

	private SensorsAdapter adapter;
	private OnSensorSelectedListener sensorSelectedListener;
	private LoadSensorCounts loadCounts;

	public interface OnSensorSelectedListener {
		public void onSensorSelected(Uri uri);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			sensorSelectedListener = (OnSensorSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnSensorSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sensor_list, container, false);
	}
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		adapter = new SensorsAdapter(getActivity(), null);
		getLoaderManager().initLoader(0, null, this);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sensors_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.upload_all:
			i = new Intent(getActivity(), SensAppService.class);
			i.setAction(SensAppService.ACTION_UPLOAD);
			i.setData(SensAppContract.Measure.CONTENT_URI);
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
		menu.add(0, MENU_DELETE_ID, 0, "Delete sensor");
		menu.add(0, MENU_UPLOAD_ID, 0, "Upload measures");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Cursor c = adapter.getCursor();
		c.moveToPosition(info.position);
		String sensorName = c.getString(c.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
		switch (item.getItemId()) {
		case MENU_DELETE_ID:
			new DeleteSensorsTask(getActivity()).execute(sensorName);
			return true;
		case MENU_UPLOAD_ID:
			Intent i = new Intent(getActivity(), SensAppService.class);
			i.setAction(SensAppService.ACTION_UPLOAD);
			i.setData(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + sensorName));
			getActivity().startService(i);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = adapter.getCursor();
		sensorSelectedListener.onSensorSelected(Uri.parse(SensAppContract.Sensor.CONTENT_URI + "/" + cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME))));
	}
	
	@Override
	public void onStop() {
		if (loadCounts != null) {
			loadCounts.terminate();
		}
		super.onStop();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = {SensAppContract.Sensor.NAME, SensAppContract.Sensor.ICON};
		Uri uri = getActivity().getIntent().getData();
		if (uri == null) {
			uri = SensAppContract.Sensor.CONTENT_URI;
		} else {
			((TextView) getActivity().findViewById(android.R.id.empty)).setText(getString(R.string.no_sensors) + " in " + uri.getLastPathSegment() + " composite");
		}
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, projection, null, null, null);
		return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		loadCounts = new LoadSensorCounts(data);
		loadCounts.start();
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	public class LoadSensorCounts extends Thread {
		private ArrayList<String> names = new ArrayList<String>();
		private boolean terminate = false;
		public LoadSensorCounts(Cursor cursor) {
			for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {			
				names.add(cursor.getString(cursor.getColumnIndex(SensAppContract.Sensor.NAME)));
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
				Cursor c = getActivity().getContentResolver().query(Uri.parse(SensAppContract.Measure.CONTENT_URI + "/" + name), new String[]{SensAppContract.Measure.ID}, null, null, null);
				if (c != null) {
					if (!terminate) {
						adapter.getCounts().put(name, c.getCount());
						if (getActivity() != null) {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();	
								}
							});
						}
					}
					c.close();
				}
			}
		}
	}
}
