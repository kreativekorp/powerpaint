package com.kreative.paint.geom.draw;

import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.ShapeDrawObject.CircularShape;
import com.kreative.paint.geom.Cycloid;

public class CycloidDrawObject extends CircularShape {
	private boolean epi;
	private int smoothness;
	private int begin;
	private int end;
	private double R;
	private double r;
	private double d;
	
	public CycloidDrawObject(
		PaintSettings ps,
		boolean epi,
		int smoothness,
		int begin,
		int end,
		double R,
		double r,
		double d,
		double cx, double cy,
		double ax, double ay
	) {
		super(ps, cx, cy, ax, ay);
		this.epi = epi;
		this.smoothness = smoothness;
		this.begin = begin;
		this.end = end;
		this.R = R;
		this.r = r;
		this.d = d;
	}
	
	private CycloidDrawObject(CycloidDrawObject o) {
		super(o);
		this.epi = o.epi;
		this.smoothness = o.smoothness;
		this.begin = o.begin;
		this.end = o.end;
		this.R = o.R;
		this.r = o.r;
		this.d = o.d;
	}
	
	@Override
	public CycloidDrawObject clone() {
		return new CycloidDrawObject(this);
	}
	
	@Override
	public Cycloid getShape() {
		return new Cycloid(
			epi, smoothness,
			begin, end,
			R, r, d,
			(float)cx, (float)cy,
			(float)ax, (float)ay
		);
	}
}
