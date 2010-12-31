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

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.List;
import java.util.Vector;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.geom.BitmapShape;
import com.kreative.paint.geom.CircularShape;
import com.kreative.paint.geom.ParameterPoint;
import com.kreative.paint.geom.ParameterizedShape;
import com.kreative.paint.geom.RegularPolygon;
import com.kreative.paint.geom.RightArc;
import com.kreative.paint.geom.ScaledShape;
import com.kreative.paint.util.ShapeUtils;

public class ShapeDrawObject extends AbstractDrawObject {
	protected Shape originalShape;
	
	public ShapeDrawObject(Shape originalShape) {
		super();
		this.originalShape = originalShape;
	}
	
	public ShapeDrawObject(Shape originalShape, PaintSettings ps) {
		super(ps);
		this.originalShape = originalShape;
	}
	
	public ShapeDrawObject clone() {
		ShapeDrawObject o = new ShapeDrawObject(ShapeUtils.cloneShape(originalShape), getPaintSettings());
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		return o;
	}
	
	public Shape getOriginalShape() {
		return originalShape;
	}
	
	protected Shape getTransformedShape() {
		AffineTransform tx = getTransform();
		return (tx == null) ? originalShape : tx.createTransformedShape(originalShape);
	}
	
	protected Shape getTransformedStrokedShape() {
		AffineTransform tx = getTransform();
		Shape sh = (tx == null) ? originalShape : tx.createTransformedShape(originalShape);
		Stroke s = getStroke();
		return (s == null) ? sh : s.createStrokedShape(sh);
	}
	
	protected void paint(Graphics2D g, Shape ts, Shape tss) {
		push(g);
		if (isFilled()) {
			applyFill(g);
			g.fill(ts);
		}
		if (isDrawn()) {
			applyDraw(g);
			g.fill(tss);
		}
		pop(g);
	}
	
	public void paint(Graphics2D g) {
		paint(g, getTransformedShape(), getTransformedStrokedShape());
	}
	
	public void paint(Graphics2D g, int tx, int ty) {
		paint(g,
			AffineTransform.getTranslateInstance(tx, ty)
			.createTransformedShape(getTransformedShape()),
			AffineTransform.getTranslateInstance(tx, ty)
			.createTransformedShape(getTransformedStrokedShape())
		);
	}

	public boolean contains(Point2D p) {
		return (isFilled() && getTransformedShape().contains(p))
			|| (isDrawn() && getTransformedStrokedShape().contains(p));
	}

	public boolean contains(Rectangle2D r) {
		return (isFilled() && getTransformedShape().contains(r))
			|| (isDrawn() && getTransformedStrokedShape().contains(r));
	}

	public boolean contains(double x, double y) {
		return (isFilled() && getTransformedShape().contains(x,y))
			|| (isDrawn() && getTransformedStrokedShape().contains(x,y));
	}

	public boolean contains(double x, double y, double w, double h) {
		return (isFilled() && getTransformedShape().contains(x,y,w,h))
			|| (isDrawn() && getTransformedStrokedShape().contains(x,y,w,h));
	}

	public Rectangle getBounds() {
		return isDrawn() ? getTransformedStrokedShape().getBounds() : getTransformedShape().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return isDrawn() ? getTransformedStrokedShape().getBounds2D() : getTransformedShape().getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return getTransformedShape().getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getTransformedShape().getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return (isFilled() && getTransformedShape().intersects(r))
			|| (isDrawn() && getTransformedStrokedShape().intersects(r));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return (isFilled() && getTransformedShape().intersects(x,y,w,h))
			|| (isDrawn() && getTransformedStrokedShape().intersects(x,y,w,h));
	}
	
	public int getControlPointCount() {
		if (originalShape instanceof ScaledShape && ((ScaledShape)originalShape).getOriginalShape() instanceof ParameterizedShape) {
			ParameterizedShape ps = (ParameterizedShape)((ScaledShape)originalShape).getOriginalShape();
			return 9 + ps.getParameterCount();
		}
		else if (originalShape instanceof Arc2D) return 11;
		else if (originalShape instanceof RectangularShape) return 9;
		else if (originalShape instanceof CircularShape) return 2;
		else if (originalShape instanceof CubicCurve2D) return 4;
		else if (originalShape instanceof GeneralPath) return DrawObjectUtilities.getPathControlPointCount(originalShape);
		else if (originalShape instanceof Line2D) return 2;
		else if (originalShape instanceof Polygon) return ((Polygon)originalShape).npoints;
		else if (originalShape instanceof QuadCurve2D) return 3;
		else if (originalShape instanceof BitmapShape) return 1;
		else if (originalShape instanceof RegularPolygon) return 2;
		else {
			System.err.println("Warning: The Shape subclass "+originalShape.getClass().getSimpleName()+" is unrecognized. No control points were returned for ShapeDrawObject.");
			return 0;
		}
	}
	
