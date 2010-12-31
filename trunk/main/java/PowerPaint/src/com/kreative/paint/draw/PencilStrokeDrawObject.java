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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.PaintSettings;

public class PencilStrokeDrawObject extends AbstractDrawObject {
	private GeneralPath pts;
	
	public PencilStrokeDrawObject(GeneralPath p) {
		super();
		pts = p;
	}
	
	public PencilStrokeDrawObject(GeneralPath p, PaintSettings ps) {
		super(ps);
		pts = p;
	}
	
	public PencilStrokeDrawObject clone() {
		PencilStrokeDrawObject o = new PencilStrokeDrawObject((GeneralPath)pts.clone(), getPaintSettings());
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		return o;
	}
	
	public GeneralPath getPath() { return pts; }
	
	private Shape getShape() {
		AffineTransform tx = getTransform();
		return (tx == null) ? pts : tx.createTransformedShape(pts);
	}
	
	public void paint(Graphics2D g) {
		push(g);
		if (isFilled()) {
			applyFill(g);
			g.setStroke(new BasicStroke(1));
			g.draw(getShape());
		}
		pop(g);
	}

	public void paint(Graphics2D g, int tx, int ty) {
		push(g);
		if (isFilled()) {
			applyFill(g);
			g.setStroke(new BasicStroke(1));
			g.draw(AffineTransform.getTranslateInstance(tx,ty).createTransformedShape(getShape()));
		}
		pop(g);
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
	
	public String toString() {
		return "com.kreative.paint.objects.PencilStrokeDrawObject["+pts+","+super.toString()+"]";
	}
}
