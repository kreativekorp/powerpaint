package com.kreative.paint.stroke;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Arrowhead implements List<ArrowheadShape> {
	private final List<ArrowheadShape> shapes;
	public final boolean scale;
	
	public Arrowhead(boolean scale) {
		this.shapes = new ArrayList<ArrowheadShape>();
		this.scale = scale;
	}
	
	public Shape createArrowhead(float prevX, float prevY, float endX, float endY, float lineWidth) {
		float s = scale ? (((lineWidth-1)/2)+1) : 1;
		double r = Math.atan2(endY-prevY, endX-prevX);
		BasicStroke stroke = new BasicStroke(lineWidth);
		AffineTransform st = AffineTransform.getScaleInstance(s, s);
		AffineTransform rt = AffineTransform.getRotateInstance(r);
		AffineTransform tt = AffineTransform.getTranslateInstance(endX, endY);
		Area a = new Area();
		for (ArrowheadShape ash : shapes) {
			Shape sh = st.createTransformedShape(ash);
			if (ash.fill) a.add(new Area(sh));
			if (ash.stroke) a.add(new Area(stroke.createStrokedShape(sh)));
		}
		Shape sh = rt.createTransformedShape(a);
		return tt.createTransformedShape(sh);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof Arrowhead) {
			return (this.shapes.equals(((Arrowhead)that).shapes))
			    && (this.scale == ((Arrowhead)that).scale);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int hashCode = shapes.hashCode();
		if (scale) hashCode ^= -1;
		return hashCode;
	}
	
	@Override
	public boolean add(ArrowheadShape e) {
		return shapes.add(e);
	}
	@Override
	public void add(int index, ArrowheadShape element) {
		shapes.add(index, element);
	}
	@Override
	public boolean addAll(Collection<? extends ArrowheadShape> c) {
		return shapes.addAll(c);
	}
	@Override
	public boolean addAll(int index, Collection<? extends ArrowheadShape> c) {
		return shapes.addAll(index, c);
	}
	@Override
	public void clear() {
		shapes.clear();
	}
	@Override
	public boolean contains(Object o) {
		return shapes.contains(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return shapes.containsAll(c);
	}
	@Override
	public ArrowheadShape get(int index) {
		return shapes.get(index);
	}
	@Override
	public int indexOf(Object o) {
		return shapes.indexOf(o);
	}
	@Override
	public boolean isEmpty() {
		return shapes.isEmpty();
	}
	@Override
	public Iterator<ArrowheadShape> iterator() {
		return shapes.iterator();
	}
	@Override
	public int lastIndexOf(Object o) {
		return shapes.lastIndexOf(o);
	}
	@Override
	public ListIterator<ArrowheadShape> listIterator() {
		return shapes.listIterator();
	}
	@Override
	public ListIterator<ArrowheadShape> listIterator(int index) {
		return shapes.listIterator(index);
	}
	@Override
	public boolean remove(Object o) {
		return shapes.remove(o);
	}
	@Override
	public ArrowheadShape remove(int index) {
		return shapes.remove(index);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return shapes.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return shapes.retainAll(c);
	}
	@Override
	public ArrowheadShape set(int index, ArrowheadShape element) {
		return shapes.set(index, element);
	}
	@Override
	public int size() {
		return shapes.size();
	}
	@Override
	public List<ArrowheadShape> subList(int fromIndex, int toIndex) {
		return shapes.subList(fromIndex, toIndex);
	}
	@Override
	public Object[] toArray() {
		return shapes.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return shapes.toArray(a);
	}
}
