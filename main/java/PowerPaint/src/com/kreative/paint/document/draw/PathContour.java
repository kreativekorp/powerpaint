package com.kreative.paint.document.draw;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PathContour implements Cloneable, List<PathPoint> {
	private List<PathPoint> points;
	private boolean closed;
	
	public PathContour() {
		this.points = new ArrayList<PathPoint>();
		this.closed = false;
	}
	
	private PathContour(PathContour o) {
		this.points = new ArrayList<PathPoint>();
		for (PathPoint p : o.points) this.points.add(p.clone());
		this.closed = o.closed;
	}
	
	@Override
	public PathContour clone() {
		return new PathContour(this);
	}
	
	public GeneralPath toAWTShape() {
		GeneralPath path = new GeneralPath();
		appendTo(path);
		return path;
	}
	
	public void appendTo(GeneralPath path) {
		if (points.isEmpty()) return;
		PathPoint p1 = points.get(0);
		path.moveTo(p1.getX(), p1.getY());
		for (int i = 1, n = points.size(); i < n; i++) {
			PathPoint p2 = points.get(i);
			appendSegment(path, p1, p2);
			p1 = p2;
		}
		if (closed) {
			PathPoint p2 = points.get(0);
			appendSegment(path, p1, p2);
			path.closePath();
		}
	}
	
	private static void appendSegment(GeneralPath path, PathPoint p1, PathPoint p2) {
		if (p1.isNextLinear() && p2.isPreviousLinear()) {
			path.lineTo(p2.getX(), p2.getY());
		} else {
			Point2D cp1 = p1.getNextCtrl();
			Point2D cp2 = p2.getPreviousCtrl();
			if (p1.isNextQuadratic() || p2.isPreviousQuadratic()) {
				double cx = (cp1.getX() + cp2.getX()) / 2;
				double cy = (cp1.getY() + cp2.getY()) / 2;
				path.quadTo(cx, cy, p2.getX(), p2.getY());
			} else {
				path.curveTo(
					cp1.getX(), cp1.getY(),
					cp2.getX(), cp2.getY(),
					p2.getX(), p2.getY()
				);
			}
		}
	}
	
	@Override
	public boolean add(PathPoint p) {
		return points.add(p);
	}
	
	@Override
	public void add(int index, PathPoint p) {
		points.add(index, p);
	}
	
	@Override
	public boolean addAll(Collection<? extends PathPoint> c) {
		return points.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends PathPoint> c) {
		return points.addAll(index, c);
	}
	
	@Override
	public void clear() {
		points.clear();
		closed = false;
	}
	
	@Override
	public boolean contains(Object o) {
		return points.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return points.containsAll(c);
	}
	
	@Override
	public PathPoint get(int index) {
		return points.get(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return points.indexOf(o);
	}
	
	@Override
	public boolean isEmpty() {
		return points.isEmpty();
	}
	
	@Override
	public Iterator<PathPoint> iterator() {
		return points.iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return points.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<PathPoint> listIterator() {
		return points.listIterator();
	}
	
	@Override
	public ListIterator<PathPoint> listIterator(int index) {
		return points.listIterator(index);
	}
	
	@Override
	public boolean remove(Object o) {
		return points.remove(o);
	}
	
	@Override
	public PathPoint remove(int index) {
		return points.remove(index);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return points.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return points.retainAll(c);
	}
	
	@Override
	public PathPoint set(int index, PathPoint p) {
		return points.set(index, p);
	}
	
	@Override
	public int size() {
		return points.size();
	}
	
	@Override
	public List<PathPoint> subList(int start, int end) {
		return points.subList(start, end);
	}
	
	@Override
	public Object[] toArray() {
		return points.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return points.toArray(a);
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof PathContour) {
			return (this.points.equals(((PathContour)that).points))
			    && (this.closed == ((PathContour)that).closed);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return points.hashCode() ^ (closed ? -1 : 0);
	}
}
