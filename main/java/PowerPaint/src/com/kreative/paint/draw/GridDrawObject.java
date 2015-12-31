package com.kreative.paint.draw;

import java.awt.Graphics2D;
import java.awt.Shape;
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

public class GridDrawObject extends DrawObject {
	public static final int NONE = 0;
	public static final int LINEAR = 1;
	public static final int LOGARITHMIC = 2;
	
	private double x1, y1, x2, y2;
	private int horizGridType;
	private double horizGridSpacing;
	private int vertGridType;
	private double vertGridSpacing;
	
	public GridDrawObject(
		PaintSettings ps,
		double x, double y,
		double w, double h,
		int hgt, double hgs,
		int vgt, double vgs
	) {
		super(ps);
		this.x1 = x;
		this.y1 = y;
		this.x2 = x + w;
		this.y2 = y + h;
		this.horizGridType = hgt;
		this.horizGridSpacing = hgs;
		this.vertGridType = vgt;
		this.vertGridSpacing = vgs;
	}
	
	private GridDrawObject(GridDrawObject o) {
		super(o);
		this.x1 = o.x1;
		this.y1 = o.y1;
		this.x2 = o.x2;
		this.y2 = o.y2;
		this.horizGridType = o.horizGridType;
		this.horizGridSpacing = o.horizGridSpacing;
		this.vertGridType = o.vertGridType;
		this.vertGridSpacing = o.vertGridSpacing;
	}
	
	@Override
	public GridDrawObject clone() {
		return new GridDrawObject(this);
	}
	
	public Rectangle2D getGridBounds() {
		return new Rectangle2D.Double(
			Math.min(x1, x2), Math.min(y1, y2),
			Math.abs(x2 - x1), Math.abs(y2 - y1)
		);
	}
	
	public int getHorizGridType() { return horizGridType; }
	public double getHorizGridSpacing() { return horizGridSpacing; }
	public int getVertGridType() { return vertGridType; }
	public double getVertGridSpacing() { return vertGridSpacing; }
	
	@Override protected Shape getBoundaryImpl() { return getGridBounds(); }
	@Override protected Shape getHitAreaImpl() { return getGridBounds(); }
	
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
		if (ps.isFilled()) {
			ps.applyFill(g);
			g.fill(getGridBounds());
		}
		if (ps.isDrawn()) {
			ps.applyDraw(g);
			paintGrid(
				g,
				Math.min(x1, x2), Math.min(y1, y2),
				Math.max(x1, x2), Math.max(y1, y2)
			);
		}
	}
	
	private void paintGrid(Graphics2D g, double x1, double y1, double x2, double y2) {
		switch (horizGridType) {
			case LINEAR:
				for (double y = y1; y <= y2; y += horizGridSpacing) {
					g.draw(new Line2D.Double(x1, y, x2 - 1, y));
				}
				break;
			case LOGARITHMIC:
				double m = (1 - 1 / horizGridSpacing);
				for (double h = (y2 - y1) * m; h >= 1; h *= m) {
					g.draw(new Line2D.Double(x1, y1 + h - 1, x2 - 1, y1 + h - 1));
				}
				break;
		}
		switch (vertGridType) {
			case LINEAR:
				for (double x = x1; x <= x2; x += vertGridSpacing) {
					g.draw(new Line2D.Double(x, y1, x, y2 - 1));
				}
				break;
			case LOGARITHMIC:
				double m = (1 - 1 / vertGridSpacing);
				for (double w = (x2 - x1) * m; w >= 1; w *= m) {
					g.draw(new Line2D.Double(x1 + w - 1, y1, x1 + w - 1, y2 - 1));
				}
				break;
		}
	}
}
