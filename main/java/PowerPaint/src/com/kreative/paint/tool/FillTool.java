/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.kreative.paint.Layer;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.document.tile.PaintSurface;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.util.Bitmap;
import com.kreative.paint.util.CursorUtils;

public class FillTool extends AbstractPaintDrawTool {
	private static final int K = 0xFF000000;
	// A bit of history: When I was little, I thought this icon/cursor
	// was a graduation cap. It actually wasn't until I read the
	// HyperTalk Script Language Guide and learned the same tool in
	// HyperCard was called the "bucket" tool that I got it.
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,0,K,0,K,0,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,K,0,K,K,0,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,K,K,K,0,0,
					0,0,0,K,0,0,0,0,K,0,0,0,K,K,K,0,
					0,0,K,0,0,0,0,K,0,K,0,0,0,K,K,K,
					0,K,0,0,0,0,0,0,K,0,0,0,0,K,K,K,
					K,0,0,0,0,0,0,0,0,0,0,0,K,K,K,K,
					K,0,0,0,0,0,0,0,0,0,0,K,0,K,K,K,
					0,K,0,0,0,0,0,0,0,0,K,0,0,K,K,K,
					0,0,K,0,0,0,0,0,0,K,0,0,0,K,K,K,
					0,0,0,K,0,0,0,0,K,0,0,0,0,K,K,K,
					0,0,0,0,K,0,0,K,0,0,0,0,0,K,K,0,
					0,0,0,0,0,K,K,0,0,0,0,0,0,K,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		boolean all = e.isCtrlDown();
		Layer layer = e.getLayer();
		if (layer == null) {
			return false;
		} else if (e.isInDrawMode()) {
			e.beginTransaction(getName());
			Point2D.Float lp = new Point2D.Float(e.getCanvasX()-layer.getX(), e.getCanvasY()-layer.getY());
			PaintSettings ps = e.getPaintSettings();
			for (int j = layer.size()-1; j >= 0; j--) {
				DrawObject obj = layer.get(j);
				if (all || obj.contains(lp)) {
					if (!obj.isLocked()) {
						if (e.isAltDown()) {
							if (e.isShiftDown()) {
								obj.setDrawComposite(ps.getDrawComposite());
								obj.setDrawPaint(ps.getDrawPaint());
								obj.setFillComposite(ps.getFillComposite());
								obj.setFillPaint(ps.getFillPaint());
								obj.setStroke(ps.getStroke());
							} else {
								obj.setDrawComposite(ps.getDrawComposite());
								obj.setDrawPaint(ps.getDrawPaint());
								obj.setStroke(ps.getStroke());
							}
						} else {
							obj.setFillComposite(ps.getFillComposite());
							obj.setFillPaint(ps.getFillPaint());
						}
					}
					if (!all) {
						e.commitTransaction();
						return true;
					}
				}
			}
			e.commitTransaction();
			return true;
		} else {
			e.beginTransaction(getName());
			Rectangle bounds = new Rectangle(-layer.getX(), -layer.getY(), e.getCanvas().getWidth(), e.getCanvas().getHeight());
			int x = (int)Math.floor(e.getCanvasX()-layer.getX());
			int y = (int)Math.floor(e.getCanvasY()-layer.getY());
			Point seedPoint = new Point(x, y);
			Graphics2D g2 = layer.getCachedPaintGraphics();
			e.getPaintSettings().applyFill(g2);
			if (all) {
				allFill(bounds, seedPoint, layer, g2);
			} else {
				seedFill(bounds, seedPoint, layer, g2);
			}
			e.commitTransaction();
			return true;
		}
	}
	
	public Cursor getCursor(ToolEvent e) {
		return CursorUtils.CURSOR_BUCKET;
	}
	
	private static boolean colorCmp(int c, int targetcolor) {
		// in the future this could be refactored to support "tolerance"
		return c == targetcolor;
	}
	
	private static void allFill(Rectangle bounds, Point seedPoint, PaintSurface p, Graphics2D g2) {
		int targetcolor = p.getRGB(seedPoint.x, seedPoint.y);
		int[] rgb = new int[bounds.width*bounds.height];
		p.getRGB(bounds.x, bounds.y, bounds.width, bounds.height, rgb, 0, bounds.width);
		int[] prgb = new int[bounds.width*bounds.height];
		int cbx1 = bounds.width, cby1 = bounds.height, cbx2 = -1, cby2 = -1;
		for (int ay = 0, by = 0; ay < rgb.length; ay += bounds.width, by++) {
			for (int ax = 0, bx = 0; ax < bounds.width; ax++, bx++) {
				if (colorCmp(rgb[ay+ax], targetcolor)) {
					prgb[ay+ax] = 0xFF000000;
					if (bx < cbx1) cbx1 = bx;
					if (by < cby1) cby1 = by;
					if (bx > cbx2) cbx2 = bx;
					if (by > cby2) cby2 = by;
				}
			}
		}
		if (cbx2 >= cbx1 && cby2 >= cby1) {
			int cw = cbx2-cbx1+1;
			int ch = cby2-cby1+1;
			int[] nrgb = new int[cw*ch];
			for (
					int ny = 0, py = cby1*bounds.width;
					ny < nrgb.length && py < prgb.length;
					ny += cw, py += bounds.width
			) {
				for (
						int nx = 0, px = cbx1;
						nx < cw && px < bounds.width;
						nx++, px++
				) {
					nrgb[ny+nx] = prgb[py+px];
				}
			}
			new Bitmap(cw, ch, nrgb).paint(g2, bounds.x+cbx1, bounds.y+cby1);
		}
	}
	
	private static void seedFill(Rectangle bounds, Point node, PaintSurface p, Graphics2D g2) {
		int xmin = bounds.x, xmax = bounds.x+bounds.width-1, ymin = bounds.y, ymax = bounds.y+bounds.height-1;
		int bx = bounds.x, by = bounds.y, rc = bounds.width;
		int targetcolor = p.getRGB(node.x, node.y);
		int[] rgb = new int[bounds.width*bounds.height];
		p.getRGB(bounds.x, bounds.y, bounds.width, bounds.height, rgb, 0, bounds.width);
		int[] prgb = new int[bounds.width*bounds.height];
		int cbx1 = bounds.width, cby1 = bounds.height, cbx2 = -1, cby2 = -1;
		List<Long> Q = new LinkedList<Long>();
		Set<Long> V = new LongBitSet();
		Q.add(((node.x & 0xFFFFFFFFL) << 32L) | (node.y & 0xFFFFFFFFL));
		while (!Q.isEmpty()) {
			long n = Q.remove(0);
			if (!V.contains(n)) {
				int nx = (int)(n >> 32L);
				int ny = (int)(n);
				if (colorCmp(rgb[(ny-by)*rc + (nx-bx)], targetcolor)) {
					int w = nx;
					int e = nx;
					int y = ny;
					while (w > xmin && colorCmp(rgb[(y-by)*rc + ((w-1)-bx)], targetcolor)) w--;
					while (e < xmax && colorCmp(rgb[(y-by)*rc + ((e+1)-bx)], targetcolor)) e++;
					for (int x = w; x <= e; x++) {
						prgb[(y-by)*rc + (x-bx)] = 0xFF000000;
						if (x-bx < cbx1) cbx1 = x-bx;
						if (y-by < cby1) cby1 = y-by;
						if (x-bx > cbx2) cbx2 = x-bx;
						if (y-by > cby2) cby2 = y-by;
						V.add(((x & 0xFFFFFFFFL) << 32L) | (y & 0xFFFFFFFFL));
					}
					if (y > ymin) {
						for (int x = w; x <= e; x++) {
							if (colorCmp(rgb[((y-1)-by)*rc + (x-bx)], targetcolor)) {
								long q = ((x & 0xFFFFFFFFL) << 32L) | ((y-1) & 0xFFFFFFFFL);
								if (!V.contains(q)) Q.add(q);
							}
						}
					}
					if (y < ymax) {
						for (int x = w; x <= e; x++) {
							if (colorCmp(rgb[((y+1)-by)*rc + (x-bx)], targetcolor)) {
								long q = ((x & 0xFFFFFFFFL) << 32L) | ((y+1) & 0xFFFFFFFFL);
								if (!V.contains(q)) Q.add(q);
							}
						}
					}
				}
			}
		}
		if (cbx2 >= cbx1 && cby2 >= cby1) {
			int cw = cbx2-cbx1+1;
			int ch = cby2-cby1+1;
			int[] nrgb = new int[cw*ch];
			for (
					int ny = 0, py = cby1*bounds.width;
					ny < nrgb.length && py < prgb.length;
					ny += cw, py += bounds.width
			) {
				for (
						int nx = 0, px = cbx1;
						nx < cw && px < bounds.width;
						nx++, px++
				) {
					nrgb[ny+nx] = prgb[py+px];
				}
			}
			new Bitmap(cw, ch, nrgb).paint(g2, bounds.x+cbx1, bounds.y+cby1);
		}
	}
	
	private static class LongBitSet implements Set<Long> {
		private Map<Long,BitSet> m = new HashMap<Long,BitSet>();
		public boolean add(Long o) {
			if (m.containsKey(o >> 24)) {
				m.get(o >> 24).set((int)(o & 0xFFFFFF));
			} else {
				BitSet b = new BitSet();
				b.set((int)(o & 0xFFFFFF));
				m.put(o >> 24, b);
			}
			return true;
		}
		public boolean addAll(Collection<? extends Long> c) {
			boolean ret = false;
			for (long l : c) {
				if (add(l)) ret = true;
			}
			return ret;
		}
		public void clear() {
			m.clear();
		}
		public boolean contains(Object o) {
			if (m.containsKey((Long)o >> 24)) {
				return m.get((Long)o >> 24).get((int)((Long)o & 0xFFFFFF));
			} else {
				return false;
			}
		}
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (!contains(o)) return false;
			}
			return true;
		}
		public boolean isEmpty() {
			for (BitSet b : m.values()) {
				if (!b.isEmpty()) return false;
			}
			return true;
		}
		public Iterator<Long> iterator() {
			throw new UnsupportedOperationException("LongBitSet.iterator");
		}
		public boolean remove(Object o) {
			if (m.containsKey((Long)o >> 24)) {
				m.get((Long)o >> 24).clear((int)((Long)o & 0xFFFFFF));
				return true;
			} else {
				return false;
			}
		}
		public boolean removeAll(Collection<?> c) {
			boolean ret = false;
			for (Object o : c) {
				if (remove(o)) ret = true;
			}
			return ret;
		}
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException("LongBitSet.retainAll");
		}
		public int size() {
			int size = 0;
			for (BitSet b : m.values()) {
				size += b.cardinality();
			}
			return size;
		}
		public Object[] toArray() {
			throw new UnsupportedOperationException("LongBitSet.toArray");
		}
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException("LongBitSet.toArray");
		}
	}
}
