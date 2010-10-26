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

package com.kreative.paint.draw;

import java.awt.AlphaComposite;
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
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class GroupDrawObjectGraphics extends Graphics2D {
	private GroupDrawObject d;
	private BufferedImage modelImage;
	private Graphics2D modelGraphics;
	
	public GroupDrawObjectGraphics(GroupDrawObject d) {
		this.d = d;
		this.modelImage = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		this.modelGraphics = modelImage.createGraphics();
	}
	
	// UNSUPPORTED OPERATIONS: rendering hints, clipping regions, copyArea
	
	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.addRenderingHints");
		modelGraphics.addRenderingHints(hints);
	}

	@Override
	public void clip(Shape s) {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.clip");
		modelGraphics.clip(s);
	}

	@Override
	public void draw(Shape s) {
		ShapeDrawObject ws = new ShapeDrawObject(s);
		ws.setDrawComposite(modelGraphics.getComposite());
		ws.setDrawPaint(modelGraphics.getPaint());
		ws.setFillComposite(null);
		ws.setFillPaint(null);
		ws.setStroke(modelGraphics.getStroke());
		ws.setTransform(modelGraphics.getTransform());
		d.add(ws);
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		// TODO GroupDrawObjectGraphics.drawGlyphVector(GlyphVector)
		System.err.println("Warning: Invocation of unimplemented method GroupDrawObjectGraphics.drawString(GlyphVector, float, float)");
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		ImageDrawObject i = new ImageDrawObject(img, xform);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
		return true;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		ImageDrawObject i = new ImageDrawObject(img, op, x, y);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		ImageDrawObject i = new ImageDrawObject(img, xform);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		ImageDrawObject i = new ImageDrawObject(img, xform);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
	}

	@Override
	public void drawString(String str, int x, int y) {
		TextDrawObject o = new TextDrawObject(x, y, str);
		o.setFillComposite(modelGraphics.getComposite());
		o.setFillPaint(modelGraphics.getPaint());
		o.setDrawComposite(null);
		o.setDrawPaint(null);
		o.setFont(modelGraphics.getFont());
		o.setTextAlignment(DrawObject.LEFT);
		o.setAntiAliased(modelGraphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING) == RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		o.setTransform(modelGraphics.getTransform());
		d.add(o);
	}

	@Override
	public void drawString(String s, float x, float y) {
		TextDrawObject o = new TextDrawObject(x, y, s);
		o.setFillComposite(modelGraphics.getComposite());
		o.setFillPaint(modelGraphics.getPaint());
		o.setDrawComposite(null);
		o.setDrawPaint(null);
		o.setFont(modelGraphics.getFont());
		o.setTextAlignment(DrawObject.LEFT);
		o.setAntiAliased(modelGraphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING) == RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		o.setTransform(modelGraphics.getTransform());
		d.add(o);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// TODO GroupDrawObjectGraphics.drawString(AttributedCharacterIterator)
		System.err.println("Warning: Invocation of unimplemented method GroupDrawObjectGraphics.drawString(AttributedCharacterIterator, int, int)");
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		// TODO GroupDrawObjectGraphics.drawString(AttributedCharacterIterator)
		System.err.println("Warning: Invocation of unimplemented method GroupDrawObjectGraphics.drawString(AttributedCharacterIterator, float, float)");
	}

	@Override
	public void fill(Shape s) {
		ShapeDrawObject ws = new ShapeDrawObject(s);
		ws.setDrawComposite(null);
		ws.setDrawPaint(null);
		ws.setFillComposite(modelGraphics.getComposite());
		ws.setFillPaint(modelGraphics.getPaint());
		ws.setStroke(modelGraphics.getStroke());
		ws.setTransform(modelGraphics.getTransform());
		d.add(ws);
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
	public Object getRenderingHint(RenderingHints.Key hintKey) {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.getRenderingHint");
		return modelGraphics.getRenderingHint(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.getRenderingHints");
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
	public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
		if (!(hintKey == RenderingHints.KEY_ANTIALIASING || hintKey == RenderingHints.KEY_TEXT_ANTIALIASING))
			System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.setRenderingHint");
		modelGraphics.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.setRenderingHints");
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
		ShapeDrawObject ws = new ShapeDrawObject(new Rectangle(x, y, width, height));
		ws.setDrawComposite(null);
		ws.setDrawPaint(null);
		ws.setFillComposite(AlphaComposite.SrcOver);
		ws.setFillPaint(modelGraphics.getBackground());
		ws.setStroke(modelGraphics.getStroke());
		ws.setTransform(modelGraphics.getTransform());
		d.add(ws);
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.clipRect");
		modelGraphics.clipRect(x, y, width, height);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.copyArea");
		// nothing
	}

	@Override
	public Graphics create() {
		return new GroupDrawObjectGraphics(d);
	}

	@Override
	public void dispose() {
		modelGraphics.dispose();
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		draw(new Arc2D.Float(x, y, width, height, startAngle, arcAngle, Arc2D.OPEN));
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		ImageDrawObject i = new ImageDrawObject(img, x, y);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		ImageDrawObject i = new ImageDrawObject(img, x, y, bgcolor);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		ImageDrawObject i = new ImageDrawObject(img, x, y, width, height);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		ImageDrawObject i = new ImageDrawObject(img, x, y, width, height, bgcolor);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		ImageDrawObject i = new ImageDrawObject(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		ImageDrawObject i = new ImageDrawObject(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor);
		i.setTransform(modelGraphics.getTransform());
		d.add(i);
		return true;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		draw(new Line2D.Float(x1, y1, x2, y2));
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		draw(new Ellipse2D.Float(x, y, width, height));
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		draw(new Polygon(points, points2, points3));
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		draw(new Polygon(points, points2, points3));
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		draw(new Rectangle(x, y, width, height));
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		draw(new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		fill(new Arc2D.Float(x, y, width, height, startAngle, arcAngle, Arc2D.PIE));
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		fill(new Ellipse2D.Float(x, y, width, height));
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		fill(new Polygon(points, points2, points3));
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		fill(new Rectangle(x, y, width, height));
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		fill(new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
	}

	@Override
	public Shape getClip() {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.getClip");
		return modelGraphics.getClip();
	}

	@Override
	public Rectangle getClipBounds() {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.getClipBounds");
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
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.setClip");
		modelGraphics.setClip(clip);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		System.err.println("Warning: Invocation of unsupported method GroupDrawObjectGraphics.setClip");
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
	
	public String toString() {
		return "com.kreative.paint.GroupDrawObjectGraphics["+d+","+modelImage+","+modelGraphics+"]";
	}
}
