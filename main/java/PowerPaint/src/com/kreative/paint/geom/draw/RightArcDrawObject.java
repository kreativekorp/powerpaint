package com.kreative.paint.geom.draw;

import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.ShapeDrawObject.RectangularShape;
import com.kreative.paint.geom.RightArc;

public class RightArcDrawObject extends RectangularShape {
	public RightArcDrawObject(
		PaintSettings ps,
		double x, double y,
		double width, double height
	) {
		super(ps, x, y, x + width, y + height);
	}
	
	private RightArcDrawObject(RightArcDrawObject o) {
		super(o);
	}
	
	@Override
	public RightArcDrawObject clone() {
		return new RightArcDrawObject(this);
	}
	
	@Override
	public RightArc getShape() {
		return new RightArc(
			(float)x1, (float)y1,
			(float)(x2 - x1),
			(float)(y2 - y1)
		);
	}
}
