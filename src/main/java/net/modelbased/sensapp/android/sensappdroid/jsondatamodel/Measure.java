package net.modelbased.sensapp.android.sensappdroid.jsondatamodel;

public class Measure {
	
	private int id;
	private String sensor;
	private int value;
	private long time;
	private boolean uploaded;
	
	public Measure() {
		sensor = new String();
	}
	
	public Measure(int id, String sensor, int value, long time, boolean uploaded) {
		this.id = id;
		this.sensor = sensor;
		this.value = value;
		this.time = time;
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

	public boolean isUploaded() {
		return uploaded;
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

	@Override
	public String toString() {
		return "MEASURE/ Id: " + id + " - Sensor: " + sensor + " - Value: " + value + " - Time: " + time + " - Uploaded:" + uploaded;
	}
}
