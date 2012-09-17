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

public class MeasuresAdapter extends CursorAdapter {

	private Hashtable<String, byte[]> icons =  new Hashtable<String, byte[]>();
	
	private LayoutInflater inflater;
	private int indexSensor;
	private int indexValue;
	
	public MeasuresAdapter(Context context, Cursor c) {
		super(context, c);
		inflater = LayoutInflater.from(context);
	}
	
	public Hashtable<String, byte[]> getIcons() {
		return icons;
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if (newCursor != null) {
			indexSensor = newCursor.getColumnIndex(SensAppContract.Measure.SENSOR);
			indexValue = newCursor.getColumnIndex(SensAppContract.Measure.VALUE);
		}
		return super.swapCursor(newCursor);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView iconIv = (ImageView) view.findViewById(R.id.icon);
		TextView value = (TextView) view.findViewById(R.id.value);
		
		// Retrieve the icon if exits
		String sensor = cursor.getString(indexSensor);
		byte[] icon = icons.get(sensor); 
		if (icon != null) {
			iconIv.setImageBitmap(BitmapFactory.decodeByteArray(icon, 0, icon.length));
		}
		
		String valueString = cursor.getString(indexValue);
		// Take care of large values concatenation
		if (valueString.length() > 10) {
			valueString = valueString.substring(0, 6) + "...";
		}
		value.setText(valueString);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.measure_row, parent, false);
	}
}
