package org.sensapp.android.sensappdroid.models;

public class NumericalMeasure extends Measure {
	
	private float value;
	
	public NumericalMeasure() {
		super();
	}
	
	public NumericalMeasure(int id, String sensor, float value, long time, long basetime, boolean uploaded) {
		super(id, sensor, time, basetime, uploaded);
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "MEASURE/ Id: " + getId() + " - Sensor: " + getSensor() + " - Value: " + getValue() + " - Time: " + getTime() + " - Uploaded:" + isUploaded();
	}
}
