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

package com.kreative.paint.pict;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.ColorModel;
import java.io.*;

public class RGBColor implements Paint {
	public int red;
	public int green;
	public int blue;
	
	public static RGBColor read(DataInputStream in) throws IOException {
		RGBColor c = new RGBColor();
		c.red = in.readUnsignedShort();
		c.green = in.readUnsignedShort();
		c.blue = in.readUnsignedShort();
		return c;
	}
	
	public RGBColor() {
		red = 0;
		green = 0;
		blue = 0;
	}
	
	public RGBColor(int rgb) {
		red = ((rgb >>> 16) & 0xFF) * 257;
		green = ((rgb >>> 8) & 0xFF) * 257;
		blue = (rgb & 0xFF) * 257;
	}
	
	public RGBColor(int r, int g, int b) {
		red = r*257;
		green = g*257;
		blue = b*257;
	}
	
	public RGBColor(float r, float g, float b) {
		red = (int)(r*65535);
		green = (int)(g*65535);
		blue = (int)(b*65535);
	}
	
	public RGBColor(Color c) {
		red = c.getRed()*257;
		green = c.getGreen()*257;
		blue = c.getBlue()*257;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeShort(red);
		out.writeShort(green);
		out.writeShort(blue);
	}
	
	public Color toColor() {
		return new Color(red/65535.0f, green/65535.0f, blue/65535.0f);
	}
	
	public int toRGB() {
		return 0xFF000000 | ((red / 257) << 16) | ((green / 257) << 8) | (blue / 257);
	}
	
	public String toString() {
		return red+","+green+","+blue;
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		return toColor().createContext(cm, deviceBounds, userBounds, xform, hints);
	}

	public int getTransparency() {
		return OPAQUE;
	}
}
