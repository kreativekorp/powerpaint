/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class PairList<F,L> implements List<Pair<F,L>> {
	private List<Pair<F,L>> plist; 
	private List<F> flist;
	private List<L> llist;
	
	public PairList() {
		plist = new ArrayList<Pair<F,L>>();
		flist = new ArrayList<F>();
		llist = new ArrayList<L>();
	}
	
	public PairList(Collection<? extends Pair<F,L>> c) {
		plist = new ArrayList<Pair<F,L>>();
		flist = new ArrayList<F>();
		llist = new ArrayList<L>();
		plist.addAll(c);
		for (Pair<F,L> p : c) {
			flist.add(p.getFormer());
			llist.add(p.getLatter());
		}
	}
	
	public PairList(Map<? extends F, ? extends L> m) {
		plist = new ArrayList<Pair<F,L>>();
		flist = new ArrayList<F>();
		llist = new ArrayList<L>();
		flist.addAll(m.keySet());
		llist.addAll(m.values());
		for (Map.Entry<? extends F, ? extends L> e : m.entrySet()) {
			plist.add(new Pair<F,L>(e.getKey(), e.getValue()));
		}
	}
	
	public boolean add(Pair<F,L> o) {
		return plist.add(o) && flist.add(o.getFormer()) && llist.add(o.getLatter());
	}
	
	public boolean add(F f, L l) {
		return plist.add(new Pair<F,L>(f,l)) && flist.add(f) && llist.add(l);
	}

	public void add(int index, Pair<F,L> element) {
		plist.add(index, element);
		flist.add(index, element.getFormer());
		llist.add(index, element.getLatter());
	}
	
	public void add(int index, F f, L l) {
		plist.add(index, new Pair<F,L>(f,l));
		flist.add(index, f);
		llist.add(index, l);
	}

	public boolean addAll(Collection<? extends Pair<F,L>> c) {
		boolean changed = false;
		if (plist.addAll(c)) changed = true;
		for (Pair<F,L> p : c) {
			if (flist.add(p.getFormer())) changed = true;
			if (llist.add(p.getLatter())) changed = true;
		}
		return changed;
	}
	
	public boolean addAll(Map<? extends F, ? extends L> m) {
		boolean changed = false;
		for (Map.Entry<? extends F, ? extends L> e : m.entrySet()) {
			if (plist.add(new Pair<F,L>(e.getKey(), e.getValue()))) changed = true;
			if (flist.add(e.getKey())) changed = true;
			if (llist.add(e.getValue())) changed = true;
		}
		return changed;
	}

	public boolean addAll(int index, Collection<? extends Pair<F,L>> c) {
		boolean changed = false;
		if (plist.addAll(index, c)) changed = true;
		for (Pair<F,L> p : c) {
			flist.add(index, p.getFormer());
			llist.add(index, p.getLatter());
			index++;
			changed = true;
		}
		return changed;
	}
	
	public boolean addAll(int index, Map<? extends F, ? extends L> m) {
		boolean changed = false;
		for (Map.Entry<? extends F, ? extends L> e : m.entrySet()) {
			plist.add(index, new Pair<F,L>(e.getKey(), e.getValue()));
			flist.add(index, e.getKey());
			llist.add(index, e.getValue());
			index++;
			changed = true;
		}
		return changed;
	}

	public void clear() {
		plist.clear();
		flist.clear();
		llist.clear();
	}

	public boolean contains(Object o) {
		return plist.contains(o);
	}
	
	public boolean containsFormer(Object o) {
		return flist.contains(o);
	}
	
	public boolean containsLatter(Object o) {
		return llist.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return plist.containsAll(c);
	}
	
	public boolean containsAllFormer(Collection<?> c) {
		return flist.containsAll(c);
	}
	
	public boolean containsAllLatter(Collection<?> c) {
		return llist.containsAll(c);
	}

	public Pair<F,L> get(int index) {
		return plist.get(index);
	}
	
	public F getFormer(int index) {
		return flist.get(index);
	}
	
	public L getLatter(int index) {
		return llist.get(index);
	}

	public int indexOf(Object o) {
		return plist.indexOf(o);
	}
	
	public int indexOfFormer(Object o) {
		return flist.indexOf(o);
	}
	
	public int indexOfLatter(Object o) {
		return llist.indexOf(o);
	}

	public boolean isEmpty() {
		return plist.isEmpty();
	}

	public Iterator<Pair<F,L>> iterator() {
		return new Iterator<Pair<F,L>>() {
			private int i = 0;
			public boolean hasNext() {
				return i < plist.size();
			}
			public Pair<F,L> next() {
				return plist.get(i++);
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
		};
	}
	
	public Iterator<F> formerIterator() {
		return new Iterator<F>() {
			private int i = 0;
			public boolean hasNext() {
				return i < flist.size();
			}
			public F next() {
				return flist.get(i++);
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
		};
	}
	
	public Iterator<L> latterIterator() {
		return new Iterator<L>() {
			private int i = 0;
			public boolean hasNext() {
				return i < llist.size();
			}
			public L next() {
				return llist.get(i++);
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
		};
	}

	public int lastIndexOf(Object o) {
		return plist.lastIndexOf(o);
	}
	
	public int lastIndexOfFormer(Object o) {
		return flist.lastIndexOf(o);
	}
	
	public int lastIndexOfLatter(Object o) {
		return llist.lastIndexOf(o);
	}

	public ListIterator<Pair<F,L>> listIterator() {
		return new ListIterator<Pair<F,L>>() {
			private int i = 0;
			public void add(Pair<F,L> o) {
				plist.add(i, o);
				flist.add(i, o.getFormer());
				llist.add(i, o.getLatter());
			}
			public boolean hasNext() {
				return i < plist.size();
			}
			public boolean hasPrevious() {
				return i > 0;
			}
			public Pair<F,L> next() {
				return plist.get(i++);
			}
			public int nextIndex() {
				return i;
			}
			public Pair<F,L> previous() {
				return plist.get(--i);
			}
			public int previousIndex() {
				return i-1;
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
			public void set(Pair<F, L> o) {
				plist.set(i-1, o);
				flist.set(i-1, o.getFormer());
				llist.set(i-1, o.getLatter());
			}
		};
	}
	
	public ListIterator<F> formerListIterator() {
		return new ListIterator<F>() {
			private int i = 0;
			public void add(F o) {
				throw new UnsupportedOperationException();
			}
			public boolean hasNext() {
				return i < flist.size();
			}
			public boolean hasPrevious() {
				return i > 0;
			}
			public F next() {
				return flist.get(i++);
			}
			public int nextIndex() {
				return i;
			}
			public F previous() {
				return flist.get(--i);
			}
			public int previousIndex() {
				return i-1;
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
			public void set(F o) {
				plist.set(i-1, new Pair<F,L>(o, llist.get(i-1)));
				flist.set(i-1, o);
			}
		};
	}

	public ListIterator<L> latterListIterator() {
		return new ListIterator<L>() {
			private int i = 0;
			public void add(L o) {
				throw new UnsupportedOperationException();
			}
			public boolean hasNext() {
				return i < llist.size();
			}
			public boolean hasPrevious() {
				return i > 0;
			}
			public L next() {
				return llist.get(i++);
			}
			public int nextIndex() {
				return i;
			}
			public L previous() {
				return llist.get(--i);
			}
			public int previousIndex() {
				return i-1;
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
			public void set(L o) {
				plist.set(i-1, new Pair<F,L>(flist.get(i-1), o));
				llist.set(i-1, o);
			}
		};
	}

	public ListIterator<Pair<F,L>> listIterator(final int index) {
		return new ListIterator<Pair<F,L>>() {
			private int i = index;
			public void add(Pair<F, L> o) {
				plist.add(i, o);
				flist.add(i, o.getFormer());
				llist.add(i, o.getLatter());
			}
			public boolean hasNext() {
				return i < plist.size();
			}
			public boolean hasPrevious() {
				return i > 0;
			}
			public Pair<F,L> next() {
				return plist.get(i++);
			}
			public int nextIndex() {
				return i;
			}
			public Pair<F, L> previous() {
				return plist.get(--i);
			}
			public int previousIndex() {
				return i-1;
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
			public void set(Pair<F, L> o) {
				plist.set(i-1, o);
				flist.set(i-1, o.getFormer());
				llist.set(i-1, o.getLatter());
			}
		};
	}
	
	public ListIterator<F> formerListIterator(final int index) {
		return new ListIterator<F>() {
			private int i = index;
			public void add(F o) {
				throw new UnsupportedOperationException();
			}
			public boolean hasNext() {
				return i < flist.size();
			}
			public boolean hasPrevious() {
				return i > 0;
			}
			public F next() {
				return flist.get(i++);
			}
			public int nextIndex() {
				return i;
			}
			public F previous() {
				return flist.get(--i);
			}
			public int previousIndex() {
				return i-1;
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
			public void set(F o) {
				plist.set(i-1, new Pair<F,L>(o, llist.get(i-1)));
				flist.set(i-1, o);
			}
		};
	}

	public ListIterator<L> latterListIterator(final int index) {
		return new ListIterator<L>() {
			private int i = index;
			public void add(L o) {
				throw new UnsupportedOperationException();
			}
			public boolean hasNext() {
				return i < llist.size();
			}
			public boolean hasPrevious() {
				return i > 0;
			}
			public L next() {
				return llist.get(i++);
			}
			public int nextIndex() {
				return i;
			}
			public L previous() {
				return llist.get(--i);
			}
			public int previousIndex() {
				return i-1;
			}
			public void remove() {
				i--;
				plist.remove(i);
				flist.remove(i);
				llist.remove(i);
			}
			public void set(L o) {
				plist.set(i-1, new Pair<F,L>(flist.get(i-1), o));
				llist.set(i-1, o);
			}
		};
	}

	public boolean remove(Object o) {
		if (plist.contains(o)) {
			int i = plist.indexOf(o);
			plist.remove(i);
			flist.remove(i);
			llist.remove(i);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeFormer(Object o) {
		if (flist.contains(o)) {
			int i = flist.indexOf(o);
			plist.remove(i);
			flist.remove(i);
			llist.remove(i);
			return true;
		} else {
			return false;
		}
	}

	public boolean removeLatter(Object o) {
		if (llist.contains(o)) {
			int i = llist.indexOf(o);
			plist.remove(i);
			flist.remove(i);
			llist.remove(i);
			return true;
		} else {
			return false;
		}
	}

	public Pair<F,L> remove(int index) {
		flist.remove(index);
		llist.remove(index);
		return plist.remove(index);
	}
	
	public F removeFormer(int index) {
		plist.remove(index);
		llist.remove(index);
		return flist.remove(index);
	}
	
	public L removeLatter(int index) {
		plist.remove(index);
		flist.remove(index);
		return llist.remove(index);
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			if (remove(o)) changed = true;
		}
		return changed;
	}
	
	public boolean removeAllFormer(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			if (removeFormer(o)) changed = true;
		}
		return changed;
	}
	
	public boolean removeAllLatter(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			if (removeLatter(o)) changed = true;
		}
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		for (int i = plist.size()-1; i >= 0; i--) {
			if (!c.contains(plist.get(i))) {
				remove(i);
				changed = true;
			}
		}
		return changed;
	}

	public boolean retainAllFormer(Collection<?> c) {
		boolean changed = false;
		for (int i = flist.size()-1; i >= 0; i--) {
			if (!c.contains(flist.get(i))) {
				remove(i);
				changed = true;
			}
		}
		return changed;
	}

	public boolean retainAllLatter(Collection<?> c) {
		boolean changed = false;
		for (int i = llist.size()-1; i >= 0; i--) {
			if (!c.contains(llist.get(i))) {
				remove(i);
				changed = true;
			}
		}
		return changed;
	}
	
	public Pair<F,L> set(int index, Pair<F, L> element) {
		flist.set(index, element.getFormer());
		llist.set(index, element.getLatter());
		return plist.set(index, element);
	}
	
	public F setFormer(int index, F element) {
		plist.set(index, new Pair<F,L>(element, llist.get(index)));
		return flist.set(index, element);
	}
	
	public L setLatter(int index, L element) {
		plist.set(index, new Pair<F,L>(flist.get(index), element));
		return llist.set(index, element);
	}

	public int size() {
		return plist.size();
	}
	
	private PairList(PairList<F,L> src, int fromIndex, int toIndex) {
		plist = src.plist.subList(fromIndex, toIndex);
		flist = src.flist.subList(fromIndex, toIndex);
		llist = src.llist.subList(fromIndex, toIndex);
	}
	public List<Pair<F,L>> subList(int fromIndex, int toIndex) {
		return new PairList<F,L>(this, fromIndex, toIndex);
	}
	
	public Object[] toArray() {
		return plist.toArray();
	}
	
	public Object[] toFormerArray() {
		return flist.toArray();
	}
	
	public Object[] toLatterArray() {
		return llist.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return plist.toArray(a);
	}
	
	public <T> T[] toFormerArray(T[] a) {
		return flist.toArray(a);
	}
	
	public <T> T[] toLatterArray(T[] a) {
		return llist.toArray(a);
	}
	
	public List<F> formerList() {
		return new List<F>() {
			public boolean add(F o) {
				throw new UnsupportedOperationException();
			}
			public void add(int index, F element) {
				throw new UnsupportedOperationException();
			}
			public boolean addAll(Collection<? extends F> c) {
				throw new UnsupportedOperationException();
			}
			public boolean addAll(int index, Collection<? extends F> c) {
				throw new UnsupportedOperationException();
			}
			public void clear() {
				PairList.this.clear();
			}
			public boolean contains(Object o) {
				return PairList.this.containsFormer(o);
			}
			public boolean containsAll(Collection<?> c) {
				return PairList.this.containsAllFormer(c);
			}
			public F get(int index) {
				return PairList.this.getFormer(index);
			}
			public int indexOf(Object o) {
				return PairList.this.indexOfFormer(o);
			}
			public boolean isEmpty() {
				return PairList.this.isEmpty();
			}
			public Iterator<F> iterator() {
				return PairList.this.formerIterator();
			}
			public int lastIndexOf(Object o) {
				return PairList.this.lastIndexOfFormer(o);
			}
			public ListIterator<F> listIterator() {
				return PairList.this.formerListIterator();
			}
			public ListIterator<F> listIterator(int index) {
				return PairList.this.formerListIterator(index);
			}
			public boolean remove(Object o) {
				return PairList.this.removeFormer(o);
			}
			public F remove(int index) {
				return PairList.this.removeFormer(index);
			}
			public boolean removeAll(Collection<?> c) {
				return PairList.this.removeAllFormer(c);
			}
			public boolean retainAll(Collection<?> c) {
				return PairList.this.retainAllFormer(c);
			}
			public F set(int index, F element) {
				return PairList.this.setFormer(index, element);
			}
			public int size() {
				return PairList.this.size();
			}
			@SuppressWarnings("unchecked")
			public List<F> subList(int fromIndex, int toIndex) {
				return ((PairList<F,L>)PairList.this.subList(fromIndex, toIndex)).formerList();
			}
			public Object[] toArray() {
				return PairList.this.toFormerArray();
			}
			public <T> T[] toArray(T[] a) {
				return PairList.this.toFormerArray(a);
			}
		};
	}
	
	public List<L> latterList() {
		return new List<L>() {
			public boolean add(L o) {
				throw new UnsupportedOperationException();
			}
			public void add(int index, L element) {
				throw new UnsupportedOperationException();
			}
			public boolean addAll(Collection<? extends L> c) {
				throw new UnsupportedOperationException();
			}
			public boolean addAll(int index, Collection<? extends L> c) {
				throw new UnsupportedOperationException();
			}
			public void clear() {
				PairList.this.clear();
			}
			public boolean contains(Object o) {
				return PairList.this.containsLatter(o);
			}
			public boolean containsAll(Collection<?> c) {
				return PairList.this.containsAllLatter(c);
			}
			public L get(int index) {
				return PairList.this.getLatter(index);
			}
			public int indexOf(Object o) {
				return PairList.this.indexOfLatter(o);
			}
			public boolean isEmpty() {
				return PairList.this.isEmpty();
			}
			public Iterator<L> iterator() {
				return PairList.this.latterIterator();
			}
			public int lastIndexOf(Object o) {
				return PairList.this.lastIndexOfLatter(o);
			}
			public ListIterator<L> listIterator() {
				return PairList.this.latterListIterator();
			}
			public ListIterator<L> listIterator(int index) {
				return PairList.this.latterListIterator(index);
			}
			public boolean remove(Object o) {
				return PairList.this.removeLatter(o);
			}
			public L remove(int index) {
				return PairList.this.removeLatter(index);
			}
			public boolean removeAll(Collection<?> c) {
				return PairList.this.removeAllLatter(c);
			}
			public boolean retainAll(Collection<?> c) {
				return PairList.this.retainAllLatter(c);
			}
			public L set(int index, L element) {
				return PairList.this.setLatter(index, element);
			}
			public int size() {
				return PairList.this.size();
			}
			@SuppressWarnings("unchecked")
			public List<L> subList(int fromIndex, int toIndex) {
				return ((PairList<F,L>)PairList.this.subList(fromIndex, toIndex)).latterList();
			}
			public Object[] toArray() {
				return PairList.this.toLatterArray();
			}
			public <T> T[] toArray(T[] a) {
				return PairList.this.toLatterArray(a);
			}
		};
	}
	
	public Map<F,L> asMap() {
		return new Map<F,L>() {
			public void clear() {
				PairList.this.clear();
			}
			public boolean containsKey(Object key) {
				return PairList.this.containsFormer(key);
			}
			public boolean containsValue(Object value) {
				return PairList.this.containsLatter(value);
			}
			public Set<Map.Entry<F,L>> entrySet() {
				return new Set<Map.Entry<F,L>>() {
					public boolean add(Map.Entry<F,L> o) {
						put(o.getKey(), o.getValue());
						return true;
					}
					public boolean addAll(Collection<? extends Map.Entry<F,L>> c) {
						boolean changed = false;
						for (Map.Entry<F,L> o : c) {
							put(o.getKey(), o.getValue());
							changed = true;
						}
						return changed;
					}
					public void clear() {
						PairList.this.clear();
					}
					public boolean contains(Object o) {
						if (o instanceof Map.Entry) {
							Map.Entry<?,?> e = (Map.Entry<?,?>)o;
							return PairList.this.contains(new Pair<Object,Object>(e.getKey(), e.getValue()));
						}
						return false;
					}
					public boolean containsAll(Collection<?> c) {
						for (Object o : c) {
							if (o instanceof Map.Entry) {
								Map.Entry<?,?> e = (Map.Entry<?,?>)o;
								if (!PairList.this.contains(new Pair<Object,Object>(e.getKey(), e.getValue()))) {
									return false;
								}
							} else {
								return false;
							}
						}
						return true;
					}
					public boolean isEmpty() {
						return PairList.this.isEmpty();
					}
					public Iterator<Map.Entry<F,L>> iterator() {
						return new Iterator<Map.Entry<F,L>>() {
							private int i = 0;
							public boolean hasNext() {
								return i < PairList.this.size();
							}
							public Map.Entry<F,L> next() {
								final int j = i++;
								return new Map.Entry<F,L>() {
									public F getKey() {
										return PairList.this.getFormer(j);
									}
									public L getValue() {
										return PairList.this.getLatter(j);
									}
									public L setValue(L value) {
										return PairList.this.setLatter(j, value);
									}
								};
							}
							public void remove() {
								i--;
								PairList.this.remove(i);
							}
						};
					}
					public boolean remove(Object o) {
						if (o instanceof Map.Entry) {
							Map.Entry<?,?> e = (Map.Entry<?,?>)o;
							return PairList.this.remove(new Pair<Object,Object>(e.getKey(), e.getValue()));
						}
						return false;
					}
					public boolean removeAll(Collection<?> c) {
						boolean changed = false;
						for (Object o : c) {
							if (o instanceof Map.Entry) {
								Map.Entry<?,?> e = (Map.Entry<?,?>)o;
								if (PairList.this.remove(new Pair<Object,Object>(e.getKey(), e.getValue()))) {
									changed = true;
								}
							}
						}
						return changed;
					}
					public boolean retainAll(Collection<?> c) {
						boolean changed = false;
						for (int i = PairList.this.size()-1; i >= 0; i--) {
							final int j = i++;
							Map.Entry<F,L> e = new Map.Entry<F,L>() {
								public F getKey() {
									return PairList.this.getFormer(j);
								}
								public L getValue() {
									return PairList.this.getLatter(j);
								}
								public L setValue(L value) {
									return PairList.this.setLatter(j, value);
								}
							};
							if (!c.contains(e)) {
								PairList.this.remove(i);
							}
						}
						return changed;
					}
					public int size() {
						return PairList.this.size();
					}
					public Object[] toArray() {
						Object[] o = new Object[PairList.this.size()];
						for (int i = 0; i < o.length; i++) {
							final int j = i;
							o[i] = new Map.Entry<F,L>() {
								public F getKey() {
									return PairList.this.getFormer(j);
								}
								public L getValue() {
									return PairList.this.getLatter(j);
								}
								public L setValue(L value) {
									return PairList.this.setLatter(j, value);
								}
							};
						}
						return o;
					}
					@SuppressWarnings("unchecked")
					public <T> T[] toArray(T[] a) {
						return (T[])toArray();
					}
				};
			}
			public L get(Object key) {
				int i = PairList.this.indexOfFormer(key);
				if (i >= 0) {
					return PairList.this.getLatter(i);
				} else {
					return null;
				}
			}
			public boolean isEmpty() {
				return PairList.this.isEmpty();
			}
			public Set<F> keySet() {
				return new Set<F>() {
					public boolean add(F o) {
						throw new UnsupportedOperationException();
					}
					public boolean addAll(Collection<? extends F> c) {
						throw new UnsupportedOperationException();
					}
					public void clear() {
						PairList.this.clear();
					}
					public boolean contains(Object o) {
						return PairList.this.containsFormer(o);
					}
					public boolean containsAll(Collection<?> c) {
						return PairList.this.containsAllFormer(c);
					}
					public boolean isEmpty() {
						return PairList.this.isEmpty();
					}
					public Iterator<F> iterator() {
						return PairList.this.formerIterator();
					}
					public boolean remove(Object o) {
						return PairList.this.removeFormer(o);
					}
					public boolean removeAll(Collection<?> c) {
						return PairList.this.removeAllFormer(c);
					}
					public boolean retainAll(Collection<?> c) {
						return PairList.this.retainAllFormer(c);
					}
					public int size() {
						return PairList.this.size();
					}
					public Object[] toArray() {
						return PairList.this.toFormerArray();
					}
					public <T> T[] toArray(T[] a) {
						return PairList.this.toFormerArray(a);
					}
				};
			}
			public L put(F key, L value) {
				int i = PairList.this.indexOfFormer(key);
				if (i >= 0) {
					return PairList.this.setLatter(i, value);
				} else {
					PairList.this.add(key, value);
					return null;
				}
			}
			public void putAll(Map<? extends F, ? extends L> t) {
				for (Map.Entry<? extends F, ? extends L> e : t.entrySet()) {
					put(e.getKey(), e.getValue());
				}
			}
			public L remove(Object key) {
				int i = PairList.this.indexOfFormer(key);
				if (i >= 0) {
					return PairList.this.removeLatter(i);
				} else {
					return null;
				}
			}
			public int size() {
				return PairList.this.size();
			}
			public Collection<L> values() {
				return new Collection<L>() {
					public boolean add(L o) {
						throw new UnsupportedOperationException();
					}
					public boolean addAll(Collection<? extends L> c) {
						throw new UnsupportedOperationException();
					}
					public void clear() {
						PairList.this.clear();
					}
					public boolean contains(Object o) {
						return PairList.this.containsLatter(o);
					}
					public boolean containsAll(Collection<?> c) {
						return PairList.this.containsAllLatter(c);
					}
					public boolean isEmpty() {
						return PairList.this.isEmpty();
					}
					public Iterator<L> iterator() {
						return PairList.this.latterIterator();
					}
					public boolean remove(Object o) {
						return PairList.this.removeLatter(o);
					}
					public boolean removeAll(Collection<?> c) {
						return PairList.this.removeAllLatter(c);
					}
					public boolean retainAll(Collection<?> c) {
						return PairList.this.retainAllLatter(c);
					}
					public int size() {
						return PairList.this.size();
					}
					public Object[] toArray() {
						return PairList.this.toLatterArray();
					}
					public <T> T[] toArray(T[] a) {
						return PairList.this.toLatterArray(a);
					}
				};
			}
		};
	}
	
	public int hashCode() {
		return plist.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof PairList) {
			PairList<?,?> other = (PairList<?,?>)o;
			return this.plist.equals(other.plist);
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "com.kreative.paint.util.PairList["+plist.toString()+"]";
	}
}
