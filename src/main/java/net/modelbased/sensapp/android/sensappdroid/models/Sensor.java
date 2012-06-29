package net.modelbased.sensapp.android.sensappdroid.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import net.modelbased.sensapp.android.sensappdroid.contentprovider.SensAppCPContract;
import net.modelbased.sensapp.android.sensappdroid.database.DatabaseException;

public class Sensor {
	
	private String name;
	private String description;
	private String backend;
	private String template;
	private String unit;
	private boolean uploaded;
	
	public static Sensor fromDatabase(Context context, String name) throws DatabaseException {
		String[] projection = {SensAppCPContract.Sensor.DESCRIPTION, SensAppCPContract.Sensor.BACKEND, SensAppCPContract.Sensor.TEMPLATE, SensAppCPContract.Sensor.UNIT, SensAppCPContract.Sensor.UPLOADED};
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + name), projection, null, null, null);
		if (cursor == null) {
			throw new DatabaseException("Null cursor");
		}
		if (!cursor.moveToFirst()) {
			throw new DatabaseException("Sensor does not exist: " + name);
		}
		String description = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.DESCRIPTION));
		String backend = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.BACKEND));
		String template = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.TEMPLATE));
		String unit = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UNIT));
		int uploaded = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Sensor.UPLOADED));
		cursor.close();
		return new Sensor(name, description, backend, template, unit, uploaded == 0 ? false : true);
	}
	
	public Sensor() {
		name = new String();
		description = new String();
		backend = new String();
		template = new String();
		unit = new String();
		uploaded = false;
	}
	
	public Sensor(String name, String description, String backend, String template, String unit, boolean uploaded) {
		this.name = name;
		this.description = description;
		this.backend = backend;
		this.template = template;
		this.unit = unit;
		this.uploaded = uploaded;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getBackend() {
		return backend;
	}
	
	public void setBackend(String backend) {
		this.backend = backend;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public boolean isUploaded() {
		return uploaded;
	}
	
	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}
	
	public void toDatabase (Context context) throws DatabaseException {
		String[] projection = {SensAppCPContract.Sensor.NAME};
		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + name), projection, null, null, null);
		if (cursor == null) {
			throw new DatabaseException("Null cursor");
		}
		ContentValues cv = new ContentValues();
		cv.put(SensAppCPContract.Sensor.NAME, name);
		cv.put(SensAppCPContract.Sensor.DESCRIPTION, description);
		cv.put(SensAppCPContract.Sensor.BACKEND, backend);
		cv.put(SensAppCPContract.Sensor.TEMPLATE, template);
		cv.put(SensAppCPContract.Sensor.UNIT, unit);
		cv.put(SensAppCPContract.Sensor.UPLOADED, uploaded ? 1 : 0);
		if (cursor.getCount() == 0) {
			context.getContentResolver().insert(SensAppCPContract.Sensor.CONTENT_URI, cv);
		} else {
			context.getContentResolver().update(Uri.parse(SensAppCPContract.Sensor.CONTENT_URI + "/" + name), cv, null, null);
		}
		cursor.close();
	}
}
