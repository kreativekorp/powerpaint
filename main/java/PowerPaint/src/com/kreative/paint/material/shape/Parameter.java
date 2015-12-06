package com.kreative.paint.material.shape;

import java.awt.geom.Point2D;
import java.util.Map;

public class Parameter {
	public final String name;
	public final double originX, originY;
	public final boolean polar;
	public final double minX, minY, minR, minA;
	public final double defX, defY, defR, defA;
	public final double maxX, maxY, maxR, maxA;
	
	public Parameter(
		String name, double originX, double originY, boolean polar,
		double minX, double minY, double minR, double minA,
		double defX, double defY, double defR, double defA,
		double maxX, double maxY, double maxR, double maxA
	) {
		this.name = name; this.originX = originX; this.originY = originY; this.polar = polar;
		this.minX = minX; this.minY = minY; this.minR = minR; this.minA = minA;
		this.defX = defX; this.defY = defY; this.defR = defR; this.defA = defA;
		this.maxX = maxX; this.maxY = maxY; this.maxR = maxR; this.maxA = maxA;
	}
	
	public Point2D getDefaultLocation() {
		if (polar) {
			double x = originX + defR * Math.cos(defA);
			double y = originY - defR * Math.sin(defA);
			return new Point2D.Double(x, y);
		} else {
			return new Point2D.Double(defX, defY);
		}
	}
	
	public boolean isValidPoint(double x, double y) {
		if (polar) {
			double r = Math.hypot(originY - y, x - originX);
			double a = Math.atan2(originY - y, x - originX);
			return (r >= minR && r <= maxR && a >= minA && a <= maxA);
		} else {
			return (x >= minX && x <= maxX && y >= minY && y <= maxY);
		}
	}
	
	public Point2D toValidPoint(double x, double y, Point2D p) {
		if (polar) {
			double r = Math.hypot(originY - y, x - originX);
			double a = Math.atan2(originY - y, x - originX);
			if (r < minR || r > maxR || a < minA || a > maxA) {
				if (r < minR) r = minR;
				if (r > maxR) r = maxR;
				if (a < minA) a = minA;
				if (a > maxA) a = maxA;
				x = originX + r * Math.cos(a);
				y = originY - r * Math.sin(a);
			}
		} else {
			if (x < minX) x = minX;
			if (x > maxX) x = maxX;
			if (y < minY) y = minY;
			if (y > maxY) y = maxY;
		}
		if (p == null) {
			return new Point2D.Double(x, y);
		} else {
			p.setLocation(x, y);
			return p;
		}
	}
	
	public double getX(Map<String,Point2D> currentValues) {
		if (currentValues.containsKey(name)) {
			return currentValues.get(name).getX();
		} else if (polar) {
			return originX + defR * Math.cos(defA);
		} else {
			return defX;
		}
	}
	
	public double getY(Map<String,Point2D> currentValues) {
		if (currentValues.containsKey(name)) {
			return currentValues.get(name).getY();
		} else if (polar) {
			return originY - defR * Math.sin(defA);
		} else {
			return defY;
		}
	}
	
	public double getDX(Map<String,Point2D> currentValues) {
		if (currentValues.containsKey(name)) {
			return currentValues.get(name).getX() - originX;
		} else if (polar) {
			return defR * Math.cos(defA);
		} else {
			return defX - originX;
		}
	}
	
	public double getDY(Map<String,Point2D> currentValues) {
		if (currentValues.containsKey(name)) {
			return currentValues.get(name).getY() - originY;
		} else if (polar) {
			return -(defR * Math.sin(defA));
		} else {
			return defY - originY;
		}
	}
	
	public double getRadius(Map<String,Point2D> currentValues) {
		if (currentValues.containsKey(name)) {
			double y = originY - currentValues.get(name).getY();
			double x = currentValues.get(name).getX() - originX;
			return Math.hypot(y, x);
		} else if (polar) {
			return defR;
		} else {
			return Math.hypot(originY - defY, defX - originX);
		}
	}
	
	public double getAngle(Map<String,Point2D> currentValues) {
		if (currentValues.containsKey(name)) {
			double y = originY - currentValues.get(name).getY();
			double x = currentValues.get(name).getX() - originX;
			return Math.atan2(y, x);
		} else if (polar) {
			return defA;
		} else {
			return Math.atan2(originY - defY, defX - originX);
		}
	}
	
	public double getValue(Map<String,Point2D> currentValues, String key) {
		if (key == null) return Double.NaN;
		if (key.equalsIgnoreCase("x")) return getX(currentValues);
		if (key.equalsIgnoreCase("y")) return getY(currentValues);
		if (key.equalsIgnoreCase("dx")) return getDX(currentValues);
		if (key.equalsIgnoreCase("dy")) return getDY(currentValues);
		if (key.equalsIgnoreCase("r")) return getRadius(currentValues);
		if (key.equalsIgnoreCase("radius")) return getRadius(currentValues);
		if (key.equalsIgnoreCase("a")) return getAngle(currentValues);
		if (key.equalsIgnoreCase("angle")) return getAngle(currentValues);
		if (key.equalsIgnoreCase("t")) return getAngle(currentValues);
		if (key.equalsIgnoreCase("th")) return getAngle(currentValues);
		if (key.equalsIgnoreCase("theta")) return getAngle(currentValues);
		return Double.NaN;
	}
	
	public Point2D getLocation(Map<String,Point2D> currentValues) {
		if (currentValues.containsKey(name)) {
			return currentValues.get(name);
		} else {
			return getDefaultLocation();
		}
	}
	
	public void setLocation(Map<String,Point2D> currentValues, double x, double y) {
		currentValues.put(name, toValidPoint(x, y, null));
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Parameter) {
			if (this.originX != ((Parameter)that).originX) return false;
			if (this.originY != ((Parameter)that).originY) return false;
			if (this.polar != ((Parameter)that).polar) return false;
			if (this.polar) {
				if (this.minR != ((Parameter)that).minR) return false;
				if (this.minA != ((Parameter)that).minA) return false;
				if (this.defR != ((Parameter)that).defR) return false;
				if (this.defA != ((Parameter)that).defA) return false;
				if (this.maxR != ((Parameter)that).maxR) return false;
				if (this.maxA != ((Parameter)that).maxA) return false;
			} else {
				if (this.minX != ((Parameter)that).minX) return false;
				if (this.minY != ((Parameter)that).minY) return false;
				if (this.defX != ((Parameter)that).defX) return false;
				if (this.defY != ((Parameter)that).defY) return false;
				if (this.maxX != ((Parameter)that).maxX) return false;
				if (this.maxY != ((Parameter)that).maxY) return false;
			}
			if (this.name == null) return (((Parameter)that).name == null);
			if (((Parameter)that).name == null) return (this.name == null);
			return this.name.equals(((Parameter)that).name);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hashCode = (name != null) ? name.hashCode() : 0;
		hashCode ^= Double.valueOf(originX + originY).hashCode();
		if (polar) hashCode ^= Double.valueOf(minR + minA + defR + defA + maxR + maxA).hashCode();
		else hashCode ^= Double.valueOf(minX + minY + defX + defY + maxX + maxY).hashCode();
		return hashCode;
	};
}
