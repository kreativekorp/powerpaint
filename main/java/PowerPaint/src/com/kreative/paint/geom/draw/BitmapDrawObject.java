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
import com.kreative.paint.geom.BitmapShape;

public class BitmapDrawObject extends ShapeDrawObject {
	private int[] bitmap;
	private int x, y, w, h;
	
	public BitmapDrawObject(PaintSettings ps, int[] bitmap, int x, int y, int w, int h) {
		super(ps);
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	private BitmapDrawObject(BitmapDrawObject o) {
		super(o);
		this.bitmap = o.bitmap;
		this.x = o.x;
		this.y = o.y;
		this.w = o.w;
		this.h = o.h;
	}
	
	@Override
	public BitmapDrawObject clone() {
		return new BitmapDrawObject(this);
	}
	
	@Override
	public BitmapShape getShape() {
		return new BitmapShape(bitmap, x, y, w, h);
	}
	
	@Override
	protected Object getControlState() {
		return new int[]{ x, y };
	}
	
	@Override
	protected void setControlState(Object o) {
		int[] state = (int[])o;
		x = state[0];
		y = state[1];
	}
	
	@Override
	public int getControlPointCount() {
		return 1;
	}
	
	@Override
	protected ControlPoint getControlPointImpl(int i) {
		return new ControlPoint(ControlPointType.BASELINE, x, y);
	}
	
	@Override
	protected List<ControlPoint> getControlPointsImpl() {
		List<ControlPoint> cpts = new ArrayList<ControlPoint>();
		cpts.add(new ControlPoint(ControlPointType.BASELINE, x, y));
		return cpts;
	}
	
	@Override
	protected Collection<Line2D> getControlLinesImpl() {
		return null;
	}
	
	@Override
	protected int setControlPointImpl(int i, double x, double y) {
		this.x = (int)Math.round(x);
		this.y = (int)Math.round(y);
		return i;
	}
	
	@Override
	protected Point2D getLocationImpl() {
		return new Point2D.Double(x, y);
	}
	
	@Override
	protected void setLocationImpl(double x, double y) {
		this.x = (int)Math.round(x);
		this.y = (int)Math.round(y);
	}
}
