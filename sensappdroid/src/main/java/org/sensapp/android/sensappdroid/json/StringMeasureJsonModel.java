package org.sensapp.android.sensappdroid.json;

import java.util.List;

import org.sensapp.android.sensappdroid.models.StringMeasure;


public class StringMeasureJsonModel extends MeasureJsonModel {
	
	public StringMeasureJsonModel() {
		super();
	}
	
	public StringMeasureJsonModel(String bn, String bu) {
		super(bn, bu);
	}
	
	public StringMeasureJsonModel(String bn, long bt, String bu, List<ValueJsonModel> e) {
		super(bn, bt, bu, e);
	}
	
	public StringMeasureJsonModel appendMeasure(String value, long time) {
		getE().add(new StringValueJsonModel(value, time));
		return this;
	}
	
	public StringMeasureJsonModel appendMeasure(StringMeasure measure) {
		if (!measure.getSensor().equals(getBn())) {
			throw new IllegalArgumentException("Incompatible sensor name: " + measure.getSensor());
		} 
		getE().add(new StringValueJsonModel(measure.getValue(), measure.getTime()));
		return this;
	}

	@Override
	public String toString() {
		return "MEASURE MODEL/ bn: " + getBn() + " - bt: " + getBt() + " - bu: " + getBu();
	}
}
