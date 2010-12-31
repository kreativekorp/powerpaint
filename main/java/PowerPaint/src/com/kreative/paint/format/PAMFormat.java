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

package com.kreative.paint.format;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;

public class PAMFormat implements Format {
	public String getName() { return "PAM"; }
	public String getExpandedName() { return "Portable Arbitrary Map"; }
	public String getExtension() { return "pam"; }
	public int getMacFileType() { return 0x50414D20; }
	public int getMacResourceType() { return 0x50414D20; }
	public long getDFFType() { return 0x496D61676550414DL; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.BITMAP; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.RGB_8; }
	public AlphaType getAlphaType() { return AlphaType.CHANNEL; }
	public LayerType getLayerType() { return LayerType.FLAT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 3; }
	public boolean acceptsMagic(byte[] start, long length) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(start));
			if (in.readShort() != (short)0x5037) return false;
			if (!isLineBreak(in.readByte())) return false;
			in.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("pam"); }
	public boolean acceptsMacFileType(int type) { return type == 0x50414D20 || type == 0x50414D66 || type == 0x50414D6D; }
	public boolean acceptsMacResourceType(int type) { return type == 0x50414D20 || type == 0x50414D66 || type == 0x50414D6D; }
	public boolean acceptsDFFType(long type) { return type == 0x496D61676550414DL || type == 0x496D672050414D20L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor mon) throws IOException {
		if (in.readShort() != (short)0x5037) throw new NotThisFormatException();
		if (!isLineBreak(in.readByte())) throw new NotThisFormatException();
		int w = 0;
		int h = 0;
		int d = 0;
		int m = 0;
		//String t = "";
		while (true) {
			String s = readLine(in).trim();
			if (s.length() > 0 && !s.startsWith("#")) {
				String[] ss = s.split("\\s+");
				if (ss[0].equalsIgnoreCase("ENDHDR")) {
					break;
				}
				else if (ss[0].equalsIgnoreCase("WIDTH") && ss.length > 1) {
					w = Integer.parseInt(ss[1]);
				}
				else if (ss[0].equalsIgnoreCase("HEIGHT") && ss.length > 1) {
					h = Integer.parseInt(ss[1]);
				}
				else if (ss[0].equalsIgnoreCase("DEPTH") && ss.length > 1) {
					d = Integer.parseInt(ss[1]);
				}
				else if (ss[0].equalsIgnoreCase("MAXVAL") && ss.length > 1) {
					m = Integer.parseInt(ss[1]);
				}
				else if (ss[0].equalsIgnoreCase("TUPLTYPE") && ss.length > 1) {
					//t = ss[1].toUpperCase();
				}
			}
		}
		int[] pixels = new int[w*h];
		for (int i = 0; i < pixels.length; i++) {
			switch (d) {
			case 1:
				{
					int v = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					pixels[i] = 0xFF000000 | (v << 16) | (v << 8) | v;
				}
				break;
			case 2:
				{
					int v = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					int a = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					pixels[i] = (a << 24) | (v << 16) | (v << 8) | v;
				}
				break;
			case 3:
				{
					int r = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					int g = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					int b = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					pixels[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
				}
				break;
			case 4:
				{
					int r = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					int g = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					int b = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					int a = ((m >= 256) ? (in.readShort() & 0xFFFF) : (in.readByte() & 0xFF))*255/m;
					pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
				}
				break;
			}
		}
		Canvas c = new Canvas(w, h);
		c.get(0).setRGB(0, 0, w, h, pixels, 0, w);
		return c;
	}
	
	public boolean supportsWrite() { return true; }
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) {
		return c.getWidth()*c.getHeight()*4;
	}
	public void write(Canvas c, DataOutputStream out, Monitor mon) throws IOException {
		int w = c.getWidth();
		int h = c.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		c.paint(g);
		g.dispose();
		int[] pixels = new int[w*h];
		bi.getRGB(0, 0, w, h, pixels, 0, w);
		int tt = determineTupleType(pixels);
		int d = TUPLE_TYPE_DEPTH[tt];
		int m = TUPLE_TYPE_MAXVAL[tt];
		String t = TUPLE_TYPE_STRING[tt];
		out.writeShort(0x5037);
		out.writeByte(0x0A);
		out.writeBytes("WIDTH "+w);
		out.writeByte(0x0A);
		out.writeBytes("HEIGHT "+h);
		out.writeByte(0x0A);
		out.writeBytes("DEPTH "+d);
		out.writeByte(0x0A);
		out.writeBytes("MAXVAL "+m);
		out.writeByte(0x0A);
		out.writeBytes("TUPLTYPE "+t);
		out.writeByte(0x0A);
		out.writeBytes("ENDHDR");
		out.writeByte(0x0A);
		for (int pixel : pixels) {
			if (tt == 0 || tt == 3) {
				out.writeByte((isBlack(pixel) < 0x80) ? 0 : 1);
			}
			if (tt == 1 || tt == 4) {
				out.writeByte(isBlack(pixel));
			}
			if (tt == 2 || tt == 5) {
				out.writeByte((pixel >>> 16) & 0xFF);
				out.writeByte((pixel >>>  8) & 0xFF);
				out.writeByte((pixel >>>  0) & 0xFF);
			}
			if (tt == 3) {
				out.writeByte((((pixel >>> 24) & 0xFF) < 0x80) ? 0 : 1);
			}
			if (tt == 4 || tt == 5) {
				out.writeByte((pixel >>> 24) & 0xFF);
			}
		}
	}
	
	private static boolean isLineBreak(byte b) {
		return ((b == (byte)0x0A) || (b == (byte)0x0D));
	}

	private static int isBlack(int pixel) {
		int r = ((pixel >>> 16) & 0xFF);
		int g = ((pixel >>>  8) & 0xFF);
		int b = ((pixel >>>  0) & 0xFF);
		int k = (30*r + 59*g + 11*b) / 100;
		return k;
	}
	
	private static String readLine(DataInputStream in) throws IOException {
		StringBuffer sb = new StringBuffer();
		while (true) {
			byte b = in.readByte();
			if (isLineBreak(b)) return sb.toString();
			else sb.append((char)(b & 0xFF));
		}
	}
	
	private static final String[] TUPLE_TYPE_STRING = new String[] {
		"BLACKANDWHITE", "GRAYSCALE", "RGB",
		"BLACKANDWHITE_ALPHA", "GRAYSCALE_ALPHA", "RGB_ALPHA",
	};
	private static final int[] TUPLE_TYPE_DEPTH = new int[] { 1, 1, 3, 2, 2, 4 };
	private static final int[] TUPLE_TYPE_MAXVAL = new int[] { 1, 255, 255, 1, 255, 255 };
	
	private static int determineTupleType(int[] pixels) {
		boolean alpha = false;
		int complexity = 0;
		for (int pixel : pixels) {
			int a = (pixel >>> 24) & 0xFF;
			int rgb = pixel & 0xFFFFFF;
			if (a != 0xFF) alpha = true;
			if (a == 0x00 || a == 0xFF) {
				if (complexity < 0) complexity = 0;
			} else {
				if (complexity < 1) complexity = 1;
			}
			if (rgb == 0x000000 || rgb == 0xFFFFFF) {
				if (complexity < 0) complexity = 0;
			} else {
				int r = (pixel >>> 16) & 0xFF;
				int g = (pixel >>> 8) & 0xFF;
				int b = pixel & 0xFF;
				if (r == g && g == b) {
					if (complexity < 1) complexity = 1;
				} else {
					if (complexity < 2) complexity = 2;
				}
			}
		}
		if (alpha) complexity += 3;
		return complexity;
	}
}
