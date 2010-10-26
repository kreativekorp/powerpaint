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

package com.kreative.paint.draw;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.PaintSettings;

public class CropMarkDrawObject extends AbstractDrawObject {
	private float x1, y1, x2, y2;
	private int divH, divV;
	
	public CropMarkDrawObject(float x1, float y1, float x2, float y2) {
		super();
		setFillComposite(null);
		setFillPaint(null);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.divH = 1;
		this.divV = 1;
	}
	
	public CropMarkDrawObject(float x1, float y1, float x2, float y2, int divH, int divV) {
		super();
		setFillComposite(null);
		setFillPaint(null);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.divH = divH;
		this.divV = divV;
	}
	
	public CropMarkDrawObject(float x1, float y1, float x2, float y2, PaintSettings ps) {
		super(ps);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.divH = 1;
		this.divV = 1;
	}
	
	public CropMarkDrawObject(float x1, float y1, float x2, float y2, int divH, int divV, PaintSettings ps) {
		super(ps);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.divH = divH;
		this.divV = divV;
	}
	
	public CropMarkDrawObject clone() {
		CropMarkDrawObject o = new CropMarkDrawObject(x1, y1, x2, y2, divH, divV, getPaintSettings());
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		return o;
	}
	
	public float getX1() { return x1; }
	public float getY1() { return y1; }
	public float getX2() { return x2; }
	public float getY2() { return y2; }
	public int getHorizDivisions() { return divH; }
	public int getVertDivisions() { return divV; }
	
	private Shape makeFillShape(int tx, int ty) {
		float x = Math.min(x1,x2);
		float y = Math.min(y1,y2);
		float w = Math.abs(x2-x1);
		float h = Math.abs(y2-y1);
		Shape s = new Rectangle2D.Float(x-16.0f, y-16.0f, w+32.0f, h+32.0f);
		s = (getTransform() == null) ? s : getTransform().createTransformedShape(s);
		s = (tx == 0 && ty == 0) ? s : AffineTransform.getTranslateInstance(tx, ty).createTransformedShape(s);
		return s;
	}
	
	private Shape makeMarkShape(int tx, int ty) {
		float x3 = Math.min(x1,x2);
		float y3 = Math.min(y1,y2);
		float x4 = Math.max(x1,x2);
		float y4 = Math.max(y1,y2);
		float w  = Math.abs(x2-x1);
		float h  = Math.abs(y2-y1);
		GeneralPath p = new GeneralPath();
		for (int i = 0; i <= divH; i++) {
			float x = x3 + w * (float)i / (float)divH;
			p.moveTo(x, y3-40.0f);
			p.lineTo(x, y3-8.0f);
			p.moveTo(x, y4+8.0f);
			p.lineTo(x, y4+40.0f);
		}
		for (int i = 0; i <= divV; i++) {
			float y = y3 + h * (float)i / (float)divV;
			p.moveTo(x3-40.0f, y);
			p.lineTo(x3-8.0f, y);
			p.moveTo(x4+8.0f, y);
			p.lineTo(x4+40.0f, y);
		}
		Shape s = p;
		s = (getTransform() == null) ? s : getTransform().createTransformedShape(s);
		s = (tx == 0 && ty == 0) ? s : AffineTransform.getTranslateInstance(tx, ty).createTransformedShape(s);
		return s;
	}

	public void paint(Graphics2D g) {
		push(g);
		if (isFilled()) {
			applyFill(g);
			g.fill(makeFillShape(0,0));
		}
		if (isDrawn()) {
			applyDraw(g);
			g.draw(makeMarkShape(0,0));
		}
		pop(g);
	}

	public void paint(Graphics2D g, int tx, int ty) {
		push(g);
		if (isFilled()) {
			applyFill(g);
			g.fill(makeFillShape(tx,ty));
		}
		if (isDrawn()) {
			applyDraw(g);
			g.draw(makeMarkShape(tx,ty));
		}
		pop(g);
	}

	public boolean contains(Point2D p) {
		return (isFilled() && makeFillShape(0,0).contains(p)) || (isDrawn() && makeMarkShape(0,0).contains(p));
	}

