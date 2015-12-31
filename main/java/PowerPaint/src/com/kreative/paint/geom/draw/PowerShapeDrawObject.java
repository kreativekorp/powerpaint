package com.kreative.paint.geom.draw;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import com.kreative.paint.document.draw.ControlPoint;
import com.kreative.paint.document.draw.ControlPointType;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.ShapeDrawObject.RectangularShape;
import com.kreative.paint.material.shape.Parameter;
import com.kreative.paint.material.shape.PowerShape;

public class PowerShapeDrawObject extends RectangularShape {
	private PowerShape shape;
	
	public PowerShapeDrawObject(
		PaintSettings ps,
		PowerShape shape,
		double x, double y,
		double width, double height
	) {
		super(ps, x, y, x + width, y + height);
		this.shape = shape.clone();
	}
	
	private PowerShapeDrawObject(PowerShapeDrawObject o) {
		super(o);
		this.shape = o.shape.clone();
	}
	
	@Override
	public PowerShapeDrawObject clone() {
		return new PowerShapeDrawObject(this);
	}
	
	public PowerShape getPowerShape() { return shape; }
	public double getX() { return x1; }
	public double getY() { return y1; }
	public double getWidth() { return x2 - x1; }
	public double getHeight() { return y2 - y1; }
	
	@Override
	public Shape getShape() {
		AffineTransform t = AffineTransform.getTranslateInstance(x1, y1);
		AffineTransform s = AffineTransform.getScaleInstance(x2 - x1, y2 - y1);
		return t.createTransformedShape(s.createTransformedShape(shape));
	}
	
	@Override
	protected Object getControlState() {
		return new Object[]{
			super.getControlState(),
			shape.clone()
		};
	}
	
	@Override
	protected void setControlState(Object o) {
		Object[] state = (Object[])o;
		super.setControlState(state[0]);
		shape = ((PowerShape)state[1]).clone();
	}
	
	@Override
	public int getControlPointCount() {
		int n = super.getControlPointCount();
		return n + shape.getParameterNames().size();
	}
	
	@Override
	protected ControlPoint getControlPointImpl(int i) {
		int n = super.getControlPointCount();
		if (i < n) return super.getControlPointImpl(i);
		Point2D p = shape.getParameterValue(shape.getParameterNames().get(i - n));
		return new ControlPoint(
			ControlPointType.PULL_TAB,
			x1 + (x2 - x1) * p.getX(),
			y1 + (y2 - y1) * p.getY()
		);
	}
	
	@Override
	protected List<ControlPoint> getControlPointsImpl() {
		List<ControlPoint> cpts = super.getControlPointsImpl();
		for (String pn : shape.getParameterNames()) {
			Point2D p = shape.getParameterValue(pn);
			cpts.add(new ControlPoint(
				ControlPointType.PULL_TAB,
				x1 + (x2 - x1) * p.getX(),
				y1 + (y2 - y1) * p.getY()
			));
		}
		return cpts;
	}
	
	@Override
	protected Collection<Line2D> getControlLinesImpl() {
		Collection<Line2D> lines = new HashSet<Line2D>();
		for (String pn : shape.getParameterNames()) {
			Parameter p = shape.getParameter(pn);
			if (p.polar) {
				if (p.minR != p.maxR) {
					lines.add(new Line2D.Double(
						x1 + (x2 - x1) * (p.originX + p.minR * Math.cos(p.minA)),
						y1 + (y2 - y1) * (p.originY + p.minR * Math.sin(p.minA)),
						x1 + (x2 - x1) * (p.originX + p.maxR * Math.cos(p.minA)),
						y1 + (y2 - y1) * (p.originY + p.maxR * Math.sin(p.minA))
					));
					if (p.minA != p.maxA) {
						lines.add(new Line2D.Double(
							x1 + (x2 - x1) * (p.originX + p.minR * Math.cos(p.maxA)),
							y1 + (y2 - y1) * (p.originY + p.minR * Math.sin(p.maxA)),
							x1 + (x2 - x1) * (p.originX + p.maxR * Math.cos(p.maxA)),
							y1 + (y2 - y1) * (p.originY + p.maxR * Math.sin(p.maxA))
						));
					}
				}
			} else {
				if (p.minX == p.maxX || p.minY == p.maxY) {
					lines.add(new Line2D.Double(
						x1 + (x2 - x1) * p.minX,
						y1 + (y2 - y1) * p.minY,
						x1 + (x2 - x1) * p.maxX,
						y1 + (y2 - y1) * p.maxY
					));
				} else {
					lines.add(new Line2D.Double(
						x1 + (x2 - x1) * p.minX,
						y1 + (y2 - y1) * p.minY,
						x1 + (x2 - x1) * p.maxX,
						y1 + (y2 - y1) * p.minY
					));
					lines.add(new Line2D.Double(
						x1 + (x2 - x1) * p.minX,
						y1 + (y2 - y1) * p.maxY,
						x1 + (x2 - x1) * p.maxX,
						y1 + (y2 - y1) * p.maxY
					));
					lines.add(new Line2D.Double(
						x1 + (x2 - x1) * p.minX,
						y1 + (y2 - y1) * p.minY,
						x1 + (x2 - x1) * p.minX,
						y1 + (y2 - y1) * p.maxY
					));
					lines.add(new Line2D.Double(
						x1 + (x2 - x1) * p.maxX,
						y1 + (y2 - y1) * p.minY,
						x1 + (x2 - x1) * p.maxX,
						y1 + (y2 - y1) * p.maxY
					));
				}
			}
		}
		return lines;
	}
	
	@Override
	protected int setControlPointImpl(int i, double x, double y) {
		int n = super.getControlPointCount();
		if (i < n) return super.setControlPointImpl(i, x, y);
		shape.setParameterValue(
			shape.getParameterNames().get(i - n),
			(x - x1) / (x2 - x1),
			(y - y1) / (y2 - y1)
		);
		return i;
	}
}
