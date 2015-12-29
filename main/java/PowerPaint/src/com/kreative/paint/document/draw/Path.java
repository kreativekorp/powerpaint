package com.kreative.paint.document.draw;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Path implements Cloneable, List<PathContour> {
	private List<PathContour> contours;
	
	public Path() {
		this.contours = new ArrayList<PathContour>();
	}
	
	private Path(Path o) {
		this.contours = new ArrayList<PathContour>();
		for (PathContour c : o.contours) this.contours.add(c.clone());
	}
	
	@Override
	public Path clone() {
		return new Path(this);
	}
	
	public GeneralPath toAWTShape() {
		GeneralPath path = new GeneralPath();
		appendTo(path);
		return path;
	}
	
	public void appendTo(GeneralPath path) {
		for (PathContour c : contours) c.appendTo(path);
	}
	
	public void appendAWTShape(Shape s) {
		if (s == null) return;
		PathPoint firstPoint = new PathPoint(0, 0);
		PathPoint currPoint = firstPoint;
		PathContour currContour = null;
		double[] coords = new double[6];
		for (PathIterator p = s.getPathIterator(null); !p.isDone(); p.next()) {
			int type = p.currentSegment(coords);
			switch (type) {
				case PathIterator.SEG_MOVETO:
					firstPoint = new PathPoint(coords[0], coords[1]);
					currPoint = firstPoint;
					currContour = null;
					break;
				case PathIterator.SEG_CLOSE:
					if (currContour != null) {
						currContour.setClosed(true);
						firstPoint = new PathPoint(firstPoint.getX(), firstPoint.getY());
						currPoint = firstPoint;
						currContour = null;
					}
					break;
				default:
					if (currContour == null) {
						contours.add(currContour = new PathContour());
						currContour.add(currPoint);
					}
					Point2D ep = getSegmentEndpoint(type, coords);
					if (ep.getX() == firstPoint.getX() && ep.getY() == firstPoint.getY()) {
						setSegmentControlPoints(type, coords, currPoint, firstPoint);
						currContour.setClosed(true);
						firstPoint = new PathPoint(firstPoint.getX(), firstPoint.getY());
						currPoint = firstPoint;
						currContour = null;
					} else {
						PathPoint nextPoint = new PathPoint(ep.getX(), ep.getY());
						setSegmentControlPoints(type, coords, currPoint, nextPoint);
						currContour.add(currPoint = nextPoint);
					}
					break;
			}
		}
	}
	
	private static Point2D getSegmentEndpoint(int type, double[] coords) {
		switch (type) {
			case PathIterator.SEG_LINETO:
				return new Point2D.Double(coords[0], coords[1]);
			case PathIterator.SEG_QUADTO:
				return new Point2D.Double(coords[2], coords[3]);
			case PathIterator.SEG_CUBICTO:
				return new Point2D.Double(coords[4], coords[5]);
			default:
				return null;
		}
	}
	
	private static void setSegmentControlPoints(int type, double[] coords, PathPoint p1, PathPoint p2) {
		switch (type) {
			case PathIterator.SEG_LINETO:
				p1.setNextLinear();
				p2.setPreviousLinear();
				break;
			case PathIterator.SEG_QUADTO:
				p1.setNextCtrl(coords[0], coords[1]);
				p1.setNextQuadratic(true);
				p2.setPreviousCtrl(coords[0], coords[1]);
				p2.setPreviousQuadratic(true);
				break;
			case PathIterator.SEG_CUBICTO:
				p1.setNextCtrl(coords[0], coords[1]);
				p1.setNextQuadratic(false);
				p2.setPreviousCtrl(coords[2], coords[3]);
				p2.setPreviousQuadratic(false);
				break;
		}
	}
	
	@Override
	public boolean add(PathContour c) {
		return contours.add(c);
	}
	
	@Override
	public void add(int index, PathContour c) {
		contours.add(index, c);
	}
	
	@Override
	public boolean addAll(Collection<? extends PathContour> c) {
		return contours.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends PathContour> c) {
		return contours.addAll(index, c);
	}
	
	@Override
	public void clear() {
		contours.clear();
	}
	
	@Override
	public boolean contains(Object o) {
		return contours.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return contours.containsAll(c);
	}
	
	@Override
	public PathContour get(int index) {
		return contours.get(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return contours.indexOf(o);
	}
	
	@Override
	public boolean isEmpty() {
		return contours.isEmpty();
	}
	
	@Override
	public Iterator<PathContour> iterator() {
		return contours.iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return contours.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<PathContour> listIterator() {
		return contours.listIterator();
	}
	
	@Override
	public ListIterator<PathContour> listIterator(int index) {
		return contours.listIterator(index);
	}
	
	@Override
	public boolean remove(Object o) {
		return contours.remove(o);
	}
	
	@Override
	public PathContour remove(int index) {
		return contours.remove(index);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return contours.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return contours.retainAll(c);
	}
	
	@Override
	public PathContour set(int index, PathContour c) {
		return contours.set(index, c);
	}
	
	@Override
	public int size() {
		return contours.size();
	}
	
	@Override
	public List<PathContour> subList(int start, int end) {
		return contours.subList(start, end);
	}
	
	@Override
	public Object[] toArray() {
		return contours.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return contours.toArray(a);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Path) {
			return this.contours.equals(((Path)that).contours);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return contours.hashCode();
	}
}
