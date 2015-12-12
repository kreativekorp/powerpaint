package com.kreative.paint.material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MaterialList<T> {
	private final List<String> names;
	private final List<T> values;
	
	public MaterialList() {
		this.names = new ArrayList<String>();
		this.values = new ArrayList<T>();
	}
	
	public void add(String name, T value) {
		names.add(name);
		values.add(value);
	}
	
	public void add(int index, String name, T value) {
		names.add(index, name);
		values.add(index, value);
	}
	
	public void addAll(Map<String, ? extends T> m) {
		for (Map.Entry<String, ? extends T> e : m.entrySet()) {
			names.add(e.getKey());
			values.add(e.getValue());
		}
	}
	
	public void addAll(int index, Map<String, ? extends T> m) {
		for (Map.Entry<String, ? extends T> e : m.entrySet()) {
			names.add(index, e.getKey());
			values.add(index, e.getValue());
			index++;
		}
	}
	
	public void addAll(MaterialList<? extends T> m) {
		names.addAll(m.names);
		values.addAll(m.values);
	}
	
	public void addAll(int index, MaterialList<? extends T> m) {
		names.addAll(index, m.names);
		values.addAll(index, m.values);
	}
	
	public void clear() {
		names.clear();
		values.clear();
	}
	
	public boolean containsName(String name) {
		return names.contains(name);
	}
	
	public boolean containsValue(T value) {
		return values.contains(value);
	}
	
	public boolean containsAllNames(Collection<String> names) {
		return this.names.containsAll(names);
	}
	
	public boolean containsAllValues(Collection<? extends T> values) {
		return this.values.containsAll(values);
	}
	
	public String getName(int index) {
		return names.get(index);
	}
	
	public String getName(T value) {
		int o = values.indexOf(value);
		return (o < 0) ? null : names.get(o);
	}
	
	public T getValue(int index) {
		return values.get(index);
	}
	
	public T getValue(String name) {
		int o = names.indexOf(name);
		return (o < 0) ? null : values.get(o);
	}
	
	public int indexOfName(String name) {
		return names.indexOf(name);
	}
	
	public int indexOfValue(T value) {
		return values.indexOf(value);
	}
	
	public boolean isEmpty() {
		return names.isEmpty() || values.isEmpty();
	}
	
	public Iterator<String> nameIterator() {
		return Collections.unmodifiableList(names).iterator();
	}
	
	public Iterator<T> valueIterator() {
		return Collections.unmodifiableList(values).iterator();
	}
	
	public int lastIndexOfName(String name) {
		return names.lastIndexOf(name);
	}
	
	public int lastIndexOfValue(T value) {
		return values.lastIndexOf(value);
	}
	
	public ListIterator<String> nameListIterator() {
		return Collections.unmodifiableList(names).listIterator();
	}
	
	public ListIterator<String> nameListIterator(int index) {
		return Collections.unmodifiableList(names).listIterator(index);
	}
	
	public ListIterator<T> valueListIterator() {
		return Collections.unmodifiableList(values).listIterator();
	}
	
	public ListIterator<T> valueListIterator(int index) {
		return Collections.unmodifiableList(values).listIterator(index);
	}
	
	public String removeName(int index) {
		values.remove(index);
		return names.remove(index);
	}
	
	public boolean removeName(String name) {
		int o = names.indexOf(name);
		if (o < 0) return false;
		names.remove(o);
		values.remove(o);
		return true;
	}
	
	public T removeValue(int index) {
		names.remove(index);
		return values.remove(index);
	}
	
	public boolean removeValue(T value) {
		int o = values.indexOf(value);
		if (o < 0) return false;
		names.remove(o);
		values.remove(o);
		return true;
	}
	
	public boolean removeAllNames(Collection<String> names) {
		boolean changed = false;
		for (String name : names) {
			changed |= removeName(name);
		}
		return changed;
	}
	
	public boolean removeAllValues(Collection<? extends T> values) {
		boolean changed = false;
		for (T value : values) {
			changed |= removeValue(value);
		}
		return changed;
	}
	
	public boolean retainAllNames(Collection<String> names) {
		boolean changed = false;
		for (int i = this.names.size() - 1; i >= 0; i--) {
			if (!names.contains(this.names.get(i))) {
				this.names.remove(i);
				this.values.remove(i);
				changed = true;
			}
		}
		return changed;
	}
	
	public boolean retainAllValues(Collection<? extends T> values) {
		boolean changed = false;
		for (int i = this.names.size() - 1; i >= 0; i--) {
			if (!values.contains(this.values.get(i))) {
				this.names.remove(i);
				this.values.remove(i);
				changed = true;
			}
		}
		return changed;
	}
	
	public String setName(int index, String name) {
		return names.set(index, name);
	}
	
	public String setName(T value, String name) {
		int o = values.indexOf(value);
		return (o < 0) ? null : names.set(o, name);
	}
	
	public T setValue(int index, T value) {
		return values.set(index, value);
	}
	
	public T setValue(String name, T value) {
		int o = names.indexOf(name);
		return (o < 0) ? null : values.set(o, value);
	}
	
	public int size() {
		return Math.min(names.size(), values.size());
	}
	
	public MaterialList<T> subList(int from, int to) {
		return new MaterialList<T>(this, from, to);
	}
	private MaterialList(MaterialList<T> parent, int from, int to) {
		this.names = parent.names.subList(from, to);
		this.values = parent.values.subList(from, to);
	}
	
	public String[] toNameArray() {
		return names.toArray(new String[0]);
	}
	
	public T[] toValueArray() {
		@SuppressWarnings("unchecked")
		T[] a = (T[])new Object[0];
		return values.toArray(a);
	}
	
	public List<String> nameList() {
		return Collections.unmodifiableList(names);
	}
	
	public List<T> valueList() {
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof MaterialList) {
			return this.names.equals(((MaterialList<?>)that).names)
			    && this.values.equals(((MaterialList<?>)that).values);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return names.hashCode() ^ values.hashCode();
	}
}
