package org.sensapp.android.sensappdroid.json;

import java.io.IOException;
import java.util.ArrayList;



import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.sensapp.android.sensappdroid.models.Composite;
import org.sensapp.android.sensappdroid.models.Sensor;

import android.net.Uri;

public class JsonPrinter {

	private static ObjectMapper mapper = new ObjectMapper();
	
	public static String measuresToJson(MeasureJsonModel jsonModel) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(jsonModel);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static String stringMeasuresToJson(StringMeasureJsonModel jsonModel) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(jsonModel);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static String sensorToJson(Sensor sensor) {
		String jsonString = null;
		SensorJsonModel.Schema schema = new SensorJsonModel.Schema(sensor.getBackend(), sensor.getTemplate());
		SensorJsonModel jsonSensor = new SensorJsonModel(sensor.getName(), sensor.getDescription(), schema);
		try {
			jsonString = mapper.writeValueAsString(jsonSensor);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static String compositeToJson(Composite composite) {
		String jsonString = null;
		ArrayList<String> sensors = new ArrayList<String>();
		for (Uri uri : composite.getSensors()) {
			sensors.add(uri.toString());
		}
		ComposititeJsonModel jsonComposite = new ComposititeJsonModel(composite.getName(), composite.getDescription(), sensors);
		try {
			jsonString = mapper.writeValueAsString(jsonComposite);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
}
