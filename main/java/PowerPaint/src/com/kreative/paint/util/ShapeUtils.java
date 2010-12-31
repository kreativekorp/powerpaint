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

package com.kreative.paint.util;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RectangularShape;
import com.kreative.paint.geom.BitmapShape;
import com.kreative.paint.geom.CircularShape;
import com.kreative.paint.geom.RegularPolygon;
import com.kreative.paint.geom.ScaledShape;

public class ShapeUtils {
	private ShapeUtils() {}
	
	public static boolean shapeIsEmpty(Shape s) {
		if (s == null) return true;
		else if (s instanceof RectangularShape) return ((RectangularShape)s).isEmpty();
		else return s.getBounds2D().isEmpty();
	}
	
	public static Shape cloneShape(Shape s) {
		// MY GOD JAVA WHY!?!?!?
		// WHY CAN'T I DO ((Cloneable)s).clone()!?!?!?
		if (s instanceof ScaledShape) {
			// This is technically covered by RectangularShape but it doesn't make a deep copy
			ScaledShape ss = (ScaledShape)s;
			return new ScaledShape(ss.getX(), ss.getY(), ss.getWidth(), ss.getHeight(), cloneShape(ss.getOriginalShape()));
		}
		if (s instanceof Area) return (Area)((Area)s).clone();
		if (s instanceof CubicCurve2D) return (CubicCurve2D)((CubicCurve2D)s).clone();
		if (s instanceof GeneralPath) return (GeneralPath)((GeneralPath)s).clone();
		if (s instanceof Line2D) return (Line2D)((Line2D)s).clone();
		if (s instanceof Polygon) {
			// Polygon is actually one of the few subclasses of Shape that is not Cloneable
			Polygon p = (Polygon)s;
			int[] xp = new int[p.xpoints.length];
			for (int i = 0; i < xp.length; i++) xp[i] = p.xpoints[i];
			int[] yp = new int[p.ypoints.length];
			for (int i = 0; i < yp.length; i++) yp[i] = p.ypoints[i];
			return new Polygon(xp, yp, p.npoints);
		}
		if (s instanceof QuadCurve2D) return (QuadCurve2D)((QuadCurve2D)s).clone();
		if (s instanceof Rectangle) return (Rectangle)((Rectangle)s).clone();
		if (s instanceof RectangularShape) return (RectangularShape)((RectangularShape)s).clone();
		if (s instanceof BitmapShape) return (BitmapShape)((BitmapShape)s).clone();
		if (s instanceof CircularShape) return (CircularShape)((CircularShape)s).clone();
		if (s instanceof RegularPolygon) return (RegularPolygon)((RegularPolygon)s).clone();
		throw new RuntimeException("Clone not supported for Shape subclass "+s.getClass().getCanonicalName()+".");
	}
	
	public static GeneralPath shapeToPath(Shape s) {
		GeneralPath g = new GeneralPath();
		if (s != null) {
			float[] coords = new float[6];
			for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
				switch (p.currentSegment(coords)) {
				case PathIterator.SEG_MOVETO:
					g.moveTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_LINETO:
					g.lineTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_QUADTO:
					g.quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					g.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
					break;
				case PathIterator.SEG_CLOSE:
					g.closePath();
					break;
				}
			}
		}
		return g;
	}
}
