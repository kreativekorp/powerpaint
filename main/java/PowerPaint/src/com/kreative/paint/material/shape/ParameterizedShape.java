package com.kreative.paint.material.shape;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public abstract class ParameterizedShape {
	public abstract Shape awtShape(Bindings bindings);
	
	public static class Arc extends ParameterizedShape {
		public final ParameterizedValue cx, cy, rx, ry;
		public final ParameterizedValue start, extent;
		public final ArcType type;
		public Arc(
			ParameterizedValue cx, ParameterizedValue cy,
			ParameterizedValue rx, ParameterizedValue ry,
			ParameterizedValue start, ParameterizedValue extent,
			ArcType type
		) {
			this.cx = cx; this.cy = cy;
			this.rx = rx; this.ry = ry;
			this.start = start; this.extent = extent;
			this.type = type;
		}
		@Override
		public Arc2D awtShape(Bindings bindings) {
			double cx = this.cx.value(bindings), cy = this.cy.value(bindings);
			double rx = this.rx.value(bindings), ry = this.ry.value(bindings);
			double start = this.start.value(bindings), extent = this.extent.value(bindings);
			int type = (this.type != null) ? this.type.awtValue : Arc2D.OPEN;
			return new Arc2D.Double(cx-rx, cy-ry, rx+rx, ry+ry, start, extent, type);
		}
		@Override
		public boolean equals(Object that) {
			return (that instanceof Arc)
			    && (this.cx.equals(((Arc)that).cx))
			    && (this.cy.equals(((Arc)that).cy))
			    && (this.rx.equals(((Arc)that).rx))
			    && (this.ry.equals(((Arc)that).ry))
			    && (this.start.equals(((Arc)that).start))
			    && (this.extent.equals(((Arc)that).extent))
			    && (this.type == ((Arc)that).type);
		}
		@Override
		public int hashCode() {
			return cx.hashCode() ^ cy.hashCode()
			     ^ rx.hashCode() ^ ry.hashCode()
			     ^ start.hashCode() ^ extent.hashCode();
		}
	}
	
	public static class Circle extends ParameterizedShape {
		public final ParameterizedValue cx, cy, r;
		public Circle(ParameterizedValue cx, ParameterizedValue cy, ParameterizedValue r) {
			this.cx = cx; this.cy = cy; this.r = r;
		}
		@Override
		public Ellipse2D awtShape(Bindings bindings) {
			double cx = this.cx.value(bindings);
			double cy = this.cy.value(bindings);
			double r = this.r.value(bindings);
			return new Ellipse2D.Double(cx-r, cy-r, r+r, r+r);
		}
		@Override
		public boolean equals(Object that) {
			return (that instanceof Circle)
			    && (this.cx.equals(((Circle)that).cx))
			    && (this.cy.equals(((Circle)that).cy))
			    && (this.r.equals(((Circle)that).r));
		}
		@Override
		public int hashCode() {
			return cx.hashCode() ^ cy.hashCode() ^ r.hashCode();
		}
	}
	
	public static class Ellipse extends ParameterizedShape {
		public final ParameterizedValue cx, cy, rx, ry;
		public Ellipse(
			ParameterizedValue cx, ParameterizedValue cy,
			ParameterizedValue rx, ParameterizedValue ry
		) {
			this.cx = cx; this.cy = cy;
			this.rx = rx; this.ry = ry;
		}
		@Override
		public Ellipse2D awtShape(Bindings bindings) {
			double cx = this.cx.value(bindings), cy = this.cy.value(bindings);
			double rx = this.rx.value(bindings), ry = this.ry.value(bindings);
			return new Ellipse2D.Double(cx-rx, cy-ry, rx+rx, ry+ry);
		}
		@Override
		public boolean equals(Object that) {
			return (that instanceof Ellipse)
			    && (this.cx.equals(((Ellipse)that).cx))
			    && (this.cy.equals(((Ellipse)that).cy))
			    && (this.rx.equals(((Ellipse)that).rx))
			    && (this.ry.equals(((Ellipse)that).ry));
		}
		@Override
		public int hashCode() {
			return cx.hashCode() ^ cy.hashCode()
			     ^ rx.hashCode() ^ ry.hashCode();
		}
	}
	
	public static class Line extends ParameterizedShape {
		public final ParameterizedValue x1, y1, x2, y2;
		public Line(
			ParameterizedValue x1, ParameterizedValue y1,
			ParameterizedValue x2, ParameterizedValue y2
		) {
			this.x1 = x1; this.y1 = y1;
			this.x2 = x2; this.y2 = y2;
		}
		@Override
		public Line2D awtShape(Bindings bindings) {
			double x1 = this.x1.value(bindings), y1 = this.y1.value(bindings);
			double x2 = this.x2.value(bindings), y2 = this.y2.value(bindings);
			return new Line2D.Double(x1, y1, x2, y2);
		}
		@Override
		public boolean equals(Object that) {
			return (that instanceof Line)
			    && (this.x1.equals(((Line)that).x1))
			    && (this.y1.equals(((Line)that).y1))
			    && (this.x2.equals(((Line)that).x2))
			    && (this.y2.equals(((Line)that).y2));
		}
		@Override
		public int hashCode() {
			return x1.hashCode() ^ y1.hashCode()
			     ^ x2.hashCode() ^ y2.hashCode();
		}
	}
	
	public static class Polygon extends ParameterizedShape {
		public final ParameterizedValue[] points;
		public Polygon(ParameterizedValue[] points) {
			this.points = points;
		}
		@Override
		public Shape awtShape(Bindings bindings) {
			GeneralPath p = new GeneralPath();
			if (points.length >= 2) {
				double x = points[0].value(bindings);
				double y = points[1].value(bindings);
				p.moveTo(x, y);
				for (int i = 2, j = 1, n = points.length / 2; j < n; j++) {
					x = points[i++].value(bindings);
					y = points[i++].value(bindings);
					p.lineTo(x, y);
				}
				p.closePath();
			}
			return p;
		}
		@Override
		public boolean equals(Object that) {
			if (that instanceof Polygon) {
				ParameterizedValue[] thisPoints = this.points;
				ParameterizedValue[] thatPoints = ((Polygon)that).points;
				if (thisPoints.length != thatPoints.length) return false;
				for (int i = 0; i < thisPoints.length; i++) {
					if (!thisPoints[i].equals(thatPoints[i])) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
		@Override
		public int hashCode() {
			int hashCode = 0;
			for (ParameterizedValue point : points) {
				hashCode ^= point.hashCode();
			}
			return hashCode;
		}
	}
	
	public static class PolyLine extends ParameterizedShape {
		public final ParameterizedValue[] points;
		public PolyLine(ParameterizedValue[] points) {
			this.points = points;
		}
		@Override
		public Shape awtShape(Bindings bindings) {
			GeneralPath p = new GeneralPath();
			if (points.length >= 2) {
				double x = points[0].value(bindings);
				double y = points[1].value(bindings);
				p.moveTo(x, y);
				for (int i = 2, j = 1, n = points.length / 2; j < n; j++) {
					x = points[i++].value(bindings);
					y = points[i++].value(bindings);
					p.lineTo(x, y);
				}
			}
			return p;
		}
		@Override
		public boolean equals(Object that) {
			if (that instanceof PolyLine) {
				ParameterizedValue[] thisPoints = this.points;
				ParameterizedValue[] thatPoints = ((PolyLine)that).points;
				if (thisPoints.length != thatPoints.length) return false;
				for (int i = 0; i < thisPoints.length; i++) {
					if (!thisPoints[i].equals(thatPoints[i])) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
		@Override
		public int hashCode() {
			int hashCode = 0;
			for (ParameterizedValue point : points) {
				hashCode ^= point.hashCode();
			}
			return hashCode;
		}
	}
	
	public static class Rect extends ParameterizedShape {
		public final ParameterizedValue x, y, width, height, rx, ry;
		public Rect(
			ParameterizedValue x, ParameterizedValue y,
			ParameterizedValue width, ParameterizedValue height,
			ParameterizedValue rx, ParameterizedValue ry
		) {
			this.x = x; this.y = y;
			this.width = width; this.height = height;
			this.rx = rx; this.ry = ry;
		}
		@Override
		public Shape awtShape(Bindings bindings) {
			double x = this.x.value(bindings), y = this.y.value(bindings);
			double width = this.width.value(bindings), height = this.height.value(bindings);
			double rx = this.rx.value(bindings), ry = this.ry.value(bindings);
			if (rx == 0 && ry == 0) {
				return new Rectangle2D.Double(x, y, width, height);
			} else {
				return new RoundRectangle2D.Double(x, y, width, height, rx, ry);
			}
		}
		@Override
		public boolean equals(Object that) {
			return (that instanceof Rect)
			    && (this.x.equals(((Rect)that).x))
			    && (this.y.equals(((Rect)that).y))
			    && (this.width.equals(((Rect)that).width))
			    && (this.height.equals(((Rect)that).height))
			    && (this.rx.equals(((Rect)that).rx))
			    && (this.ry.equals(((Rect)that).ry));
		}
		@Override
		public int hashCode() {
			return x.hashCode() ^ y.hashCode()
			     ^ width.hashCode() ^ height.hashCode()
			     ^ rx.hashCode() ^ ry.hashCode();
		}
	}
}
