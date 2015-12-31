package com.kreative.paint.draw;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.kreative.paint.document.draw.ControlPoint;
import com.kreative.paint.document.draw.ControlPointType;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.PaintSettings;

public class ThreeDBoxDrawObject extends DrawObject {
	private double x1, y1, x2, y2, dx, dy;
	
	public ThreeDBoxDrawObject(PaintSettings ps, double x, double y, double w, double h, double dx, double dy) {
		super(ps);
		this.x1 = x;
		this.y1 = y;
		this.x2 = x + w;
		this.y2 = y + h;
		this.dx = dx;
		this.dy = dy;
	}
	
	private ThreeDBoxDrawObject(ThreeDBoxDrawObject o) {
		super(o);
		this.x1 = o.x1;
		this.y1 = o.y1;
		this.x2 = o.x2;
		this.y2 = o.y2;
		this.dx = o.dx;
		this.dy = o.dy;
	}
	
	@Override
	public ThreeDBoxDrawObject clone() {
		return new ThreeDBoxDrawObject(this);
	}
	
	public double getX() { return x1; }
	public double getY() { return y1; }
	public double getWidth() { return x2 - x1; }
	public double getHeight() { return y2 - y1; }
	public double getDX() { return dx; }
	public double getDY() { return dy; }
	
	@Override
	protected Shape getBoundaryImpl() {
		Shape[] ss = getShapes(x1, y1, x2 - x1, y2 - y1, dx, dy);
		Area a = new Area();
		for (Shape s : ss) a.add(new Area(s));
		return a;
	}
	
	@Override
	protected Shape getPostTxHitAreaImpl(AffineTransform tx) {
		Area a = new Area();
		Shape[] ss = getShapes(x1, y1, x2 - x1, y2 - y1, dx, dy);
		for (Shape s : ss) {
			if (tx != null) {
				try { s = tx.createTransformedShape(s); }
				catch (Exception e) {}
			}
			if (ps.isFilled()) {
				a.add(new Area(s));
			}
			if (ps.isDrawn()) {
				try { a.add(new Area(ps.drawStroke.createStrokedShape(s))); }
				catch (Exception e) {}
			}
		}
		return a;
	}
	
	@Override
	protected Object getControlState() {
		return new double[]{ x1, y1, x2, y2, dx, dy };
	}
	
	@Override
	protected void setControlState(Object o) {
		double[] state = (double[])o;
		x1 = state[0]; y1 = state[1];
		x2 = state[2]; y2 = state[3];
		dx = state[4]; dy = state[5];
	}
	
	@Override
	public int getControlPointCount() {
		return 10;
	}
	
	@Override
	protected ControlPoint getControlPointImpl(int i) {
		switch (i) {
			case 0: return new ControlPoint(ControlPointType.CENTER, (x1 + x2) / 2, (y1 + y2) / 2);
			case 1: return new ControlPoint(ControlPointType.NORTHWEST, x1, y1);
			case 2: return new ControlPoint(ControlPointType.NORTHEAST, x2, y1);
			case 3: return new ControlPoint(ControlPointType.SOUTHWEST, x1, y2);
			case 4: return new ControlPoint(ControlPointType.SOUTHEAST, x2, y2);
			case 5: return new ControlPoint(ControlPointType.NORTH, (x1 + x2) / 2, y1);
			case 6: return new ControlPoint(ControlPointType.SOUTH, (x1 + x2) / 2, y2);
			case 7: return new ControlPoint(ControlPointType.WEST, x1, (y1 + y2) / 2);
			case 8: return new ControlPoint(ControlPointType.EAST, x2, (y1 + y2) / 2);
			case 9: return new ControlPoint(ControlPointType.PULL_TAB, (x1 + x2) / 2 + dx, (y1 + y2) / 2 + dy);
			default: return null;
		}
	}
	
	@Override
	protected List<ControlPoint> getControlPointsImpl() {
		List<ControlPoint> cpts = new ArrayList<ControlPoint>();
		cpts.add(new ControlPoint(ControlPointType.CENTER, (x1 + x2) / 2, (y1 + y2) / 2));
		cpts.add(new ControlPoint(ControlPointType.NORTHWEST, x1, y1));
		cpts.add(new ControlPoint(ControlPointType.NORTHEAST, x2, y1));
		cpts.add(new ControlPoint(ControlPointType.SOUTHWEST, x1, y2));
		cpts.add(new ControlPoint(ControlPointType.SOUTHEAST, x2, y2));
		cpts.add(new ControlPoint(ControlPointType.NORTH, (x1 + x2) / 2, y1));
		cpts.add(new ControlPoint(ControlPointType.SOUTH, (x1 + x2) / 2, y2));
		cpts.add(new ControlPoint(ControlPointType.WEST, x1, (y1 + y2) / 2));
		cpts.add(new ControlPoint(ControlPointType.EAST, x2, (y1 + y2) / 2));
		cpts.add(new ControlPoint(ControlPointType.PULL_TAB, (x1 + x2) / 2 + dx, (y1 + y2) / 2 + dy));
		return cpts;
	}
	
