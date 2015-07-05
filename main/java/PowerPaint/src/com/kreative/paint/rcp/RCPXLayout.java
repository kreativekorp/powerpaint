package com.kreative.paint.rcp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class RCPXLayout extends RCPXLayoutOrSwatch {
	private static final Rectangle BOGUS_RECTANGLE = new Rectangle(0, 0, 2, 2);
	private static final Point BOGUS_POINT = new Point(1, 1);
	
	@Override public final boolean isLayout() { return true; }
	@Override public final RCPXLayout asLayout() { return this; }
	@Override public final boolean isSwatch() { return false; }
	@Override public final RCPXSwatch asSwatch() { return null; }
	
	public static class Oriented extends RCPXLayout {
		public final RCPXLayoutOrSwatch horizontal;
		public final RCPXLayoutOrSwatch square;
		public final RCPXLayoutOrSwatch vertical;
		public Oriented(
			RCPXLayoutOrSwatch horizontal,
			RCPXLayoutOrSwatch square,
			RCPXLayoutOrSwatch vertical
		) {
			this.horizontal = (horizontal != null) ? horizontal : new RCPXSwatch.Empty(1);
			this.square     = (square     != null) ? square     : new RCPXSwatch.Empty(1);
			this.vertical   = (vertical   != null) ? vertical   : new RCPXSwatch.Empty(1);
		}
		public RCPXLayoutOrSwatch getLayout(Rectangle r) {
			if (r.width  > (r.height + r.height / 2)) return horizontal;
			if (r.height > (r.width  + r.width  / 2)) return vertical;
			return square;
		}
		@Override
		public int repeatCount() {
			return 1;
		}
		@Override
		public Color awtColor(List<RCPXColor> colors, int ignored, Rectangle r, Point p) {
			return getLayout(r).awtColor(colors, 0, r, p);
		}
		@Override
		public String name(List<RCPXColor> colors, int ignored, Rectangle r, Point p) {
			return getLayout(r).name(colors, 0, r, p);
		}
		@Override
		public void paint(List<RCPXColor> colors, int ignored, Graphics g, Rectangle r, Color currCol) {
			getLayout(r).paint(colors, 0, g, r, currCol);
		}
	}
	
	public static abstract class RowOrColumn extends RCPXLayout implements List<RCPXLayoutOrSwatch> {
		private final List<RCPXLayoutOrSwatch> children;
		private final List<Integer> weights;
		public RowOrColumn() {
			this.children = new ArrayList<RCPXLayoutOrSwatch>();
			this.weights = new ArrayList<Integer>();
		}
		@Override
		public boolean add(RCPXLayoutOrSwatch e) {
			return children.add(e) && weights.add(1);
		}
		public boolean add(RCPXLayoutOrSwatch e, int w) {
			return children.add(e) && weights.add(w);
		}
		public boolean add(int w) {
			return children.add(new RCPXSwatch.Empty(1)) && weights.add(w);
		}
		@Override
		public void add(int i, RCPXLayoutOrSwatch e) {
			children.add(i, e);
			weights.add(i, 1);
		}
		public void add(int i, RCPXLayoutOrSwatch e, int w) {
			children.add(i, e);
			weights.add(i, w);
		}
		public void add(int i, int w) {
			children.add(i, new RCPXSwatch.Empty(1));
			weights.add(i, w);
		}
		@Override
		public boolean addAll(Collection<? extends RCPXLayoutOrSwatch> c) {
			boolean changed = false;
			for (RCPXLayoutOrSwatch e : c) {
				if (add(e)) {
					changed = true;
				}
			}
			return changed;
		}
		@Override
		public boolean addAll(int i, Collection<? extends RCPXLayoutOrSwatch> c) {
			boolean changed = false;
			for (RCPXLayoutOrSwatch e : c) {
				add(i, e);
				changed = true;
				i++;
			}
			return changed;
		}
		@Override
		public void clear() {
			children.clear();
			weights.clear();
		}
		@Override
		public boolean contains(Object o) {
			return children.contains(o);
		}
		@Override
		public boolean containsAll(Collection<?> c) {
			return children.containsAll(c);
		}
		@Override
		public RCPXLayoutOrSwatch get(int i) {
			return children.get(i);
		}
		public int getWeight(int i) {
			return weights.get(i);
		}
		@Override
		public int indexOf(Object o) {
			return children.indexOf(o);
		}
		@Override
		public boolean isEmpty() {
			return children.isEmpty() && weights.isEmpty();
		}
		@Override
		public Iterator<RCPXLayoutOrSwatch> iterator() {
			return Collections.unmodifiableList(children).iterator();
		}
		@Override
		public int lastIndexOf(Object o) {
			return children.lastIndexOf(o);
		}
		@Override
		public ListIterator<RCPXLayoutOrSwatch> listIterator() {
			return Collections.unmodifiableList(children).listIterator();
		}
		@Override
		public ListIterator<RCPXLayoutOrSwatch> listIterator(int i) {
			return Collections.unmodifiableList(children).listIterator(i);
		}
		@Override
		public boolean remove(Object o) {
			int i = children.indexOf(o);
			if (i < 0) return false;
			children.remove(i);
			weights.remove(i);
			return true;
		}
		@Override
		public RCPXLayoutOrSwatch remove(int i) {
			RCPXLayoutOrSwatch r = children.remove(i);
			weights.remove(i);
			return r;
		}
		@Override
		public boolean removeAll(Collection<?> c) {
			boolean changed = false;
			for (Object o : c) {
				if (remove(o)) {
					changed = true;
				}
			}
			return changed;
		}
		@Override
		public boolean retainAll(Collection<?> c) {
			boolean changed = false;
			for (int i = children.size() - 1; i >= 0; i--) {
				if (!c.contains(children.get(i))) {
					remove(i);
					changed = true;
				}
			}
			return changed;
		}
		@Override
		public RCPXLayoutOrSwatch set(int i, RCPXLayoutOrSwatch e) {
			return children.set(i, e);
		}
		public RCPXLayoutOrSwatch set(int i, RCPXLayoutOrSwatch e, int w) {
			RCPXLayoutOrSwatch r = children.set(i, e);
			weights.set(i, w);
			return r;
		}
		public int set(int i, int w) {
			return weights.set(i, w);
		}
		@Override
		public int size() {
			return Math.min(children.size(), weights.size());
		}
		@Override
		public List<RCPXLayoutOrSwatch> subList(int fromIndex, int toIndex) {
			return Collections.unmodifiableList(children).subList(fromIndex, toIndex);
		}
		@Override
		public Object[] toArray() {
			return children.toArray();
		}
		@Override
		public <T> T[] toArray(T[] a) {
			return children.toArray(a);
		}
		private int calculateTotalWeight() {
			int totalWeight = 0;
			for (int i = 0, n = children.size(); i < n; i++) {
				RCPXLayoutOrSwatch child = children.get(i);
				int repeatCount = child.repeatCount();
				int weight = weights.get(i);
				totalWeight += repeatCount * weight;
			}
			return totalWeight;
		}
		public abstract Rectangle getRectangle(Rectangle r, int i, int j, int max);
		@Override
		public int repeatCount() {
			return 1;
		}
		@Override
		public Color awtColor(List<RCPXColor> colors, int ignored, Rectangle r, Point p) {
			int totalWeight = calculateTotalWeight();
			int currentWeight = 0;
			for (int i = 0, n = children.size(); i < n; i++) {
				RCPXLayoutOrSwatch child = children.get(i);
				int repeatCount = child.repeatCount();
				int weight = weights.get(i);
				for (int j = 0; j < repeatCount; j++) {
					int nextWeight = currentWeight + weight;
					Rectangle rect = getRectangle(r, currentWeight, nextWeight, totalWeight);
					if (rect.contains(p)) return child.awtColor(colors, j, rect, p);
					currentWeight = nextWeight;
				}
			}
			return null;
		}
		@Override
		public String name(List<RCPXColor> colors, int ignored, Rectangle r, Point p) {
			int totalWeight = calculateTotalWeight();
			int currentWeight = 0;
			for (int i = 0, n = children.size(); i < n; i++) {
				RCPXLayoutOrSwatch child = children.get(i);
				int repeatCount = child.repeatCount();
				int weight = weights.get(i);
				for (int j = 0; j < repeatCount; j++) {
					int nextWeight = currentWeight + weight;
					Rectangle rect = getRectangle(r, currentWeight, nextWeight, totalWeight);
					if (rect.contains(p)) return child.name(colors, j, rect, p);
					currentWeight = nextWeight;
				}
			}
			return null;
		}
		@Override
		public void paint(List<RCPXColor> colors, int ignored, Graphics g, Rectangle r, Color currCol) {
			int totalWeight = calculateTotalWeight();
			int currentWeight = 0;
			for (int i = 0, n = children.size(); i < n; i++) {
				RCPXLayoutOrSwatch child = children.get(i);
				int repeatCount = child.repeatCount();
				int weight = weights.get(i);
				for (int j = 0; j < repeatCount; j++) {
					int nextWeight = currentWeight + weight;
					Rectangle rect = getRectangle(r, currentWeight, nextWeight, totalWeight);
					child.paint(colors, j, g, rect, currCol);
					currentWeight = nextWeight;
				}
			}
		}
	}
	
	public static class Row extends RowOrColumn {
		@Override
		public Rectangle getRectangle(Rectangle r, int i, int j, int max) {
			int x1 = r.x + (int)Math.round((float)(r.width - 1) * (float)i / (float)max);
			int x2 = r.x + (int)Math.round((float)(r.width - 1) * (float)j / (float)max) + 1;
			return new Rectangle(x1, r.y, x2-x1, r.height);
		}
	}
	
	public static class Column extends RowOrColumn {
		@Override
		public Rectangle getRectangle(Rectangle r, int i, int j, int max) {
			int y1 = r.y + (int)Math.round((float)(r.height - 1) * (float)i / (float)max);
			int y2 = r.y + (int)Math.round((float)(r.height - 1) * (float)j / (float)max) + 1;
			return new Rectangle(r.x, y1, r.width, y2-y1);
		}
	}
	
	public static class Diagonal extends RCPXLayout implements List<RCPXSwatch> {
		public final int cols;
		public final int rows;
		public final boolean square;
		private final List<RCPXSwatch> children;
		private transient RCPXLayoutOrSwatch[] swatchCache;
		private transient int[] repeatIndexCache;
		public Diagonal(int cols, int rows, boolean square) {
			this.cols = cols;
			this.rows = rows;
			this.square = square;
			this.children = new ArrayList<RCPXSwatch>();
			this.swatchCache = null;
			this.repeatIndexCache = null;
		}
		private void createCache() {
			if (swatchCache == null || repeatIndexCache == null) {
				int totalCount = 0;
				for (RCPXLayoutOrSwatch child : children) {
					totalCount += child.repeatCount();
				}
				swatchCache = new RCPXLayoutOrSwatch[totalCount];
				repeatIndexCache = new int[totalCount];
				for (int p = 0, i = 0, n = children.size(); i < n; i++) {
					RCPXLayoutOrSwatch child = children.get(i);
					int repeatCount = child.repeatCount();
					for (int j = 0; j < repeatCount; j++, p++) {
						swatchCache[p] = child;
						repeatIndexCache[p] = j;
					}
				}
			}
		}
		private void clearCache() {
			swatchCache = null;
			repeatIndexCache = null;
		}
		@Override
		public boolean add(RCPXSwatch e) {
			clearCache();
			return children.add(e);
		}
		@Override
		public void add(int i, RCPXSwatch e) {
			clearCache();
			children.add(i, e);
		}
		@Override
		public boolean addAll(Collection<? extends RCPXSwatch> c) {
			clearCache();
			return children.addAll(c);
		}
		@Override
		public boolean addAll(int i, Collection<? extends RCPXSwatch> c) {
			clearCache();
			return children.addAll(i, c);
		}
		@Override
		public void clear() {
			clearCache();
			children.clear();
		}
		@Override
		public boolean contains(Object e) {
			return children.contains(e);
		}
		@Override
		public boolean containsAll(Collection<?> c) {
			return children.containsAll(c);
		}
		@Override
		public RCPXSwatch get(int i) {
			return children.get(i);
		}
		@Override
		public int indexOf(Object e) {
			return children.indexOf(e);
		}
		@Override
		public boolean isEmpty() {
			return children.isEmpty();
		}
		@Override
		public Iterator<RCPXSwatch> iterator() {
			return Collections.unmodifiableList(children).iterator();
		}
		@Override
		public int lastIndexOf(Object e) {
			return children.lastIndexOf(e);
		}
		@Override
		public ListIterator<RCPXSwatch> listIterator() {
			return Collections.unmodifiableList(children).listIterator();
		}
		@Override
		public ListIterator<RCPXSwatch> listIterator(int i) {
			return Collections.unmodifiableList(children).listIterator(i);
		}
		@Override
		public boolean remove(Object e) {
			clearCache();
			return children.remove(e);
		}
		@Override
		public RCPXSwatch remove(int i) {
			clearCache();
			return children.remove(i);
		}
		@Override
		public boolean removeAll(Collection<?> c) {
			clearCache();
			return children.removeAll(c);
		}
		@Override
		public boolean retainAll(Collection<?> c) {
			clearCache();
			return children.retainAll(c);
		}
		@Override
		public RCPXSwatch set(int i, RCPXSwatch e) {
			clearCache();
			return children.set(i, e);
		}
		@Override
		public int size() {
			return children.size();
		}
		@Override
		public List<RCPXSwatch> subList(int fromIndex, int toIndex) {
			return Collections.unmodifiableList(children).subList(fromIndex, toIndex);
		}
		@Override
		public Object[] toArray() {
			return children.toArray();
		}
		@Override
		public <T> T[] toArray(T[] a) {
			return children.toArray(a);
		}
		@Override
		public int repeatCount() {
			return 1;
		}
		@Override
		public Color awtColor(List<RCPXColor> colors, int ignored, Rectangle r, Point p) {
			createCache();
			Object[] whr = calculateWHR(r);
			float w = ((Number)whr[0]).floatValue();
			float h = ((Number)whr[1]).floatValue();
			r = ((Rectangle)whr[2]);
			if (!r.contains(p)) return null;
			int i = calculateIndex(r, p, w, h);
			if (i < 0 || i >= swatchCache.length) return null;
			return swatchCache[i].awtColor(colors, repeatIndexCache[i], BOGUS_RECTANGLE, BOGUS_POINT);
		}
		@Override
		public String name(List<RCPXColor> colors, int ignored, Rectangle r, Point p) {
			createCache();
			Object[] whr = calculateWHR(r);
			float w = ((Number)whr[0]).floatValue();
			float h = ((Number)whr[1]).floatValue();
			r = ((Rectangle)whr[2]);
			if (!r.contains(p)) return null;
			int i = calculateIndex(r, p, w, h);
			if (i < 0 || i >= swatchCache.length) return null;
			return swatchCache[i].name(colors, repeatIndexCache[i], BOGUS_RECTANGLE, BOGUS_POINT);
		}
		@Override
		public void paint(List<RCPXColor> colors, int ignored, Graphics g, Rectangle r, Color currCol) {
			createCache();
			Object[] whr = calculateWHR(r);
			float w = ((Number)whr[0]).floatValue();
			float h = ((Number)whr[1]).floatValue();
			r = ((Rectangle)whr[2]);
			for (int y = 0; y <= rows; y++) {
				for (int x = 0; x <= cols; x++) {
					int cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
					int ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
					int cx = (int)Math.floor(w*x);
					int cy = (int)Math.floor(h*y);
					Rectangle cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
					int i1 = ((cols+3)/2)*(y+((x+y)%2))+(x/2);
					int i2 = ((cols+3)/2)*(y+((x+y+1)%2))+((x+1)/2);
					boolean tlbr = (((x+y)%2)==1);
					paintDiagonal(colors, g, cr, currCol, tlbr, i1, i2);
				}
			}
		}
		private Object[] calculateWHR(Rectangle r) {
			if (square) {
				float w = (float)Math.floor((float)(r.width-1) / (float)(cols+1));
				float h = (float)Math.floor((float)(r.height-1) / (float)(rows+1));
				if (w < h) h = w; else if (h < w) w = h;
				r = new Rectangle(
					r.x + (r.width-(int)(w*(cols+1)+1))/2,
					r.y + (r.height-(int)(h*(rows+1)+1))/2,
					(int)(w*(cols+1)+1), (int)(h*(rows+1)+1)
				);
				return new Object[]{w, h, r};
			} else {
				float w = (float)(r.width-1) / (float)(cols+1);
				float h = (float)(r.height-1) / (float)(rows+1);
				return new Object[]{w, h, r};
			}
		}
		private int calculateIndex(Rectangle r, Point p, float w, float h) {
			int x = (int)Math.floor((float)(cols+1)*(float)(p.x-r.x)/(float)r.width);
			int y = (int)Math.floor((float)(rows+1)*(float)(p.y-r.y)/(float)r.height);
			if (x < 0) x = 0; else if (x > cols) x = cols;
			if (y < 0) y = 0; else if (y > rows) y = rows;
			int cw = (int)Math.floor(w*(x+1)) - (int)Math.floor(w*x);
			int ch = (int)Math.floor(h*(y+1)) - (int)Math.floor(h*y);
			int cx = (int)Math.floor(w*x);
			int cy = (int)Math.floor(h*y);
			Rectangle cr = new Rectangle(r.x+cx, r.y+cy, cw+1, ch+1);
			int i1 = ((cols+3)/2)*(y+((x+y)%2))+(x/2);
			int i2 = ((cols+3)/2)*(y+((x+y+1)%2))+((x+1)/2);
			boolean tlbr = (((x+y)%2)==1);
			float mxf = (float)(p.x-cr.x)/(float)(cr.width);
			float myf = (float)(p.y-cr.y)/(float)(cr.height);
			return ((tlbr ? (mxf >= myf) : (mxf+myf >= 1.0f)) ? i2 : i1);
		}
		private void paintDiagonal(List<RCPXColor> colors, Graphics g, Rectangle r, Color currCol, boolean tlbr, int i1, int i2) {
			int[] xs1, ys1, xs2, ys2;
			int l1x1, l1y1, l1x2, l1y2;
			int l2x1, l2y1, l2x2, l2y2;
			int lx1, ly1, lx2, ly2;
			if (tlbr) {
				xs1 = new int[]{r.x, r.x, r.x+r.width-1};
				ys1 = new int[]{r.y, r.y+r.height-1, r.y+r.height-1};
				xs2 = new int[]{r.x, r.x+r.width-1, r.x+r.width-1};
				ys2 = new int[]{r.y, r.y, r.y+r.height-1};
				l1x1 = r.x; l1y1 = r.y+1; l1x2 = r.x+r.width-2; l1y2 = r.y+r.height-1;
				l2x1 = r.x+1; l2y1 = r.y; l2x2 = r.x+r.width-1; l2y2 = r.y+r.height-2;
				lx1 = r.x; ly1 = r.y; lx2 = r.x+r.width-1; ly2 = r.y+r.height-1;
			} else {
				xs1 = new int[]{r.x, r.x, r.x+r.width-1};
				ys1 = new int[]{r.y+r.height-1, r.y, r.y};
				xs2 = new int[]{r.x, r.x+r.width-1, r.x+r.width-1};
				ys2 = new int[]{r.y+r.height-1, r.y+r.height-1, r.y};
				l1x1 = r.x+r.width-2; l1y1 = r.y; l1x2 = r.x; l1y2 = r.y+r.height-2;
				l2x1 = r.x+r.width-1; l2y1 = r.y+1; l2x2 = r.x+1; l2y2 = r.y+r.height-1;
				lx1 = r.x+r.width-1; ly1 = r.y; lx2 = r.x; ly2 = r.y+r.height-1;
			}
			Color c1 = null;
			Color c2 = null;
			if (i1 >= 0 && i1 < swatchCache.length) {
				c1 = swatchCache[i1].awtColor(colors, repeatIndexCache[i1], BOGUS_RECTANGLE, BOGUS_POINT);
				if (c1 != null) {
					g.setColor(c1);
					g.fillPolygon(xs1, ys1, 3);
					if (c1.equals(currCol)) {
						g.setColor(RCPXBorder.contrastingColor(c1));
						g.drawLine(l1x1, l1y1, l1x2, l1y2);
					}
				}
			}
			if (i2 >= 0 && i2 < swatchCache.length) {
				c2 = swatchCache[i2].awtColor(colors, repeatIndexCache[i2], BOGUS_RECTANGLE, BOGUS_POINT);
				if (c2 != null) {
					g.setColor(c2);
					g.fillPolygon(xs2, ys2, 3);
					if (c2.equals(currCol)) {
						g.setColor(RCPXBorder.contrastingColor(c2));
						g.drawLine(l2x1, l2y1, l2x2, l2y2);
					}
				}
			}
			if (c1 != null || c2 != null) {
				g.setColor(Color.black);
				g.drawLine(lx1, ly1, lx2, ly2);
			}
		}
	}
}
