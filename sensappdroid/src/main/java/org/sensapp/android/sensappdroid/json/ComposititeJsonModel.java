package org.sensapp.android.sensappdroid.json;

import java.util.List;

public class ComposititeJsonModel {
	
	private String id;
	private String descr;
	private List<String> sensors;
	
	public ComposititeJsonModel(String id, String descr, List<String> sensors) {
		this.id = id;
		this.descr = descr;
		this.sensors = sensors;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public List<String> getSensors() {
		return sensors;
	}
	public void setSensors(List<String> sensors) {
		this.sensors = sensors;
	}
}
