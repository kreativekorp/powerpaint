package com.kreative.paint.powershape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PowerShapeList implements List<PowerShape> {
	private final List<PowerShape> shapes;
	public final String name;
	
	public PowerShapeList(String name) {
		this.shapes = new ArrayList<PowerShape>();
		this.name = name;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof PowerShapeList) {
			return this.equals((PowerShapeList)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(PowerShapeList that, boolean withName) {
		if (!this.shapes.equals(that.shapes)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return shapes.hashCode();
	}

	@Override
	public boolean add(PowerShape e) {
		return shapes.add(e);
	}
	@Override
	public void add(int index, PowerShape element) {
		shapes.add(index, element);
	}
	@Override
	public boolean addAll(Collection<? extends PowerShape> c) {
		return shapes.addAll(c);
	}
	@Override
	public boolean addAll(int index, Collection<? extends PowerShape> c) {
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
	public PowerShape get(int index) {
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
	public Iterator<PowerShape> iterator() {
		return shapes.iterator();
	}
	@Override
	public int lastIndexOf(Object o) {
		return shapes.lastIndexOf(o);
	}
	@Override
	public ListIterator<PowerShape> listIterator() {
		return shapes.listIterator();
	}
	@Override
	public ListIterator<PowerShape> listIterator(int index) {
		return shapes.listIterator(index);
	}
	@Override
	public boolean remove(Object o) {
		return shapes.remove(o);
	}
	@Override
	public PowerShape remove(int index) {
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
	public PowerShape set(int index, PowerShape element) {
		return shapes.set(index, element);
	}
	@Override
	public int size() {
		return shapes.size();
	}
	@Override
	public List<PowerShape> subList(int fromIndex, int toIndex) {
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
