package com.kreative.paint.draw;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.kreative.paint.document.draw.ControlPoint;
import com.kreative.paint.document.draw.ControlPointType;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.PaintSettings;

public class CropMarkDrawObject extends DrawObject {
	private double x1, y1, x2, y2;
	private int divH, divV;
	
	public CropMarkDrawObject(PaintSettings ps, double x1, double y1, double x2, double y2, int divH, int divV) {
		super(ps);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.divH = divH;
		this.divV = divV;
	}
	
	private CropMarkDrawObject(CropMarkDrawObject o) {
		super(o);
		this.x1 = o.x1;
		this.y1 = o.y1;
		this.x2 = o.x2;
		this.y2 = o.y2;
		this.divH = o.divH;
		this.divV = o.divV;
	}
	
	@Override
	public CropMarkDrawObject clone() {
		return new CropMarkDrawObject(this);
	}
	
	public double getX1() { return x1; }
	public double getY1() { return y1; }
	public double getX2() { return x2; }
	public double getY2() { return y2; }
	public int getHorizDivisions() { return divH; }
	public int getVertDivisions() { return divV; }
	
	@Override
	protected Shape getBoundaryImpl() {
		return new Rectangle2D.Double(
			Math.min(x1, x2), Math.min(y1, y2),
			Math.abs(x2 - x1), Math.abs(y2 - y1)
		);
	}
	
	@Override
	protected Shape getHitAreaImpl() {
		Area a = new Area();
		if (ps.isFilled()) {
			a.add(new Area(makeFillShape()));
		}
		if (ps.isDrawn()) {
			try { a.add(new Area(ps.drawStroke.createStrokedShape(makeMarkShape()))); }
			catch (Exception e) {}
		}
		return a;
	}
	
	@Override
	protected Object getControlState() {
		return new double[]{ x1, y1, x2, y2 };
	}
	
	@Override
	protected void setControlState(Object o) {
		double[] state = (double[])o;
		x1 = state[0]; y1 = state[1];
		x2 = state[2]; y2 = state[3];
	}
	
	@Override
	public int getControlPointCount() {
		return 9;
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
	protected void paintImpl(Graphics2D g) {
		// See preTxPaintImpl().
	}
	
	@Override
	protected void preTxPaintImpl(Graphics2D g) {
		if (ps.isFilled()) {
			Shape s = makeFillShape();
			if (tx != null) {
				try { s = tx.createTransformedShape(s); }
				catch (Exception e) { s = makeFillShape(); }
			}
			ps.applyFill(g);
			g.fill(s);
		}
		if (ps.isDrawn()) {
			Shape s = makeMarkShape();
			if (tx != null) {
				try { s = tx.createTransformedShape(s); }
				catch (Exception e) { s = makeMarkShape(); }
			}
			ps.applyDraw(g);
			g.draw(s);
		}
	}
	
	private Shape makeFillShape() {
		double x = Math.min(x1, x2);
		double y = Math.min(y1, y2);
		double w = Math.abs(x2 - x1);
		double h = Math.abs(y2 - y1);
		return new Rectangle2D.Double(x - 16, y - 16, w + 32, h + 32);
	}
	
	private Shape makeMarkShape() {
		double x3 = Math.min(x1, x2);
		double y3 = Math.min(y1, y2);
		double x4 = Math.max(x1, x2);
		double y4 = Math.max(y1, y2);
		double w  = Math.abs(x2 - x1);
		double h  = Math.abs(y2 - y1);
		GeneralPath p = new GeneralPath();
		for (int i = 0; i <= divH; i++) {
			double x = x3 + w * (double)i / (double)divH;
			p.moveTo(x, y3 - 40);
			p.lineTo(x, y3 -  8);
			p.moveTo(x, y4 +  8);
			p.lineTo(x, y4 + 40);
		}
		for (int i = 0; i <= divV; i++) {
			double y = y3 + h * (double)i / (double)divV;
			p.moveTo(x3 - 40, y);
			p.lineTo(x3 -  8, y);
			p.moveTo(x4 +  8, y);
			p.lineTo(x4 + 40, y);
		}
		return p;
	}
}
