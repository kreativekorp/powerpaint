package com.kreative.paint.io;

import java.awt.Shape;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import com.kreative.paint.document.draw.Path;
import com.kreative.paint.document.draw.PathContour;
import com.kreative.paint.document.draw.PathPoint;
import com.kreative.paint.geom.*;
import com.kreative.paint.material.shape.*;

public class CKPGeomSerializer extends Serializer {
	private static final int TYPE_BITMAP_SHAPE = fcc("BmSh");
	private static final int TYPE_CYCLOID = fcc("Cycl");
	private static final int TYPE_FLOWER = fcc("Flow");
	private static final int TYPE_REGULAR_POLYGON = fcc("RPly");
	private static final int TYPE_RIGHT_ARC = fcc("ArcR");
	private static final int TYPE_SCALED_SHAPE = fcc("ScSh");
	private static final int TYPE_SPIRAL = fcc("Spir");
	private static final int TYPE_PARAMETER = fcc("pPrm");
	private static final int TYPE_PARAMETERIZED_PATH = fcc("pPth");
	private static final int TYPE_PARAMETERIZED_POINT = fcc("pPnt");
	private static final int TYPE_PARAMETERIZED_SHAPE_ARC = fcc("pArc");
	private static final int TYPE_PARAMETERIZED_SHAPE_CIRCLE = fcc("pCir");
	private static final int TYPE_PARAMETERIZED_SHAPE_ELLIPSE = fcc("pEll");
	private static final int TYPE_PARAMETERIZED_SHAPE_LINE = fcc("pLin");
	private static final int TYPE_PARAMETERIZED_SHAPE_POLYGON = fcc("pPly");
	private static final int TYPE_PARAMETERIZED_SHAPE_POLYLINE = fcc("pPLn");
	private static final int TYPE_PARAMETERIZED_SHAPE_RECT = fcc("pRec");
	private static final int TYPE_PARAMETERIZED_VALUE = fcc("pVal");
	private static final int TYPE_PATH = fcc("^Pth");
	private static final int TYPE_PATH_CONTOUR = fcc("^Cnt");
	private static final int TYPE_PATH_POINT = fcc("^Pnt");
	private static final int TYPE_POWERSHAPE = fcc("pShp");
	private static final int TYPE_POWERSHAPE_LIST = fcc("SLst");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BITMAP_SHAPE, 1, BitmapShape.class);
		addTypeAndClass(TYPE_CYCLOID, 1, Cycloid.class);
		addTypeAndClass(TYPE_FLOWER, 1, Flower.class);
		addTypeAndClass(TYPE_REGULAR_POLYGON, 1, RegularPolygon.class);
		addTypeAndClass(TYPE_RIGHT_ARC, 1, RightArc.class);
		addTypeAndClass(TYPE_SCALED_SHAPE, 1, ScaledShape.class);
		addTypeAndClass(TYPE_SPIRAL, 1, Spiral.class);
		addTypeAndClass(TYPE_PARAMETER, 1, Parameter.class);
		addTypeAndClass(TYPE_PARAMETERIZED_PATH, 2, ParameterizedPath.class);
		addTypeAndClass(TYPE_PARAMETERIZED_POINT, 1, ParameterizedPoint.class);
		addTypeAndClass(TYPE_PARAMETERIZED_SHAPE_ARC, 1, ParameterizedShape.Arc.class);
		addTypeAndClass(TYPE_PARAMETERIZED_SHAPE_CIRCLE, 1, ParameterizedShape.Circle.class);
		addTypeAndClass(TYPE_PARAMETERIZED_SHAPE_ELLIPSE, 1, ParameterizedShape.Ellipse.class);
		addTypeAndClass(TYPE_PARAMETERIZED_SHAPE_LINE, 1, ParameterizedShape.Line.class);
		addTypeAndClass(TYPE_PARAMETERIZED_SHAPE_POLYGON, 1, ParameterizedShape.Polygon.class);
		addTypeAndClass(TYPE_PARAMETERIZED_SHAPE_POLYLINE, 1, ParameterizedShape.PolyLine.class);
		addTypeAndClass(TYPE_PARAMETERIZED_SHAPE_RECT, 1, ParameterizedShape.Rect.class);
		addTypeAndClass(TYPE_PARAMETERIZED_VALUE, 1, ParameterizedValue.class);
		addTypeAndClass(TYPE_PATH, 1, Path.class);
		addTypeAndClass(TYPE_PATH_CONTOUR, 1, PathContour.class);
		addTypeAndClass(TYPE_PATH_POINT, 1, PathPoint.class);
		addTypeAndClass(TYPE_POWERSHAPE, 2, PowerShape.class);
		addTypeAndClass(TYPE_POWERSHAPE_LIST, 1, PowerShapeList.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof BitmapShape) {
			BitmapShape v = (BitmapShape)o;
			stream.writeInt(v.getX());
			stream.writeInt(v.getY());
			stream.writeInt(v.getWidth());
			stream.writeInt(v.getHeight());
			int[] rgb = v.getBitmap();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DeflaterOutputStream dos = new DeflaterOutputStream(bos);
			for (int p : rgb) dos.write((p >> 24) & 0xFF);
			for (int p : rgb) dos.write((p >> 16) & 0xFF);
			for (int p : rgb) dos.write((p >> 8) & 0xFF);
			for (int p : rgb) dos.write(p & 0xFF);
			dos.finish();
			dos.close();
			bos.close();
			byte crgb[] = bos.toByteArray();
			stream.writeInt(crgb.length);
			stream.write(crgb);
		} else if (o instanceof Cycloid) {
			Cycloid v = (Cycloid)o;
			stream.writeInt(v.isEpicycloid() ? 1 : 0);
			stream.writeInt(v.getSmoothness());
			stream.writeInt(v.getBegin());
			stream.writeInt(v.getEnd());
			stream.writeDouble(v.getR());
			stream.writeDouble(v.getr());
			stream.writeDouble(v.getd());
			stream.writeFloat(v.getCenter().x);
			stream.writeFloat(v.getCenter().y);
			stream.writeFloat(v.getEndpoint().x);
			stream.writeFloat(v.getEndpoint().y);
		} else if (o instanceof Flower) {
			Flower v = (Flower)o;
			stream.writeInt(v.getPetals());
			stream.writeDouble(v.getWidth());
			stream.writeInt(v.getSmoothness());
			stream.writeInt(v.getIncludeCenter() ? 1 : 0);
			stream.writeFloat(v.getCenter().x);
			stream.writeFloat(v.getCenter().y);
			stream.writeFloat(v.getEndpoint().x);
			stream.writeFloat(v.getEndpoint().y);
		} else if (o instanceof RegularPolygon) {
			RegularPolygon v = (RegularPolygon)o;
			stream.writeInt(v.getSides());
			stream.writeInt(v.getSkips());
			Point2D c = v.getCenterInternal();
			Point2D e1 = v.getFirstVertexInternal();
			Point2D e2 = v.getSecondVertexInternal();
			if (c == null && e2 != null) {
				stream.writeInt(fcc("VtoV"));
				stream.writeDouble(e1.getX());
				stream.writeDouble(e1.getY());
				stream.writeDouble(e2.getX());
				stream.writeDouble(e2.getY());
			}
			else if (e2 == null && c != null) {
				stream.writeInt(fcc("CtoV"));
				stream.writeDouble(c.getX());
				stream.writeDouble(c.getY());
				stream.writeDouble(e1.getX());
				stream.writeDouble(e1.getY());
			}
			else {
				stream.writeInt(fcc("????"));
			}
		} else if (o instanceof RightArc) {
			RightArc v = (RightArc)o;
			stream.writeFloat((float)v.getX());
			stream.writeFloat((float)v.getY());
			stream.writeFloat((float)v.getWidth());
			stream.writeFloat((float)v.getHeight());
		} else if (o instanceof ScaledShape) {
			ScaledShape v = (ScaledShape)o;
			stream.writeDouble(v.getX());
			stream.writeDouble(v.getY());
			stream.writeDouble(v.getWidth());
			stream.writeDouble(v.getHeight());
			SerializationManager.writeObject(v.getOriginalShape(), stream);
		} else if (o instanceof Spiral) {
			Spiral v = (Spiral)o;
			stream.writeInt(v.getSides());
			stream.writeInt(v.getSpokes() ? 1 : 0);
			stream.writeDouble(v.getSpacing());
			stream.writeFloat(v.getCenter().x);
			stream.writeFloat(v.getCenter().y);
			stream.writeFloat(v.getEndpoint().x);
			stream.writeFloat(v.getEndpoint().y);
		} else if (o instanceof Parameter) {
			Parameter v = (Parameter)o;
			stream.writeInt(v.polar ? 0x706F6C72 : 0x72656374);
			stream.writeDouble(v.originX);
			stream.writeDouble(v.originY);
			if (v.polar) {
				stream.writeDouble(v.minR);
				stream.writeDouble(v.minA);
				stream.writeDouble(v.defR);
				stream.writeDouble(v.defA);
				stream.writeDouble(v.maxR);
				stream.writeDouble(v.maxA);
			} else {
				stream.writeDouble(v.minX);
				stream.writeDouble(v.minY);
				stream.writeDouble(v.defX);
				stream.writeDouble(v.defY);
				stream.writeDouble(v.maxX);
				stream.writeDouble(v.maxY);
			}
			stream.writeUTF(v.name);
		} else if (o instanceof ParameterizedPath) {
			ParameterizedPath v = (ParameterizedPath)o;
			stream.writeInt(v.size());
			for (int i = 0, n = v.size(); i < n; i++) {
				stream.writeChar(v.getOpcode(i));
				stream.writeShort(v.getOperands(i).size());
				for (ParameterizedValue pv : v.getOperands(i)) {
					SerializationManager.writeObject(pv, stream);
				}
			}
		} else if (o instanceof ParameterizedPoint) {
			ParameterizedPoint v = (ParameterizedPoint)o;
			boolean xv = (v.x.expr instanceof Expression.Value);
			boolean yv = (v.y.expr instanceof Expression.Value);
			if (xv && yv) {
				stream.writeInt(-2);
				stream.writeInt(-2);
				stream.writeDouble(((Expression.Value)v.x.expr).value);
				stream.writeDouble(((Expression.Value)v.y.expr).value);
			} else {
				stream.writeInt(-1);
				stream.writeInt(-1);
				stream.writeUTF(v.x.source);
				stream.writeUTF(v.y.source);
			}
		} else if (o instanceof ParameterizedShape.Arc) {
			ParameterizedShape.Arc v = (ParameterizedShape.Arc)o;
			SerializationManager.writeObject(v.cx, stream);
			SerializationManager.writeObject(v.cy, stream);
			SerializationManager.writeObject(v.rx, stream);
			SerializationManager.writeObject(v.ry, stream);
			SerializationManager.writeObject(v.start, stream);
			SerializationManager.writeObject(v.extent, stream);
			stream.writeInt((v.type != null) ? v.type.awtValue : -1);
		} else if (o instanceof ParameterizedShape.Circle) {
			ParameterizedShape.Circle v = (ParameterizedShape.Circle)o;
			SerializationManager.writeObject(v.cx, stream);
			SerializationManager.writeObject(v.cy, stream);
			SerializationManager.writeObject(v.r, stream);
		} else if (o instanceof ParameterizedShape.Ellipse) {
			ParameterizedShape.Ellipse v = (ParameterizedShape.Ellipse)o;
			SerializationManager.writeObject(v.cx, stream);
			SerializationManager.writeObject(v.cy, stream);
			SerializationManager.writeObject(v.rx, stream);
			SerializationManager.writeObject(v.ry, stream);
		} else if (o instanceof ParameterizedShape.Line) {
			ParameterizedShape.Line v = (ParameterizedShape.Line)o;
			SerializationManager.writeObject(v.x1, stream);
			SerializationManager.writeObject(v.y1, stream);
			SerializationManager.writeObject(v.x2, stream);
			SerializationManager.writeObject(v.y2, stream);
		} else if (o instanceof ParameterizedShape.Polygon) {
			ParameterizedShape.Polygon v = (ParameterizedShape.Polygon)o;
			stream.writeInt(v.points.length);
			for (ParameterizedValue pv : v.points) {
				SerializationManager.writeObject(pv, stream);
			}
		} else if (o instanceof ParameterizedShape.PolyLine) {
			ParameterizedShape.PolyLine v = (ParameterizedShape.PolyLine)o;
			stream.writeInt(v.points.length);
			for (ParameterizedValue pv : v.points) {
				SerializationManager.writeObject(pv, stream);
			}
		} else if (o instanceof ParameterizedShape.Rect) {
			ParameterizedShape.Rect v = (ParameterizedShape.Rect)o;
			SerializationManager.writeObject(v.x, stream);
			SerializationManager.writeObject(v.y, stream);
			SerializationManager.writeObject(v.width, stream);
			SerializationManager.writeObject(v.height, stream);
			SerializationManager.writeObject(v.rx, stream);
			SerializationManager.writeObject(v.ry, stream);
		} else if (o instanceof ParameterizedValue) {
			ParameterizedValue v = (ParameterizedValue)o;
			if (v.expr instanceof Expression.Value) {
				stream.writeInt(-2);
				stream.writeDouble(((Expression.Value)v.expr).value);
			} else {
				stream.writeInt(-1);
				stream.writeUTF(v.source);
			}
		} else if (o instanceof Path) {
			Path v = (Path)o;
			stream.writeInt(v.size());
			for (PathContour c : v) {
				stream.writeInt(c.size());
				for (PathPoint p : c) {
					stream.writeDouble(p.getPreviousCtrl().getX());
					stream.writeDouble(p.getPreviousCtrl().getY());
					stream.writeDouble(p.getX());
					stream.writeDouble(p.getY());
					stream.writeDouble(p.getNextCtrl().getX());
					stream.writeDouble(p.getNextCtrl().getY());
					stream.writeBoolean(p.isPreviousQuadratic());
					stream.writeBoolean(p.isAngleLocked());
					stream.writeBoolean(p.isRadiusLocked());
					stream.writeBoolean(p.isNextQuadratic());
				}
				stream.writeBoolean(c.isClosed());
			}
		} else if (o instanceof PathContour) {
			PathContour v = (PathContour)o;
			stream.writeInt(v.size());
			for (PathPoint p : v) {
				stream.writeDouble(p.getPreviousCtrl().getX());
				stream.writeDouble(p.getPreviousCtrl().getY());
				stream.writeDouble(p.getX());
				stream.writeDouble(p.getY());
				stream.writeDouble(p.getNextCtrl().getX());
				stream.writeDouble(p.getNextCtrl().getY());
				stream.writeBoolean(p.isPreviousQuadratic());
				stream.writeBoolean(p.isAngleLocked());
				stream.writeBoolean(p.isRadiusLocked());
				stream.writeBoolean(p.isNextQuadratic());
			}
			stream.writeBoolean(v.isClosed());
		} else if (o instanceof PathPoint) {
			PathPoint v = (PathPoint)o;
			stream.writeDouble(v.getPreviousCtrl().getX());
			stream.writeDouble(v.getPreviousCtrl().getY());
			stream.writeDouble(v.getX());
			stream.writeDouble(v.getY());
			stream.writeDouble(v.getNextCtrl().getX());
			stream.writeDouble(v.getNextCtrl().getY());
			stream.writeBoolean(v.isPreviousQuadratic());
			stream.writeBoolean(v.isAngleLocked());
			stream.writeBoolean(v.isRadiusLocked());
			stream.writeBoolean(v.isNextQuadratic());
		} else if (o instanceof PowerShape) {
			PowerShape v = (PowerShape)o;
			List<String> params = v.getParameterNames();
			List<ParameterizedShape> shapes = v.getShapes();
			stream.writeInt(params.size());
			stream.writeInt(params.size());
			stream.writeInt(shapes.size());
			stream.writeInt((v.windingRule != null) ? v.windingRule.awtValue : -1);
			stream.writeUTF((v.name != null) ? v.name : "");
			for (String n : params) {
				SerializationManager.writeObject(v.getParameter(n), stream);
			}
			for (String n : params) {
				Point2D p = v.getParameterValue(n);
				stream.writeDouble(p.getX());
				stream.writeDouble(p.getY());
				stream.writeUTF(n);
			}
			for (ParameterizedShape s : shapes) {
				SerializationManager.writeObject(s, stream);
			}
		} else if (o instanceof PowerShapeList) {
			PowerShapeList v = (PowerShapeList)o;
			stream.writeInt(v.size());
			stream.writeBoolean(v.name != null);
			for (PowerShape shape : v) {
				SerializationManager.writeObject(shape, stream);
			}
			if (v.name != null) {
				stream.writeUTF(v.name);
			}
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (type == TYPE_BITMAP_SHAPE) {
			if (version != 1) throw new IOException("Invalid version number.");
			int x = stream.readInt();
			int y = stream.readInt();
			int w = stream.readInt();
			int h = stream.readInt();
			int l = stream.readInt();
			byte[] crgb = new byte[l];
			stream.read(crgb);
			int[] rgb = new int[w*h];
			ByteArrayInputStream bis = new ByteArrayInputStream(crgb);
			InflaterInputStream iis = new InflaterInputStream(bis);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 24) & 0xFF000000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 16) & 0x00FF0000);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= ((iis.read() << 8) & 0xFF00);
			for (int i = 0; i < rgb.length; i++) rgb[i] |= (iis.read() & 0xFF);
			iis.close();
			bis.close();
			return new BitmapShape(rgb, x, y, w, h);
		} else if (type == TYPE_CYCLOID) {
			if (version != 1) throw new IOException("Invalid version number.");
			boolean epi = (stream.readInt() != 0);
			int smoothness = stream.readInt();
			int begin = stream.readInt();
			int end = stream.readInt();
			double R = stream.readDouble();
			double r = stream.readDouble();
			double d = stream.readDouble();
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float vx = stream.readFloat();
			float vy = stream.readFloat();
			return new Cycloid(epi, smoothness, begin, end, R, r, d, cx, cy, vx, vy);
		} else if (type == TYPE_FLOWER) {
			if (version != 1) throw new IOException("Invalid version number.");
			int petals = stream.readInt();
			double width = stream.readDouble();
			int smoothness = stream.readInt();
			boolean includeCenter = (stream.readInt() != 0);
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float vx = stream.readFloat();
			float vy = stream.readFloat();
			return new Flower(petals, width, smoothness, includeCenter, cx, cy, vx, vy);
		} else if (type == TYPE_REGULAR_POLYGON) {
			if (version != 1) throw new IOException("Invalid version number.");
			int sides = stream.readInt();
			int skips = stream.readInt();
			int t = stream.readInt();
			if (t == fcc("VtoV")) {
				double v1x = stream.readDouble();
				double v1y = stream.readDouble();
				double v2x = stream.readDouble();
				double v2y = stream.readDouble();
				return new RegularPolygon(v1x, v1y, v2x, v2y, sides, skips, false);
			}
			else if (t == fcc("CtoV")) {
				double cx = stream.readDouble();
				double cy = stream.readDouble();
				double vx = stream.readDouble();
				double vy = stream.readDouble();
				return new RegularPolygon(cx, cy, vx, vy, sides, skips, true);
			}
			else return null;
		} else if (type == TYPE_RIGHT_ARC) {
			if (version != 1) throw new IOException("Invalid version number.");
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			return new RightArc(x, y, w, h);
		} else if (type == TYPE_SCALED_SHAPE) {
			if (version != 1) throw new IOException("Invalid version number.");
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			Shape shape = (Shape)SerializationManager.readObject(stream);
			return new ScaledShape(x, y, w, h, shape);
		} else if (type == TYPE_SPIRAL) {
			if (version != 1) throw new IOException("Invalid version number.");
			int sides = stream.readInt();
			boolean spokes = (stream.readInt() != 0);
			double spacing = stream.readDouble();
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float vx = stream.readFloat();
			float vy = stream.readFloat();
			return new Spiral(sides, spacing, spokes, cx, cy, vx, vy);
		} else if (type == TYPE_PARAMETER) {
			if (version != 1) throw new IOException("Invalid version number.");
			boolean polar = (stream.readInt() == 0x706F6C72);
			double originX = stream.readDouble();
			double originY = stream.readDouble();
			double minX = stream.readDouble();
			double minY = stream.readDouble();
			double defX = stream.readDouble();
			double defY = stream.readDouble();
			double maxX = stream.readDouble();
			double maxY = stream.readDouble();
			String name = stream.readUTF();
			return new Parameter(
				name, originX, originY, polar,
				minX, minY, minX, minY,
				defX, defY, defX, defY,
				maxX, maxY, maxX, maxY
			);
		} else if (type == TYPE_PARAMETERIZED_PATH) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			if (version >= 2) {
				ParameterizedPath path = new ParameterizedPath();
				int nseg = stream.readInt();
				while (nseg-- > 0) {
					char t = stream.readChar();
					int n = stream.readShort();
					List<ParameterizedValue> v = new ArrayList<ParameterizedValue>();
					while (n-- > 0) {
						v.add((ParameterizedValue)SerializationManager.readObject(stream));
					}
					path.add(t, v);
				}
				return path;
			} else {
				ParameterizedPath path = new ParameterizedPath();
				switch (stream.readShort()) {
				case 0x454F: path.add('W', new ParameterizedValue(0.0)); break;
				case 0x4E5A: path.add('W', new ParameterizedValue(1.0)); break;
				}
				stream.readShort();
				int npp = stream.readInt();
				while (npp-- > 0) {
					Parameter pp = (Parameter)SerializationManager.readObject(stream);
					path.add(
						'P',
						new ParameterizedValue(pp.name, new Expression.Binding(pp.name)),
						new ParameterizedValue(pp.originX),
						new ParameterizedValue(pp.originY),
						new ParameterizedValue(pp.polar ? 1.0 : 0.0),
						new ParameterizedValue(pp.polar ? pp.minR : pp.minX),
						new ParameterizedValue(pp.polar ? pp.minA : pp.minY),
						new ParameterizedValue(pp.polar ? pp.defR : pp.defX),
						new ParameterizedValue(pp.polar ? pp.defA : pp.defY),
						new ParameterizedValue(pp.polar ? pp.maxR : pp.maxX),
						new ParameterizedValue(pp.polar ? pp.maxA : pp.maxY)
					);
				}
				int nseg = stream.readInt();
				while (nseg-- > 0) {
					int t = stream.readShort();
					int n = stream.readShort();
					ParameterizedPoint[] c = new ParameterizedPoint[n];
					for (int j = 0; j < n; j++) {
						c[j] = (ParameterizedPoint)SerializationManager.readObject(stream);
					}
					switch (t) {
					case 0x436C:
						path.add('Z');
						break;
					case 0x4375:
						path.add('C', c[0].x, c[0].y, c[1].x, c[1].y, c[2].x, c[2].y);
						break;
					case 0x4C69:
						path.add('L', c[0].x, c[0].y);
						break;
					case 0x4D6F:
						path.add('M', c[0].x, c[0].y);
						break;
					case 0x5175:
						path.add('Q', c[0].x, c[0].y, c[1].x, c[1].y);
						break;
					case 0x4172:
						path.add('G', c[0].x, c[0].y, c[1].x, c[1].y);
						break;
					case 0x6152: case 0x6352:
						path.add(
							'R', c[0].x, c[0].y, c[1].x, c[1].y,
							new ParameterizedValue(0.0),
							new ParameterizedValue(0.0)
						);
						break;
					case 0x6144: case 0x6344:
						path.add('R', c[0].x, c[0].y, c[1].x, c[1].y, c[2].x, c[2].y);
						break;
					case 0x6145: case 0x6345:
						path.add(
							'E', c[0].x, c[0].y, c[1].x, c[1].y,
							new ParameterizedValue(0.0),
							new ParameterizedValue(360.0),
							new ParameterizedValue(0.0)
						);
						break;
					case 0x6141:
						path.add(
							'E', c[0].x, c[0].y, c[1].x, c[1].y,
							new ParameterizedValue("toDeg(" + c[2].x.source + ")"),
							new ParameterizedValue("toDeg(" + c[2].y.source + ")"),
							new ParameterizedValue(3.0)
						);
						break;
					case 0x6341:
						path.add(
							'E', c[0].x, c[0].y, c[1].x, c[1].y,
							new ParameterizedValue("toDeg(" + c[2].x.source + ")"),
							new ParameterizedValue("toDeg(" + c[2].y.source + ")"),
							new ParameterizedValue(4.0)
						);
						break;
					}
				}
				return path;
			}
		} else if (type == TYPE_PARAMETERIZED_POINT) {
			if (version != 1) throw new IOException("Invalid version number.");
			int nx = stream.readInt();
			int ny = stream.readInt();
			if (nx < 0 || ny < 0) {
				if (nx == -2) {
					ParameterizedValue x = new ParameterizedValue(stream.readDouble());
					ParameterizedValue y = new ParameterizedValue(stream.readDouble());
					return new ParameterizedPoint(x, y);
				} else {
					ParameterizedValue x = new ParameterizedValue(stream.readUTF());
					ParameterizedValue y = new ParameterizedValue(stream.readUTF());
					return new ParameterizedPoint(x, y);
				}
			} else {
				Expression xe = null;
				Expression ye = null;
				StringBuffer xs = new StringBuffer();
				StringBuffer ys = new StringBuffer();
				while (nx-- > 0) {
					double v = stream.readDouble();
					String k = stream.readUTF();
					if (k.length() > 0) {
						Expression ve = new Expression.Value(v);
						Expression ke = new Expression.Binding(k);
						Expression te = new Expression.Binary(Operator.mul, ve, ke);
						xe = ((xe == null) ? te : new Expression.Binary(Operator.add, xe, te));
						xs.append("+(" + ParameterizedValue.NUMBER_FORMAT.format(v) + "*(" + k + "))");
					} else {
						Expression te = new Expression.Value(v);
						xe = ((xe == null) ? te : new Expression.Binary(Operator.add, xe, te));
						xs.append("+(" + ParameterizedValue.NUMBER_FORMAT.format(v) + ")");
					}
				}
				while (ny-- > 0) {
					double v = stream.readDouble();
					String k = stream.readUTF();
					if (k.length() > 0) {
						Expression ve = new Expression.Value(v);
						Expression ke = new Expression.Binding(k);
						Expression te = new Expression.Binary(Operator.mul, ve, ke);
						ye = ((ye == null) ? te : new Expression.Binary(Operator.add, ye, te));
						ys.append("+(" + ParameterizedValue.NUMBER_FORMAT.format(v) + "*(" + k + "))");
					} else {
						Expression te = new Expression.Value(v);
						ye = ((ye == null) ? te : new Expression.Binary(Operator.add, ye, te));
						ys.append("+(" + ParameterizedValue.NUMBER_FORMAT.format(v) + ")");
					}
				}
				if (xe == null) {
					xe = new Expression.Value(0);
					xs.append("+0");
				}
				if (ye == null) {
					ye = new Expression.Value(0);
					ys.append("+0");
				}
				ParameterizedValue xp = new ParameterizedValue(xs.toString().substring(1), xe);
				ParameterizedValue yp = new ParameterizedValue(ys.toString().substring(1), ye);
				return new ParameterizedPoint(xp, yp);
			}
		} else if (type == TYPE_PARAMETERIZED_SHAPE_ARC) {
			if (version != 1) throw new IOException("Invalid version number.");
			ParameterizedValue cx = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue cy = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue rx = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue ry = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue start = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue extent = (ParameterizedValue)SerializationManager.readObject(stream);
			ArcType arcType = ArcType.forAWTValue(stream.readInt());
			return new ParameterizedShape.Arc(cx, cy, rx, ry, start, extent, arcType);
		} else if (type == TYPE_PARAMETERIZED_SHAPE_CIRCLE) {
			if (version != 1) throw new IOException("Invalid version number.");
			ParameterizedValue cx = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue cy = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue r = (ParameterizedValue)SerializationManager.readObject(stream);
			return new ParameterizedShape.Circle(cx, cy, r);
		} else if (type == TYPE_PARAMETERIZED_SHAPE_ELLIPSE) {
			if (version != 1) throw new IOException("Invalid version number.");
			ParameterizedValue cx = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue cy = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue rx = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue ry = (ParameterizedValue)SerializationManager.readObject(stream);
			return new ParameterizedShape.Ellipse(cx, cy, rx, ry);
		} else if (type == TYPE_PARAMETERIZED_SHAPE_LINE) {
			if (version != 1) throw new IOException("Invalid version number.");
			ParameterizedValue x1 = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue y1 = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue x2 = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue y2 = (ParameterizedValue)SerializationManager.readObject(stream);
			return new ParameterizedShape.Line(x1, y1, x2, y2);
		} else if (type == TYPE_PARAMETERIZED_SHAPE_POLYGON) {
			if (version != 1) throw new IOException("Invalid version number.");
			int count = stream.readInt(); if (count < 0) count = 0;
			ParameterizedValue[] points = new ParameterizedValue[count];
			for (int i = 0; i < count; i++) {
				points[i] = (ParameterizedValue)SerializationManager.readObject(stream);
			}
			return new ParameterizedShape.Polygon(points);
		} else if (type == TYPE_PARAMETERIZED_SHAPE_POLYLINE) {
			if (version != 1) throw new IOException("Invalid version number.");
			int count = stream.readInt(); if (count < 0) count = 0;
			ParameterizedValue[] points = new ParameterizedValue[count];
			for (int i = 0; i < count; i++) {
				points[i] = (ParameterizedValue)SerializationManager.readObject(stream);
			}
			return new ParameterizedShape.PolyLine(points);
		} else if (type == TYPE_PARAMETERIZED_SHAPE_RECT) {
			if (version != 1) throw new IOException("Invalid version number.");
			ParameterizedValue x = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue y = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue width = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue height = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue rx = (ParameterizedValue)SerializationManager.readObject(stream);
			ParameterizedValue ry = (ParameterizedValue)SerializationManager.readObject(stream);
			return new ParameterizedShape.Rect(x, y, width, height, rx, ry);
		} else if (type == TYPE_PARAMETERIZED_VALUE) {
			if (version != 1) throw new IOException("Invalid version number.");
			int n = stream.readInt();
			if (n < 0) {
				if (n == -2) {
					return new ParameterizedValue(stream.readDouble());
				} else {
					return new ParameterizedValue(stream.readUTF());
				}
			} else {
				Expression e = null;
				StringBuffer s = new StringBuffer();
				while (n-- > 0) {
					double v = stream.readDouble();
					String k = stream.readUTF();
					if (k.length() > 0) {
						Expression ve = new Expression.Value(v);
						Expression ke = new Expression.Binding(k);
						Expression te = new Expression.Binary(Operator.mul, ve, ke);
						e = ((e == null) ? te : new Expression.Binary(Operator.add, e, te));
						s.append("+(" + ParameterizedValue.NUMBER_FORMAT.format(v) + "*(" + k + "))");
					} else {
						Expression te = new Expression.Value(v);
						e = ((e == null) ? te : new Expression.Binary(Operator.add, e, te));
						s.append("+(" + ParameterizedValue.NUMBER_FORMAT.format(v) + ")");
					}
				}
				if (e == null) {
					e = new Expression.Value(0);
					s.append("+0");
				}
				return new ParameterizedValue(s.toString().substring(1), e);
			}
		} else if (type == TYPE_PATH) {
			if (version != 1) throw new IOException("Invalid version number.");
			Path path = new Path();
			int nc = stream.readInt();
			for (int ci = 0; ci < nc; ci++) {
				PathContour c = new PathContour();
				int np = stream.readInt();
				for (int pi = 0; pi < np; pi++) {
					double px = stream.readDouble();
					double py = stream.readDouble();
					double x = stream.readDouble();
					double y = stream.readDouble();
					double nx = stream.readDouble();
					double ny = stream.readDouble();
					boolean pq = stream.readBoolean();
					boolean al = stream.readBoolean();
					boolean rl = stream.readBoolean();
					boolean nq = stream.readBoolean();
					PathPoint p = new PathPoint(x, y);
					p.setPreviousCtrl(px, py);
					p.setNextCtrl(nx, ny);
					p.setPreviousQuadratic(pq);
					p.setAngleLocked(al);
					p.setRadiusLocked(rl);
					p.setNextQuadratic(nq);
					c.add(p);
				}
				c.setClosed(stream.readBoolean());
				path.add(c);
			}
			return path;
		} else if (type == TYPE_PATH_CONTOUR) {
			if (version != 1) throw new IOException("Invalid version number.");
			PathContour c = new PathContour();
			int np = stream.readInt();
			for (int pi = 0; pi < np; pi++) {
				double px = stream.readDouble();
				double py = stream.readDouble();
				double x = stream.readDouble();
				double y = stream.readDouble();
				double nx = stream.readDouble();
				double ny = stream.readDouble();
				boolean pq = stream.readBoolean();
				boolean al = stream.readBoolean();
				boolean rl = stream.readBoolean();
				boolean nq = stream.readBoolean();
				PathPoint p = new PathPoint(x, y);
				p.setPreviousCtrl(px, py);
				p.setNextCtrl(nx, ny);
				p.setPreviousQuadratic(pq);
				p.setAngleLocked(al);
				p.setRadiusLocked(rl);
				p.setNextQuadratic(nq);
				c.add(p);
			}
			c.setClosed(stream.readBoolean());
			return c;
		} else if (type == TYPE_PATH_POINT) {
			if (version != 1) throw new IOException("Invalid version number.");
			double px = stream.readDouble();
			double py = stream.readDouble();
			double x = stream.readDouble();
			double y = stream.readDouble();
			double nx = stream.readDouble();
			double ny = stream.readDouble();
			boolean pq = stream.readBoolean();
			boolean al = stream.readBoolean();
			boolean rl = stream.readBoolean();
			boolean nq = stream.readBoolean();
			PathPoint p = new PathPoint(x, y);
			p.setPreviousCtrl(px, py);
			p.setNextCtrl(nx, ny);
			p.setPreviousQuadratic(pq);
			p.setAngleLocked(al);
			p.setRadiusLocked(rl);
			p.setNextQuadratic(nq);
			return p;
		} else if (type == TYPE_POWERSHAPE) {
			if (version < 1 || version > 2) throw new IOException("Invalid version number.");
			if (version >= 2) {
				int np = stream.readInt();
				int nv = stream.readInt();
				int ns = stream.readInt();
				WindingRule winding = WindingRule.forAWTValue(stream.readInt());
				String name = stream.readUTF();
				PowerShape ps = new PowerShape(winding, name);
				while (np-- > 0) {
					ps.addParameter((Parameter)SerializationManager.readObject(stream));
				}
				while (nv-- > 0) {
					double x = stream.readDouble();
					double y = stream.readDouble();
					String n = stream.readUTF();
					ps.setParameterValue(n, x, y);
				}
				while (ns-- > 0) {
					ps.addShape((ParameterizedShape)SerializationManager.readObject(stream));
				}
				return ps;
			} else {
				WindingRule winding = WindingRule.NON_ZERO;
				List<Parameter> params = new ArrayList<Parameter>();
				ParameterizedPath newPath = new ParameterizedPath();
				ParameterizedPath oldPath = (ParameterizedPath)SerializationManager.readObject(stream);
				for (int i = 0, n = oldPath.size(); i < n; i++) {
					char inst = oldPath.getOpcode(i);
					List<ParameterizedValue> args = oldPath.getOperands(i);
					switch (inst) {
					case 'W': case 'w':
						double wdv = args.get(0).value(null);
						int wiv = Math.abs((int)Math.round(wdv)) % 2;
						winding = WindingRule.forAWTValue(wiv);
						break;
					case 'P': case 'p':
						params.add(new Parameter(
							args.get(0).source,
							args.get(1).value(null),
							args.get(2).value(null),
							args.get(3).value(null) != 0,
							args.get(4).value(null),
							args.get(5).value(null),
							args.get(4).value(null),
							args.get(5).value(null),
							args.get(6).value(null),
							args.get(7).value(null),
							args.get(6).value(null),
							args.get(7).value(null),
							args.get(8).value(null),
							args.get(9).value(null),
							args.get(8).value(null),
							args.get(9).value(null)
						));
						break;
					default:
						newPath.add(inst, args);
						break;
					}
				}
				PowerShape ps = new PowerShape(winding, null);
				for (Parameter param : params) ps.addParameter(param);
				ps.addShape(newPath);
				int npp = stream.readInt();
				while (npp-- > 0) {
					double x = stream.readDouble();
					double y = stream.readDouble();
					String n = stream.readUTF();
					ps.setParameterValue(n, x, y);
				}
				return ps;
			}
		} else if (type == TYPE_POWERSHAPE_LIST) {
			if (version != 1) throw new IOException("Invalid version number.");
			int l = stream.readInt();
			boolean n = stream.readBoolean();
			PowerShape[] ps = new PowerShape[l];
			for (int i = 0; i < l; i++) {
				ps[i] = (PowerShape)SerializationManager.readObject(stream);
			}
			String name = n ? stream.readUTF() : null;
			PowerShapeList list = new PowerShapeList(name);
			for (PowerShape shape : ps) list.add(shape);
			return list;
		} else {
			return null;
		}
	}
}
