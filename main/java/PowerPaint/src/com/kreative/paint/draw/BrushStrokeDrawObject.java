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

package com.kreative.paint.draw;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import com.kreative.paint.BufferedImagePaintSurface;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.util.Bitmap;

public class BrushStrokeDrawObject extends AbstractDrawObject {
	private Bitmap brush;
	private GeneralPath pts;
	private transient int cachex, cachey;
	private transient BufferedImage cache;
	
	public BrushStrokeDrawObject(Bitmap brush, GeneralPath p) {
		super();
		this.brush = brush;
		this.pts = p;
		this.cache = null;
	}
	
	public BrushStrokeDrawObject(Bitmap brush, GeneralPath p, PaintSettings ps) {
		super(ps);
		this.brush = brush;
		this.pts = p;
		this.cache = null;
	}
	
	public BrushStrokeDrawObject clone() {
		BrushStrokeDrawObject o = new BrushStrokeDrawObject(brush, (GeneralPath)pts.clone(), getPaintSettings());
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		return o;
	}
	
	public Bitmap getBrush() { return brush; }
	public GeneralPath getPath() { return pts; }
	
	private Shape getShape() {
		AffineTransform tx = getTransform();
		return (tx == null) ? pts : tx.createTransformedShape(pts);
	}
	
	private void drag(PaintSurface srf, Graphics2D g, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 0; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m;
			float y = sy + ((dy-sy)*i)/m;
			x -= brush.getWidth()/2;
			y -= brush.getHeight()/2;
			brush.paint(srf, g, (int)x, (int)y);
		}
	}
	
	private void paint(PaintSurface srf, Graphics2D g, Shape sh) {
		push(g);
		if (isFilled()) {
			applyFill(g);
			float cx = 0.0f, cy = 0.0f;
			float[] c = new float[6];
			PathIterator i = sh.getPathIterator(null, 1.0);
			while (!i.isDone()) {
				switch (i.currentSegment(c)) {
				case PathIterator.SEG_MOVETO:
					cx = c[0]; cy = c[1];
					break;
				case PathIterator.SEG_LINETO:
					drag(srf, g, cx, cy, c[0], c[1]);
					cx = c[0]; cy = c[1];
					break;
				case PathIterator.SEG_QUADTO:
					drag(srf, g, cx, cy, c[2], c[3]);
					cx = c[2]; cy = c[3];
					break;
				case PathIterator.SEG_CUBICTO:
					drag(srf, g, cx, cy, c[4], c[5]);
					cx = c[4]; cy = c[5];
					break;
				}
				i.next();
			}
		}
		pop(g);
	}

	public void paint(Graphics2D g) {
		if (cache == null) {
			Shape sh = getShape();
			Rectangle b = sh.getBounds();
			cachex = b.x;
			cachey = b.y;
			cache = new BufferedImage(b.width+brush.getWidth()*2, b.height+brush.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
			PaintSurface srf = new BufferedImagePaintSurface(cache);
			Graphics2D gc = cache.createGraphics();
			gc.translate(brush.getWidth()-b.x, brush.getHeight()-b.y);
			paint(srf, gc, sh);
			gc.dispose();
		}
		g.drawImage(cache, null, cachex-brush.getWidth(), cachey-brush.getHeight());
	}

	public void paint(Graphics2D g, int tx, int ty) {
		if (cache == null) {
			Shape sh = getShape();
			Rectangle b = sh.getBounds();
			cachex = b.x;
			cachey = b.y;
			cache = new BufferedImage(b.width+brush.getWidth()*2, b.height+brush.getHeight()*2, BufferedImage.TYPE_INT_ARGB);
			PaintSurface srf = new BufferedImagePaintSurface(cache);
			Graphics2D gc = cache.createGraphics();
			gc.translate(brush.getWidth()-b.x, brush.getHeight()-b.y);
			paint(srf, gc, sh);
			gc.dispose();
		}
		g.drawImage(cache, null, cachex+tx-brush.getWidth(), cachey+ty-brush.getHeight());
	}

	public boolean contains(Point2D p) {
		return getShape().contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return getShape().contains(r);
	}

	public boolean contains(double x, double y) {
		return getShape().contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return getShape().contains(x, y, w, h);
	}

	public Rectangle getBounds() {
		return getShape().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return getShape().getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return getShape().getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getShape().getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return getShape().intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return getShape().intersects(x, y, w, h);
	}
	
	public int getControlPointCount() {
		return DrawObjectUtilities.getPathControlPointCount(pts);
	}
	
	protected ControlPoint getControlPointImpl(int i) {
		return DrawObjectUtilities.getPathControlPoint(pts, i);
	}
	
	protected ControlPoint[] getControlPointsImpl() {
		return DrawObjectUtilities.getPathControlPoints(pts);
	}
	
	protected int setControlPointImpl(int i, Point2D p) {
		pts = DrawObjectUtilities.setPathControlPoint(pts, i, p);
		return i;
	}
	
	protected Point2D getAnchorImpl() {
		return DrawObjectUtilities.getPathAnchor(pts);
	}
	
	protected void setAnchorImpl(Point2D p) {
		pts = DrawObjectUtilities.setPathAnchor(pts, p);
	}
	
	protected void edited() {
		cache = null;
	}
	
	public String toString() {
		return "com.kreative.paint.objects.BrushStrokeDrawObject["+brush+","+pts+","+super.toString()+"]";
	}
}