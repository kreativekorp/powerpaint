package com.kreative.paint.material.stroke;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.Arrays;

public class PowerStroke implements Stroke {
	public static final PowerStroke DEFAULT = new PowerStroke(
		/* lineWidth    = */ 1.0f,
		/* multiplicity = */ 1,
		/* dashArray    = */ null,
		/* dashPhase    = */ 0.0f,
		/* arrowOnStart = */ null,
		/* arrowOnEnd   = */ null,
		/* endCap       = */ EndCap.SQUARE,
		/* lineJoin     = */ LineJoin.MITER,
		/* miterLimit   = */ 10.0f,
		/* name         = */ "Default"
	);
	
	private final BasicStroke base;
	public final float lineWidth;
	public final int multiplicity;
	public final float[] dashArray;
	public final float dashPhase;
	public final Arrowhead arrowOnStart;
	public final Arrowhead arrowOnEnd;
	public final EndCap endCap;
	public final LineJoin lineJoin;
	public final float miterLimit;
	public final String name;
	
	public PowerStroke(
		float lineWidth, int multiplicity,
		float[] dashArray, float dashPhase,
		Arrowhead arrowOnStart, Arrowhead arrowOnEnd,
		EndCap endCap, LineJoin lineJoin, float miterLimit,
		String name
	) {
		this.base = new BasicStroke(
			lineWidth,
			((endCap != null) ? endCap.awtValue : 0),
			((lineJoin != null) ? lineJoin.awtValue : 0),
			miterLimit,
			dashArray,
			dashPhase
		);
		this.lineWidth = lineWidth;
		this.multiplicity = multiplicity;
		this.dashArray = dashArray;
		this.dashPhase = dashPhase;
		this.arrowOnStart = arrowOnStart;
		this.arrowOnEnd = arrowOnEnd;
		this.endCap = endCap;
		this.lineJoin = lineJoin;
		this.miterLimit = miterLimit;
		this.name = name;
	}
	
	public Shape createStrokedShape(Shape shape) {
		Shape originalShape = shape;
		Shape strokedShape = shape;
		for (int i = 0; i < multiplicity; i++) {
			strokedShape = base.createStrokedShape(strokedShape);
		}
		if (arrowOnStart == null && arrowOnEnd == null) {
			return strokedShape;
		}
		float sx1 = 0.0f;
		float sy1 = 0.0f;
		float sx2 = 0.0f;
		float sy2 = 0.0f;
		boolean startset = false;
		float ex1 = 0.0f;
		float ey1 = 0.0f;
		float ex2 = 0.0f;
		float ey2 = 0.0f;
		boolean endset = false;
		float mx = 0.0f;
		float my = 0.0f;
		float cx = 0.0f;
		float cy = 0.0f;
		float[] coords = new float[6];
		PathIterator i = originalShape.getPathIterator(null);
		while (!i.isDone()) {
			switch (i.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				mx = coords[0]; my = coords[1];
				cx = coords[0]; cy = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				if (!startset) {
					sx1 = cx; sy1 = cy;
					sx2 = coords[0]; sy2 = coords[1];
					startset = true;
				}
				ex1 = cx; ey1 = cy;
				ex2 = coords[0]; ey2 = coords[1];
				endset = true;
				cx = coords[0]; cy = coords[1];
				break;
			case PathIterator.SEG_QUADTO:
				if (!startset) {
					sx1 = cx; sy1 = cy;
					sx2 = coords[0]; sy2 = coords[1];
					startset = true;
				}
				ex1 = coords[0]; ey1 = coords[1];
				ex2 = coords[2]; ey2 = coords[3];
				endset = true;
				cx = coords[2]; cy = coords[3];
				break;
			case PathIterator.SEG_CUBICTO:
				if (!startset) {
					sx1 = cx; sy1 = cy;
					sx2 = coords[0]; sy2 = coords[1];
					startset = true;
				}
				ex1 = coords[2]; ey1 = coords[3];
				ex2 = coords[4]; ey2 = coords[5];
				endset = true;
				cx = coords[4]; cy = coords[5];
				break;
			case PathIterator.SEG_CLOSE:
				if (!startset) {
					sx1 = cx; sy1 = cy;
					sx2 = mx; sy2 = my;
					startset = true;
				}
				ex1 = cx; ey1 = cy;
				ex2 = mx; ey2 = my;
				endset = true;
				cx = mx; cy = my;
				break;
			}
			i.next();
		}
		if (startset && endset && sx1 == ex2 && sy1 == ey2) {
			return strokedShape;
		}
		Area a = new Area(strokedShape);
		if (startset && arrowOnStart != null) {
			Shape arrow = arrowOnStart.createArrowhead(sx2, sy2, sx1, sy1, lineWidth);
			a.add(new Area(arrow));
		}
		if (endset && arrowOnEnd != null) {
			Shape arrow = arrowOnEnd.createArrowhead(ex1, ey1, ex2, ey2, lineWidth);
			a.add(new Area(arrow));
		}
		return a;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof PowerStroke) {
			return this.equals((PowerStroke)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(PowerStroke that, boolean withName) {
		if (this.lineWidth != that.lineWidth) return false;
		if (this.multiplicity != that.multiplicity) return false;
		if (!Arrays.equals(this.dashArray, that.dashArray)) return false;
		if (this.dashPhase != that.dashPhase) return false;
		if (!arrowheadEquals(this.arrowOnStart, that.arrowOnStart)) return false;
		if (!arrowheadEquals(this.arrowOnEnd, that.arrowOnEnd)) return false;
		if (this.endCap != that.endCap) return false;
		if (this.lineJoin != that.lineJoin) return false;
		if (this.miterLimit != that.miterLimit) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		hashCode ^= Float.floatToIntBits(lineWidth);
		hashCode ^= multiplicity;
		hashCode ^= Arrays.hashCode(dashArray);
		hashCode ^= Float.floatToIntBits(dashPhase);
		if (arrowOnStart != null) hashCode ^= arrowOnStart.hashCode();
		if (arrowOnEnd != null) hashCode ^= arrowOnEnd.hashCode();
		if (endCap != null) hashCode ^= endCap.awtValue;
		if (lineJoin != null) hashCode ^= lineJoin.awtValue;
		hashCode ^= Float.floatToIntBits(miterLimit);
		return hashCode;
	}
	
	public PowerStroke deriveBasicStroke(BasicStroke stroke) {
		return new PowerStroke(
			stroke.getLineWidth(), multiplicity,
			stroke.getDashArray(), stroke.getDashPhase(),
			arrowOnStart, arrowOnEnd,
			EndCap.forAWTValue(stroke.getEndCap()),
			LineJoin.forAWTValue(stroke.getLineJoin()),
			stroke.getMiterLimit(),
			null
		);
	}
	public PowerStroke deriveLineWidth(float lineWidth) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveMultiplicity(int multiplicity) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveDashArray(float[] dashArray) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveDashPhase(float dashPhase) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveArrowOnStart(Arrowhead arrowOnStart) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveArrowOnEnd(Arrowhead arrowOnEnd) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveEndCap(EndCap endCap) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveLineJoin(LineJoin lineJoin) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveMiterLimit(float miterLimit) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			null
		);
	}
	public PowerStroke deriveName(String name) {
		return new PowerStroke(
			lineWidth, multiplicity,
			dashArray, dashPhase,
			arrowOnStart, arrowOnEnd,
			endCap, lineJoin, miterLimit,
			name
		);
	}
	
	private static boolean arrowheadEquals(Arrowhead a, Arrowhead b) {
		if (a == null) return (b == null);
		if (b == null) return (a == null);
		return a.equals(b);
	}
}
