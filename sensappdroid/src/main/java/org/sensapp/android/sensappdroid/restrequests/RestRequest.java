package org.sensapp.android.sensappdroid.restrequests;

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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.sensapp.android.sensappdroid.json.JsonPrinter;
import org.sensapp.android.sensappdroid.models.Composite;
import org.sensapp.android.sensappdroid.models.Sensor;

import android.net.Uri;
import android.util.Log;

public class RestRequest {
	
	public static final String SENSOR_PATH = "/sensapp/registry/sensors";
	public static final String COMPOSITE_PATH = "/sensapp/registry/composite/sensors";
	
	private static final String TAG = RestRequest.class.getSimpleName(); 
	private static final String DISPATCHER_PATH = "/sensapp/dispatch";
	
	public static boolean isSensorRegistred(Sensor sensor) throws RequestErrorException {
		URI target;
		try {
			target = new URI(sensor.getUri().toString() + SENSOR_PATH + "/" + sensor.getName());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new RequestErrorException(e1.getMessage());
		}
		Log.v(TAG, "Target: " + target.toString());
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(target);
		StatusLine status;
		try {
			status = client.execute(request).getStatusLine();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		} catch (IOException e) {
			throw new RequestErrorException(e.getMessage());
		}
		if (status.getStatusCode() == 200) {
			Log.i(TAG, "Sensor " + sensor.getName() + " already registered");
			return true;
		}
		Log.w(TAG, "Sensor " + sensor.getName() + " not yet registered");
		return false;
	}
	
	public static String postSensor(Sensor sensor) throws RequestErrorException {
		String content = JsonPrinter.sensorToJson(sensor);
		Log.i(TAG, "POST Sensor");
		Log.v(TAG, "Content: " + content);
		URI target;
		try {
			target = new URI(sensor.getUri().toString() + SENSOR_PATH);
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
	
	public static String deleteSensor(Uri uri, Sensor sensor) throws RequestErrorException {
		Log.i(TAG, "DELETE Sensor " + sensor.getName());
		URI target;
		try {
			target = new URI(uri.toString() + SENSOR_PATH + "/" + sensor.getName());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		}
		Log.v(TAG, "Target: " + target.toString());
		HttpClient client = new DefaultHttpClient();
		HttpDelete request = new HttpDelete(target);
		request.setHeader("Content-type", "application/json");
		String response = null;
		try {
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
	
	public static boolean isCompositeRegistred(Composite composite) throws RequestErrorException {
		URI target;
		try {
			target = new URI(composite.getUri().toString() + COMPOSITE_PATH + "/" + composite.getName());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new RequestErrorException(e1.getMessage());
		}
		Log.v(TAG, "Target: " + target.toString());
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(target);
		StatusLine status;
		try {
			status = client.execute(request).getStatusLine();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		} catch (IOException e) {
			throw new RequestErrorException(e.getMessage());
		}
		if (status.getStatusCode() == 200) {
			Log.i(TAG, "Sensor " + composite.getName() + " already registered");
			return true;
		}
		Log.w(TAG, "Sensor " + composite.getName() + " not yet registered");
		return false;
	}
	
	public static String postComposite(Composite composite) throws RequestErrorException {
		String content = JsonPrinter.compositeToJson(composite);
		Log.i(TAG, "POST Composite");
		Log.v(TAG, "Content: " + content);
		URI target;
		try {
			target = new URI(composite.getUri().toString() + COMPOSITE_PATH);
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
