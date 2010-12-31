/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.awt;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

public class BasicDerivableStroke implements DerivableStroke {
	private BasicStroke base;
	private int multiplicity;
	private Arrowhead astart;
	private Arrowhead aend;
	
	public BasicDerivableStroke() {
		this(new BasicStroke(), 1, null, null);
	}
	
	public BasicDerivableStroke(float width) {
		this(new BasicStroke(width), 1, null, null);
	}
	
	public BasicDerivableStroke(float width, int cap, int join, float miterlimit) {
		this(new BasicStroke(width, cap, join, miterlimit), 1, null, null);
	}
	
	public BasicDerivableStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase) {
		this(new BasicStroke(width, cap, join, miterlimit, dash, dashphase), 1, null, null);
	}
	
	public BasicDerivableStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase, int multiplicity) {
		this(new BasicStroke(width, cap, join, miterlimit, dash, dashphase), multiplicity, null, null);
	}
	
	public BasicDerivableStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase, int multiplicity, Arrowhead start, Arrowhead end) {
		this(new BasicStroke(width, cap, join, miterlimit, dash, dashphase), multiplicity, start, end);
	}
	
	public BasicDerivableStroke(BasicStroke stroke) {
		this(stroke, 1, null, null);
	}
	
	public BasicDerivableStroke(BasicStroke stroke, int multiplicity) {
		this(stroke, multiplicity, null, null);
	}
	
	public BasicDerivableStroke(BasicStroke stroke, int multiplicity, Arrowhead start, Arrowhead end) {
		this.base = stroke;
		this.multiplicity = multiplicity;
		this.astart = start;
		this.aend = end;
	}
	
	public BasicDerivableStroke deriveStroke(float width) {
		return new BasicDerivableStroke(
				new BasicStroke(
						width,
						base.getEndCap(),
						base.getLineJoin(),
						base.getMiterLimit(),
						base.getDashArray(),
						base.getDashPhase()
				),
				multiplicity,
				astart,
				aend
		);
	}
	
	public BasicDerivableStroke deriveStroke(float width, int cap, int join, float miterlimit) {
		return new BasicDerivableStroke(
				new BasicStroke(
						width,
						cap,
						join,
						miterlimit,
						base.getDashArray(),
						base.getDashPhase()
				),
				multiplicity,
				astart,
				aend
		);
	}
	
	public BasicDerivableStroke deriveStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase) {
		return new BasicDerivableStroke(
				new BasicStroke(
						width,
						cap,
						join,
						miterlimit,
						dash,
						dashphase
				),
				multiplicity,
				astart,
				aend
		);
	}
	
	public BasicDerivableStroke deriveStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase, int multiplicity) {
		return new BasicDerivableStroke(
				new BasicStroke(
						width,
						cap,
						join,
						miterlimit,
						dash,
						dashphase
				),
				multiplicity,
				astart,
				aend
		);
	}
	
	public BasicDerivableStroke deriveStroke(float width, int cap, int join, float miterlimit, float[] dash, float dashphase, int multiplicity, Arrowhead start, Arrowhead end) {
		return new BasicDerivableStroke(
				new BasicStroke(
						width,
						cap,
						join,
						miterlimit,
						dash,
						dashphase
				),
				multiplicity,
				start,
				end
		);
	}
	
	public BasicDerivableStroke deriveStroke(int cap, int join, float miterlimit) {
		return new BasicDerivableStroke(
				new BasicStroke(
						base.getLineWidth(),
						cap,
						join,
						miterlimit,
						base.getDashArray(),
						base.getDashPhase()
				),
				multiplicity,
				astart,
				aend
		);
	}
	
	public BasicDerivableStroke deriveStroke(float[] dash, float dashphase) {
		return new BasicDerivableStroke(
				new BasicStroke(
						base.getLineWidth(),
						base.getEndCap(),
						base.getLineJoin(),
						base.getMiterLimit(),
						dash,
						dashphase
				),
				multiplicity,
				astart,
				aend
		);
	}
	
	public BasicDerivableStroke deriveStroke(int multiplicity) {
		return new BasicDerivableStroke(base, multiplicity, astart, aend);
	}
	
	public BasicDerivableStroke deriveStroke(Arrowhead start, Arrowhead end) {
		return new BasicDerivableStroke(base, multiplicity, start, end);
	}
	
	public Shape createStrokedShape(Shape shape) {
		Shape originalShape = shape;
		Shape strokedShape = shape;
		for (int i = 0; i < multiplicity; i++) {
			strokedShape = base.createStrokedShape(strokedShape);
		}
		if (astart == null && aend == null) {
			return strokedShape;
		} else {
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
			} else {
				Area a = new Area(strokedShape);
				if (startset && astart != null) a.add(new Area(astart.createArrowhead(sx2, sy2, sx1, sy1, base.getLineWidth())));
				if (endset && aend != null) a.add(new Area(aend.createArrowhead(ex1, ey1, ex2, ey2, base.getLineWidth())));
				return a;
			}
		}
	}
	
	public float getLineWidth() {
		return base.getLineWidth();
	}
	
	public int getEndCap() {
		return base.getEndCap();
	}
	
	public int getLineJoin() {
		return base.getLineJoin();
	}
	
	public float getMiterLimit() {
		return base.getMiterLimit();
	}
	
	public float[] getDashArray() {
		return base.getDashArray();
	}
	
	public float getDashPhase() {
		return base.getDashPhase();
	}
	
	public int getMultiplicity() {
		return multiplicity;
	}
	
	public Arrowhead getArrowOnStart() {
		return astart;
	}
	
	public Arrowhead getArrowOnEnd() {
		return aend;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof BasicDerivableStroke) {
			BasicDerivableStroke other = (BasicDerivableStroke)obj;
			return ((this.base == null) ? (other.base == null) : (other.base == null) ? (this.base == null) : this.base.equals(other.base))
				&& (this.multiplicity == other.multiplicity)
				&& ((this.astart == null) ? (other.astart == null) : (other.astart == null) ? (this.astart == null) : this.astart.equals(other.astart))
				&& ((this.aend == null) ? (other.aend == null) : (other.aend == null) ? (this.aend == null) : this.aend.equals(other.aend));
		} else if (obj instanceof BasicStroke) {
			BasicStroke other = (BasicStroke)obj;
			return ((this.base == null) ? (other == null) : (other == null) ? (this.base == null) : this.base.equals(other))
				&& (this.multiplicity == 1) && (this.astart == null) && (this.aend == null);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return ((base == null) ? 0 : base.hashCode())
			^ (multiplicity-1)
			^ ((astart == null) ? 0 : astart.hashCode())
			^ ((aend == null) ? 0 : aend.hashCode());
	}
}
