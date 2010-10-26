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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class CircularShape implements Shape, Cloneable {
	public abstract double getCenterX();
	public abstract double getCenterY();
	public abstract double getEndpointX();
	public abstract double getEndpointY();
	public abstract void setCircle(double cx, double cy, double ex, double ey);
	
	public abstract CircularShape clone();
	
	public boolean contains(Point2D p) {
		Point2D c = getCenter();
		double rd = getRadius();
		return (p.distance(c) <= rd);
	}
	
	public boolean contains(Rectangle2D r) {
		Point2D c = getCenter();
		double rd = getRadius();
		Point2D p1 = new Point2D.Double(r.getMinX(), r.getMinY());
		Point2D p2 = new Point2D.Double(r.getMinX(), r.getMaxY());
		Point2D p3 = new Point2D.Double(r.getMaxX(), r.getMinY());
		Point2D p4 = new Point2D.Double(r.getMaxX(), r.getMaxY());
		return (p1.distance(c) <= rd && p2.distance(c) <= rd && p3.distance(c) <= rd && p4.distance(c) <= rd);
	}
	
	public Rectangle getBounds() {
		Point2D c = getCenter();
		double r = getRadius();
		int x1 = (int)Math.floor(c.getX()-r);
		int x2 = (int)Math.ceil(c.getX()+r);
		int y1 = (int)Math.floor(c.getY()-r);
		int y2 = (int)Math.ceil(c.getY()+r);
		return new Rectangle(x1, y1, x2-x1, y2-y1);
	}
	
	public Point2D getCenter() {
		return new Point2D.Double(getCenterX(), getCenterY());
	}
	
	public Point2D getEndpoint() {
		return new Point2D.Double(getEndpointX(), getEndpointY());
	}
	
	public double getRadius() {
		return Math.hypot(getEndpointY()-getCenterY(), getEndpointX()-getCenterX());
	}
	
	public double getAngle() {
		return Math.atan2(getEndpointY()-getCenterY(), getEndpointX()-getCenterX());
	}
	
	public boolean intersects(Rectangle2D r) {
		Point2D c = getCenter();
		double rd = getRadius();
		Shape s = new Ellipse2D.Double(c.getX()-rd, c.getY()-rd, rd*2.0, rd*2.0);
		return s.intersects(r);
	}
	
	public void setCenter(Point2D c) {
		double dx = getEndpointX()-getCenterX();
		double dy = getEndpointY()-getCenterY();
		setCircle(c.getX(), c.getY(), c.getX()+dx, c.getY()+dy);
	}
	
	public void setEndpoint(Point2D e) {
		double cx = getCenterX();
		double cy = getCenterY();
		setCircle(cx, cy, e.getX(), e.getY());
	}
	
	public void setRadius(double r) {
		double cx = getCenterX();
		double cy = getCenterY();
		double a = Math.atan2(getEndpointY()-getCenterY(), getEndpointX()-getCenterX());
		setCircle(cx, cy, cx+r*Math.cos(a), cy+r*Math.sin(a));
	}
	
	public void setAngle(double a) {
		double cx = getCenterX();
		double cy = getCenterY();
		double r = Math.hypot(getEndpointY()-getCenterY(), getEndpointX()-getCenterX());
		setCircle(cx, cy, cx+r*Math.cos(a), cy+r*Math.sin(a));
	}
}
