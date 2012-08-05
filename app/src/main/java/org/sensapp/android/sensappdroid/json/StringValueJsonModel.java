package org.sensapp.android.sensappdroid.json;

public class StringValueJsonModel extends ValueJsonModel {
	
	private String sv;
	
	public StringValueJsonModel() {
	}
	
	public StringValueJsonModel(String sv, long t) {
		super(t);
		this.sv = sv;
	}
	
	public String getSv() {
		return sv;
	}
	
	public void setSv(String sv) {
		this.sv = sv;
	}
}
