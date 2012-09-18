package org.sensapp.android.sensappdroid.fragments;

import java.util.ArrayList;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.activities.SensAppService;
import org.sensapp.android.sensappdroid.contract.SensAppContract;
import org.sensapp.android.sensappdroid.datarequests.DeleteCompositeTask;
import org.sensapp.android.sensappdroid.preferences.GeneralPrefFragment;
import org.sensapp.android.sensappdroid.preferences.PreferencesActivity;
import org.sensapp.android.sensappdroid.restrequests.PostCompositeRestTask;

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
import android.support.v4.app.DialogFragment;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CompositeListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static final int MENU_DELETE_ID = Menu.FIRST + 1;
	private static final int MENU_MANAGESENSORS_ID = Menu.FIRST + 2;
	private static final int MENU_UPLOAD_ID = Menu.FIRST + 3;

	private SimpleCursorAdapter adapter;
	private OnCompositeSelectedListener compositeSelectedListener;
	private NewCompositeDialogFragment newCompositedialog;
	
	public interface OnCompositeSelectedListener {
		public void onCompositeSelected(Uri uri);
	}
	
	public static class ManageCompositeDialogFragment extends DialogFragment {

		private static final String COMPOSITE_NAME = "composite_name";
		private ArrayList<String> sensorsAdded = new ArrayList<String>();
		private ArrayList<String> sensorsRemoved = new ArrayList<String>();
		private Cursor cursor;
		
		public static ManageCompositeDialogFragment newInstance(String compositeName) {
	        ManageCompositeDialogFragment frag = new ManageCompositeDialogFragment();
	        Bundle args = new Bundle();
	        args.putString(COMPOSITE_NAME, compositeName);
	        frag.setArguments(args);
	        return frag;
	    }
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	final String compositeName = getArguments().getString(COMPOSITE_NAME);
			cursor = getActivity().getContentResolver().query(Uri.parse(SensAppContract.Composite.CONTENT_URI + "/managesensors/" + compositeName), null, null, null, null);
			String[] sensorNames = new String[cursor.getCount()];
			boolean[] sensorStatus = new boolean[cursor.getCount()];
			for (int i = 0 ; cursor.moveToNext() ; i ++) {
				sensorNames[i] = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
				sensorStatus[i] = cursor.getInt(cursor.getColumnIndexOrThrow("status")) == 1;
			}
			return new AlertDialog.Builder(getActivity())
			.setTitle("Add sensors to " + compositeName + " composite")
			.setMultiChoiceItems(sensorNames, sensorStatus, 
					new DialogInterface.OnMultiChoiceClickListener() {
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					cursor.moveToPosition(which);
					String sensorName = cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Sensor.NAME));
					if (isChecked) {
						sensorsRemoved.remove(sensorName);
						sensorsAdded.add(sensorName);
					} else {
						sensorsAdded.remove(sensorName);
						sensorsRemoved.add(sensorName);
					}
				}
			})
			.setPositiveButton("Done", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					ContentValues values = new ContentValues();
					for (String name : sensorsAdded) {
						values.put(SensAppContract.Compose.SENSOR, name);
						values.put(SensAppContract.Compose.COMPOSITE, compositeName);
						getActivity().getContentResolver().insert(SensAppContract.Compose.CONTENT_URI, values);
						values.clear();
					} 
					for (String name : sensorsRemoved) {
						String where = SensAppContract.Compose.SENSOR + " = \"" + name + "\" AND " + SensAppContract.Compose.COMPOSITE + " = \"" + compositeName + "\"";
						getActivity().getContentResolver().delete(SensAppContract.Compose.CONTENT_URI, where, null);	
					}
					cursor.close();
				}
			}).create();
	    }

		@Override
		public void onCancel(DialogInterface dialog) {
			super.onCancel(dialog);
			if (cursor != null) {
				cursor.close();
			}
		}    
	}
	
	public static class NewCompositeDialogFragment extends DialogFragment {

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	LayoutInflater factory = LayoutInflater.from(getActivity());
	    	final View newCompositeView = factory.inflate(R.layout.alert_dialog_new_composite, null);
	    	return new AlertDialog.Builder(getActivity())
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
	    				String uri = GeneralPrefFragment.buildUri(PreferenceManager.getDefaultSharedPreferences(getActivity()), getResources());
	    				values.put(SensAppContract.Composite.URI, uri);
	    			} catch (IllegalStateException e) {
	    				e.printStackTrace();
					}	
					getActivity().getContentResolver().insert(SensAppContract.Composite.CONTENT_URI, values);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			})
			.create();
	    }
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
		setHasOptionsMenu(true);
		newCompositedialog = new NewCompositeDialogFragment();
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.composite_row, null, new String[]{SensAppContract.Composite.NAME}, new int[]{R.id.name}, 0);
		getLoaderManager().initLoader(0, null, this);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.composites_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.new_composite:
			newCompositedialog.show(getFragmentManager(), "NewCompositeDialog");
			return true;
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
			ManageCompositeDialogFragment.newInstance(name).show(getFragmentManager(), "ManageCompositeDialog");
			return true;
		case MENU_UPLOAD_ID:
			new PostCompositeRestTask(getActivity(), name).execute();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = adapter.getCursor();
		compositeSelectedListener.onCompositeSelected(Uri.parse(SensAppContract.Composite.CONTENT_URI + "/" + cursor.getString(cursor.getColumnIndexOrThrow(SensAppContract.Composite.NAME))));
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
