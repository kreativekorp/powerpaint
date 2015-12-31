package com.kreative.paint.document.draw;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupDrawObject extends DrawObject {
	private List<DrawObject> objects;
	private Rectangle2D boundary;
	private Area hitArea;
	private double x1, y1, x2, y2;
	
	public GroupDrawObject(Collection<DrawObject> objects) {
		super((PaintSettings)null);
		this.objects = new ArrayList<DrawObject>();
		Area ba = new Area();
		Area ha = new Area();
		for (DrawObject o : objects) {
			this.objects.add(o.clone());
			ba.add(new Area(o.getBounds2D()));
			ha.add(new Area(o.getHitArea()));
		}
		this.boundary = ba.getBounds2D();
		this.hitArea = ha;
		this.x1 = boundary.getMinX();
		this.y1 = boundary.getMinY();
		this.x2 = boundary.getMaxX();
		this.y2 = boundary.getMaxY();
	}
	
	public GroupDrawObject(Collection<DrawObject> objects, double x, double y, double width, double height) {
		super((PaintSettings)null);
		this.objects = new ArrayList<DrawObject>();
		Area ba = new Area();
		Area ha = new Area();
		for (DrawObject o : objects) {
			this.objects.add(o.clone());
			ba.add(new Area(o.getBounds2D()));
			ha.add(new Area(o.getHitArea()));
		}
		this.boundary = ba.getBounds2D();
		this.hitArea = ha;
		this.x1 = x;
		this.y1 = y;
		this.x2 = x + width;
		this.y2 = y + height;
	}
	
	private GroupDrawObject(GroupDrawObject original) {
		super(original);
		this.objects = new ArrayList<DrawObject>();
		Area ba = new Area();
		Area ha = new Area();
		for (DrawObject o : original.objects) {
			this.objects.add(o.clone());
			ba.add(new Area(o.getBounds2D()));
			ha.add(new Area(o.getHitArea()));
		}
		this.boundary = ba.getBounds2D();
		this.hitArea = ha;
		this.x1 = original.x1;
		this.y1 = original.y1;
		this.x2 = original.x2;
		this.y2 = original.y2;
	}
	
	@Override
	public GroupDrawObject clone() {
		return new GroupDrawObject(this);
	}
	
	public double getX() { return x1; }
	public double getY() { return y1; }
	public double getWidth() { return x2 - x1; }
	public double getHeight() { return y2 - y1; }
	
	private AffineTransform getGroupTransform() {
		AffineTransform tx = new AffineTransform();
		tx.translate(x1, y1);
		tx.scale((x2 - x1) / boundary.getWidth(), (y2 - y1) / boundary.getHeight());
		tx.translate(-boundary.getX(), -boundary.getY());
		return tx;
	}
	
	@Override
	protected Shape getBoundaryImpl() {
		return getGroupTransform().createTransformedShape(boundary);
	}
	
	@Override
	protected Shape getHitAreaImpl() {
		return getGroupTransform().createTransformedShape(hitArea);
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
	protected void preTxPaintImpl(Graphics2D g, AffineTransform tx) {
		AffineTransform tx2 = new AffineTransform();
		if (tx != null) tx2.concatenate(tx);
		tx2.concatenate(getGroupTransform());
		for (DrawObject o : objects) {
			if (o.visible) o.paint(g, tx2);
		}
	}
	
	public List<DrawObject> getObjects() {
		List<DrawObject> objects = new ArrayList<DrawObject>();
		for (DrawObject o : this.objects) {
			objects.add(o.clone());
		}
		return objects;
	}
	
	public List<DrawObject> ungroup() {
		List<DrawObject> ungrouped = new ArrayList<DrawObject>();
		for (DrawObject o : this.objects) {
			o = o.clone();
			AffineTransform tx = new AffineTransform();
			if (this.tx != null) tx.concatenate(this.tx);
			tx.concatenate(getGroupTransform());
			if (o.tx != null) tx.concatenate(o.tx);
			o.tx = tx;
			ungrouped.add(o);
		}
		return ungrouped;
	}
}
