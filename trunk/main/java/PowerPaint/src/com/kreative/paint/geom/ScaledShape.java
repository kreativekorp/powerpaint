/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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
import java.awt.geom.Dimension2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public class ScaledShape extends RectangularShape {
	private double x, y, w, h;
	private Shape shape;
	
	public ScaledShape(Rectangle2D bounds, Shape shape) {
		this.x = bounds.getX();
		this.y = bounds.getY();
		this.w = bounds.getWidth();
		this.h = bounds.getHeight();
		this.shape = shape;
	}
	
	public ScaledShape(Point2D origin, Dimension2D size, Shape shape) {
		this.x = origin.getX();
		this.y = origin.getY();
		this.w = size.getWidth();
		this.h = size.getHeight();
		this.shape = shape;
	}
	
	public ScaledShape(double x, double y, double w, double h, Shape shape) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.shape = shape;
	}
	
	public ScaledShape clone() {
		return new ScaledShape(x, y, w, h, shape);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getWidth() {
		return w;
	}
	
	public double getHeight() {
		return h;
	}
	
	public Rectangle2D getFrame() {
		return new Rectangle2D.Double(x, y, w, h);
	}
	
	public Shape getOriginalShape() {
		return shape;
	}
	
	public void setFrame(Rectangle2D bounds) {
		this.x = bounds.getX();
		this.y = bounds.getY();
		this.w = bounds.getWidth();
		this.h = bounds.getHeight();
	}
	
	public void setFrame(Point2D origin, Dimension2D size) {
		this.x = origin.getX();
		this.y = origin.getY();
		this.w = size.getWidth();
		this.h = size.getHeight();
	}
	
	public void setFrame(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void setOriginalShape(Shape shape) {
		this.shape = shape;
	}
	
	public boolean isEmpty() {
		if (shape == null) return true;
		else if (shape instanceof RectangularShape) return ((RectangularShape)shape).isEmpty();
		else return shape.getBounds2D().isEmpty();
	}
	
	private Shape getScaledShape() {
		Shape ss = AffineTransform.getScaleInstance(w, h).createTransformedShape(shape);
		return AffineTransform.getTranslateInstance(x, y).createTransformedShape(ss);
	}
	
	public boolean contains(Point2D pt) {
		return getScaledShape().contains(pt);
	}
	
	public boolean contains(double x, double y) {
		return getScaledShape().contains(x, y);
	}
	
	public boolean contains(Rectangle2D bounds) {
		return getScaledShape().contains(bounds);
	}
	
	public boolean contains(double x, double y, double w, double h) {
		return getScaledShape().contains(x, y, w, h);
	}
	
	public Rectangle getBounds() {
		return getScaledShape().getBounds();
	}
	
	public Rectangle2D getBounds2D() {
		return getScaledShape().getBounds2D();
	}
	
	public PathIterator getPathIterator(AffineTransform at) {
		return getScaledShape().getPathIterator(at);
	}
	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getScaledShape().getPathIterator(at, flatness);
	}
	
	public boolean intersects(Rectangle2D bounds) {
		return getScaledShape().intersects(bounds);
	}
	
	public boolean intersects(double x, double y, double w, double h) {
		return getScaledShape().intersects(x, y, w, h);
	}
}
