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
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.PaintSettings;

public class PerspectiveGridDrawObject extends AbstractDrawObject {
	private Rectangle2D bounds;
	private int nt, nb, nh;
	
	public PerspectiveGridDrawObject(float x, float y, float w, float h) {
		super();
		bounds = new Rectangle2D.Float(x, y, w, h);
		nt = 20; nb = 10; nh = 10;
	}

	public PerspectiveGridDrawObject(float x, float y, float w, float h, int nt, int nb, int nh) {
		super();
		bounds = new Rectangle2D.Float(x, y, w, h);
		this.nt = nt; this.nb = nb; this.nh = nh;
	}
	
	public PerspectiveGridDrawObject(PaintSettings ps, float x, float y, float w, float h) {
		super(ps);
		bounds = new Rectangle2D.Float(x, y, w, h);
		nt = 20; nb = 10; nh = 10;
	}

	public PerspectiveGridDrawObject(PaintSettings ps, float x, float y, float w, float h, int nt, int nb, int nh) {
		super(ps);
		bounds = new Rectangle2D.Float(x, y, w, h);
		this.nt = nt; this.nb = nb; this.nh = nh;
	}
	
	public PerspectiveGridDrawObject clone() {
		PerspectiveGridDrawObject o = new PerspectiveGridDrawObject(
				getPaintSettings(),
				(float)bounds.getX(), (float)bounds.getY(),
				(float)bounds.getWidth(), (float)bounds.getHeight(),
				nt, nb, nh
		);
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		return o;
	}
	
	public Rectangle2D getGridBounds() { return bounds; }
	public int getGridWidthTop() { return nt; }
	public int getGridWidthBottom() { return nb; }
	public int getGridHeight() { return nh; }
	
	private void paintGrid(Graphics2D g) {
		Shape clip = g.getClip();
		g.clip(bounds.getBounds2D());
		float vx = (float)bounds.getMinX();
		float vy = (float)bounds.getMinY();
		float sw = (float)getStroke().createStrokedShape(new Line2D.Float(0f,0f,10f,0f)).getBounds2D().getHeight();
		float vw = (float)bounds.getWidth()-sw;
		float vh = (float)bounds.getHeight()-sw;
		int nw = Math.max(nt,nb);
		for (int i = -nw; i <= nw; i+=2) {
			float x1 = (float)(vw * (i + nt)) / (float)(2 * nt);
			float x2 = (float)(vw * (i + nb)) / (float)(2 * nb);
			g.draw(new Line2D.Float(vx+x1, vy, vx+x2, vy+vh));
		}
		for (int j = 0; j <= nh; j++) {
			float y = (float)(vh * j * nb) / (float)((nh-j) * nt + j * nb);
			g.draw(new Line2D.Float(vx, vy+y, vx+vw, vy+y));
		}
		g.setClip(clip);
	}

	public void paint(Graphics2D g) {
		push(g);
		AffineTransform tx = g.getTransform();
		if (getTransform() != null) g.transform(getTransform());
		if (isFilled()) {
			applyFill(g);
			g.fill(bounds);
		}
		if (isDrawn()) {
			applyDraw(g);
			paintGrid(g);
		}
		pop(g);
		g.setTransform(tx);
	}

	public void paint(Graphics2D g, int x, int y) {
		push(g);
		AffineTransform tx = g.getTransform();
		g.translate(x, y);
		if (getTransform() != null) g.transform(getTransform());
		if (isFilled()) {
			applyFill(g);
			g.fill(bounds);
		}
		if (isDrawn()) {
			applyDraw(g);
			paintGrid(g);
		}
		pop(g);
		g.setTransform(tx);
	}
	
	private Shape getTransformedBounds() {
		AffineTransform tx = getTransform();
		return (tx == null) ? bounds : tx.createTransformedShape(bounds);
	}

	public boolean contains(Point2D p) {
		return getTransformedBounds().contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return getTransformedBounds().contains(r);
	}

	public boolean contains(double x, double y) {
		return getTransformedBounds().contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return getTransformedBounds().contains(x, y, w, h);
	}

