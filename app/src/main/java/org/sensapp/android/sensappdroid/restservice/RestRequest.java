package org.sensapp.android.sensappdroid.restservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.sensapp.android.sensappdroid.json.JsonPrinter;
import org.sensapp.android.sensappdroid.models.Sensor;

import android.net.Uri;
import android.util.Log;

public class RestRequest {
	
	private static final String TAG = RestRequest.class.getSimpleName(); 
	private static final String SENSOR_PATH = "/sensapp/registry/sensors";
	private static final String DISPATCHER_PATH = "/sensapp/dispatch";
	
	public static String postSensor(Uri uri, Sensor sensor) throws RequestErrorException {
		String content = JsonPrinter.sensorToJson(sensor);
		Log.i(TAG, "POST Sensor");
		Log.v(TAG, "Content: " + content);
		URI target;
		try {
			target = new URI(uri.toString() + SENSOR_PATH);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new RequestErrorException(e1.getMessage());
		}
		Log.v(TAG, "Target: " + target.toString());
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(target);
		request.setHeader("Content-type", "application/json");
		String response = null;
		try {
			StringEntity seContent = new StringEntity(content);
			seContent.setContentType("text/json");  
			request.setEntity(seContent);  
			response = resolveResponse(client.execute(request));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		} catch (IOException e) {
			throw new RequestErrorException(e.getMessage());
		}
		return response; 
	}
	
	public static String putData(Uri uri, String data) throws RequestErrorException {
		Log.i(TAG, "PUT Data");
		Log.v(TAG, "Content: " + data);
		URI target;
		try {
			target = new URI(uri.toString() + DISPATCHER_PATH);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new RequestErrorException(e1.getMessage());
		}
		Log.v(TAG, "Target: " + target.toString());
		HttpClient client = new DefaultHttpClient();
		HttpPut request = new HttpPut(target);
		request.setHeader("Content-type", "application/json");
		String response = null;
		try {
			StringEntity seContent = new StringEntity(data);
			seContent.setContentType("text/json");  
			request.setEntity(seContent);  
			response = resolveResponse(client.execute(request));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		}
		Log.i(TAG, "Put data result: " + response);
		if (response.trim().length() > 2) {
			throw new RequestErrorException("Sensor not registred: " + response);
		}
		return response; 
	}
	
	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	private static String resolveResponse(HttpResponse response) throws RequestErrorException {
		StatusLine status = response.getStatusLine();
		HttpEntity entity = response.getEntity();
		String result = null;
		if (entity != null) {
			InputStream inputStream = null;
			try {
				inputStream = entity.getContent();
				result = convertStreamToString(inputStream);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (status.getStatusCode() == 200) {
			return result;
		} else if (status.getStatusCode() == 409) {
			Log.w(TAG, status.toString());
			Log.w(TAG, result);
			return result;
		} else {
			throw new RequestErrorException("Invalid response from server: " + result, new IOException(status.toString()), status.getStatusCode());
		}
	}
}
