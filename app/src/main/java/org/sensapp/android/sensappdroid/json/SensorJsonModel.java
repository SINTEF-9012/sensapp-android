package org.sensapp.android.sensappdroid.json;

public class SensorJsonModel {
	
	public static class Schema {
		
		private String backend;
		private String template;
		
		public Schema(String backend, String template) {
			this.backend = backend;
			this.template = template;
		}
		
		public String getBackend() {
			return backend;
		}
		public void setBackend(String backend) {
			this.backend = backend;
		}
		public String getTemplate() {
			return template;
		}
		public void setTemplate(String template) {
			this.template = template;
		}
	}
	
	private String id;
	private String descr;
	private Schema schema;
	
	public SensorJsonModel(String id, String descr, Schema schema) {
		this.id = id;
		this.descr = descr;
		this.schema = schema;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public Schema getSchema() {
		return schema;
	}
	public void setSchema(Schema schema) {
		this.schema = schema;
	}
}
