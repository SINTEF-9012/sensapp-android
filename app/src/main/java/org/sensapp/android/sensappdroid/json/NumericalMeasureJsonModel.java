package org.sensapp.android.sensappdroid.json;

import java.util.List;

import org.sensapp.android.sensappdroid.models.NumericalMeasure;


public class NumericalMeasureJsonModel extends MeasureJsonModel {
	
	public NumericalMeasureJsonModel() {
		super();
	}
	
	public NumericalMeasureJsonModel(String bn, String bu) {
		super(bn, bu);
	}
	
	public NumericalMeasureJsonModel(String bn, long bt, String bu, List<ValueJsonModel> e) {
		super(bn, bt, bu, e);
	}
	
	public NumericalMeasureJsonModel appendMeasure(int value, long time) {
		getE().add(new NumericalValueJsonModel(value, time));
		return this;
	}
	
	public NumericalMeasureJsonModel appendMeasure(NumericalMeasure measure) {
		if (!measure.getSensor().equals(getBn())) {
			throw new IllegalArgumentException("Incompatible sensor name: " + measure.getSensor());
		} 
		getE().add(new NumericalValueJsonModel(measure.getValue(), measure.getTime()));
		return this;
	}

	@Override
	public String toString() {
		return "MEASURE MODEL/ bn: " + getBn() + " - bt: " + getBt() + " - bu: " + getBu();
	}
}
