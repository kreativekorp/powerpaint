package com.kreative.paint.rfp;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class FontList {
	private final List<Integer> fontIds;
	private final SortedSet<Integer> sortedIds;
	private final List<String> fontNames;
	private final SortedSet<String> sortedNames;
	private final SortedMap<Integer,SortedSet<String>> idNameMap;
	private final SortedMap<String,SortedSet<Integer>> nameIdMap;
	public final String name;
	
	public FontList(String name) {
		this.fontIds = new ArrayList<Integer>();
		this.sortedIds = new TreeSet<Integer>();
		this.fontNames = new ArrayList<String>();
		this.sortedNames = new TreeSet<String>();
		this.idNameMap = new TreeMap<Integer,SortedSet<String>>();
		this.nameIdMap = new TreeMap<String,SortedSet<Integer>>();
		this.name = name;
	}
	
	public FontList(String name, String... fontNames) {
		this(name);
		for (String fontName : fontNames) {
			add(null, fontName);
		}
	}
	
	public void add(Integer id, String name) {
		fontIds.add(id); if (id != null) sortedIds.add(id);
		fontNames.add(name); if (name != null) sortedNames.add(name);
		if (id != null && name != null) {
			if (idNameMap.containsKey(id)) {
				idNameMap.get(id).add(name);
			} else {
				SortedSet<String> names = new TreeSet<String>();
				names.add(name);
				idNameMap.put(id, names);
			}
			if (nameIdMap.containsKey(name)) {
				nameIdMap.get(name).add(id);
			} else {
				SortedSet<Integer> ids = new TreeSet<Integer>();
				ids.add(id);
				nameIdMap.put(name, ids);
			}
		}
	}
	
	public void clear() {
		fontIds.clear();
		sortedIds.clear();
		fontNames.clear();
		sortedNames.clear();
		idNameMap.clear();
		nameIdMap.clear();
	}
	
	public boolean containsId(Integer id) {
		if (id == null) return fontIds.contains(null);
		else return sortedIds.contains(id);
	}
	
	public boolean containsName(String name) {
		if (name == null) return fontNames.contains(null);
		else return sortedNames.contains(name);
	}
	
	public boolean contains(Integer id, String name) {
		if (containsId(id) && containsName(name)) {
			for (int i = 0, n = size(); i < n; i++) {
				Integer cid = fontIds.get(i);
				String cname = fontNames.get(i);
				if (equals(cid, id) && equals(cname, name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Integer getId(int index) {
		return fontIds.get(index);
	}
	
	public String getName(int index) {
		return fontNames.get(index);
	}
	
	public String[] idToName(Integer id) {
		if (id == null) return null;
		SortedSet<String> names = idNameMap.get(id);
		if (names == null || names.isEmpty()) return null;
		return names.toArray(new String[names.size()]);
	}
	
	public Integer[] nameToId(String name) {
		if (name == null) return null;
		SortedSet<Integer> ids = nameIdMap.get(name);
		if (ids == null || ids.isEmpty()) return null;
		return ids.toArray(new Integer[ids.size()]);
	}
	
	public int indexOfId(Integer id) {
		return fontIds.indexOf(id);
	}
	
	public int indexOfName(String name) {
		return fontNames.indexOf(name);
	}
	
	public int indexOf(Integer id, String name) {
		if (containsId(id) && containsName(name)) {
			for (int i = 0, n = size(); i < n; i++) {
				Integer cid = fontIds.get(i);
				String cname = fontNames.get(i);
				if (equals(cid, id) && equals(cname, name)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public boolean isEmpty() {
		return fontIds.isEmpty() && fontNames.isEmpty();
	}
	
	public int lastIndexOfId(Integer id) {
		return fontIds.lastIndexOf(id);
	}
	
	public int lastIndexOfName(String name) {
		return fontNames.lastIndexOf(name);
	}
	
	public int lastIndexOf(Integer id, String name) {
		if (containsId(id) && containsName(name)) {
			for (int i = size() - 1; i >= 0; i--) {
				Integer cid = fontIds.get(i);
				String cname = fontNames.get(i);
				if (equals(cid, id) && equals(cname, name)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public int size() {
		return Math.min(fontIds.size(), fontNames.size());
	}
	
	public int uniqueIdCount() {
		return sortedIds.size();
	}
	
	public int uniqueNameCount() {
		return sortedNames.size();
	}
	
	public Integer[] idsToArray() {
		return fontIds.toArray(new Integer[fontIds.size()]);
	}
	
	public Integer[] uniqueIdsToArray() {
		return sortedIds.toArray(new Integer[sortedIds.size()]);
	}
	
	public String[] namesToArray() {
		return fontNames.toArray(new String[fontNames.size()]);
	}
	
	public String[] uniqueNamesToArray() {
		return sortedNames.toArray(new String[sortedNames.size()]);
	}
	
	private static boolean equals(Object dis, Object dat) {
		if (dis == null) return (dat == null);
		if (dat == null) return (dis == null);
		return dis.equals(dat);
	}
}
