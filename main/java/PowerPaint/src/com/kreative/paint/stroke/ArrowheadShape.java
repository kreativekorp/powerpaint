package com.kreative.paint.stroke;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ArrowheadShape implements Shape {
	private final Shape awtShape;
	public final boolean stroke;
	public final boolean fill;
	
	protected ArrowheadShape(Shape awtShape, boolean stroke, boolean fill) {
		this.awtShape = awtShape;
		this.stroke = stroke;
		this.fill = fill;
	}
	
	protected abstract boolean equalsImpl(ArrowheadShape that);
	protected abstract int hashCodeImpl();
	
	@Override
	public final boolean equals(Object that) {
		if (that instanceof ArrowheadShape) {
			return (this.equalsImpl((ArrowheadShape)that))
			    && (this.stroke == ((ArrowheadShape)that).stroke)
			    && (this.fill == ((ArrowheadShape)that).fill);
		} else {
			return false;
		}
	}
	
	@Override
	public final int hashCode() {
		int hashCode = hashCodeImpl();
		if (stroke) hashCode ^= 0x55555555;
		if (fill  ) hashCode ^= 0xAAAAAAAA;
		return hashCode;
	}
	
	public static class Circle extends ArrowheadShape {
		public final float cx, cy, r;
		public Circle(float cx, float cy, float r, boolean stroke, boolean fill) {
			super(new Ellipse2D.Float(cx-r, cy-r, r+r, r+r), stroke, fill);
			this.cx = cx; this.cy = cy; this.r = r;
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			return (that instanceof Circle)
			    && (this.cx == ((Circle)that).cx)
			    && (this.cy == ((Circle)that).cy)
			    && (this.r == ((Circle)that).r);
		}
		@Override
		protected int hashCodeImpl() {
			return Float.floatToIntBits(cx + cy + r);
		}
	}
	
	public static class Ellipse extends ArrowheadShape {
		public final float cx, cy, rx, ry;
		public Ellipse(float cx, float cy, float rx, float ry, boolean stroke, boolean fill) {
			super(new Ellipse2D.Float(cx-rx, cy-ry, rx+rx, ry+ry), stroke, fill);
			this.cx = cx; this.cy = cy; this.rx = rx; this.ry = ry;
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			return (that instanceof Ellipse)
			    && (this.cx == ((Ellipse)that).cx)
			    && (this.cy == ((Ellipse)that).cy)
			    && (this.rx == ((Ellipse)that).rx)
			    && (this.ry == ((Ellipse)that).ry);
		}
		@Override
		protected int hashCodeImpl() {
			return Float.floatToIntBits(cx + cy + rx + ry);
		}
	}
	
	public static class Line extends ArrowheadShape {
		public final float x1, y1, x2, y2;
		public Line(float x1, float y1, float x2, float y2, boolean stroke, boolean fill) {
			super(new Line2D.Float(x1, y1, x2, y2), stroke, fill);
			this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			return (that instanceof Line)
			    && (this.x1 == ((Line)that).x1)
			    && (this.y1 == ((Line)that).y1)
			    && (this.x2 == ((Line)that).x2)
			    && (this.y2 == ((Line)that).y2);
		}
		@Override
		protected int hashCodeImpl() {
			return Float.floatToIntBits(x1 + y1 + x2 + y2);
		}
	}
	
	public static class Path extends ArrowheadShape {
		public final String d;
		public Path(String d, boolean stroke, boolean fill) {
			this(parseInstructions(d), stroke, fill);
		}
		// Do not try this at home.
		private Path(Object[] o, boolean stroke, boolean fill) {
			super((Path2D.Float)o[1], stroke, fill);
			this.d = (String)o[0];
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			if (that instanceof Path) {
				return this.d.equals(((Path)that).d);
			} else {
				return false;
			}
		}
		@Override
		protected int hashCodeImpl() {
			return this.d.hashCode();
		}
	}
	
	public static class Polygon extends ArrowheadShape {
		public final float[] points;
		public Polygon(float[] points, boolean stroke, boolean fill) {
			super(makeShape(points), stroke, fill);
			this.points = points;
		}
		public Polygon(String points, boolean stroke, boolean fill) {
			this(parseFloats(points), stroke, fill);
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			if (that instanceof Polygon) {
				float[] thisPoints = this.points;
				float[] thatPoints = ((Polygon)that).points;
				if (thisPoints.length != thatPoints.length) return false;
				for (int i = 0; i < thisPoints.length; i++) {
					if (thisPoints[i] != thatPoints[i]) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
		@Override
		protected int hashCodeImpl() {
			float f = 0.0f;
			for (float point : points) f += point;
			return Float.floatToIntBits(f);
		}
		private static Path2D.Float makeShape(float[] points) {
			Path2D.Float p = new Path2D.Float();
			if (points.length >= 2) {
				p.moveTo(points[0], points[1]);
				for (int i = 2, j = 1, n = points.length / 2; j < n; j++) {
					float x = points[i++];
					float y = points[i++];
					p.lineTo(x, y);
				}
				p.closePath();
			}
			return p;
		}
	}
	
	public static class PolyLine extends ArrowheadShape {
		public final float[] points;
		public PolyLine(float[] points, boolean stroke, boolean fill) {
			super(makeShape(points), stroke, fill);
			this.points = points;
		}
		public PolyLine(String points, boolean stroke, boolean fill) {
			this(parseFloats(points), stroke, fill);
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			if (that instanceof PolyLine) {
				float[] thisPoints = this.points;
				float[] thatPoints = ((PolyLine)that).points;
				if (thisPoints.length != thatPoints.length) return false;
				for (int i = 0; i < thisPoints.length; i++) {
					if (thisPoints[i] != thatPoints[i]) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
		@Override
		protected int hashCodeImpl() {
			float f = 0.0f;
			for (float point : points) f += point;
			return Float.floatToIntBits(f);
		}
		private static Path2D.Float makeShape(float[] points) {
			Path2D.Float p = new Path2D.Float();
			if (points.length >= 2) {
				p.moveTo(points[0], points[1]);
				for (int i = 2, j = 1, n = points.length / 2; j < n; j++) {
					float x = points[i++];
					float y = points[i++];
					p.lineTo(x, y);
				}
			}
			return p;
		}
	}
	
	public static class Rect extends ArrowheadShape {
		public final float x, y, width, height, rx, ry;
		public Rect(
			float x, float y, float width, float height,
			float rx, float ry, boolean stroke, boolean fill
		) {
			super(
				(rx == 0.0f && ry == 0.0f) ?
				new Rectangle2D.Float(x, y, width, height) :
				new RoundRectangle2D.Float(x, y, width, height, rx, ry),
				stroke, fill
			);
			this.x = x; this.y = y;
			this.width = width; this.height = height;
			this.rx = rx; this.ry = ry;
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			return (that instanceof Rect)
			    && (this.x == ((Rect)that).x)
			    && (this.y == ((Rect)that).y)
			    && (this.width == ((Rect)that).width)
			    && (this.height == ((Rect)that).height)
			    && (this.rx == ((Rect)that).rx)
			    && (this.ry == ((Rect)that).ry);
		}
		@Override
		protected int hashCodeImpl() {
			return Float.floatToIntBits(x + y + width + height + rx + ry);
		}
	}
	
	@Override
	public final boolean contains(Point2D p) {
		return awtShape.contains(p);
	}
	@Override
	public final boolean contains(Rectangle2D r) {
		return awtShape.contains(r);
	}
	@Override
	public final boolean contains(double x, double y) {
		return awtShape.contains(x, y);
	}
	@Override
	public final boolean contains(double x, double y, double w, double h) {
		return awtShape.contains(x, y, w, h);
	}
	@Override
	public final Rectangle getBounds() {
		return awtShape.getBounds();
	}
	@Override
	public final Rectangle2D getBounds2D() {
		return awtShape.getBounds2D();
	}
	@Override
	public final PathIterator getPathIterator(AffineTransform at) {
		return awtShape.getPathIterator(at);
	}
	@Override
	public final PathIterator getPathIterator(AffineTransform at, double flatness) {
		return awtShape.getPathIterator(at, flatness);
	}
	@Override
	public final boolean intersects(Rectangle2D r) {
		return awtShape.intersects(r);
	}
	@Override
	public final boolean intersects(double x, double y, double w, double h) {
		return awtShape.intersects(x, y, w, h);
	}
	
	private static final Pattern NUMBER_PATTERN = Pattern.compile("([+-]?)([0-9]+([.][0-9]*)?|[.][0-9]+)");
	private static float[] parseFloats(String s) {
		List<Float> floats = new ArrayList<Float>();
		Matcher m = NUMBER_PATTERN.matcher(s);
		while (m.find()) {
			try {
				float f = Float.parseFloat(m.group());
				floats.add(f);
			} catch (NumberFormatException nfe) {
				// ignored
			}
		}
		int i = 0, n = floats.size();
		float[] a = new float[n];
		for (float f : floats) a[i++] = f;
		return a;
	}
	
	private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("[A-Za-z]|([+-]?)([0-9]+([.][0-9]*)?|[.][0-9]+)");
	private static Object[] parseInstructions(String s) {
		List<String> instructions = new ArrayList<String>();
		Path2D p = new Path2D.Float();
		float lx = 0.0f, ly = 0.0f, cx1, cy1, cx2, cy2;
		Matcher m = INSTRUCTION_PATTERN.matcher(s);
		while (m.find()) {
			String inst = m.group();
			switch (inst.charAt(0)) {
				case 'M':
					instructions.add("M");
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.moveTo(lx, ly);
					break;
				case 'm':
					instructions.add("M");
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.moveTo(lx, ly);
					break;
				case 'H':
					instructions.add("H");
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					p.lineTo(lx, ly);
					break;
				case 'h':
					instructions.add("H");
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					p.lineTo(lx, ly);
					break;
				case 'V':
					instructions.add("V");
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.lineTo(lx, ly);
					break;
				case 'v':
					instructions.add("V");
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.lineTo(lx, ly);
					break;
				case 'L':
					instructions.add("L");
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.lineTo(lx, ly);
					break;
				case 'l':
					instructions.add("L");
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.lineTo(lx, ly);
					break;
				case 'Q':
					instructions.add("Q");
					instructions.add(Float.toString(cx1 = parseInstructionFloat(m)));
					instructions.add(Float.toString(cy1 = parseInstructionFloat(m)));
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.quadTo(cx1, cy1, lx, ly);
					break;
				case 'q':
					instructions.add("Q");
					instructions.add(Float.toString(cx1 = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(cy1 = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.quadTo(cx1, cy1, lx, ly);
					break;
				case 'C':
					instructions.add("C");
					instructions.add(Float.toString(cx1 = parseInstructionFloat(m)));
					instructions.add(Float.toString(cy1 = parseInstructionFloat(m)));
					instructions.add(Float.toString(cx2 = parseInstructionFloat(m)));
					instructions.add(Float.toString(cy2 = parseInstructionFloat(m)));
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.curveTo(cx1, cy1, cx2, cy2, lx, ly);
					break;
				case 'c':
					instructions.add("C");
					instructions.add(Float.toString(cx1 = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(cy1 = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(cx2 = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(cy2 = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.curveTo(cx1, cy1, cx2, cy2, lx, ly);
					break;
				case 'Z':
				case 'z':
					instructions.add("Z");
					p.closePath();
					break;
			}
		}
		StringBuffer sb = new StringBuffer();
		Iterator<String> ii = instructions.iterator();
		if (ii.hasNext()) {
			sb.append(ii.next());
		}
		while (ii.hasNext()) {
			sb.append(" ");
			sb.append(ii.next());
		}
		s = sb.toString();
		return new Object[]{s,p};
	}
	private static float parseInstructionFloat(Matcher m) {
		if (m.find()) {
			try {
				return Float.parseFloat(m.group());
			} catch (NumberFormatException nfe) {
				return 0.0f;
			}
		} else {
			return 0.0f;
		}
	}
}
