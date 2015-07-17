package com.kreative.paint.io;

import java.awt.*;
import java.io.*;
import com.kreative.paint.pattern.*;
import com.kreative.paint.stroke.*;

public class CKPAWTSerializer extends Serializer {
	private static final int TYPE_PATTERN = fcc("Patt");
	private static final int TYPE_PATTERN_LIST = fcc("Pats");
	private static final int TYPE_PATTERN_PAINT = fcc("PPnt");
	private static final int TYPE_ARROWHEAD = fcc("Arhd");
	private static final int TYPE_ARROWHEAD_SHAPE_CIRCLE = fcc(">Cir");
	private static final int TYPE_ARROWHEAD_SHAPE_ELLIPSE = fcc(">Ell");
	private static final int TYPE_ARROWHEAD_SHAPE_LINE = fcc(">Lin");
	private static final int TYPE_ARROWHEAD_SHAPE_PATH = fcc(">Pth");
	private static final int TYPE_ARROWHEAD_SHAPE_POLYGON = fcc(">Ply");
	private static final int TYPE_ARROWHEAD_SHAPE_POLYLINE = fcc(">PLn");
	private static final int TYPE_ARROWHEAD_SHAPE_RECT = fcc(">Rec");
	private static final int TYPE_END_CAP = fcc("EdCp");
	private static final int TYPE_LINE_JOIN = fcc("LnJn");
	private static final int TYPE_POWERSTROKE = fcc("DStk");
	private static final int TYPE_STROKE_SET = fcc("Stks");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_PATTERN, 1, Pattern.class);
		addTypeAndClass(TYPE_PATTERN_LIST, 1, PatternList.class);
		addTypeAndClass(TYPE_PATTERN_PAINT, 2, PatternPaint.class);
		addTypeAndClass(TYPE_ARROWHEAD, 1, Arrowhead.class);
		addTypeAndClass(TYPE_ARROWHEAD_SHAPE_CIRCLE, 1, ArrowheadShape.Circle.class);
		addTypeAndClass(TYPE_ARROWHEAD_SHAPE_ELLIPSE, 1, ArrowheadShape.Ellipse.class);
		addTypeAndClass(TYPE_ARROWHEAD_SHAPE_LINE, 2, ArrowheadShape.Line.class);
		addTypeAndClass(TYPE_ARROWHEAD_SHAPE_PATH, 2, ArrowheadShape.Path.class);
		addTypeAndClass(TYPE_ARROWHEAD_SHAPE_POLYGON, 2, ArrowheadShape.Polygon.class);
		addTypeAndClass(TYPE_ARROWHEAD_SHAPE_POLYLINE, 2, ArrowheadShape.PolyLine.class);
		addTypeAndClass(TYPE_ARROWHEAD_SHAPE_RECT, 2, ArrowheadShape.Rect.class);
		addTypeAndClass(TYPE_END_CAP, 1, EndCap.class);
		addTypeAndClass(TYPE_LINE_JOIN, 1, LineJoin.class);
		addTypeAndClass(TYPE_POWERSTROKE, 2, PowerStroke.class);
		addTypeAndClass(TYPE_STROKE_SET, 1, StrokeSet.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof Pattern) {
			Pattern v = (Pattern)o;
			stream.writeInt(v.width);
			stream.writeInt(v.height);
			stream.writeInt(v.denominator);
			stream.writeInt(v.values.length);
			stream.writeBoolean(v.name != null);
			if (v.denominator < 256) {
				for (int value : v.values) {
					stream.writeByte(value);
				}
			} else if (v.denominator < 65536) {
				for (int value : v.values) {
					stream.writeShort(value);
				}
			} else {
				for (int value : v.values) {
					stream.writeInt(value);
				}
			}
			if (v.name != null) {
				stream.writeUTF(v.name);
			}
		} else if (o instanceof PatternList) {
			PatternList v = (PatternList)o;
			stream.writeInt(v.size());
			stream.writeBoolean(v.name != null);
			for (Pattern pattern : v) {
				SerializationManager.writeObject(pattern, stream);
			}
			if (v.name != null) {
				stream.writeUTF(v.name);
			}
		} else if (o instanceof PatternPaint) {
			PatternPaint v = (PatternPaint)o;
			SerializationManager.writeObject(v.pattern, stream);
			SerializationManager.writeObject(v.foreground, stream);
			SerializationManager.writeObject(v.background, stream);
		} else if (o instanceof Arrowhead) {
			Arrowhead v = (Arrowhead)o;
			stream.writeBoolean(v.scale);
			stream.writeInt(v.size());
			for (ArrowheadShape sh : v) {
				SerializationManager.writeObject(sh, stream);
			}
		} else if (o instanceof ArrowheadShape.Circle) {
			ArrowheadShape.Circle v = (ArrowheadShape.Circle)o;
			stream.writeBoolean(v.stroke);
			stream.writeBoolean(v.fill);
			stream.writeFloat(v.cx);
			stream.writeFloat(v.cy);
			stream.writeFloat(v.r);
		} else if (o instanceof ArrowheadShape.Ellipse) {
			ArrowheadShape.Ellipse v = (ArrowheadShape.Ellipse)o;
			stream.writeBoolean(v.stroke);
			stream.writeBoolean(v.fill);
			stream.writeFloat(v.cx);
			stream.writeFloat(v.cy);
			stream.writeFloat(v.rx);
			stream.writeFloat(v.ry);
		} else if (o instanceof ArrowheadShape.Line) {
			ArrowheadShape.Line v = (ArrowheadShape.Line)o;
			stream.writeBoolean(v.stroke);
			stream.writeBoolean(v.fill);
			stream.writeFloat(v.x1);
			stream.writeFloat(v.y1);
			stream.writeFloat(v.x2);
			stream.writeFloat(v.y2);
			stream.writeInt((v.endCap != null) ? v.endCap.awtValue : -1);
		} else if (o instanceof ArrowheadShape.Path) {
			ArrowheadShape.Path v = (ArrowheadShape.Path)o;
			stream.writeBoolean(v.stroke);
			stream.writeBoolean(v.fill);
			stream.writeUTF(v.d);
			stream.writeInt((v.endCap != null) ? v.endCap.awtValue : -1);
			stream.writeInt((v.lineJoin != null) ? v.lineJoin.awtValue : -1);
			stream.writeFloat(v.miterLimit);
		} else if (o instanceof ArrowheadShape.Polygon) {
			ArrowheadShape.Polygon v = (ArrowheadShape.Polygon)o;
			stream.writeBoolean(v.stroke);
			stream.writeBoolean(v.fill);
			stream.writeInt(v.points.length);
			for (float p : v.points) {
				stream.writeFloat(p);
			}
			stream.writeInt((v.endCap != null) ? v.endCap.awtValue : -1);
			stream.writeInt((v.lineJoin != null) ? v.lineJoin.awtValue : -1);
			stream.writeFloat(v.miterLimit);
		} else if (o instanceof ArrowheadShape.PolyLine) {
			ArrowheadShape.PolyLine v = (ArrowheadShape.PolyLine)o;
			stream.writeBoolean(v.stroke);
			stream.writeBoolean(v.fill);
			stream.writeInt(v.points.length);
			for (float p : v.points) {
				stream.writeFloat(p);
			}
			stream.writeInt((v.endCap != null) ? v.endCap.awtValue : -1);
			stream.writeInt((v.lineJoin != null) ? v.lineJoin.awtValue : -1);
			stream.writeFloat(v.miterLimit);
		} else if (o instanceof ArrowheadShape.Rect) {
			ArrowheadShape.Rect v = (ArrowheadShape.Rect)o;
			stream.writeBoolean(v.stroke);
			stream.writeBoolean(v.fill);
			stream.writeFloat(v.x);
			stream.writeFloat(v.y);
			stream.writeFloat(v.width);
			stream.writeFloat(v.height);
			stream.writeFloat(v.rx);
			stream.writeFloat(v.ry);
			stream.writeInt((v.lineJoin != null) ? v.lineJoin.awtValue : -1);
			stream.writeFloat(v.miterLimit);
		} else if (o instanceof EndCap) {
			EndCap v = (EndCap)o;
			stream.writeInt(v.awtValue);
		} else if (o instanceof LineJoin) {
			LineJoin v = (LineJoin)o;
			stream.writeInt(v.awtValue);
		} else if (o instanceof PowerStroke) {
			PowerStroke v = (PowerStroke)o;
			stream.writeFloat(v.lineWidth);
			stream.writeInt((v.endCap != null) ? v.endCap.awtValue : -1);
			stream.writeInt((v.lineJoin != null) ? v.lineJoin.awtValue : -1);
			stream.writeFloat(v.miterLimit);
			stream.writeInt(v.multiplicity);
			stream.writeFloat(v.dashPhase);
			if (v.dashArray == null) {
				stream.writeInt(-1);
			} else {
				stream.writeInt(v.dashArray.length);
				for (int i = 0; i < v.dashArray.length; i++) {
					stream.writeFloat(v.dashArray[i]);
				}
			}
			SerializationManager.writeObject(v.arrowOnStart, stream);
			SerializationManager.writeObject(v.arrowOnEnd, stream);
			stream.writeBoolean(v.name != null);
			if (v.name != null) {
				stream.writeUTF(v.name);
			}
		} else if (o instanceof StrokeSet) {
			StrokeSet v = (StrokeSet)o;
			stream.writeInt(v.strokes.size());
			stream.writeInt(v.widths.size());
			stream.writeInt(v.multiplicities.size());
			stream.writeInt(v.dashes.size());
			stream.writeInt(v.arrowheads.size());
			stream.writeBoolean(v.name != null);
			if (v.name != null) {
				stream.writeUTF(v.name);
			}
			for (PowerStroke s : v.strokes) {
				SerializationManager.writeObject(s, stream);
			}
			for (float w : v.widths) {
				stream.writeFloat(w);
			}
			for (int m : v.multiplicities) {
				stream.writeInt(m);
			}
			for (float[] d : v.dashes) {
				if (d == null) {
					stream.writeInt(-1);
				} else {
					stream.writeInt(d.length);
					for (int i = 0; i < d.length; i++) {
						stream.writeFloat(d[i]);
					}
				}
			}
			for (Arrowhead a : v.arrowheads) {
				SerializationManager.writeObject(a, stream);
			}
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (type == TYPE_PATTERN) {
			if (version != 1) throw new IOException("Invalid version number.");
			int w = stream.readInt();
			int h = stream.readInt();
			int d = stream.readInt();
			int l = stream.readInt();
			boolean n = stream.readBoolean();
			int[] v = new int[l];
			if (d < 256) {
				for (int i = 0; i < l; i++) {
					v[i] = stream.readUnsignedByte();
				}
			} else if (d < 65536) {
				for (int i = 0; i < l; i++) {
					v[i] = stream.readUnsignedShort();
				}
			} else {
				for (int i = 0; i < l; i++) {
					v[i] = stream.readInt();
				}
			}
			String name = n ? stream.readUTF() : null;
			Pattern pattern = new Pattern(w, h, d, name);
			for (int i = 0; i < l; i++) {
				pattern.values[i] = v[i];
			}
			return pattern;
		} else if (type == TYPE_PATTERN_LIST) {
			if (version != 1) throw new IOException("Invalid version number.");
			int l = stream.readInt();
			boolean n = stream.readBoolean();
			Pattern[] p = new Pattern[l];
			for (int i = 0; i < l; i++) {
				p[i] = (Pattern)SerializationManager.readObject(stream);
			}
			String name = n ? stream.readUTF() : null;
			PatternList list = new PatternList(name);
			for (Pattern pp : p) list.add(pp);
			return list;
		} else if (type == TYPE_PATTERN_PAINT) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			Pattern patt;
			if (version < 2) {
				patt = new Pattern(stream.readLong(), null);
			} else {
				patt = (Pattern)SerializationManager.readObject(stream);
			}
			Paint fg = (Paint)SerializationManager.readObject(stream);
			Paint bg = (Paint)SerializationManager.readObject(stream);
			return new PatternPaint(fg, bg, patt);
		} else if (type == TYPE_ARROWHEAD) {
			if (version != 1) throw new IOException("Invalid version number.");
			boolean scale = stream.readBoolean();
			int n = stream.readInt();
			Arrowhead a = new Arrowhead(scale);
			for (int i = 0; i < n; i++) {
				a.add((ArrowheadShape)SerializationManager.readObject(stream));
			}
			return a;
		} else if (type == TYPE_ARROWHEAD_SHAPE_CIRCLE) {
			if (version != 1) throw new IOException("Invalid version number.");
			boolean stroke = stream.readBoolean();
			boolean fill = stream.readBoolean();
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float r = stream.readFloat();
			return new ArrowheadShape.Circle(cx, cy, r, stroke, fill);
		} else if (type == TYPE_ARROWHEAD_SHAPE_ELLIPSE) {
			if (version != 1) throw new IOException("Invalid version number.");
			boolean stroke = stream.readBoolean();
			boolean fill = stream.readBoolean();
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float rx = stream.readFloat();
			float ry = stream.readFloat();
			return new ArrowheadShape.Ellipse(cx, cy, rx, ry, stroke, fill);
		} else if (type == TYPE_ARROWHEAD_SHAPE_LINE) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			boolean stroke = stream.readBoolean();
			boolean fill = stream.readBoolean();
			float x1 = stream.readFloat();
			float y1 = stream.readFloat();
			float x2 = stream.readFloat();
			float y2 = stream.readFloat();
			EndCap endCap = (version >= 2) ? EndCap.forAWTValue(stream.readInt()) : EndCap.SQUARE;
			return new ArrowheadShape.Line(x1, y1, x2, y2, endCap, stroke, fill);
		} else if (type == TYPE_ARROWHEAD_SHAPE_PATH) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			boolean stroke = stream.readBoolean();
			boolean fill = stream.readBoolean();
			String d = stream.readUTF();
			EndCap endCap = (version >= 2) ? EndCap.forAWTValue(stream.readInt()) : EndCap.SQUARE;
			LineJoin lineJoin = (version >= 2) ? LineJoin.forAWTValue(stream.readInt()) : LineJoin.MITER;
			float miterLimit = (version >= 2) ? stream.readFloat() : 10.0f;
			return new ArrowheadShape.Path(d, endCap, lineJoin, miterLimit, stroke, fill);
		} else if (type == TYPE_ARROWHEAD_SHAPE_POLYGON) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			boolean stroke = stream.readBoolean();
			boolean fill = stream.readBoolean();
			int n = stream.readInt();
			float[] p = new float[n];
			for (int i = 0; i < n; i++) {
				p[i] = stream.readFloat();
			}
			EndCap endCap = (version >= 2) ? EndCap.forAWTValue(stream.readInt()) : EndCap.SQUARE;
			LineJoin lineJoin = (version >= 2) ? LineJoin.forAWTValue(stream.readInt()) : LineJoin.MITER;
			float miterLimit = (version >= 2) ? stream.readFloat() : 10.0f;
			return new ArrowheadShape.Polygon(p, endCap, lineJoin, miterLimit, stroke, fill);
		} else if (type == TYPE_ARROWHEAD_SHAPE_POLYLINE) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			boolean stroke = stream.readBoolean();
			boolean fill = stream.readBoolean();
			int n = stream.readInt();
			float[] p = new float[n];
			for (int i = 0; i < n; i++) {
				p[i] = stream.readFloat();
			}
			EndCap endCap = (version >= 2) ? EndCap.forAWTValue(stream.readInt()) : EndCap.SQUARE;
			LineJoin lineJoin = (version >= 2) ? LineJoin.forAWTValue(stream.readInt()) : LineJoin.MITER;
			float miterLimit = (version >= 2) ? stream.readFloat() : 10.0f;
			return new ArrowheadShape.PolyLine(p, endCap, lineJoin, miterLimit, stroke, fill);
		} else if (type == TYPE_ARROWHEAD_SHAPE_RECT) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			boolean stroke = stream.readBoolean();
			boolean fill = stream.readBoolean();
			float x = stream.readFloat();
			float y = stream.readFloat();
			float width = stream.readFloat();
			float height = stream.readFloat();
			float rx = stream.readFloat();
			float ry = stream.readFloat();
			LineJoin lineJoin = (version >= 2) ? LineJoin.forAWTValue(stream.readInt()) : LineJoin.MITER;
			float miterLimit = (version >= 2) ? stream.readFloat() : 10.0f;
			return new ArrowheadShape.Rect(x, y, width, height, rx, ry, lineJoin, miterLimit, stroke, fill);
		} else if (type == TYPE_END_CAP) {
			if (version != 1) throw new IOException("Invalid version number.");
			return EndCap.forAWTValue(stream.readInt());
		} else if (type == TYPE_LINE_JOIN) {
			if (version != 1) throw new IOException("Invalid version number.");
			return LineJoin.forAWTValue(stream.readInt());
		} else if (type == TYPE_POWERSTROKE) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			float width = stream.readFloat();
			EndCap endCap = EndCap.forAWTValue(stream.readInt());
			LineJoin lineJoin = LineJoin.forAWTValue(stream.readInt());
			float miterLimit = stream.readFloat();
			int multiplicity = stream.readInt();
			float dashPhase = stream.readFloat();
			int n = stream.readInt();
			float[] dashArray = ((n < 0) ? null : new float[n]);
			for (int i = 0; i < n; i++) {
				dashArray[i] = stream.readFloat();
			}
			Arrowhead arrowOnStart = (Arrowhead)SerializationManager.readObject(stream);
			Arrowhead arrowOnEnd = (Arrowhead)SerializationManager.readObject(stream);
			boolean hasName = (version >= 2) ? stream.readBoolean() : false;
			String name = hasName ? stream.readUTF() : null;
			return new PowerStroke(
				width, multiplicity,
				dashArray, dashPhase,
				arrowOnStart, arrowOnEnd,
				endCap, lineJoin, miterLimit,
				name
			);
		} else if (type == TYPE_STROKE_SET) {
			if (version != 1) throw new IOException("Invalid version number.");
			int ns = stream.readInt();
			int nw = stream.readInt();
			int nm = stream.readInt();
			int nd = stream.readInt();
			int na = stream.readInt();
			boolean n = stream.readBoolean();
			String name = n ? stream.readUTF() : null;
			StrokeSet ss = new StrokeSet(name);
			for (int i = 0; i < ns; i++) {
				ss.strokes.add((PowerStroke)SerializationManager.readObject(stream));
			}
			for (int i = 0; i < nw; i++) {
				ss.widths.add(stream.readFloat());
			}
			for (int i = 0; i < nm; i++) {
				ss.multiplicities.add(stream.readInt());
			}
			for (int i = 0; i < nd; i++) {
				int l = stream.readInt();
				float[] d = ((l < 0) ? null : new float[l]);
				for (int j = 0; j < l; j++) {
					d[j] = stream.readFloat();
				}
				ss.dashes.add(d);
			}
			for (int i = 0; i < na; i++) {
				ss.arrowheads.add((Arrowhead)SerializationManager.readObject(stream));
			}
			return ss;
		} else {
			return null;
		}
	}
}