	protected ControlPoint getControlPointImpl(int i) {
		if (originalShape instanceof ScaledShape && ((ScaledShape)originalShape).getOriginalShape() instanceof ParameterizedShape && i >= 9) {
			ScaledShape s = (ScaledShape)originalShape;
			ParameterizedShape ps = (ParameterizedShape)s.getOriginalShape();
			Point2D p = ps.getParameterValue(i-9);
			return new ControlPoint.Double(s.getX()+s.getWidth()*p.getX(), s.getY()+s.getHeight()*p.getY(), ControlPointType.PULLTAB);
		}
		else if (originalShape instanceof Arc2D) {
			Arc2D s = (Arc2D)originalShape;
			double x1 = s.getMinX();
			double y1 = s.getMinY();
			double x2 = s.getMaxX();
			double y2 = s.getMaxY();
			double ax = (x1+x2)/2.0 + s.getWidth()*0.5*Math.cos(Math.toRadians(s.getAngleStart()));
			double ay = (y1+y2)/2.0 + s.getHeight()*0.5*Math.sin(Math.toRadians(s.getAngleStart()));
			double bx = (x1+x2)/2.0 + s.getWidth()*0.5*Math.cos(Math.toRadians(s.getAngleStart()+s.getAngleExtent()));
			double by = (y1+y2)/2.0 + s.getHeight()*0.5*Math.sin(Math.toRadians(s.getAngleStart()+s.getAngleExtent()));
			switch (i) {
			case 0: return new ControlPoint.Double((x1+x2)/2.0, (y1+y2)/2.0, ControlPointType.CENTER);
			case 1: return new ControlPoint.Double(x1, y1, ControlPointType.NORTHWEST);
			case 2: return new ControlPoint.Double(x2, y1, ControlPointType.NORTHEAST);
			case 3: return new ControlPoint.Double(x1, y2, ControlPointType.SOUTHWEST);
			case 4: return new ControlPoint.Double(x2, y2, ControlPointType.SOUTHEAST);
			case 5: return new ControlPoint.Double((x1+x2)/2.0, y1, ControlPointType.NORTH);
			case 6: return new ControlPoint.Double((x1+x2)/2.0, y2, ControlPointType.SOUTH);
			case 7: return new ControlPoint.Double(x1, (y1+y2)/2.0, ControlPointType.WEST);
			case 8: return new ControlPoint.Double(x2, (y1+y2)/2.0, ControlPointType.EAST);
			case 9: return new ControlPoint.Double(ax, ay, ControlPointType.RADIUS);
			case 10: return new ControlPoint.Double(bx, by, ControlPointType.RADIUS);
			default: return null;
			}
		}
		else if (originalShape instanceof RectangularShape) {
			RectangularShape s = (RectangularShape)originalShape;
			double x1 = s.getMinX();
			double y1 = s.getMinY();
			double x2 = s.getMaxX();
			double y2 = s.getMaxY();
			switch (i) {
			case 0: return new ControlPoint.Double((x1+x2)/2.0, (y1+y2)/2.0, ControlPointType.CENTER);
			case 1: return new ControlPoint.Double(x1, y1, ControlPointType.NORTHWEST);
			case 2: return new ControlPoint.Double(x2, y1, ControlPointType.NORTHEAST);
			case 3: return new ControlPoint.Double(x1, y2, ControlPointType.SOUTHWEST);
			case 4: return new ControlPoint.Double(x2, y2, ControlPointType.SOUTHEAST);
			case 5: return new ControlPoint.Double((x1+x2)/2.0, y1, ControlPointType.NORTH);
			case 6: return new ControlPoint.Double((x1+x2)/2.0, y2, ControlPointType.SOUTH);
			case 7: return new ControlPoint.Double(x1, (y1+y2)/2.0, ControlPointType.WEST);
			case 8: return new ControlPoint.Double(x2, (y1+y2)/2.0, ControlPointType.EAST);
			default: return null;
			}
		}
		else if (originalShape instanceof CircularShape) {
			CircularShape s = (CircularShape)originalShape;
			switch (i) {
			case 0: return new ControlPoint.Double(s.getCenter(), ControlPointType.CENTER);
			case 1: return new ControlPoint.Double(s.getEndpoint(), ControlPointType.RADIUS);
			default: return null;
			}
		}
		else if (originalShape instanceof CubicCurve2D) {
			CubicCurve2D s = (CubicCurve2D)originalShape;
			switch (i) {
			case 0: return new ControlPoint.Double(s.getP1(), ControlPointType.ENDPOINT);
			case 1: return new ControlPoint.Double(s.getCtrlP1(), ControlPointType.CONTROL_POINT);
			case 2: return new ControlPoint.Double(s.getCtrlP2(), ControlPointType.CONTROL_POINT);
			case 3: return new ControlPoint.Double(s.getP2(), ControlPointType.ENDPOINT);
			default: return null;
			}
		}
		else if (originalShape instanceof GeneralPath) {
			return DrawObjectUtilities.getPathControlPoint(originalShape, i);
		}
		else if (originalShape instanceof Line2D) {
			Line2D s = (Line2D)originalShape;
			switch (i) {
			case 0: return new ControlPoint.Double(s.getP1(), ControlPointType.ENDPOINT);
			case 1: return new ControlPoint.Double(s.getP2(), ControlPointType.ENDPOINT);
			default: return null;
			}
		}
		else if (originalShape instanceof Polygon) {
			Polygon s = (Polygon)originalShape;
			return new ControlPoint.Float(s.xpoints[i], s.ypoints[i], ControlPointType.STRAIGHT_MIDPOINT);
		}
		else if (originalShape instanceof QuadCurve2D) {
			QuadCurve2D s = (QuadCurve2D)originalShape;
			switch (i) {
			case 0: return new ControlPoint.Double(s.getP1(), ControlPointType.ENDPOINT);
			case 1: return new ControlPoint.Double(s.getCtrlPt(), ControlPointType.CONTROL_POINT);
			case 2: return new ControlPoint.Double(s.getP2(), ControlPointType.ENDPOINT);
			default: return null;
			}
		}
		else if (originalShape instanceof BitmapShape) {
			BitmapShape s = (BitmapShape)originalShape;
			return new ControlPoint.Float(s.getX(), s.getY(), ControlPointType.BASELINE);
		}
		else if (originalShape instanceof RegularPolygon) {
			RegularPolygon s = (RegularPolygon)originalShape;
			if (s.getCenterInternal() != null) {
				switch (i) {
				case 0: return new ControlPoint.Double(s.getCenter(), ControlPointType.CENTER);
				case 1: return new ControlPoint.Double(s.getFirstVertex(), ControlPointType.RADIUS);
				default: return null;
				}
			} else {
				switch (i) {
				case 0: return new ControlPoint.Double(s.getFirstVertex(), ControlPointType.ENDPOINT);
				case 1: return new ControlPoint.Double(s.getSecondVertex(), ControlPointType.ENDPOINT);
				default: return null;
				}
			}
		}
		else {
			System.err.println("Warning: The Shape subclass "+originalShape.getClass().getSimpleName()+" is unrecognized. No control points were returned for ShapeDrawObject.");
			return null;
		}
	}
	
