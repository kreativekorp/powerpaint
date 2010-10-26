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

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.Map;
import com.kreative.paint.gradient.*;

public class CKPGradientSerializer extends Serializer {
	private static final int TYPE_ANGULAR_GS = fcc("$Ang");
	private static final int TYPE_GRADIENT = fcc("Grad");
	private static final int TYPE_GRADIENT_COLOR_MAP = fcc("GCMp");
	private static final int TYPE_GRADIENT_PAINT_2 = fcc("GPn2");
	private static final int TYPE_LINEAR_GS = fcc("$Lin");
	private static final int TYPE_RADIAL_GS = fcc("$Rad");
	private static final int TYPE_RECTANGULAR_GS = fcc("$Rec");
	
	protected void loadRecognizedTypesAndClasses() {
		addTypeAndClass(TYPE_ANGULAR_GS, 1, AngularGradientShape.class);
		addTypeAndClass(TYPE_GRADIENT, 1, Gradient.class);
		addTypeAndClass(TYPE_GRADIENT_COLOR_MAP, 1, GradientColorMap.class);
		addTypeAndClass(TYPE_GRADIENT_PAINT_2, 1, GradientPaint2.class);
		addTypeAndClass(TYPE_LINEAR_GS, 1, LinearGradientShape.class);
		addTypeAndClass(TYPE_RADIAL_GS, 1, RadialGradientShape.class);
		addTypeAndClass(TYPE_RECTANGULAR_GS, 1, RectangularGradientShape.class);
	}
	
	public void serializeObject(Object o, DataOutputStream stream) throws IOException {
		if (o instanceof AngularGradientShape) {
			AngularGradientShape v = (AngularGradientShape)o;
			stream.writeDouble(v.centerX);
			stream.writeDouble(v.centerY);
			stream.writeDouble(v.poleX);
			stream.writeDouble(v.poleY);
			stream.writeBoolean(v.repeat);
			stream.writeBoolean(v.reflect);
			stream.writeBoolean(v.reverse);
		}
		else if (o instanceof Gradient) {
			Gradient v = (Gradient)o;
			SerializationManager.writeObject(v.shape, stream);
			SerializationManager.writeObject(v.colorMap, stream);
			SerializationManager.writeObject(v.boundingRect, stream);
		}
		else if (o instanceof GradientColorMap) {
			GradientColorMap v = (GradientColorMap)o;
			stream.writeInt(v.size());
			for (Map.Entry<Double,Color> e : v.entrySet()) {
				stream.writeDouble(e.getKey());
				float[] rgb = e.getValue().getRGBComponents(null);
				stream.writeFloat(rgb[0]);
				stream.writeFloat(rgb[1]);
				stream.writeFloat(rgb[2]);
				stream.writeFloat(rgb[3]);
			}
		}
		else if (o instanceof GradientPaint2) {
			GradientPaint2 v = (GradientPaint2)o;
			SerializationManager.writeObject(v.getGradientShape(), stream);
			SerializationManager.writeObject(v.getGradientColorMap(), stream);
			SerializationManager.writeObject(v.getGradientBounds(), stream);
		}
		else if (o instanceof LinearGradientShape) {
			LinearGradientShape v = (LinearGradientShape)o;
			stream.writeDouble(v.zeroX);
			stream.writeDouble(v.zeroY);
			stream.writeDouble(v.oneX);
			stream.writeDouble(v.oneY);
			stream.writeBoolean(v.repeat);
			stream.writeBoolean(v.reflect);
			stream.writeBoolean(v.reverse);
		}
		else if (o instanceof RadialGradientShape) {
			RadialGradientShape v = (RadialGradientShape)o;
			stream.writeDouble(v.centerX);
			stream.writeDouble(v.centerY);
			stream.writeDouble(v.zeroX);
			stream.writeDouble(v.zeroY);
			stream.writeDouble(v.oneX);
			stream.writeDouble(v.oneY);
			stream.writeBoolean(v.repeat);
			stream.writeBoolean(v.reflect);
			stream.writeBoolean(v.reverse);
		}
		else if (o instanceof RectangularGradientShape) {
			RectangularGradientShape v = (RectangularGradientShape)o;
			stream.writeDouble(v.zeroX1);
			stream.writeDouble(v.zeroY1);
			stream.writeDouble(v.zeroX2);
			stream.writeDouble(v.zeroY2);
			stream.writeDouble(v.oneX1);
			stream.writeDouble(v.oneY1);
			stream.writeDouble(v.oneX2);
			stream.writeDouble(v.oneY2);
			stream.writeBoolean(v.repeat);
			stream.writeBoolean(v.reflect);
			stream.writeBoolean(v.reverse);
		}
	}
	
