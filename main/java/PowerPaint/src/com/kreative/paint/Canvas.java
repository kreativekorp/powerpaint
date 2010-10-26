/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import javax.swing.SwingConstants;
import com.kreative.paint.undo.Atom;
import com.kreative.paint.undo.History;
import com.kreative.paint.undo.Recordable;
import com.kreative.paint.util.ImageUtils;
import com.kreative.paint.util.ShapeUtils;

public class Canvas implements List<Layer>, Printable, Paintable, Recordable {
	private static final long serialVersionUID = 1L;
	
	public static final int NORTH = SwingConstants.NORTH;
	public static final int SOUTH = SwingConstants.SOUTH;
	public static final int WEST = SwingConstants.WEST;
	public static final int EAST = SwingConstants.EAST;
	public static final int NORTH_WEST = SwingConstants.NORTH_WEST;
	public static final int NORTH_EAST = SwingConstants.NORTH_EAST;
	public static final int SOUTH_WEST = SwingConstants.SOUTH_WEST;
	public static final int SOUTH_EAST = SwingConstants.SOUTH_EAST;
	public static final int CENTER = SwingConstants.CENTER;
	
	private History history;
	private int width;
	private int height;
	private int dpiX;
	private int dpiY;
	private Area selection;
	private Vector<Layer> layers;
	private int hsX;
	private int hsY;
	
	public Canvas(int width, int height) {
		this(width, height, 72, 72);
	}
	
	public Canvas(int width, int height, int dpi) {
		this(width, height, dpi, dpi);
	}
	
	public Canvas(int width, int height, int dpiX, int dpiY) {
		this.width = width;
		this.height = height;
		this.dpiX = dpiX;
		this.dpiY = dpiY;
		this.layers = new Vector<Layer>();
		this.layers.add(new Layer());
		this.hsX = 0;
		this.hsY = 0;
	}
	
	public Canvas(int width, int height, List<Layer> layers) {
		this(width, height, 72, 72, layers);
	}
	
	public Canvas(int width, int height, int dpi, List<Layer> layers) {
		this(width, height, dpi, dpi, layers);
	}
	
	public Canvas(int width, int height, int dpiX, int dpiY, List<Layer> layers) {
		this.width = width;
		this.height = height;
		this.dpiX = dpiX;
		this.dpiY = dpiY;
		this.layers = new Vector<Layer>();
		this.layers.addAll(layers);
		this.hsX = 0;
		this.hsY = 0;
	}
	
	public History getHistory() {
		return history;
	}
	
	public void setHistory(History history) {
		this.history = history;
		for (Layer l : layers) l.setHistory(history);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Dimension getSize() {
		return new Dimension(width, height);
	}
	
	public int getDPIX() {
		return dpiX;
	}
	
	public int getDPIY() {
		return dpiY;
	}
	
	public Dimension getDPI() {
		return new Dimension(dpiX, dpiY);
	}
	
	public int getHotspotX() {
		return hsX;
	}
	
	public int getHotspotY() {
		return hsY;
	}
	
	public Point getHotspot() {
		return new Point(hsX, hsY);
	}
	
	private static class DimensionAtom implements Atom {
		private Canvas c;
		private int oldw, oldh, oldrx, oldry;
		private int neww, newh, newrx, newry;
		public DimensionAtom(Canvas c, int width, int height, int x, int y) {
			this.c = c;
			this.oldw = c.width;
			this.oldh = c.height;
			this.oldrx = c.dpiX;
			this.oldry = c.dpiY;
			this.neww = width;
			this.newh = height;
			this.newrx = x;
			this.newry = y;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldw = ((DimensionAtom)previousAtom).oldw;
			this.oldh = ((DimensionAtom)previousAtom).oldh;
			this.oldrx = ((DimensionAtom)previousAtom).oldrx;
			this.oldry = ((DimensionAtom)previousAtom).oldry;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof DimensionAtom) && ((DimensionAtom)previousAtom).c == this.c;
		}
		public void redo() {
			c.width = neww;
			c.height = newh;
			c.dpiX = newrx;
			c.dpiY = newry;
		}
		public void undo() {
			c.width = oldw;
			c.height = oldh;
			c.dpiX = oldrx;
			c.dpiY = oldry;
		}
	}
	
