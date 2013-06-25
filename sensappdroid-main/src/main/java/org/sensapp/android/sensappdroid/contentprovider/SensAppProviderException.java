package org.sensapp.android.sensappdroid.contentprovider;

public class SensAppProviderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SensAppProviderException(String message) {
		super(message);
	}
	
	public SensAppProviderException(String message, Exception e) {
		super(message, e);
	}
}
