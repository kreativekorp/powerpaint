package com.kreative.paint.draw;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.Path;

public class PencilStrokeDrawObject extends StrokeDrawObject {
	public PencilStrokeDrawObject(PaintSettings ps, Path path) {
		super(ps, path);
	}
	
	public PencilStrokeDrawObject(PaintSettings ps, Shape path) {
		super(ps, path);
	}
	
	private PencilStrokeDrawObject(PencilStrokeDrawObject o) {
		super(o);
	}
	
	@Override
	public PencilStrokeDrawObject clone() {
		return new PencilStrokeDrawObject(this);
	}
	
	@Override
	protected void preTxPaintImpl(Graphics2D g, AffineTransform tx) {
		Shape s = path.toAWTShape();
		if (tx != null) {
			try { s = tx.createTransformedShape(s); }
			catch (Exception e) { s = path.toAWTShape(); }
		}
		if (ps.isFilled()) {
			ps.applyFill(g);
			g.setStroke(new BasicStroke(1));
			g.draw(s);
		}
	}
}