	protected ControlPoint[] getControlPointsImpl() {
		if (originalShape instanceof GeneralPath) {
			return DrawObjectUtilities.getPathControlPoints(originalShape);
		}
		else if (originalShape instanceof Polygon) {
			List<ControlPoint> cp = new Vector<ControlPoint>();
			Polygon poly = (Polygon)originalShape;
			for (int i = 0; i < poly.npoints; i++) {
				cp.add(new ControlPoint.Float(poly.xpoints[i], poly.ypoints[i], ControlPointType.STRAIGHT_MIDPOINT));
			}
			return cp.toArray(new ControlPoint[0]);
		}
		else {
			return super.getControlPointsImpl();
		}
	}
	
	protected double[][] getControlLinesImpl() {
		if (originalShape instanceof ScaledShape && ((ScaledShape)originalShape).getOriginalShape() instanceof ParameterizedShape) {
			ScaledShape s = (ScaledShape)originalShape;
			ParameterizedShape ps = (ParameterizedShape)s.getOriginalShape();
			Vector<double[]> lines = new Vector<double[]>();
			for (ParameterPoint pp : ps.getParameterPoints()) {
				double x1 = pp.getMinimumXorRadius();
				double x2 = pp.getMaximumXorRadius();
				double y1 = pp.getMinimumYorTheta();
				double y2 = pp.getMaximumYorTheta();
				if (!(
						Double.isNaN(x1) || Double.isInfinite(x1)
						|| Double.isNaN(x2) || Double.isInfinite(x2)
						|| Double.isNaN(y1) || Double.isInfinite(y1)
						|| Double.isNaN(y2) || Double.isInfinite(y2)
						|| (x1 == x2 && y1 == y2)
				)) {
					if (pp.isPolar()) {
						if (x1 != x2) {
							lines.add(new double[]{
									s.getX()+s.getWidth()*(pp.getOriginX() + x1 * Math.cos(y1)),
									s.getY()+s.getHeight()*(pp.getOriginY() + x1 * Math.sin(y1)),
									s.getX()+s.getWidth()*(pp.getOriginX() + x2 * Math.cos(y1)),
									s.getY()+s.getHeight()*(pp.getOriginY() + x2 * Math.sin(y1))
							});
							if (y2 != y1) {
								lines.add(new double[]{
										s.getX()+s.getWidth()*(pp.getOriginX() + x1 * Math.cos(y2)),
										s.getY()+s.getHeight()*(pp.getOriginY() + x1 * Math.sin(y2)),
										s.getX()+s.getWidth()*(pp.getOriginX() + x2 * Math.cos(y2)),
										s.getY()+s.getHeight()*(pp.getOriginY() + x2 * Math.sin(y2))
								});
							}
						}
					} else {
						if (x1 == x2 || y1 == y2) {
							lines.add(new double[]{s.getX()+s.getWidth()*x1, s.getY()+s.getHeight()*y1, s.getX()+s.getWidth()*x2, s.getY()+s.getHeight()*y2});
						} else {
							lines.add(new double[]{s.getX()+s.getWidth()*x1, s.getY()+s.getHeight()*y1, s.getX()+s.getWidth()*x2, s.getY()+s.getHeight()*y1});
							lines.add(new double[]{s.getX()+s.getWidth()*x1, s.getY()+s.getHeight()*y2, s.getX()+s.getWidth()*x2, s.getY()+s.getHeight()*y2});
							lines.add(new double[]{s.getX()+s.getWidth()*x1, s.getY()+s.getHeight()*y1, s.getX()+s.getWidth()*x1, s.getY()+s.getHeight()*y2});
							lines.add(new double[]{s.getX()+s.getWidth()*x2, s.getY()+s.getHeight()*y1, s.getX()+s.getWidth()*x2, s.getY()+s.getHeight()*y2});
						}
					}
				}
			}
			return lines.toArray(new double[0][]);
		}
		else if (originalShape instanceof CubicCurve2D) {
			CubicCurve2D s = (CubicCurve2D)originalShape;
			return new double[][]{
					new double[]{s.getX1(), s.getY1(), s.getCtrlX1(), s.getCtrlY1()},
					new double[]{s.getX2(), s.getY2(), s.getCtrlX2(), s.getCtrlY2()},
			};
		}
		else if (originalShape instanceof GeneralPath) {
			return DrawObjectUtilities.getPathControlLines(originalShape);
		}
		else if (originalShape instanceof QuadCurve2D) {
			QuadCurve2D s = (QuadCurve2D)originalShape;
			return new double[][]{
					new double[]{s.getX1(), s.getY1(), s.getCtrlX(), s.getCtrlY()},
					new double[]{s.getX2(), s.getY2(), s.getCtrlX(), s.getCtrlY()},
			};
		}
		else {
			return super.getControlLinesImpl();
		}
	}
	
