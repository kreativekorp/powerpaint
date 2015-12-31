package com.kreative.paint.document.draw;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import com.kreative.paint.document.undo.Atom;

public abstract class ShapeDrawObject extends DrawObject {
	public static ShapeDrawObject forShape(PaintSettings ps, Shape s) {
		if (s instanceof Line2D) {
			Line2D l = (Line2D)s;
			return new Line(ps, l.getX1(), l.getY1(), l.getX2(), l.getY2());
		} else if (s instanceof Rectangle2D) {
			Rectangle2D r = (Rectangle2D)s;
			return new Rectangle(ps, r.getX(), r.getY(), r.getWidth(), r.getHeight());
		} else if (s instanceof RoundRectangle2D) {
			RoundRectangle2D r = (RoundRectangle2D)s;
			return new RoundRectangle(ps, r.getX(), r.getY(), r.getWidth(), r.getHeight(), r.getArcWidth(), r.getArcHeight());
		} else if (s instanceof Ellipse2D) {
			Ellipse2D e = (Ellipse2D)s;
			return new Ellipse(ps, e.getX(), e.getY(), e.getWidth(), e.getHeight());
		} else if (s instanceof Arc2D) {
			Arc2D a = (Arc2D)s;
			return new Arc(ps, a.getX(), a.getY(), a.getWidth(), a.getHeight(), a.getAngleStart(), a.getAngleExtent(), ArcType.forAWTValue(a.getArcType()));
		} else if (s instanceof QuadCurve2D) {
			QuadCurve2D q = (QuadCurve2D)s;
			return new QuadCurve(ps, q.getX1(), q.getY1(), q.getCtrlX(), q.getCtrlY(), q.getX2(), q.getY2());
		} else if (s instanceof CubicCurve2D) {
			CubicCurve2D c = (CubicCurve2D)s;
			return new CubicCurve(ps, c.getX1(), c.getY1(), c.getCtrlX1(), c.getCtrlY1(), c.getCtrlX2(), c.getCtrlY2(), c.getX2(), c.getY2());
		} else if (s instanceof java.awt.Polygon) {
			java.awt.Polygon p = (java.awt.Polygon)s;
			return new Polygon(ps, p.xpoints, p.ypoints, p.npoints, true);
		} else {
			return new PathDrawObject(ps, s);
		}
	}
	
	protected ShadowSettings shadow;
	
	protected ShapeDrawObject(PaintSettings ps) {
		super(ps);
		this.shadow = null;
	}
	
	protected ShapeDrawObject(ShapeDrawObject original) {
		super(original);
		this.shadow = original.shadow;
	}
	
	public ShadowSettings getShadowSettings() { return shadow; }
	
	private static class ShadowSettingsAtom implements Atom {
		private ShapeDrawObject d;
		private ShadowSettings oldShadow;
		private ShadowSettings newShadow;
		public ShadowSettingsAtom(ShapeDrawObject d, ShadowSettings newShadow) {
			this.d = d;
			this.oldShadow = d.shadow;
			this.newShadow = newShadow;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof ShadowSettingsAtom)
			    && (((ShadowSettingsAtom)prev).d == this.d);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldShadow = ((ShadowSettingsAtom)prev).oldShadow;
			return this;
		}
		@Override
		public void undo() {
			d.shadow = oldShadow;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
		@Override
		public void redo() {
			d.shadow = newShadow;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
		}
	}
	
