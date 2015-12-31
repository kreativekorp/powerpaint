package com.kreative.paint.draw;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.Path;
import com.kreative.paint.powerbrush.BrushSettings;

public class PowerBrushStrokeDrawObject extends StrokeDrawObject {
	private BrushSettings brush;
	private transient BufferedImage cache;
	private transient int cachex, cachey;
	
	public PowerBrushStrokeDrawObject(PaintSettings ps, Path path, BrushSettings brush) {
		super(ps, path);
		this.brush = brush;
		this.cache = null;
	}
	
	public PowerBrushStrokeDrawObject(PaintSettings ps, Shape path, BrushSettings brush) {
		super(ps, path);
		this.brush = brush;
		this.cache = null;
	}
	
	private PowerBrushStrokeDrawObject(PowerBrushStrokeDrawObject o) {
		super(o);
		this.brush = o.brush;
		this.cache = null;
	}
	
	@Override
	public PowerBrushStrokeDrawObject clone() {
		return new PowerBrushStrokeDrawObject(this);
	}
	
	@Override
	protected void notifyDrawObjectListeners(int id) {
		this.cache = null;
		super.notifyDrawObjectListeners(id);
	}
	
	public BrushSettings getBrush() { return brush; }
	
	@Override
	protected void paintImpl(Graphics2D g) {
		// See preTxPaintImpl().
	}
	
	@Override
	protected void preTxPaintImpl(Graphics2D g) {
		if (cache == null) {
			Shape s = path.toAWTShape();
			if (tx != null) {
				try { s = tx.createTransformedShape(s); }
				catch (Exception e) { s = path.toAWTShape(); }
			}
			Rectangle b = s.getBounds();
			this.cachex = (int)Math.floor(b.x - brush.getOuterWidth());
			this.cachey = (int)Math.floor(b.y - brush.getOuterHeight());
			this.cache = new BufferedImage(
				(int)Math.ceil(b.width + brush.getOuterWidth() * 2),
				(int)Math.ceil(b.height + brush.getOuterHeight() * 2),
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
					brush.paint(g, (float)c[0], (float)c[1]);
					sx = cx = c[0]; sy = cy = c[1];
					break;
				case PathIterator.SEG_LINETO:
					brush.paint(g, false, (float)cx, (float)cy, (float)c[0], (float)c[1]);
					cx = c[0]; cy = c[1];
					break;
				case PathIterator.SEG_QUADTO:
					brush.paint(g, false, (float)cx, (float)cy, (float)c[2], (float)c[3]);
					cx = c[2]; cy = c[3];
					break;
				case PathIterator.SEG_CUBICTO:
					brush.paint(g, false, (float)cx, (float)cy, (float)c[4], (float)c[5]);
					cx = c[4]; cy = c[5];
					break;
				case PathIterator.SEG_CLOSE:
					brush.paint(g, false, (float)cx, (float)cy, (float)sx, (float)sy);
					cx = sx; cy = sy;
					break;
			}
		}
	}
}