	public void setSize(int width, int height) {
		if (this.width == width && this.height == height) return;
		if (history != null) history.add(new DimensionAtom(this, width, height, dpiX, dpiY));
		this.width = width;
		this.height = height;
	}
	
	public void setSize(Dimension d) {
		if (this.width == d.width && this.height == d.height) return;
		if (history != null) history.add(new DimensionAtom(this, d.width, d.height, dpiX, dpiY));
		this.width = d.width;
		this.height = d.height;
	}
	
	public void setSize(int width, int height, int anchor) {
		if (history != null) history.add(new DimensionAtom(this, width, height, dpiX, dpiY));
		int xmargin = width-this.width;
		int ymargin = height-this.height;
		this.width = width;
		this.height = height;
		switch (anchor) {
		case NORTH: case CENTER: case SOUTH:
			for (Layer l : this) {
				l.setX(l.getX() + xmargin/2);
			}
			break;
		case NORTH_EAST: case EAST: case SOUTH_EAST:
			for (Layer l : this) {
				l.setX(l.getX() + xmargin);
			}
			break;
		}
		switch (anchor) {
		case WEST: case CENTER: case EAST:
			for (Layer l : this) {
				l.setY(l.getY() + ymargin/2);
			}
			break;
		case SOUTH_WEST: case SOUTH: case SOUTH_EAST:
			for (Layer l : this) {
				l.setY(l.getY() + ymargin);
			}
			break;
		}
	}
	
	public void setSize(Dimension d, int anchor) {
		if (history != null) history.add(new DimensionAtom(this, d.width, d.height, dpiX, dpiY));
		int xmargin = d.width-this.width;
		int ymargin = d.height-this.height;
		this.width = d.width;
		this.height = d.height;
		switch (anchor) {
		case NORTH: case CENTER: case SOUTH:
			for (Layer l : this) {
				l.setX(l.getX() + xmargin/2);
			}
			break;
		case NORTH_EAST: case EAST: case SOUTH_EAST:
			for (Layer l : this) {
				l.setX(l.getX() + xmargin);
			}
			break;
		}
		switch (anchor) {
		case WEST: case CENTER: case EAST:
			for (Layer l : this) {
				l.setY(l.getY() + ymargin/2);
			}
			break;
		case SOUTH_WEST: case SOUTH: case SOUTH_EAST:
			for (Layer l : this) {
				l.setY(l.getY() + ymargin);
			}
			break;
		}
	}
	
	public void setDPI(int dpi) {
		if (this.dpiX == dpi && this.dpiY == dpi) return;
		if (history != null) history.add(new DimensionAtom(this, width, height, dpi, dpi));
		this.dpiX = dpi;
		this.dpiY = dpi;
	}
	
	public void setDPI(int x, int y) {
		if (this.dpiX == x && this.dpiY == y) return;
		if (history != null) history.add(new DimensionAtom(this, width, height, x, y));
		this.dpiX = x;
		this.dpiY = y;
	}
	
	public void setDPI(Dimension d) {
		if (this.dpiX == d.width && this.dpiY == d.height) return;
		if (history != null) history.add(new DimensionAtom(this, width, height, d.width, d.height));
		this.dpiX = d.width;
		this.dpiY = d.height;
	}
	
