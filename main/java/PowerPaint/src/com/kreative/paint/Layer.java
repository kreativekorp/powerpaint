package com.kreative.paint;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.DrawObjectSurface;
import com.kreative.paint.document.draw.DrawSurface;
import com.kreative.paint.document.draw.ImageDrawObject;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.tile.PaintSurface;
import com.kreative.paint.document.tile.Tile;
import com.kreative.paint.document.tile.TileSurface;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.document.undo.Recordable;
import com.kreative.paint.util.ImageUtils;

public class Layer implements PaintSurface, DrawSurface, Paintable, Recordable {
	private History history;
	private String name;
	private boolean visible;
	private boolean locked;
	private boolean selected;
	private int x;
	private int y;
	private Shape clip;
	private TileSurface ts;
	private BufferedImage poppedImage;
	private AffineTransform poppedImageTransform;
	private DrawObjectSurface ds;
	private Graphics2D paintGraphicsCache;
	private Graphics2D drawGraphicsCache;
	
	public Layer() {
		this(8, 0x00FFFFFF);
	}
	
	public Layer(int tileSize, int matte) {
		this.name = "";
		this.visible = true;
		this.locked = false;
		this.selected = true;
		this.x = 0;
		this.y = 0;
		this.clip = null;
		this.ts = new TileSurface(0, 0, tileSize, tileSize, matte);
		this.poppedImage = null;
		this.poppedImageTransform = null;
		this.ds = new DrawObjectSurface(0, 0);
		this.paintGraphicsCache = null;
		this.drawGraphicsCache = null;
	}
	
	public History getHistory() {
		return history;
	}
	
	public void setHistory(History history) {
		this.history = history;
		ts.setHistory(history);
		ds.setHistory(history);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public boolean isViewable() {
		return visible;
	}
	
	public boolean isEditable() {
		return visible && selected && !locked;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Point getLocation() {
		return new Point(x, y);
	}
	
	public Shape getClip() {
		return clip;
	}
	
	public boolean isImagePopped() {
		return poppedImage != null;
	}
	
	public BufferedImage getPoppedImage() {
		return poppedImage;
	}
	
	public AffineTransform getPoppedImageTransform() {
		return poppedImageTransform;
	}
	
	public int getMatte() {
		return ts.getMatte();
	}
	
	private static class NameAtom implements Atom {
		private Layer l;
		private String oldName;
		private String newName;
		public NameAtom(Layer l, String n) {
			this.l = l;
			this.oldName = l.name;
			this.newName = n;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldName = ((NameAtom)previousAtom).oldName;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof NameAtom) && ((NameAtom)previousAtom).l == this.l;
		}
		public void redo() {
			l.name = newName;
		}
		public void undo() {
			l.name = oldName;
		}
	}
	
	public void setName(String name) {
		if (this.name == name) return;
		if (history != null) history.add(new NameAtom(this, name));
		this.name = name;
	}
	
	private static class AttributeAtom implements Atom {
		private Layer l;
		private boolean oldv, oldl, olds;
		private boolean newv, newl, news;
		public AttributeAtom(Layer l, boolean vis, boolean lok, boolean sel) {
			this.l = l;
			this.oldv = l.visible;
			this.oldl = l.locked;
			this.olds = l.selected;
			this.newv = vis;
			this.newl = lok;
			this.news = sel;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldv = ((AttributeAtom)previousAtom).oldv;
			this.oldl = ((AttributeAtom)previousAtom).oldl;
			this.olds = ((AttributeAtom)previousAtom).olds;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof AttributeAtom) && ((AttributeAtom)previousAtom).l == this.l;
		}
		public void redo() {
			l.visible = newv;
			l.locked = newl;
			l.selected = news;
		}
		public void undo() {
			l.visible = oldv;
			l.locked = oldl;
			l.selected = olds;
		}
	}
	
	public void setVisible(boolean visible) {
		if (this.visible == visible) return;
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.visible = visible;
	}
	
	public void setLocked(boolean locked) {
		if (this.locked == locked) return;
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.locked = locked;
	}
	
	public void setSelected(boolean selected) {
		if (this.selected == selected) return;
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.selected = selected;
	}
	
