/**
 * Copyright (C) 2012 SINTEF <fabien@fleurey.com>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sensapp.android.sensappdroid.restrequests;

import android.net.Uri;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

public final class RestRequest {
	
	public static final String SENSOR_PATH = "/sensapp/registry/sensors";
	public static final String COMPOSITE_PATH = "/sensapp/registry/composite/sensors";
	
	private static final String TAG = RestRequest.class.getSimpleName(); 
	private static final String DISPATCHER_PATH = "/sensapp/dispatch";
	
	private RestRequest() {}
	
	public static boolean isSensorRegistred(Sensor sensor) throws RequestErrorException {
		URI target;
		try {
			target = new URI(sensor.getUri().toString() + SENSOR_PATH + "/" + sensor.getName());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		}
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(target);
		StatusLine status;
		try {
			status = client.execute(request).getStatusLine();
		} catch (Exception e) {
			throw new RequestErrorException(e.getMessage());
		}
		if (status.getStatusCode() == 200) {
			return true;
		}
		return false;
	}
	
	public static String postSensor(Sensor sensor) throws RequestErrorException {
		String content = JsonPrinter.sensorToJson(sensor);
		URI target;
		try {
			target = new URI(sensor.getUri().toString() + SENSOR_PATH);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new RequestErrorException(e1.getMessage());
		}
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(target);
		request.setHeader("Content-type", "application/json");
		String response = null;
		try {
			StringEntity seContent = new StringEntity(content);
			seContent.setContentType("text/json");  
			request.setEntity(seContent);  
			response = resolveResponse(client.execute(request));
		} catch (Exception e) {
			throw new RequestErrorException(e.getMessage());
		}
		return response; 
	}
	
	public static String deleteSensor(Uri uri, Sensor sensor) throws RequestErrorException {
		URI target;
		try {
			target = new URI(uri.toString() + SENSOR_PATH + "/" + sensor.getName());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RequestErrorException(e.getMessage());
		}
		HttpClient client = new DefaultHttpClient();
		HttpDelete request = new HttpDelete(target);
		request.setHeader("Content-type", "application/json");
		String response = null;
		try {
			response = resolveResponse(client.execute(request));
		} catch (Exception e) {
			throw new RequestErrorException(e.getMessage());
		}
		return response; 
	}
	
	public static String putData(Uri uri, String data) throws RequestErrorException {
		URI target;
		try {
			target = new URI(uri.toString() + DISPATCHER_PATH);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new RequestErrorException(e1.getMessage());
		}
		HttpClient client = new DefaultHttpClient();
		HttpPut request = new HttpPut(target);
		request.setHeader("Content-type", "application/json");
		String response = null;
		try {
			StringEntity seContent = new StringEntity(data);
			seContent.setContentType("text/json");  
			request.setEntity(seContent);  
			response = resolveResponse(client.execute(request));
		} catch (Exception e) {
			throw new RequestErrorException(e.getMessage());
		}
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
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(target);
		StatusLine status;
		try {
			status = client.execute(request).getStatusLine();
		} catch (Exception e) {
			throw new RequestErrorException(e.getMessage());
		}
		if (status.getStatusCode() == 200) {
			return true;
		}
		return false;
	}
	
	public static String postComposite(Composite composite) throws RequestErrorException {
		String content = JsonPrinter.compositeToJson(composite);
		URI target;
		try {
			target = new URI(composite.getUri().toString() + COMPOSITE_PATH);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new RequestErrorException(e1.getMessage());
		}
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(target);
		request.setHeader("Content-type", "application/json");
		String response = null;
		try {
			StringEntity seContent = new StringEntity(content);
			seContent.setContentType("text/json");  
			request.setEntity(seContent);  
			response = resolveResponse(client.execute(request));
		} catch (Exception e) {
			throw new RequestErrorException(e.getMessage());
		} 
		return response; 
	}
	
	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
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
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
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
