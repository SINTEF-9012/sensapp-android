package org.sensapp.android.sensappdroid.models;

import java.util.ArrayList;

import android.net.Uri;

public class Composite {
	
	private String name;
	private String description;
	private Uri uri; 
	private ArrayList<Uri> sensors;
	
	public Composite() {
		name = new String();
		description = new String();
		sensors = new ArrayList<Uri>();
	}
	
	public Composite(String name, String description, Uri uri) {
		this.name = name;
		this.description = description;
		this.uri = uri;
	}
	
	public Composite(String name, String description, Uri uri, ArrayList<Uri> sensors) {
		this.name = name;
		this.description = description;
		this.uri = uri;
		this.sensors = sensors;
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
	
	public Uri getUri() {
		return uri;
	}
	
	public void setUri(Uri uri) {
		this.uri = uri;
	}
	
	public ArrayList<Uri> getSensors() {
		return sensors;
	}
	
	public void setSensors(ArrayList<Uri> sensors) {
		this.sensors = sensors;
	}
}
