package net.modelbased.sensapp.android.sensappdroid.jsondatamodel;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonParser {

	private static ObjectMapper mapper = new ObjectMapper();
	
	public static String measureToJson(MeasureJsonModel measure) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(measure);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static String sensorToJson(SensorJsonModel sensor) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(sensor);
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
