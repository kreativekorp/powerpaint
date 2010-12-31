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
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class BitmapShape implements Shape, Cloneable {
	private int[] bitmap;
	private int x, y, w, h;
	private GeneralPath path;
	
	public BitmapShape(int[] bitmap, int x, int y, int w, int h) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		PathGraph pg = new PathGraph();
		for (int ay = 0, py = y; ay < w*h; ay += w, py++) {
			for (int ax = 0, px = x; ax < w; ax++, px++) {
				if (bit(bitmap[ay+ax])) pg.plot(px, py);
			}
		}
		this.path = pg.makePath();
	}
	
	public BitmapShape clone() {
		int[] bm = new int[bitmap.length];
		for (int i = 0; i < bm.length; i++) bm[i] = bitmap[i];
		return new BitmapShape(bm, x, y, w, h);
	}
	
	public int[] getBitmap() { return bitmap; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getWidth() { return w; }
	public int getHeight() { return h; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	
	public boolean contains(Point2D p) {
		return this.path.contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return this.path.contains(r);
	}

	public boolean contains(double x, double y) {
		return this.path.contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return this.path.contains(x, y, w, h);
	}

	public Rectangle getBounds() {
		return this.path.getBounds();
	}

	public Rectangle2D getBounds2D() {
		return this.path.getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return this.path.getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return this.path.getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return this.path.intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return this.path.intersects(x, y, w, h);
	}
	
	private static boolean bit(int c) {
		return (((c & 0xFF000000) < 0) && ((c & 0xFF0000) < 0x800000) && ((c & 0xFF00) < 0x8000) && ((c & 0xFF) < 0x80));
	}
}
