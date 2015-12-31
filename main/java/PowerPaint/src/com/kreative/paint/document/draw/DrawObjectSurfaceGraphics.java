package com.kreative.paint.document.draw;

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

public class DrawObjectSurfaceGraphics extends Graphics2D {
	private final DrawObjectSurface ds;
	private final int x;
	private final int y;
	private final BufferedImage modelImage;
	private final Graphics2D modelGraphics;
	
	public DrawObjectSurfaceGraphics(DrawObjectSurface ds, int x, int y) {
		this.ds = ds;
		this.x = x;
		this.y = y;
		this.modelImage = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		this.modelGraphics = modelImage.createGraphics();
	}
	
	private void unsupported(String method) {
		System.err.println("Warning: Invocation of unsupported method " + method + " in DrawObjectSurfaceGraphics.");
	}
	
	private AffineTransform getSurfaceTransform(AffineTransform objectTx) {
		AffineTransform modelTx = modelGraphics.getTransform();
		AffineTransform surfaceTx = new AffineTransform();
		surfaceTx.translate(-x, -y);
		if (modelTx != null) surfaceTx.concatenate(modelTx);
		if (objectTx != null) surfaceTx.concatenate(objectTx);
		return surfaceTx;
	}
	
	@Override
	public void addRenderingHints(Map<?,?> hints) {
		unsupported("addRenderingHints(Map<?,?> hints)");
		modelGraphics.addRenderingHints(hints);
	}
	
	@Override
	public void clip(Shape s) {
		unsupported("clip(Shape s)");
		modelGraphics.clip(s);
	}
	
	@Override
	public void draw(Shape s) {
		PaintSettings ps = PaintSettings.forGraphicsDraw(modelGraphics);
		DrawObject o = ShapeDrawObject.forShape(ps, s);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = new PathDrawObject(ps, g.getOutline(x, y));
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public boolean drawImage(Image image, AffineTransform xform, ImageObserver obs) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, image, xform);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
		return true;
	}
	
	@Override
	public void drawImage(BufferedImage image, BufferedImageOp op, int x, int y) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, image, op, x, y);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawRenderableImage(RenderableImage image, AffineTransform xform) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawRenderableImage(ps, image, xform);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawRenderedImage(RenderedImage image, AffineTransform xform) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawRenderedImage(ps, image, xform);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawString(String str, int x, int y) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = new TextDrawObject(ps, x, y, Integer.MAX_VALUE, str);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawString(String str, float x, float y) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = new TextDrawObject(ps, x, y, Integer.MAX_VALUE, str);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		unsupported("drawString(AttributedCharacterIterator iterator, int x, int y)");
	}
	
	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		unsupported("drawString(AttributedCharacterIterator iterator, float x, float y)");
	}
	
	@Override
	public void fill(Shape s) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = ShapeDrawObject.forShape(ps, s);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
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
	public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
		unsupported("setRenderingHint(RenderingHints.Key hintKey, Object hintValue)");
		modelGraphics.setRenderingHint(hintKey, hintValue);
	}
	
	@Override
	public void setRenderingHints(Map<?,?> hints) {
		unsupported("setRenderingHints(Map<?,?> hints)");
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
		PaintSettings ps = PaintSettings.forGraphicsClear(modelGraphics);
		DrawObject o = new ShapeDrawObject.Rectangle(ps, x, y, width, height);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void clipRect(int x, int y, int width, int height) {
		unsupported("clipRect(int x, int y, int width, int height)");
		modelGraphics.clipRect(x, y, width, height);
	}
	
	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		unsupported("copyArea(int x, int y, int width, int height, int dx, int dy)");
	}
	
	@Override
	public Graphics create() {
		return new DrawObjectSurfaceGraphics(ds, x, y);
	}
	
	@Override
	public void dispose() {
		modelGraphics.dispose();
	}
	
	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		PaintSettings ps = PaintSettings.forGraphicsDraw(modelGraphics);
		DrawObject o = new ShapeDrawObject.Arc(ps, x, y, width, height, startAngle, arcAngle, ArcType.OPEN);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public boolean drawImage(Image image, int x, int y, ImageObserver observer) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, image, x, y);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
		return true;
	}
	
	@Override
	public boolean drawImage(Image image, int x, int y, Color bgcolor, ImageObserver observer) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, image, x, y, bgcolor);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
		return true;
	}
	
	@Override
	public boolean drawImage(Image image, int x, int y, int width, int height, ImageObserver observer) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, image, x, y, width, height);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
		return true;
	}
	
	@Override
	public boolean drawImage(Image image, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, image, x, y, width, height, bgcolor);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
		return true;
	}
	
	@Override
	public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
		return true;
	}
	
	@Override
	public boolean drawImage(Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		PaintSettings ps = PaintSettings.forGraphicsDrawImage(modelGraphics);
		DrawObject o = ImageDrawObject.forGraphicsDrawImage(ps, image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
		return true;
	}
	
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		PaintSettings ps = PaintSettings.forGraphicsDraw(modelGraphics);
		DrawObject o = new ShapeDrawObject.Line(ps, x1, y1, x2, y2);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawOval(int x, int y, int width, int height) {
		PaintSettings ps = PaintSettings.forGraphicsDraw(modelGraphics);
		DrawObject o = new ShapeDrawObject.Ellipse(ps, x, y, width, height);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		PaintSettings ps = PaintSettings.forGraphicsDraw(modelGraphics);
		DrawObject o = new ShapeDrawObject.Polygon(ps, xPoints, yPoints, nPoints, true);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		PaintSettings ps = PaintSettings.forGraphicsDraw(modelGraphics);
		DrawObject o = new ShapeDrawObject.Polygon(ps, xPoints, yPoints, nPoints, false);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void drawRoundRect(int x, int y, int width, int height, int rx, int ry) {
		PaintSettings ps = PaintSettings.forGraphicsDraw(modelGraphics);
		DrawObject o = new ShapeDrawObject.RoundRectangle(ps, x, y, width, height, rx, ry);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = new ShapeDrawObject.Arc(ps, x, y, width, height, startAngle, arcAngle, ArcType.PIE);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void fillOval(int x, int y, int width, int height) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = new ShapeDrawObject.Ellipse(ps, x, y, width, height);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = new ShapeDrawObject.Polygon(ps, xPoints, yPoints, nPoints, true);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void fillRect(int x, int y, int width, int height) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = new ShapeDrawObject.Rectangle(ps, x, y, width, height);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
	}
	
	@Override
	public void fillRoundRect(int x, int y, int width, int height, int rx, int ry) {
		PaintSettings ps = PaintSettings.forGraphicsFill(modelGraphics);
		DrawObject o = new ShapeDrawObject.RoundRectangle(ps, x, y, width, height, rx, ry);
		o.tx = getSurfaceTransform(o.tx);
		ds.add(o);
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
		unsupported("setClip(Shape clip)");
		modelGraphics.setClip(clip);
	}
	
	@Override
	public void setClip(int x, int y, int width, int height) {
		unsupported("setClip(int x, int y, int width, int height)");
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
