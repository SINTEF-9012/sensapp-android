package org.sensapp.android.sensappdroid.models;

abstract public class Measure {
	
	private int id;
	private String sensor;
	private long time;
	private long basetime;
	private boolean uploaded;
	
	public Measure() {
		sensor = new String();
	}
	
	public Measure(int id, String sensor, long time, long basetime, boolean uploaded) {
		this.id = id;
		this.sensor = sensor;
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
}
