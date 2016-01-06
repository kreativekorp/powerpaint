package com.kreative.paint.document.layer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Collection;
import com.kreative.paint.document.draw.ImageDrawObject;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.tile.PaintSurface;
import com.kreative.paint.document.tile.Tile;
import com.kreative.paint.document.tile.TileSurface;
import com.kreative.paint.document.tile.TileSurfaceEvent;
import com.kreative.paint.document.tile.TileSurfaceListener;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;

public class PaintLayer extends Layer implements PaintSurface {
	private TileSurface ts;
	private Shape clip;
	private BufferedImage poppedImage;
	private AffineTransform poppedImageTransform;
	private TileSurfaceListener listener;
	
	public PaintLayer(String name, int matte) {
		this(name, 8, 8, matte);
	}
	
	public PaintLayer(String name, int tileWidth, int tileHeight, int matte) {
		super(name);
		this.ts = new TileSurface(0, 0, tileWidth, tileHeight, matte);
		this.clip = null;
		this.poppedImage = null;
		this.poppedImageTransform = null;
		this.listener = new PaintLayerTileSurfaceListener(this);
		this.ts.addTileSurfaceListener(this.listener);
	}
	
	private PaintLayer(PaintLayer o) {
		super(o);
		this.ts = o.ts.clone();
		this.clip = o.clip;
		this.poppedImage = cloneImage(o.poppedImage);
		this.poppedImageTransform = o.poppedImageTransform;
		this.listener = new PaintLayerTileSurfaceListener(this);
		this.ts.addTileSurfaceListener(this.listener);
	}
	
	@Override
	public void setHistory(History history) {
		super.setHistory(history);
		this.ts.setHistory(history);
	}
	
	@Override
	public PaintLayer clone() {
		return new PaintLayer(this);
	}
	
	@Override
	protected void paintImpl(Graphics2D g, int gx, int gy, int gw, int gh) {
		ts.paint(g);
		if (poppedImage != null) {
			while (!g.drawImage(
				poppedImage,
				poppedImageTransform,
				null
			));
		}
	}
	
	public Shape getClip() { return clip; }
	
