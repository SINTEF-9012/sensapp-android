package org.sensapp.android.sensappdroid.restrequests;

import org.sensapp.android.sensappdroid.activities.SensAppService;

import android.content.Context;
import android.content.Intent;

public class RequestTask {
	
	public static final String ACTION_REQUEST_SUCCEED = "net.modelbased.sensapp.android.sensappdroid.requesttask.ACTION_REQUEST_SUCCEED";
	public static final String ACTION_REQUEST_FAILURE = "net.modelbased.sensapp.android.sensappdroid.requesttask.ACTION_REQUEST_FAILURE";
	public static final String EXTRA_REQUEST_CODE = "net.modelbased.sensapp.android.sensappdroid.requesttask.EXTRA_REQUEST_CODE";
	public static final String EXTRA_RESPONSE = "net.modelbased.sensapp.android.sensappdroid.requesttask.EXTRA_RESPONSE";
	
	public static final int CODE_POST_SENSOR = 10;
	public static final int CODE_PUT_MEASURE = 20;
	
	protected static void uploadFailure(Context context, int requestCode, String response) {
		Intent i = new Intent(context, SensAppService.class);
		i.setAction(ACTION_REQUEST_FAILURE);
		i.putExtra(EXTRA_REQUEST_CODE, requestCode);
		i.putExtra(EXTRA_RESPONSE, response);
		context.startService(i);
	}
	
	protected static void uploadSuccess(Context context, int requestCode, String response) {
		Intent i = new Intent(context, SensAppService.class);
		i.setAction(ACTION_REQUEST_SUCCEED);
		i.putExtra(EXTRA_REQUEST_CODE, requestCode);
		i.putExtra(EXTRA_RESPONSE, response);
		context.startService(i);
	}
}
