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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public interface CellSelectorModel<T> extends List<T> {
	public int getSelectedIndex();
	public T getSelectedObject();
	public void clearSelection();
	public void setSelectedIndex(int index);
	public void setSelectedObject(T object);
	
	public void addCellSelectionListener(CellSelectionListener<T> l);
	public void removeCellSelectionListener(CellSelectionListener<T> l);
	public CellSelectionListener<T>[] getCellSelectionListeners();
	public void notifyCellSelectionListeners(CellSelectionEvent<T> e);

	public boolean add(T o);
	public boolean add(T o, String tip);
	public void add(int index, T element);
	public void add(int index, T element, String tip);
	public boolean addAll(Collection<? extends T> c);
	public boolean addAll(Map<String, ? extends T> m);
	public boolean addAll(int index, Collection<? extends T> c);
	public boolean addAll(int index, Map<String, ? extends T> m);
	public void clear();
	public boolean contains(Object o);
	public boolean containsAll(Collection<?> c);
	public T get(int index);
	public String getToolTipText(int index);
	public int indexOf(Object o);
	public boolean isEmpty();
	public Iterator<T> iterator();
	public Iterator<String> iteratorToolTipText();
	public int lastIndexOf(Object o);
	public ListIterator<T> listIterator();
	public ListIterator<String> listIteratorToolTipText();
	public ListIterator<T> listIterator(int index);
	public ListIterator<String> listIteratorToolTipText(int index);
	public boolean remove(Object o);
	public T remove(int index);
	public boolean removeAll(Collection<?> c);
	public boolean retainAll(Collection<?> c);
	public T set(int index, T element);
	public T set(int index, T element, String tip);
	public String setToolTipText(int index, String tip);
	public int size();
	public List<T> subList(int fromIndex, int toIndex);
	public List<String> subListToolTipText(int fromIndex, int toIndex);
	public Object[] toArray();
	public <T2> T2[] toArray(T2[] a);
	public String[] toArrayToolTipText();
}
