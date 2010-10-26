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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Cycloid extends CircularShape {
	public static final int DEFAULT_SMOOTHNESS = 6;
	public static final int DEFAULT_BEGIN = 0;
	public static final int DEFAULT_END = 0;
	public static final double DEFAULT_R_FOR_HYPOCYCLOID = 67.0/22.0 + 2.0;
	public static final double DEFAULT_R_FOR_EPICYCLOID = 67.0/22.0 - 2.0;
	public static final double DEFAULT_r = 1.0;
	public static final double DEFAULT_d = -3.0;
	public static final int DEFAULT_CURVE_FACTOR = 6;
	public static final int DEFAULT_FIXED_RING = 67;
	public static final int DEFAULT_ROLLING_WHEEL = 22;
	public static final int DEFAULT_PEN_POSITION = 3;
	
	// true if this is an epitrochoid, false if this is a hypotrochoid
	private boolean epi;
	// smoothness is the resolution of theta (or number of steps) over 2*pi
	private int smoothness;
	// theta starts at begin * 2pi
	private int begin;
	// theta ends at end * 2pi; if end == begin then theta ends only when we reach a point we've reached before
	private int end;
	// the radius of the outer circle
	private double R;
	// the radius of the inner circle
	private double r;
	// the position of the pen from the center of the inner circle
	private double d;
	// the center of the outer circle
	private Point2D.Float center;
	// a point at a distance from the outer circle's center that determines the maximum size of the cycloid
	private Point2D.Float endPoint;
	// the path
	private GeneralPath path;
	
	public Cycloid(boolean epi, int smoothness, int begin, int end, double R, double r, double d, float cx, float cy, float ex, float ey) {
		this.epi = epi;
		this.smoothness = smoothness;
		this.begin = begin;
		this.end = end;
		this.R = R;
		this.r = r;
		this.d = d;
		this.center = new Point2D.Float(cx, cy);
		this.endPoint = new Point2D.Float(ex, ey);
		this.path = makePath();
	}
	
	public Cycloid(boolean epi, int curveFactor, int fixedRing, int rollingWheel, int penPosition, float cx, float cy, float ex, float ey) {
		this.epi = epi;
		this.smoothness = curveFactor;
		this.begin = 0;
		this.end = 0;
		this.R = (double)rollingWheel / (double)fixedRing + (epi ? -2.0 : 2.0);
		this.r = 1.0;
		this.d = -penPosition;
		this.center = new Point2D.Float(cx, cy);
		this.endPoint = new Point2D.Float(ex, ey);
		this.path = makePath();
	}
	
	public Cycloid clone() {
		return new Cycloid(epi, smoothness, begin, end, R, r, d, (float)center.getX(), (float)center.getY(), (float)endPoint.getX(), (float)endPoint.getY());
	}
	
	public boolean isEpicycloid() { return epi; }
	public int getSmoothness() { return smoothness; }
	public int getBegin() { return begin; }
	public int getEnd() { return end; }
	public double getR() { return R; }
	public double getr() { return r; }
	public double getd() { return d; }
	public Point2D.Float getCenter() { return center; }
	public Point2D.Float getEndpoint() { return endPoint; }
	public double getCenterX() { return center.x; }
	public double getCenterY() { return center.y; }
	public double getEndpointX() { return endPoint.x; }
	public double getEndpointY() { return endPoint.y; }
	
	public void setCircle(double cx, double cy, double ex, double ey) {
		this.center = new Point2D.Float((float)cx, (float)cy);
		this.endPoint = new Point2D.Float((float)ex, (float)ey);
		this.path = makePath();
	}
	
	private GeneralPath makePath() {
		GeneralPath p = new GeneralPath();
		
		double rf = Math.hypot(endPoint.x-center.x, endPoint.y-center.y) /
			(epi ? (Math.abs(R+r)+Math.abs(d)) : (Math.abs(R-r)+Math.abs(d)));
		double th0 = begin*2.0*Math.PI;
		double x0 = epi
				? ((R+r)*Math.cos(th0) - d*Math.cos(((R+r)*th0)/r))
				: ((R-r)*Math.cos(th0) + d*Math.cos(((R-r)*th0)/r));
		double y0 = epi
				? ((R+r)*Math.sin(th0) - d*Math.sin(((R+r)*th0)/r))
				: ((R-r)*Math.sin(th0) - d*Math.sin(((R-r)*th0)/r));
		double xx0 = center.x + x0*rf;
		double yy0 = center.y - y0*rf;
		p.moveTo((float)xx0, (float)yy0);
		boolean repeated = false;
		for (int i = begin; (begin == end) ? (!repeated) : (i <= end); i++) {
			for (int j = 1; (begin == end) ? (j <= smoothness && !repeated) : (j <= smoothness); j++) {
				double th = ( (double)i + (double)j/(double)smoothness )*2.0*Math.PI;
				double x = epi
						? ((R+r)*Math.cos(th) - d*Math.cos(((R+r)*th)/r))
						: ((R-r)*Math.cos(th) + d*Math.cos(((R-r)*th)/r));
				double y = epi
						? ((R+r)*Math.sin(th) - d*Math.sin(((R+r)*th)/r))
						: ((R-r)*Math.sin(th) - d*Math.sin(((R-r)*th)/r));
				double xx = center.x + x*rf;
				double yy = center.y - y*rf;
				p.lineTo((float)xx, (float)yy);
				if ((Math.abs(x-x0) < 1e-10) && (Math.abs(y-y0) < 1e-10)) repeated = true;
			}
		}
		
		return p;
	}

	public boolean contains(Point2D p) {
		return path.contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return path.contains(r);
	}

	public boolean contains(double x, double y) {
		return path.contains(x,y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return path.contains(x,y,w,h);
	}

	public Rectangle getBounds() {
		return path.getBounds();
	}

	public Rectangle2D getBounds2D() {
		return path.getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return path.getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return path.getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return path.intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return path.intersects(x,y,w,h);
	}
	
	public String toString() {
		return "com.kreative.paint.geom.Cycloid["+smoothness+","+begin+","+end+","+epi+","+R+","+r+","+d+","+center+","+endPoint+"]";
	}
}
