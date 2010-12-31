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

public class CKPAWTSerializer extends Serializer {
	private static final int TYPE_BASIC_DERIVABLE_STROKE = fcc("DStk");
	private static final int TYPE_CIRCLE_ARROWHEAD = fcc("CAhd");
	private static final int TYPE_PATTERN_PAINT = fcc("PPnt");
	private static final int TYPE_POLYGON_ARROWHEAD = fcc("PAhd");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_BASIC_DERIVABLE_STROKE, 1, BasicDerivableStroke.class);
		addTypeAndClass(TYPE_CIRCLE_ARROWHEAD, 1, CircleArrowhead.class);
		addTypeAndClass(TYPE_PATTERN_PAINT, 1, PatternPaint.class);
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
		}
		else if (o instanceof CircleArrowhead) {
			CircleArrowhead v = (CircleArrowhead)o;
			stream.writeBoolean(v.isFilled());
			stream.writeBoolean(v.isScaled());
			stream.writeFloat(v.getCenterX());
			stream.writeFloat(v.getCenterY());
			stream.writeFloat(v.getWidth());
			stream.writeFloat(v.getHeight());
		}
		else if (o instanceof PatternPaint) {
			PatternPaint v = (PatternPaint)o;
			stream.writeLong(v.getPattern());
			SerializationManager.writeObject(v.getForeground(), stream);
			SerializationManager.writeObject(v.getBackground(), stream);
		}
		else if (o instanceof PolygonArrowhead) {
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
		if (!(version == 1)) throw new IOException("Invalid version number.");
		else if (type == TYPE_BASIC_DERIVABLE_STROKE) {
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
		}
		else if (type == TYPE_CIRCLE_ARROWHEAD) {
			boolean f = stream.readBoolean();
			boolean s = stream.readBoolean();
			float cx = stream.readFloat();
			float cy = stream.readFloat();
			float w = stream.readFloat();
			float h = stream.readFloat();
			return new CircleArrowhead(cx, cy, w, h, f, s);
		}
		else if (type == TYPE_PATTERN_PAINT) {
			long p = stream.readLong();
			Paint fg = (Paint)SerializationManager.readObject(stream);
			Paint bg = (Paint)SerializationManager.readObject(stream);
			return new PatternPaint(fg,bg,p);
		}
		else if (type == TYPE_POLYGON_ARROWHEAD) {
			boolean f = stream.readBoolean();
			boolean s = stream.readBoolean();
			int np = stream.readInt();
			float[] p = new float[np];
			for (int i = 0; i < np; i++) {
				p[i] = stream.readFloat();
			}
			return new PolygonArrowhead(p, f, s);
		}
		else return null;
	}
}
