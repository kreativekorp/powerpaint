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
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class CircleArrowhead implements Arrowhead {
	private float cx, cy, w, h;
	private boolean filled;
	private boolean scaled;
	
	public CircleArrowhead(float centerX, float centerY, float width, float height, boolean filled, boolean scaled) {
		this.cx = centerX;
		this.cy = centerY;
		this.w = width;
		this.h = height;
		this.filled = filled;
		this.scaled = scaled;
	}
	
	public float getCenterX() { return cx; }
	public float getCenterY() { return cy; }
	public float getWidth() { return w; }
	public float getHeight() { return h; }
	
	public boolean isFilled() {
		return filled;
	}
	
	public boolean isScaled() {
		return scaled;
	}
	
	public Shape createArrowhead(float prevX, float prevY, float endX, float endY, float lineWidth) {
		Shape s = new Ellipse2D.Float(
				scaled ? ((cx-w/2) * lineWidth) : (cx-w/2),
				scaled ? ((cy-h/2) * lineWidth) : (cy-h/2),
				scaled ? (w * lineWidth) : w,
				scaled ? (h * lineWidth) : h
		);
		s = AffineTransform.getRotateInstance(Math.atan2(endY-prevY, endX-prevX)).createTransformedShape(s);
		s = AffineTransform.getTranslateInstance(endX, endY).createTransformedShape(s);
		if (!filled) s = new BasicStroke(lineWidth).createStrokedShape(s);
		return s;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CircleArrowhead) {
			CircleArrowhead other = (CircleArrowhead)o;
			return (this.cx == other.cx && this.cy == other.cy && this.w == other.w && this.h == other.h && this.filled == other.filled && this.scaled == other.scaled);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return Float.floatToRawIntBits(cx) ^ Float.floatToRawIntBits(cy) ^ Float.floatToRawIntBits(w) ^ Float.floatToRawIntBits(h) ^ (filled ? 0xFFFF0000 : 0) ^ (scaled ? 0x0000FFFF : 0);
	}
}
