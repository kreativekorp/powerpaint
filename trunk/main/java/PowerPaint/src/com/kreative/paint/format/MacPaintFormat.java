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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;

public class MacPaintFormat implements Format {
	public String getName() { return "MacPaint"; }
	public String getExpandedName() { return "MacPaint"; }
	public String getExtension() { return "mpnt"; }
	public int getMacFileType() { return 0x504E5447; }
	public int getMacResourceType() { return 0x504E5447; }
	public long getDFFType() { return 0x496D67204D504E54L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.BITMAP; }
	public SizeType getSizeType() { return SizeType.FIXED; }
	public ColorType getColorType() { return ColorType.BLACK_AND_WHITE; }
	public AlphaType getAlphaType() { return AlphaType.OPAQUE; }
	public LayerType getLayerType() { return LayerType.FLAT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 0; }
	public boolean acceptsMagic(byte[] start, long length) { return false; }
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("mac") || ext.equalsIgnoreCase("pnt") || ext.equalsIgnoreCase("mpnt") || ext.equalsIgnoreCase("mpt"); }
	public boolean acceptsMacFileType(int type) { return type == 0x504E5447; }
	public boolean acceptsMacResourceType(int type) { return type == 0x504E5447; }
	public boolean acceptsDFFType(long type) { return type == 0x496D67204D504E54L; }
	
	public boolean supportsRead() { return true; }
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		int p;
		in.readFully(new byte[512]);
		byte[] data = new byte[51840];
		p = 0;
		while (p < data.length) {
			int v = in.readByte();
			if (v < 0) {
				int count = -v+1;
				byte d = in.readByte();
				while (count-- > 0 && p < data.length) {
					data[p++] = d;
				}
			} else {
				int count = v+1;
				while (count-- > 0 && p < data.length) {
					data[p++] = in.readByte();
				}
			}
		}
		int[] pixels = new int[414720];
		p = 0;
		for (byte b : data) {
			pixels[p++] = ((b & 0x80) != 0) ? 0xFF000000 : 0xFFFFFFFF;
			pixels[p++] = ((b & 0x40) != 0) ? 0xFF000000 : 0xFFFFFFFF;
			pixels[p++] = ((b & 0x20) != 0) ? 0xFF000000 : 0xFFFFFFFF;
			pixels[p++] = ((b & 0x10) != 0) ? 0xFF000000 : 0xFFFFFFFF;
			pixels[p++] = ((b & 0x08) != 0) ? 0xFF000000 : 0xFFFFFFFF;
			pixels[p++] = ((b & 0x04) != 0) ? 0xFF000000 : 0xFFFFFFFF;
			pixels[p++] = ((b & 0x02) != 0) ? 0xFF000000 : 0xFFFFFFFF;
			pixels[p++] = ((b & 0x01) != 0) ? 0xFF000000 : 0xFFFFFFFF;
		}
		Canvas c = new Canvas(576, 720);
		c.get(0).setRGB(0, 0, 576, 720, pixels, 0, 576);
		return c;
	}
	
	public boolean supportsWrite() { return true; }
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) {
		return 32768;
	}
	public void write(Canvas c, DataOutputStream out, Monitor m) throws IOException {
		BufferedImage bi = new BufferedImage(576, 720, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		c.paint(g);
		g.dispose();
		int[] pixels = new int[414720];
		bi.getRGB(0, 0, 576, 720, pixels, 0, 576);
		out.write(new byte[512]);
		byte[] row = new byte[72];
		byte[] runs = new byte[144];
		byte[] runs2 = new byte[144];
		for (int y = 0, py = 0; y < 720 && py < 414720; y++, py += 576) {
			for (int x = 0, px = py, rx = 0; x < 576 && px < 414720 && rx < 72; x += 8, px += 8, rx++) {
				row[rx] = 0;
				for (int p = 0, pp = px; p < 8 && pp < 414720; p++, pp++) {
					row[rx] <<= 1;
					if (isOpaque(pixels[pp]) && isBlack(pixels[pp])) row[rx] |= 1;
				}
			}
			int rp = 0;
			for (byte b : row) {
				if (rp >= 2 && runs[rp-1] == b) {
					runs[rp-2]++;
				} else {
					runs[rp++] = 1;
					runs[rp++] = b;
				}
			}
			int rp2 = 0;
			int rpc = 0;
			for (int i = 0; i < rp; i += 2) {
				int cnt = runs[i];
				byte b = runs[i+1];
				if (cnt > 1) {
					runs2[rp2++] = (byte)(-cnt+1);
					runs2[rp2++] = b;
					rpc = rp2;
				} else {
					if (rpc == rp2) {
						runs2[rp2++] = 0;
						runs2[rp2++] = b;
					} else {
						runs2[rpc]++;
						runs2[rp2++] = b;
					}
				}
			}
			out.write(runs2, 0, rp2);
		}
	}

	private static boolean isBlack(int pixel) {
		int r = ((pixel >>> 16) & 0xFF);
		int g = ((pixel >>>  8) & 0xFF);
		int b = ((pixel >>>  0) & 0xFF);
		int k = (30*r + 59*g + 11*b) / 100;
		return (k < 0x80);
	}
	
	private static boolean isOpaque(int pixel) {
		int a = ((pixel >>> 24) & 0xFF);
		return (a >= 0x80);
	}
}
