package com.kreative.paint.material.gradient;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class GradientPaint2 implements Paint {
	public static final GradientPaint2 BLACK_TO_WHITE = new GradientPaint2(
		GradientShape.SIMPLE_LINEAR, GradientColorMap.BLACK_TO_WHITE, null
	);
	public static final GradientPaint2 WHITE_TO_BLACK = new GradientPaint2(
		GradientShape.SIMPLE_LINEAR, GradientColorMap.WHITE_TO_BLACK, null
	);
	public static final GradientPaint2 RGB_SPECTRUM = new GradientPaint2(
		GradientShape.SIMPLE_LINEAR, GradientColorMap.RGB_SPECTRUM, null
	);
	public static final GradientPaint2 RGB_WHEEL = new GradientPaint2(
		GradientShape.REVERSE_ANGULAR, GradientColorMap.RGB_SPECTRUM, null
	);
	
	public final GradientShape shape;
	public final GradientColorMap colorMap;
	public final Rectangle2D boundingRect;
	
	public GradientPaint2(GradientPreset preset, Rectangle2D boundingRect) {
		this.shape = preset.shape;
		this.colorMap = preset.colorMap;
		this.boundingRect = boundingRect;
	}
	
	public GradientPaint2(GradientShape shape, GradientColorMap colorMap, Rectangle2D boundingRect) {
		this.shape = shape;
		this.colorMap = colorMap;
		this.boundingRect = boundingRect;
	}
	
	public GradientPaint2 derivePaint(GradientPreset preset) {
		return new GradientPaint2(preset, boundingRect);
	}
	
	public GradientPaint2 derivePaint(GradientShape shape) {
		return new GradientPaint2(shape, colorMap, boundingRect);
	}
	
	public GradientPaint2 derivePaint(GradientColorMap colorMap) {
		return new GradientPaint2(shape, colorMap, boundingRect);
	}
	
	public GradientPaint2 derivePaint(Rectangle2D boundingRect) {
		return new GradientPaint2(shape, colorMap, boundingRect);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof GradientPaint2) {
			if (!shapeEquals(this.shape, ((GradientPaint2)that).shape)) return false;
			if (!colorMapEquals(this.colorMap, ((GradientPaint2)that).colorMap)) return false;
			if (!rectangleEquals(this.boundingRect, ((GradientPaint2)that).boundingRect)) return false;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		return new GradientPaintContext(shape, colorMap, boundingRect, cm, deviceBounds, userBounds, xform, hints);
	}
	
	@Override
	public int getTransparency() {
		return TRANSLUCENT;
	}
	
	private static class GradientPaintContext implements PaintContext {
		private final GradientShape shape;
		private final GradientColorMap colorMap;
		private final Rectangle2D boundingRect;
		private final Rectangle deviceBounds;
		private final AffineTransform xform;
		
		public GradientPaintContext(
			GradientShape shape, GradientColorMap colorMap, Rectangle2D boundingRect,
			ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds,
			AffineTransform xform, RenderingHints hints
		) {
			this.shape = shape;
			this.colorMap = colorMap;
			this.boundingRect = boundingRect;
			this.deviceBounds = deviceBounds;
			this.xform = xform;
		}
		
		public Raster getRaster(int x, int y, int w, int h) {
			Rectangle dgb = (
				(boundingRect != null) ?
				xform.createTransformedShape(boundingRect).getBounds() :
				deviceBounds
			);
			double[] gx = new double[w*h];
			double[] gy = new double[w*h];
			for (int ay = 0, ry = y; ay < w*h; ay += w, ry++) {
				for (int ax = 0, rx = x; ax < w; ax++, rx++) {
					gx[ay+ax] = (double)(rx - dgb.x) / (double)dgb.width;
					gy[ay+ax] = (double)(ry - dgb.y) / (double)dgb.height;
				}
			}
			double[] gp = shape.getGradientPositions(gx, gy, w*h);
			int[] rgb = colorMap.getRGB(gp, shape.repeat, shape.reflect, shape.reverse);
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, w, h, rgb, 0, w);
			return img.getData();
		}
		
		public ColorModel getColorModel() {
			BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
			return img.getColorModel();
		}
		
		public void dispose() {
			// nothing
		}
	}
	
	private static boolean shapeEquals(GradientShape a, GradientShape b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b, false);
	}
	
	private static boolean colorMapEquals(GradientColorMap a, GradientColorMap b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b, false);
	}
	
	private static boolean rectangleEquals(Rectangle2D a, Rectangle2D b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b);
	}
}
