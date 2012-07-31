package net.modelbased.sensapp.android.sensappdroid.json;

import java.io.IOException;


import net.modelbased.sensapp.android.sensappdroid.models.Sensor;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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
}
