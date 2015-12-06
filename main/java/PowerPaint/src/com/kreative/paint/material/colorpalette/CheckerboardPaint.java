package com.kreative.paint.material.colorpalette;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class CheckerboardPaint implements Paint {
	public static final CheckerboardPaint LIGHT = new CheckerboardPaint(0xFFFFFFFF, 0xFFCCCCCC);
	public static final CheckerboardPaint DARK = new CheckerboardPaint(0xFF333333, 0xFF000000);
	
	private final Paint pattern;
	
	public CheckerboardPaint(int light, int dark) {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(new Color(light));
		g.fillRect(0, 0, 8, 8);
		g.fillRect(8, 8, 8, 8);
		g.setColor(new Color(dark));
		g.fillRect(8, 0, 8, 8);
		g.fillRect(0, 8, 8, 8);
		g.dispose();
		Rectangle2D rect = new Rectangle2D.Float(0, 0, 16, 16);
		pattern = new TexturePaint(img, rect);
	}
	
	@Override
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		return pattern.createContext(cm, deviceBounds, userBounds, xform, hints);
	}
	
	@Override
	public int getTransparency() {
		return pattern.getTransparency();
	}
}
