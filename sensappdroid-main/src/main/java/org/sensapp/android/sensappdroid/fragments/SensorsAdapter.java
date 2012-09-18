package org.sensapp.android.sensappdroid.fragments;

import java.util.Hashtable;

import org.sensapp.android.sensappdroid.R;
import org.sensapp.android.sensappdroid.contract.SensAppContract;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorsAdapter extends CursorAdapter {
	
	private Hashtable<String, Integer> counts =  new Hashtable<String, Integer>();

	public SensorsAdapter(Context context, Cursor c) {
		super(context, c, 0);
	}
	
	public Hashtable<String, Integer> getCounts() {
		return counts;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		//Log.e("DEBUG", "Bind: " + cursor.getString(cursor.getColumnIndex(SensAppContract.Sensor.NAME)));
		byte[] byteIcon = cursor.getBlob(cursor.getColumnIndex(SensAppContract.Sensor.ICON));
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		if (byteIcon != null) {
			icon.setImageBitmap(BitmapFactory.decodeByteArray(byteIcon, 0, byteIcon.length));
		} else {
			icon.setImageResource(R.drawable.ic_launcher);
		}
		String sensor = cursor.getString(cursor.getColumnIndex(SensAppContract.Sensor.NAME)); 
		TextView name = (TextView) view.findViewById(R.id.name);
		name.setText(sensor);
		TextView count = (TextView) view.findViewById(R.id.count);
		if (counts.get(sensor) != null) {
			count.setText("(" + String.valueOf(counts.get(sensor)) + ")");
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = LayoutInflater.from(context).inflate(R.layout.sensor_row, parent, false);
		//bindView(v, context, cursor);
		return v;
	}
}