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

package com.kreative.paint.draw;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

public class DrawObjectUtilities {
	private DrawObjectUtilities() {}
		
	static int getPathControlPointCount(Shape s) {
		int i = 0;
		double[] coords = new double[6];
		for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				i += 1;
				break;
			case PathIterator.SEG_LINETO:
				i += 1;
				break;
			case PathIterator.SEG_QUADTO:
				i += 2;
				break;
			case PathIterator.SEG_CUBICTO:
				i += 3;
				break;
			case PathIterator.SEG_CLOSE:
				break;
			}
		}
		return i;
	}
	
	static ControlPoint getPathControlPoint(Shape s, int i) {
		double[] coords = new double[6];
		for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				if (i == 0) return new ControlPoint.Double(coords[0], coords[1], ControlPointType.STRAIGHT_MIDPOINT);
				i -= 1;
				break;
			case PathIterator.SEG_LINETO:
				if (i == 0) return new ControlPoint.Double(coords[0], coords[1], ControlPointType.STRAIGHT_MIDPOINT);
				i -= 1;
				break;
			case PathIterator.SEG_QUADTO:
				if (i == 0) return new ControlPoint.Double(coords[0], coords[1], ControlPointType.CONTROL_POINT);
				if (i == 1) return new ControlPoint.Double(coords[2], coords[3], ControlPointType.STRAIGHT_MIDPOINT);
				i -= 2;
				break;
			case PathIterator.SEG_CUBICTO:
				if (i == 0) return new ControlPoint.Double(coords[0], coords[1], ControlPointType.CONTROL_POINT);
				if (i == 1) return new ControlPoint.Double(coords[2], coords[3], ControlPointType.CONTROL_POINT);
				if (i == 2) return new ControlPoint.Double(coords[4], coords[5], ControlPointType.STRAIGHT_MIDPOINT);
				i -= 3;
				break;
			case PathIterator.SEG_CLOSE:
				break;
			}
		}
		return null;
	}
	
	static ControlPoint[] getPathControlPoints(Shape s) {
		List<ControlPoint> pts = new Vector<ControlPoint>();
		double[] coords = new double[6];
		for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				pts.add(new ControlPoint.Double(coords[0], coords[1], ControlPointType.STRAIGHT_MIDPOINT));
				break;
			case PathIterator.SEG_LINETO:
				pts.add(new ControlPoint.Double(coords[0], coords[1], ControlPointType.STRAIGHT_MIDPOINT));
				break;
			case PathIterator.SEG_QUADTO:
				pts.add(new ControlPoint.Double(coords[0], coords[1], ControlPointType.CONTROL_POINT));
				pts.add(new ControlPoint.Double(coords[2], coords[3], ControlPointType.STRAIGHT_MIDPOINT));
				break;
			case PathIterator.SEG_CUBICTO:
				pts.add(new ControlPoint.Double(coords[0], coords[1], ControlPointType.CONTROL_POINT));
				pts.add(new ControlPoint.Double(coords[2], coords[3], ControlPointType.CONTROL_POINT));
				pts.add(new ControlPoint.Double(coords[4], coords[5], ControlPointType.STRAIGHT_MIDPOINT));
				break;
			case PathIterator.SEG_CLOSE:
				break;
			}
		}
		return pts.toArray(new ControlPoint[0]);
	}
	
	static double[][] getPathControlLines(Shape s) {
		List<double[]> lines = new Vector<double[]>();
		double lastX = 0, lastY = 0;
		double[] coords = new double[6];
		for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				lastX = coords[0];
				lastY = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				lastX = coords[0];
				lastY = coords[1];
				break;
			case PathIterator.SEG_QUADTO:
				lines.add(new double[]{ lastX, lastY, coords[0], coords[1] });
				lines.add(new double[]{ coords[2], coords[3], coords[0], coords[1] });
				lastX = coords[2];
				lastY = coords[3];
				break;
			case PathIterator.SEG_CUBICTO:
				lines.add(new double[]{ lastX, lastY, coords[0], coords[1] });
				lines.add(new double[]{ coords[4], coords[5], coords[2], coords[3] });
				lastX = coords[4];
				lastY = coords[5];
				break;
			case PathIterator.SEG_CLOSE:
				break;
			}
		}
		return lines.toArray(new double[0][]);
	}
	
	static GeneralPath setPathControlPoint(Shape s, int i, Point2D pt) {
		GeneralPath gp = new GeneralPath();
		float[] coords = new float[6];
		for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				if (i == 0) { coords[0] = (float)pt.getX(); coords[1] = (float)pt.getY(); }
				gp.moveTo(coords[0], coords[1]);
				i -= 1;
				break;
			case PathIterator.SEG_LINETO:
				if (i == 0) { coords[0] = (float)pt.getX(); coords[1] = (float)pt.getY(); }
				gp.lineTo(coords[0], coords[1]);
				i -= 1;
				break;
			case PathIterator.SEG_QUADTO:
				if (i == 0) { coords[0] = (float)pt.getX(); coords[1] = (float)pt.getY(); }
				if (i == 1) { coords[2] = (float)pt.getX(); coords[3] = (float)pt.getY(); }
				gp.quadTo(coords[0], coords[1], coords[2], coords[3]);
				i -= 2;
				break;
			case PathIterator.SEG_CUBICTO:
				if (i == 0) { coords[0] = (float)pt.getX(); coords[1] = (float)pt.getY(); }
				if (i == 1) { coords[2] = (float)pt.getX(); coords[3] = (float)pt.getY(); }
				if (i == 2) { coords[4] = (float)pt.getX(); coords[5] = (float)pt.getY(); }
				gp.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				i -= 3;
				break;
			case PathIterator.SEG_CLOSE:
				gp.closePath();
				break;
			}
		}
		return gp;
	}
	
	static Point2D getPathAnchor(Shape s) {
		double[] coords = new double[6];
		for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
			if (p.currentSegment(coords) != PathIterator.SEG_CLOSE) {
				return new Point2D.Double(coords[0], coords[1]);
			}
		}
		return new Point2D.Double(0,0);
	}
	
	static GeneralPath setPathAnchor(Shape s, Point2D pt) {
		Point2D a = getPathAnchor(s);
		float dx = (float)(pt.getX() - a.getX());
		float dy = (float)(pt.getY() - a.getY());
		GeneralPath gp = new GeneralPath();
		float[] coords = new float[6];
		for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
			switch (p.currentSegment(coords)) {
			case PathIterator.SEG_MOVETO:
				gp.moveTo(coords[0]+dx, coords[1]+dy);
				break;
			case PathIterator.SEG_LINETO:
				gp.lineTo(coords[0]+dx, coords[1]+dy);
				break;
			case PathIterator.SEG_QUADTO:
				gp.quadTo(coords[0]+dx, coords[1]+dy, coords[2]+dx, coords[3]+dy);
				break;
			case PathIterator.SEG_CUBICTO:
				gp.curveTo(coords[0]+dx, coords[1]+dy, coords[2]+dx, coords[3]+dy, coords[4]+dx, coords[5]+dy);
				break;
			case PathIterator.SEG_CLOSE:
				gp.closePath();
				break;
			}
		}
		return gp;
	}
}
