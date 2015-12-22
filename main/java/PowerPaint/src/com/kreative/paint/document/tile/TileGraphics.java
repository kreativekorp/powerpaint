package com.kreative.paint.document.tile;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;

public class TileGraphics extends Graphics2D {
	private final Tile t;
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final BufferedImage image;
	private final History history;
	private final Graphics2D g;
	
	public TileGraphics(
		Tile t, int x, int y,
		int width, int height,
		BufferedImage image,
		History history
	) {
		this.t = t;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = image;
		this.history = history;
		this.g = image.createGraphics();
		this.g.translate(-x, -y);
	}
	
	private static class SetRGBAtom implements Atom {
		private Tile t;
		private BufferedImage i;
		private int x, y, width, height;
		private int[] oldRGB;
		private int[] newRGB;
		public SetRGBAtom(
			Tile t, BufferedImage i,
			int x, int y, int width, int height,
			int[] oldRGB, int[] newRGB
		) {
			this.t = t;
			this.i = i;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.oldRGB = oldRGB;
			this.newRGB = newRGB;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof SetRGBAtom)
			    && (((SetRGBAtom)prev).t == this.t)
			    && (((SetRGBAtom)prev).i == this.i)
			    && (((SetRGBAtom)prev).x == this.x)
			    && (((SetRGBAtom)prev).y == this.y)
			    && (((SetRGBAtom)prev).width == this.width)
			    && (((SetRGBAtom)prev).height == this.height);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldRGB = ((SetRGBAtom)prev).oldRGB;
			return this;
		}
		@Override
		public void redo() {
			i.setRGB(x, y, width, height, newRGB, 0, width);
			t.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
		}
		@Override
		public void undo() {
			i.setRGB(x, y, width, height, oldRGB, 0, width);
			t.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
		}
	}
	
	private int[] changing() {
		if (history == null) return null;
		int[] oldRGB = new int[width * height];
		t.getRGB(x, y, width, height, oldRGB, 0, width);
		return oldRGB;
	}
	
	private void changed(int[] oldRGB) {
		if (history != null) {
			int[] newRGB = new int[width * height];
			t.getRGB(x, y, width, height, newRGB, 0, width);
			int tx = 0, ty = 0, tw = width, th = height;
			while (th > 0 && rowEq(oldRGB, newRGB, width, ty)) { ty++; th--; }
			while (th > 0 && rowEq(oldRGB, newRGB, width, ty + th - 1)) th--;
			if (th == 0) return;
			while (tw > 0 && colEq(oldRGB, newRGB, width, tx)) { tx++; tw--; }
			while (tw > 0 && colEq(oldRGB, newRGB, width, tx + tw - 1)) tw--;
			if (tw == 0) return;
			oldRGB = shrinkRGB(oldRGB, width, height, tx, ty, tw, th);
			newRGB = shrinkRGB(newRGB, width, height, tx, ty, tw, th);
			history.add(new SetRGBAtom(t, image, tx, ty, tw, th, oldRGB, newRGB));
		}
		t.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
	}
	
	private static boolean rowEq(int[] rgb1, int[] rgb2, int width, int row) {
		for (int i = width * row, x = 0; x < width; i++, x++) {
			if (rgb1[i] != rgb2[i]) return false;
		}
		return true;
	}
	
	private static boolean colEq(int[] rgb1, int[] rgb2, int width, int col) {
		for (int i = col; i < rgb1.length && i < rgb2.length; i += width) {
			if (rgb1[i] != rgb2[i]) return false;
		}
		return true;
	}
	
	private static int[] shrinkRGB(int[] src, int sw, int sh, int sx, int sy, int dw, int dh) {
		int[] dst = new int[dw * dh];
		for (int siy = sw * sy + sx, diy = 0, iy = 0; iy < dh; siy += sw, diy += dw, iy++) {
			for (int six = siy, dix = diy, ix = 0; ix < dw; six++, dix++, ix++) {
				dst[dix] = src[six];
			}
		}
		return dst;
	}
	
	@Override
	public void addRenderingHints(Map<?,?> hints) {
		this.g.addRenderingHints(hints);
	}
	
	@Override
	public void clip(Shape s) {
		this.g.clip(s);
	}
	
	@Override
	public void draw(Shape s) {
		int[] c = changing();
		this.g.draw(s);
		changed(c);
	}
	
	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		int[] c = changing();
		this.g.drawGlyphVector(g, x, y);
		changed(c);
	}
	
	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		int[] c = changing();
		boolean r = this.g.drawImage(img, xform, obs);
		changed(c);
		return r;
	}
	
	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		int[] c = changing();
		this.g.drawImage(img, op, x, y);
		changed(c);
	}
	
	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		int[] c = changing();
		this.g.drawRenderableImage(img, xform);
		changed(c);
	}
	
	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		int[] c = changing();
		this.g.drawRenderedImage(img, xform);
		changed(c);
	}
	
	@Override
	public void drawString(String str, int x, int y) {
		int[] c = changing();
		this.g.drawString(str, x, y);
		changed(c);
	}
	
	@Override
	public void drawString(String str, float x, float y) {
		int[] c = changing();
		this.g.drawString(str, x, y);
		changed(c);
	}
	
	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		int[] c = changing();
		this.g.drawString(iterator, x, y);
		changed(c);
	}
	
	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		int[] c = changing();
		this.g.drawString(iterator, x, y);
		changed(c);
	}
	
	@Override
	public void fill(Shape s) {
		int[] c = changing();
		this.g.fill(s);
		changed(c);
	}
	
	@Override
	public Color getBackground() {
		return this.g.getBackground();
	}
	
	@Override
	public Composite getComposite() {
		return this.g.getComposite();
	}
	
	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return this.g.getDeviceConfiguration();
	}
	
	@Override
	public FontRenderContext getFontRenderContext() {
		return this.g.getFontRenderContext();
	}
	
	@Override
	public Paint getPaint() {
		return this.g.getPaint();
	}
	
	@Override
	public Object getRenderingHint(Key hintKey) {
		return this.g.getRenderingHint(hintKey);
	}
	
	@Override
	public RenderingHints getRenderingHints() {
		return this.g.getRenderingHints();
	}
	
	@Override
	public Stroke getStroke() {
		return this.g.getStroke();
	}
	
	@Override
	public AffineTransform getTransform() {
		return this.g.getTransform();
	}
	
	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return this.g.hit(rect, s, onStroke);
	}
	
	@Override
	public void rotate(double theta) {
		this.g.rotate(theta);
	}
	
	@Override
	public void rotate(double theta, double x, double y) {
		this.g.rotate(theta, x, y);
	}
	
	@Override
	public void scale(double sx, double sy) {
		this.g.scale(sx, sy);
	}
	
	@Override
	public void setBackground(Color color) {
		this.g.setBackground(color);
	}
	
	@Override
	public void setComposite(Composite comp) {
		this.g.setComposite(comp);
	}
	
	@Override
	public void setPaint(Paint paint) {
		this.g.setPaint(paint);
	}
	
	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		this.g.setRenderingHint(hintKey, hintValue);
	}
	
	@Override
	public void setRenderingHints(Map<?,?> hints) {
		this.g.setRenderingHints(hints);
	}
	
	@Override
	public void setStroke(Stroke s) {
		this.g.setStroke(s);
	}
	
	@Override
	public void setTransform(AffineTransform Tx) {
		this.g.setTransform(Tx);
	}
	
	@Override
	public void shear(double shx, double shy) {
		this.g.shear(shx, shy);
	}
	
	@Override
	public void transform(AffineTransform Tx) {
		this.g.transform(Tx);
	}
	
	@Override
	public void translate(int x, int y) {
		this.g.translate(x, y);
	}
	
	@Override
	public void translate(double tx, double ty) {
		this.g.translate(tx, ty);
	}
	
	@Override
	public void clearRect(int x, int y, int width, int height) {
		int[] c = changing();
		this.g.clearRect(x, y, width, height);
		changed(c);
	}
	
	@Override
	public void clipRect(int x, int y, int width, int height) {
		this.g.clipRect(x, y, width, height);
	}
	
	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		int[] c = changing();
		this.g.copyArea(x, y, width, height, dx, dy);
		changed(c);
	}
	
	@Override
	public Graphics create() {
		return new TileGraphics(t, x, y, width, height, image, history);
	}
	
	@Override
	public void dispose() {
		this.g.dispose();
	}
	
	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		int[] c = changing();
		this.g.drawArc(x, y, width, height, startAngle, arcAngle);
		changed(c);
	}
	
	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		int[] c = changing();
		boolean r = this.g.drawImage(img, x, y, observer);
		changed(c);
		return r;
	}
	
	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		int[] c = changing();
		boolean r = this.g.drawImage(img, x, y, bgcolor, observer);
		changed(c);
		return r;
	}
	
	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		int[] c = changing();
		boolean r = this.g.drawImage(img, x, y, width, height, observer);
		changed(c);
		return r;
	}
	
	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		int[] c = changing();
		boolean r = this.g.drawImage(img, x, y, width, height, bgcolor, observer);
		changed(c);
		return r;
	}
	
	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		int[] c = changing();
		boolean r = this.g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
		changed(c);
		return r;
	}
	
	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		int[] c = changing();
		boolean r = this.g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
		changed(c);
		return r;
	}
	
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		int[] c = changing();
		this.g.drawLine(x1, y1, x2, y2);
		changed(c);
	}
	
	@Override
	public void drawOval(int x, int y, int width, int height) {
		int[] c = changing();
		this.g.drawOval(x, y, width, height);
		changed(c);
	}
	
	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		int[] c = changing();
		this.g.drawPolygon(xPoints, yPoints, nPoints);
		changed(c);
	}
	
	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		int[] c = changing();
		this.g.drawPolyline(xPoints, yPoints, nPoints);
		changed(c);
	}
	
	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		int[] c = changing();
		this.g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		changed(c);
	}
	
	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		int[] c = changing();
		this.g.fillArc(x, y, width, height, startAngle, arcAngle);
		changed(c);
	}
	
	@Override
	public void fillOval(int x, int y, int width, int height) {
		int[] c = changing();
		this.g.fillOval(x, y, width, height);
		changed(c);
	}
	
	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		int[] c = changing();
		this.g.fillPolygon(xPoints, yPoints, nPoints);
		changed(c);
	}
	
	@Override
	public void fillRect(int x, int y, int width, int height) {
		int[] c = changing();
		this.g.fillRect(x, y, width, height);
		changed(c);
	}
	
	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		int[] c = changing();
		this.g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		changed(c);
	}
	
	@Override
	public Shape getClip() {
		return this.g.getClip();
	}
	
	@Override
	public Rectangle getClipBounds() {
		return this.g.getClipBounds();
	}
	
	@Override
	public Color getColor() {
		return this.g.getColor();
	}
	
	@Override
	public Font getFont() {
		return this.g.getFont();
	}
	
	@Override
	public FontMetrics getFontMetrics(Font f) {
		return this.g.getFontMetrics(f);
	}
	
	@Override
	public void setClip(Shape clip) {
		this.g.setClip(clip);
	}
	
	@Override
	public void setClip(int x, int y, int width, int height) {
		this.g.setClip(x, y, width, height);
	}
	
	@Override
	public void setColor(Color c) {
		this.g.setColor(c);
	}
	
	@Override
	public void setFont(Font font) {
		this.g.setFont(font);
	}
	
	@Override
	public void setPaintMode() {
		this.g.setPaintMode();
	}
	
	@Override
	public void setXORMode(Color c1) {
		this.g.setXORMode(c1);
	}
}