	public boolean contains(Rectangle2D r) {
		return (isFilled() && makeFillShape(0,0).contains(r)) || (isDrawn() && makeMarkShape(0,0).contains(r));
	}

	public boolean contains(double x, double y) {
		return (isFilled() && makeFillShape(0,0).contains(x,y)) || (isDrawn() && makeMarkShape(0,0).contains(x,y));
	}

	public boolean contains(double x, double y, double w, double h) {
		return (isFilled() && makeFillShape(0,0).contains(x,y,w,h)) || (isDrawn() && makeMarkShape(0,0).contains(x,y,w,h));
	}

	public Rectangle getBounds() {
		return isDrawn() ? makeMarkShape(0,0).getBounds() : makeFillShape(0,0).getBounds();
	}

	public Rectangle2D getBounds2D() {
		return isDrawn() ? makeMarkShape(0,0).getBounds2D() : makeFillShape(0,0).getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		GeneralPath p = new GeneralPath();
		p.append(makeFillShape(0,0), false);
		p.append(makeMarkShape(0,0), false);
		return p.getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		GeneralPath p = new GeneralPath();
		p.append(makeFillShape(0,0), false);
		p.append(makeMarkShape(0,0), false);
		return p.getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return (isFilled() && makeFillShape(0,0).intersects(r)) || (isDrawn() && makeMarkShape(0,0).intersects(r));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return (isFilled() && makeFillShape(0,0).intersects(x,y,w,h)) || (isDrawn() && makeMarkShape(0,0).intersects(x,y,w,h));
	}
	
	public int getControlPointCount() {
		return 9;
	}
	
	protected ControlPoint getControlPointImpl(int i) {
		switch (i) {
		case 0: return new ControlPoint.Float((x1+x2)/2.0f, (y1+y2)/2.0f, ControlPointType.CENTER);
		case 1: return new ControlPoint.Float(x1, y1, ControlPointType.NORTHWEST);
		case 2: return new ControlPoint.Float(x2, y1, ControlPointType.NORTHEAST);
		case 3: return new ControlPoint.Float(x1, y2, ControlPointType.SOUTHWEST);
		case 4: return new ControlPoint.Float(x2, y2, ControlPointType.SOUTHEAST);
		case 5: return new ControlPoint.Float((x1+x2)/2.0f, y1, ControlPointType.NORTH);
		case 6: return new ControlPoint.Float((x1+x2)/2.0f, y2, ControlPointType.SOUTH);
		case 7: return new ControlPoint.Float(x1, (y1+y2)/2.0f, ControlPointType.WEST);
		case 8: return new ControlPoint.Float(x2, (y1+y2)/2.0f, ControlPointType.EAST);
		default: return null;
		}
	}
	
	protected int setControlPointImpl(int i, Point2D p) {
		switch (i) {
		case 0:
			float w = x2-x1;
			float h = y2-y1;
			x1 = (float)p.getX() - w/2.0f;
			x2 = (float)p.getX() + w/2.0f;
			y1 = (float)p.getY() - h/2.0f;
			y2 = (float)p.getY() + h/2.0f;
			break;
		case 1: x1 = (float)p.getX(); y1 = (float)p.getY(); break;
		case 2: x2 = (float)p.getX(); y1 = (float)p.getY(); break;
		case 3: x1 = (float)p.getX(); y2 = (float)p.getY(); break;
		case 4: x2 = (float)p.getX(); y2 = (float)p.getY(); break;
		case 5: y1 = (float)p.getY(); break;
		case 6: y2 = (float)p.getY(); break;
		case 7: x1 = (float)p.getX(); break;
		case 8: x2 = (float)p.getX(); break;
		}
		return i;
	}
	
	protected Point2D getAnchorImpl() {
		return new Point2D.Float(x1,y1);
	}
	
	protected void setAnchorImpl(Point2D p) {
		float dx = x2-x1;
		float dy = y2-y1;
		x1 = (float)p.getX();
		y1 = (float)p.getY();
		x2 = x1+dx;
		y2 = y1+dy;
	}
	
	public String toString() {
		return "com.kreative.paint.objects.CropMarkDrawObject["+x1+","+y1+","+x2+","+y2+","+divH+","+divV+","+super.toString()+"]";
	}
}
