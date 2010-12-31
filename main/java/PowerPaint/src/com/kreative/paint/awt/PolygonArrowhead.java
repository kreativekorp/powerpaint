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
import java.awt.geom.GeneralPath;
import java.util.Arrays;

public class PolygonArrowhead implements Arrowhead {
	private float[] points;
	private boolean filled;
	private boolean scaled;
	
	public PolygonArrowhead(float[] points, boolean filled, boolean scaled) {
		this.points = points;
		this.filled = filled;
		this.scaled = scaled;
	}
	
	public float[] getPointArray() {
		return points;
	}
	
	public boolean isFilled() {
		return filled;
	}
	
	public boolean isScaled() {
		return scaled;
	}
	
	public Shape createArrowhead(float prevX, float prevY, float endX, float endY, float lineWidth) {
		float scale = scaled ? (((lineWidth-1)/2)+1) : 1;
		GeneralPath p = new GeneralPath();
		p.moveTo(points[0] * scale, points[1] * scale);
		for (int i = 2; i < points.length; i += 2) {
			p.lineTo(points[i] * scale, points[i+1] * scale);
		}
		Shape s = AffineTransform.getRotateInstance(Math.atan2(endY-prevY, endX-prevX)).createTransformedShape(p);
		s = AffineTransform.getTranslateInstance(endX, endY).createTransformedShape(s);
		if (!filled) s = new BasicStroke(lineWidth).createStrokedShape(s);
		return s;
	}
	
	public boolean equals(Object o) {
		if (o instanceof PolygonArrowhead) {
			PolygonArrowhead other = (PolygonArrowhead)o;
			return (Arrays.equals(this.points, other.points) && this.filled == other.filled && this.scaled == other.scaled);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return Arrays.hashCode(points) ^ (filled ? 0xFFFF0000 : 0) ^ (scaled ? 0x0000FFFF : 0);
	}
}