	public Object deserializeObject(int type, int version, DataInputStream stream) throws IOException {
		if (version != 1) throw new IOException("Invalid version number.");
		else if (type == TYPE_ANGULAR_GS) {
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double px = stream.readDouble();
			double py = stream.readDouble();
			boolean rep = stream.readBoolean();
			boolean ref = stream.readBoolean();
			boolean rev = stream.readBoolean();
			return new AngularGradientShape(cx,cy,px,py,rep,ref,rev);
		}
		else if (type == TYPE_GRADIENT) {
			GradientShape gs = (GradientShape)SerializationManager.readObject(stream);
			GradientColorMap gc = (GradientColorMap)SerializationManager.readObject(stream);
			Rectangle2D gb = (Rectangle2D)SerializationManager.readObject(stream);
			return new Gradient(gs,gc,gb);
		}
		else if (type == TYPE_GRADIENT_COLOR_MAP) {
			GradientColorMap gcm = new GradientColorMap();
			int n = stream.readInt();
			for (int i = 0; i < n; i++) {
				double p = stream.readDouble();
				float r = stream.readFloat();
				float g = stream.readFloat();
				float b = stream.readFloat();
				float a = stream.readFloat();
				gcm.put(p, new Color(r,g,b,a));
			}
			return gcm;
		}
		else if (type == TYPE_GRADIENT_PAINT_2) {
			GradientShape gs = (GradientShape)SerializationManager.readObject(stream);
			GradientColorMap gc = (GradientColorMap)SerializationManager.readObject(stream);
			Rectangle2D gb = (Rectangle2D)SerializationManager.readObject(stream);
			return new GradientPaint2(gs,gc,gb);
		}
		else if (type == TYPE_LINEAR_GS) {
			double zx = stream.readDouble();
			double zy = stream.readDouble();
			double ox = stream.readDouble();
			double oy = stream.readDouble();
			boolean rep = stream.readBoolean();
			boolean ref = stream.readBoolean();
			boolean rev = stream.readBoolean();
			return new LinearGradientShape(zx,zy,ox,oy,rep,ref,rev);
		}
		else if (type == TYPE_RADIAL_GS) {
			double cx = stream.readDouble();
			double cy = stream.readDouble();
			double zx = stream.readDouble();
			double zy = stream.readDouble();
			double ox = stream.readDouble();
			double oy = stream.readDouble();
			boolean rep = stream.readBoolean();
			boolean ref = stream.readBoolean();
			boolean rev = stream.readBoolean();
			return new RadialGradientShape(cx,cy,zx,zy,ox,oy,rep,ref,rev);
		}
		else if (type == TYPE_RECTANGULAR_GS) {
			double zx1 = stream.readDouble();
			double zy1 = stream.readDouble();
			double zx2 = stream.readDouble();
			double zy2 = stream.readDouble();
			double ox1 = stream.readDouble();
			double oy1 = stream.readDouble();
			double ox2 = stream.readDouble();
			double oy2 = stream.readDouble();
			boolean rep = stream.readBoolean();
			boolean ref = stream.readBoolean();
			boolean rev = stream.readBoolean();
			return new RectangularGradientShape(zx1,zy1,zx2,zy2,ox1,oy1,ox2,oy2,rep,ref,rev);
		}
		else return null;
	}
}
