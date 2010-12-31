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

package com.kreative.paint;

import java.awt.Color;
import java.awt.Composite;
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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
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
import java.util.Map;

public class TiledPaintSurfaceGraphics extends Graphics2D {
	private TiledPaintSurface p;
	private BufferedImage modelImage;
	private Graphics2D modelGraphics;
	private AffineTransform ttx;
	
	public TiledPaintSurfaceGraphics(TiledPaintSurface p) {
		this.p = p;
		this.modelImage = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		this.modelGraphics = modelImage.createGraphics();
		this.ttx = modelGraphics.getTransform();
	}
	
	private void transferModelTo(Graphics2D g, int lx, int ly) {
		g.setBackground(modelGraphics.getBackground());
		g.setComposite(modelGraphics.getComposite());
		g.setPaint(modelGraphics.getPaint());
		g.setRenderingHints(modelGraphics.getRenderingHints());
		g.setStroke(modelGraphics.getStroke());
		g.setFont(modelGraphics.getFont());
		g.setTransform(ttx);
		g.translate(-lx, -ly);
		g.transform(modelGraphics.getTransform());
		g.setClip(modelGraphics.getClip());
	}
	
	private Collection<Tile> getTilesForShape(Shape s) {
		Stroke st = modelGraphics.getStroke();
		Shape ss = (st == null) ? s : st.createStrokedShape(s);
		AffineTransform tr = modelGraphics.getTransform();
		Shape ts = (tr == null) ? ss : tr.createTransformedShape(ss);
		Rectangle bounds = ts.getBounds();
		return p.getTiles(bounds.x, bounds.y, bounds.width, bounds.height, true);
	}
	
	@Override
	public void addRenderingHints(Map<?, ?> hints) {
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.draw(s);
			g.dispose();
		}
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		Shape s = g.getPixelBounds(modelGraphics.getFontRenderContext(), x, y);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D gg = t.createPaintGraphics();
			transferModelTo(gg, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
		Rectangle s = (op == null) ? new Rectangle(0, 0, width, height) : op.getBounds2D(img).getBounds();
		s.x += x; s.y += y;
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawRenderedImage(img, xform);
			g.dispose();
		}
	}

	@Override
	public void drawString(String str, int x, int y) {
		TextLayout layout = new TextLayout(str, modelGraphics.getFont(), modelGraphics.getFontRenderContext());
		Shape ss = AffineTransform.getTranslateInstance(x, y).createTransformedShape(layout.getBounds());
		Collection<Tile> tt = getTilesForShape(ss);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawString(str, x, y);
			g.dispose();
		}
	}

	@Override
	public void drawString(String s, float x, float y) {
		TextLayout layout = new TextLayout(s, modelGraphics.getFont(), modelGraphics.getFontRenderContext());
		Shape ss = AffineTransform.getTranslateInstance(x, y).createTransformedShape(layout.getBounds());
		Collection<Tile> tt = getTilesForShape(ss);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawString(s, x, y);
			g.dispose();
		}
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		TextLayout layout = new TextLayout(iterator, modelGraphics.getFontRenderContext());
		Shape s = AffineTransform.getTranslateInstance(x, y).createTransformedShape(layout.getBounds());
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawString(iterator, x, y);
			g.dispose();
		}
	}

	@Override
	public void fill(Shape s) {
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
	public void setRenderingHints(Map<?, ?> hints) {
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
		for (Tile t : p.getTiles()) {
			g.drawImage(t.getImage(), t.getX() - x, t.getY() - y, null);
		}
		g.dispose();
		drawImage(image, dx, dy, null);
	}

	@Override
	public Graphics create() {
		return new TiledPaintSurfaceGraphics(p);
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawLine(x1, y1, x2, y2);
			g.dispose();
		}
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawOval(x, y, width, height);
			g.dispose();
		}
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		Shape s = new Polygon(points, points2, points3);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawPolygon(points, points2, points3);
			g.dispose();
		}
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		Shape s = new Polygon(points, points2, points3);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawPolyline(points, points2, points3);
			g.dispose();
		}
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawRect(x, y, width, height);
			g.dispose();
		}
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
			g.dispose();
		}
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.fillArc(x, y, width, height, startAngle, arcAngle);
			g.dispose();
		}
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.fillOval(x, y, width, height);
			g.dispose();
		}
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		Shape s = new Polygon(points, points2, points3);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.fillPolygon(points, points2, points3);
			g.dispose();
		}
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
			g.fillRect(x, y, width, height);
			g.dispose();
		}
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		Shape s = new Rectangle(x, y, width, height);
		Collection<Tile> tt = getTilesForShape(s);
		for (Tile t : tt) {
			Graphics2D g = t.createPaintGraphics();
			transferModelTo(g, t.getX(), t.getY());
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
