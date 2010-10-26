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

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.util.*;

public class ColorTable {
	public int ctSeed; // unique identifier from table
	public int ctFlags; // flags describing the value in the ctTable field; clear for a pixel map
	public int ctSize; // number of entries in the next field minus 1
	public List<ColorSpec> ctTable; // an array of ColorSpec records
	
	public static ColorTable read(DataInputStream in) throws IOException {
		ColorTable t = new ColorTable();
		t.ctSeed = in.readInt();
		t.ctFlags = in.readUnsignedShort();
		t.ctSize = in.readShort();
		t.ctTable = new Vector<ColorSpec>();
		for (int i = 0; i <= t.ctSize; i++) {
			t.ctTable.add(ColorSpec.read(in));
		}
		return t;
	}
	
	public ColorTable() {
		this.ctSeed = 0;
		this.ctFlags = 0;
		this.ctSize = -1;
		this.ctTable = new Vector<ColorSpec>();
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(ctSeed);
		out.writeShort(ctFlags);
		out.writeShort(ctSize = ctTable.size()-1);
		for (ColorSpec c : ctTable) {
			c.write(out);
		}
	}
	
	public int[] toIntArray() {
		int[] a = new int[ctTable.size()];
		int i = 0;
		for (ColorSpec c : ctTable) {
			a[i++] = c.toRGB();
		}
		return a;
	}
	
	public Color[] toColorArray() {
		Color[] a = new Color[ctTable.size()];
		int i = 0;
		for (ColorSpec c : ctTable) {
			a[i++] = c.toColor();
		}
		return a;
	}
	
	public int[] toIntArrayByPixelIndex(int size) {
		for (ColorSpec c : ctTable) {
			if (c.value+1 > size) size = c.value+1;
		}
		int[] a = new int[size];
		for (ColorSpec c : ctTable) {
			a[c.value] = c.toRGB();
		}
		return a;
	}
	
	public Color[] toColorArrayByPixelIndex(int size) {
		for (ColorSpec c : ctTable) {
			if (c.value+1 > size) size = c.value+1;
		}
		Color[] a = new Color[size];
		for (ColorSpec c : ctTable) {
			a[c.value] = c.toColor();
		}
		return a;
	}
	
	public IndexColorModel toIndexColorModel(int bits) {
		byte[] r = new byte[ctTable.size()];
		byte[] g = new byte[ctTable.size()];
		byte[] b = new byte[ctTable.size()];
		int i = 0;
		for (ColorSpec c : ctTable) {
			r[i] = (byte)(c.rgb.red/257);
			g[i] = (byte)(c.rgb.green/257);
			b[i] = (byte)(c.rgb.blue/257);
			i++;
		}
		return new IndexColorModel(bits, ctTable.size(), r, g, b);
	}
	
	public IndexColorModel toIndexColorModelByPixelIndex(int bits, int size) {
		for (ColorSpec c : ctTable) {
			if (c.value+1 > size) size = c.value+1;
		}
		byte[] r = new byte[size];
		byte[] g = new byte[size];
		byte[] b = new byte[size];
		for (ColorSpec c : ctTable) {
			r[c.value] = (byte)(c.rgb.red/257);
			g[c.value] = (byte)(c.rgb.green/257);
			b[c.value] = (byte)(c.rgb.blue/257);
		}
		return new IndexColorModel(bits, ctTable.size(), r, g, b);
	}
	
	public String toString() {
		return "ColorTable["+ctSeed+","+ctFlags+","+ctTable.size()+"]";
	}
}
