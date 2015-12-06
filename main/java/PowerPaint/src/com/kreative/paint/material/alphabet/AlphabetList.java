package com.kreative.paint.material.alphabet;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AlphabetList implements List<Alphabet> {
	public final String name;
	public final int width;
	public final Font font;
	private final List<Alphabet> alphabets;
	
	public AlphabetList(String name, int width, Font font) {
		this.name = name;
		this.width = width;
		this.font = font;
		this.alphabets = new ArrayList<Alphabet>();
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof AlphabetList) {
			return this.equals((AlphabetList)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(AlphabetList that, boolean withName) {
		if (!this.alphabets.equals(that.alphabets)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return alphabets.hashCode();
	}
	
	@Override
	public boolean add(Alphabet e) {
		return alphabets.add(e);
	}
	@Override
	public void add(int index, Alphabet element) {
		alphabets.add(index, element);
	}
	@Override
	public boolean addAll(Collection<? extends Alphabet> c) {
		return alphabets.addAll(c);
	}
	@Override
	public boolean addAll(int index, Collection<? extends Alphabet> c) {
		return alphabets.addAll(index, c);
	}
	@Override
	public void clear() {
		alphabets.clear();
	}
	@Override
	public boolean contains(Object o) {
		return alphabets.contains(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return alphabets.containsAll(c);
	}
	@Override
	public Alphabet get(int index) {
		return alphabets.get(index);
	}
	@Override
	public int indexOf(Object o) {
		return alphabets.indexOf(o);
	}
	@Override
	public boolean isEmpty() {
		return alphabets.isEmpty();
	}
	@Override
	public Iterator<Alphabet> iterator() {
		return alphabets.iterator();
	}
	@Override
	public int lastIndexOf(Object o) {
		return alphabets.lastIndexOf(o);
	}
	@Override
	public ListIterator<Alphabet> listIterator() {
		return alphabets.listIterator();
	}
	@Override
	public ListIterator<Alphabet> listIterator(int index) {
		return alphabets.listIterator(index);
	}
	@Override
	public boolean remove(Object o) {
		return alphabets.remove(o);
	}
	@Override
	public Alphabet remove(int index) {
		return alphabets.remove(index);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return alphabets.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return alphabets.retainAll(c);
	}
	@Override
	public Alphabet set(int index, Alphabet element) {
		return alphabets.set(index, element);
	}
	@Override
	public int size() {
		return alphabets.size();
	}
	@Override
	public List<Alphabet> subList(int fromIndex, int toIndex) {
		return alphabets.subList(fromIndex, toIndex);
	}
	@Override
	public Object[] toArray() {
		return alphabets.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return alphabets.toArray(a);
	}
}
