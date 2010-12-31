/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.io;

import java.awt.*;
import java.awt.geom.*;
import java.io.*;

/* This more or less covers the java.awt.geom package. */
/* This and AWTSerializer more or less cover the java.awt package. */
public class ShapeSerializer extends Serializer {
	private static final int TYPE_AFFINE_TRANSFORM = fcc("AfTx");
	private static final int TYPE_ARC2D_DOUBLE = fcc("ArcD");
	private static final int TYPE_ARC2D_FLOAT = fcc("ArcF");
	private static final int TYPE_AREA = fcc("Area");
	private static final int TYPE_CUBIC_CURVE_DOUBLE = fcc("CubD");
	private static final int TYPE_CUBIC_CURVE_FLOAT = fcc("CubF");
	private static final int TYPE_DIMENSION = fcc("Dime");
	private static final int TYPE_ELLIPSE_DOUBLE = fcc("EllD");
	private static final int TYPE_ELLIPSE_FLOAT = fcc("EllF");
	private static final int TYPE_GENERAL_PATH = fcc("Path");
	private static final int TYPE_LINE2D_DOUBLE = fcc("LinD");
	private static final int TYPE_LINE2D_FLOAT = fcc("LinF");
	private static final int TYPE_POINT = fcc("Poin");
	private static final int TYPE_POINT_DOUBLE = fcc("PoiD");
	private static final int TYPE_POINT_FLOAT = fcc("PoiF");
	private static final int TYPE_POLYGON = fcc("Poly");
	private static final int TYPE_QUAD_CURVE_DOUBLE = fcc("QuaD");
	private static final int TYPE_QUAD_CURVE_FLOAT = fcc("QuaF");
	private static final int TYPE_RECTANGLE = fcc("Rect");
	private static final int TYPE_RECTANGLE_DOUBLE = fcc("RecD");
	private static final int TYPE_RECTANGLE_FLOAT = fcc("RecF");
	private static final int TYPE_ROUND_RECT_DOUBLE = fcc("RReD");
	private static final int TYPE_ROUND_RECT_FLOAT = fcc("RReF");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_AFFINE_TRANSFORM, 1, AffineTransform.class);
		addTypeAndClass(TYPE_ARC2D_DOUBLE, 1, Arc2D.Double.class);
		addTypeAndClass(TYPE_ARC2D_FLOAT, 1, Arc2D.Float.class);
		addTypeAndClass(TYPE_AREA, 1, Area.class);
		addTypeAndClass(TYPE_CUBIC_CURVE_DOUBLE, 1, CubicCurve2D.Double.class);
		addTypeAndClass(TYPE_CUBIC_CURVE_FLOAT, 1, CubicCurve2D.Float.class);
		addTypeAndClass(TYPE_DIMENSION, 1, Dimension.class);
		addTypeAndClass(TYPE_ELLIPSE_DOUBLE, 1, Ellipse2D.Double.class);
		addTypeAndClass(TYPE_ELLIPSE_FLOAT, 1, Ellipse2D.Float.class);
		addTypeAndClass(TYPE_GENERAL_PATH, 1, GeneralPath.class);
		addTypeAndClass(TYPE_LINE2D_DOUBLE, 1, Line2D.Double.class);
		addTypeAndClass(TYPE_LINE2D_FLOAT, 1, Line2D.Float.class);
		addTypeAndClass(TYPE_POINT, 1, Point.class);
		addTypeAndClass(TYPE_POINT_DOUBLE, 1, Point2D.Double.class);
		addTypeAndClass(TYPE_POINT_FLOAT, 1, Point2D.Float.class);
		addTypeAndClass(TYPE_POLYGON, 1, Polygon.class);
		addTypeAndClass(TYPE_QUAD_CURVE_DOUBLE, 1, QuadCurve2D.Double.class);
		addTypeAndClass(TYPE_QUAD_CURVE_FLOAT, 1, QuadCurve2D.Float.class);
		addTypeAndClass(TYPE_RECTANGLE, 1, Rectangle.class);
		addTypeAndClass(TYPE_RECTANGLE_DOUBLE, 1, Rectangle2D.Double.class);
		addTypeAndClass(TYPE_RECTANGLE_FLOAT, 1, Rectangle2D.Float.class);
		addTypeAndClass(TYPE_ROUND_RECT_DOUBLE, 1, RoundRectangle2D.Double.class);
		addTypeAndClass(TYPE_ROUND_RECT_FLOAT, 1, RoundRectangle2D.Float.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof AffineTransform) {
			AffineTransform v = (AffineTransform)o;
			double[] m = new double[6];
			v.getMatrix(m);
			for (int i = 0; i < 6; i++) {
				stream.writeDouble(m[i]);
			}
		}
		else if (o instanceof Arc2D.Double) {
			Arc2D.Double v = (Arc2D.Double)o;
			stream.writeDouble(v.x);
			stream.writeDouble(v.y);
			stream.writeDouble(v.width);
			stream.writeDouble(v.height);
			stream.writeDouble(v.start);
			stream.writeDouble(v.extent);
			stream.writeInt(v.getArcType());
		}
		else if (o instanceof Arc2D.Float) {
			Arc2D.Float v = (Arc2D.Float)o;
			stream.writeFloat(v.x);
			stream.writeFloat(v.y);
			stream.writeFloat(v.width);
			stream.writeFloat(v.height);
			stream.writeFloat(v.start);
			stream.writeFloat(v.extent);
			stream.writeInt(v.getArcType());
		}
		else if (o instanceof Area) {
			Area v = (Area)o;
			PathIterator i = v.getPathIterator(null);
			while (!i.isDone()) {
				float[] coords = new float[6];
				int type = i.currentSegment(coords);
				int wind = i.getWindingRule();
				int n = 0;
				switch (type) {
				case PathIterator.SEG_CLOSE: stream.writeShort(0x436C); n = 0; break;
				case PathIterator.SEG_CUBICTO: stream.writeShort(0x4375); n = 6; break;
				case PathIterator.SEG_LINETO: stream.writeShort(0x4C69); n = 2; break;
				case PathIterator.SEG_MOVETO: stream.writeShort(0x4D6F); n = 2; break;
				case PathIterator.SEG_QUADTO: stream.writeShort(0x5175); n = 4; break;
				default: stream.writeShort(0x3F3F); n = 0; break;
				}
				switch (wind) {
				case PathIterator.WIND_EVEN_ODD: stream.writeShort(0x454F); break;
				case PathIterator.WIND_NON_ZERO: stream.writeShort(0x4E5A); break;
				default: stream.writeShort(0x3F3F); break;
				}
				stream.writeInt(n);
				for (int j = 0; j < n; j++) {
					stream.writeFloat(coords[j]);
				}
				i.next();
			}
			stream.writeShort(-1);
			stream.writeShort(-1);
			stream.writeInt(-1);
		}
		else if (o instanceof CubicCurve2D.Double) {
			CubicCurve2D.Double v = (CubicCurve2D.Double)o;
			stream.writeDouble(v.x1);
			stream.writeDouble(v.y1);
			stream.writeDouble(v.ctrlx1);
			stream.writeDouble(v.ctrly1);
			stream.writeDouble(v.ctrlx2);
			stream.writeDouble(v.ctrly2);
			stream.writeDouble(v.x2);
			stream.writeDouble(v.y2);
		}
		else if (o instanceof CubicCurve2D.Float) {
			CubicCurve2D.Float v = (CubicCurve2D.Float)o;
			stream.writeFloat(v.x1);
			stream.writeFloat(v.y1);
			stream.writeFloat(v.ctrlx1);
			stream.writeFloat(v.ctrly1);
			stream.writeFloat(v.ctrlx2);
			stream.writeFloat(v.ctrly2);
			stream.writeFloat(v.x2);
			stream.writeFloat(v.y2);
		}
		else if (o instanceof Dimension) {
			Dimension v = (Dimension)o;
			stream.writeInt(v.width);
			stream.writeInt(v.height);
		}
		else if (o instanceof Ellipse2D.Double) {
			Ellipse2D.Double v = (Ellipse2D.Double)o;
			stream.writeDouble(v.x);
			stream.writeDouble(v.y);
			stream.writeDouble(v.width);
			stream.writeDouble(v.height);
		}
		else if (o instanceof Ellipse2D.Float) {
			Ellipse2D.Float v = (Ellipse2D.Float)o;
			stream.writeFloat(v.x);
			stream.writeFloat(v.y);
			stream.writeFloat(v.width);
			stream.writeFloat(v.height);
		}
		else if (o instanceof GeneralPath) {
			GeneralPath v = (GeneralPath)o;
			PathIterator i = v.getPathIterator(null);
			while (!i.isDone()) {
				float[] coords = new float[6];
				int type = i.currentSegment(coords);
				int wind = i.getWindingRule();
				int n = 0;
				switch (type) {
				case PathIterator.SEG_CLOSE: stream.writeShort(0x436C); n = 0; break;
				case PathIterator.SEG_CUBICTO: stream.writeShort(0x4375); n = 6; break;
				case PathIterator.SEG_LINETO: stream.writeShort(0x4C69); n = 2; break;
				case PathIterator.SEG_MOVETO: stream.writeShort(0x4D6F); n = 2; break;
				case PathIterator.SEG_QUADTO: stream.writeShort(0x5175); n = 4; break;
				default: stream.writeShort(0x3F3F); n = 0; break;
				}
				switch (wind) {
				case PathIterator.WIND_EVEN_ODD: stream.writeShort(0x454F); break;
				case PathIterator.WIND_NON_ZERO: stream.writeShort(0x4E5A); break;
				default: stream.writeShort(0x3F3F); break;
				}
				stream.writeInt(n);
				for (int j = 0; j < n; j++) {
					stream.writeFloat(coords[j]);
				}
				i.next();
			}
			stream.writeShort(-1);
			stream.writeShort(-1);
			stream.writeInt(-1);
		}
		else if (o instanceof Line2D.Double) {
			Line2D.Double v = (Line2D.Double)o;
			stream.writeDouble(v.x1);
			stream.writeDouble(v.y1);
			stream.writeDouble(v.x2);
			stream.writeDouble(v.y2);
		}
		else if (o instanceof Line2D.Float) {
			Line2D.Float v = (Line2D.Float)o;
			stream.writeFloat(v.x1);
			stream.writeFloat(v.y1);
			stream.writeFloat(v.x2);
			stream.writeFloat(v.y2);
		}
		else if (o instanceof Point) {
			Point v = (Point)o;
			stream.writeInt(v.x);
			stream.writeInt(v.y);
		}
		else if (o instanceof Point2D.Double) {
			Point2D.Double v = (Point2D.Double)o;
			stream.writeDouble(v.x);
			stream.writeDouble(v.y);
		}
		else if (o instanceof Point2D.Float) {
			Point2D.Float v = (Point2D.Float)o;
			stream.writeFloat(v.x);
			stream.writeFloat(v.y);
		}
		else if (o instanceof Polygon) {
			Polygon v = (Polygon)o;
			stream.writeInt(v.npoints);
			for (int i = 0; i < v.npoints; i++) {
				stream.writeInt(v.xpoints[i]);
				stream.writeInt(v.ypoints[i]);
			}
		}
		else if (o instanceof QuadCurve2D.Double) {
			QuadCurve2D.Double v = (QuadCurve2D.Double)o;
			stream.writeDouble(v.x1);
			stream.writeDouble(v.y1);
			stream.writeDouble(v.ctrlx);
			stream.writeDouble(v.ctrly);
			stream.writeDouble(v.x2);
			stream.writeDouble(v.y2);
		}
		else if (o instanceof QuadCurve2D.Float) {
			QuadCurve2D.Float v = (QuadCurve2D.Float)o;
			stream.writeFloat(v.x1);
			stream.writeFloat(v.y1);
			stream.writeFloat(v.ctrlx);
			stream.writeFloat(v.ctrly);
			stream.writeFloat(v.x2);
			stream.writeFloat(v.y2);
		}
		else if (o instanceof Rectangle) {
			Rectangle v = (Rectangle)o;
			stream.writeInt(v.x);
			stream.writeInt(v.y);
			stream.writeInt(v.width);
			stream.writeInt(v.height);
		}
		else if (o instanceof Rectangle2D.Double) {
			Rectangle2D.Double v = (Rectangle2D.Double)o;
			stream.writeDouble(v.x);
			stream.writeDouble(v.y);
			stream.writeDouble(v.width);
			stream.writeDouble(v.height);
		}
		else if (o instanceof Rectangle2D.Float) {
			Rectangle2D.Float v = (Rectangle2D.Float)o;
			stream.writeFloat(v.x);
			stream.writeFloat(v.y);
			stream.writeFloat(v.width);
			stream.writeFloat(v.height);
		}
		else if (o instanceof RoundRectangle2D.Double) {
			RoundRectangle2D.Double v = (RoundRectangle2D.Double)o;
			stream.writeDouble(v.x);
			stream.writeDouble(v.y);
			stream.writeDouble(v.width);
			stream.writeDouble(v.height);
			stream.writeDouble(v.arcwidth);
			stream.writeDouble(v.archeight);
		}
		else if (o instanceof RoundRectangle2D.Float) {
			RoundRectangle2D.Float v = (RoundRectangle2D.Float)o;
			stream.writeFloat(v.x);
			stream.writeFloat(v.y);
			stream.writeFloat(v.width);
			stream.writeFloat(v.height);
			stream.writeFloat(v.arcwidth);
			stream.writeFloat(v.archeight);
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (version != 1) throw new IOException("Invalid version number.");
		else if (type == TYPE_AFFINE_TRANSFORM) {
			double[] m = new double[6];
			for (int i = 0; i < 6; i++) {
				m[i] = stream.readDouble();
			}
			return new AffineTransform(m);
		}
		else if (type == TYPE_ARC2D_DOUBLE) {
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			double s = stream.readDouble();
			double e = stream.readDouble();
			int t = stream.readInt();
			return new Arc2D.Double(x,y,w,h,s,e,t);
		}
		else if (type == TYPE_ARC2D_FLOAT) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			float s = stream.readFloat();
			float e = stream.readFloat();
			int t = stream.readInt();
			return new Arc2D.Float(x,y,w,h,s,e,t);
		}
		else if (type == TYPE_AREA) {
			GeneralPath p = new GeneralPath();
			while (true) {
				short t = stream.readShort();
				if (t < 0) break;
				short w = stream.readShort();
				if (t < 0) break;
				int n = stream.readInt();
				if (n < 0) break;
				float[] c = new float[n];
				for (int i = 0; i < n; i++) {
					c[i] = stream.readFloat();
				}
				switch (t) {
				case 0x436C: p.closePath(); break;
				case 0x4375: p.curveTo(c[0], c[1], c[2], c[3], c[4], c[5]); break;
				case 0x4C69: p.lineTo(c[0], c[1]); break;
				case 0x4D6F: p.moveTo(c[0], c[1]); break;
				case 0x5175: p.quadTo(c[0], c[1], c[2], c[3]); break;
				}
				switch (w) {
				case 0x454F: p.setWindingRule(GeneralPath.WIND_EVEN_ODD); break;
				case 0x4E5A: p.setWindingRule(GeneralPath.WIND_NON_ZERO); break;
				}
			}
			return new Area(p);
		}
		else if (type == TYPE_CUBIC_CURVE_DOUBLE) {
			double x1 = stream.readDouble();
			double y1 = stream.readDouble();
			double cx1 = stream.readDouble();
			double cy1 = stream.readDouble();
			double cx2 = stream.readDouble();
			double cy2 = stream.readDouble();
			double x2 = stream.readDouble();
			double y2 = stream.readDouble();
			return new CubicCurve2D.Double(x1,y1,cx1,cy1,cx2,cy2,x2,y2);
		}
		else if (type == TYPE_CUBIC_CURVE_FLOAT) {
			float x1 = stream.readFloat();
			float y1 = stream.readFloat();
			float cx1 = stream.readFloat();
			float cy1 = stream.readFloat();
			float cx2 = stream.readFloat();
			float cy2 = stream.readFloat();
			float x2 = stream.readFloat();
			float y2 = stream.readFloat();
			return new CubicCurve2D.Float(x1,y1,cx1,cy1,cx2,cy2,x2,y2);
		}
		else if (type == TYPE_DIMENSION) {
			int w = stream.readInt();
			int h = stream.readInt();
			return new Dimension(w,h);
		}
		else if (type == TYPE_ELLIPSE_DOUBLE) {
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			return new Ellipse2D.Double(x,y,w,h);
		}
		else if (type == TYPE_ELLIPSE_FLOAT) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			return new Ellipse2D.Float(x,y,w,h);
		}
		else if (type == TYPE_GENERAL_PATH) {
			GeneralPath p = new GeneralPath();
			while (true) {
				short t = stream.readShort();
				if (t < 0) break;
				short w = stream.readShort();
				if (t < 0) break;
				int n = stream.readInt();
				if (n < 0) break;
				float[] c = new float[n];
				for (int i = 0; i < n; i++) {
					c[i] = stream.readFloat();
				}
				switch (t) {
				case 0x436C: p.closePath(); break;
				case 0x4375: p.curveTo(c[0], c[1], c[2], c[3], c[4], c[5]); break;
				case 0x4C69: p.lineTo(c[0], c[1]); break;
				case 0x4D6F: p.moveTo(c[0], c[1]); break;
				case 0x5175: p.quadTo(c[0], c[1], c[2], c[3]); break;
				}
				switch (w) {
				case 0x454F: p.setWindingRule(GeneralPath.WIND_EVEN_ODD); break;
				case 0x4E5A: p.setWindingRule(GeneralPath.WIND_NON_ZERO); break;
				}
			}
			return p;
		}
		else if (type == TYPE_LINE2D_DOUBLE) {
			double x1 = stream.readDouble();
			double y1 = stream.readDouble();
			double x2 = stream.readDouble();
			double y2 = stream.readDouble();
			return new Line2D.Double(x1,y1,x2,y2);
		}
		else if (type == TYPE_LINE2D_FLOAT) {
			float x1 = stream.readFloat();
			float y1 = stream.readFloat();
			float x2 = stream.readFloat();
			float y2 = stream.readFloat();
			return new Line2D.Float(x1,y1,x2,y2);
		}
		else if (type == TYPE_POINT) {
			int x = stream.readInt();
			int y = stream.readInt();
			return new Point(x,y);
		}
		else if (type == TYPE_POINT_DOUBLE) {
			double x = stream.readDouble();
			double y = stream.readDouble();
			return new Point2D.Double(x,y);
		}
		else if (type == TYPE_POINT_FLOAT) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			return new Point2D.Float(x,y);
		}
		else if (type == TYPE_POLYGON) {
			int n = stream.readInt();
			int[] x = new int[n];
			int[] y = new int[n];
			for (int i = 0; i < n; i++) {
				x[i] = stream.readInt();
				y[i] = stream.readInt();
			}
			return new Polygon(x, y, n);
		}
		else if (type == TYPE_QUAD_CURVE_DOUBLE) {
			double x1 = stream.readDouble();
			double y1 = stream.readDouble();
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double x2 = stream.readDouble();
			double y2 = stream.readDouble();
			return new QuadCurve2D.Double(x1,y1,cx,cy,x2,y2);
		}
		else if (type == TYPE_QUAD_CURVE_FLOAT) {
			float x1 = stream.readFloat();
			float y1 = stream.readFloat();
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float x2 = stream.readFloat();
			float y2 = stream.readFloat();
			return new QuadCurve2D.Float(x1,y1,cx,cy,x2,y2);
		}
		else if (type == TYPE_RECTANGLE) {
			int x = stream.readInt();
			int y = stream.readInt();
			int w = stream.readInt();
			int h = stream.readInt();
			return new Rectangle(x,y,w,h);
		}
		else if (type == TYPE_RECTANGLE_DOUBLE) {
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			return new Rectangle2D.Double(x,y,w,h);
		}
		else if (type == TYPE_RECTANGLE_FLOAT) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			return new Rectangle2D.Float(x,y,w,h);
		}
		else if (type == TYPE_ROUND_RECT_DOUBLE) {
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			double aw = stream.readDouble();
			double ah = stream.readDouble();
			return new RoundRectangle2D.Double(x,y,w,h,aw,ah);
		}
		else if (type == TYPE_ROUND_RECT_FLOAT) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			float aw = stream.readFloat();
			float ah = stream.readFloat();
			return new RoundRectangle2D.Float(x,y,w,h,aw,ah);
		}
		else return null;
	}
}
