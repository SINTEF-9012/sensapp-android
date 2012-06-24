package net.modelbased.sensapp.android.sensappdroid;

public class RESTExchangeThread extends Thread {
	
	@Override
	public void run() {
		new RestAPI("46.51.169.123", 80);
	}
	
}
