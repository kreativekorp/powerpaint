package com.kreative.paint.geom.draw;

import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.ShapeDrawObject.CircularShape;
import com.kreative.paint.geom.Flower;

public class FlowerDrawObject extends CircularShape {
	private int petals;
	private double width;
	private int smoothness;
	private boolean includeCenter;
	
	public FlowerDrawObject(
		PaintSettings ps,
		int petals,
		double width,
		int smoothness,
		boolean includeCenter,
		double cx, double cy,
		double ax, double ay
	) {
		super(ps, cx, cy, ax, ay);
		this.petals = petals;
		this.width = width;
		this.smoothness = smoothness;
		this.includeCenter = includeCenter;
	}
	
	private FlowerDrawObject(FlowerDrawObject o) {
		super(o);
		this.petals = o.petals;
		this.width = o.width;
		this.smoothness = o.smoothness;
		this.includeCenter = o.includeCenter;
	}
	
	@Override
	public FlowerDrawObject clone() {
		return new FlowerDrawObject(this);
	}
	
	@Override
	public Flower getShape() {
		return new Flower(
			petals,
			width,
			smoothness,
			includeCenter,
			(float)cx, (float)cy,
			(float)ax, (float)ay
		);
	}
}
