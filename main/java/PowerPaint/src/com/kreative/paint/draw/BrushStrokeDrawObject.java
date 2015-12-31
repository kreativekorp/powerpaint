package com.kreative.paint.draw;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.Path;
import com.kreative.paint.material.sprite.Sprite;

public class BrushStrokeDrawObject extends StrokeDrawObject {
	private Sprite brush;
	private transient BufferedImage cache;
	private transient int cachex, cachey;
	
	public BrushStrokeDrawObject(PaintSettings ps, Path path, Sprite brush) {
		super(ps, path);
		this.brush = brush;
		this.cache = null;
	}
	
	public BrushStrokeDrawObject(PaintSettings ps, Shape path, Sprite brush) {
		super(ps, path);
		this.brush = brush;
		this.cache = null;
	}
	
	private BrushStrokeDrawObject(BrushStrokeDrawObject o) {
		super(o);
		this.brush = o.brush;
		this.cache = null;
	}
	
	@Override
	public BrushStrokeDrawObject clone() {
		return new BrushStrokeDrawObject(this);
	}
	
	@Override
	protected void notifyDrawObjectListeners(int id) {
		this.cache = null;
		super.notifyDrawObjectListeners(id);
	}
	
	@Override
	protected int getStrokeWidth() {
		return Math.max(brush.getWidth(), brush.getHeight());
	}
	
	public Sprite getBrush() { return brush; }
	
	@Override
	protected void preTxPaintImpl(Graphics2D g, AffineTransform tx) {
		if (cache == null) {
			Shape s = path.toAWTShape();
			if (tx != null) {
				try { s = tx.createTransformedShape(s); }
				catch (Exception e) { s = path.toAWTShape(); }
			}
			Rectangle b = s.getBounds();
			this.cachex = b.x - brush.getWidth();
			this.cachey = b.y - brush.getHeight();
			this.cache = new BufferedImage(
				b.width + brush.getWidth() * 2,
				b.height + brush.getHeight() * 2,
				BufferedImage.TYPE_INT_ARGB
			);
			if (ps.isFilled()) {
				Graphics2D gc = cache.createGraphics();
				gc.translate(-cachex, -cachey);
				ps.applyFill(gc);
				drag(gc, s);
				gc.dispose();
			}
		}
		g.drawImage(cache, null, cachex, cachey);
	}
	
	private void drag(Graphics2D g, Shape s) {
		double sx = 0, sy = 0;
		double cx = 0, cy = 0;
		double[] c = new double[6];
		for (PathIterator i = s.getPathIterator(null, 1); !i.isDone(); i.next()) {
			switch (i.currentSegment(c)) {
				case PathIterator.SEG_MOVETO:
					sx = cx = c[0];
					sy = cy = c[1];
					break;
				case PathIterator.SEG_LINETO:
					drag(g, cx, cy, c[0], c[1]);
					cx = c[0]; cy = c[1];
					break;
				case PathIterator.SEG_QUADTO:
					drag(g, cx, cy, c[2], c[3]);
					cx = c[2]; cy = c[3];
					break;
				case PathIterator.SEG_CUBICTO:
					drag(g, cx, cy, c[4], c[5]);
					cx = c[4]; cy = c[5];
					break;
				case PathIterator.SEG_CLOSE:
					drag(g, cx, cy, sx, sy);
					cx = sx; cy = sy;
					break;
			}
		}
	}
	
	private void drag(Graphics2D g, double sx, double sy, double dx, double dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx - sx), Math.abs(dy - sy)));
		for (int i = 0; i <= m; i++) {
			double x = sx + ((dx - sx) * i) / m;
			double y = sy + ((dy - sy) * i) / m;
			brush.paint(g, (int)x, (int)y);
		}
	}
}
