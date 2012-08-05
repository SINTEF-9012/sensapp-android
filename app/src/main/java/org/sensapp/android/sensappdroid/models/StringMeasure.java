package org.sensapp.android.sensappdroid.models;

public class StringMeasure extends Measure {
	
	private String value;
	
	public StringMeasure() {
		super();
	}
	
	public StringMeasure(int id, String sensor, String value, long time, long basetime, boolean uploaded) {
		super(id, sensor, time, basetime, uploaded);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "STRING MEASURE/ Id: " + getId() + " - Sensor: " + getSensor() + " - Value: " + getValue() + " - Time: " + getTime() + " - Uploaded:" + isUploaded();
	}
}