	@Override
	protected Collection<Line2D> getControlLinesImpl() {
		return null;
	}
	
	@Override
	protected int setControlPointImpl(int i, double x, double y) {
		switch (i) {
			case 0:
				double width2 = (x2 - x1) / 2;
				double height2 = (y2 - y1) / 2;
				x1 = x - width2;
				y1 = y - height2;
				x2 = x + width2;
				y2 = y + height2;
				break;
			case 1: x1 = x; y1 = y; break;
			case 2: x2 = x; y1 = y; break;
			case 3: x1 = x; y2 = y; break;
			case 4: x2 = x; y2 = y; break;
			case 5: y1 = y; break;
			case 6: y2 = y; break;
			case 7: x1 = x; break;
			case 8: x2 = x; break;
			case 9:
				dx = x - (x1 + x2) / 2;
				dy = y - (y1 + y2) / 2;
				break;
		}
		return i;
	}
	
	@Override
	protected Point2D getLocationImpl() {
		return new Point2D.Double(x1, y1);
	}
	
	@Override
	protected void setLocationImpl(double x, double y) {
		this.x2 = x + (this.x2 - this.x1);
		this.y2 = y + (this.y2 - this.y1);
		this.x1 = x;
		this.y1 = y;
	}
	
	@Override
	protected void preTxPaintImpl(Graphics2D g, AffineTransform tx) {
		Shape[] ss = getShapes(x1, y1, x2 - x1, y2 - y1, dx, dy);
		for (Shape s : ss) {
			if (tx != null) {
				try { s = tx.createTransformedShape(s); }
				catch (Exception e) {}
			}
			if (ps.isFilled()) {
				ps.applyFill(g);
				g.fill(s);
			}
			if (ps.isDrawn()) {
				ps.applyDraw(g);
				g.draw(s);
			}
		}
	}
	
	private static Shape[] getShapes(double x, double y, double w, double h, double dx, double dy) {
		double hypot = Math.hypot(dy, dx);
		double angle = Math.atan2(dy, dx);
		double x2 = x + hypot * (3 * Math.sqrt(2) * Math.cos(angle) - 1) / 4;
		double y2 = y + hypot * (3 * Math.sqrt(2) * Math.sin(angle) - 1) / 4;
		double w2 = w + hypot / 2;
		double h2 = h + hypot / 2;
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
	
	private static GeneralPath makeBack(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo(x, y);
		p.lineTo(x + w, y);
		p.lineTo(x + w, y + h);
		p.lineTo(x, y + h);
		p.closePath();
		return p;
	}
	
	private static GeneralPath makeTop(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo(x2, y2);
		p.lineTo(x2 + w2, y2);
		p.lineTo(x + w, y);
		p.lineTo(x, y);
		p.closePath();
		return p;
	}
	
	private static GeneralPath makeBottom(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo(x, y + h);
		p.lineTo(x + w, y + h);
		p.lineTo(x2 + w2, y2 + h2);
		p.lineTo(x2, y2 + h2);
		p.closePath();
		return p;
	}
	
	private static GeneralPath makeLeft(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo(x2, y2);
		p.lineTo(x, y);
		p.lineTo(x, y + h);
		p.lineTo(x2, y2 + h2);
		p.closePath();
		return p;
	}
	
	private static GeneralPath makeRight(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo(x + w, y);
		p.lineTo(x2 + w2, y2);
		p.lineTo(x2 + w2, y2 + h2);
		p.lineTo(x + w, y + h);
		p.closePath();
		return p;
	}
	
	private static GeneralPath makeFront(double x, double y, double w, double h, double x2, double y2, double w2, double h2) {
		GeneralPath p = new GeneralPath();
		p.moveTo(x2, y2);
		p.lineTo(x2 + w2, y2);
		p.lineTo(x2 + w2, y2 + h2);
		p.lineTo(x2, y2 + h2);
		p.closePath();
		return p;
	}
}