	public Rectangle getBounds() {
		return getTransformedBounds().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return getTransformedBounds().getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return getTransformedBounds().getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getTransformedBounds().getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return getTransformedBounds().intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return getTransformedBounds().intersects(x, y, w, h);
	}
	
	public int getControlPointCount() {
		return 9;
	}
	
	protected ControlPoint getControlPointImpl(int i) {
		switch (i) {
		case 0: return new ControlPoint.Double(bounds.getX()+bounds.getWidth()/2.0f, bounds.getY()+bounds.getHeight()/2.0f, ControlPointType.CENTER);
		case 1: return new ControlPoint.Double(bounds.getX(), bounds.getY(), ControlPointType.NORTHWEST);
		case 2: return new ControlPoint.Double(bounds.getX()+bounds.getWidth(), bounds.getY(), ControlPointType.NORTHEAST);
		case 3: return new ControlPoint.Double(bounds.getX(), bounds.getY()+bounds.getHeight(), ControlPointType.SOUTHWEST);
		case 4: return new ControlPoint.Double(bounds.getX()+bounds.getWidth(), bounds.getY()+bounds.getHeight(), ControlPointType.SOUTHEAST);
		case 5: return new ControlPoint.Double(bounds.getX()+bounds.getWidth()/2.0f, bounds.getY(), ControlPointType.NORTH);
		case 6: return new ControlPoint.Double(bounds.getX()+bounds.getWidth()/2.0f, bounds.getY()+bounds.getHeight(), ControlPointType.SOUTH);
		case 7: return new ControlPoint.Double(bounds.getX(), bounds.getY()+bounds.getHeight()/2.0f, ControlPointType.WEST);
		case 8: return new ControlPoint.Double(bounds.getX()+bounds.getWidth(), bounds.getY()+bounds.getHeight()/2.0f, ControlPointType.EAST);
		default: return null;
		}
	}
	
	protected int setControlPointImpl(int i, Point2D p) {
		double x1 = bounds.getX(), y1 = bounds.getY(), x2 = bounds.getX()+bounds.getWidth(), y2 = bounds.getY()+bounds.getHeight();
		switch (i) {
		case 0:
			x1 = (int)(p.getX() - bounds.getWidth()/2.0);
			y1 = (int)(p.getY() - bounds.getHeight()/2.0);
			x2 = x1+bounds.getWidth();
			y2 = y1+bounds.getHeight();
			break;
		case 1: x1 = p.getX(); y1 = p.getY(); break;
		case 2: x2 = p.getX(); y1 = p.getY(); break;
		case 3: x1 = p.getX(); y2 = p.getY(); break;
		case 4: x2 = p.getX(); y2 = p.getY(); break;
		case 5: y1 = p.getY(); break;
		case 6: y2 = p.getY(); break;
		case 7: x1 = p.getX(); break;
		case 8: x2 = p.getX(); break;
		}
		bounds = new Rectangle2D.Double(Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2-x1), Math.abs(y2-y1));
		if (x2 < x1) {
			     if (i == 2) i = 1;
			else if (i == 8) i = 7;
			else if (i == 4) i = 3;
			else if (i == 1) i = 2;
			else if (i == 7) i = 8;
			else if (i == 3) i = 4;
		}
		if (y2 < y1) {
			     if (i == 1) i = 3;
			else if (i == 5) i = 6;
			else if (i == 2) i = 4;
			else if (i == 3) i = 1;
			else if (i == 6) i = 5;
			else if (i == 4) i = 2;
		}
		return i;
	}
	
	protected Point2D getAnchorImpl() {
		return new Point2D.Double(bounds.getX(), bounds.getY());
	}
	
	protected void setAnchorImpl(Point2D p) {
		bounds.setFrame(p.getX(), p.getY(), bounds.getWidth(), bounds.getHeight());
	}
	
	public String toString() {
		return "com.kreative.paint.objects.PerspectiveGridDrawObject["+bounds+","+nt+","+nb+","+nh+","+super.toString()+"]";
	}
}
