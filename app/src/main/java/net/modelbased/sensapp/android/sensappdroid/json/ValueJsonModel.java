package net.modelbased.sensapp.android.sensappdroid.json;

public class ValueJsonModel {
	
	private int v;
	private long t;
	
	public ValueJsonModel() {
	}
	
	public ValueJsonModel(int v, long t) {
		this.v = v;
		this.t = t;
	}
	
	public int getV() {
		return v;
	}
	public void setV(int v) {
		this.v = v;
	}
	public long getT() {
		return t;
	}
	public void setT(long t) {
		this.t = t;
	}

}
