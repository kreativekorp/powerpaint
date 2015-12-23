package com.kreative.paint.util;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.Arrays;

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
	
	public void paint(Graphics2D g, int x, int y) {
		Paint p = g.getPaint();
		RenderingHints h = g.getRenderingHints();
		AffineTransform t = g.getTransform();
		Rectangle b = new Rectangle(x, y, width, height);
		PaintContext pc = p.createContext(null, b, b, t, h);
		ColorModel cm = pc.getColorModel();
		Raster r = pc.getRaster(x, y, width, height);
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] rgb = new int[width * height];
		for (int iy = 0, i = 0; iy < height; iy++) {
			for (int ix = 0; ix < width; ix++, i++) {
				rgb[i] = cm.getRGB(r.getDataElements(ix, iy, null));
				rgb[i] = (rgb[i] & 0xFFFFFF) | (((rgb[i] >>> 24) * (pixels[i] >>> 24) / 255) << 24);
			}
		}
		img.setRGB(0, 0, width, height, rgb, 0, width);
		g.drawImage(img, null, x, y);
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
