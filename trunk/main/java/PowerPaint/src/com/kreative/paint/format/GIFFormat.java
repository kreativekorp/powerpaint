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

package com.kreative.paint.format;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import com.kreative.paint.Canvas;
import com.kreative.paint.form.Form;
import com.kreative.paint.io.Monitor;

public class GIFFormat implements Format {
	public String getName() { return "GIF"; }
	public String getExpandedName() { return "Graphics Interchange Format"; }
	public String getExtension() { return "gif"; }
	public int getMacFileType() { return 0x47494620; }
	public int getMacResourceType() { return 0x47494620; }
	public long getDFFType() { return 0x496D616765474946L; }
	
	public MediaType getMediaType() { return MediaType.IMAGE; }
	public GraphicType getGraphicType() { return GraphicType.BITMAP; }
	public SizeType getSizeType() { return SizeType.ARBITRARY; }
	public ColorType getColorType() { return ColorType.INDEXED_256; }
	public AlphaType getAlphaType() { return AlphaType.OPAQUE_AND_TRANSPARENT; }
	public LayerType getLayerType() { return LayerType.FLAT; }
	
	public boolean onlyUponRequest() { return false; }
	public int usesMagic() { return 6; }
	public boolean acceptsMagic(byte[] start, long length) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(start));
			long magic = 0;
			magic = (magic << 16) | (in.readShort() & 0xFFFF);
			magic = (magic << 16) | (in.readShort() & 0xFFFF);
			magic = (magic << 16) | (in.readShort() & 0xFFFF);
			if (!(magic == 0x474946383761L || magic == 0x474946383961L)) return false;
			in.close();
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
	public boolean acceptsExtension(String ext) { return ext.equalsIgnoreCase("gif"); }
	public boolean acceptsMacFileType(int type) { return type == 0x47494620 || type == 0x47494666; }
	public boolean acceptsMacResourceType(int type) { return type == 0x47494620 || type == 0x47494666; }
	public boolean acceptsDFFType(long type) { return type == 0x496D616765474946L || type == 0x496D672047494620L; }
	
	public boolean supportsRead() {
		for (String s : ImageIO.getReaderFormatNames()) {
			if (s.equalsIgnoreCase("gif")) return true;
		}
		return false;
	}
	public boolean usesReadOptionForm() { return false; }
	public Form getReadOptionForm() { return null; }
	public Canvas read(DataInputStream in, Monitor m) throws IOException {
		BufferedImage bi = ImageIO.read(in);
		if (bi == null) {
			throw new IOException();
		} else {
			Canvas c = new Canvas(bi.getWidth(), bi.getHeight());
			Graphics2D g = c.get(0).createPaintGraphics();
			g.drawImage(bi, null, 0, 0);
			g.dispose();
			return c;
		}
	}
	
	public boolean supportsWrite() {
		for (String s : ImageIO.getWriterFormatNames()) {
			if (s.equalsIgnoreCase("gif")) return true;
		}
		return false;
	}
	public boolean usesWriteOptionForm() { return false; }
	public Form getWriteOptionForm() { return null; }
	public int approximateFileSize(Canvas c) {
		return c.getWidth()*c.getHeight()/5;
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
		IndexColorModel cm = makeColorModel(pixels);
		BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED, cm);
		Graphics2D g2 = bi2.createGraphics();
		while (!g2.drawImage(bi, 0, 0, null));
		g2.dispose();
		ImageIO.write(bi2, "gif", out);
	}
	
	private static IndexColorModel makeColorModel(int[] pixels) {
		Map<Integer,Long> h1 = new HashMap<Integer,Long>();
		for (int pixel : pixels) {
			int p = (pixel < 0) ? (pixel & 0xFFFFFF) : 0x01000000;
			if (h1.containsKey(p)) {
				h1.put(p, h1.get(p)+0x100000000L);
			} else {
				h1.put(p, p+0x100000000L);
			}
		}
		Long[] h2 = h1.values().toArray(new Long[0]);
		Arrays.sort(h2);
		int offset = (h2.length > 256) ? (h2.length-256) : 0;
		int length = (h2.length > 256) ? 256 : h2.length;
		int[] h3 = new int[length];
		for (int i = 0, j = offset; i < length && j < h2.length; i++, j++) {
			h3[i] = h2[j].intValue();
		}
		Arrays.sort(h3);
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];
		int t = -1;
		for (int k = 0; k < 256; k += length) {
			for (int i = 0, j = length-1; i < length && k+i < 256 && j >= 0; i++, j--) {
				if (h3[j] == 0x01000000) {
					r[k+i] = -1;
					g[k+i] = -1;
					b[k+i] = -1;
					t = i;
				} else {
					r[k+i] = (byte)((h3[j] >>> 16) & 0xFF);
					g[k+i] = (byte)((h3[j] >>>  8) & 0xFF);
					b[k+i] = (byte)((h3[j] >>>  0) & 0xFF);
				}
			}
		}
		if (t >= 0) {
			int nc;
			do {
				nc = (int)(Math.random() * 0x1000000) & 0xFFFFFF;
			} while (h1.containsKey(nc));
			r[t] = (byte)((nc >>> 16) & 0xFF);
			g[t] = (byte)((nc >>>  8) & 0xFF);
			b[t] = (byte)((nc >>>  0) & 0xFF);
			return new IndexColorModel(8, 256, r, g, b, t);
		} else {
			return new IndexColorModel(8, 256, r, g, b);
		}
	}
}
