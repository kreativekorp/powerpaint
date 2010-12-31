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

package com.kreative.paint.gradient;

public class RadialGradientShape extends GradientShape {
	public double centerX, centerY, zeroX, zeroY, oneX, oneY;
	
	public RadialGradientShape(double cx, double cy, double zx, double zy, double ox, double oy) {
		centerX = cx;
		centerY = cy;
		zeroX = zx;
		zeroY = zy;
		oneX = ox;
		oneY = oy;
		repeat = false;
		reflect = false;
		reverse = false;
	}
	
	public RadialGradientShape(double cx, double cy, double zx, double zy, double ox, double oy, boolean rep, boolean ref, boolean rev) {
		centerX = cx;
		centerY = cy;
		zeroX = zx;
		zeroY = zy;
		oneX = ox;
		oneY = oy;
		repeat = rep;
		reflect = ref;
		reverse = rev;
	}
	
	public RadialGradientShape clone() {
		return new RadialGradientShape(centerX, centerY, zeroX, zeroY, oneX, oneY, repeat, reflect, reverse);
	}
	
	public double getGradientPosition(double x, double y) {
		double zeroR = Math.hypot(zeroY-centerY, zeroX-centerX);
		double oneR = Math.hypot(oneY-centerY, oneX-centerX);
		double r = Math.hypot(y-centerY, x-centerX);
		return (r-zeroR)/(oneR-zeroR);
	}
	
	public double[] getGradientPositions(double[] x, double[] y, int npoints) {
		double zeroR = Math.hypot(zeroY-centerY, zeroX-centerX);
		double oneR = Math.hypot(oneY-centerY, oneX-centerX);
		double[] ret = new double[npoints];
		for (int i = 0; i < npoints; i++) {
			double r = Math.hypot(y[i]-centerY, x[i]-centerX);
			ret[i] = (r-zeroR)/(oneR-zeroR);
		}
		return ret;
	}
	
	public boolean equals(Object o) {
		if (o instanceof RadialGradientShape) {
			RadialGradientShape other = (RadialGradientShape)o;
			return (
					this.centerX == other.centerX &&
					this.centerY == other.centerY &&
					this.zeroX == other.zeroX &&
					this.zeroY == other.zeroY &&
					this.oneX == other.oneX &&
					this.oneY == other.oneY &&
					this.repeat == other.repeat &&
					this.reflect == other.reflect &&
					this.reverse == other.reverse
			);
		}
		return false;
	}
}
