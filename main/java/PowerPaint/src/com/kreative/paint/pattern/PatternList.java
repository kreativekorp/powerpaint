package com.kreative.paint.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PatternList implements List<Pattern> {
	private final List<Pattern> patterns;
	public final String name;
	
	public PatternList(String name) {
		this.patterns = new ArrayList<Pattern>();
		this.name = name;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof PatternList) {
			return this.equals((PatternList)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(PatternList that, boolean withName) {
		if (!this.patterns.equals(that.patterns)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return patterns.hashCode();
	}

	@Override
	public boolean add(Pattern e) {
		return patterns.add(e);
	}
	@Override
	public void add(int index, Pattern element) {
		patterns.add(index, element);
	}
	@Override
	public boolean addAll(Collection<? extends Pattern> c) {
		return patterns.addAll(c);
	}
	@Override
	public boolean addAll(int index, Collection<? extends Pattern> c) {
		return patterns.addAll(index, c);
	}
	@Override
	public void clear() {
		patterns.clear();
	}
	@Override
	public boolean contains(Object o) {
		return patterns.contains(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return patterns.containsAll(c);
	}
	@Override
	public Pattern get(int index) {
		return patterns.get(index);
	}
	@Override
	public int indexOf(Object o) {
		return patterns.indexOf(o);
	}
	@Override
	public boolean isEmpty() {
		return patterns.isEmpty();
	}
	@Override
	public Iterator<Pattern> iterator() {
		return patterns.iterator();
	}
	@Override
	public int lastIndexOf(Object o) {
		return patterns.lastIndexOf(o);
	}
	@Override
	public ListIterator<Pattern> listIterator() {
		return patterns.listIterator();
	}
	@Override
	public ListIterator<Pattern> listIterator(int index) {
		return patterns.listIterator(index);
	}
	@Override
	public boolean remove(Object o) {
		return patterns.remove(o);
	}
	@Override
	public Pattern remove(int index) {
		return patterns.remove(index);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return patterns.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return patterns.retainAll(c);
	}
	@Override
	public Pattern set(int index, Pattern element) {
		return patterns.set(index, element);
	}
	@Override
	public int size() {
		return patterns.size();
	}
	@Override
	public List<Pattern> subList(int fromIndex, int toIndex) {
		return patterns.subList(fromIndex, toIndex);
	}
	@Override
	public Object[] toArray() {
		return patterns.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return patterns.toArray(a);
	}
}
