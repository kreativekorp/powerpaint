package com.kreative.paint.material.shape;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PowerShape implements Cloneable, Shape {
	private final SortedMap<String,Parameter> parameters;
	private final SortedMap<String,Point2D> parameterValues;
	private final Bindings parameterBindings;
	private final List<ParameterizedShape> shapes;
	private transient GeneralPath awtShape;
	public final WindingRule windingRule;
	public final String name;
	
	public PowerShape(WindingRule windingRule, String name) {
		this.parameters = new TreeMap<String,Parameter>();
		this.parameterValues = new TreeMap<String,Point2D>();
		this.parameterBindings = new ParameterBindings(parameters, parameterValues);
		this.shapes = new ArrayList<ParameterizedShape>();
		this.awtShape = null;
		this.windingRule = windingRule;
		this.name = name;
	}
	
	@Override
	public PowerShape clone() {
		PowerShape clone = new PowerShape(this.windingRule, this.name);
		clone.parameters.putAll(this.parameters);
		for (Map.Entry<String,Point2D> e : this.parameterValues.entrySet()) {
			Point2D p = e.getValue();
			p = new Point2D.Double(p.getX(), p.getY());
			clone.parameterValues.put(e.getKey(), p);
		}
		clone.shapes.addAll(this.shapes);
		return clone;
	}
	
	public void addParameter(Parameter p) {
		parameters.put(p.name, p);
		awtShape = null;
	}
	
	public List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.addAll(parameters.keySet());
		return Collections.unmodifiableList(names);
	}
	
	public Parameter getParameter(String name) {
		return parameters.get(name);
	}
	
	public Point2D getParameterValue(String name) {
		Parameter p = parameters.get(name);
		if (p == null) return null;
		return p.getLocation(parameterValues);
	}
	
	public void setParameterValue(String name, double x, double y) {
		Parameter p = parameters.get(name);
		if (p == null) return;
		p.setLocation(parameterValues, x, y);
		awtShape = null;
	}
	
	public void addShape(ParameterizedShape shape) {
		shapes.add(shape);
		awtShape = null;
	}
	
	public List<ParameterizedShape> getShapes() {
		return Collections.unmodifiableList(shapes);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof PowerShape) {
			return this.equals((PowerShape)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(PowerShape that, boolean withName) {
		if (!this.parameters.equals(that.parameters)) return false;
		if (!this.parameterValues.equals(that.parameterValues)) return false;
		if (!this.shapes.equals(that.shapes)) return false;
		if (this.windingRule != that.windingRule) return false;
		if (!withName) return true;
		if (this.name == null) return (that.name == null);
		if (that.name == null) return (this.name == null);
		return this.name.equals(that.name);
	}
	
	@Override
	public int hashCode() {
		return parameters.hashCode() ^ parameterValues.hashCode() ^ shapes.hashCode();
	}
	
	private void makeAWTShape() {
		awtShape = new GeneralPath();
		if (windingRule != null) {
			awtShape.setWindingRule(windingRule.awtValue);
		}
		for (ParameterizedShape shape : shapes) {
			awtShape.append(shape.awtShape(parameterBindings), false);
		}
	}
	@Override
	public boolean contains(Point2D p) {
		if (awtShape == null) makeAWTShape();
		return awtShape.contains(p);
	}
	@Override
	public boolean contains(Rectangle2D r) {
		if (awtShape == null) makeAWTShape();
		return awtShape.contains(r);
	}
	@Override
	public boolean contains(double x, double y) {
		if (awtShape == null) makeAWTShape();
		return awtShape.contains(x, y);
	}
	@Override
	public boolean contains(double x, double y, double w, double h) {
		if (awtShape == null) makeAWTShape();
		return awtShape.contains(x, y, w, h);
	}
	@Override
	public Rectangle getBounds() {
		if (awtShape == null) makeAWTShape();
		return awtShape.getBounds();
	}
	@Override
	public Rectangle2D getBounds2D() {
		if (awtShape == null) makeAWTShape();
		return awtShape.getBounds2D();
	}
	@Override
	public PathIterator getPathIterator(AffineTransform t) {
		if (awtShape == null) makeAWTShape();
		return awtShape.getPathIterator(t);
	}
	@Override
	public PathIterator getPathIterator(AffineTransform t, double s) {
		if (awtShape == null) makeAWTShape();
		return awtShape.getPathIterator(t, s);
	}
	@Override
	public boolean intersects(Rectangle2D r) {
		if (awtShape == null) makeAWTShape();
		return awtShape.intersects(r);
	}
	@Override
	public boolean intersects(double x, double y, double w, double h) {
		if (awtShape == null) makeAWTShape();
		return awtShape.intersects(x, y, w, h);
	}
}