	private static class HotspotAtom implements Atom {
		private Canvas c;
		private int oldHSX, oldHSY;
		private int newHSX, newHSY;
		public HotspotAtom(Canvas c, int x, int y) {
			this.c = c;
			this.oldHSX = c.hsX;
			this.oldHSY = c.hsY;
			this.newHSX = x;
			this.newHSY = y;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldHSX = ((HotspotAtom)previousAtom).oldHSX;
			this.oldHSY = ((HotspotAtom)previousAtom).oldHSY;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof HotspotAtom) && ((HotspotAtom)previousAtom).c == this.c;
		}
		public void redo() {
			c.hsX = newHSX;
			c.hsY = newHSY;
		}
		public void undo() {
			c.hsX = oldHSX;
			c.hsY = oldHSY;
		}
	}
	
	public void setHotspotX(int hsX) {
		if (this.hsX == hsX) return;
		if (history != null) history.add(new HotspotAtom(this, hsX, hsY));
		this.hsX = hsX;
	}
	
	public void setHotspotY(int hsY) {
		if (this.hsY == hsY) return;
		if (history != null) history.add(new HotspotAtom(this, hsX, hsY));
		this.hsY = hsY;
	}
	
	public void setHotspot(int hsX, int hsY) {
		if (this.hsX == hsX && this.hsY == hsY) return;
		if (history != null) history.add(new HotspotAtom(this, hsX, hsY));
		this.hsX = hsX;
		this.hsY = hsY;
	}
	
	public void setHotspot(Point p) {
		if (this.hsX == p.x && this.hsY == p.y) return;
		if (history != null) history.add(new HotspotAtom(this, p.x, p.y));
		this.hsX = p.x;
		this.hsY = p.y;
	}
	
	public Layer getPaintDrawLayer() {
		ListIterator<Layer> i = layers.listIterator(layers.size());
		while (i.hasPrevious()) {
			Layer layer = i.previous();
			if (layer.isEditable()) return layer;
		}
		return null;
	}
	
	public void paint(Graphics2D g) {
		for (Layer l : layers) if (l.isViewable()) l.paint(g);
	}
	
	public void paint(Graphics2D g, int tx, int ty) {
		for (Layer l : layers) if (l.isViewable()) l.paint(g, tx, ty);
	}
	
	public int print(Graphics g, PageFormat pf, int i) throws PrinterException {
		int nx = (int)Math.ceil(width / pf.getImageableWidth());
		int ny = (int)Math.ceil(height / pf.getImageableHeight());
		int x = i % nx;
		int y = i / nx;
		if (x >= nx || y >= ny) return NO_SUCH_PAGE;
		else {
			paint(
					(Graphics2D)g,
					(int)(pf.getImageableX() - x*pf.getImageableWidth()),
					(int)(pf.getImageableY() - y*pf.getImageableHeight())
			);
			return PAGE_EXISTS;
		}
	}
	
	public Shape getPaintSelection() {
		return selection;
	}
	
	private static class PaintSelectionAtom implements Atom {
		private Canvas c;
		private Area oldSel;
		private Area newSel;
		public PaintSelectionAtom(Canvas c, Area a) {
			this.c = c;
			this.oldSel = c.selection;
			this.newSel = a;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldSel = ((PaintSelectionAtom)previousAtom).oldSel;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof PaintSelectionAtom) && (((PaintSelectionAtom)previousAtom).c == this.c);
		}
		public void redo() {
			c.selection = newSel;
		}
		public void undo() {
			c.selection = oldSel;
		}
	}
	
	public void setPaintSelection(Shape s) {
		Area a = (ShapeUtils.shapeIsEmpty(s) ? null : (new Area(s)));
		if (selection == a) return;
		if (history != null) history.add(new PaintSelectionAtom(this, a));
		selection = a;
	}
	
	public void addPaintSelection(Shape s) {
		Area a = (selection == null) ? null : (Area)selection.clone();
		Area sa = (ShapeUtils.shapeIsEmpty(s) ? null : (new Area(s)));
		if (sa != null) {
			if (a == null) a = sa;
			else a.add(sa);
		}
		if (selection == a) return;
		if (history != null) history.add(new PaintSelectionAtom(this, a));
		selection = a;
	}
	
	public void subtractPaintSelection(Shape s) {
		Area a = (selection == null) ? null : (Area)selection.clone();
		Area sa = (ShapeUtils.shapeIsEmpty(s) ? null : (new Area(s)));
		if (sa != null && a != null) {
			a.subtract(sa);
		}
		if (selection == a) return;
		if (history != null) history.add(new PaintSelectionAtom(this, a));
		selection = a;
	}
	
	public void xorPaintSelection(Shape s) {
		Area a = (selection == null) ? null : (Area)selection.clone();
		Area sa = (ShapeUtils.shapeIsEmpty(s) ? null : (new Area(s)));
		if (sa != null) {
			if (a == null) a = sa;
			else a.exclusiveOr(sa);
		}
		if (selection == a) return;
		if (history != null) history.add(new PaintSelectionAtom(this, a));
		selection = a;
	}
	
	public boolean isPaintSelectionPopped() {
		for (Layer l : layers) {
			if (l.isImagePopped()) return true;
		}
		return false;
	}
	
	public void updatePaintSelection() {
		for (Layer l : layers) {
			if (selection == null) l.setClip(null);
			else l.setClip(AffineTransform
					.getTranslateInstance(-l.getX(), -l.getY())
					.createTransformedShape(selection));
			if (!l.isEditable()) l.pushImage();
		}
	}
	
	public void clearPaintSelection() {
		setPaintSelection(null);
		for (Layer l : layers) {
			l.setClip(null);
			l.pushImage();
		}
	}
	
	public void pushPaintSelection() {
		for (Layer l : layers) {
			if (selection == null) l.setClip(null);
			else l.setClip(AffineTransform
					.getTranslateInstance(-l.getX(), -l.getY())
					.createTransformedShape(selection));
			l.pushImage();
		}
	}
	
	public void popPaintSelection(boolean all, boolean copy) {
		boolean first = true;
		ListIterator<Layer> i = layers.listIterator(layers.size());
		while (i.hasPrevious()) {
			Layer l = i.previous();
			Shape ls;
			if (selection == null) ls = null;
			else ls = AffineTransform
				.getTranslateInstance(-l.getX(), -l.getY())
				.createTransformedShape(selection);
			l.setClip(ls);
			if (selection == null) l.pushImage();
			else if (!l.isEditable()) l.pushImage();
			else if (all) l.popImage(ls, copy);
			else if (first) { l.popImage(ls, copy); first = false; }
			else l.pushImage();
		}
	}
	
	public void popPaintSelection(Collection<Layer> poppedLayers, boolean copy) {
		for (Layer l : layers) {
			Shape ls;
			if (selection == null) ls = null;
			else ls = AffineTransform
				.getTranslateInstance(-l.getX(), -l.getY())
				.createTransformedShape(selection);
			l.setClip(ls);
			if (selection == null) l.pushImage();
			else if (!l.isEditable()) l.pushImage();
			else if (poppedLayers.contains(l)) l.popImage(ls, copy);
			else l.pushImage();
		}
	}
	
	public Image copyPaintSelection() {
		if (selection == null) {
			return null;
		} else {
			Layer l = getPaintDrawLayer();
			if (l == null) {
				return null;
			} else {
				return l.copyImage(selection);
			}
		}
	}
	
	public void pastePaintSelection(Image img, AffineTransform tx) {
		if (ImageUtils.prepImage(img)) {
			int w, h;
			if (img instanceof BufferedImage) {
				w = ((BufferedImage)img).getWidth();
				h = ((BufferedImage)img).getHeight();
			} else {
				w = img.getWidth(null);
				h = img.getHeight(null);
			}
			Shape s = (tx == null) ? new Rectangle(0, 0, w, h) : tx.createTransformedShape(new Rectangle(0, 0, w, h));
			setPaintSelection(s);
			boolean first = true;
			ListIterator<Layer> i = layers.listIterator(layers.size());
			while (i.hasPrevious()) {
				Layer l = i.previous();
				Shape ls;
				if (s == null) ls = null;
				else ls = AffineTransform
					.getTranslateInstance(-l.getX(), -l.getY())
					.createTransformedShape(s);
				l.setClip(ls);
				if (s == null) l.pushImage();
				else if (!l.isEditable()) l.pushImage();
				else if (first) { tx.translate(-l.getX(), -l.getY()); l.pasteImage(img, tx); first = false; }
				else l.pushImage();
			}
		} else {
			System.err.println("Error: Failed to paste paint selection. Paint selection unaffected.");
		}
	}
	
	public void transformPaintSelection(AffineTransform tx) {
		Shape s = (tx == null || selection == null) ? selection : tx.createTransformedShape(selection);
		setPaintSelection(s);
		for (Layer l : layers) {
			if (s == null) l.setClip(null);
			else l.setClip(AffineTransform
				.getTranslateInstance(-l.getX(), -l.getY())
				.createTransformedShape(s));
			if (l.isImagePopped() && tx != null) {
				l.transformPoppedImage(tx);
			}
		}
	}
	
	public void deletePaintSelection(boolean all) {
		boolean first = true;
		ListIterator<Layer> i = layers.listIterator(layers.size());
		while (i.hasPrevious()) {
			Layer l = i.previous();
			Shape ls;
			if (selection == null) ls = null;
			else ls = AffineTransform
				.getTranslateInstance(-l.getX(), -l.getY())
				.createTransformedShape(selection);
			l.setClip(ls);
			if (selection == null) l.pushImage();
			else if (!l.isEditable()) l.pushImage();
			else if (all) l.deletePoppedImage();
			else if (first) { l.deletePoppedImage(); first = false; }
			else l.pushImage();
		}
	}
	
	public void deletePaintSelection(Collection<Layer> poppedLayers) {
		for (Layer l : layers) {
			Shape ls;
			if (selection == null) ls = null;
			else ls = AffineTransform
				.getTranslateInstance(-l.getX(), -l.getY())
				.createTransformedShape(selection);
			l.setClip(ls);
			if (selection == null) l.pushImage();
			else if (!l.isEditable()) l.pushImage();
			else if (poppedLayers.contains(l)) l.deletePoppedImage();
			else l.pushImage();
		}
	}
	
	private static class AddLayerAtom implements Atom {
		private Canvas c;
		private int index;
		private Layer l;
		public AddLayerAtom(Canvas c, Layer l) {
			this.c = c;
			this.index = -1;
			this.l = l;
		}
		public AddLayerAtom(Canvas c, int index, Layer l) {
			this.c = c;
			this.index = index;
			this.l = l;
		}
		public Atom buildUpon(Atom previousAtom) {
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return false;
		}
		public void redo() {
			if (index < 0) {
				c.layers.add(l);
			} else {
				c.layers.add(index, l);
			}
		}
		public void undo() {
			if (index < 0) {
				c.layers.remove(l);
			} else {
				if (c.layers.get(index) == l) c.layers.remove(index);
				else c.layers.remove(l);
			}
		}
	}

	public boolean add(Layer o) {
		if (history != null) {
			history.add(new AddLayerAtom(this, o));
			o.setHistory(history);
		}
		return layers.add(o);
	}

	public void add(int index, Layer element) {
		if (history != null) {
			history.add(new AddLayerAtom(this, index, element));
			element.setHistory(history);
		}
		layers.add(index, element);
	}
	
	private static class ChangeLayersAtom implements Atom {
		private Canvas c;
		private Vector<Layer> oldLayers;
		private Vector<Layer> newLayers;
		public ChangeLayersAtom(Canvas c, List<Layer> oldl, List<Layer> newl) {
			this.c = c;
			this.oldLayers = new Vector<Layer>();
			this.oldLayers.addAll(oldl);
			this.newLayers = new Vector<Layer>();
			this.newLayers.addAll(newl);
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldLayers = ((ChangeLayersAtom)previousAtom).oldLayers;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof ChangeLayersAtom) && ((ChangeLayersAtom)previousAtom).c == this.c;
		}
		public void redo() {
			c.layers.clear();
			c.layers.addAll(newLayers);
		}
		public void undo() {
			c.layers.clear();
			c.layers.addAll(oldLayers);
		}
	}
	
	private Vector<Layer> layersChangTmp;
	
	private void layersChanging() {
		if (history != null) {
			layersChangTmp = new Vector<Layer>();
			layersChangTmp.addAll(layers);
		}
	}
	
	private void layersChanged() {
		if (history != null) {
			history.add(new ChangeLayersAtom(this, layersChangTmp, this.layers));
			for (Layer d : this.layers) d.setHistory(history);
			layersChangTmp = null;
		}
	}

	public boolean addAll(Collection<? extends Layer> c) {
		layersChanging();
		boolean ret = layers.addAll(c);
		layersChanged();
		return ret;
	}

	public boolean addAll(int index, Collection<? extends Layer> c) {
		layersChanging();
		boolean ret = layers.addAll(index, c);
		layersChanged();
		return ret;
	}

	public void clear() {
		layersChanging();
		layers.clear();
		layersChanged();
	}

	public boolean contains(Object o) {
		return layers.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return layers.containsAll(c);
	}

	public Layer get(int index) {
		return layers.get(index);
	}

	public int indexOf(Object o) {
		return layers.indexOf(o);
	}

	public boolean isEmpty() {
		return layers.isEmpty();
	}

	public Iterator<Layer> iterator() {
		return layers.iterator();
	}

	public int lastIndexOf(Object o) {
		return layers.lastIndexOf(o);
	}

	public ListIterator<Layer> listIterator() {
		return layers.listIterator();
	}

	public ListIterator<Layer> listIterator(int index) {
		return layers.listIterator(index);
	}
	
	private static class RemoveLayerAtom implements Atom {
		private Canvas c;
		private int index;
		private Object o;
		public RemoveLayerAtom(Canvas c, int index, Object o) {
			this.c = c;
			this.index = index;
			this.o = o;
		}
		public Atom buildUpon(Atom previousAtom) {
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return false;
		}
		public void redo() {
			if (c.layers.get(index) == o) c.layers.remove(index);
			else c.layers.remove(o);
		}
		public void undo() {
			c.layers.add(index, (Layer)o);
		}
	}

	public boolean remove(Object o) {
		if (history != null) history.add(new RemoveLayerAtom(this, layers.indexOf(o), o));
		return layers.remove(o);
	}

	public Layer remove(int index) {
		if (history != null) history.add(new RemoveLayerAtom(this, index, layers.get(index)));
		return layers.remove(index);
	}

	public boolean removeAll(Collection<?> c) {
		layersChanging();
		boolean ret = layers.removeAll(c);
		layersChanged();
		return ret;
	}

	public boolean retainAll(Collection<?> c) {
		layersChanging();
		boolean ret = layers.retainAll(c);
		layersChanged();
		return ret;
	}
	
	private static class SetLayerAtom implements Atom {
		private Canvas c;
		private int index;
		private Layer oldl;
		private Layer newl;
		public SetLayerAtom(Canvas c, int index, Layer l) {
			this.c = c;
			this.index = index;
			this.oldl = c.layers.get(index);
			this.newl = l;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldl = ((SetLayerAtom)previousAtom).oldl;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof SetLayerAtom) && (((SetLayerAtom)previousAtom).c == this.c)
				&& (((SetLayerAtom)previousAtom).index == this.index);
		}
		public void redo() {
			c.layers.set(index, newl);
		}
		public void undo() {
			c.layers.set(index, oldl);
		}
	}

	public Layer set(int index, Layer element) {
		if (history != null) history.add(new SetLayerAtom(this, index, element));
		return layers.set(index, element);
	}

	public int size() {
		return layers.size();
	}

	public List<Layer> subList(int fromIndex, int toIndex) {
		return layers.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return layers.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return layers.toArray(a);
	}
	
	public String toString() {
		return "com.kreative.paint.Canvas["+width+","+height+","+dpiX+","+dpiY+","+layers+"]";
	}
}
