package com.kreative.paint.document.tile;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class TileSurfaceGraphics extends Graphics2D {
	private final TileSurface ts;
	private final int x;
	private final int y;
	private final BufferedImage modelImage;
	private final Graphics2D modelGraphics;
	
	public TileSurfaceGraphics(TileSurface ts, int x, int y) {
		this.ts = ts;
		this.x = x;
		this.y = y;
		this.modelImage = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		this.modelGraphics = modelImage.createGraphics();
	}
	
	private Graphics2D createGraphics(Tile t) {
		Graphics2D g = t.createPaintGraphics();
		g.setBackground(modelGraphics.getBackground());
		g.setComposite(modelGraphics.getComposite());
		g.setPaint(modelGraphics.getPaint());
		g.setRenderingHints(modelGraphics.getRenderingHints());
		g.setStroke(modelGraphics.getStroke());
		g.setTransform(AffineTransform.getTranslateInstance(
			-t.getX() -x,
			-t.getY() -y
		));
		g.transform(modelGraphics.getTransform());
		g.setClip(modelGraphics.getClip());
		g.setFont(modelGraphics.getFont());
		return g;
	}
	
	private Collection<Tile> getTilesForShape(Shape sh) {
		if (sh == null) return new HashSet<Tile>();
		Stroke st = modelGraphics.getStroke();
		if (st != null) {
			try { sh = st.createStrokedShape(sh); }
			catch (Exception e) {}
		}
		AffineTransform tx = modelGraphics.getTransform();
		if (tx != null) {
			try { sh = tx.createTransformedShape(sh); }
			catch (Exception e) {}
		}
		Rectangle bounds = sh.getBounds();
		return ts.getTiles(bounds.x, bounds.y, bounds.width, bounds.height, true);
	}
	
	@Override
	public void addRenderingHints(Map<?,?> hints) {
		modelGraphics.addRenderingHints(hints);
	}
	
	@Override
	public void clip(Shape s) {
		modelGraphics.clip(s);
	}
	
	@Override
	public void draw(Shape s) {
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.draw(s);
			g.dispose();
		}
	}
	
	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		Shape s = g.getPixelBounds(modelGraphics.getFontRenderContext(), x, y);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D gg = createGraphics(t);
			gg.drawGlyphVector(g, x, y);
			gg.dispose();
		}
	}
	
	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		int width = img.getWidth(obs);
		int height = img.getHeight(obs);
		if (width < 0 || height < 0) return false;
		Shape s = xform.createTransformedShape(new Rectangle(0, 0, width, height));
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			boolean drawn = g.drawImage(img, xform, obs);
			g.dispose();
			if (!drawn) return false;
		}
		return true;
	}
	
	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		int width = img.getWidth();
		int height = img.getHeight();
		int area = width * height;
		AffineTransform tx = modelGraphics.getTransform();
		Shape clip = modelGraphics.getClip();
		if (
			(op == null) && (area < 10000) &&
			(tx == null || tx.isIdentity()) &&
			(clip == null || clip.contains(x, y, width, height))
		) {
			BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int[] dstRGB = new int[area];
			ts.getRGB(x, y, width, height, dstRGB, 0, width);
			dst.setRGB(0, 0, width, height, dstRGB, 0, width);
			CompositeContext cc = getComposite().createContext(
				img.getColorModel(),
				dst.getColorModel(),
				getRenderingHints()
			);
			cc.compose(img.getData(), dst.getData(), dst.getRaster());
			dst.getRGB(0, 0, width, height, dstRGB, 0, width);
			ts.setRGB(x, y, width, height, dstRGB, 0, width);
			return;
		}
		Rectangle s = (op == null) ? new Rectangle(0, 0, width, height) : op.getBounds2D(img).getBounds();
		s.x += x; s.y += y;
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawImage(img, op, x, y);
			g.dispose();
		}
	}
	
	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		int width = (int)Math.ceil(img.getWidth());
		int height = (int)Math.ceil(img.getHeight());
		Shape s = xform.createTransformedShape(new Rectangle(0, 0, width, height));
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawRenderableImage(img, xform);
			g.dispose();
		}
	}
	
	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		int width = img.getWidth();
		int height = img.getHeight();
		Shape s = xform.createTransformedShape(new Rectangle(0, 0, width, height));
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawRenderedImage(img, xform);
			g.dispose();
		}
	}
	
	@Override
	public void drawString(String str, int x, int y) {
		TextLayout layout = new TextLayout(str, modelGraphics.getFont(), modelGraphics.getFontRenderContext());
		Shape s = AffineTransform.getTranslateInstance(x, y).createTransformedShape(layout.getBounds());
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawString(str, x, y);
			g.dispose();
		}
	}
	
	@Override
	public void drawString(String str, float x, float y) {
		TextLayout layout = new TextLayout(str, modelGraphics.getFont(), modelGraphics.getFontRenderContext());
		Shape s = AffineTransform.getTranslateInstance(x, y).createTransformedShape(layout.getBounds());
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawString(str, x, y);
			g.dispose();
		}
	}
	
	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		TextLayout layout = new TextLayout(iterator, modelGraphics.getFontRenderContext());
		Shape s = AffineTransform.getTranslateInstance(x, y).createTransformedShape(layout.getBounds());
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawString(iterator, x, y);
			g.dispose();
		}
	}
	
	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		TextLayout layout = new TextLayout(iterator, modelGraphics.getFontRenderContext());
		Shape s = AffineTransform.getTranslateInstance(x, y).createTransformedShape(layout.getBounds());
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawString(iterator, x, y);
			g.dispose();
		}
	}
	
	@Override
	public void fill(Shape s) {
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.fill(s);
			g.dispose();
		}
	}
	
	@Override
	public Color getBackground() {
		return modelGraphics.getBackground();
	}
	
	@Override
	public Composite getComposite() {
		return modelGraphics.getComposite();
	}
	
	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return modelGraphics.getDeviceConfiguration();
	}
	
	@Override
	public FontRenderContext getFontRenderContext() {
		return modelGraphics.getFontRenderContext();
	}
	
	@Override
	public Paint getPaint() {
		return modelGraphics.getPaint();
	}
	
	@Override
	public Object getRenderingHint(Key hintKey) {
		return modelGraphics.getRenderingHint(hintKey);
	}
	
	@Override
	public RenderingHints getRenderingHints() {
		return modelGraphics.getRenderingHints();
	}
	
	@Override
	public Stroke getStroke() {
		return modelGraphics.getStroke();
	}
	
	@Override
	public AffineTransform getTransform() {
		return modelGraphics.getTransform();
	}
	
	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return modelGraphics.hit(rect, s, onStroke);
	}
	
	@Override
	public void rotate(double theta) {
		modelGraphics.rotate(theta);
	}
	
	@Override
	public void rotate(double theta, double x, double y) {
		modelGraphics.rotate(theta, x, y);
	}
	
	@Override
	public void scale(double sx, double sy) {
		modelGraphics.scale(sx, sy);
	}
	
	@Override
	public void setBackground(Color color) {
		modelGraphics.setBackground(color);
	}
	
	@Override
	public void setComposite(Composite comp) {
		modelGraphics.setComposite(comp);
	}
	
	@Override
	public void setPaint(Paint paint) {
		modelGraphics.setPaint(paint);
	}
	
	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		modelGraphics.setRenderingHint(hintKey, hintValue);
	}
	
	@Override
	public void setRenderingHints(Map<?,?> hints) {
		modelGraphics.setRenderingHints(hints);
	}
	
	@Override
	public void setStroke(Stroke s) {
		modelGraphics.setStroke(s);
	}
	
	@Override
	public void setTransform(AffineTransform Tx) {
		modelGraphics.setTransform(Tx);
	}
	
	@Override
	public void shear(double shx, double shy) {
		modelGraphics.shear(shx, shy);
	}
	
	@Override
	public void transform(AffineTransform Tx) {
		modelGraphics.transform(Tx);
	}
	
	@Override
	public void translate(int x, int y) {
		modelGraphics.translate(x, y);
	}
	
	@Override
	public void translate(double tx, double ty) {
		modelGraphics.translate(tx, ty);
	}
	
	@Override
	public void clearRect(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.clearRect(x, y, width, height);
			g.dispose();
		}
	}
	
	@Override
	public void clipRect(int x, int y, int width, int height) {
		modelGraphics.clipRect(x, y, width, height);
	}
	
	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		for (Tile t : ts.getTiles()) t.paint(g, -x, -y);
		g.dispose();
		drawImage(image, dx, dy, null);
	}
	
	@Override
	public Graphics create() {
		return new TileSurfaceGraphics(ts, x, y);
	}
	
	@Override
	public void dispose() {
		modelGraphics.dispose();
	}
	
	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawArc(x, y, width, height, startAngle, arcAngle);
			g.dispose();
		}
	}
	
	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		int width = img.getWidth(observer);
		int height = img.getHeight(observer);
		if (width < 0 || height < 0) return false;
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			boolean drawn = g.drawImage(img, x, y, observer);
			g.dispose();
			if (!drawn) return false;
		}
		return true;
	}
	
	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		int width = img.getWidth(observer);
		int height = img.getHeight(observer);
		if (width < 0 || height < 0) return false;
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			boolean drawn = g.drawImage(img, x, y, bgcolor, observer);
			g.dispose();
			if (!drawn) return false;
		}
		return true;
	}
	
	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			boolean drawn = g.drawImage(img, x, y, width, height, observer);
			g.dispose();
			if (!drawn) return false;
		}
		return true;
	}
	
	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			boolean drawn = g.drawImage(img, x, y, width, height, bgcolor, observer);
			g.dispose();
			if (!drawn) return false;
		}
		return true;
	}
	
	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		Shape s = new Rectangle(Math.min(dx1,dx2), Math.min(dy1,dy2), Math.abs(dx2-dx1), Math.abs(dy2-dy1));
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			boolean drawn = g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
			g.dispose();
			if (!drawn) return false;
		}
		return true;
	}
	
	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		Shape s = new Rectangle(Math.min(dx1,dx2), Math.min(dy1,dy2), Math.abs(dx2-dx1), Math.abs(dy2-dy1));
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			boolean drawn = g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
			g.dispose();
			if (!drawn) return false;
		}
		return true;
	}
	
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		Shape s = new Rectangle(Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2-x1), Math.abs(y2-y1));
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawLine(x1, y1, x2, y2);
			g.dispose();
		}
	}
	
	@Override
	public void drawOval(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawOval(x, y, width, height);
			g.dispose();
		}
	}
	
	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		Shape s = new Polygon(xPoints, yPoints, nPoints);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawPolygon(xPoints, yPoints, nPoints);
			g.dispose();
		}
	}
	
	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		Shape s = new Polygon(xPoints, yPoints, nPoints);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawPolyline(xPoints, yPoints, nPoints);
			g.dispose();
		}
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawRect(x, y, width, height);
			g.dispose();
		}
	}
	
	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
			g.dispose();
		}
	}
	
	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.fillArc(x, y, width, height, startAngle, arcAngle);
			g.dispose();
		}
	}
	
	@Override
	public void fillOval(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.fillOval(x, y, width, height);
			g.dispose();
		}
	}
	
	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		Shape s = new Polygon(xPoints, yPoints, nPoints);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.fillPolygon(xPoints, yPoints, nPoints);
			g.dispose();
		}
	}
	
	@Override
	public void fillRect(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.fillRect(x, y, width, height);
			g.dispose();
		}
	}
	
	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = createGraphics(t);
			g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
			g.dispose();
		}
	}
	
	@Override
	public Shape getClip() {
		return modelGraphics.getClip();
	}
	
	@Override
	public Rectangle getClipBounds() {
		return modelGraphics.getClipBounds();
	}
	
	@Override
	public Color getColor() {
		return modelGraphics.getColor();
	}
	
	@Override
	public Font getFont() {
		return modelGraphics.getFont();
	}
	
	@Override
	public FontMetrics getFontMetrics(Font f) {
		return modelGraphics.getFontMetrics(f);
	}
	
	@Override
	public void setClip(Shape clip) {
		modelGraphics.setClip(clip);
	}
	
	@Override
	public void setClip(int x, int y, int width, int height) {
		modelGraphics.setClip(x, y, width, height);
	}
	
	@Override
	public void setColor(Color c) {
		modelGraphics.setColor(c);
	}
	
	@Override
	public void setFont(Font font) {
		modelGraphics.setFont(font);
	}
	
	@Override
	public void setPaintMode() {
		modelGraphics.setPaintMode();
	}
	
	@Override
	public void setXORMode(Color c1) {
		modelGraphics.setXORMode(c1);
	}
}