	private static class LocationAtom implements Atom {
		private Layer l;
		private int oldX, oldY;
		private int newX, newY;
		public LocationAtom(Layer l, int x, int y) {
			this.l = l;
			this.oldX = l.x;
			this.oldY = l.y;
			this.newX = x;
			this.newY = y;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldX = ((LocationAtom)previousAtom).oldX;
			this.oldY = ((LocationAtom)previousAtom).oldY;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof LocationAtom) && ((LocationAtom)previousAtom).l == this.l;
		}
		public void redo() {
			l.x = newX;
			l.y = newY;
		}
		public void undo() {
			l.x = oldX;
			l.y = oldY;
		}
	}
	
	public void setX(int x) {
		if (this.x == x) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.x = x;
	}
	
	public void setY(int y) {
		if (this.y == y) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.y = y;
	}
	
	public void setLocation(Point p) {
		if (this.x == p.x && this.y == p.y) return;
		if (history != null) history.add(new LocationAtom(this, p.x, p.y));
		this.x = p.x;
		this.y = p.y;
	}
	
	private static class ClipAtom implements Atom {
		private Layer l;
		private Shape oldClip;
		private Shape newClip;
		public ClipAtom(Layer l, Shape clip) {
			this.l = l;
			this.oldClip = l.clip;
			this.newClip = clip;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldClip = ((ClipAtom)previousAtom).oldClip;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof ClipAtom) && ((ClipAtom)previousAtom).l == this.l;
		}
		public void redo() {
			l.clip = newClip;
		}
		public void undo() {
			l.clip = oldClip;
		}
	}
	
	public void setClip(Shape clip) {
		if (this.clip == clip) return;
		if (history != null) history.add(new ClipAtom(this, clip));
		this.clip = clip;
	}
	
	private static class PoppedImageAtom implements Atom {
		private Layer l;
		private BufferedImage oldPoppedImage;
		private AffineTransform oldPoppedTx;
		private BufferedImage newPoppedImage;
		private AffineTransform newPoppedTx;
		public PoppedImageAtom(Layer l, BufferedImage pimg, AffineTransform tx) {
			this.l = l;
			this.oldPoppedImage = l.poppedImage;
			this.oldPoppedTx = l.poppedImageTransform;
			this.newPoppedImage = pimg;
			this.newPoppedTx = tx;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldPoppedImage = ((PoppedImageAtom)previousAtom).oldPoppedImage;
			this.oldPoppedTx = ((PoppedImageAtom)previousAtom).oldPoppedTx;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof PoppedImageAtom) && ((PoppedImageAtom)previousAtom).l == this.l;
		}
		public void redo() {
			l.poppedImage = newPoppedImage;
			l.poppedImageTransform = newPoppedTx;
		}
		public void undo() {
			l.poppedImage = oldPoppedImage;
			l.poppedImageTransform = oldPoppedTx;
		}
	}
	
	public void setPoppedImage(BufferedImage pimg, AffineTransform ptx) {
		if (this.poppedImage == pimg && this.poppedImageTransform == ptx) return;
		if (history != null) history.add(new PoppedImageAtom(this, pimg, ptx));
		this.poppedImage = pimg;
		this.poppedImageTransform = ptx;
	}
	
	public void popImage(Shape shape, boolean copy) {
		if (poppedImage != null) pushImage();
		Rectangle bounds = shape.getBounds();
		BufferedImage pimg = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		AffineTransform ptx = AffineTransform.getTranslateInstance(bounds.x, bounds.y);
		Graphics2D pg = pimg.createGraphics();
		pg.translate(-bounds.x, -bounds.y);
		pg.setClip(shape);
		ts.paint(pg);
		pg.dispose();
		if (!copy) {
			Shape sc = clip;
			clip = shape;
			Rectangle r = shape.getBounds();
			clear(r.x, r.y, r.width, r.height);
			clip = sc;
		}
		setPoppedImage(pimg, ptx);
	}
	
	private boolean isTranslation(AffineTransform tx) {
		return (tx.getScaleX() == 1.0 && tx.getScaleY() == 1.0 && tx.getShearX() == 0.0 && tx.getShearY() == 0.0);
	}
	
	public Image copyImage(Shape shape) {
		if (poppedImage != null) {
			if (poppedImageTransform == null || isTranslation(poppedImageTransform)) {
				return poppedImage;
			} else {
				Shape bounds = poppedImageTransform.createTransformedShape(new Rectangle(0, 0, poppedImage.getWidth(), poppedImage.getHeight()));
				Rectangle boundsBounds = bounds.getBounds();
				BufferedImage img = new BufferedImage(boundsBounds.width, boundsBounds.height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = img.createGraphics();
				AffineTransformOp op = new AffineTransformOp(poppedImageTransform, AffineTransformOp.TYPE_BICUBIC);
				g.drawImage(poppedImage, op, -boundsBounds.x, -boundsBounds.y);
				g.dispose();
				return img;
			}
		} else {
			Rectangle bounds = shape.getBounds();
			BufferedImage pimg = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D pg = pimg.createGraphics();
			pg.translate(-bounds.x, -bounds.y);
			pg.setClip(shape);
			ts.paint(pg);
			pg.dispose();
			return pimg;
		}
	}
	
	public ImageDrawObject copyImageObject(Shape shape) {
		PaintSettings ps = new PaintSettings(null, null);
		if (poppedImage != null) {
			if (poppedImageTransform == null) {
				return ImageDrawObject.forGraphicsDrawImage(ps, (Image)poppedImage, 0, 0);
			} else {
				return ImageDrawObject.forGraphicsDrawImage(ps, (Image)poppedImage, poppedImageTransform);
			}
		} else {
			Rectangle bounds = shape.getBounds();
			BufferedImage pimg = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D pg = pimg.createGraphics();
			pg.translate(-bounds.x, -bounds.y);
			pg.setClip(shape);
			ts.paint(pg);
			pg.dispose();
			return ImageDrawObject.forGraphicsDrawImage(ps, (Image)pimg, AffineTransform.getTranslateInstance(bounds.x, bounds.y));
		}
	}
	
	public void pasteImage(Image img, AffineTransform tx) {
		if (ImageUtils.prepImage(img)) {
			if (poppedImage != null) pushImage();
			BufferedImage pimg = ImageUtils.toBufferedImage(img, true);
			AffineTransform ptx = AffineTransform.getTranslateInstance(0, 0);
			ptx.concatenate(tx);
			setPoppedImage(pimg, ptx);
		} else {
			System.err.println("Error: Failed to paste image. Selection unaffected.");
		}
	}
	
	public void pushImage() {
		if (poppedImage != null) {
			AffineTransformOp op = (poppedImageTransform == null) ? null : new AffineTransformOp(poppedImageTransform, AffineTransformOp.TYPE_BICUBIC);
			Graphics2D g = getCachedPaintGraphics();
			g.setClip(null);
			g.setPaint(new Color(ts.getMatte()));
			g.setComposite(AlphaComposite.SrcOver);
			g.drawImage(poppedImage, op, 0, 0);
			setPoppedImage(null, null);
		}
	}
	
	public void transformPoppedImage(AffineTransform tx) {
		if (poppedImage != null) {
			AffineTransform ntx = AffineTransform.getTranslateInstance(0, 0);
			if (poppedImageTransform != null) ntx.concatenate(poppedImageTransform);
			ntx.concatenate(tx);
			setPoppedImage(poppedImage, ntx);
		}
	}
	
	public void deletePoppedImage() {
		if (poppedImage != null) {
			setPoppedImage(null, null);
		} else if (clip != null) {
			Rectangle r = clip.getBounds();
			clear(r.x, r.y, r.width, r.height);
		}
	}
	
	public int getTileSize() {
		return ts.getTileWidth();
	}
	
	public void paint(Graphics2D g) {
		ts.paint(g, x, y);
		if (poppedImage != null) {
			AffineTransform tr = AffineTransform.getTranslateInstance(0, 0);
			tr.translate(x, y);
			if (poppedImageTransform != null) tr.concatenate(poppedImageTransform);
			while (!g.drawImage(poppedImage, tr, null));
		}
		ds.paint(g, x, y);
	}
	
	public void paint(Graphics2D g, int tx, int ty) {
		ts.paint(g, x + tx, y + ty);
		if (poppedImage != null) {
			AffineTransform tr = AffineTransform.getTranslateInstance(0, 0);
			tr.translate(x+tx, y+ty);
			if (poppedImageTransform != null) tr.concatenate(poppedImageTransform);
			while (!g.drawImage(poppedImage, tr, null));
		}
		ds.paint(g, x + tx, y + ty);
	}
	
	public Graphics2D createPaintGraphics() {
		Graphics2D g = ts.createPaintGraphics();
		g.setClip(clip);
		return g;
	}
	
	public Graphics2D createDrawGraphics() {
		return ds.createDrawGraphics();
	}
	
	public Graphics2D getCachedPaintGraphics() {
		if (paintGraphicsCache == null)
			paintGraphicsCache = ts.createPaintGraphics();
		paintGraphicsCache.setClip(clip);
		return paintGraphicsCache;
	}
	
	public Graphics2D getCachedDrawGraphics() {
		if (drawGraphicsCache == null)
			drawGraphicsCache = ds.createDrawGraphics();
		return drawGraphicsCache;
	}
	
	public void flushCache() {
		if (paintGraphicsCache != null)
			paintGraphicsCache.dispose();
		paintGraphicsCache = null;
		if (drawGraphicsCache != null)
			drawGraphicsCache.dispose();
		drawGraphicsCache = null;
	}
	
	public int getMinX() {
		return Integer.MIN_VALUE;
	}
	
	public int getMinY() {
		return Integer.MIN_VALUE;
	}
	
	public int getMaxX() {
		return Integer.MAX_VALUE;
	}
	
	public int getMaxY() {
		return Integer.MAX_VALUE;
	}
	
	public boolean contains(int x, int y) {
		return true;
	}
	
	public boolean contains(int x, int y, int width, int height) {
		return true;
	}
	
	public int getRGB(int x, int y) {
		return ts.getRGB(x, y);
	}
	
	public int[] getRGB(int bx, int by, int width, int height, int[] rgb, int offset, int rowCount) {
		return ts.getRGB(bx, by, width, height, rgb, offset, rowCount);
	}
	
	public void setRGB(int x, int y, int rgb) {
		ts.setRGB(x, y, rgb, clip);
	}
	
	public void setRGB(int x, int y, int rgb, Shape clip) {
		Area a = new Area(this.clip);
		a.intersect(new Area(clip));
		ts.setRGB(x, y, rgb, a);
	}
	
	public void setRGB(int bx, int by, int width, int height, int[] rgb, int offset, int rowCount) {
		ts.setRGB(bx, by, width, height, rgb, offset, rowCount, clip);
	}
	
	public void setRGB(int bx, int by, int width, int height, int[] rgb, int offset, int rowCount, Shape clip) {
		Area a = new Area(this.clip);
		a.intersect(new Area(clip));
		ts.setRGB(bx, by, width, height, rgb, offset, rowCount, a);
	}
	
	public void clear(int x, int y, int width, int height) {
		ts.clear(x, y, width, height, clip);
	}
	
	public void clear(int x, int y, int width, int height, Shape clip) {
		Area a = new Area(this.clip);
		a.intersect(new Area(clip));
		ts.clear(x, y, width, height, a);
	}
	
	public void clearAll() {
		if (clip != null) {
			Rectangle r = clip.getBounds();
			ts.clear(r.x, r.y, r.width, r.height, clip);
		} else {
			ts.clearAll();
		}
	}
	
	public Tile getTile(int x, int y, boolean create) {
		return ts.getTile(x, y, create);
	}
	
	public void addTile(Tile t) {
		ts.addTile(t);
	}
	
	public Collection<Tile> getTiles(int bx, int by, int width, int height, boolean create) {
		return ts.getTiles(bx, by, width, height, create);
	}
	
	public Collection<Tile> getTiles() {
		return ts.getTiles();
	}
	
	public boolean hasSelection() { return ds.hasSelection(); }
	public int getFirstSelectedIndex() { return ds.getFirstSelectedIndex(); }
	public int getLastSelectedIndex() { return ds.getLastSelectedIndex(); }
	public List<DrawObject> getSelection() { return ds.getSelection(); }
	
	public boolean add(DrawObject o) { return ds.add(o); }
	public void add(int index, DrawObject o) { ds.add(index, o); }
	public boolean addAll(Collection<? extends DrawObject> c) { return ds.addAll(c); }
	public boolean addAll(int index, Collection<? extends DrawObject> c) { return ds.addAll(index, c); }
	public void clear() { ds.clear(); }
	public boolean contains(Object o) { return ds.contains(o); }
	public boolean containsAll(Collection<?> c) { return ds.containsAll(c); }
	public DrawObject get(int index) { return ds.get(index); }
	public int indexOf(Object o) { return ds.indexOf(o); }
	public boolean isEmpty() { return ds.isEmpty(); }
	public Iterator<DrawObject> iterator() { return ds.iterator(); }
	public int lastIndexOf(Object o) { return ds.lastIndexOf(o); }
	public ListIterator<DrawObject> listIterator() { return ds.listIterator(); }
	public ListIterator<DrawObject> listIterator(int index) { return ds.listIterator(index); }
	public boolean remove(Object o) { return ds.remove(o); }
	public DrawObject remove(int index) { return ds.remove(index); }
	public boolean removeAll(Collection<?> c) { return ds.removeAll(c); }
	public boolean retainAll(Collection<?> c) { return ds.retainAll(c); }
	public DrawObject set(int index, DrawObject o) { return ds.set(index, o); }
	public int size() { return ds.size(); }
	public List<DrawObject> subList(int start, int end) { return ds.subList(start, end); }
	public Object[] toArray() { return ds.toArray(); }
	public <T> T[] toArray(T[] a) { return ds.toArray(a); }
}
