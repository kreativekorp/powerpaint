package com.kreative.paint.geom.draw;

import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.ShapeDrawObject.CircularShape;
import com.kreative.paint.geom.Spiral;

public class SpiralDrawObject extends CircularShape {
	private int sides;
	private double spacing;
	private boolean spokes;
	
	public SpiralDrawObject(
		PaintSettings ps,
		int sides,
		double spacing,
		boolean spokes,
		double cx, double cy,
		double ax, double ay
	) {
		super(ps, cx, cy, ax, ay);
		this.sides = sides;
		this.spacing = spacing;
		this.spokes = spokes;
	}
	
	private SpiralDrawObject(SpiralDrawObject o) {
		super(o);
		this.sides = o.sides;
		this.spacing = o.spacing;
		this.spokes = o.spokes;
	}
	
	@Override
	public SpiralDrawObject clone() {
		return new SpiralDrawObject(this);
	}
	
	@Override
	public Spiral getShape() {
		return new Spiral(
			sides, spacing, spokes,
			(float)cx, (float)cy,
			(float)ax, (float)ay
		);
	}
}
