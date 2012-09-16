package org.sensapp.android.sensappdroid.api;

public enum SensAppBackend {
	raw("raw");
	private String backend;
	private SensAppBackend(String backend) {
		this.backend = backend;
	}
	public String getBackend() {
		return backend;
	}
}
