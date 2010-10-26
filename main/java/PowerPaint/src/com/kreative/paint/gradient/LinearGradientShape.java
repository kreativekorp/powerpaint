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

public class LinearGradientShape extends GradientShape {
	public double zeroX, zeroY, oneX, oneY;
	
	public LinearGradientShape(double zx, double zy, double ox, double oy) {
		zeroX = zx;
		zeroY = zy;
		oneX = ox;
		oneY = oy;
		repeat = false;
		reflect = false;
		reverse = false;
	}
	
	public LinearGradientShape(double zx, double zy, double ox, double oy, boolean rep, boolean ref, boolean rev) {
		zeroX = zx;
		zeroY = zy;
		oneX = ox;
		oneY = oy;
		repeat = rep;
		reflect = ref;
		reverse = rev;
	}
	
	public LinearGradientShape clone() {
		return new LinearGradientShape(zeroX, zeroY, oneX, oneY, repeat, reflect, reverse);
	}
	
	public double getGradientPosition(double x, double y) {
		double dX = oneX-zeroX;
		double dY = oneY-zeroY;
		double div = dX*dX + dY*dY;
		return ((x-zeroX)*dX + (y-zeroY)*dY) / div;
	}
	
	public double[] getGradientPositions(double[] x, double[] y, int npoints) {
		double dX = oneX-zeroX;
		double dY = oneY-zeroY;
		double div = dX*dX + dY*dY;
		double[] ret = new double[npoints];
		for (int i = 0; i < npoints; i++) {
			ret[i] = ((x[i]-zeroX)*dX + (y[i]-zeroY)*dY) / div;
		}
		return ret;
	}
	
	public boolean equals(Object o) {
		if (o instanceof LinearGradientShape) {
			LinearGradientShape other = (LinearGradientShape)o;
			return (
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
