package com.kreative.paint.geom.draw;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.kreative.paint.document.draw.ControlPoint;
import com.kreative.paint.document.draw.ControlPointType;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.geom.RegularPolygon;

public class RegularPolygonDrawObject extends ShapeDrawObject {
	private int sides;
	private int skips;
	private double x1, y1, x2, y2;
	private boolean fromCenter;
	
	public RegularPolygonDrawObject(
		PaintSettings ps,
		int sides,
		int skips,
		double x1, double y1,
		double x2, double y2,
		boolean fromCenter
	) {
		super(ps);
		this.sides = sides;
		this.skips = skips;
		this.x1 = x1; this.y1 = y1;
		this.x2 = x2; this.y2 = y2;
		this.fromCenter = fromCenter;
	}
	
	private RegularPolygonDrawObject(RegularPolygonDrawObject o) {
		super(o);
		this.sides = o.sides;
		this.skips = o.skips;
		this.x1 = o.x1; this.y1 = o.y1;
		this.x2 = o.x2; this.y2 = o.y2;
		this.fromCenter = o.fromCenter;
	}
	
	@Override
	public RegularPolygonDrawObject clone() {
		return new RegularPolygonDrawObject(this);
	}
	
	@Override
	public RegularPolygon getShape() {
		return new RegularPolygon(x1, y1, x2, y2, sides, skips, fromCenter);
	}
	
	@Override
	protected Object getControlState() {
		return new double[]{ x1, y1, x2, y2 };
	}
	
	@Override
	protected void setControlState(Object o) {
		double[] state = (double[])o;
		this.x1 = state[0]; this.y1 = state[1];
		this.x2 = state[2]; this.y2 = state[3];
	}
	
	@Override
	public int getControlPointCount() {
		return 2;
	}
	
	@Override
	protected ControlPoint getControlPointImpl(int i) {
		switch (i) {
			case 0: return new ControlPoint(
				fromCenter ?
				ControlPointType.CENTER :
				ControlPointType.ENDPOINT,
				x1, y1
			);
			case 1: return new ControlPoint(
				fromCenter ?
				ControlPointType.RADIUS :
				ControlPointType.ENDPOINT,
				x2, y2
			);
			default: return null;
		}
	}
	
	@Override
	protected List<ControlPoint> getControlPointsImpl() {
		List<ControlPoint> cpts = new ArrayList<ControlPoint>();
		cpts.add(new ControlPoint(
			fromCenter ?
			ControlPointType.CENTER :
			ControlPointType.ENDPOINT,
			x1, y1
		));
		cpts.add(new ControlPoint(
			fromCenter ?
			ControlPointType.RADIUS :
			ControlPointType.ENDPOINT,
			x2, y2
		));
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
				if (fromCenter) {
					this.x2 = x + (this.x2 - this.x1);
					this.y2 = y + (this.y2 - this.y1);
				}
				this.x1 = x;
				this.y1 = y;
				break;
			case 1:
				this.x2 = x;
				this.y2 = y;
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
}
