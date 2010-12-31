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
import java.util.Arrays;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;

public class RaaBitsFormat implements Format {
	public String getName() { return "RaaBits"; }
	public String getExpandedName() { return "RaaBits"; }
	public String getExtension() { return "rbit"; }
	public int getMacFileType() { return 0x52424954; }
	public int getMacResourceType() { return 0x52424954; }
	public long getDFFType() { return 0x496D672052424954L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.BITMAP; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.INDEXED_256; }
	public AlphaType getAlphaType() { return AlphaType.CHANNEL; }
	public LayerType getLayerType() { return LayerType.FLAT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 8; }
	public boolean acceptsMagic(byte[] start, long length) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(start));
			if (in.readInt() != 0x12AAB175) return false;
			if (in.readInt() != 0) return false;
			in.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("rbit") || ext.equalsIgnoreCase("raa"); }
	public boolean acceptsMacFileType(int type) { return type == 0x52424954; }
	public boolean acceptsMacResourceType(int type) { return type == 0x52424954; }
	public boolean acceptsDFFType(long type) { return type == 0x496D672052424954L || type == 0x496D672052414120L || type == 0x496D616765524141L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		if (in.readInt() != 0x12AAB175) throw new NotThisFormatException();
		if (in.readInt() != 0) throw new NotThisFormatException();
		int w = in.readInt();
		int h = in.readInt();
		int d = in.readInt();
		int md = in.readInt();
		int l = in.readInt();
		int ml = in.readInt();
		byte[] data = new byte[l];
		in.readFully(data);
		byte[] mdata = new byte[ml];
		in.readFully(mdata);
		int[] pixels = new int[w*h];
		Arrays.fill(pixels, 0xFF000000);
		if (d > 0 && l > 0) {
			int ppb = 8/d;
			int bpr = w*d; if ((bpr & 7) != 0) bpr = (bpr | 7) + 1; bpr >>>= 3;
			int bm = ((1 << d)-1);
			int[] table =
				(d <= 1) ? COLORS_1BIT :
				(d <= 2) ? COLORS_2BIT :
				(d <= 4) ? COLORS_4BIT :
				(d <= 8) ? COLORS_8BIT :
				COLORS_8BIT;
			for (int y = 0, py = 0, dy = 0; y < h && py < w*h && dy < l; y++, py += w, dy += bpr) {
				for (int x = 0, px = py, dx = dy; x < w && px < w*h && dx < l; x += ppb, px += ppb, dx++) {
					for (int p = 0, pp = px, dp = data[dx] & 0xFF, ds = 8-d; p < ppb && pp < w*h; p++, pp++, ds -= d) {
						pixels[pp] = table[(dp >>> ds) & bm];
					}
				}
			}
		}
		if (md > 0 && ml > 0) {
			int mppb = 8/md;
			int mbpr = w*md; if ((mbpr & 7) != 0) mbpr = (mbpr | 7) + 1; mbpr >>>= 3;
			int mbm = ((1 << md)-1);
			for (int y = 0, py = 0, dy = 0; y < h && py < w*h && dy < ml; y++, py += w, dy += mbpr) {
				for (int x = 0, px = py, dx = dy; x < w && px < w*h && dx < ml; x += mppb, px += mppb, dx++) {
					for (int p = 0, pp = px, dp = mdata[dx] & 0xFF, ds = 8-md; p < mppb && pp < w*h; p++, pp++, ds -= md) {
						pixels[pp] = (pixels[pp] & 0x00FFFFFF) | (((((dp >>> ds) & mbm)*0xFF)/mbm) << 24);
					}
				}
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
		return c.getWidth()*c.getHeight()*2;
	}
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {
		int w = c.getWidth();
		int h = c.getHeight();
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		c.paint(g);
		g.dispose();
		int[] pixels = new int[w*h];
		bi.getRGB(0, 0, w, h, pixels, 0, w);
		int d = calculateDepth(pixels);
		int md = calculateMaskDepth(pixels);
		byte[] data = null;
		byte[] mdata = null;
		if (d == 0) {
			data = new byte[0];
			d = 1;
		} else {
			int ppb = 8/d;
			int bpr = w*d; if ((bpr & 7) != 0) bpr = (bpr | 7) + 1; bpr >>>= 3;
			int bm = ((1 << d)-1);
			data = new byte[bpr*h];
			for (int y = 0, py = 0, dy = 0; y < h && py < w*h && dy < bpr*h; y++, py += w, dy += bpr) {
				for (int x = 0, px = py, dx = dy; x < w && px < w*h && dx < bpr*h; x += ppb, px += ppb, dx++) {
					data[dx] = 0;
					for (int p = 0, pp = px; p < ppb; p++, pp++) {
						data[dx] <<= d;
						if (pp < w*h) {
							int i =
								(d <= 1) ? reverse1Bit(pixels[pp]) :
								(d <= 2) ? reverse2Bit(pixels[pp]) :
								(d <= 4) ? reverse4Bit(pixels[pp]) :
								(d <= 8) ? reverse8Bit(pixels[pp]) :
								reverse8Bit(pixels[pp]);
							data[dx] |= (i & bm);
						}
					}
				}
			}
		}
		if (md == 0) {
			mdata = new byte[0];
			md = 1;
		} else {
			int mppb = 8/md;
			int mbpr = w*md; if ((mbpr & 7) != 0) mbpr = (mbpr | 7) + 1; mbpr >>>= 3;
			int mbm = ((1 << md)-1);
			mdata = new byte[mbpr*h];
			for (int y = 0, py = 0, dy = 0; y < h && py < w*h && dy < mbpr*h; y++, py += w, dy += mbpr) {
				for (int x = 0, px = py, dx = dy; x < w && px < w*h && dx < mbpr*h; x += mppb, px += mppb, dx++) {
					mdata[dx] = 0;
					for (int p = 0, pp = px; p < mppb; p++, pp++) {
						mdata[dx] <<= md;
						if (pp < w*h) {
							mdata[dx] |= ((int)Math.round((((pixels[pp] >>> 24) & 0xFF) * mbm)/255.0) & mbm);
						}
					}
				}
			}
		}
		out.writeInt(0x12AAB175);
		out.writeInt(0);
		out.writeInt(w);
		out.writeInt(h);
		out.writeInt(d);
		out.writeInt(md);
		out.writeInt(data.length);
		out.writeInt(mdata.length);
		out.write(data);
		out.write(mdata);
	}
	
	private static int calculateDepth(int[] pixels) {
		int d = 1;
		for (int pixel : pixels) {
			int p = pixel & 0xFFFFFF;
			switch (p) {
			case 0x000000:
			case 0xFFFFFF:
				if (d < 1) d = 1; break;
			case 0x808080:
				if (d < 2) d = 2; break;
			case 0xFCF305:
			case 0xFF6503:
			case 0xDD0907:
			case 0xF30885:
			case 0x4700A5:
			case 0x0000D4:
			case 0x02ABEB:
			case 0x1FB814:
			case 0x006512:
			case 0x562D05:
			case 0x91713A:
			case 0xC0C0C0:
			case 0x404040:
				if (d < 4) d = 4; break;
			default:
				if (d < 8) d = 8; break;
			}
		}
		return d;
	}
	
	private static int calculateMaskDepth(int[] pixels) {
		int md = 0;
		for (int pixel : pixels) {
			int a = (pixel >>> 24) & 0xFF;
			switch (a) {
			case 0xFF:
				if (md < 0) md = 0; break;
			case 0x00:
				if (md < 1) md = 1; break;
			case 0x55: case 0xAA:
				if (md < 2) md = 2; break;
			case 0x11: case 0x22: case 0x33: case 0x44:
			case 0x66: case 0x77: case 0x88: case 0x99:
			case 0xBB: case 0xCC: case 0xDD: case 0xEE:
				if (md < 4) md = 4; break;
			default:
				if (md < 8) md = 8; break;
			}
		}
		return md;
	}
	
	private static int reverse8Bit(int pixel) {
		if ((pixel & 0xFFFFFF) == 0xFFFFFF) return 0;
		if ((pixel & 0xFFFFFF) == 0x000000) return 255;
		int r = (int)Math.round(((pixel >>> 16) & 0xFF) / 17.0) & 0xF;
		int g = (int)Math.round(((pixel >>>  8) & 0xFF) / 17.0) & 0xF;
		int b = (int)Math.round(((pixel >>>  0) & 0xFF) / 17.0) & 0xF;
		if (r == g && g == b) {
			switch (r) {
			case 0xF: return 0;
			case 0xC: return 43;
			case 0x9: return 86;
			case 0x6: return 129;
			case 0x3: return 172;
			case 0xE: return 245;
			case 0xD: return 246;
			case 0xB: return 247;
			case 0xA: return 248;
			case 0x8: return 249;
			case 0x7: return 250;
			case 0x5: return 251;
			case 0x4: return 252;
			case 0x2: return 253;
			case 0x1: return 254;
			case 0x0: return 255;
			}
		}
		if (r == 0 && g == 0) {
			switch (b) {
			case 0xF: return 210;
			case 0xC: return 211;
			case 0x9: return 212;
			case 0x6: return 213;
			case 0x3: return 214;
			case 0xE: return 235;
			case 0xD: return 236;
			case 0xB: return 237;
			case 0xA: return 238;
			case 0x8: return 239;
			case 0x7: return 240;
			case 0x5: return 241;
			case 0x4: return 242;
			case 0x2: return 243;
			case 0x1: return 244;
			case 0x0: return 255;
			}
		}
		if (r == 0 && b == 0) {
			switch (g) {
			case 0xF: return 185;
			case 0xC: return 191;
			case 0x9: return 197;
			case 0x6: return 203;
			case 0x3: return 209;
			case 0xE: return 225;
			case 0xD: return 226;
			case 0xB: return 227;
			case 0xA: return 228;
			case 0x8: return 229;
			case 0x7: return 230;
			case 0x5: return 231;
			case 0x4: return 232;
			case 0x2: return 233;
			case 0x1: return 234;
			case 0x0: return 255;
			}
		}
		if (g == 0 && b == 0) {
			switch (r) {
			case 0xF: return 35;
			case 0xC: return 71;
			case 0x9: return 107;
			case 0x6: return 143;
			case 0x3: return 179;
			case 0xE: return 215;
			case 0xD: return 216;
			case 0xB: return 217;
			case 0xA: return 218;
			case 0x8: return 219;
			case 0x7: return 220;
			case 0x5: return 221;
			case 0x4: return 222;
			case 0x2: return 223;
			case 0x1: return 224;
			case 0x0: return 255;
			}
		}
		r = 5 - ((int)Math.round(((pixel >>> 16) & 0xFF) / 51.0) & 0x7);
		g = 5 - ((int)Math.round(((pixel >>>  8) & 0xFF) / 51.0) & 0x7);
		b = 5 - ((int)Math.round(((pixel >>>  0) & 0xFF) / 51.0) & 0x7);
		if (r == 5 && g == 5 && b == 5) return 255;
		else return r*36 + g*6 + b;
	}
	
	private static int reverse4Bit(int pixel) {
		int r = ((pixel >>> 16) & 0xFF);
		int g = ((pixel >>>  8) & 0xFF);
		int b = ((pixel >>>  0) & 0xFF);
		int cc = 0;
		int cd = 1000;
		for (int i = 0; i < 16; i++) {
			int c = COLORS_4BIT[i];
			int cr = ((c >>> 16) & 0xFF);
			int cg = ((c >>>  8) & 0xFF);
			int cb = ((c >>>  0) & 0xFF);
			int d = Math.abs(cr-r)+Math.abs(cg-g)+Math.abs(cb-b);
			if (d < cd) {
				cc = i;
				cd = d;
			}
		}
		return cc;
	}
	
	private static int reverse2Bit(int pixel) {
		int r = ((pixel >>> 16) & 0xFF);
		int g = ((pixel >>>  8) & 0xFF);
		int b = ((pixel >>>  0) & 0xFF);
		int k = (30*r + 59*g + 11*b) / 100;
		return (k >= 170) ? 0 : (k >= 85) ? 1 : 3;
	}
	
	private static int reverse1Bit(int pixel) {
		int r = ((pixel >>> 16) & 0xFF);
		int g = ((pixel >>>  8) & 0xFF);
		int b = ((pixel >>>  0) & 0xFF);
		int k = (30*r + 59*g + 11*b) / 100;
		return (k >= 0x80) ? 0 : 1;
	}
	
	private static final int[] COLORS_8BIT = {
		0xFFFFFFFF, 0xFFFFFFCC, 0xFFFFFF99, 0xFFFFFF66, 0xFFFFFF33, 0xFFFFFF00, 0xFFFFCCFF, 0xFFFFCCCC,
		0xFFFFCC99, 0xFFFFCC66, 0xFFFFCC33, 0xFFFFCC00, 0xFFFF99FF, 0xFFFF99CC, 0xFFFF9999, 0xFFFF9966,
		0xFFFF9933, 0xFFFF9900, 0xFFFF66FF, 0xFFFF66CC, 0xFFFF6699, 0xFFFF6666, 0xFFFF6633, 0xFFFF6600,
		0xFFFF33FF, 0xFFFF33CC, 0xFFFF3399, 0xFFFF3366, 0xFFFF3333, 0xFFFF3300, 0xFFFF00FF, 0xFFFF00CC,
		0xFFFF0099, 0xFFFF0066, 0xFFFF0033, 0xFFFF0000, 0xFFCCFFFF, 0xFFCCFFCC, 0xFFCCFF99, 0xFFCCFF66,
		0xFFCCFF33, 0xFFCCFF00, 0xFFCCCCFF, 0xFFCCCCCC, 0xFFCCCC99, 0xFFCCCC66, 0xFFCCCC33, 0xFFCCCC00,
		0xFFCC99FF, 0xFFCC99CC, 0xFFCC9999, 0xFFCC9966, 0xFFCC9933, 0xFFCC9900, 0xFFCC66FF, 0xFFCC66CC,
		0xFFCC6699, 0xFFCC6666, 0xFFCC6633, 0xFFCC6600, 0xFFCC33FF, 0xFFCC33CC, 0xFFCC3399, 0xFFCC3366,
		0xFFCC3333, 0xFFCC3300, 0xFFCC00FF, 0xFFCC00CC, 0xFFCC0099, 0xFFCC0066, 0xFFCC0033, 0xFFCC0000,
		0xFF99FFFF, 0xFF99FFCC, 0xFF99FF99, 0xFF99FF66, 0xFF99FF33, 0xFF99FF00, 0xFF99CCFF, 0xFF99CCCC,
		0xFF99CC99, 0xFF99CC66, 0xFF99CC33, 0xFF99CC00, 0xFF9999FF, 0xFF9999CC, 0xFF999999, 0xFF999966,
		0xFF999933, 0xFF999900, 0xFF9966FF, 0xFF9966CC, 0xFF996699, 0xFF996666, 0xFF996633, 0xFF996600,
		0xFF9933FF, 0xFF9933CC, 0xFF993399, 0xFF993366, 0xFF993333, 0xFF993300, 0xFF9900FF, 0xFF9900CC,
		0xFF990099, 0xFF990066, 0xFF990033, 0xFF990000, 0xFF66FFFF, 0xFF66FFCC, 0xFF66FF99, 0xFF66FF66,
		0xFF66FF33, 0xFF66FF00, 0xFF66CCFF, 0xFF66CCCC, 0xFF66CC99, 0xFF66CC66, 0xFF66CC33, 0xFF66CC00,
		0xFF6699FF, 0xFF6699CC, 0xFF669999, 0xFF669966, 0xFF669933, 0xFF669900, 0xFF6666FF, 0xFF6666CC,
		0xFF666699, 0xFF666666, 0xFF666633, 0xFF666600, 0xFF6633FF, 0xFF6633CC, 0xFF663399, 0xFF663366,
		0xFF663333, 0xFF663300, 0xFF6600FF, 0xFF6600CC, 0xFF660099, 0xFF660066, 0xFF660033, 0xFF660000,
		0xFF33FFFF, 0xFF33FFCC, 0xFF33FF99, 0xFF33FF66, 0xFF33FF33, 0xFF33FF00, 0xFF33CCFF, 0xFF33CCCC,
		0xFF33CC99, 0xFF33CC66, 0xFF33CC33, 0xFF33CC00, 0xFF3399FF, 0xFF3399CC, 0xFF339999, 0xFF339966,
		0xFF339933, 0xFF339900, 0xFF3366FF, 0xFF3366CC, 0xFF336699, 0xFF336666, 0xFF336633, 0xFF336600,
		0xFF3333FF, 0xFF3333CC, 0xFF333399, 0xFF333366, 0xFF333333, 0xFF333300, 0xFF3300FF, 0xFF3300CC,
		0xFF330099, 0xFF330066, 0xFF330033, 0xFF330000, 0xFF00FFFF, 0xFF00FFCC, 0xFF00FF99, 0xFF00FF66,
		0xFF00FF33, 0xFF00FF00, 0xFF00CCFF, 0xFF00CCCC, 0xFF00CC99, 0xFF00CC66, 0xFF00CC33, 0xFF00CC00,
		0xFF0099FF, 0xFF0099CC, 0xFF009999, 0xFF009966, 0xFF009933, 0xFF009900, 0xFF0066FF, 0xFF0066CC,
		0xFF006699, 0xFF006666, 0xFF006633, 0xFF006600, 0xFF0033FF, 0xFF0033CC, 0xFF003399, 0xFF003366,
		0xFF003333, 0xFF003300, 0xFF0000FF, 0xFF0000CC, 0xFF000099, 0xFF000066, 0xFF000033, 0xFFEE0000,
		0xFFDD0000, 0xFFBB0000, 0xFFAA0000, 0xFF880000, 0xFF770000, 0xFF550000, 0xFF440000, 0xFF220000,
		0xFF110000, 0xFF00EE00, 0xFF00DD00, 0xFF00BB00, 0xFF00AA00, 0xFF008800, 0xFF007700, 0xFF005500,
		0xFF004400, 0xFF002200, 0xFF001100, 0xFF0000EE, 0xFF0000DD, 0xFF0000BB, 0xFF0000AA, 0xFF000088,
		0xFF000077, 0xFF000055, 0xFF000044, 0xFF000022, 0xFF000011, 0xFFEEEEEE, 0xFFDDDDDD, 0xFFBBBBBB,
		0xFFAAAAAA, 0xFF888888, 0xFF777777, 0xFF555555, 0xFF444444, 0xFF222222, 0xFF111111, 0xFF000000
	};
	
	private static final int[] COLORS_4BIT = {
		0xFFFFFFFF, 0xFFFCF305, 0xFFFF6503, 0xFFDD0907, 0xFFF30885, 0xFF4700A5, 0xFF0000D4, 0xFF02ABEB,
		0xFF1FB814, 0xFF006512, 0xFF562D05, 0xFF91713A, 0xFFC0C0C0, 0xFF808080, 0xFF404040, 0xFF000000
	};
	
	private static final int[] COLORS_2BIT = {
		0xFFFFFFFF, 0xFF808080, 0xFFCCCCFF, 0xFF000000
	};
	
	private static final int[] COLORS_1BIT = {
		0xFFFFFFFF, 0xFF000000
	};
}
