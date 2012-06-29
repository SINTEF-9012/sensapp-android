package net.modelbased.sensapp.android.sensappdroid.models;

public class Measure {
	
	private int id;
	private String sensor;
	private int value;
	private long time;
	private long basetime;
	private boolean uploaded;
	
	public Measure() {
		sensor = new String();
	}
	
	public Measure(int id, String sensor, int value, long time,long basetime, boolean uploaded) {
		this.id = id;
		this.sensor = sensor;
		this.value = value;
		this.time = time;
		this.basetime = basetime;
		this.uploaded = uploaded;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSensor() {
		return sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getBasetime() {
		return basetime;
	}

	public void setBasetime(long basetime) {
		this.basetime = basetime;
	}

	public boolean isUploaded() {
		return uploaded;
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

//	public static Measure fromDatabase(Context context, int id) throws DatabaseException {
//		Cursor cursor = context.getContentResolver().query(Uri.parse(SensAppCPContract.Measure.CONTENT_URI + "/" + id), null, null, null, null);
//		if (cursor == null) {
//			throw new DatabaseException("Null cursor");
//		}
//		if (!cursor.moveToFirst()) {
//			throw new DatabaseException("Measure does not exist: " + id);
//		}
//		int idDatabase = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.ID));
//		String sensor = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.SENSOR));
//		int value = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.VALUE));
//		long time = cursor.getLong(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.TIME));
//		int uploaded = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.UPLOADED));
//		cursor.close();
//		return new Measure(idDatabase, sensor, value, time, uploaded == 0 ? false : true);
//	}
//	
//	public static Hashtable<Integer, Measure> fromDatabaseAll(Context context) throws DatabaseException {
//		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, null, null, null, null);
//		if (cursor == null) {
//			throw new DatabaseException("Null cursor");
//		}
//		Hashtable<Integer, Measure> measures = new Hashtable<Integer, Measure>();
//		while (cursor.moveToNext()) {
//			int id = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.ID));
//			String sensor = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.SENSOR));
//			int value = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.VALUE));
//			long time = cursor.getLong(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.TIME));
//			int uploaded = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.UPLOADED));
//			Measure m = new Measure(id, sensor, value, time, uploaded == 0 ? false : true);
//			measures.put(id, m);
//		}
//		return measures;
//	}
//	
//	public static Hashtable<Integer, Measure> fromDatabaseNotUpload(Context context) throws DatabaseException {
//		String selection = SensAppCPContract.Measure.UPLOADED + " = 0";
//		Cursor cursor = context.getContentResolver().query(SensAppCPContract.Measure.CONTENT_URI, null, selection, null, null);
//		if (cursor == null) {
//			throw new DatabaseException("Null cursor");
//		}
//		Hashtable<Integer, Measure> measures = new Hashtable<Integer, Measure>();
//		while (cursor.moveToNext()) {
//			int id = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.ID));
//			String sensor = cursor.getString(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.SENSOR));
//			int value = cursor.getInt(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.VALUE));
//			long time = cursor.getLong(cursor.getColumnIndexOrThrow(SensAppCPContract.Measure.TIME));
//			Measure m = new Measure(id, sensor, value, time, false);
//			measures.put(id, m);
//		}
//		return measures;
//	}
	
	@Override
	public String toString() {
		return "MEASURE/ Id: " + id + " - Sensor: " + sensor + " - Value: " + value + " - Time: " + time + " - Uploaded:" + uploaded;
	}
}
