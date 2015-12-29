package com.kreative.paint.document.draw;

import java.awt.geom.Point2D;

public class ControlPoint extends Point2D implements Cloneable {
	private ControlPointType type;
	private double x;
	private double y;
	
	public ControlPoint(ControlPointType type, double x, double y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public ControlPoint clone() {
		return new ControlPoint(type, x, y);
	}
	
	public ControlPointType getType() {
		return type;
	}
	
	public void setType(ControlPointType type) {
		this.type = type;
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override
	public double getY() {
		return y;
	}
	
	@Override
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof ControlPoint) {
			return this.type == ((ControlPoint)that).type
			    && this.x == ((ControlPoint)that).x
			    && this.y == ((ControlPoint)that).y;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int t = this.type.hashCode();
		int x = java.lang.Float.floatToRawIntBits((float)this.x);
		int y = java.lang.Float.floatToRawIntBits((float)this.y);
		return t ^ x ^ y;
	}
}
