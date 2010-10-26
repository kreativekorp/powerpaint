/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.gradient;

public class RectangularGradientShape extends GradientShape {
	public double zeroX1, zeroY1, zeroX2, zeroY2, oneX1, oneY1, oneX2, oneY2;
	
	public RectangularGradientShape(double zx1, double zy1, double zx2, double zy2, double ox1, double oy1, double ox2, double oy2) {
		zeroX1 = zx1;
		zeroY1 = zy1;
		zeroX2 = zx2;
		zeroY2 = zy2;
		oneX1 = ox1;
		oneY1 = oy1;
		oneX2 = ox2;
		oneY2 = oy2;
		repeat = false;
		reflect = false;
		reverse = false;
	}
	
	public RectangularGradientShape(double zx1, double zy1, double zx2, double zy2, double ox1, double oy1, double ox2, double oy2, boolean rep, boolean ref, boolean rev) {
		zeroX1 = zx1;
		zeroY1 = zy1;
		zeroX2 = zx2;
		zeroY2 = zy2;
		oneX1 = ox1;
		oneY1 = oy1;
		oneX2 = ox2;
		oneY2 = oy2;
		repeat = rep;
		reflect = ref;
		reverse = rev;
	}

	public RectangularGradientShape clone() {
		return new RectangularGradientShape(zeroX1, zeroY1, zeroX2, zeroY2, oneX1, oneY1, oneX2, oneY2, repeat, reflect, reverse);
	}
	
	private double xintersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		double u = (((x4-x3)*(y1-y3))-((y4-y3)*(x1-x3)))/(((y4-y3)*(x2-x1))-((x4-x3)*(y2-y1)));
		return x1+u*(x2-x1);
	}
	
	private double yintersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		double u = (((x4-x3)*(y1-y3))-((y4-y3)*(x1-x3)))/(((y4-y3)*(x2-x1))-((x4-x3)*(y2-y1)));
		return y1+u*(y2-y1);
	}

	public double getGradientPosition(double x, double y) {
		double tlm = (oneY1-zeroY1)/(oneX1-zeroX1);
		double tlb =        zeroY1  -  tlm*zeroX1;
		double trm = (oneY1-zeroY1)/(oneX2-zeroX2);
		double trb =        zeroY1  -  trm*zeroX2;
		double blm = (oneY2-zeroY2)/(oneX1-zeroX1);
		double blb =        zeroY2  -  blm*zeroX1;
		double brm = (oneY2-zeroY2)/(oneX2-zeroX2);
		double brb =        zeroY2  -  brm*zeroX2;
		double lrx = xintersection(zeroX1, zeroY1, oneX1, oneY1, zeroX2, zeroY1, oneX2, oneY1);
		double tby = yintersection(zeroX1, zeroY1, oneX1, oneY1, zeroX1, zeroY2, oneX1, oneY2);
		
		boolean tl = (y < tlm*x + tlb);
		boolean tr = (y < trm*x + trb);
		boolean bl = (y < blm*x + blb);
		boolean br = (y < brm*x + brb);
		boolean t = (tl && tr);
		boolean l = (bl && !tl);
		boolean b = (!bl && !br);
		boolean r = (br && !tr);
		if (t && b) {
			t = (y < tby);
			b = (y > tby);
		}
		if (l && r) {
			l = (x < lrx);
			r = (x > lrx);
		}
		if (t && l || t && b || t && r || l && b || l && r || b && r) {
			System.err.println("Notice: Assert failed on RectangularGradientShape.getGradientPosition. Point "+x+","+y+" was determined to be in multiple quadrants. T="+t+" L="+l+" B="+b+" R="+r);
		}
		if (t) {
			return (y-zeroY1)/(oneY1-zeroY1);
		}
		else if (b) {
			return (y-zeroY2)/(oneY2-zeroY2);
		}
		else if (l) {
			return (x-zeroX1)/(oneX1-zeroX1);
		}
		else if (r) {
			return (x-zeroX2)/(oneX2-zeroX2);
		}
		else {
			System.err.println("Notice: Assert failed on RectangularGradientShape.getGradientPosition. Point "+x+","+y+" was determined to be in no quadrants.");
			return 0;
		}
	}
	
	public double[] getGradientPositions(double[] x, double[] y, int npoints) {
		double tlm = (oneY1-zeroY1)/(oneX1-zeroX1);
		double tlb =        zeroY1  -  tlm*zeroX1;
		double trm = (oneY1-zeroY1)/(oneX2-zeroX2);
		double trb =        zeroY1  -  trm*zeroX2;
		double blm = (oneY2-zeroY2)/(oneX1-zeroX1);
		double blb =        zeroY2  -  blm*zeroX1;
		double brm = (oneY2-zeroY2)/(oneX2-zeroX2);
		double brb =        zeroY2  -  brm*zeroX2;
		double lrx = xintersection(zeroX1, zeroY1, oneX1, oneY1, zeroX2, zeroY1, oneX2, oneY1);
		double tby = yintersection(zeroX1, zeroY1, oneX1, oneY1, zeroX1, zeroY2, oneX1, oneY2);
		
		double[] ret = new double[npoints];
		for (int i = 0; i < npoints; i++) {
			boolean tl = (y[i] < tlm*x[i] + tlb);
			boolean tr = (y[i] < trm*x[i] + trb);
			boolean bl = (y[i] < blm*x[i] + blb);
			boolean br = (y[i] < brm*x[i] + brb);
			boolean t = (tl && tr);
			boolean l = (bl && !tl);
			boolean b = (!bl && !br);
			boolean r = (br && !tr);
			if (t && b) {
				t = (y[i] < tby);
				b = (y[i] > tby);
			}
			if (l && r) {
				l = (x[i] < lrx);
				r = (x[i] > lrx);
			}
			if (t && l || t && b || t && r || l && b || l && r || b && r) {
				System.err.println("Notice: Assert failed on RectangularGradientShape.getGradientPosition. Point "+x[i]+","+y[i]+" was determined to be in multiple quadrants. T="+t+" L="+l+" B="+b+" R="+r);
			}
			if (t) {
				ret[i] = (y[i]-zeroY1)/(oneY1-zeroY1);
			}
			else if (b) {
				ret[i] = (y[i]-zeroY2)/(oneY2-zeroY2);
			}
			else if (l) {
				ret[i] = (x[i]-zeroX1)/(oneX1-zeroX1);
			}
			else if (r) {
				ret[i] = (x[i]-zeroX2)/(oneX2-zeroX2);
			}
			else {
				System.err.println("Notice: Assert failed on RectangularGradientShape.getGradientPosition. Point "+x[i]+","+y[i]+" was determined to be in no quadrants.");
				ret[i] = 0;
			}
		}
		return ret;
	}
	
	public boolean equals(Object o) {
		if (o instanceof RectangularGradientShape) {
			RectangularGradientShape other = (RectangularGradientShape)o;
			return (
					this.zeroX1 == other.zeroX1 &&
					this.zeroY1 == other.zeroY1 &&
					this.zeroX2 == other.zeroX2 &&
					this.zeroY2 == other.zeroY2 &&
					this.oneX1 == other.oneX1 &&
					this.oneY1 == other.oneY1 &&
					this.oneX2 == other.oneX2 &&
					this.oneY2 == other.oneY2 &&
					this.repeat == other.repeat &&
					this.reflect == other.reflect &&
					this.reverse == other.reverse
			);
		}
		return false;
	}
}
