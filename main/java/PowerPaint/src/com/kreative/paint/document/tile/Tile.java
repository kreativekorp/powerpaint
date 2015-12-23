package com.kreative.paint.document.tile;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.document.undo.Recordable;

public class Tile implements Recordable, PaintSurface {
	private int x;
	private int y;
	private int width;
	private int height;
	private int matte;
	private BufferedImage image;
	private History history;
	private List<TileListener> listeners;
	
	public Tile(int x, int y, int width, int height, int matte) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.matte = matte;
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] rgb = new int[width * height];
		for (int i = 0; i < rgb.length; i++) rgb[i] = matte;
		this.image.setRGB(0, 0, width, height, rgb, 0, width);
		this.history = null;
		this.listeners = new ArrayList<TileListener>();
	}
	
	@Override
	public History getHistory() {
		return history;
	}
	
	@Override
	public void setHistory(History history) {
		this.history = history;
	}
	
	public void addTileListener(TileListener l) {
		listeners.add(l);
	}
	
	public void removeTileListener(TileListener l) {
		listeners.remove(l);
	}
	
	public TileListener[] getTileListeners() {
		return listeners.toArray(new TileListener[listeners.size()]);
	}
	
	protected void notifyTileListeners(int id) {
		if (listeners.isEmpty()) return;
		TileEvent e = new TileEvent(id, this);
		switch (id) {
			case TileEvent.TILE_LOCATION_CHANGED:
				for (TileListener l : listeners)
					l.tileLocationChanged(e);
				break;
			case TileEvent.TILE_MATTE_CHANGED:
				for (TileListener l : listeners)
					l.tileMatteChanged(e);
				break;
			case TileEvent.TILE_CONTENT_CHANGED:
				for (TileListener l : listeners)
					l.tileContentChanged(e);
				break;
		}
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public Point getLocation() { return new Point(x, y); }
	
	private static class LocationAtom implements Atom {
		private Tile t;
		private int oldX, oldY;
		private int newX, newY;
		public LocationAtom(Tile t, int newX, int newY) {
			this.t = t;
			this.oldX = t.x; this.oldY = t.y;
			this.newX = newX; this.newY = newY;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof LocationAtom)
			    && (((LocationAtom)prev).t == this.t);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldX = ((LocationAtom)prev).oldX;
			this.oldY = ((LocationAtom)prev).oldY;
			return this;
		}
		@Override
		public void redo() {
			t.x = newX;
			t.y = newY;
			t.notifyTileListeners(TileEvent.TILE_LOCATION_CHANGED);
		}
		@Override
		public void undo() {
			t.x = oldX;
			t.y = oldY;
			t.notifyTileListeners(TileEvent.TILE_LOCATION_CHANGED);
		}
	}
	
	public void setX(int x) {
		if (this.x == x) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.x = x;
		this.notifyTileListeners(TileEvent.TILE_LOCATION_CHANGED);
	}
	
	public void setY(int y) {
		if (this.y == y) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.y = y;
		this.notifyTileListeners(TileEvent.TILE_LOCATION_CHANGED);
	}
	
	public void setLocation(Point p) {
		if (this.x == p.x && this.y == p.y) return;
		if (history != null) history.add(new LocationAtom(this, p.x, p.y));
		this.x = p.x;
		this.y = p.y;
		this.notifyTileListeners(TileEvent.TILE_LOCATION_CHANGED);
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public Dimension getSize() { return new Dimension(width, height); }
	
	public int getMatte() { return matte; }
	
	private static class MatteAtom implements Atom {
		private Tile t;
		private int oldMatte;
		private int newMatte;
		public MatteAtom(Tile t, int newMatte) {
			this.t = t;
			this.oldMatte = t.matte;
			this.newMatte = newMatte;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof MatteAtom)
			    && (((MatteAtom)prev).t == this.t);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldMatte = ((MatteAtom)prev).oldMatte;
			return this;
		}
		@Override
		public void redo() {
			t.matte = newMatte;
			t.notifyTileListeners(TileEvent.TILE_MATTE_CHANGED);
		}
		@Override
		public void undo() {
			t.matte = oldMatte;
			t.notifyTileListeners(TileEvent.TILE_MATTE_CHANGED);
		}
	}
	
	public void setMatte(int matte) {
		if (this.matte == matte) return;
		if (history != null) history.add(new MatteAtom(this, matte));
		this.matte = matte;
		this.notifyTileListeners(TileEvent.TILE_MATTE_CHANGED);
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void paint(Graphics2D g) {
		g.drawImage(image, null, x, y);
	}
	
	public void paint(Graphics2D g, int x, int y) {
		g.drawImage(image, null, this.x + x, this.y + y);
	}
	
	@Override public int getMinX() { return x; }
	@Override public int getMinY() { return y; }
	@Override public int getMaxX() { return x + width; }
	@Override public int getMaxY() { return y + height; }
	
	@Override
	public boolean contains(int x, int y) {
		return x >= this.x && y >= this.y
		    && x < this.x + this.width
		    && y < this.y + this.height;
	}
	
	@Override
	public boolean contains(int x, int y, int width, int height) {
		return x >= this.x && y >= this.y
		    && x + width <= this.x + this.width
		    && y + height <= this.y + this.height;
	}
	
	@Override
	public int getRGB(int x, int y) {
		try {
			WritableRaster r = image.getRaster();
			DataBufferInt b = (DataBufferInt)r.getDataBuffer();
			int[] d = b.getData();
			return d[this.width * (y - this.y) + (x - this.x)];
		} catch (Exception e) {
			return image.getRGB(x - this.x, y - this.y);
		}
	}
	
	@Override
	public int[] getRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
		try {
			WritableRaster r = image.getRaster();
			DataBufferInt b = (DataBufferInt)r.getDataBuffer();
			int[] d = b.getData();
			if (rgb == null) rgb = new int[offset + rowCount * height];
			for (int sy = this.width * (y - this.y) + (x - this.x), dy = offset, iy = 0; iy < height; sy += this.width, dy += rowCount, iy++) {
				for (int sx = sy, dx = dy, ix = 0; ix < width; sx++, dx++, ix++) {
					rgb[dx] = d[sx];
				}
			}
			return rgb;
		} catch (Exception e) {
			return image.getRGB(x - this.x, y - this.y, width, height, rgb, offset, rowCount);
		}
	}
	
	private static class SetRGBAtom implements Atom {
		private Tile t;
		private int x, y, width, height;
		private int[] oldRGB;
		private int[] newRGB;
		private int offset, rowCount;
		public SetRGBAtom(Tile t, int x, int y, int width, int height, int[] rgb, int offset, int rowCount, boolean copy) {
			this.t = t;
			this.x = x - t.x;
			this.y = y - t.y;
			this.width = width;
			this.height = height;
			this.oldRGB = new int[width * height];
			t.getRGB(x, y, width, height, this.oldRGB, 0, width);
			if (copy) {
				this.newRGB = new int[width * height];
				for (int sy = offset, dy = 0, iy = 0; iy < height; sy += rowCount, dy += width, iy++) {
					for (int sx = sy, dx = dy, ix = 0; ix < width; sx++, dx++, ix++) {
						this.newRGB[dx] = rgb[sx];
					}
				}
				this.offset = 0;
				this.rowCount = width;
			} else {
				this.newRGB = rgb;
				this.offset = offset;
				this.rowCount = rowCount;
			}
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof SetRGBAtom)
			    && (((SetRGBAtom)prev).t == this.t)
			    && (((SetRGBAtom)prev).x == this.x)
			    && (((SetRGBAtom)prev).y == this.y)
			    && (((SetRGBAtom)prev).width == this.width)
			    && (((SetRGBAtom)prev).height == this.height);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldRGB = ((SetRGBAtom)prev).oldRGB;
			return this;
		}
		@Override
		public void redo() {
			t.image.setRGB(x, y, width, height, newRGB, offset, rowCount);
			t.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
		}
		@Override
		public void undo() {
			t.image.setRGB(x, y, width, height, oldRGB, 0, width);
			t.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
		}
	}
	
	@Override
	public void setRGB(int x, int y, int rgb) {
		if (history != null) history.add(new SetRGBAtom(this, x, y, 1, 1, new int[]{rgb}, 0, 1, false));
		this.image.setRGB(x - this.x, y - this.y, rgb);
		this.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
	}
	
	@Override
	public void setRGB(int x, int y, int rgb, Shape clip) {
		if (clip == null || clip.contains(x, y)) {
			this.setRGB(x, y, rgb);
		}
	}
	
	@Override
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
		if (history != null) history.add(new SetRGBAtom(this, x, y, width, height, rgb, offset, rowCount, true));
		this.image.setRGB(x - this.x, y - this.y, width, height, rgb, offset, rowCount);
		this.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
	}
	
	@Override
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount, Shape clip) {
		if (clip == null || clip.contains(x, y, width, height)) {
			this.setRGB(x, y, width, height, rgb, offset, rowCount);
		} else {
			int[] oldRGB = new int[width * height];
			this.getRGB(x, y, width, height, oldRGB, 0, width);
			for (int oy = 0, ny = offset, ay = y, iy = 0; iy < height; oy += width, ny += rowCount, ay++, iy++) {
				for (int ox = oy, nx = ny, ax = x, ix = 0; ix < width; ox++, nx++, ax++, ix++) {
					if (clip.contains(ax, ay)) oldRGB[ox] = rgb[nx];
				}
			}
			this.setRGB(x, y, width, height, oldRGB, 0, width);
		}
	}
	
	@Override
	public void clear(int x, int y, int width, int height) {
		int[] rgb = new int[width * height];
		for (int i = 0; i < rgb.length; i++) rgb[i] = matte;
		if (history != null) history.add(new SetRGBAtom(this, x, y, width, height, rgb, 0, width, false));
		this.image.setRGB(x - this.x, y - this.y, width, height, rgb, 0, width);
		this.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
	}
	
	@Override
	public void clear(int x, int y, int width, int height, Shape clip) {
		if (clip == null || clip.contains(x, y, width, height)) {
			this.clear(x, y, width, height);
		} else {
			int[] oldRGB = new int[width * height];
			this.getRGB(x, y, width, height, oldRGB, 0, width);
			for (int oy = 0, ay = y, iy = 0; iy < height; oy += width, ay++, iy++) {
				for (int ox = oy, ax = x, ix = 0; ix < width; ox++, ax++, ix++) {
					if (clip.contains(ax, ay)) oldRGB[ox] = matte;
				}
			}
			this.setRGB(x, y, width, height, oldRGB, 0, width);
		}
	}
	
	private static class ClearAllAtom implements Atom {
		private Tile t;
		private BufferedImage oldImage;
		private BufferedImage newImage;
		public ClearAllAtom(Tile t, BufferedImage newImage) {
			this.t = t;
			this.oldImage = t.image;
			this.newImage = newImage;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof ClearAllAtom)
			    && (((ClearAllAtom)prev).t == this.t);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldImage = ((ClearAllAtom)prev).oldImage;
			return this;
		}
		@Override
		public void redo() {
			t.image = newImage;
			t.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
		}
		@Override
		public void undo() {
			t.image = oldImage;
			t.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
		}
	}
	
	@Override
	public void clearAll() {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] rgb = new int[width * height];
		for (int i = 0; i < rgb.length; i++) rgb[i] = matte;
		newImage.setRGB(0, 0, width, height, rgb, 0, width);
		if (history != null) history.add(new ClearAllAtom(this, newImage));
		this.image = newImage;
		this.notifyTileListeners(TileEvent.TILE_CONTENT_CHANGED);
	}
	
	@Override
	public Graphics2D createPaintGraphics() {
		return new TileGraphics(
			this, this.x, this.y,
			this.width, this.height,
			this.image, this.history
		);
	}
}
