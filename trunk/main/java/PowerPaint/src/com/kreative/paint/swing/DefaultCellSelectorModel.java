/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.swing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

public class DefaultCellSelectorModel<T> implements CellSelectorModel<T> {
	private List<T> list;
	private List<String> tipList;
	private int sel;
	private List<CellSelectionListener<T>> listeners;
	
	public DefaultCellSelectorModel() {
		list = new Vector<T>();
		tipList = new Vector<String>();
		sel = -1;
		listeners = new Vector<CellSelectionListener<T>>();
	}
	
	public DefaultCellSelectorModel(Collection<? extends T> c) {
		this();
		addAll(c);
	}
	
	public DefaultCellSelectorModel(Map<String, ? extends T> m) {
		this();
		addAll(m);
	}
	
	public DefaultCellSelectorModel(Collection<? extends T> c, T sel) {
		this();
		addAll(c);
		setSelectedObject(sel);
	}
	
	public DefaultCellSelectorModel(Map<String, ? extends T> m, T sel) {
		this();
		addAll(m);
		setSelectedObject(sel);
	}

	public boolean add(T o) {
		return (list.add(o) && tipList.add(null));
	}

	public boolean add(T o, String tip) {
		return (list.add(o) && tipList.add(tip));
	}

	public void add(int index, T element) {
		list.add(index, element);
		tipList.add(index, null);
		if (index <= sel) {
			sel++;
			CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
			notifyCellSelectionListeners(e);
		}
	}

	public void add(int index, T element, String tip) {
		list.add(index, element);
		tipList.add(index, tip);
		if (index <= sel) {
			sel++;
			CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
			notifyCellSelectionListeners(e);
		}
	}

	public boolean addAll(Collection<? extends T> c) {
		return (list.addAll(c) && tipList.addAll(Arrays.asList(new String[c.size()])));
	}

	public boolean addAll(Map<String, ? extends T> m) {
		return (list.addAll(m.values()) && tipList.addAll(m.keySet()));
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		if (list.addAll(c) && tipList.addAll(Arrays.asList(new String[c.size()]))) {
			if (index <= sel) {
				sel += c.size();
				CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
				notifyCellSelectionListeners(e);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean addAll(int index, Map<String, ? extends T> m) {
		if (list.addAll(m.values()) && tipList.addAll(m.keySet())) {
			if (index <= sel) {
				sel += m.size();
				CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
				notifyCellSelectionListeners(e);
			}
			return true;
		} else {
			return false;
		}
	}

	public void addCellSelectionListener(CellSelectionListener<T> l) {
		listeners.add(l);
	}

	public void clear() {
		list.clear();
		tipList.clear();
		sel = -1;
		CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
		notifyCellSelectionListeners(e);
	}

	public void clearSelection() {
		sel = -1;
		CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
		notifyCellSelectionListeners(e);
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public T get(int index) {
		return list.get(index);
	}

	@SuppressWarnings("unchecked")
	public CellSelectionListener<T>[] getCellSelectionListeners() {
		return (CellSelectionListener<T>[])listeners.toArray(new CellSelectionListener[0]);
	}

	public int getSelectedIndex() {
		return sel;
	}

	public T getSelectedObject() {
		if (sel < 0 || sel >= list.size()) return null;
		else return list.get(sel);
	}

	public String getToolTipText(int index) {
		return tipList.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<T> iterator() {
		return list.iterator();
	}

	public Iterator<String> iteratorToolTipText() {
		return tipList.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	public ListIterator<T> listIterator(int index) {
		return list.listIterator(index);
	}

	public ListIterator<String> listIteratorToolTipText() {
		return tipList.listIterator();
	}

	public ListIterator<String> listIteratorToolTipText(int index) {
		return tipList.listIterator(index);
	}

	public void notifyCellSelectionListeners(CellSelectionEvent<T> e) {
		for (CellSelectionListener<T> l : listeners) l.cellSelected(e);
	}

	public boolean remove(Object o) {
		if (list.contains(o)) {
			int i = list.indexOf(o);
			list.remove(i);
			tipList.remove(i);
			if (i == sel) {
				sel = -1;
				CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
				notifyCellSelectionListeners(e);
			} else if (i < sel) {
				sel--;
				CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
				notifyCellSelectionListeners(e);
			}
			return true;
		} else {
			return false;
		}
	}

	public T remove(int index) {
		T ret = list.remove(index);
		tipList.remove(index);
		if (index == sel) {
			sel = -1;
			CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
			notifyCellSelectionListeners(e);
		} else if (index < sel) {
			sel--;
			CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
			notifyCellSelectionListeners(e);
		}
		return ret;
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			if (remove(o)) changed = true;
		}
		return changed;
	}

	public void removeCellSelectionListener(CellSelectionListener<T> l) {
		listeners.remove(l);
	}

	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		for (int i = list.size()-1; i >= 0; i--) {
			if (!c.contains(list.get(i))) {
				remove(i);
				changed = true;
			}
		}
		return changed;
	}

	public T set(int index, T element) {
		T ret = list.set(index, element);
		tipList.set(index, null);
		return ret;
	}

	public T set(int index, T element, String tip) {
		T ret = list.set(index, element);
		tipList.set(index, tip);
		return ret;
	}

	public void setSelectedIndex(int index) {
		sel = index;
		CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
		notifyCellSelectionListeners(e);
	}

	public void setSelectedObject(T object) {
		sel = list.indexOf(object);
		CellSelectionEvent<T> e = new CellSelectionEvent<T>(this, sel, getSelectedObject());
		notifyCellSelectionListeners(e);
	}

	public String setToolTipText(int index, String tip) {
		return tipList.set(index, tip);
	}

	public int size() {
		return list.size();
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	public List<String> subListToolTipText(int fromIndex, int toIndex) {
		return tipList.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T2> T2[] toArray(T2[] a) {
		return list.toArray(a);
	}

	public String[] toArrayToolTipText() {
		return tipList.toArray(new String[0]);
	}
}
