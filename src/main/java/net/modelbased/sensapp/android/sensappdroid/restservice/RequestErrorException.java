package net.modelbased.sensapp.android.sensappdroid.restservice;

public class RequestErrorException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public RequestErrorException(String msg) {
		super(msg);
	}
	
	public RequestErrorException(String msg, Exception e) {
		super(msg, e);
	}

}
