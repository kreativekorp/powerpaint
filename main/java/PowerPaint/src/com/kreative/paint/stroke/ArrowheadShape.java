package com.kreative.paint.stroke;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
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
	public static final ArrowheadShape GENERAL_FILLED_ARROW = new ArrowheadShape.Polygon(
		new float[]{6,0,0,6,0,-6}, EndCap.SQUARE, LineJoin.MITER, 10.0f, true, true
	);
	public static final ArrowheadShape GENERAL_STROKED_ARROW = new ArrowheadShape.Polygon(
		new float[]{6,0,0,6,0,-6}, EndCap.SQUARE, LineJoin.MITER, 10.0f, true, false
	);
	public static final ArrowheadShape GENERAL_FILLED_CIRCLE = new ArrowheadShape.Circle(2, 0, 4, true, true);
	public static final ArrowheadShape GENERAL_STROKED_CIRCLE = new ArrowheadShape.Circle(2, 0, 4, true, false);
	
	private final Shape awtShape;
	public final boolean stroke;
	public final boolean fill;
	
	protected ArrowheadShape(Shape awtShape, boolean stroke, boolean fill) {
		this.awtShape = awtShape;
		this.stroke = stroke;
		this.fill = fill;
	}
	
	public abstract Stroke getStroke(float lineWidth);
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
	
	public static class Arc extends ArrowheadShape {
		public final float cx, cy, rx, ry;
		public final float start, extent;
		public final ArcType type;
		public final EndCap endCap;
		public final LineJoin lineJoin;
		public final float miterLimit;
		public Arc(
			float cx, float cy, float rx, float ry,
			float start, float extent, ArcType type,
			EndCap endCap, LineJoin lineJoin, float miterLimit,
			boolean stroke, boolean fill
		) {
			super(new Arc2D.Float(
				cx-rx, cy-ry, rx+rx, ry+ry, start, extent,
				((type != null) ? type.awtValue : Arc2D.OPEN)
			), stroke, fill);
			this.cx = cx; this.cy = cy; this.rx = rx; this.ry = ry;
			this.start = start; this.extent = extent;
			this.type = type;
			this.endCap = endCap;
			this.lineJoin = lineJoin;
			this.miterLimit = miterLimit;
		}
		@Override
		public Stroke getStroke(float lineWidth) {
			return new BasicStroke(
				lineWidth,
				((endCap != null) ? endCap.awtValue : BasicStroke.CAP_SQUARE),
				((lineJoin != null) ? lineJoin.awtValue : BasicStroke.JOIN_MITER),
				miterLimit
			);
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			return (that instanceof Arc)
			    && (this.cx == ((Arc)that).cx)
			    && (this.cy == ((Arc)that).cy)
			    && (this.rx == ((Arc)that).rx)
			    && (this.ry == ((Arc)that).ry)
			    && (this.start == ((Arc)that).start)
			    && (this.extent == ((Arc)that).extent)
			    && (this.type == ((Arc)that).type)
			    && (this.endCap == ((Arc)that).endCap)
			    && (this.lineJoin == ((Arc)that).lineJoin)
			    && (this.miterLimit == ((Arc)that).miterLimit);
		}
		@Override
		protected int hashCodeImpl() {
			return Float.floatToIntBits(cx + cy + rx + ry + start + extent);
		}
	}
	
	public static class Circle extends ArrowheadShape {
		public final float cx, cy, r;
		public Circle(float cx, float cy, float r, boolean stroke, boolean fill) {
			super(new Ellipse2D.Float(cx-r, cy-r, r+r, r+r), stroke, fill);
			this.cx = cx; this.cy = cy; this.r = r;
		}
		@Override
		public Stroke getStroke(float lineWidth) {
			return new BasicStroke(lineWidth);
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
		public Stroke getStroke(float lineWidth) {
			return new BasicStroke(lineWidth);
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
		public final EndCap endCap;
		public Line(float x1, float y1, float x2, float y2, EndCap endCap, boolean stroke, boolean fill) {
			super(new Line2D.Float(x1, y1, x2, y2), stroke, fill);
			this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
			this.endCap = endCap;
		}
		@Override
		public Stroke getStroke(float lineWidth) {
			return new BasicStroke(
				lineWidth,
				((endCap != null) ? endCap.awtValue : BasicStroke.CAP_SQUARE),
				BasicStroke.JOIN_MITER,
				10.0f
			);
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			return (that instanceof Line)
			    && (this.x1 == ((Line)that).x1)
			    && (this.y1 == ((Line)that).y1)
			    && (this.x2 == ((Line)that).x2)
			    && (this.y2 == ((Line)that).y2)
			    && (this.endCap == ((Line)that).endCap);
		}
		@Override
		protected int hashCodeImpl() {
			return Float.floatToIntBits(x1 + y1 + x2 + y2);
		}
	}
	
	public static class Path extends ArrowheadShape {
		public final String d;
		public final EndCap endCap;
		public final LineJoin lineJoin;
		public final float miterLimit;
		public Path(String d, EndCap endCap, LineJoin lineJoin, float miterLimit, boolean stroke, boolean fill) {
			this(parseInstructions(d), endCap, lineJoin, miterLimit, stroke, fill);
		}
		// Do not try this at home.
		private Path(Object[] o, EndCap endCap, LineJoin lineJoin, float miterLimit, boolean stroke, boolean fill) {
			super((GeneralPath)o[1], stroke, fill);
			this.d = (String)o[0];
			this.endCap = endCap;
			this.lineJoin = lineJoin;
			this.miterLimit = miterLimit;
		}
		@Override
		public Stroke getStroke(float lineWidth) {
			return new BasicStroke(
				lineWidth,
				((endCap != null) ? endCap.awtValue : BasicStroke.CAP_SQUARE),
				((lineJoin != null) ? lineJoin.awtValue : BasicStroke.JOIN_MITER),
				miterLimit
			);
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			if (that instanceof Path) {
				if (!this.d.equals(((Path)that).d)) return false;
				if (this.endCap != ((Path)that).endCap) return false;
				if (this.lineJoin != ((Path)that).lineJoin) return false;
				if (this.miterLimit != ((Path)that).miterLimit) return false;
				return true;
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
		public final EndCap endCap;
		public final LineJoin lineJoin;
		public final float miterLimit;
		public Polygon(float[] points, EndCap endCap, LineJoin lineJoin, float miterLimit, boolean stroke, boolean fill) {
			super(makeShape(points), stroke, fill);
			this.points = points;
			this.endCap = endCap;
			this.lineJoin = lineJoin;
			this.miterLimit = miterLimit;
		}
		public Polygon(String points, EndCap endCap, LineJoin lineJoin, float miterLimit, boolean stroke, boolean fill) {
			this(parseFloats(points), endCap, lineJoin, miterLimit, stroke, fill);
		}
		@Override
		public Stroke getStroke(float lineWidth) {
			return new BasicStroke(
				lineWidth,
				((endCap != null) ? endCap.awtValue : BasicStroke.CAP_SQUARE),
				((lineJoin != null) ? lineJoin.awtValue : BasicStroke.JOIN_MITER),
				miterLimit
			);
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
				if (this.endCap != ((Polygon)that).endCap) return false;
				if (this.lineJoin != ((Polygon)that).lineJoin) return false;
				if (this.miterLimit != ((Polygon)that).miterLimit) return false;
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
		private static GeneralPath makeShape(float[] points) {
			GeneralPath p = new GeneralPath();
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
		public final EndCap endCap;
		public final LineJoin lineJoin;
		public final float miterLimit;
		public PolyLine(float[] points, EndCap endCap, LineJoin lineJoin, float miterLimit, boolean stroke, boolean fill) {
			super(makeShape(points), stroke, fill);
			this.points = points;
			this.endCap = endCap;
			this.lineJoin = lineJoin;
			this.miterLimit = miterLimit;
		}
		public PolyLine(String points, EndCap endCap, LineJoin lineJoin, float miterLimit, boolean stroke, boolean fill) {
			this(parseFloats(points), endCap, lineJoin, miterLimit, stroke, fill);
		}
		@Override
		public Stroke getStroke(float lineWidth) {
			return new BasicStroke(
				lineWidth,
				((endCap != null) ? endCap.awtValue : BasicStroke.CAP_SQUARE),
				((lineJoin != null) ? lineJoin.awtValue : BasicStroke.JOIN_MITER),
				miterLimit
			);
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
				if (this.endCap != ((PolyLine)that).endCap) return false;
				if (this.lineJoin != ((PolyLine)that).lineJoin) return false;
				if (this.miterLimit != ((PolyLine)that).miterLimit) return false;
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
		private static GeneralPath makeShape(float[] points) {
			GeneralPath p = new GeneralPath();
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
		public final LineJoin lineJoin;
		public final float miterLimit;
		public Rect(
			float x, float y, float width, float height,
			float rx, float ry, LineJoin lineJoin, float miterLimit,
			boolean stroke, boolean fill
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
			this.lineJoin = lineJoin;
			this.miterLimit = miterLimit;
		}
		@Override
		public Stroke getStroke(float lineWidth) {
			return new BasicStroke(
				lineWidth,
				BasicStroke.CAP_SQUARE,
				((lineJoin != null) ? lineJoin.awtValue : BasicStroke.JOIN_MITER),
				miterLimit
			);
		}
		@Override
		protected boolean equalsImpl(ArrowheadShape that) {
			return (that instanceof Rect)
			    && (this.x == ((Rect)that).x)
			    && (this.y == ((Rect)that).y)
			    && (this.width == ((Rect)that).width)
			    && (this.height == ((Rect)that).height)
			    && (this.rx == ((Rect)that).rx)
			    && (this.ry == ((Rect)that).ry)
			    && (this.lineJoin == ((Rect)that).lineJoin)
			    && (this.miterLimit == ((Rect)that).miterLimit);
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
		GeneralPath p = new GeneralPath();
		float lcx = 0.0f, lcy = 0.0f, lx = 0.0f, ly = 0.0f;
		float ccx, ccy, arx, ary, aa;
		boolean large, sweep;
		float rx, ry, rw, rh, rrx, rry, ras, rae;
		int rat;
		Matcher m = INSTRUCTION_PATTERN.matcher(s);
		while (m.find()) {
			String inst = m.group();
			switch (inst.charAt(0)) {
				case 'M':
					instructions.add("M");
					instructions.add(Float.toString(lcx = lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly = parseInstructionFloat(m)));
					p.moveTo(lx, ly);
					break;
				case 'm':
					instructions.add("M");
					instructions.add(Float.toString(lcx = lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly += parseInstructionFloat(m)));
					p.moveTo(lx, ly);
					break;
				case 'H':
					instructions.add("H");
					instructions.add(Float.toString(lcx = lx = parseInstructionFloat(m)));
					p.lineTo(lx, lcy = ly);
					break;
				case 'h':
					instructions.add("H");
					instructions.add(Float.toString(lcx = lx += parseInstructionFloat(m)));
					p.lineTo(lx, lcy = ly);
					break;
				case 'V':
					instructions.add("V");
					instructions.add(Float.toString(lcy = ly = parseInstructionFloat(m)));
					p.lineTo(lcx = lx, ly);
					break;
				case 'v':
					instructions.add("V");
					instructions.add(Float.toString(lcy = ly += parseInstructionFloat(m)));
					p.lineTo(lcx = lx, ly);
					break;
				case 'L':
					instructions.add("L");
					instructions.add(Float.toString(lcx = lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly = parseInstructionFloat(m)));
					p.lineTo(lx, ly);
					break;
				case 'l':
					instructions.add("L");
					instructions.add(Float.toString(lcx = lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly += parseInstructionFloat(m)));
					p.lineTo(lx, ly);
					break;
				case 'Q':
					instructions.add("Q");
					instructions.add(Float.toString(lcx = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = parseInstructionFloat(m)));
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.quadTo(lcx, lcy, lx, ly);
					break;
				case 'q':
					instructions.add("Q");
					instructions.add(Float.toString(lcx = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.quadTo(lcx, lcy, lx, ly);
					break;
				case 'T':
					instructions.add("T");
					lcx = lx + lx - lcx;
					lcy = ly + ly - lcy;
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.quadTo(lcx, lcy, lx, ly);
					break;
				case 't':
					instructions.add("T");
					lcx = lx + lx - lcx;
					lcy = ly + ly - lcy;
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.quadTo(lcx, lcy, lx, ly);
					break;
				case 'C':
					instructions.add("C");
					instructions.add(Float.toString(ccx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ccy = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcx = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = parseInstructionFloat(m)));
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.curveTo(ccx, ccy, lcx, lcy, lx, ly);
					break;
				case 'c':
					instructions.add("C");
					instructions.add(Float.toString(ccx = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(ccy = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(lcx = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.curveTo(ccx, ccy, lcx, lcy, lx, ly);
					break;
				case 'S':
					instructions.add("S");
					ccx = lx + lx - lcx;
					ccy = ly + ly - lcy;
					instructions.add(Float.toString(lcx = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = parseInstructionFloat(m)));
					instructions.add(Float.toString(lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ly = parseInstructionFloat(m)));
					p.curveTo(ccx, ccy, lcx, lcy, lx, ly);
					break;
				case 's':
					instructions.add("S");
					ccx = lx + lx - lcx;
					ccy = ly + ly - lcy;
					instructions.add(Float.toString(lcx = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(ly += parseInstructionFloat(m)));
					p.curveTo(ccx, ccy, lcx, lcy, lx, ly);
					break;
				case 'A':
					instructions.add("A");
					instructions.add(Float.toString(arx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ary = parseInstructionFloat(m)));
					instructions.add(Float.toString(aa = parseInstructionFloat(m)));
					instructions.add((large = (parseInstructionFloat(m) != 0)) ? "1" : "0");
					instructions.add((sweep = (parseInstructionFloat(m) != 0)) ? "1" : "0");
					instructions.add(Float.toString(lcx = lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly = parseInstructionFloat(m)));
					arcTo(p, arx, ary, aa, large, sweep, lx, ly);
					break;
				case 'a':
					instructions.add("A");
					instructions.add(Float.toString(arx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ary = parseInstructionFloat(m)));
					instructions.add(Float.toString(aa = parseInstructionFloat(m)));
					instructions.add((large = (parseInstructionFloat(m) != 0)) ? "1" : "0");
					instructions.add((sweep = (parseInstructionFloat(m) != 0)) ? "1" : "0");
					instructions.add(Float.toString(lcx = lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly += parseInstructionFloat(m)));
					arcTo(p, arx, ary, aa, large, sweep, lx, ly);
					break;
				case 'Z':
				case 'z':
					instructions.add("Z");
					p.closePath();
					lcx = lx = (float)p.getCurrentPoint().getX();
					lcy = ly = (float)p.getCurrentPoint().getY();
					break;
				case 'G':
					instructions.add("G");
					instructions.add(Float.toString(ccx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ccy = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcx = lx = parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly = parseInstructionFloat(m)));
					coArcTo(p, ccx, ccy, lx, ly);
					break;
				case 'g':
					instructions.add("G");
					instructions.add(Float.toString(ccx = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(ccy = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(lcx = lx += parseInstructionFloat(m)));
					instructions.add(Float.toString(lcy = ly += parseInstructionFloat(m)));
					coArcTo(p, ccx, ccy, lx, ly);
					break;
				case 'R':
					instructions.add("R");
					instructions.add(Float.toString(rx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ry = parseInstructionFloat(m)));
					instructions.add(Float.toString(rw = parseInstructionFloat(m)));
					instructions.add(Float.toString(rh = parseInstructionFloat(m)));
					instructions.add(Float.toString(rrx = parseInstructionFloat(m)));
					instructions.add(Float.toString(rry = parseInstructionFloat(m)));
					if (rrx == 0 || rry == 0) {
						p.append(new Rectangle2D.Float(rx, ry, rw, rh), false);
					} else {
						p.append(new RoundRectangle2D.Float(rx, ry, rw, rh, rrx, rry), false);
					}
					p.moveTo(lcx = lx, lcy = ly);
					break;
				case 'r':
					instructions.add("R");
					instructions.add(Float.toString(rx = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(ry = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(rw = parseInstructionFloat(m)));
					instructions.add(Float.toString(rh = parseInstructionFloat(m)));
					instructions.add(Float.toString(rrx = parseInstructionFloat(m)));
					instructions.add(Float.toString(rry = parseInstructionFloat(m)));
					if (rrx == 0 || rry == 0) {
						p.append(new Rectangle2D.Float(rx, ry, rw, rh), false);
					} else {
						p.append(new RoundRectangle2D.Float(rx, ry, rw, rh, rrx, rry), false);
					}
					p.moveTo(lcx = lx, lcy = ly);
					break;
				case 'E':
					instructions.add("E");
					instructions.add(Float.toString(rx = parseInstructionFloat(m)));
					instructions.add(Float.toString(ry = parseInstructionFloat(m)));
					instructions.add(Float.toString(rw = parseInstructionFloat(m)));
					instructions.add(Float.toString(rh = parseInstructionFloat(m)));
					instructions.add(Float.toString(ras = parseInstructionFloat(m)));
					instructions.add(Float.toString(rae = parseInstructionFloat(m)));
					instructions.add(Integer.toString(rat = (Math.abs((int)Math.round(parseInstructionFloat(m))) % 5)));
					if (rae <= -360 || rae >= 360) {
						p.append(new Ellipse2D.Float(rx, ry, rw, rh), false);
						p.moveTo(lcx = lx, lcy = ly);
					} else if (rat < 3) {
						p.append(new Arc2D.Float(rx, ry, rw, rh, ras, rae, rat), false);
						p.moveTo(lcx = lx, lcy = ly);
					} else {
						p.append(new Arc2D.Float(rx, ry, rw, rh, ras, rae, Arc2D.OPEN), rat > 3);
						Point2D cp = p.getCurrentPoint();
						lcx = lx = (float)cp.getX();
						lcy = ly = (float)cp.getY();
					}
					break;
				case 'e':
					instructions.add("E");
					instructions.add(Float.toString(rx = lx + parseInstructionFloat(m)));
					instructions.add(Float.toString(ry = ly + parseInstructionFloat(m)));
					instructions.add(Float.toString(rw = parseInstructionFloat(m)));
					instructions.add(Float.toString(rh = parseInstructionFloat(m)));
					instructions.add(Float.toString(ras = parseInstructionFloat(m)));
					instructions.add(Float.toString(rae = parseInstructionFloat(m)));
					instructions.add(Integer.toString(rat = (Math.abs((int)Math.round(parseInstructionFloat(m))) % 5)));
					if (rae <= -360 || rae >= 360) {
						p.append(new Ellipse2D.Float(rx, ry, rw, rh), false);
						p.moveTo(lcx = lx, lcy = ly);
					} else if (rat < 3) {
						p.append(new Arc2D.Float(rx, ry, rw, rh, ras, rae, rat), false);
						p.moveTo(lcx = lx, lcy = ly);
					} else {
						p.append(new Arc2D.Float(rx, ry, rw, rh, ras, rae, Arc2D.OPEN), rat > 3);
						Point2D cp = p.getCurrentPoint();
						lcx = lx = (float)cp.getX();
						lcy = ly = (float)cp.getY();
					}
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
	private static void arcTo(
		GeneralPath p, double rx, double ry, double a,
		boolean large, boolean sweep, double x, double y
	) {
		Point2D p0 = p.getCurrentPoint();
		double x0 = p0.getX();
		double y0 = p0.getY();
		if (x0 == x && y0 == y) return;
		if (rx == 0 || ry == 0) { p.lineTo(x, y); return; }
		double dx2 = (x0 - x) / 2;
		double dy2 = (y0 - y) / 2;
		a = Math.toRadians(a % 360);
		double ca = Math.cos(a);
		double sa = Math.sin(a);
		double x1 = sa * dy2 + ca * dx2;
		double y1 = ca * dy2 - sa * dx2;
		rx = Math.abs(rx);
		ry = Math.abs(ry);
		double Prx = rx * rx;
		double Pry = ry * ry;
		double Px1 = x1 * x1;
		double Py1 = y1 * y1;
		double rc = Px1/Prx + Py1/Pry;
		if (rc > 1) {
			rx = Math.sqrt(rc) * rx;
			ry = Math.sqrt(rc) * ry;
			Prx = rx * rx;
			Pry = ry * ry;
		}
		double s = (large == sweep) ? -1 : 1;
		double sq = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1)) / ((Prx*Py1)+(Pry*Px1));
		if (sq < 0) sq = 0;
		double m = s * Math.sqrt(sq);
		double cx1 = m *  ((rx * y1) / ry);
		double cy1 = m * -((ry * x1) / rx);
		double sx2 = (x0 + x) / 2;
		double sy2 = (y0 + y) / 2;
		double cx = sx2 + ca * cx1 - sa * cy1;
		double cy = sy2 + sa * cx1 + ca * cy1;
		double ux = (x1 - cx1) / rx;
		double uy = (y1 - cy1) / ry;
		double vx = (-x1 -cx1) / rx;
		double vy = (-y1 -cy1) / ry;
		double sn = Math.sqrt(ux*ux + uy*uy);
		double sp = ux;
		double ss = (uy < 0) ? -1 : 1;
		double as = Math.toDegrees(ss * Math.acos(sp / sn));
		double en = Math.sqrt((ux*ux + uy*uy) * (vx*vx + vy*vy));
		double ep = ux * vx + uy * vy;
		double es = (ux * vy - uy * vx < 0) ? -1 : 1;
		double ae = Math.toDegrees(es * Math.acos(ep / en));
		if (!sweep && ae > 0) ae -= 360;
		if (sweep && ae < 0) ae += 360;
		ae %= 360;
		as %= 360;
		Arc2D.Double arc = new Arc2D.Double();
		arc.x = cx - rx;
		arc.y = cy - ry;
		arc.width = rx * 2;
		arc.height = ry * 2;
		arc.start = -as;
		arc.extent = -ae;
		double acx = arc.getCenterX();
		double acy = arc.getCenterY();
		AffineTransform t = AffineTransform.getRotateInstance(a, acx, acy);
		p.append(t.createTransformedShape(arc), true);
	}
	private static void coArcTo(GeneralPath p, double x2, double y2, double x3, double y3) {
		Point2D p1 = p.getCurrentPoint();
		double x1 = p1.getX();
		double y1 = p1.getY();
		boolean xe = (x1 == x2 && x2 == x3);
		boolean ye = (y1 == y2 && y2 == y3);
		if (xe && ye) return;
		if (xe || ye) { p.lineTo(x3, y3); return; }
		double d = arcHK(x1, y1, x2, y2, x3, y3);
		double h = arcH(x1, y1, x2, y2, x3, y3) / d;
		double k = arcK(x1, y1, x2, y2, x3, y3) / d;
		if (Double.isNaN(h) || Double.isInfinite(h)) { p.lineTo(x3, y3); return; }
		if (Double.isNaN(k) || Double.isInfinite(k)) { p.lineTo(x3, y3); return; }
		double r = Math.hypot(k - y1, x1 - h);
		double a1 = Math.toDegrees(Math.atan2(k - y1, x1 - h));
		double a2 = Math.toDegrees(Math.atan2(k - y2, x2 - h));
		double a3 = Math.toDegrees(Math.atan2(k - y3, x3 - h));
		Arc2D.Double arc = new Arc2D.Double();
		arc.x = h - r;
		arc.y = k - r;
		arc.width = r + r;
		arc.height = r + r;
		arc.start = a1;
		if ((a1 <= a2 && a2 <= a3) || (a3 <= a2 && a2 <= a1)) {
			arc.extent = a3 - a1;
		} else if (a3 <= a1) {
			arc.extent = a3 - a1 + 360;
		} else {
			arc.extent = a3 - a1 - 360;
		}
		p.append(arc, true);
	}
	private static double arcdet(double a, double b, double c, double d, double e, double f, double g, double h, double i) {
		return a*e*i + b*f*g + c*d*h - a*f*h - b*d*i - c*e*g;
	}
	private static double arcHK(double x1, double y1, double x2, double y2, double x3, double y3) {
		return arcdet(x1, y1, 1, x2, y2, 1, x3, y3, 1) * 2;
	}
	private static double arcH(double x1, double y1, double x2, double y2, double x3, double y3) {
		return arcdet(x1*x1 + y1*y1, y1, 1, x2*x2 + y2*y2, y2, 1, x3*x3 + y3*y3, y3, 1);
	}
	private static double arcK(double x1, double y1, double x2, double y2, double x3, double y3) {
		return arcdet(x1, x1*x1 + y1*y1, 1, x2, x2*x2 + y2*y2, 1, x3, x3*x3 + y3*y3, 1);
	}
}
