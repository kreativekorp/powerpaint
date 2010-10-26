/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.Shape;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import com.kreative.paint.geom.*;

public class CKPGeomSerializer extends Serializer {
	private static final int TYPE_BITMAP_SHAPE = fcc("BmSh");
	private static final int TYPE_CYCLOID = fcc("Cycl");
	private static final int TYPE_FLOWER = fcc("Flow");
	private static final int TYPE_PARAMETER_POINT = fcc("pPrm");
	private static final int TYPE_PARAMETERIZED_POINT = fcc("pPnt");
	private static final int TYPE_PARAMETERIZED_PATH = fcc("pPth");
	private static final int TYPE_PARAMETERIZED_SHAPE = fcc("pShp");
	private static final int TYPE_REGULAR_POLYGON = fcc("RPly");
	private static final int TYPE_RIGHT_ARC = fcc("ArcR");
	private static final int TYPE_SCALED_SHAPE = fcc("ScSh");
	private static final int TYPE_SPIRAL = fcc("Spir");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BITMAP_SHAPE, 1, BitmapShape.class);
		addTypeAndClass(TYPE_CYCLOID, 1, Cycloid.class);
		addTypeAndClass(TYPE_FLOWER, 1, Flower.class);
		addTypeAndClass(TYPE_PARAMETER_POINT, 1, ParameterPoint.class);
		addTypeAndClass(TYPE_PARAMETERIZED_POINT, 1, ParameterizedPoint.class);
		addTypeAndClass(TYPE_PARAMETERIZED_PATH, 1, ParameterizedPath.class);
		addTypeAndClass(TYPE_PARAMETERIZED_SHAPE, 1, ParameterizedShape.class);
		addTypeAndClass(TYPE_REGULAR_POLYGON, 1, RegularPolygon.class);
		addTypeAndClass(TYPE_RIGHT_ARC, 1, RightArc.class);
		addTypeAndClass(TYPE_SCALED_SHAPE, 1, ScaledShape.class);
		addTypeAndClass(TYPE_SPIRAL, 1, Spiral.class);
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
		}
		else if (o instanceof Cycloid) {
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
		}
		else if (o instanceof Flower) {
			Flower v = (Flower)o;
			stream.writeInt(v.getPetals());
			stream.writeDouble(v.getWidth());
			stream.writeInt(v.getSmoothness());
			stream.writeInt(v.getIncludeCenter() ? 1 : 0);
			stream.writeFloat(v.getCenter().x);
			stream.writeFloat(v.getCenter().y);
			stream.writeFloat(v.getEndpoint().x);
			stream.writeFloat(v.getEndpoint().y);
		}
		else if (o instanceof ParameterPoint) {
			ParameterPoint v = (ParameterPoint)o;
			stream.writeInt(v.isPolar() ? 0x706F6C72 : 0x72656374);
			stream.writeDouble(v.getOriginX());
			stream.writeDouble(v.getOriginY());
			stream.writeDouble(v.getMinimumXorRadius());
			stream.writeDouble(v.getMinimumYorTheta());
			stream.writeDouble(v.getDefaultXorRadius());
			stream.writeDouble(v.getDefaultYorTheta());
			stream.writeDouble(v.getMaximumXorRadius());
			stream.writeDouble(v.getMaximumYorTheta());
			stream.writeUTF(v.getName());
		}
		else if (o instanceof ParameterizedPoint) {
			ParameterizedPoint v = (ParameterizedPoint)o;
			if (v.hasXValue() && v.hasYValue()) {
				stream.writeInt(-2);
				stream.writeInt(-2);
				stream.writeDouble(v.getXValue());
				stream.writeDouble(v.getYValue());
			} else {
				stream.writeInt(-1);
				stream.writeInt(-1);
				stream.writeUTF(v.getXExpression());
				stream.writeUTF(v.getYExpression());
			}
		}
		else if (o instanceof ParameterizedPath) {
			ParameterizedPath v = (ParameterizedPath)o;
			switch (v.getWindingRule()) {
			case ParameterizedPath.WIND_EVEN_ODD: stream.writeShort(0x454F); break;
			case ParameterizedPath.WIND_NON_ZERO: stream.writeShort(0x4E5A); break;
			default: stream.writeShort(0x3F3F); break;
			}
			stream.writeShort(0);
			Collection<ParameterPoint> pp = v.getParameterPoints();
			stream.writeInt(pp.size());
			for (ParameterPoint p : pp) {
				SerializationManager.writeObject(p, stream);
			}
			int sc = v.getSegmentCount();
			stream.writeInt(sc);
			for (int i = 0; i < sc; i++) {
				ParameterizedPoint[] coords = new ParameterizedPoint[3];
				int type = v.getSegment(i, coords);
				int n = 0;
				switch (type) {
				case ParameterizedPath.SEG_CLOSE: stream.writeShort(0x436C); n = 0; break;
				case ParameterizedPath.SEG_CUBICTO: stream.writeShort(0x4375); n = 3; break;
				case ParameterizedPath.SEG_LINETO: stream.writeShort(0x4C69); n = 1; break;
				case ParameterizedPath.SEG_MOVETO: stream.writeShort(0x4D6F); n = 1; break;
				case ParameterizedPath.SEG_QUADTO: stream.writeShort(0x5175); n = 2; break;
				case ParameterizedPath.SEG_ARCTO: stream.writeShort(0x4172); n = 2; break;
				case ParameterizedPath.SEG_APPEND_RECT: stream.writeShort(0x6152); n = 2; break;
				case ParameterizedPath.SEG_CONNECT_RECT: stream.writeShort(0x6352); n = 2; break;
				case ParameterizedPath.SEG_APPEND_RRECT: stream.writeShort(0x6144); n = 3; break;
				case ParameterizedPath.SEG_CONNECT_RRECT: stream.writeShort(0x6344); n = 3; break;
				case ParameterizedPath.SEG_APPEND_ELLIPSE: stream.writeShort(0x6145); n = 2; break;
				case ParameterizedPath.SEG_CONNECT_ELLIPSE: stream.writeShort(0x6345); n = 2; break;
				case ParameterizedPath.SEG_APPEND_ARC: stream.writeShort(0x6141); n = 3; break;
				case ParameterizedPath.SEG_CONNECT_ARC: stream.writeShort(0x6341); n = 3; break;
				default: stream.writeShort(0x3F3F); n = 0; break;
				}
				stream.writeShort(n);
				for (int j = 0; j < n; j++) {
					SerializationManager.writeObject(coords[j], stream);
				}
			}
		}
		else if (o instanceof ParameterizedShape) {
			ParameterizedShape v = (ParameterizedShape)o;
			SerializationManager.writeObject(v.getParameterizedPath(), stream);
			Set<Map.Entry<String,Point2D>> pp = v.getParameterValues();
			stream.writeInt(pp.size());
			for (Map.Entry<String,Point2D> p : pp) {
				stream.writeDouble(p.getValue().getX());
				stream.writeDouble(p.getValue().getY());
				stream.writeUTF(p.getKey());
			}
		}
		else if (o instanceof RegularPolygon) {
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
		}
		else if (o instanceof RightArc) {
			RightArc v = (RightArc)o;
			stream.writeFloat((float)v.getX());
			stream.writeFloat((float)v.getY());
			stream.writeFloat((float)v.getWidth());
			stream.writeFloat((float)v.getHeight());
		}
		else if (o instanceof ScaledShape) {
			ScaledShape v = (ScaledShape)o;
			stream.writeDouble(v.getX());
			stream.writeDouble(v.getY());
			stream.writeDouble(v.getWidth());
			stream.writeDouble(v.getHeight());
			SerializationManager.writeObject(v.getOriginalShape(), stream);
		}
		else if (o instanceof Spiral) {
			Spiral v = (Spiral)o;
			stream.writeInt(v.getSides());
			stream.writeInt(v.getSpokes() ? 1 : 0);
			stream.writeDouble(v.getSpacing());
			stream.writeFloat(v.getCenter().x);
			stream.writeFloat(v.getCenter().y);
			stream.writeFloat(v.getEndpoint().x);
			stream.writeFloat(v.getEndpoint().y);
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (version != 1) throw new IOException("Invalid version number.");
		else if (type == TYPE_BITMAP_SHAPE) {
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
		}
		else if (type == TYPE_CYCLOID) {
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
		}
		else if (type == TYPE_FLOWER) {
			int petals = stream.readInt();
			double width = stream.readDouble();
			int smoothness = stream.readInt();
			boolean includeCenter = (stream.readInt() != 0);
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float vx = stream.readFloat();
			float vy = stream.readFloat();
			return new Flower(petals, width, smoothness, includeCenter, cx, cy, vx, vy);
		}
		else if (type == TYPE_PARAMETER_POINT) {
			boolean polar = (stream.readInt() == 0x706F6C72);
			double ox = stream.readDouble();
			double oy = stream.readDouble();
			double minx = stream.readDouble();
			double miny = stream.readDouble();
			double defx = stream.readDouble();
			double defy = stream.readDouble();
			double maxx = stream.readDouble();
			double maxy = stream.readDouble();
			String n = stream.readUTF();
			return new ParameterPoint(n, ox, oy, polar, minx, miny, defx, defy, maxx, maxy);
		}
		else if (type == TYPE_PARAMETERIZED_POINT) {
			int nx = stream.readInt();
			int ny = stream.readInt();
			if (nx < 0 || ny < 0) {
				if (nx == -2) {
					double x = stream.readDouble();
					double y = stream.readDouble();
					return new ParameterizedPoint(x, y);
				} else {
					String x = stream.readUTF();
					String y = stream.readUTF();
					return new ParameterizedPoint(x, y);
				}
			} else {
				String x = "";
				String y = "";
				while (nx-->0) {
					double v = stream.readDouble();
					String k = stream.readUTF();
					if (k.length() > 0)
						x += "+(" + ParameterizedPoint.FORMAT.format(v) + "*(" + k + "))";
					else
						x += "+(" + ParameterizedPoint.FORMAT.format(v) + ")";
				}
				while (ny-->0) {
					double v = stream.readDouble();
					String k = stream.readUTF();
					if (k.length() > 0)
						y += "+(" + ParameterizedPoint.FORMAT.format(v) + "*(" + k + "))";
					else
						y += "+(" + ParameterizedPoint.FORMAT.format(v) + ")";
				}
				if (x.length() > 0) x = x.substring(1);
				if (y.length() > 0) y = y.substring(1);
				return new ParameterizedPoint(x, y);
			}
		}
		else if (type == TYPE_PARAMETERIZED_PATH) {
			ParameterizedPath path = new ParameterizedPath();
			switch (stream.readShort()) {
			case 0x454F: path.setWindingRule(ParameterizedPath.WIND_EVEN_ODD); break;
			case 0x4E5A: path.setWindingRule(ParameterizedPath.WIND_NON_ZERO); break;
			}
			stream.readShort();
			int npp = stream.readInt();
			while (npp-->0) {
				path.addParameterPoint((ParameterPoint)SerializationManager.readObject(stream));
			}
			int nseg = stream.readInt();
			while (nseg-->0) {
				int t = stream.readShort();
				int n = stream.readShort();
				ParameterizedPoint[] c = new ParameterizedPoint[n];
				for (int j = 0; j < n; j++) {
					c[j] = (ParameterizedPoint)SerializationManager.readObject(stream);
				}
				switch (t) {
				case 0x436C: path.closePath(); break;
				case 0x4375: path.curveTo(c[0], c[1], c[2]); break;
				case 0x4C69: path.lineTo(c[0]); break;
				case 0x4D6F: path.moveTo(c[0]); break;
				case 0x5175: path.quadTo(c[0], c[1]); break;
				case 0x4172: path.arcTo(c[0], c[1]); break;
				case 0x6152: path.appendRectangle(c[0], c[1], false); break;
				case 0x6352: path.appendRectangle(c[0], c[1], true); break;
				case 0x6144: path.appendRoundRectangle(c[0], c[1], c[2], false); break;
				case 0x6344: path.appendRoundRectangle(c[0], c[1], c[2], true); break;
				case 0x6145: path.appendEllipse(c[0], c[1], false); break;
				case 0x6345: path.appendEllipse(c[0], c[1], true); break;
				case 0x6141: path.appendArc(c[0], c[1], c[2], false); break;
				case 0x6341: path.appendArc(c[0], c[1], c[2], true); break;
				}
			}
			return path;
		}
		else if (type == TYPE_PARAMETERIZED_SHAPE) {
			ParameterizedPath path = (ParameterizedPath)SerializationManager.readObject(stream);
			ParameterizedShape ps = new ParameterizedShape(path);
			int npp = stream.readInt();
			while (npp-->0) {
				double x = stream.readDouble();
				double y = stream.readDouble();
				String n = stream.readUTF();
				ps.setParameterValue(n, x, y);
			}
			return ps;
		}
		else if (type == TYPE_REGULAR_POLYGON) {
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
		}
		else if (type == TYPE_RIGHT_ARC) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			return new RightArc(x, y, w, h);
		}
		else if (type == TYPE_SCALED_SHAPE) {
			double x = stream.readDouble();
			double y = stream.readDouble();
			double w = stream.readDouble();
			double h = stream.readDouble();
			Shape shape = (Shape)SerializationManager.readObject(stream);
			return new ScaledShape(x, y, w, h, shape);
		}
		else if (type == TYPE_SPIRAL) {
			int sides = stream.readInt();
			boolean spokes = (stream.readInt() != 0);
			double spacing = stream.readDouble();
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float vx = stream.readFloat();
			float vy = stream.readFloat();
			return new Spiral(sides, spacing, spokes, cx, cy, vx, vy);
		}
		else return null;
	}
}