	protected int setControlPointImpl(int i, Point2D p) {
		if (originalShape instanceof ScaledShape && ((ScaledShape)originalShape).getOriginalShape() instanceof ParameterizedShape && i >= 9) {
			ScaledShape s = (ScaledShape)originalShape;
			ParameterizedShape ps = (ParameterizedShape)s.getOriginalShape();
			ps.setParameterValue(i-9, (p.getX()-s.getX())/s.getWidth(), (p.getY()-s.getY())/s.getHeight());
			return i;
		}
		else if (originalShape instanceof Arc2D) {
			Arc2D s = (Arc2D)originalShape;
			double x1 = s.getX();
			double y1 = s.getY();
			double x2 = s.getX() + s.getWidth();
			double y2 = s.getY() + s.getHeight();
			double ax = (x1+x2)/2.0 + s.getWidth()*0.5*Math.cos(Math.toRadians(s.getAngleStart()));
			double ay = (y1+y2)/2.0 + s.getHeight()*0.5*Math.sin(Math.toRadians(s.getAngleStart()));
			double bx = (x1+x2)/2.0 + s.getWidth()*0.5*Math.cos(Math.toRadians(s.getAngleStart()+s.getAngleExtent()));
			double by = (y1+y2)/2.0 + s.getHeight()*0.5*Math.sin(Math.toRadians(s.getAngleStart()+s.getAngleExtent()));
			boolean changedAngle = false;
			switch (i) {
			case 0:
				x1 = (int)(p.getX() - s.getWidth()/2.0);
				y1 = (int)(p.getY() - s.getHeight()/2.0);
				x2 = x1+s.getWidth();
				y2 = y1+s.getHeight();
				break;
			case 1: x1 = p.getX(); y1 = p.getY(); break;
			case 2: x2 = p.getX(); y1 = p.getY(); break;
			case 3: x1 = p.getX(); y2 = p.getY(); break;
			case 4: x2 = p.getX(); y2 = p.getY(); break;
			case 5: y1 = p.getY(); break;
			case 6: y2 = p.getY(); break;
			case 7: x1 = p.getX(); break;
			case 8: x2 = p.getX(); break;
			case 9: ax = p.getX(); ay = p.getY(); changedAngle = true; break;
			case 10: bx = p.getX(); by = p.getY(); changedAngle = true; break;
			}
			if (changedAngle) {
				double cx = (x1+x2)/2.0;
				double cy = (y1+y2)/2.0;
				double a = Math.atan2(ay-cy, ax-cx);
				double b = Math.atan2(by-cy, bx-cx);
				s.setAngleStart(Math.toDegrees(a));
				s.setAngleExtent(Math.toDegrees(b-a));
				return i;
			} else {
				s.setFrameFromDiagonal(x1, y1, x2, y2);
				if (x2 < x1) {
					     if (i == 2) i = 1;
					else if (i == 8) i = 7;
					else if (i == 4) i = 3;
					else if (i == 1) i = 2;
					else if (i == 7) i = 8;
					else if (i == 3) i = 4;
				}
				if (y2 < y1) {
					     if (i == 1) i = 3;
					else if (i == 5) i = 6;
					else if (i == 2) i = 4;
					else if (i == 3) i = 1;
					else if (i == 6) i = 5;
					else if (i == 4) i = 2;
				}
				return i;
			}
		}
		else if (originalShape instanceof RectangularShape) {
			RectangularShape s = (RectangularShape)originalShape;
			double x1 = s.getX();
			double y1 = s.getY();
			double x2 = s.getX() + s.getWidth();
			double y2 = s.getY() + s.getHeight();
			switch (i) {
			case 0:
				x1 = (int)(p.getX() - s.getWidth()/2.0);
				y1 = (int)(p.getY() - s.getHeight()/2.0);
				x2 = x1+s.getWidth();
				y2 = y1+s.getHeight();
				break;
			case 1: x1 = p.getX(); y1 = p.getY(); break;
			case 2: x2 = p.getX(); y1 = p.getY(); break;
			case 3: x1 = p.getX(); y2 = p.getY(); break;
			case 4: x2 = p.getX(); y2 = p.getY(); break;
			case 5: y1 = p.getY(); break;
			case 6: y2 = p.getY(); break;
			case 7: x1 = p.getX(); break;
			case 8: x2 = p.getX(); break;
			}
			if (s instanceof RightArc || s instanceof ScaledShape) {
				s.setFrame(x1, y1, x2-x1, y2-y1);
				return i;
			} else {
				s.setFrameFromDiagonal(x1, y1, x2, y2);
				if (x2 < x1) {
					     if (i == 2) i = 1;
					else if (i == 8) i = 7;
					else if (i == 4) i = 3;
					else if (i == 1) i = 2;
					else if (i == 7) i = 8;
					else if (i == 3) i = 4;
				}
				if (y2 < y1) {
					     if (i == 1) i = 3;
					else if (i == 5) i = 6;
					else if (i == 2) i = 4;
					else if (i == 3) i = 1;
					else if (i == 6) i = 5;
					else if (i == 4) i = 2;
				}
				return i;
			}
		}
		else if (originalShape instanceof CircularShape) {
			CircularShape s = (CircularShape)originalShape;
			switch (i) {
			case 0: s.setCenter(p); break;
			case 1: s.setEndpoint(p); break;
			}
			return i;
		}
		else if (originalShape instanceof CubicCurve2D) {
			CubicCurve2D s = (CubicCurve2D)originalShape;
			Point2D p1 = s.getP1();
			Point2D cp1 = s.getCtrlP1();
			Point2D cp2 = s.getCtrlP2();
			Point2D p2 = s.getP2();
			switch (i) {
			case 0: p1 = p; break;
			case 1: cp1 = p; break;
			case 2: cp2 = p; break;
			case 3: p2 = p; break;
			}
			s.setCurve(p1, cp1, cp2, p2);
			return i;
		}
		else if (originalShape instanceof GeneralPath) {
			originalShape = DrawObjectUtilities.setPathControlPoint(originalShape, i, p);
			return i;
		}
		else if (originalShape instanceof Line2D) {
			Line2D s = (Line2D)originalShape;
			Point2D p1 = s.getP1();
			Point2D p2 = s.getP2();
			switch (i) {
			case 0: p1 = p; break;
			case 1: p2 = p; break;
			}
			s.setLine(p1, p2);
			return i;
		}
		else if (originalShape instanceof Polygon) {
			Polygon s = (Polygon)originalShape;
			s.xpoints[i] = (int)p.getX();
			s.ypoints[i] = (int)p.getY();
			return i;
		}
		else if (originalShape instanceof QuadCurve2D) {
			QuadCurve2D s = (QuadCurve2D)originalShape;
			Point2D p1 = s.getP1();
			Point2D cp = s.getCtrlPt();
			Point2D p2 = s.getP2();
			switch (i) {
			case 0: p1 = p; break;
			case 1: cp = p; break;
			case 2: p2 = p; break;
			}
			s.setCurve(p1, cp, p2);
			return i;
		}
		else if (originalShape instanceof BitmapShape) {
			BitmapShape s = (BitmapShape)originalShape;
			s.setX((int)p.getX());
			s.setY((int)p.getY());
			return i;
		}
		else if (originalShape instanceof RegularPolygon) {
			RegularPolygon s = (RegularPolygon)originalShape;
			if (s.getCenterInternal() != null) {
				switch (i) {
				case 0: s.moveCenter(p); break;
				case 1: s.setFirstVertex(p); break;
				}
			} else {
				switch (i) {
				case 0: s.setFirstVertex(p); break;
				case 1: s.setSecondVertex(p); break;
				}
			}
			return i;
		}
		else {
			System.err.println("Warning: The Shape subclass "+originalShape.getClass().getSimpleName()+" is unrecognized. No control points were modified in ShapeDrawObject.");
			return i;
		}
	}
	
