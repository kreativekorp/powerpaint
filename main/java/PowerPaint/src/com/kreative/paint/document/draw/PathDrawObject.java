package com.kreative.paint.document.draw;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PathDrawObject extends ShapeDrawObject {
	private Path path;
	
	public PathDrawObject(PaintSettings ps) {
		super(ps);
		this.path = new Path();
	}
	
	public PathDrawObject(PaintSettings ps, Path path) {
		super(ps);
		this.path = path.clone();
	}
	
	public PathDrawObject(PaintSettings ps, Shape s) {
		super(ps);
		this.path = new Path();
		this.path.appendAWTShape(s);
	}
	
	private PathDrawObject(PathDrawObject o) {
		super(o);
		this.path = o.path.clone();
	}
	
	@Override
	public PathDrawObject clone() {
		return new PathDrawObject(this);
	}
	
	public Path getPath() {
		return path.clone();
	}
	
	@Override
	public GeneralPath getShape() {
		return path.toAWTShape();
	}
	
	@Override
	protected Object getControlState() {
		return path.clone();
	}
	
	@Override
	protected void setControlState(Object o) {
		this.path = ((Path)o).clone();
	}
	
	@Override
	public int getControlPointCount() {
		int count = 0;
		for (PathContour c : path) {
			count += c.size() * 3;
		}
		return count;
	}
	
	@Override
	protected ControlPoint getControlPointImpl(int i) {
		for (PathContour c : path) {
			int n3 = c.size() * 3;
			if (i < n3) {
				PathPoint p = c.get(i / 3);
				Point2D p2;
				switch (i % 3) {
					case 0:
						p2 = p.getPreviousCtrl();
						return new ControlPoint(
							p.isPreviousLinear() ?
							ControlPointType.HIDDEN :
							ControlPointType.CONTROL_POINT,
							p2.getX(), p2.getY()
						);
					case 1:
						return new ControlPoint(
							p.isAngleLocked() ?
							ControlPointType.CURVED_MIDPOINT :
							ControlPointType.STRAIGHT_MIDPOINT,
							p.getX(), p.getY()
						);
					case 2:
						p2 = p.getNextCtrl();
						return new ControlPoint(
							p.isNextLinear() ?
							ControlPointType.HIDDEN :
							ControlPointType.CONTROL_POINT,
							p2.getX(), p2.getY()
						);
				}
			} else {
				i -= n3;
			}
		}
		return null;
	}
	
	@Override
	protected List<ControlPoint> getControlPointsImpl() {
		List<ControlPoint> cpts = new ArrayList<ControlPoint>();
		for (PathContour c : path) {
			for (PathPoint p : c) {
				Point2D p2;
				p2 = p.getPreviousCtrl();
				cpts.add(new ControlPoint(
					p.isPreviousLinear() ?
					ControlPointType.HIDDEN :
					ControlPointType.CONTROL_POINT,
					p2.getX(), p2.getY()
				));
				cpts.add(new ControlPoint(
					ControlPointType.STRAIGHT_MIDPOINT,
					p.getX(), p.getY()
				));
				p2 = p.getNextCtrl();
				cpts.add(new ControlPoint(
					p.isNextLinear() ?
					ControlPointType.HIDDEN :
					ControlPointType.CONTROL_POINT,
					p2.getX(), p2.getY()
				));
			}
		}
		return cpts;
	}
	
	@Override
	protected Collection<Line2D> getControlLinesImpl() {
		Collection<Line2D> lines = new HashSet<Line2D>();
		for (PathContour c : path) {
			for (PathPoint p : c) {
				if (!p.isPreviousLinear()) {
					lines.add(new Line2D.Double(p.getLocation(), p.getPreviousCtrl()));
				}
				if (!p.isNextLinear()) {
					lines.add(new Line2D.Double(p.getLocation(), p.getNextCtrl()));
				}
			}
		}
		return lines;
	}
	
	@Override
	protected int setControlPointImpl(int i, double x, double y) {
		int ret = i;
		for (PathContour c : path) {
			int n3 = c.size() * 3;
			if (i < n3) {
				PathPoint p = c.get(i / 3);
				switch (i % 3) {
					case 0: p.setPreviousCtrl(x, y); break;
					case 1: p.setLocation(x, y); break;
					case 2: p.setNextCtrl(x, y); break;
				}
				return ret;
			} else {
				i -= n3;
			}
		}
		return ret;
	}
	
	@Override
	protected Point2D getLocationImpl() {
		for (PathContour c : path) {
			for (PathPoint p : c) {
				return p.getLocation();
			}
		}
		return new Point2D.Double(0, 0);
	}
	
	@Override
	protected void setLocationImpl(double x, double y) {
		Point2D p0 = getLocationImpl();
		double x0 = p0.getX();
		double y0 = p0.getY();
		for (PathContour c : path) {
			for (PathPoint p : c) {
				double px = p.getX();
				double py = p.getY();
				p.setLocation(x + (px - x0), y + (py - y0));
			}
		}
	}
}
