package com.kreative.paint.powershape;

import java.awt.geom.Point2D;
import java.util.Map;

public class ParameterBindings implements Bindings {
	private final Map<String,Parameter> parameters;
	private final Map<String,Point2D> currentValues;
	
	public ParameterBindings(
		Map<String,Parameter> parameters,
		Map<String,Point2D> currentValues
	) {
		this.parameters = parameters;
		this.currentValues = currentValues;
	}
	
	@Override
	public double get(String key) {
		if (key == null) return Double.NaN;
		int i = key.indexOf('.');
		if (i < 0) return Double.NaN;
		String paramName = key.substring(0, i);
		Parameter param = parameters.get(paramName);
		if (param == null) return Double.NaN;
		String valueName = key.substring(i + 1);
		return param.getValue(currentValues, valueName);
	}
	
	@Override
	public void set(String key, double value) {
		if (key == null) return;
		int i = key.indexOf('.');
		if (i < 0) return;
		String paramName = key.substring(0, i);
		Parameter param = parameters.get(paramName);
		if (param == null) return;
		Point2D p = param.getLocation(currentValues);
		String valueName = key.substring(i + 1);
		if (valueName.equalsIgnoreCase("x")) {
			param.setLocation(currentValues, value, p.getY());
		} else if (valueName.equalsIgnoreCase("y")) {
			param.setLocation(currentValues, p.getX(), value);
		}
	}
	
	@Override
	public void remove(String key) {
		if (key == null) return;
		int i = key.indexOf('.');
		if (i < 0) return;
		String paramName = key.substring(0, i);
		Parameter param = parameters.get(paramName);
		if (param == null) return;
		Point2D d = param.getDefaultLocation();
		Point2D p = param.getLocation(currentValues);
		String valueName = key.substring(i + 1);
		if (valueName.equalsIgnoreCase("x")) {
			param.setLocation(currentValues, d.getX(), p.getY());
		} else if (valueName.equalsIgnoreCase("y")) {
			param.setLocation(currentValues, p.getX(), d.getY());
		}
	}
}