	protected Point2D getAnchorImpl() {
		if (originalShape instanceof RectangularShape) {
			RectangularShape s = (RectangularShape)originalShape;
			return new Point2D.Double(s.getX(), s.getY());
		}
		else if (originalShape instanceof CircularShape) {
			CircularShape s = (CircularShape)originalShape;
			return s.getCenter();
		}
		else if (originalShape instanceof CubicCurve2D) {
			CubicCurve2D s = (CubicCurve2D)originalShape;
			return s.getP1();
		}
		else if (originalShape instanceof GeneralPath) {
			return DrawObjectUtilities.getPathAnchor(originalShape);
		}
		else if (originalShape instanceof Line2D) {
			Line2D s = (Line2D)originalShape;
			return s.getP1();
		}
		else if (originalShape instanceof Polygon) {
			Polygon s = (Polygon)originalShape;
			return new Point2D.Float(s.xpoints[0], s.ypoints[0]);
		}
		else if (originalShape instanceof QuadCurve2D) {
			QuadCurve2D s = (QuadCurve2D)originalShape;
			return s.getP1();
		}
		else if (originalShape instanceof BitmapShape) {
			BitmapShape s = (BitmapShape)originalShape;
			return new Point2D.Float(s.getX(), s.getY());
		}
		else if (originalShape instanceof RegularPolygon) {
			RegularPolygon s = (RegularPolygon)originalShape;
			return s.getFirstVertex();
		}
		else {
			System.err.println("Warning: The Shape subclass "+originalShape.getClass().getSimpleName()+" is unrecognized. No anchor point was returned for ShapeDrawObject.");
			return null;
		}
	}
	
