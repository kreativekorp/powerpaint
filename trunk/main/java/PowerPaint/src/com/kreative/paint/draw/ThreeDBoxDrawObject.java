/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.util.ShapeUtils;

public class ThreeDBoxDrawObject extends AbstractDrawObject {
	private double x, y, w, h, dx, dy;
	
	public ThreeDBoxDrawObject(double x, double y, double w, double h, double dx, double dy) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.dx = dx;
		this.dy = dy;
	}
	
	public ThreeDBoxDrawObject(double x, double y, double w, double h, double dx, double dy, PaintSettings ps) {
		super(ps);
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.dx = dx;
		this.dy = dy;
	}
	
	public ThreeDBoxDrawObject clone() {
		ThreeDBoxDrawObject o = new ThreeDBoxDrawObject(x, y, w, h, dx, dy, getPaintSettings());
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		return o;
	}
	
	public Rectangle2D getFrame() {
		return new Rectangle2D.Double(x, y, w, h);
	}
	
	public void setFrame(Rectangle2D f) {
		this.x = f.getX();
		this.y = f.getY();
		this.w = f.getWidth();
		this.h = f.getHeight();
	}
	
	public double getDX() {
		return dx;
	}
	
	public double getDY() {
		return dy;
	}
	
	public void setDX(double dx) {
		this.dx = dx;
	}
	
	public void setDY(double dy) {
		this.dy = dy;
	}
	
	private GeneralPath makeBack(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo((float)x, (float)y);
		p.lineTo((float)(x+w), (float)y);
		p.lineTo((float)(x+w), (float)(y+h));
		p.lineTo((float)x, (float)(y+h));
		p.closePath();
		return p;
	}
	
	private GeneralPath makeTop(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo((float)x2, (float)y2);
		p.lineTo((float)(x2+w2), (float)y2);
		p.lineTo((float)(x+w), (float)y);
		p.lineTo((float)x, (float)y);
		p.closePath();
		return p;
	}
	
	private GeneralPath makeBottom(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo((float)x, (float)(y+h));
		p.lineTo((float)(x+w), (float)(y+h));
		p.lineTo((float)(x2+w2), (float)(y2+h2));
		p.lineTo((float)x2, (float)(y2+h2));
		p.closePath();
		return p;
	}
	
	private GeneralPath makeLeft(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo((float)x2, (float)y2);
		p.lineTo((float)x, (float)y);
		p.lineTo((float)x, (float)(y+h));
		p.lineTo((float)x2, (float)(y2+h2));
		p.closePath();
		return p;
	}
	
	private GeneralPath makeRight(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo((float)(x+w), (float)y);
		p.lineTo((float)(x2+w2), (float)y2);
		p.lineTo((float)(x2+w2), (float)(y2+h2));
		p.lineTo((float)(x+w), (float)(y+h));
		p.closePath();
		return p;
	}
	
	private GeneralPath makeFront(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo((float)x2, (float)y2);
		p.lineTo((float)(x2+w2), (float)y2);
		p.lineTo((float)(x2+w2), (float)(y2+h2));
		p.lineTo((float)x2, (float)(y2+h2));
		p.closePath();
		return p;
	}
	
	private Shape[] getShapes() {
		double hypot = Math.hypot(dy,dx);
		double angle = Math.atan2(dy,dx);
		double x2 = x + hypot*(3*Math.sqrt(2)*Math.cos(angle)-1)/4;
		double y2 = y + hypot*(3*Math.sqrt(2)*Math.sin(angle)-1)/4;
		double w2 = w + hypot/2;
		double h2 = h + hypot/2;
		Shape[] p = new Shape[6];
		p[0] = makeBack(x, y, w, h, x2, y2, w2, h2);
		if (dx < 0) {
			if (dy < 0) {
				// dx is negative, dy is negative
				// draw top and left panel, then right and bottom panel
				p[1] = makeTop(x, y, w, h, x2, y2, w2, h2);
				p[2] = makeLeft(x, y, w, h, x2, y2, w2, h2);
				p[3] = makeBottom(x, y, w, h, x2, y2, w2, h2);
				p[4] = makeRight(x, y, w, h, x2, y2, w2, h2);
			} else {
				// dx is negative, dy is positive
				// draw left and bottom panel, then top and right panel
				p[1] = makeLeft(x, y, w, h, x2, y2, w2, h2);
				p[2] = makeBottom(x, y, w, h, x2, y2, w2, h2);
				p[3] = makeTop(x, y, w, h, x2, y2, w2, h2);
				p[4] = makeRight(x, y, w, h, x2, y2, w2, h2);
			}
		} else {
			if (dy < 0) {
				// dx is positive, dy is negative
				// draw top and right panel, then left and bottom panel
				p[1] = makeTop(x, y, w, h, x2, y2, w2, h2);
				p[2] = makeRight(x, y, w, h, x2, y2, w2, h2);
				p[3] = makeLeft(x, y, w, h, x2, y2, w2, h2);
				p[4] = makeBottom(x, y, w, h, x2, y2, w2, h2);
			} else {
				// dx is positive, dy is positive
				// draw right and bottom panel, then top and left panel
				p[1] = makeBottom(x, y, w, h, x2, y2, w2, h2);
				p[2] = makeRight(x, y, w, h, x2, y2, w2, h2);
				p[3] = makeTop(x, y, w, h, x2, y2, w2, h2);
				p[4] = makeLeft(x, y, w, h, x2, y2, w2, h2);
			}
		}
		p[5] = makeFront(x, y, w, h, x2, y2, w2, h2);
		return p;
	}
	
	private Shape[] getTransformedShapes() {
		Shape[] s = getShapes();
		AffineTransform tx = getTransform();
		if (tx != null) {
			for (int i = 0; i < s.length; i++) {
				s[i] = tx.createTransformedShape(s[i]);
			}
		}
		return s;
	}
	
	private Shape[] getTransformedStrokedShapes() {
		Shape[] s = getShapes();
		AffineTransform tx = getTransform();
		if (tx != null) {
			for (int i = 0; i < s.length; i++) {
				s[i] = tx.createTransformedShape(s[i]);
			}
		}
		Stroke st = getStroke();
		if (st != null) {
			for (int i = 0; i < s.length; i++) {
				if (!ShapeUtils.shapeIsEmpty(s[i])) {
					s[i] = st.createStrokedShape(s[i]);
				}
			}
		}
		return s;
	}
	
	private Shape getTransformedShape() {
		Area a = new Area();
		for (Shape s : getTransformedShapes()) {
			a.add(new Area(s));
		}
		return a;
	}
	
	private Shape getTransformedStrokedShape() {
		Area a = new Area();
		for (Shape s : getTransformedStrokedShapes()) {
			a.add(new Area(s));
		}
		return a;
	}
	
	private void paint(Graphics2D g, Shape ts, Shape tss) {
		push(g);
		if (isFilled()) {
			applyFill(g);
			g.fill(ts);
		}
		if (isDrawn()) {
			applyDraw(g);
			g.fill(tss);
		}
		pop(g);
	}
	
	public void paint(Graphics2D g) {
		paint(g, getTransformedShape(), getTransformedStrokedShape());
	}
	
	public void paint(Graphics2D g, int tx, int ty) {
		paint(g,
			AffineTransform.getTranslateInstance(tx, ty)
			.createTransformedShape(getTransformedShape()),
			AffineTransform.getTranslateInstance(tx, ty)
			.createTransformedShape(getTransformedStrokedShape())
		);
	}

	public boolean contains(Point2D p) {
		return (isFilled() && getTransformedShape().contains(p))
			|| (isDrawn() && getTransformedStrokedShape().contains(p));
	}

	public boolean contains(Rectangle2D r) {
		return (isFilled() && getTransformedShape().contains(r))
			|| (isDrawn() && getTransformedStrokedShape().contains(r));
	}

	public boolean contains(double x, double y) {
		return (isFilled() && getTransformedShape().contains(x,y))
			|| (isDrawn() && getTransformedStrokedShape().contains(x,y));
	}

	public boolean contains(double x, double y, double w, double h) {
		return (isFilled() && getTransformedShape().contains(x,y,w,h))
			|| (isDrawn() && getTransformedStrokedShape().contains(x,y,w,h));
	}

	public Rectangle getBounds() {
		return isDrawn() ? getTransformedStrokedShape().getBounds() : getTransformedShape().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return isDrawn() ? getTransformedStrokedShape().getBounds2D() : getTransformedShape().getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return getTransformedShape().getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getTransformedShape().getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return (isFilled() && getTransformedShape().intersects(r))
			|| (isDrawn() && getTransformedStrokedShape().intersects(r));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return (isFilled() && getTransformedShape().intersects(x,y,w,h))
			|| (isDrawn() && getTransformedStrokedShape().intersects(x,y,w,h));
	}
	
	public int getControlPointCount() {
		return 10;
	}
	
	protected ControlPoint getControlPointImpl(int i) {
		double x1 = Math.min(x, x+w);
		double y1 = Math.min(y, y+h);
		double x2 = Math.max(x, x+w);
		double y2 = Math.max(y, y+h);
		switch (i) {
		case 0: return new ControlPoint.Double((x1+x2)/2.0, (y1+y2)/2.0, ControlPointType.CENTER);
		case 1: return new ControlPoint.Double(x1, y1, ControlPointType.NORTHWEST);
		case 2: return new ControlPoint.Double(x2, y1, ControlPointType.NORTHEAST);
		case 3: return new ControlPoint.Double(x1, y2, ControlPointType.SOUTHWEST);
		case 4: return new ControlPoint.Double(x2, y2, ControlPointType.SOUTHEAST);
		case 5: return new ControlPoint.Double((x1+x2)/2.0, y1, ControlPointType.NORTH);
		case 6: return new ControlPoint.Double((x1+x2)/2.0, y2, ControlPointType.SOUTH);
		case 7: return new ControlPoint.Double(x1, (y1+y2)/2.0, ControlPointType.WEST);
		case 8: return new ControlPoint.Double(x2, (y1+y2)/2.0, ControlPointType.EAST);
		case 9: return new ControlPoint.Double((x1+x2)/2.0 + dx, (y1+y2)/2.0 + dy, ControlPointType.PULLTAB);
		default: return null;
		}
	}
	
	protected int setControlPointImpl(int i, Point2D p) {
		double x1 = x;
		double y1 = y;
		double x2 = x+w;
		double y2 = y+h;
		switch (i) {
		case 0:
			x1 = (int)(p.getX() - w/2.0);
			y1 = (int)(p.getY() - h/2.0);
			x2 = x1+w;
			y2 = y1+h;
			break;
		case 1: x1 = p.getX(); y1 = p.getY(); break;
		case 2: x2 = p.getX(); y1 = p.getY(); break;
		case 3: x1 = p.getX(); y2 = p.getY(); break;
		case 4: x2 = p.getX(); y2 = p.getY(); break;
		case 5: y1 = p.getY(); break;
		case 6: y2 = p.getY(); break;
		case 7: x1 = p.getX(); break;
		case 8: x2 = p.getX(); break;
		case 9: dx = p.getX() - (x1+x2)/2.0; dy = p.getY() - (y1+y2)/2.0; return 9;
		}
		x = Math.min(x1, x2);
		y = Math.min(y1, y2);
		w = Math.abs(x2-x1);
		h = Math.abs(y2-y1);
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
		return new Point2D.Double(x,y);
	}
	
	protected void setAnchorImpl(Point2D p) {
		x = p.getX();
		y = p.getY();
	}
	
	public String toString() {
		return "com.kreative.paint.objects.ThreeDBoxDrawObject["+x+","+y+","+w+","+h+","+dx+"."+dy+","+super.toString()+"]";
	}
}
