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

public class Spiral extends CircularShape {
	private int sides;
	private double spacing;
	private boolean spokes;
	private Point2D.Float center;
	private Point2D.Float endPoint;
	private GeneralPath path;
	
	public Spiral(int sides, double spacing, boolean spokes, float cx, float cy, float ex, float ey) {
		this.sides = sides;
		this.spacing = spacing;
		this.spokes = spokes;
		this.center = new Point2D.Float(cx, cy);
		this.endPoint = new Point2D.Float(ex, ey);
		this.path = makePath();
	}
	
	public Spiral clone() {
		return new Spiral(sides, spacing, spokes, (float)center.getX(), (float)center.getY(), (float)endPoint.getX(), (float)endPoint.getY());
	}
	
	public int getSides() { return sides; }
	public double getSpacing() { return spacing; }
	public boolean getSpokes() { return spokes; }
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
		
		p.moveTo(endPoint.x, endPoint.y);
		double r = Math.hypot(endPoint.y-center.y, endPoint.x-center.x);
		double t = Math.atan2(endPoint.y-center.y, endPoint.x-center.x);
		for (int i = 0; ; i++) {
			double a = t + (double)i * Math.PI * 2.0 / (double)sides;
			double b = r - spacing * (double)i / (double)sides;
			if (b < 0) break;
			double x = center.x + b * Math.cos(a);
			double y = center.y + b * Math.sin(a);
			p.lineTo((float)x, (float)y);
		}
		if (spokes) {
			for (int i = 0; i < sides; i++) {
				double a = t + (double)i * Math.PI * 2.0 / (double)sides;
				double b = r - spacing * (double)i / (double)sides;
				if (b < 0) break;
				double x = center.x + b * Math.cos(a);
				double y = center.y + b * Math.sin(a);
				p.moveTo(center.x, center.y);
				p.lineTo((float)x, (float)y);
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
		return "com.kreative.paint.geom.Spiral["+sides+","+spacing+","+spokes+","+center+","+endPoint+"]";
	}
}
