package com.kreative.paint.powershape;

import java.awt.geom.Point2D;

public class ParameterizedPoint {
	public final ParameterizedValue x;
	public final ParameterizedValue y;
	
	public ParameterizedPoint(ParameterizedValue x, ParameterizedValue y) {
		this.x = x;
		this.y = y;
	}
	
	public Point2D awtPoint(Bindings bindings) {
		return new Point2D.Double(x.value(bindings), y.value(bindings));
	}
	
	@Override
	public boolean equals(Object that) {
		return (that instanceof ParameterizedPoint)
		    && (this.x.equals(((ParameterizedPoint)that).x))
		    && (this.y.equals(((ParameterizedPoint)that).y));
	}
	
	@Override
	public int hashCode() {
		return x.hashCode() ^ y.hashCode();
	}
}
