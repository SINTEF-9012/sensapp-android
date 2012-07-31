package org.sensapp.android.sensappdroid.restservice;

public class RequestErrorException extends Exception {
	
	public static final int CODE_CONFLICT = 409;
	
	private static final long serialVersionUID = 1L;

	private int code;
	
	public RequestErrorException(String msg) {
		super(msg);
	}
	
	public RequestErrorException(String msg, int code) {
		super(msg);
		this.code = code;
	}
	
	public RequestErrorException(String msg, Exception e) {
		super(msg, e);
	}
	
	public RequestErrorException(String msg, Exception e, int code) {
		super(msg, e);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
