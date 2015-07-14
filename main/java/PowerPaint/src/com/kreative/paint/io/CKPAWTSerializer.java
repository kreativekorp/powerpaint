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
import java.io.*;
import com.kreative.paint.awt.*;
import com.kreative.paint.pattern.Pattern;
import com.kreative.paint.pattern.PatternPaint;

public class CKPAWTSerializer extends Serializer {
	private static final int TYPE_BASIC_DERIVABLE_STROKE = fcc("DStk");
	private static final int TYPE_CIRCLE_ARROWHEAD = fcc("CAhd");
	private static final int TYPE_PATTERN = fcc("Patt");
	private static final int TYPE_PATTERN_PAINT = fcc("PPnt");
	private static final int TYPE_POLYGON_ARROWHEAD = fcc("PAhd");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BASIC_DERIVABLE_STROKE, 1, BasicDerivableStroke.class);
		addTypeAndClass(TYPE_CIRCLE_ARROWHEAD, 1, CircleArrowhead.class);
		addTypeAndClass(TYPE_PATTERN, 1, Pattern.class);
		addTypeAndClass(TYPE_PATTERN_PAINT, 2, PatternPaint.class);
		addTypeAndClass(TYPE_POLYGON_ARROWHEAD, 1, PolygonArrowhead.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof BasicDerivableStroke) {
			BasicDerivableStroke v = (BasicDerivableStroke)o;
			stream.writeFloat(v.getLineWidth());
			stream.writeInt(v.getEndCap());
			stream.writeInt(v.getLineJoin());
			stream.writeFloat(v.getMiterLimit());
			stream.writeInt(v.getMultiplicity());
			stream.writeFloat(v.getDashPhase());
			float[] dash = v.getDashArray();
			if (dash == null) stream.writeInt(-1);
			else {
				stream.writeInt(dash.length);
				for (int i = 0; i < dash.length; i++) {
					stream.writeFloat(dash[i]);
				}
			}
			SerializationManager.writeObject(v.getArrowOnStart(), stream);
			SerializationManager.writeObject(v.getArrowOnEnd(), stream);
		} else if (o instanceof CircleArrowhead) {
			CircleArrowhead v = (CircleArrowhead)o;
			stream.writeBoolean(v.isFilled());
			stream.writeBoolean(v.isScaled());
			stream.writeFloat(v.getCenterX());
			stream.writeFloat(v.getCenterY());
			stream.writeFloat(v.getWidth());
			stream.writeFloat(v.getHeight());
		} else if (o instanceof Pattern) {
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
		} else if (o instanceof PatternPaint) {
			PatternPaint v = (PatternPaint)o;
			SerializationManager.writeObject(v.pattern, stream);
			SerializationManager.writeObject(v.foreground, stream);
			SerializationManager.writeObject(v.background, stream);
		} else if (o instanceof PolygonArrowhead) {
			PolygonArrowhead v = (PolygonArrowhead)o;
			stream.writeBoolean(v.isFilled());
			stream.writeBoolean(v.isScaled());
			stream.writeInt(v.getPointArray().length);
			for (float f : v.getPointArray()) {
				stream.writeFloat(f);
			}
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (type == TYPE_BASIC_DERIVABLE_STROKE) {
			if (version != 1) throw new IOException("Invalid version number.");
			float lw = stream.readFloat();
			int ec = stream.readInt();
			int lj = stream.readInt();
			float ml = stream.readFloat();
			int mx = stream.readInt();
			float dp = stream.readFloat();
			int n = stream.readInt();
			float[] dash;
			if (n < 0) {
				dash = null;
			} else {
				dash = new float[n];
				for (int i = 0; i < n; i++) {
					dash[i] = stream.readFloat();
				}
			}
			Arrowhead as = (Arrowhead)SerializationManager.readObject(stream);
			Arrowhead ae = (Arrowhead)SerializationManager.readObject(stream);
			return new BasicDerivableStroke(lw, ec, lj, ml, dash, dp, mx, as, ae);
		} else if (type == TYPE_CIRCLE_ARROWHEAD) {
			if (version != 1) throw new IOException("Invalid version number.");
			boolean f = stream.readBoolean();
			boolean s = stream.readBoolean();
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			return new CircleArrowhead(cx, cy, w, h, f, s);
		} else if (type == TYPE_PATTERN) {
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
		} else if (type == TYPE_POLYGON_ARROWHEAD) {
			if (version != 1) throw new IOException("Invalid version number.");
			boolean f = stream.readBoolean();
			boolean s = stream.readBoolean();
			int np = stream.readInt();
			float[] p = new float[np];
			for (int i = 0; i < np; i++) {
				p[i] = stream.readFloat();
			}
			return new PolygonArrowhead(p, f, s);
		} else {
			return null;
		}
	}
}
