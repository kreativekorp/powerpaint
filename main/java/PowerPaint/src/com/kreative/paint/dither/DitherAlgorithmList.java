package com.kreative.paint.dither;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DitherAlgorithmList implements List<DitherAlgorithm> {
	private final List<DitherAlgorithm> algorithms;
	public final String name;
	
	public DitherAlgorithmList(String name) {
		this.algorithms = new ArrayList<DitherAlgorithm>();
		this.name = name;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof DitherAlgorithmList) {
			return this.equals((DitherAlgorithmList)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(DitherAlgorithmList that, boolean withName) {
		if (!this.algorithms.equals(that.algorithms)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return algorithms.hashCode();
	}

	@Override
	public boolean add(DitherAlgorithm e) {
		return algorithms.add(e);
	}
	@Override
	public void add(int index, DitherAlgorithm element) {
		algorithms.add(index, element);
	}
	@Override
	public boolean addAll(Collection<? extends DitherAlgorithm> c) {
		return algorithms.addAll(c);
	}
	@Override
	public boolean addAll(int index, Collection<? extends DitherAlgorithm> c) {
		return algorithms.addAll(index, c);
	}
	@Override
	public void clear() {
		algorithms.clear();
	}
	@Override
	public boolean contains(Object o) {
		return algorithms.contains(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return algorithms.containsAll(c);
	}
	@Override
	public DitherAlgorithm get(int index) {
		return algorithms.get(index);
	}
	@Override
	public int indexOf(Object o) {
		return algorithms.indexOf(o);
	}
	@Override
	public boolean isEmpty() {
		return algorithms.isEmpty();
	}
	@Override
	public Iterator<DitherAlgorithm> iterator() {
		return algorithms.iterator();
	}
	@Override
	public int lastIndexOf(Object o) {
		return algorithms.lastIndexOf(o);
	}
	@Override
	public ListIterator<DitherAlgorithm> listIterator() {
		return algorithms.listIterator();
	}
	@Override
	public ListIterator<DitherAlgorithm> listIterator(int index) {
		return algorithms.listIterator(index);
	}
	@Override
	public boolean remove(Object o) {
		return algorithms.remove(o);
	}
	@Override
	public DitherAlgorithm remove(int index) {
		return algorithms.remove(index);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return algorithms.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return algorithms.retainAll(c);
	}
	@Override
	public DitherAlgorithm set(int index, DitherAlgorithm element) {
		return algorithms.set(index, element);
	}
	@Override
	public int size() {
		return algorithms.size();
	}
	@Override
	public List<DitherAlgorithm> subList(int fromIndex, int toIndex) {
		return algorithms.subList(fromIndex, toIndex);
	}
	@Override
	public Object[] toArray() {
		return algorithms.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return algorithms.toArray(a);
	}
}
