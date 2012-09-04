package org.sensapp.android.sensappdroid.json;

public class NumericalValueJsonModel extends ValueJsonModel {
	
	private float v;
	
	public NumericalValueJsonModel() {
		super();
	}
	
	public NumericalValueJsonModel(float v, long t) {
		super(t);
		this.v = v;
	}
	
	public float getV() {
		return v;
	}
	
	public void setV(int v) {
		this.v = v;
	}
}