	protected void setAnchorImpl(Point2D p) {
		if (originalShape instanceof RectangularShape) {
			RectangularShape s = (RectangularShape)originalShape;
			s.setFrame(p.getX(), p.getY(), s.getWidth(), s.getHeight());
		}
		else if (originalShape instanceof CircularShape) {
			CircularShape s = (CircularShape)originalShape;
			s.setCenter(p);
		}
		else if (originalShape instanceof CubicCurve2D) {
			CubicCurve2D s = (CubicCurve2D)originalShape;
			double dx = p.getX()-s.getX1();
			double dy = p.getY()-s.getY1();
			double x1 = s.getX1()+dx;
			double y1 = s.getY1()+dy;
			double ctrlx1 = s.getCtrlX1()+dx;
			double ctrly1 = s.getCtrlY1()+dy;
			double ctrlx2 = s.getCtrlX2()+dx;
			double ctrly2 = s.getCtrlY2()+dy;
			double x2 = s.getX2()+dx;
			double y2 = s.getY2()+dy;
			s.setCurve(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
		}
		else if (originalShape instanceof GeneralPath) {
			originalShape = DrawObjectUtilities.setPathAnchor(originalShape, p);
		}
		else if (originalShape instanceof Line2D) {
			Line2D s = (Line2D)originalShape;
			double dx = p.getX()-s.getX1();
			double dy = p.getY()-s.getY1();
			double x1 = s.getX1()+dx;
			double y1 = s.getY1()+dy;
			double x2 = s.getX2()+dx;
			double y2 = s.getY2()+dy;
			s.setLine(x1, y1, x2, y2);
		}
		else if (originalShape instanceof Polygon) {
			Polygon s = (Polygon)originalShape;
			double dx = p.getX()-s.xpoints[0];
			double dy = p.getY()-s.ypoints[0];
			for (int i = 0; i < s.npoints; i++) {
				s.xpoints[i] = (int)(s.xpoints[i] + dx);
				s.ypoints[i] = (int)(s.ypoints[i] + dy);
			}
		}
		else if (originalShape instanceof QuadCurve2D) {
			QuadCurve2D s = (QuadCurve2D)originalShape;
			double dx = p.getX()-s.getX1();
			double dy = p.getY()-s.getY1();
			double x1 = s.getX1()+dx;
			double y1 = s.getY1()+dy;
			double cx = s.getCtrlX()+dx;
			double cy = s.getCtrlY()+dy;
			double x2 = s.getX2()+dx;
			double y2 = s.getY2()+dy;
			s.setCurve(x1, y1, cx, cy, x2, y2);
		}
		else if (originalShape instanceof BitmapShape) {
			BitmapShape s = (BitmapShape)originalShape;
			s.setX((int)p.getX());
			s.setY((int)p.getY());
		}
		else if (originalShape instanceof RegularPolygon) {
			RegularPolygon s = (RegularPolygon)originalShape;
			double dx = p.getX() - s.getFirstVertex().getX();
			double dy = p.getY() - s.getFirstVertex().getY();
			if (s.getCenterInternal() != null) {
				double x = s.getCenterInternal().getX()+dx;
				double y = s.getCenterInternal().getY()+dy;
				s.moveCenter(x,y);
			} else {
				s.setFirstVertex(p);
				double x = s.getSecondVertexInternal().getX()+dx;
				double y = s.getSecondVertexInternal().getY()+dy;
				s.setSecondVertex(x,y);
			}
		}
		else {
			System.err.println("Warning: The Shape subclass "+originalShape.getClass().getSimpleName()+" is unrecognized. No anchor point was modified in ShapeDrawObject.");
		}
	}
	
	public String toString() {
		return "com.kreative.paint.objects.ShapeDrawObject["+originalShape+","+super.toString()+"]";
	}
}
