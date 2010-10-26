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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class RegularPolygon implements Shape, Cloneable {
	private int sides;
	private int skips;
	private Point2D.Double center;
	private Point2D.Double endPoint1;
	private Point2D.Double endPoint2;
	private GeneralPath poly;
	
	public RegularPolygon(double centerX, double centerY, double endPointX, double endPointY, int numSides) {
		sides = numSides;
		skips = 1;
		center = new Point2D.Double(centerX, centerY);
		endPoint1 = new Point2D.Double(endPointX, endPointY);
		endPoint2 = null;
		poly = makePolygon();
	}
	
	public RegularPolygon(double x1, double y1, double x2, double y2, int numSides, boolean fromCenter) {
		sides = numSides;
		skips = 1;
		if (fromCenter) {
			center = new Point2D.Double(x1, y1);
			endPoint1 = new Point2D.Double(x2, y2);
			endPoint2 = null;
		} else {
			center = null;
			endPoint1 = new Point2D.Double(x1, y1);
			endPoint2 = new Point2D.Double(x2, y2);
		}
		poly = makePolygon();
	}
	
	public RegularPolygon(double centerX, double centerY, double endPointX, double endPointY, int numSides, int numSkips) {
		sides = numSides;
		skips = numSkips;
		center = new Point2D.Double(centerX, centerY);
		endPoint1 = new Point2D.Double(endPointX, endPointY);
		endPoint2 = null;
		poly = makePolygon();
	}
	
	public RegularPolygon(double x1, double y1, double x2, double y2, int numSides, int numSkips, boolean fromCenter) {
		sides = numSides;
		skips = numSkips;
		if (fromCenter) {
			center = new Point2D.Double(x1, y1);
			endPoint1 = new Point2D.Double(x2, y2);
			endPoint2 = null;
		} else {
			center = null;
			endPoint1 = new Point2D.Double(x1, y1);
			endPoint2 = new Point2D.Double(x2, y2);
		}
		poly = makePolygon();
	}
	
	public RegularPolygon clone() {
		if (center != null) {
			return new RegularPolygon(center.x, center.y, endPoint1.x, endPoint1.y, sides, skips, true);
		} else {
			return new RegularPolygon(endPoint1.x, endPoint1.y, endPoint2.x, endPoint2.y, sides, skips, false);
		}
	}
	
	public int getSides() {
		return sides;
	}
	
	public void setSides(int numSides) {
		sides = numSides;
		poly = makePolygon();
	}
	
	public int getSkips() {
		return skips;
	}
	
	public void setSkips(int numSkips) {
		skips = numSkips;
		poly = makePolygon();
	}
	
	public Point2D getCenterInternal() {
		return center;
	}
	
	public Point2D getCenter() {
		if (center != null) {
			double cx = center.x;
			double cy = center.y;
			return new Point2D.Double(cx, cy);
		} else {
			double ex = endPoint1.x;
			double ey = endPoint1.y;
			double s = Math.hypot(endPoint2.y - endPoint1.y, endPoint2.x - endPoint1.x);
			double t = Math.atan2(endPoint2.y - endPoint1.y, endPoint2.x - endPoint1.x);
			double cx = ex + s * Math.sin(Math.PI/sides + t) / (2.0 * Math.sin(Math.PI/sides));
			double cy = ey - s * Math.cos(Math.PI/sides + t) / (2.0 * Math.sin(Math.PI/sides));
			return new Point2D.Double(cx, cy);
		}
	}
	
	public void setCenter(double centerX, double centerY) {
		this.center = new Point2D.Double(centerX, centerY);
		this.endPoint2 = null;
		poly = makePolygon();
	}
	
	public void setCenter(Point2D center) {
		this.center = new Point2D.Double(center.getX(), center.getY());
		this.endPoint2 = null;
		poly = makePolygon();
	}
	
	public void moveCenter(double ncx, double ncy) {
		Point2D c = getCenter();
		double dx = endPoint1.x - c.getX();
		double dy = endPoint1.y - c.getY();
		center = new Point2D.Double(ncx, ncy);
		endPoint1 = new Point2D.Double(ncx+dx, ncy+dy);
		endPoint2 = null;
		poly = makePolygon();
	}
	
	public void moveCenter(Point2D nc) {
		Point2D c = getCenter();
		double dx = endPoint1.x - c.getX();
		double dy = endPoint1.y - c.getY();
		center = new Point2D.Double(nc.getX(), nc.getY());
		endPoint1 = new Point2D.Double(nc.getX()+dx, nc.getY()+dy);
		endPoint2 = null;
		poly = makePolygon();
	}
	
	public Point2D getFirstVertexInternal() {
		return endPoint1;
	}
	
	public Point2D getFirstVertex() {
		return new Point2D.Double(endPoint1.x, endPoint1.y);
	}
	
	public void setFirstVertex(double x, double y) {
		this.endPoint1 = new Point2D.Double(x, y);
		poly = makePolygon();
	}
	
	public void setFirstVertex(Point2D vertex) {
		this.endPoint1 = new Point2D.Double(vertex.getX(), vertex.getY());
		poly = makePolygon();
	}
	
	public Point2D getSecondVertexInternal() {
		return endPoint2;
	}
	
	public Point2D getSecondVertex() {
		if (endPoint2 != null) {
			double ex = endPoint2.x;
			double ey = endPoint2.y;
			return new Point2D.Double(ex, ey);
		} else {
			double ex = endPoint1.x;
			double ey = endPoint1.y;
			double cx = center.x;
			double cy = center.y;
			double r = Math.hypot(ey - cy, ex - cx);
			double t = Math.atan2(ey - cy, ex - cx);
			double a = t + (Math.PI * 2.0 / (double)(sides));
			double x = cx + r * Math.cos(a);
			double y = cy + r * Math.sin(a);
			return new Point2D.Double(x, y);
		}
	}
	
	public void setSecondVertex(double x, double y) {
		this.endPoint2 = new Point2D.Double(x, y);
		this.center = null;
		poly = makePolygon();
	}
	
	public void setSecondVertex(Point2D vertex) {
		this.endPoint2 = new Point2D.Double(vertex.getX(), vertex.getY());
		this.center = null;
		poly = makePolygon();
	}
	
	private GeneralPath makePolygon() {
		GeneralPath p = new GeneralPath();
		double ex, ey;
		double cx, cy;
		if (center != null) {
			ex = endPoint1.x;
			ey = endPoint1.y;
			cx = center.x;
			cy = center.y;
		} else {
			ex = endPoint1.x;
			ey = endPoint1.y;
			double s = Math.hypot(endPoint2.y - endPoint1.y, endPoint2.x - endPoint1.x);
			double t = Math.atan2(endPoint2.y - endPoint1.y, endPoint2.x - endPoint1.x);
			cx = ex + s * Math.sin(Math.PI/sides + t) / (2.0 * Math.sin(Math.PI/sides));
			cy = ey - s * Math.cos(Math.PI/sides + t) / (2.0 * Math.sin(Math.PI/sides));
		}
		double r = Math.hypot(ey - cy, ex - cx);
		double t = Math.atan2(ey - cy, ex - cx);
		for (int n = 0, j = 0; n < sides; j++) {
			for (int i = 0; n < sides && ((i == 0) || ((i % sides) != 0)); n++, i += skips) {
				double a = t + (Math.PI * (double)((i+j)*2) / (double)(sides));
				double x = cx + r * Math.cos(a);
				double y = cy + r * Math.sin(a);
				if (i == 0) p.moveTo((float)x, (float)y);
				else p.lineTo((float)x, (float)y);
			}
			p.closePath();
		}
		return p;
	}
	
	public boolean contains(Point2D p) {
		return poly.contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return poly.contains(r);
	}

	public boolean contains(double x, double y) {
		return poly.contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return poly.contains(x, y, w, h);
	}

	public Rectangle getBounds() {
		return poly.getBounds();
	}

	public Rectangle2D getBounds2D() {
		return poly.getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return poly.getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return poly.getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return poly.intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return poly.intersects(x, y, w, h);
	}
	
	public String toString() {
		return "com.kreative.paint.geom.RegularPolygon["+sides+","+skips+","+center+","+endPoint1+","+endPoint2+"]";
	}
}
