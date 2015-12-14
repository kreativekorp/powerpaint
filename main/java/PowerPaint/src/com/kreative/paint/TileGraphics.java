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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;

public class TileGraphics extends Graphics2D {
	private History history;
	private BufferedImage image;
	private Graphics2D g;
	
	public TileGraphics(History history, BufferedImage image) {
		this.history = history;
		this.image = image;
		this.g = image.createGraphics();
	}
	
	private static class SetRGBAtom implements Atom {
		private BufferedImage i;
		private int x, y, width, height;
		private int[] oldRGB;
		private int[] newRGB;
		public SetRGBAtom(BufferedImage i, int x, int y, int width, int height, int[] oldrgb, int[] newrgb) {
			this.i = i;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.oldRGB = oldrgb;
			this.newRGB = newrgb;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldRGB = ((SetRGBAtom)previousAtom).oldRGB;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof SetRGBAtom) && (((SetRGBAtom)previousAtom).i == this.i)
				&& (((SetRGBAtom)previousAtom).x == this.x) && (((SetRGBAtom)previousAtom).y == this.y)
				&& (((SetRGBAtom)previousAtom).width == this.width) && (((SetRGBAtom)previousAtom).height == this.height);
		}
		public void redo() {
			i.setRGB(x, y, width, height, newRGB, 0, width);
		}
		public void undo() {
			i.setRGB(x, y, width, height, oldRGB, 0, width);
		}
	}
	
	private int[] changTmp;
	
	private void changing() {
		if (history != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			changTmp = new int[width*height];
			try {
				WritableRaster r = image.getRaster();
				DataBufferInt b = (DataBufferInt)r.getDataBuffer();
				int[] d = b.getData();
				for (int i = 0; i < changTmp.length && i < d.length; i++) changTmp[i] = d[i];
			} catch (Exception e) {
				System.err.println("Error: "+e.getMessage());
				image.getRGB(0, 0, width, height, changTmp, 0, width);
			}
		}
	}
	
	private boolean identicalRows(int[] rgb1, int[] rgb2, int width, int row) {
		for (int i = width*row; i < width*(row+1) && i < rgb1.length && i < rgb2.length; i++) {
			if (rgb1[i] != rgb2[i]) return false;
		}
		return true;
	}
	
	private boolean identicalColumns(int[] rgb1, int[] rgb2, int width, int col) {
		for (int i = col; i < rgb1.length && i < rgb2.length; i += width) {
			if (rgb1[i] != rgb2[i]) return false;
		}
		return true;
	}
	
	private int[] shrinkRGB(int[] rgb, int width, int height, int tx, int ty, int tw, int th) {
		int[] srgb = new int[tw*th];
		for (int sa = width*ty + tx, da = 0, y = 0; y < th; sa += width, da += tw, y++) {
			for (int saa = sa, daa = da, x = 0; x < tw; saa++, daa++, x++) {
				srgb[daa] = rgb[saa];
			}
		}
		return srgb;
	}
	
	private void changed() {
		if (history != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			int[] oldrgb = changTmp;
			int[] newrgb = new int[width*height];
			try {
				WritableRaster r = image.getRaster();
				DataBufferInt b = (DataBufferInt)r.getDataBuffer();
				int[] d = b.getData();
				for (int i = 0; i < newrgb.length && i < d.length; i++) newrgb[i] = d[i];
			} catch (Exception e) {
				System.err.println("Error: "+e.getMessage());
				image.getRGB(0, 0, width, height, newrgb, 0, width);
			}
			int tx = 0, ty = 0, tw = width, th = height;
			while (th > 0) {
				if (identicalRows(oldrgb, newrgb, width, ty)) {
					ty++;
					th--;
				} else {
					break;
				}
			}
			while (th > 0) {
				if (identicalRows(oldrgb, newrgb, width, ty+th-1)) {
					th--;
				} else {
					break;
				}
			}
			while (tw > 0) {
				if (identicalColumns(oldrgb, newrgb, width, tx)) {
					tx++;
					tw--;
				} else {
					break;
				}
			}
			while (tw > 0) {
				if (identicalColumns(oldrgb, newrgb, width, tx+tw-1)) {
					tw--;
				} else {
					break;
				}
			}
			if (tw > 0 && th > 0) {
				oldrgb = shrinkRGB(oldrgb, width, height, tx, ty, tw, th);
				newrgb = shrinkRGB(newrgb, width, height, tx, ty, tw, th);
				history.add(new SetRGBAtom(image, tx, ty, tw, th, oldrgb, newrgb));
			}
			changTmp = null;
		}
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		g.addRenderingHints(hints);
	}

	@Override
	public void clip(Shape s) {
		g.clip(s);
	}

	@Override
	public void draw(Shape s) {
		changing();
		g.draw(s);
		changed();
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		changing();
		this.g.drawGlyphVector(g, x, y);
		changed();
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		changing();
		boolean ret = g.drawImage(img, xform, obs);
		changed();
		return ret;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		changing();
		g.drawImage(img, op, x, y);
		changed();
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		changing();
		g.drawRenderableImage(img, xform);
		changed();
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		changing();
		g.drawRenderedImage(img, xform);
		changed();
	}

	@Override
	public void drawString(String str, int x, int y) {
		changing();
		g.drawString(str, x, y);
		changed();
	}

	@Override
	public void drawString(String s, float x, float y) {
		changing();
		g.drawString(s, x, y);
		changed();
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		changing();
		g.drawString(iterator, x, y);
		changed();
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		changing();
		g.drawString(iterator, x, y);
		changed();
	}

	@Override
	public void fill(Shape s) {
		changing();
		g.fill(s);
		changed();
	}

	@Override
	public Color getBackground() {
		return g.getBackground();
	}

	@Override
	public Composite getComposite() {
		return g.getComposite();
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return g.getDeviceConfiguration();
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return g.getFontRenderContext();
	}

	@Override
	public Paint getPaint() {
		return g.getPaint();
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return g.getRenderingHint(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return g.getRenderingHints();
	}

	@Override
	public Stroke getStroke() {
		return g.getStroke();
	}

	@Override
	public AffineTransform getTransform() {
		return g.getTransform();
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return g.hit(rect, s, onStroke);
	}

	@Override
	public void rotate(double theta) {
		g.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		g.rotate(theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		g.scale(sx, sy);
	}

	@Override
	public void setBackground(Color color) {
		g.setBackground(color);
	}

	@Override
	public void setComposite(Composite comp) {
		g.setComposite(comp);
	}

	@Override
	public void setPaint(Paint paint) {
		g.setPaint(paint);
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		g.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		g.setRenderingHints(hints);
	}

	@Override
	public void setStroke(Stroke s) {
		g.setStroke(s);
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		g.setTransform(Tx);
	}

	@Override
	public void shear(double shx, double shy) {
		g.shear(shx, shy);
	}

	@Override
	public void transform(AffineTransform Tx) {
		g.transform(Tx);
	}

	@Override
	public void translate(int x, int y) {
		g.translate(x, y);
	}

	@Override
	public void translate(double tx, double ty) {
		g.translate(tx, ty);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		changing();
		g.clearRect(x, y, width, height);
		changed();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		g.clipRect(x, y, width, height);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		changing();
		g.copyArea(x, y, width, height, dx, dy);
		changed();
	}

	@Override
	public Graphics create() {
		return new TileGraphics(history, image);
	}

	@Override
	public void dispose() {
		g.dispose();
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		changing();
		g.drawArc(x, y, width, height, startAngle, arcAngle);
		changed();
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		changing();
		boolean ret = g.drawImage(img, x, y, observer);
		changed();
		return ret;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		changing();
		boolean ret = g.drawImage(img, x, y, bgcolor, observer);
		changed();
		return ret;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		changing();
		boolean ret = g.drawImage(img, x, y, width, height, observer);
		changed();
		return ret;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		changing();
		boolean ret = g.drawImage(img, x, y, width, height, bgcolor, observer);
		changed();
		return ret;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		changing();
		boolean ret = g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
		changed();
		return ret;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		changing();
		boolean ret = g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
		changed();
		return ret;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		changing();
		g.drawLine(x1, y1, x2, y2);
		changed();
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		changing();
		g.drawOval(x, y, width, height);
		changed();
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		changing();
		g.drawPolygon(points, points2, points3);
		changed();
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		changing();
		g.drawPolyline(points, points2, points3);
		changed();
	}
	
	@Override
	public void drawRect(int x, int y, int width, int height) {
		changing();
		g.drawRect(x, y, width, height);
		changed();
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		changing();
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		changed();
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		changing();
		g.fillArc(x, y, width, height, startAngle, arcAngle);
		changed();
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		changing();
		g.fillOval(x, y, width, height);
		changed();
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		changing();
		g.fillPolygon(points, points2, points3);
		changed();
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		changing();
		g.fillRect(x, y, width, height);
		changed();
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		changing();
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		changed();
	}

	@Override
	public Shape getClip() {
		return g.getClip();
	}

	@Override
	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	@Override
	public Color getColor() {
		return g.getColor();
	}

	@Override
	public Font getFont() {
		return g.getFont();
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return g.getFontMetrics(f);
	}

	@Override
	public void setClip(Shape clip) {
		g.setClip(clip);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		g.setClip(x, y, width, height);
	}

	@Override
	public void setColor(Color c) {
		g.setColor(c);
	}

	@Override
	public void setFont(Font font) {
		g.setFont(font);
	}

	@Override
	public void setPaintMode() {
		g.setPaintMode();
	}

	@Override
	public void setXORMode(Color c1) {
		g.setXORMode(c1);
	}
}
