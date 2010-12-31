/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.util;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.Arrays;
import com.kreative.paint.PaintSurface;

public class Bitmap {
	private int width, height;
	private int[] pixels;
	private Cursor cursorCache;
	
	public Bitmap(int width, int height, int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		this.cursorCache = null;
	}
	
	public Bitmap(Image i) {
		BufferedImage bi = ImageUtils.toBufferedImage(i, false);
		this.width = bi.getWidth();
		this.height = bi.getHeight();
		this.pixels = new int[this.width * this.height];
		bi.getRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
		this.cursorCache = null;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getRGB(int x, int y) {
		return pixels[y*width + x];
	}
	
	public int[] getRGB() {
		int[] rgb = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) rgb[i] = pixels[i];
		return rgb;
	}
	
	public int[] getRGB(int x, int y, int w, int h, int[] rgb, int offset, int scan) {
		if (rgb == null) {
			rgb = new int[offset+h*scan];
		}
		for (int dy = offset, sy = y*width+x, iy = 0; iy < h; dy += scan, sy += width, iy++) {
			for (int dx = dy, sx = sy, ix = 0; ix < w; dx++, sx++, ix++) {
				rgb[dx] = pixels[sx];
			}
		}
		return rgb;
	}
	
	public BufferedImage getImage() {
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		i.setRGB(0, 0, width, height, pixels, 0, width);
		return i;
	}
	
	public Cursor getCursor() {
		if (cursorCache == null) {
			BufferedImage ci = new BufferedImage(width+2, height+2, BufferedImage.TYPE_INT_ARGB);
			int[] ca = new int[(width+2) * (height+2)];
			for (int sy = 0, dy = width+2, y = 0; y < height; sy += width, dy += width+2, y++) {
				for (int sx = sy, dx = dy+1, x = 0; x < width; sx++, dx++, x++) {
					if (pixels[sx] < 0 || pixels[sx] >= 0x01000000) {
						ca[dx] = (((pixels[sx] >>> 24) ^ 0xFF) * 0x010101) | 0xFF000000;
					}
				}
			}
			for (int i = width+3; i < ca.length-width-3; i++) {
				if (ca[i] < 0 || ca[i] >= 0x01000000) {
					if (ca[i-1] >= 0 && ca[i-1] < 0x01000000) ca[i-1] = 0xBECC1E;
					if (ca[i+1] >= 0 && ca[i+1] < 0x01000000) ca[i+1] = 0xBECC1E;
					if (ca[i-width-2] >= 0 && ca[i-width-2] < 0x01000000) ca[i-width-2] = 0xBECC1E;
					if (ca[i+width+2] >= 0 && ca[i+width+2] < 0x01000000) ca[i+width+2] = 0xBECC1E;
				}
			}
			for (int i = 0; i < ca.length; i++) {
				if (ca[i] == 0xBECC1E) ca[i] = -1;
			}
			ci.setRGB(0, 0, width+2, height+2, ca, 0, width+2);
			cursorCache = CursorUtils.makeCursor(ci, (width+2)/2, (height+2)/2, "-bitmap-"+Integer.toHexString(super.hashCode())+"-");
		}
		return cursorCache;
	}
	
	public void paint(PaintSurface srf, Graphics2D g, int x, int y) {
		// This method takes 1-2 ms on my MacBook Pro 2.33 GHz Intel Core 2 Duo.
		// I would really like it to be on the microsecond scale, though.
		// I *dare* you to write a faster method that can take into account all five of:
		// - the paint (including its alpha channel)
		// - the composite (including its alpha level)
		// - the clipping region
		// - the transformation matrix
		// - the bitmap's alpha channel
		// Bonus points for not using any conditionals to optimize
		// for specific situations (such as t.isIdentity() here).
		
		Paint p = g.getPaint();
		Composite c = g.getComposite();
		RenderingHints h = g.getRenderingHints();
		Shape s = g.getClip();
		AffineTransform t = g.getTransform();
		
		Rectangle b = new Rectangle(x, y, width, height);
		PaintContext pc = p.createContext(null, b, b, t, h);
		ColorModel cm = pc.getColorModel();
		Raster r = pc.getRaster(x, y, width, height);
		
		boolean fastBlit = (srf != null && (t == null || t.isIdentity()) && srf.contains(x, y, width, height));
		
		BufferedImage srci = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] srca = new int[width * height];
		for (int iy = 0, gy = y, i = 0; iy < height; iy++, gy++) {
			for (int ix = 0, gx = x; ix < width; ix++, gx++, i++) {
				srca[i] = cm.getRGB(r.getDataElements(ix, iy, null));
				if (!fastBlit || s == null || s.contains(gx, gy)) {
					srca[i] = (srca[i] & 0xFFFFFF) | (((srca[i] >>> 24) * (pixels[i] >>> 24) / 255) << 24);
				} else {
					srca[i] = (srca[i] & 0xFFFFFF);
				}
			}
		}
		srci.setRGB(0, 0, width, height, srca, 0, width);
		
		if (fastBlit) {
			BufferedImage dsti = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int[] dsta = new int[width * height];
			srf.getRGB(x, y, width, height, dsta, 0, width);
			dsti.setRGB(0, 0, width, height, dsta, 0, width);
			CompositeContext cc = c.createContext(srci.getColorModel(), dsti.getColorModel(), h);
			cc.compose(srci.getData(), dsti.getData(), dsti.getRaster());
			dsti.getRGB(0, 0, width, height, dsta, 0, width);
			srf.setRGB(x, y, width, height, dsta, 0, width);
		} else {
			g.drawImage(srci, null, x, y);
		}
	}
	
	public int hashCode() {
		return width ^ height ^ Arrays.hashCode(pixels);
	}
	
	public boolean equals(Object o) {
		if (o instanceof Bitmap) {
			Bitmap other = (Bitmap)o;
			return (this.width == other.width && this.height == other.height && Arrays.equals(this.pixels, other.pixels));
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "com.kreative.paint.util.Bitmap["+width+","+height+"]";
	}
}
