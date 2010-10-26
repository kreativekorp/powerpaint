/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.geom;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Map;

public class ParameterPoint implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private double originX;
	private double originY;
	private boolean polar;
	private double minXR;
	private double minYT;
	private double defXR;
	private double defYT;
	private double maxXR;
	private double maxYT;
	
	public ParameterPoint(String name, double originX, double originY, boolean polar, double minXR, double minYT, double defXR, double defYT, double maxXR, double maxYT) {
		this.name = name;
		this.originX = originX;
		this.originY = originY;
		this.polar = polar;
		this.minXR = minXR;
		this.minYT = minYT;
		this.defXR = defXR;
		this.defYT = defYT;
		this.maxXR = maxXR;
		this.maxYT = maxYT;
	}
	
	public ParameterPoint clone() {
		return new ParameterPoint(
			name,
			originX,
			originY,
			polar,
			minXR,
			minYT,
			defXR,
			defYT,
			maxXR,
			maxYT
		);
	}
	
	public String getName() {
		return name;
	}
	
	public double getOriginX() {
		return originX;
	}
	
	public double getOriginY() {
		return originY;
	}
	
	public boolean isPolar() {
		return polar;
	}
	
	public double getMinimumXorRadius() {
		return minXR;
	}
	
	public double getMinimumYorTheta() {
		return minYT;
	}
	
	public double getDefaultXorRadius() {
		return defXR;
	}
	
	public double getDefaultYorTheta() {
		return defYT;
	}
	
	public double getMaximumXorRadius() {
		return maxXR;
	}
	
	public double getMaximumYorTheta() {
		return maxYT;
	}
	
	public Point2D getDefaultLocation() {
		if (polar) {
			return new Point2D.Double(originX + defXR * Math.cos(defYT), originY - defXR * Math.sin(defYT));
		} else {
			return new Point2D.Double(defXR, defYT);
		}
	}
	
	public boolean isValidPoint(double x, double y) {
		if (polar) {
			double r = Math.hypot(originY-y, x-originX);
			double t = Math.atan2(originY-y, x-originX);
			return (r >= minXR && r <= maxXR && t >= minYT && t <= maxYT);
		} else {
			return (x >= minXR && x <= maxXR && y >= minYT && y <= maxYT);
		}
	}
	
	public Point2D toValidPoint(double x, double y, Point2D dest) {
		if (polar) {
			double r = Math.hypot(originY-y, x-originX);
			double t = Math.atan2(originY-y, x-originX);
			if (r < minXR || r > maxXR || t < minYT || t > maxYT) {
				if (r < minXR) r = minXR;
				if (r > maxXR) r = maxXR;
				if (t < minYT) t = minYT;
				if (t > maxYT) t = maxYT;
				x = originX + r * Math.cos(t);
				y = originY - r * Math.sin(t);
			}
		} else {
			if (x < minXR) x = minXR;
			if (x > maxXR) x = maxXR;
			if (y < minYT) y = minYT;
			if (y > maxYT) y = maxYT;
		}
		if (dest == null) {
			return new Point2D.Double(x, y);
		} else {
			dest.setLocation(x, y);
			return dest;
		}
	}
	
	public double getX(Map<String,Point2D> actualParameters) {
		if (actualParameters.containsKey(name)) {
			return actualParameters.get(name).getX();
		} else if (polar) {
			return originX + defXR * Math.cos(defYT);
		} else {
			return defXR;
		}
	}
	
	public double getY(Map<String,Point2D> actualParameters) {
		if (actualParameters.containsKey(name)) {
			return actualParameters.get(name).getY();
		} else if (polar) {
			return originY - defXR * Math.sin(defYT);
		} else {
			return defYT;
		}
	}
	
	public double getDX(Map<String,Point2D> actualParameters) {
		if (actualParameters.containsKey(name)) {
			return actualParameters.get(name).getX() - originX;
		} else if (polar) {
			return defXR * Math.cos(defYT);
		} else {
			return defXR - originX;
		}
	}
	
	public double getDY(Map<String,Point2D> actualParameters) {
		if (actualParameters.containsKey(name)) {
			return actualParameters.get(name).getY() - originY;
		} else if (polar) {
			return - defXR * Math.sin(defYT);
		} else {
			return defYT - originY;
		}
	}
	
	public double getRadius(Map<String,Point2D> actualParameters) {
		if (actualParameters.containsKey(name)) {
			return Math.hypot(originY - actualParameters.get(name).getY(), actualParameters.get(name).getX() - originX);
		} else if (polar) {
			return defXR;
		} else {
			return Math.hypot(originY - defYT, defXR - originX);
		}
	}
	
	public double getTheta(Map<String,Point2D> actualParameters) {
		if (actualParameters.containsKey(name)) {
			return Math.atan2(originY - actualParameters.get(name).getY(), actualParameters.get(name).getX() - originX);
		} else if (polar) {
			return defYT;
		} else {
			return Math.atan2(originY - defYT, defXR - originX);
		}
	}
	
	public double getValue(Map<String,Point2D> actualParameters, String which) {
		if (which == null) return Double.NaN;
		else if (which.equalsIgnoreCase("x")) return getX(actualParameters);
		else if (which.equalsIgnoreCase("y")) return getY(actualParameters);
		else if (which.equalsIgnoreCase("dx")) return getDX(actualParameters);
		else if (which.equalsIgnoreCase("dy")) return getDY(actualParameters);
		else if (which.equalsIgnoreCase("r")) return getRadius(actualParameters);
		else if (which.equalsIgnoreCase("radius")) return getRadius(actualParameters);
		else if (which.equalsIgnoreCase("t")) return getTheta(actualParameters);
		else if (which.equalsIgnoreCase("th")) return getTheta(actualParameters);
		else if (which.equalsIgnoreCase("theta")) return getTheta(actualParameters);
		else if (which.equalsIgnoreCase("a")) return getTheta(actualParameters);
		else if (which.equalsIgnoreCase("angle")) return getTheta(actualParameters);
		return Double.NaN;
	}
	
	public Point2D getLocation(Map<String,Point2D> actualParameters) {
		if (actualParameters.containsKey(name)) {
			return actualParameters.get(name);
		} else if (polar) {
			return new Point2D.Double(originX + defXR * Math.cos(defYT), originY - defXR * Math.sin(defYT));
		} else {
			return new Point2D.Double(defXR, defYT);
		}
	}
	
	public void setLocation(Map<String,Point2D> actualParameters, double x, double y) {
		actualParameters.put(name, toValidPoint(x, y, null));
	}
}
