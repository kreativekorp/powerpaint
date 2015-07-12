package com.kreative.paint.gradient;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class GradientColorMap implements SortedSet<GradientColorStop> {
	public static final GradientColorMap BLACK_TO_WHITE = new GradientColorMap(
		"Black to White",
		new GradientColorStop(0.0, GradientColor.BLACK),
		new GradientColorStop(1.0, GradientColor.WHITE)
	);
	public static final GradientColorMap WHITE_TO_BLACK = new GradientColorMap(
		"White to Black",
		new GradientColorStop(0.0, GradientColor.WHITE),
		new GradientColorStop(1.0, GradientColor.BLACK)
	);
	public static final GradientColorMap RGB_SPECTRUM = new GradientColorMap(
		"RGB Spectrum",
		new GradientColorStop(0.0/6.0, GradientColor.RED),
		new GradientColorStop(1.0/6.0, GradientColor.YELLOW),
		new GradientColorStop(2.0/6.0, GradientColor.GREEN),
		new GradientColorStop(3.0/6.0, GradientColor.CYAN),
		new GradientColorStop(4.0/6.0, GradientColor.BLUE),
		new GradientColorStop(5.0/6.0, GradientColor.MAGENTA),
		new GradientColorStop(6.0/6.0, GradientColor.RED)
	);
	
	private final SortedSet<GradientColorStop> stops;
	public final String name;
	
	public GradientColorMap(String name) {
		this.stops = new TreeSet<GradientColorStop>();
		this.name = name;
	}
	
	private GradientColorMap(String name, GradientColorStop... stops) {
		SortedSet<GradientColorStop> stopsIntl = new TreeSet<GradientColorStop>();
		for (GradientColorStop stop : stops) stopsIntl.add(stop);
		this.stops = Collections.unmodifiableSortedSet(stopsIntl);
		this.name = name;
	}
	
	public int getRGB(double pos) {
		try {
			double prevPos = Double.NEGATIVE_INFINITY;
			Color  prevCol = null;
			double nextPos = Double.POSITIVE_INFINITY;
			Color  nextCol = null;
			for (GradientColorStop stop : stops) {
				double thisPos = stop.position;
				Color  thisCol = stop.color.awtColor();
				if (thisPos <= pos && thisPos > prevPos) {
					prevPos = thisPos;
					prevCol = thisCol;
				}
				if (thisPos >= pos && thisPos < nextPos) {
					nextPos = thisPos;
					nextCol = thisCol;
				}
			}
			if (prevCol == null && nextCol == null) return 0;
			else if (prevPos == pos || nextCol == null) return prevCol.getRGB();
			else if (nextPos == pos || prevCol == null) return nextCol.getRGB();
			else {
				float f = (float)((pos - prevPos) / (nextPos - prevPos));
				float[] c1 = prevCol.getRGBComponents(new float[4]);
				float[] c2 = nextCol.getRGBComponents(new float[4]);
				float r = c1[0] + (c2[0]-c1[0])*f;
				float g = c1[1] + (c2[1]-c1[1])*f;
				float b = c1[2] + (c2[2]-c1[2])*f;
				float a = c1[3] + (c2[3]-c1[3])*f;
				int ri = (int)Math.round(r*255.0f) & 0xFF;
				int gi = (int)Math.round(g*255.0f) & 0xFF;
				int bi = (int)Math.round(b*255.0f) & 0xFF;
				int ai = (int)Math.round(a*255.0f) & 0xFF;
				return ((ai << 24) | (ri << 16) | (gi << 8) | bi);
			}
		} catch (NullPointerException npe) {
			return 0;
		}
	}
	
	public int getRGB(double pos, boolean repeat, boolean reflect, boolean reverse) {
		// repeating a reflected gradient gives an undesirable result,
		// so repeat has to be done BEFORE reflect
		if (repeat) {
			pos -= Math.floor(pos);
		}
		// reflecting a reversed gradient gives a non-unique result,
		// so reflect has to be done BEFORE reverse
		if (reflect) {
			if (pos <= 0.5) pos = pos * 2.0;
			else pos = (1.0 - pos) * 2.0;
		}
		// reversing any gradient always gives the expected result,
		// so reverse can be done last
		if (reverse) {
			pos = 1.0 - pos;
		}
		// the two restrictions above impose this specific
		// topological order on these three transformations
		return getRGB(pos);
	}
	
	public int[] getRGB(double[] pos) {
		// this is no more efficient than the caller doing the same thing,
		// but having this method opens the possibility of a more efficient
		// implementation in the future
		int[] ret = new int[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = getRGB(pos[i]);
		}
		return ret;
	}
	
	public int[] getRGB(double[] pos, boolean repeat, boolean reflect, boolean reverse) {
		// this is no more efficient than the caller doing the same thing,
		// but having this method opens the possibility of a more efficient
		// implementation in the future
		int[] ret = new int[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = getRGB(pos[i], repeat, reflect, reverse);
		}
		return ret;
	}
	
	public Color getColor(double pos) {
		double prevPos = Double.NEGATIVE_INFINITY;
		Color  prevCol = null;
		double nextPos = Double.POSITIVE_INFINITY;
		Color  nextCol = null;
		for (GradientColorStop stop : stops) {
			double thisPos = stop.position;
			Color  thisCol = stop.color.awtColor();
			if (thisPos <= pos && thisPos > prevPos) {
				prevPos = thisPos;
				prevCol = thisCol;
			}
			if (thisPos >= pos && thisPos < nextPos) {
				nextPos = thisPos;
				nextCol = thisCol;
			}
		}
		if (prevCol == null && nextCol == null) return null;
		else if (prevPos == pos || nextCol == null) return prevCol;
		else if (nextPos == pos || prevCol == null) return nextCol;
		else {
			float f = (float)((pos - prevPos) / (nextPos - prevPos));
			float[] c1 = prevCol.getRGBComponents(new float[4]);
			float[] c2 = nextCol.getRGBComponents(new float[4]);
			float r = c1[0] + (c2[0]-c1[0])*f;
			float g = c1[1] + (c2[1]-c1[1])*f;
			float b = c1[2] + (c2[2]-c1[2])*f;
			float a = c1[3] + (c2[3]-c1[3])*f;
			return new Color(r,g,b,a);
		}
	}
	
	public Color getColor(double pos, boolean repeat, boolean reflect, boolean reverse) {
		// repeating a reflected gradient gives an undesirable result,
		// so repeat has to be done BEFORE reflect
		if (repeat) {
			pos -= Math.floor(pos);
		}
		// reflecting a reversed gradient gives a non-unique result,
		// so reflect has to be done BEFORE reverse
		if (reflect) {
			if (pos <= 0.5) pos = pos * 2.0;
			else pos = (1.0 - pos) * 2.0;
		}
		// reversing any gradient always gives the expected result,
		// so reverse can be done last
		if (reverse) {
			pos = 1.0 - pos;
		}
		// the two restrictions above impose this specific
		// topological order on these three transformations
		return getColor(pos);
	}
	
	public Color[] getColors(double[] pos) {
		// this is no more efficient than the caller doing the same thing,
		// but having this method opens the possibility of a more efficient
		// implementation in the future
		Color[] ret = new Color[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = getColor(pos[i]);
		}
		return ret;
	}
	
	public Color[] getColors(double[] pos, boolean repeat, boolean reflect, boolean reverse) {
		// this is no more efficient than the caller doing the same thing,
		// but having this method opens the possibility of a more efficient
		// implementation in the future
		Color[] ret = new Color[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = getColor(pos[i], repeat, reflect, reverse);
		}
		return ret;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that instanceof GradientColorMap) {
			return this.equals((GradientColorMap)that, false);
		} else {
			return false;
		}
	}
	
	public boolean equals(GradientColorMap that, boolean withName) {
		if (!this.stops.equals(that.stops)) return false;
		if (!withName) return true;
		if (this.name != null) return this.name.equals(that.name);
		if (that.name != null) return that.name.equals(this.name);
		return true;
	}
	
	@Override
	public int hashCode() {
		return stops.hashCode();
	}
	
	@Override
	public boolean add(GradientColorStop e) {
		return stops.add(e);
	}
	@Override
	public boolean addAll(Collection<? extends GradientColorStop> c) {
		return stops.addAll(c);
	}
	@Override
	public void clear() {
		stops.clear();
	}
	@Override
	public boolean contains(Object e) {
		return stops.contains(e);
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return stops.containsAll(c);
	}
	@Override
	public boolean isEmpty() {
		return stops.isEmpty();
	}
	@Override
	public Iterator<GradientColorStop> iterator() {
		return stops.iterator();
	}
	@Override
	public boolean remove(Object e) {
		return stops.remove(e);
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return stops.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return stops.retainAll(c);
	}
	@Override
	public int size() {
		return stops.size();
	}
	@Override
	public Object[] toArray() {
		return stops.toArray();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		return stops.toArray(a);
	}
	@Override
	public Comparator<? super GradientColorStop> comparator() {
		return stops.comparator();
	}
	@Override
	public GradientColorStop first() {
		return stops.first();
	}
	@Override
	public SortedSet<GradientColorStop> headSet(GradientColorStop to) {
		return stops.headSet(to);
	}
	@Override
	public GradientColorStop last() {
		return stops.last();
	}
	@Override
	public SortedSet<GradientColorStop> subSet(GradientColorStop from, GradientColorStop to) {
		return stops.subSet(from, to);
	}
	@Override
	public SortedSet<GradientColorStop> tailSet(GradientColorStop from) {
		return stops.tailSet(from);
	}
}