	private static class ClipAtom implements Atom {
		private PaintLayer l;
		private Shape oldClip;
		private Shape newClip;
		public ClipAtom(PaintLayer l, Shape newClip) {
			this.l = l;
			this.oldClip = l.clip;
			this.newClip = newClip;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof ClipAtom)
			    && (((ClipAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldClip = ((ClipAtom)prev).oldClip;
			return this;
		}
		@Override
		public void undo() {
			l.clip = oldClip;
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void redo() {
			l.clip = newClip;
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
	}
	
	public void setClip(Shape clip) {
		if (this.clip == clip) return;
		if (history != null) history.add(new ClipAtom(this, clip));
		this.clip = clip;
		this.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
	}
	
	public boolean isImagePopped() { return poppedImage != null; }
	public BufferedImage getPoppedImage() { return poppedImage; }
	public AffineTransform getPoppedImageTransform() { return poppedImageTransform; }
	
	private static class PoppedImageAtom implements Atom {
		private PaintLayer l;
		private BufferedImage oldImage;
		private AffineTransform oldTx;
		private BufferedImage newImage;
		private AffineTransform newTx;
		public PoppedImageAtom(PaintLayer l, BufferedImage image, AffineTransform tx) {
			this.l = l;
			this.oldImage = l.poppedImage;
			this.oldTx = l.poppedImageTransform;
			this.newImage = image;
			this.newTx = tx;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof PoppedImageAtom)
			    && (((PoppedImageAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldImage = ((PoppedImageAtom)prev).oldImage;
			this.oldTx = ((PoppedImageAtom)prev).oldTx;
			return this;
		}
		@Override
		public void undo() {
			l.poppedImage = oldImage;
			l.poppedImageTransform = oldTx;
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void redo() {
			l.poppedImage = newImage;
			l.poppedImageTransform = newTx;
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
	}
	
	public void setPoppedImage(BufferedImage image, AffineTransform tx) {
		if (this.poppedImage == image && this.poppedImageTransform == tx) return;
		if (history != null) history.add(new PoppedImageAtom(this, image, tx));
		this.poppedImage = image;
		this.poppedImageTransform = tx;
		this.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
	}
	
	public BufferedImage copyImage(boolean aa) {
		if (poppedImage != null) {
			if (isTranslation(poppedImageTransform)) {
				return cloneImage(poppedImage);
			} else {
				Rectangle bounds = new Rectangle(0, 0, poppedImage.getWidth(), poppedImage.getHeight());
				bounds = poppedImageTransform.createTransformedShape(bounds).getBounds();
				BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = image.createGraphics();
				g.drawImage(
					poppedImage,
					new AffineTransformOp(
						poppedImageTransform,
						aa ? AffineTransformOp.TYPE_BICUBIC :
						AffineTransformOp.TYPE_NEAREST_NEIGHBOR
					),
					-bounds.x,
					-bounds.y
				);
				g.dispose();
				return image;
			}
		} else {
			Rectangle bounds = clip.getBounds();
			BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.translate(-bounds.x, -bounds.y);
			g.setClip(clip);
			ts.paint(g);
			g.dispose();
			return image;
		}
	}
	
	public ImageDrawObject copyImageObject() {
		PaintSettings ps = new PaintSettings(null, null);
		if (poppedImage != null) {
			if (poppedImageTransform == null) {
				return ImageDrawObject.forGraphicsDrawImage(ps, poppedImage, 0, 0);
			} else {
				return ImageDrawObject.forGraphicsDrawImage(ps, poppedImage, poppedImageTransform);
			}
		} else {
			Rectangle bounds = clip.getBounds();
			BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.translate(-bounds.x, -bounds.y);
			g.setClip(clip);
			ts.paint(g);
			g.dispose();
			return ImageDrawObject.forGraphicsDrawImage(ps, image, bounds.x, bounds.y);
		}
	}
	
	public void pasteImage(BufferedImage image, AffineTransform tx, boolean aa) {
		if (poppedImage != null) pushImage(aa);
		setPoppedImage(cloneImage(image), tx);
	}
	
	public void popImage(boolean copy, boolean aa) {
		if (poppedImage != null) pushImage(aa);
		Rectangle bounds = clip.getBounds();
		BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.translate(-bounds.x, -bounds.y);
		g.setClip(clip);
		ts.paint(g);
		g.dispose();
		if (!copy) ts.clear(bounds.x, bounds.y, bounds.width, bounds.height, clip);
		setPoppedImage(image, AffineTransform.getTranslateInstance(bounds.x, bounds.y));
	}
	
	public void pushImage(boolean aa) {
		if (poppedImage != null) {
			Graphics2D g = ts.createPaintGraphics();
			g.drawImage(
				poppedImage,
				(poppedImageTransform == null) ? null :
				new AffineTransformOp(
					poppedImageTransform,
					aa ? AffineTransformOp.TYPE_BICUBIC :
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR
				),
				0, 0
			);
			g.dispose();
			setPoppedImage(null, null);
		}
	}
	
	public void transformPoppedImage(AffineTransform tx) {
		if (poppedImage != null) {
			AffineTransform newTx = new AffineTransform();
			if (tx != null) newTx.concatenate(tx);
			if (poppedImageTransform != null) newTx.concatenate(poppedImageTransform);
			setPoppedImage(poppedImage, newTx);
		}
	}
	
	public void deletePoppedImage() {
		if (poppedImage != null) {
			setPoppedImage(null, null);
		} else if (clip != null) {
			Rectangle r = clip.getBounds();
			ts.clear(r.x, r.y, r.width, r.height, clip);
		}
	}
	
	public int getTileWidth() { return ts.getTileWidth(); }
	public int getTileHeight() { return ts.getTileHeight(); }
	public int getMatte() { return ts.getMatte(); }
	
	public void addTile(Tile t) {
		ts.addTile(t);
	}
	
	public Tile getTile(int x, int y, boolean create) {
		return ts.getTile(x, y, create);
	}
	
	public Collection<Tile> getTiles(int x, int y, int width, int height, boolean create) {
		return ts.getTiles(x, y, width, height, create);
	}
	
	public Collection<Tile> getTiles() {
		return ts.getTiles();
	}
	
	@Override public int getMinX() { return ts.getMinX(); }
	@Override public int getMinY() { return ts.getMinY(); }
	@Override public int getMaxX() { return ts.getMaxX(); }
	@Override public int getMaxY() { return ts.getMaxY(); }
	
	@Override
	public boolean contains(int x, int y) {
		return ts.contains(x, y);
	}
	
	@Override
	public boolean contains(int x, int y, int width, int height) {
		return ts.contains(x, y, width, height);
	}
	
	@Override
	public int getRGB(int x, int y) {
		return ts.getRGB(x, y);
	}
	
	@Override
	public int[] getRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
		return ts.getRGB(x, y, width, height, rgb, offset, rowCount);
	}
	
	@Override
	public void setRGB(int x, int y, int rgb) {
		ts.setRGB(x, y, rgb, clip);
	}
	
	@Override
	public void setRGB(int x, int y, int rgb, Shape clip) {
		ts.setRGB(x, y, rgb, intersect(this.clip, clip));
	}
	
	@Override
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
		ts.setRGB(x, y, width, height, rgb, offset, rowCount);
	}
	
	@Override
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount, Shape clip) {
		ts.setRGB(x, y, width, height, rgb, offset, rowCount, intersect(this.clip, clip));
	}
	
	@Override
	public void clear(int x, int y, int width, int height) {
		ts.clear(x, y, width, height, clip);
	}
	
	@Override
	public void clear(int x, int y, int width, int height, Shape clip) {
		ts.clear(x, y, width, height, intersect(this.clip, clip));
	}
	
	@Override
	public void clearAll() {
		if (clip != null) {
			Rectangle r = clip.getBounds();
			ts.clear(r.x, r.y, r.width, r.height, clip);
		} else {
			ts.clearAll();
		}
	}
	
	@Override
	public Graphics2D createPaintGraphics() {
		Graphics2D g = ts.createPaintGraphics();
		g.setClip(clip);
		return g;
	}
	
	private static BufferedImage cloneImage(BufferedImage src) {
		if (src == null) return null;
		int width = src.getWidth();
		int height = src.getHeight();
		int[] rgb = new int[width * height];
		BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		src.getRGB(0, 0, width, height, rgb, 0, width);
		dst.setRGB(0, 0, width, height, rgb, 0, width);
		return dst;
	}
	
	private static boolean isTranslation(AffineTransform tx) {
		return (tx == null) || (
			tx.getScaleX() == 1 &&
			tx.getScaleY() == 1 &&
			tx.getShearX() == 0 &&
			tx.getShearY() == 0
		);
	}
	
	private static Shape intersect(Shape a, Shape b) {
		if (a == null) return b;
		if (b == null) return a;
		Area c = new Area(a);
		c.intersect(new Area(b));
		return c;
	}
	
	private static class PaintLayerTileSurfaceListener implements TileSurfaceListener {
		private final PaintLayer l;
		public PaintLayerTileSurfaceListener(PaintLayer l) {
			this.l = l;
		}
		@Override
		public void tileSurfaceLocationChanged(TileSurfaceEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void tileSurfaceMatteChanged(TileSurfaceEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void tileSurfaceContentChanged(TileSurfaceEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
	}
}
