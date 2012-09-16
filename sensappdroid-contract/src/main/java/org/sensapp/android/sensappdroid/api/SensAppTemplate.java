package org.sensapp.android.sensappdroid.api;

public enum SensAppTemplate {
	STRING("String"), 
	NUMERICAL("Numerical");
	private String template;
	private SensAppTemplate(String template) {
		this.template = template;
	}
	public String getTemplate() {
		return template;
	}
}
