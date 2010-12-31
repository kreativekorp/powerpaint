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

package com.kreative.paint.geom;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ParameterizedPath implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;
	public static final int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;
	public static final int SEG_MOVETO = PathIterator.SEG_MOVETO;
	public static final int SEG_LINETO = PathIterator.SEG_LINETO;
	public static final int SEG_QUADTO = PathIterator.SEG_QUADTO;
	public static final int SEG_CUBICTO = PathIterator.SEG_CUBICTO;
	public static final int SEG_ARCTO = 777;
	public static final int SEG_APPEND_RECT = 800;
	public static final int SEG_CONNECT_RECT = 801;
	public static final int SEG_APPEND_RRECT = 802;
	public static final int SEG_CONNECT_RRECT = 803;
	public static final int SEG_APPEND_ELLIPSE = 804;
	public static final int SEG_CONNECT_ELLIPSE = 805;
	public static final int SEG_APPEND_ARC = 806;
	public static final int SEG_CONNECT_ARC = 807;
	public static final int SEG_CLOSE = PathIterator.SEG_CLOSE;
	
	private List<String> fparamNames;
	private Map<String,ParameterPoint> fparamPoints;
	private int winding;
	private List<Integer> segtype;
	private List<ParameterizedPoint[]> segcoords;
	
	public ParameterizedPath() {
		fparamNames = new Vector<String>();
		fparamPoints = new HashMap<String,ParameterPoint>();
		winding = WIND_NON_ZERO;
		segtype = new Vector<Integer>();
		segcoords = new Vector<ParameterizedPoint[]>();
	}
	
	public ParameterizedPath(int rule) {
		fparamNames = new Vector<String>();
		fparamPoints = new HashMap<String,ParameterPoint>();
		winding = rule;
		segtype = new Vector<Integer>();
		segcoords = new Vector<ParameterizedPoint[]>();
	}
	
	public ParameterizedPath(Shape shape) {
		fparamNames = new Vector<String>();
		fparamPoints = new HashMap<String,ParameterPoint>();
		winding = WIND_NON_ZERO;
		segtype = new Vector<Integer>();
		segcoords = new Vector<ParameterizedPoint[]>();
		append(shape, false);
	}
	
	public ParameterizedPath clone() {
		ParameterizedPath clone = new ParameterizedPath(winding);
		for (ParameterPoint pp : this.fparamPoints.values()) {
			clone.addParameterPoint(pp);
		}
		for (int s : this.segtype) {
			clone.segtype.add(s);
		}
		for (ParameterizedPoint[] tc : this.segcoords) {
			ParameterizedPoint[] cc = new ParameterizedPoint[tc.length];
			for (int i = 0; i < tc.length; i++) {
				cc[i] = tc[i].clone();
			}
			clone.segcoords.add(cc);
		}
		return clone;
	}
	
	public int getParameterCount() {
		return fparamNames.size();
	}
	
	public Collection<String> getParameterNames() {
		return fparamPoints.keySet();
	}
	
	public Collection<ParameterPoint> getParameterPoints() {
		return fparamPoints.values();
	}
	
	public int getParameterIndex(String name) {
		return fparamNames.indexOf(name);
	}
	
	public int getParameterIndex(ParameterPoint pp) {
		return fparamNames.indexOf(pp.getName());
	}
	
	public String getParameterName(int index) {
		return fparamNames.get(index);
	}
	
	public ParameterPoint getParameterPoint(int index) {
		return fparamPoints.get(fparamNames.get(index));
	}
	
	public ParameterPoint getParameterPoint(String name) {
		return fparamPoints.get(name);
	}
	
	public void addParameterPoint(ParameterPoint pp) {
		if (!fparamNames.contains(pp.getName())) {
			fparamNames.add(pp.getName());
		}
		fparamPoints.put(pp.getName(), pp);
	}
	
	public int getWindingRule() {
		return winding;
	}
	
	public void setWindingRule(int rule) {
		winding = rule;
	}
	
	public int getSegmentCount() {
		return Math.min(segtype.size(), segcoords.size());
	}
	
	public int getSegment(int index, ParameterizedPoint[] coords) {
		ParameterizedPoint[] sc = segcoords.get(index);
		for (int i = 0; i < sc.length; i++) {
			coords[i] = sc[i];
		}
		return segtype.get(index);
	}
	
	public void reset() {
		segtype.clear();
		segcoords.clear();
	}
	
	public void moveTo(ParameterizedPoint p) {
		segtype.add(SEG_MOVETO);
		segcoords.add(new ParameterizedPoint[]{p});
	}
	
	public void moveTo(double x, double y) {
		segtype.add(SEG_MOVETO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y)});
	}
	
	public void moveTo(String x, String y) {
		segtype.add(SEG_MOVETO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y)});
	}
	
	public void lineTo(ParameterizedPoint p) {
		segtype.add(SEG_LINETO);
		segcoords.add(new ParameterizedPoint[]{p});
	}
	
	public void lineTo(double x, double y) {
		segtype.add(SEG_LINETO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y)});
	}
	
	public void lineTo(String x, String y) {
		segtype.add(SEG_LINETO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y)});
	}
	
	public void quadTo(ParameterizedPoint c, ParameterizedPoint p) {
		segtype.add(SEG_QUADTO);
		segcoords.add(new ParameterizedPoint[]{c,p});
	}
	
	public void quadTo(double cx, double cy, double x, double y) {
		segtype.add(SEG_QUADTO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(cx,cy), new ParameterizedPoint(x,y)});
	}
	
	public void quadTo(String cx, String cy, String x, String y) {
		segtype.add(SEG_QUADTO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(cx,cy), new ParameterizedPoint(x,y)});
	}
	
	public void curveTo(ParameterizedPoint c1, ParameterizedPoint c2, ParameterizedPoint p) {
		segtype.add(SEG_CUBICTO);
		segcoords.add(new ParameterizedPoint[]{c1,c2,p});
	}
	
	public void curveTo(double c1x, double c1y, double c2x, double c2y, double x, double y) {
		segtype.add(SEG_CUBICTO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(c1x,c1y), new ParameterizedPoint(c2x,c2y), new ParameterizedPoint(x,y)});
	}
	
	public void curveTo(String c1x, String c1y, String c2x, String c2y, String x, String y) {
		segtype.add(SEG_CUBICTO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(c1x,c1y), new ParameterizedPoint(c2x,c2y), new ParameterizedPoint(x,y)});
	}
	
	public void arcTo(ParameterizedPoint c, ParameterizedPoint p) {
		segtype.add(SEG_ARCTO);
		segcoords.add(new ParameterizedPoint[]{c,p});
	}
	
	public void arcTo(double cx, double cy, double x, double y) {
		segtype.add(SEG_ARCTO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(cx,cy), new ParameterizedPoint(x,y)});
	}
	
	public void arcTo(String cx, String cy, String x, String y) {
		segtype.add(SEG_ARCTO);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(cx,cy), new ParameterizedPoint(x,y)});
	}
	
	public void appendRectangle(ParameterizedPoint origin, ParameterizedPoint size, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_RECT : SEG_APPEND_RECT);
		segcoords.add(new ParameterizedPoint[]{origin, size});
	}
	
	public void appendRectangle(double x, double y, double w, double h, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_RECT : SEG_APPEND_RECT);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y), new ParameterizedPoint(w,h)});
	}
	
	public void appendRectangle(String x, String y, String w, String h, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_RECT : SEG_APPEND_RECT);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y), new ParameterizedPoint(w,h)});
	}
	
	public void appendRoundRectangle(ParameterizedPoint origin, ParameterizedPoint size, ParameterizedPoint corner, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_RRECT : SEG_APPEND_RRECT);
		segcoords.add(new ParameterizedPoint[]{origin, size, corner});
	}
	
	public void appendRoundRectangle(double x, double y, double w, double h, double cw, double ch, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_RRECT : SEG_APPEND_RRECT);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y), new ParameterizedPoint(w,h), new ParameterizedPoint(cw,ch)});
	}
	
	public void appendRoundRectangle(String x, String y, String w, String h, String cw, String ch, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_RRECT : SEG_APPEND_RRECT);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y), new ParameterizedPoint(w,h), new ParameterizedPoint(cw,ch)});
	}
	
	public void appendEllipse(ParameterizedPoint origin, ParameterizedPoint size, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_ELLIPSE : SEG_APPEND_ELLIPSE);
		segcoords.add(new ParameterizedPoint[]{origin, size});
	}
	
	public void appendEllipse(double x, double y, double w, double h, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_ELLIPSE : SEG_APPEND_ELLIPSE);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y), new ParameterizedPoint(w,h)});
	}
	
	public void appendEllipse(String x, String y, String w, String h, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_ELLIPSE : SEG_APPEND_ELLIPSE);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y), new ParameterizedPoint(w,h)});
	}
	
	public void appendArc(ParameterizedPoint origin, ParameterizedPoint size, ParameterizedPoint angle, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_ARC : SEG_APPEND_ARC);
		segcoords.add(new ParameterizedPoint[]{origin, size, angle});
	}
	
	public void appendArc(double x, double y, double w, double h, double start, double extent, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_ARC : SEG_APPEND_ARC);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y), new ParameterizedPoint(w,h), new ParameterizedPoint(start,extent)});
	}
	
	public void appendArc(String x, String y, String w, String h, String start, String extent, boolean connect) {
		segtype.add(connect ? SEG_CONNECT_ARC : SEG_APPEND_ARC);
		segcoords.add(new ParameterizedPoint[]{new ParameterizedPoint(x,y), new ParameterizedPoint(w,h), new ParameterizedPoint(start,extent)});
	}
	
	public void closePath() {
		segtype.add(SEG_CLOSE);
		segcoords.add(new ParameterizedPoint[]{});
	}
	
	public void append(ParameterizedPath path, boolean connect) {
		fparamNames.removeAll(path.fparamNames);
		fparamNames.addAll(path.fparamNames);
		fparamPoints.putAll(path.fparamPoints);
		int c = segtype.size();
		segtype.addAll(path.segtype);
		segcoords.addAll(path.segcoords);
		if (connect && c < segtype.size() && segtype.get(c).intValue() == SEG_MOVETO) {
			segtype.set(c, SEG_LINETO);
		}
	}
	
	public void append(PathIterator i, boolean connect) {
		int c = segtype.size();
		float[] coords = new float[6];
		while (!i.isDone()) {
			switch (i.currentSegment(coords)) {
			case PathIterator.SEG_CLOSE: this.closePath(); break;
			case PathIterator.SEG_CUBICTO: this.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]); break;
			case PathIterator.SEG_LINETO: this.lineTo(coords[0], coords[1]); break;
			case PathIterator.SEG_MOVETO: this.moveTo(coords[0], coords[1]); break;
			case PathIterator.SEG_QUADTO: this.quadTo(coords[0], coords[1], coords[2], coords[3]); break;
			}
			i.next();
		}
		if (connect && c < segtype.size() && segtype.get(c).intValue() == SEG_MOVETO) {
			segtype.set(c, SEG_LINETO);
		}
	}
	
	public void append(Shape s, boolean connect) {
		append(s.getPathIterator(null), connect);
	}
	
	public GeneralPath toShape(Map<String,Point2D> actualParameters) {
		GeneralPath p = new GeneralPath(winding);
		for (int i = 0; i < segtype.size() && i < segcoords.size(); i++) {
			ParameterizedPoint[] sc = segcoords.get(i);
			Point2D[] coords = new Point2D[sc.length];
			for (int j = 0; j < sc.length; j++) {
				coords[j] = sc[j].getLocation(fparamPoints, actualParameters);
			}
			switch (segtype.get(i)) {
			case SEG_MOVETO: p.moveTo((float)coords[0].getX(), (float)coords[0].getY()); break;
			case SEG_LINETO: p.lineTo((float)coords[0].getX(), (float)coords[0].getY()); break;
			case SEG_QUADTO: p.quadTo((float)coords[0].getX(), (float)coords[0].getY(), (float)coords[1].getX(), (float)coords[1].getY()); break;
			case SEG_CUBICTO: p.curveTo((float)coords[0].getX(), (float)coords[0].getY(), (float)coords[1].getX(), (float)coords[1].getY(), (float)coords[2].getX(), (float)coords[2].getY()); break;
			case SEG_ARCTO:
				{
					double x1 = p.getCurrentPoint().getX();
					double y1 = p.getCurrentPoint().getY();
					double x2 = coords[0].getX();
					double y2 = coords[0].getY();
					double x3 = coords[1].getX();
					double y3 = coords[1].getY();
					if (x1 == x2 && x2 == x3 && y1 == y2 && y2 == y3) {
						// no arc
					} else if ((x1 == x2 && x2 == x3) || (y1 == y2 && y2 == y3)) {
						// colinear points
						p.lineTo((float)x3, (float)y3);
					} else {
						double d = hk_denom(x1, y1, x2, y2, x3, y3);
						double h = h_numer(x1, y1, x2, y2, x3, y3) / d;
						double k = k_numer(x1, y1, x2, y2, x3, y3) / d;
						if (Double.isNaN(h) || Double.isNaN(k) || Double.isInfinite(h) || Double.isInfinite(k)) {
							// colinear points
							p.lineTo((float)x3, (float)y3);
						} else {
							double r = Math.hypot(k-y1, x1-h);
							double a1 = Math.toDegrees(Math.atan2(k-y1, x1-h));
							double a2 = Math.toDegrees(Math.atan2(k-y2, x2-h));
							double a3 = Math.toDegrees(Math.atan2(k-y3, x3-h));
							Arc2D a;
							if ((a1 <= a2 && a2 <= a3) || (a3 <= a2 && a2 <= a1)) {
								a = new Arc2D.Double(h-r, k-r, r+r, r+r, a1, a3-a1, Arc2D.OPEN);
							} else if (a3 <= a1) {
								a = new Arc2D.Double(h-r, k-r, r+r, r+r, a1, 360+a3-a1, Arc2D.OPEN);
							} else {
								a = new Arc2D.Double(h-r, k-r, r+r, r+r, a1, a3-a1-360, Arc2D.OPEN);
							}
							p.append(a, true);
						}
					}
				}
				break;
			case SEG_APPEND_RECT: p.append(new Rectangle2D.Double(coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY()), false); break;
			case SEG_CONNECT_RECT: p.append(new Rectangle2D.Double(coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY()), true); break;
			case SEG_APPEND_RRECT: p.append(new RoundRectangle2D.Double(coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY(), coords[2].getX(), coords[2].getY()), false); break;
			case SEG_CONNECT_RRECT: p.append(new RoundRectangle2D.Double(coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY(), coords[2].getX(), coords[2].getY()), true); break;
			case SEG_APPEND_ELLIPSE: p.append(new Ellipse2D.Double(coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY()), false); break;
			case SEG_CONNECT_ELLIPSE: p.append(new Ellipse2D.Double(coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY()), true); break;
			case SEG_APPEND_ARC: p.append(new Arc2D.Double(coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY(), Math.toDegrees(coords[2].getX()), Math.toDegrees(coords[2].getY()), Arc2D.OPEN), false); break;
			case SEG_CONNECT_ARC: p.append(new Arc2D.Double(coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY(), Math.toDegrees(coords[2].getX()), Math.toDegrees(coords[2].getY()), Arc2D.OPEN), true); break;
			case SEG_CLOSE: p.closePath(); break;
			}
		}
		return p;
	}
	
	private double det(double a, double b, double c, double d, double e, double f, double g, double h, double i) {
		return a*e*i + b*f*g + c*d*h - a*f*h - b*d*i - c*e*g;
	}
	
	private double h_numer(double x1, double y1, double x2, double y2, double x3, double y3) {
		return det(x1*x1+y1*y1, y1, 1, x2*x2+y2*y2, y2, 1, x3*x3+y3*y3, y3, 1);
	}
	
	private double k_numer(double x1, double y1, double x2, double y2, double x3, double y3) {
		return det(x1, x1*x1+y1*y1, 1, x2, x2*x2+y2*y2, 1, x3, x3*x3+y3*y3, 1);
	}
	
	private double hk_denom(double x1, double y1, double x2, double y2, double x3, double y3) {
		return 2 * det(x1, y1, 1, x2, y2, 1, x3, y3, 1);
	}
}
