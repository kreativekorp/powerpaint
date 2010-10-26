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
import java.awt.geom.Arc2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public class RightArc extends RectangularShape {
	private float x, y, width, height;
	private RectangularShape s;
	
	private RectangularShape makeShape() {
		float sx = this.x;
		float sy = this.y;
		float x = this.x + this.width;
		float y = this.y + this.height;
		float x1 = Math.min(sx,x);
		float y1 = Math.min(sy,y);
		float w = Math.abs(x-sx);
		float h = Math.abs(y-sy);
		float sa, ea;
		if (y >= sy) {
			// top half
			if (x >= sx) {
				sa = 0; ea = 90;
				h += h; x1 -= w; w += w;
			} else {
				sa = 90; ea = 90;
				h += h; w += w;
			}
		} else {
			// bottom half
			if (x >= sx) {
				sa = 270; ea = 90;
				y1 -= h; h += h; x1 -= w; w += w;
			} else {
				sa = 180; ea = 90;
				y1 -= h; h += h; w += w;
			}
		}
		return new Arc2D.Float(x1, y1, w, h, sa, ea, Arc2D.PIE);
	}
	
	public RightArc(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.s = makeShape();
	}
	
	public RightArc clone() {
		return new RightArc(x, y, width, height);
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	public boolean isEmpty() {
		return s.isEmpty();
	}
	
	public void setFrame(double x, double y, double w, double h) {
		this.x = (float)x;
		this.y = (float)y;
		this.width = (float)w;
		this.height = (float)h;
		this.s = makeShape();
	}
	
	public boolean contains(Point2D p) {
		return s.contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return s.contains(r);
	}

	public boolean contains(double x, double y) {
		return s.contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return s.contains(x, y, w, h);
	}

	public Rectangle getBounds() {
		return s.getBounds();
	}

	public Rectangle2D getBounds2D() {
		return s.getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return s.getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return s.getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return s.intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return s.intersects(x, y, w, h);
	}
}