	public void setShadowSettings(ShadowSettings shadow) {
		if (equals(this.shadow, shadow)) return;
		if (history != null) history.add(new ShadowSettingsAtom(this, shadow));
		this.shadow = shadow;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED);
	}
	
	@Override public abstract ShapeDrawObject clone();
	public abstract Shape getShape();
	
	@Override
	protected Shape getBoundaryImpl() {
		return getShape();
	}
	
	@Override
	protected Shape getHitAreaImpl() {
		Shape s = getShape();
		Area a = new Area();
		if (ps.isFilled()) {
			a.add(new Area(s));
		}
		if (ps.isDrawn()) {
			try { a.add(new Area(ps.drawStroke.createStrokedShape(s))); }
			catch (Exception e) {}
		}
		return a;
	}
	
	@Override
	protected void preTxPaintImpl(Graphics2D g, AffineTransform tx) {
		Shape s = getShape();
		if (tx != null) {
			try { s = tx.createTransformedShape(s); }
			catch (Exception e) { s = getShape(); }
		}
		if (shadow != null && shadow.isShadowed()) {
			AffineTransform t = g.getTransform();
			shadow.apply(g, ps);
			Area a = new Area();
			if (ps.isFilled()) {
				a.add(new Area(s));
			}
			if (ps.isDrawn()) {
				try { a.add(new Area(ps.drawStroke.createStrokedShape(s))); }
				catch (Exception e) {}
			}
			g.fill(a);
			g.setTransform(t);
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
	
	public static class Line extends ShapeDrawObject {
		private double x1, y1, x2, y2;
		public Line(PaintSettings ps, double x1, double y1, double x2, double y2) {
			super(ps);
			this.x1 = x1; this.y1 = y1;
			this.x2 = x2; this.y2 = y2;
		}
		private Line(Line o) {
			super(o);
			this.x1 = o.x1; this.y1 = o.y1;
			this.x2 = o.x2; this.y2 = o.y2;
		}
		@Override
		public Line clone() {
			return new Line(this);
		}
		@Override
		public Line2D getShape() {
			return new Line2D.Double(x1, y1, x2, y2);
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
			return 2;
		}
		@Override
		protected ControlPoint getControlPointImpl(int i) {
			switch (i) {
				case 0: return new ControlPoint(ControlPointType.ENDPOINT, x1, y1);
				case 1: return new ControlPoint(ControlPointType.ENDPOINT, x2, y2);
				default: return null;
			}
		}
		@Override
		protected List<ControlPoint> getControlPointsImpl() {
			List<ControlPoint> cpts = new ArrayList<ControlPoint>();
			cpts.add(new ControlPoint(ControlPointType.ENDPOINT, x1, y1));
			cpts.add(new ControlPoint(ControlPointType.ENDPOINT, x2, y2));
			return cpts;
		}
		@Override
		protected Collection<Line2D> getControlLinesImpl() {
			return null;
		}
		@Override
		protected int setControlPointImpl(int i, double x, double y) {
			switch (i) {
				case 0: this.x1 = x; this.y1 = y; break;
				case 1: this.x2 = x; this.y2 = y; break;
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
	
	public static abstract class RectangularShape extends ShapeDrawObject {
		protected double x1, y1, x2, y2;
		protected RectangularShape(PaintSettings ps, double x1, double y1, double x2, double y2) {
			super(ps);
			this.x1 = x1; this.y1 = y1;
			this.x2 = x2; this.y2 = y2;
		}
		protected RectangularShape(RectangularShape o) {
			super(o);
			this.x1 = o.x1; this.y1 = o.y1;
			this.x2 = o.x2; this.y2 = o.y2;
		}
		@Override public abstract RectangularShape clone();
		@Override public abstract Shape getShape();
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
	}
	
	public static class Rectangle extends RectangularShape {
		public Rectangle(PaintSettings ps, double x, double y, double width, double height) {
			super(ps, x, y, x + width, y + height);
		}
		private Rectangle(Rectangle o) {
			super(o);
		}
		@Override
		public Rectangle clone() {
			return new Rectangle(this);
		}
		@Override
		public Rectangle2D getShape() {
			return new Rectangle2D.Double(
				Math.min(x1, x2), Math.min(y1, y2),
				Math.abs(x2 - x1), Math.abs(y2 - y1)
			);
		}
	}
	
	public static class RoundRectangle extends RectangularShape {
		private double rx, ry;
		public RoundRectangle(PaintSettings ps, double x, double y, double width, double height, double rx, double ry) {
			super(ps, x, y, x + width, y + height);
			this.rx = rx;
			this.ry = ry;
		}
		private RoundRectangle(RoundRectangle o) {
			super(o);
			this.rx = o.rx;
			this.ry = o.ry;
		}
		@Override
		public RoundRectangle clone() {
			return new RoundRectangle(this);
		}
		@Override
		public RoundRectangle2D getShape() {
			return new RoundRectangle2D.Double(
				Math.min(x1, x2), Math.min(y1, y2),
				Math.abs(x2 - x1), Math.abs(y2 - y1),
				rx, ry
			);
		}
	}
	
	public static class Ellipse extends RectangularShape {
		public Ellipse(PaintSettings ps, double x, double y, double width, double height) {
			super(ps, x, y, x + width, y + height);
		}
		private Ellipse(Ellipse o) {
			super(o);
		}
		@Override
		public Ellipse clone() {
			return new Ellipse(this);
		}
		@Override
		public Ellipse2D getShape() {
			return new Ellipse2D.Double(
				Math.min(x1, x2), Math.min(y1, y2),
				Math.abs(x2 - x1), Math.abs(y2 - y1)
			);
		}
	}
	
	public static class Arc extends RectangularShape {
		private double arcStart;
		private double arcExtent;
		private ArcType arcType;
		public Arc(
			PaintSettings ps,
			double x, double y,
			double width, double height,
			double arcStart,
			double arcExtent,
			ArcType arcType
		) {
			super(ps, x, y, x + width, y + height);
			this.arcStart = arcStart;
			this.arcExtent = arcExtent;
			this.arcType = arcType;
		}
		private Arc(Arc o) {
			super(o);
			this.arcStart = o.arcStart;
			this.arcExtent = o.arcExtent;
			this.arcType = o.arcType;
		}
		@Override
		public Arc clone() {
			return new Arc(this);
		}
		@Override
		public Arc2D getShape() {
			return new Arc2D.Double(
				Math.min(x1, x2), Math.min(y1, y2),
				Math.abs(x2 - x1), Math.abs(y2 - y1),
				arcStart, arcExtent, arcType.awtValue
			);
		}
		@Override
		protected Object getControlState() {
			return new Object[]{
				super.getControlState(),
				Double.valueOf(arcStart),
				Double.valueOf(arcExtent),
				arcType
			};
		}
		@Override
		protected void setControlState(Object o) {
			Object[] state = (Object[])o;
			super.setControlState(state[0]);
			arcStart = ((Number)state[1]).doubleValue();
			arcExtent = ((Number)state[2]).doubleValue();
			arcType = (ArcType)state[3];
		}
		@Override
		public int getControlPointCount() {
			int n = super.getControlPointCount();
			return n + 2;
		}
		@Override
		protected ControlPoint getControlPointImpl(int i) {
			int n = super.getControlPointCount();
			if (i < n) return super.getControlPointImpl(i);
			switch (i - n) {
				case 0: return new ControlPoint(
					ControlPointType.RADIUS,
					((x1 + x2) / 2) + (Math.abs(x2 - x1) / 2) * Math.cos(Math.toRadians(arcStart)),
					((y1 + y2) / 2) + (Math.abs(y2 - y1) / 2) * Math.sin(Math.toRadians(arcStart))
				);
				case 1: return new ControlPoint(
					ControlPointType.RADIUS,
					((x1 + x2) / 2) + (Math.abs(x2 - x1) / 2) * Math.cos(Math.toRadians(arcStart + arcExtent)),
					((y1 + y2) / 2) + (Math.abs(y2 - y1) / 2) * Math.sin(Math.toRadians(arcStart + arcExtent))
				);
				default: return null;
			}
		}
		@Override
		protected List<ControlPoint> getControlPointsImpl() {
			List<ControlPoint> cpts = super.getControlPointsImpl();
			cpts.add(new ControlPoint(
				ControlPointType.RADIUS,
				((x1 + x2) / 2) + (Math.abs(x2 - x1) / 2) * Math.cos(Math.toRadians(arcStart)),
				((y1 + y2) / 2) + (Math.abs(y2 - y1) / 2) * Math.sin(Math.toRadians(arcStart))
			));
			cpts.add(new ControlPoint(
				ControlPointType.RADIUS,
				((x1 + x2) / 2) + (Math.abs(x2 - x1) / 2) * Math.cos(Math.toRadians(arcStart + arcExtent)),
				((y1 + y2) / 2) + (Math.abs(y2 - y1) / 2) * Math.sin(Math.toRadians(arcStart + arcExtent))
			));
			return cpts;
		}
		@Override
		protected int setControlPointImpl(int i, double x, double y) {
			int n = super.getControlPointCount();
			if (i < n) return super.setControlPointImpl(i, x, y);
			double a = Math.toDegrees(Math.atan2(y - (y1 + y2) / 2, x - (x1 + x2) / 2));
			if (Double.isNaN(a) || Double.isInfinite(a)) return i;
			boolean negative = (arcExtent < 0);
			switch (i - n) {
				case 0:
					arcExtent = arcStart + arcExtent - a;
					arcStart = a;
					break;
				case 1:
					arcExtent = a - arcStart;
					break;
			}
			if (negative) {
				while (arcExtent < -360) arcExtent += 360;
				while (arcExtent > 0) arcExtent -= 360;
			} else {
				while (arcExtent < 0) arcExtent += 360;
				while (arcExtent > 360) arcExtent -= 360;
			}
			return i;
		}
	}
	
	public static abstract class CircularShape extends ShapeDrawObject {
		protected double cx, cy, ax, ay;
		protected CircularShape(PaintSettings ps, double cx, double cy, double ax, double ay) {
			super(ps);
			this.cx = cx; this.cy = cy;
			this.ax = ax; this.ay = ay;
		}
		protected CircularShape(CircularShape o) {
			super(o);
			this.cx = o.cx; this.cy = o.cy;
			this.ax = o.ax; this.ay = o.ay;
		}
		@Override public abstract CircularShape clone();
		@Override public abstract Shape getShape();
		@Override
		protected Object getControlState() {
			return new double[]{ cx, cy, ax, ay };
		}
		@Override
		protected void setControlState(Object o) {
			double[] state = (double[])o;
			cx = state[0]; cy = state[1];
			ax = state[2]; ay = state[3];
		}
		@Override
		public int getControlPointCount() {
			return 2;
		}
		@Override
		protected ControlPoint getControlPointImpl(int i) {
			switch (i) {
				case 0: return new ControlPoint(ControlPointType.CENTER, cx, cy);
				case 1: return new ControlPoint(ControlPointType.RADIUS, ax, ay);
				default: return null;
			}
		}
		@Override
		protected List<ControlPoint> getControlPointsImpl() {
			List<ControlPoint> cpts = new ArrayList<ControlPoint>();
			cpts.add(new ControlPoint(ControlPointType.CENTER, cx, cy));
			cpts.add(new ControlPoint(ControlPointType.RADIUS, ax, ay));
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
					ax = x + (ax - cx);
					ay = y + (ay - cy);
					cx = x;
					cy = y;
					break;
				case 1:
					ax = x;
					ay = y;
					break;
			}
			return i;
		}
		@Override
		protected Point2D getLocationImpl() {
			return new Point2D.Double(cx, cy);
		}
		@Override
		protected void setLocationImpl(double x, double y) {
			ax = x + (ax - cx);
			ay = y + (ay - cy);
			cx = x;
			cy = y;
		}
	}
	
	public static class QuadCurve extends ShapeDrawObject {
		private double x1, y1, cx, cy, x2, y2;
		public QuadCurve(PaintSettings ps, double x1, double y1, double cx, double cy, double x2, double y2) {
			super(ps);
			this.x1 = x1; this.y1 = y1;
			this.cx = cx; this.cy = cy;
			this.x2 = x2; this.y2 = y2;
		}
		private QuadCurve(QuadCurve o) {
			super(o);
			this.x1 = o.x1; this.y1 = o.y1;
			this.cx = o.cx; this.cy = o.cy;
			this.x2 = o.x2; this.y2 = o.y2;
		}
		@Override
		public QuadCurve clone() {
			return new QuadCurve(this);
		}
		@Override
		public QuadCurve2D getShape() {
			return new QuadCurve2D.Double(x1, y1, cx, cy, x2, y2);
		}
		@Override
		protected Object getControlState() {
			return new double[]{ x1, y1, cx, cy, x2, y2 };
		}
		@Override
		protected void setControlState(Object o) {
			double[] state = (double[])o;
			x1 = state[0]; y1 = state[1];
			cx = state[2]; cy = state[3];
			x2 = state[4]; y2 = state[5];
		}
		@Override
		public int getControlPointCount() {
			return 3;
		}
		@Override
		protected ControlPoint getControlPointImpl(int i) {
			switch (i) {
				case 0: return new ControlPoint(ControlPointType.ENDPOINT, x1, y1);
				case 1: return new ControlPoint(ControlPointType.CONTROL_POINT, cx, cy);
				case 2: return new ControlPoint(ControlPointType.ENDPOINT, x2, y2);
				default: return null;
			}
		}
		@Override
		protected List<ControlPoint> getControlPointsImpl() {
			List<ControlPoint> cpts = new ArrayList<ControlPoint>();
			cpts.add(new ControlPoint(ControlPointType.ENDPOINT, x1, y1));
			cpts.add(new ControlPoint(ControlPointType.CONTROL_POINT, cx, cy));
			cpts.add(new ControlPoint(ControlPointType.ENDPOINT, x2, y2));
			return cpts;
		}
		@Override
		protected Collection<Line2D> getControlLinesImpl() {
			Collection<Line2D> lines = new HashSet<Line2D>();
			lines.add(new Line2D.Double(x1, y1, cx, cy));
			lines.add(new Line2D.Double(x2, y2, cx, cy));
			return lines;
		}
		@Override
		protected int setControlPointImpl(int i, double x, double y) {
			switch (i) {
				case 0: this.x1 = x; this.y1 = y; break;
				case 1: this.cx = x; this.cy = y; break;
				case 2: this.x2 = x; this.y2 = y; break;
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
			this.cx = x + (this.cx - this.x1);
			this.cy = y + (this.cy - this.y1);
			this.x1 = x;
			this.y1 = y;
		}
	}
	
	public static class CubicCurve extends ShapeDrawObject {
		private double x1, y1, cx1, cy1, cx2, cy2, x2, y2;
		public CubicCurve(PaintSettings ps, double x1, double y1, double cx1, double cy1, double cx2, double cy2, double x2, double y2) {
			super(ps);
			this.x1 = x1; this.y1 = y1;
			this.cx1 = cx1; this.cy1 = cy1;
			this.cx2 = cx2; this.cy2 = cy2;
			this.x2 = x2; this.y2 = y2;
		}
		private CubicCurve(CubicCurve o) {
			super(o);
			this.x1 = o.x1; this.y1 = o.y1;
			this.cx1 = o.cx1; this.cy1 = o.cy1;
			this.cx2 = o.cx2; this.cy2 = o.cy2;
			this.x2 = o.x2; this.y2 = o.y2;
		}
		@Override
		public CubicCurve clone() {
			return new CubicCurve(this);
		}
		@Override
		public CubicCurve2D getShape() {
			return new CubicCurve2D.Double(x1, y1, cx1, cy1, cx2, cy2, x2, y2);
		}
		@Override
		protected Object getControlState() {
			return new double[]{ x1, y1, cx1, cy1, cx2, cy2, x2, y2 };
		}
		@Override
		protected void setControlState(Object o) {
			double[] state = (double[])o;
			x1 = state[0]; y1 = state[1];
			cx1 = state[2]; cy1 = state[3];
			cx2 = state[4]; cy2 = state[5];
			x2 = state[6]; y2 = state[7];
		}
		@Override
		public int getControlPointCount() {
			return 4;
		}
		@Override
		protected ControlPoint getControlPointImpl(int i) {
			switch (i) {
				case 0: return new ControlPoint(ControlPointType.ENDPOINT, x1, y1);
				case 1: return new ControlPoint(ControlPointType.CONTROL_POINT, cx1, cy1);
				case 2: return new ControlPoint(ControlPointType.CONTROL_POINT, cx2, cy2);
				case 3: return new ControlPoint(ControlPointType.ENDPOINT, x2, y2);
				default: return null;
			}
		}
		@Override
		protected List<ControlPoint> getControlPointsImpl() {
			List<ControlPoint> cpts = new ArrayList<ControlPoint>();
			cpts.add(new ControlPoint(ControlPointType.ENDPOINT, x1, y1));
			cpts.add(new ControlPoint(ControlPointType.CONTROL_POINT, cx1, cy1));
			cpts.add(new ControlPoint(ControlPointType.CONTROL_POINT, cx2, cy2));
			cpts.add(new ControlPoint(ControlPointType.ENDPOINT, x2, y2));
			return cpts;
		}
		@Override
		protected Collection<Line2D> getControlLinesImpl() {
			Collection<Line2D> lines = new HashSet<Line2D>();
			lines.add(new Line2D.Double(x1, y1, cx1, cy1));
			lines.add(new Line2D.Double(x2, y2, cx2, cy2));
			return lines;
		}
		@Override
		protected int setControlPointImpl(int i, double x, double y) {
			switch (i) {
				case 0:
					this.cx1 = x + (this.cx1 - this.x1);
					this.cy1 = y + (this.cy1 - this.y1);
					this.x1 = x;
					this.y1 = y;
					break;
				case 1:
					this.cx1 = x;
					this.cy1 = y;
					break;
				case 2:
					this.cx2 = x;
					this.cy2 = y;
					break;
				case 3:
					this.cx2 = x + (this.cx2 - this.x2);
					this.cy2 = y + (this.cy2 - this.y2);
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
			this.cx2 = x + (this.cx2 - this.x1);
			this.cy2 = y + (this.cy2 - this.y1);
			this.cx1 = x + (this.cx1 - this.x1);
			this.cy1 = y + (this.cy1 - this.y1);
			this.x1 = x;
			this.y1 = y;
		}
	}
	
	public static class Polygon extends ShapeDrawObject {
		private double[] x;
		private double[] y;
		private int n;
		private boolean closed;
		public Polygon(PaintSettings ps, int[] x, int[] y, int n, boolean expClosed) {
			super(ps);
			boolean impClosed = ((n > 0) && (x[0] == x[n-1]) && (y[0] == y[n-1]));
			this.closed = impClosed || expClosed;
			this.n = impClosed ? (n - 1) : n;
			this.x = new double[this.n];
			this.y = new double[this.n];
			for (int i = 0; i < this.n; i++) {
				this.x[i] = x[i];
				this.y[i] = y[i];
			}
		}
		public Polygon(PaintSettings ps, double[] x, double[] y, int n, boolean expClosed) {
			super(ps);
			boolean impClosed = ((n > 0) && (x[0] == x[n-1]) && (y[0] == y[n-1]));
			this.closed = impClosed || expClosed;
			this.n = impClosed ? (n - 1) : n;
			this.x = new double[this.n];
			this.y = new double[this.n];
			for (int i = 0; i < this.n; i++) {
				this.x[i] = x[i];
				this.y[i] = y[i];
			}
		}
		private Polygon(Polygon o) {
			super(o);
			this.closed = o.closed;
			this.n = o.n;
			this.x = new double[this.n];
			this.y = new double[this.n];
			for (int i = 0; i < this.n; i++) {
				this.x[i] = o.x[i];
				this.y[i] = o.y[i];
			}
		}
		@Override
		public Polygon clone() {
			return new Polygon(this);
		}
		public int getPointCount() { return n; }
		public double getPointX(int i) { return x[i % n]; }
		public double getPointY(int i) { return y[i % n]; }
		public boolean isClosed() { return closed; }
		@Override
		public GeneralPath getShape() {
			GeneralPath g = new GeneralPath();
			if (n > 0) g.moveTo(x[0], y[0]);
			for (int i = 1; i < n; i++) g.lineTo(x[i], y[i]);
			if (closed) g.closePath();
			return g;
		}
		@Override
		protected Object getControlState() {
			double[] state = new double[n * 2];
			for (int i = 0; i < n; i++) {
				state[i * 2 + 0] = x[i];
				state[i * 2 + 1] = y[i];
			}
			return state;
		}
		@Override
		protected void setControlState(Object o) {
			double[] state = (double[])o;
			for (int i = 0; i < n; i++) {
				x[i] = state[i * 2 + 0];
				y[i] = state[i * 2 + 1];
			}
		}
		@Override
		public int getControlPointCount() {
			return n;
		}
		@Override
		protected ControlPoint getControlPointImpl(int i) {
			return new ControlPoint(ControlPointType.STRAIGHT_MIDPOINT, x[i], y[i]);
		}
		@Override
		protected List<ControlPoint> getControlPointsImpl() {
			List<ControlPoint> cpts = new ArrayList<ControlPoint>();
			for (int i = 0; i < n; i++) cpts.add(new ControlPoint(ControlPointType.STRAIGHT_MIDPOINT, x[i], y[i]));
			return cpts;
		}
		@Override
		protected Collection<Line2D> getControlLinesImpl() {
			return null;
		}
		@Override
		protected int setControlPointImpl(int i, double x, double y) {
			this.x[i] = x;
			this.y[i] = y;
			return i;
		}
		@Override
		protected Point2D getLocationImpl() {
			if (n > 0) return new Point2D.Double(x[0], y[0]);
			else return new Point2D.Double(0, 0);
		}
		@Override
		protected void setLocationImpl(double x, double y) {
			for (int i = n - 1; i > 0; i--) {
				this.x[i] = x + (this.x[i] - this.x[0]);
				this.y[i] = y + (this.y[i] - this.y[0]);
			}
			if (n > 0) {
				this.x[0] = x;
				this.y[0] = y;
			}
		}
	}
	
	private static boolean equals(Object dis, Object dat) {
		if (dis == null) return (dat == null);
		if (dat == null) return (dis == null);
		return dis.equals(dat);
	}
}
