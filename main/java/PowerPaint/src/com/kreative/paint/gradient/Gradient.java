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

import java.awt.geom.Rectangle2D;

public class Gradient implements Cloneable {
	public GradientShape shape;
	public GradientColorMap colorMap;
	public Rectangle2D boundingRect;
	
	public Gradient(Gradient g) {
		this.shape = g.shape;
		this.colorMap = g.colorMap;
		this.boundingRect = g.boundingRect;
	}
	
	public Gradient(GradientShape shape, GradientColorMap colorMap) {
		this.shape = shape;
		this.colorMap = colorMap;
		this.boundingRect = null;
	}
	
	public Gradient(GradientShape shape, GradientColorMap colorMap, Rectangle2D boundingRect) {
		this.shape = shape;
		this.colorMap = colorMap;
		this.boundingRect = boundingRect;
	}
	
	public Gradient(GradientPaint2 gp) {
		this.shape = gp.getGradientShape();
		this.colorMap = gp.getGradientColorMap();
		this.boundingRect = gp.getGradientBounds();
	}
	
	public Gradient clone() {
		return new Gradient(this);
	}
	
	public GradientPaint2 toPaint() {
		return new GradientPaint2(this.shape, this.colorMap, this.boundingRect);
	}
	
	public boolean equals(Object o) {
		if (o instanceof Gradient) {
			Gradient other = (Gradient)o;
			return (
					((this.shape == null) ? (other.shape == null) : (other.shape == null) ? (this.shape == null) : this.shape.equals(other.shape)) &&
					((this.colorMap == null) ? (other.colorMap == null) : (other.colorMap == null) ? (this.colorMap == null) : this.colorMap.equals(other.colorMap)) &&
					((this.boundingRect == null) ? (other.boundingRect == null) : (other.boundingRect == null) ? (this.boundingRect == null) : this.boundingRect.equals(other.boundingRect))
			);
		} else if (o instanceof GradientPaint2) {
			return o.equals(this);
		}
		return false;
	}
}
