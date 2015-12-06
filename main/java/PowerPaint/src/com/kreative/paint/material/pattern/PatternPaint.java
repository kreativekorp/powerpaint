package com.kreative.paint.material.pattern;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class PatternPaint implements Paint {
	public final Paint foreground;
	public final Paint background;
	public final Pattern pattern;
	
	public PatternPaint(Paint foreground, Paint background, Pattern pattern) {
		this.foreground = foreground;
		this.background = background;
		this.pattern = pattern;
	}
	
	public PatternPaint deriveForeground(Paint foreground) {
		return new PatternPaint(foreground, background, pattern);
	}
	
	public PatternPaint deriveBackground(Paint background) {
		return new PatternPaint(foreground, background, pattern);
	}
	
	public PatternPaint derivePattern(Pattern pattern) {
		return new PatternPaint(foreground, background, pattern);
	}
	
	@Override
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		PaintContext fg = foreground.createContext(cm, deviceBounds, userBounds, xform, hints);
		PaintContext bg = background.createContext(cm, deviceBounds, userBounds, xform, hints);
		if (pattern.isForeground()) return fg;
		if (pattern.isBackground()) return bg;
		return new PatternPaintContext(fg, bg, pattern);
	}
	
	@Override
	public int getTransparency() {
		int fg = foreground.getTransparency();
		int bg = foreground.getTransparency();
		if (pattern.isForeground()) return fg;
		if (pattern.isBackground()) return bg;
		if (fg == bg) return fg;
		if (fg == TRANSLUCENT || bg == TRANSLUCENT) return TRANSLUCENT;
		if (fg == BITMASK || bg == BITMASK) return BITMASK;
		return OPAQUE;
	}
	
	private static class PatternPaintContext implements PaintContext {
		private final PaintContext foreground;
		private final PaintContext background;
		private final Pattern pattern;
		
		public PatternPaintContext(PaintContext foreground, PaintContext background, Pattern pattern) {
			this.foreground = foreground;
			this.background = background;
			this.pattern = pattern;
		}
		
		@Override
		public Raster getRaster(int x, int y, int w, int h) {
			Raster foreRaster = foreground.getRaster(x, y, w, h);
			Raster backRaster = background.getRaster(x, y, w, h);
			ColorModel foreModel = foreground.getColorModel();
			ColorModel backModel = background.getColorModel();
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			for (int ry = y, ny = 0; ny < h; ry++, ny++) {
				for (int rx = x, nx = 0; nx < w; rx++, nx++) {
					int foreRGB = foreModel.getRGB(foreRaster.getDataElements(nx, ny, null));
					int backRGB = backModel.getRGB(backRaster.getDataElements(nx, ny, null));
					int rgb = pattern.getRGB(rx, ry, backRGB, foreRGB);
					img.setRGB(nx, ny, rgb);
				}
			}
			return img.getData();
		}
		
		@Override
		public ColorModel getColorModel() {
			BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
			return img.getColorModel();
		}
		
		@Override
		public void dispose() {
			foreground.dispose();
			background.dispose();
		}
	}
}
