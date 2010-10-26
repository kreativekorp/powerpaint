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

public class AngularGradientShape extends GradientShape {
	public double centerX, centerY, poleX, poleY;
	
	public AngularGradientShape(double cx, double cy, double px, double py) {
		centerX = cx;
		centerY = cy;
		poleX = px;
		poleY = py;
		repeat = false;
		reflect = false;
		reverse = false;
	}
	
	public AngularGradientShape(double cx, double cy, double px, double py, boolean rep, boolean ref, boolean rev) {
		centerX = cx;
		centerY = cy;
		poleX = px;
		poleY = py;
		repeat = rep;
		reflect = ref;
		reverse = rev;
	}
	
	public AngularGradientShape clone() {
		return new AngularGradientShape(centerX, centerY, poleX, poleY, repeat, reflect, reverse);
	}
	
	public double getGradientPosition(double x, double y) {
		double amin = Math.atan2(poleY-centerY, poleX-centerX);
		double adist = 2.0*Math.PI;
		double a = Math.atan2(y-centerY, x-centerX);
		a -= amin;
		a -= adist*Math.floor(a/adist);
		return a / adist;
	}
	
	public double[] getGradientPositions(double[] x, double[] y, int npoints) {
		double amin = Math.atan2(poleY-centerY, poleX-centerX);
		double adist = 2.0*Math.PI;
		double[] ret = new double[npoints];
		for (int i = 0; i < npoints; i++) {
			double a = Math.atan2(y[i]-centerY, x[i]-centerX);
			a -= amin;
			a -= adist*Math.floor(a/adist);
			ret[i] =  a / adist;
		}
		return ret;
	}
	
	public boolean equals(Object o) {
		if (o instanceof AngularGradientShape) {
			AngularGradientShape other = (AngularGradientShape)o;
			return (
					this.centerX == other.centerX &&
					this.centerY == other.centerY &&
					this.poleX == other.poleX &&
					this.poleY == other.poleY &&
					this.repeat == other.repeat &&
					this.reflect == other.reflect &&
					this.reverse == other.reverse
			);
		}
		return false;
	}
}
