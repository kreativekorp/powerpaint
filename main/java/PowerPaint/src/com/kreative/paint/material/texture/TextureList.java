package com.kreative.paint.material.texture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TextureList implements List<Texture> {
	public final String name;
	private final List<Texture> textures;
	
	public TextureList(String name) {
		this.name = name;
		this.textures = new ArrayList<Texture>();
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof TextureList) {
			return this.equals((TextureList)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(TextureList that, boolean withName) {
		if (!this.textures.equals(that.textures)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return textures.hashCode();
	}
	
	@Override
	public boolean add(Texture e) {
		return textures.add(e);
	}
	@Override
	public void add(int index, Texture element) {
		textures.add(index, element);
	}
	@Override
	public boolean addAll(Collection<? extends Texture> c) {
		return textures.addAll(c);
	}
	@Override
	public boolean addAll(int index, Collection<? extends Texture> c) {
		return textures.addAll(index, c);
	}
	@Override
	public void clear() {
		textures.clear();
	}
	@Override
	public boolean contains(Object o) {
		return textures.contains(o);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return textures.containsAll(c);
	}
	@Override
	public Texture get(int index) {
		return textures.get(index);
	}
	@Override
	public int indexOf(Object o) {
		return textures.indexOf(o);
	}
	@Override
	public boolean isEmpty() {
		return textures.isEmpty();
	}
	@Override
	public Iterator<Texture> iterator() {
		return textures.iterator();
	}
	@Override
	public int lastIndexOf(Object o) {
		return textures.lastIndexOf(o);
	}
	@Override
	public ListIterator<Texture> listIterator() {
		return textures.listIterator();
	}
	@Override
	public ListIterator<Texture> listIterator(int index) {
		return textures.listIterator(index);
	}
	@Override
	public boolean remove(Object o) {
		return textures.remove(o);
	}
	@Override
	public Texture remove(int index) {
		return textures.remove(index);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return textures.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return textures.retainAll(c);
	}
	@Override
	public Texture set(int index, Texture element) {
		return textures.set(index, element);
	}
	@Override
	public int size() {
		return textures.size();
	}
	@Override
	public List<Texture> subList(int fromIndex, int toIndex) {
		return textures.subList(fromIndex, toIndex);
	}
	@Override
	public Object[] toArray() {
		return textures.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return textures.toArray(a);
	}
}
