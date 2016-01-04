package com.kreative.paint.document.tile;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.document.undo.Recordable;

public class TileSurface implements Cloneable, Recordable, PaintSurface {
	private int x;
	private int y;
	private int tws, twa, twm;
	private int ths, tha, thm;
	private int matte;
	private Map<Long,Tile> tiles;
	private History history;
	private List<TileSurfaceListener> listeners;
	private TileListener tileListener;
	
	public TileSurface(int x, int y, int tileWidth, int tileHeight, int matte) {
		if (tileWidth < 4 || tileWidth > 16) throw new IllegalArgumentException("tileWidth = " + tileWidth);
		if (tileHeight < 4 || tileHeight > 16) throw new IllegalArgumentException("tileHeight = " + tileHeight);
		this.x = x;
		this.y = y;
		this.tws = tileWidth;
		this.twa = 1 << tileWidth;
		this.twm = (1 << tileWidth) - 1;
		this.ths = tileHeight;
		this.tha = 1 << tileHeight;
		this.thm = (1 << tileHeight) - 1;
		this.matte = matte;
		this.tiles = new HashMap<Long,Tile>();
		this.history = null;
		this.listeners = new ArrayList<TileSurfaceListener>();
		this.tileListener = new TileSurfaceTileListener(this);
	}
	
	private TileSurface(TileSurface o) {
		this.x = o.x;
		this.y = o.y;
		this.tws = o.tws;
		this.twa = o.twa;
		this.twm = o.twm;
		this.ths = o.ths;
		this.tha = o.tha;
		this.thm = o.thm;
		this.matte = o.matte;
		this.tiles = new HashMap<Long,Tile>();
		for (Map.Entry<Long,Tile> e : o.tiles.entrySet()) {
			this.tiles.put(e.getKey(), e.getValue().clone());
		}
		this.history = null;
		this.listeners = new ArrayList<TileSurfaceListener>();
		this.tileListener = new TileSurfaceTileListener(this);
		for (Tile t : this.tiles.values()) {
			t.addTileListener(this.tileListener);
		}
	}
	
	@Override
	public TileSurface clone() {
		return new TileSurface(this);
	}
	
	@Override
	public History getHistory() {
		return history;
	}
	
	@Override
	public void setHistory(History history) {
		this.history = history;
		for (Tile t : tiles.values()) t.setHistory(history);
	}
	
	public void addTileSurfaceListener(TileSurfaceListener l) {
		listeners.add(l);
	}
	
	public void removeTileSurfaceListener(TileSurfaceListener l) {
		listeners.remove(l);
	}
	
	public TileSurfaceListener[] getTileSurfaceListeners() {
		return listeners.toArray(new TileSurfaceListener[listeners.size()]);
	}
	
	protected void notifyTileSurfaceListeners(int id) {
		if (listeners.isEmpty()) return;
		TileSurfaceEvent e = new TileSurfaceEvent(id, this);
		switch (id) {
			case TileSurfaceEvent.TILE_SURFACE_LOCATION_CHANGED:
				for (TileSurfaceListener l : listeners)
					l.tileSurfaceLocationChanged(e);
				break;
			case TileSurfaceEvent.TILE_SURFACE_MATTE_CHANGED:
				for (TileSurfaceListener l : listeners)
					l.tileSurfaceMatteChanged(e);
				break;
			case TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED:
				for (TileSurfaceListener l : listeners)
					l.tileSurfaceContentChanged(e);
				break;
		}
	}
	
	private static class TileSurfaceTileListener implements TileListener {
		private final TileSurface ts;
		public TileSurfaceTileListener(TileSurface ts) {
			this.ts = ts;
		}
		@Override
		public void tileLocationChanged(TileEvent e) {
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void tileMatteChanged(TileEvent e) {
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void tileContentChanged(TileEvent e) {
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED);
		}
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public Point getLocation() { return new Point(x, y); }
	
	private static class LocationAtom implements Atom {
		private TileSurface ts;
		private int oldX, oldY;
		private int newX, newY;
		public LocationAtom(TileSurface ts, int newX, int newY) {
			this.ts = ts;
			this.oldX = ts.x; this.oldY = ts.y;
			this.newX = newX; this.newY = newY;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof LocationAtom)
			    && (((LocationAtom)prev).ts == this.ts);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldX = ((LocationAtom)prev).oldX;
			this.oldY = ((LocationAtom)prev).oldY;
			return this;
		}
		@Override
		public void redo() {
			ts.x = newX;
			ts.y = newY;
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_LOCATION_CHANGED);
		}
		@Override
		public void undo() {
			ts.x = oldX;
			ts.y = oldY;
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_LOCATION_CHANGED);
		}
	}
	
	public void setX(int x) {
		if (this.x == x) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.x = x;
		this.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_LOCATION_CHANGED);
	}
	
	public void setY(int y) {
		if (this.y == y) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.y = y;
		this.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_LOCATION_CHANGED);
	}
	
	public void setLocation(Point p) {
		if (this.x == p.x && this.y == p.y) return;
		if (history != null) history.add(new LocationAtom(this, p.x, p.y));
		this.x = p.x;
		this.y = p.y;
		this.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_LOCATION_CHANGED);
	}
	
	public int getTileWidth() { return tws; }
	public int getTileHeight() { return ths; }
	
	public int getMatte() { return matte; }
	
	private static class MatteAtom implements Atom {
		private TileSurface ts;
		private int oldMatte;
		private int newMatte;
		public MatteAtom(TileSurface ts, int newMatte) {
			this.ts = ts;
			this.oldMatte = ts.matte;
			this.newMatte = newMatte;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof MatteAtom)
			    && (((MatteAtom)prev).ts == this.ts);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldMatte = ((MatteAtom)prev).oldMatte;
			return this;
		}
		@Override
		public void redo() {
			ts.matte = newMatte;
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_MATTE_CHANGED);
		}
		@Override
		public void undo() {
			ts.matte = oldMatte;
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_MATTE_CHANGED);
		}
	}
	
	public void setMatte(int matte) {
		if (this.matte == matte) return;
		if (history != null) history.add(new MatteAtom(this, matte));
		this.matte = matte;
		for (Tile t : tiles.values()) t.setMatte(matte);
		this.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_MATTE_CHANGED);
	}
	
	private static class SetTileAtom implements Atom {
		private TileSurface ts;
		private long key;
		private Tile oldTile;
		private Tile newTile;
		public SetTileAtom(TileSurface ts, long key, Tile newTile) {
			this.ts = ts;
			this.key = key;
			this.oldTile = ts.tiles.get(key);
			this.newTile = newTile;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof SetTileAtom)
			    && (((SetTileAtom)prev).ts == this.ts)
			    && (((SetTileAtom)prev).key == this.key);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldTile = ((SetTileAtom)prev).oldTile;
			return this;
		}
		@Override
		public void redo() {
			if (oldTile != null) {
				oldTile.setHistory(null);
				oldTile.removeTileListener(ts.tileListener);
			}
			if (newTile != null) {
				newTile.setHistory(ts.history);
				newTile.addTileListener(ts.tileListener);
				ts.tiles.put(key, newTile);
			} else {
				ts.tiles.remove(key);
			}
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void undo() {
			if (newTile != null) {
				newTile.setHistory(null);
				newTile.removeTileListener(ts.tileListener);
			}
			if (oldTile != null) {
				oldTile.setHistory(ts.history);
				oldTile.addTileListener(ts.tileListener);
				ts.tiles.put(key, oldTile);
			} else {
				ts.tiles.remove(key);
			}
			ts.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED);
		}
	}
	
	private Tile createTile(long key, int x, int y) {
		Tile t = new Tile(x << tws, y << ths, twa, tha, matte);
		if (history != null) history.add(new SetTileAtom(this, key, t));
		t.setHistory(history);
		t.addTileListener(tileListener);
		this.tiles.put(key, t);
		this.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED);
		return t;
	}
	
	public void addTile(Tile t) {
		int x = t.getX() >> tws;
		int y = t.getY() >> ths;
		long key = (x & 0xFFFFFFFFL) | ((y & 0xFFFFFFFFL) << 32L);
		if (history != null) history.add(new SetTileAtom(this, key, t));
		if (tiles.containsKey(key)) {
			tiles.get(key).setHistory(null);
			tiles.get(key).removeTileListener(tileListener);
		}
		t.setHistory(history);
		t.addTileListener(tileListener);
		this.tiles.put(key, t);
		this.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED);
	}
	
	public Tile getTile(int x, int y, boolean create) {
		x = ((x - this.x) >> tws);
		y = ((y - this.y) >> ths);
		long key = (x & 0xFFFFFFFFL) | ((y & 0xFFFFFFFFL) << 32L);
		if (tiles.containsKey(key)) return tiles.get(key);
		else if (create) return createTile(key, x, y);
		else return null;
	}
	
	public Collection<Tile> getTiles(int x, int y, int width, int height, boolean create) {
		Collection<Tile> intersectingTiles = new HashSet<Tile>();
		if (width < 0) { x += width; width = -width; }
		if (height < 0) { y += height; height = -height; }
		int left = ((x - this.x - 8) >> tws);
		int top = ((y - this.y - 8) >> ths);
		int right = ((x - this.x + width + 8) >> tws);
		int bottom = ((y - this.y + height + 8) >> ths);
		for (int ky = top; ky <= bottom; ky++) {
			for (int kx = left; kx <= right; kx++) {
				long key = (kx & 0xFFFFFFFFL) | ((ky & 0xFFFFFFFFL) << 32L);
				if (tiles.containsKey(key)) intersectingTiles.add(tiles.get(key));
				else if (create) intersectingTiles.add(createTile(key, kx, ky));
			}
		}
		return intersectingTiles;
	}
	
	public Collection<Tile> getTiles() {
		return tiles.values();
	}
	
	public void paint(Graphics2D g) {
		for (Tile t : tiles.values()) t.paint(g, x, y);
	}
	
	public void paint(Graphics2D g, int x, int y) {
		for (Tile t : tiles.values()) t.paint(g, this.x + x, this.y + y);
	}
	
	@Override public int getMinX() { return Integer.MIN_VALUE; }
	@Override public int getMinY() { return Integer.MIN_VALUE; }
	@Override public int getMaxX() { return Integer.MAX_VALUE; }
	@Override public int getMaxY() { return Integer.MAX_VALUE; }
	@Override public boolean contains(int x, int y) { return true; }
	@Override public boolean contains(int x, int y, int width, int height) { return true; }
	
	@Override
	public int getRGB(int x, int y) {
		Tile t = getTile(x, y, false);
		if (t == null) return matte;
		return t.getRGB(
			t.getX() + ((x - this.x) & twm),
			t.getY() + ((y - this.y) & thm)
		);
	}
	
	@Override
	public int[] getRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
		if (rgb == null) rgb = new int[offset + height * rowCount];
		for (int ay = offset, iy = 0; iy < height; ay += rowCount, iy++) {
			for (int ax = ay, ix = 0; ix < width; ax++, ix++) {
				rgb[ax] = matte;
			}
		}
		Collection<Tile> tt = getTiles(x, y, width, height, false);
		x -= this.x;
		y -= this.y;
		for (Tile t : tt) {
			int tx = t.getX();
			int ty = t.getY();
			int x1 = x - tx; if (x1 < 0) x1 = 0; else if (x1 > twa) x1 = twa;
			int y1 = y - ty; if (y1 < 0) y1 = 0; else if (y1 > tha) y1 = tha;
			int x2 = x + width - tx; if (x2 < 0) x2 = 0; else if (x2 > twa) x2 = twa;
			int y2 = y + height - ty; if (y2 < 0) y2 = 0; else if (y2 > tha) y2 = tha;
			if (x1 == x2 || y1 == y2) continue;
			int xo = tx - x; if (xo < 0) xo = 0;
			int yo = ty - y; if (yo < 0) yo = 0;
			t.getRGB(tx + x1, ty + y1, x2 - x1, y2 - y1, rgb, offset + (yo * rowCount) + xo, rowCount);
		}
		return rgb;
	}
	
	@Override
	public void setRGB(int x, int y, int rgb) {
		Tile t = getTile(x, y, true);
		t.setRGB(
			t.getX() + ((x - this.x) & twm),
			t.getY() + ((y - this.y) & thm),
			rgb
		);
	}
	
	@Override
	public void setRGB(int x, int y, int rgb, Shape clip) {
		if (clip == null || clip.contains(x, y)) {
			this.setRGB(x, y, rgb);
		}
	}
	
	@Override
	public void setRGB(int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
		Collection<Tile> tt = getTiles(x, y, width, height, true);
		x -= this.x;
		y -= this.y;
		for (Tile t : tt) {
			int tx = t.getX();
			int ty = t.getY();
			int x1 = x - tx; if (x1 < 0) x1 = 0; else if (x1 > twa) x1 = twa;
			int y1 = y - ty; if (y1 < 0) y1 = 0; else if (y1 > tha) y1 = tha;
			int x2 = x + width - tx; if (x2 < 0) x2 = 0; else if (x2 > twa) x2 = twa;
			int y2 = y + height - ty; if (y2 < 0) y2 = 0; else if (y2 > tha) y2 = tha;
			if (x1 == x2 || y1 == y2) continue;
			int xo = tx - x; if (xo < 0) xo = 0;
			int yo = ty - y; if (yo < 0) yo = 0;
			t.setRGB(tx + x1, ty + y1, x2 - x1, y2 - y1, rgb, offset + (yo * rowCount) + xo, rowCount);
		}
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
		this.setRGB(x, y, width, height, rgb, 0, width);
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
	
	@Override
	public void clearAll() {
		if (history != null) {
			for (long key : tiles.keySet()) {
				history.add(new SetTileAtom(this, key, null));
			}
		}
		for (Tile t : tiles.values()) {
			t.setHistory(null);
			t.removeTileListener(tileListener);
		}
		this.tiles.clear();
		this.notifyTileSurfaceListeners(TileSurfaceEvent.TILE_SURFACE_CONTENT_CHANGED);
	}
	
	@Override
	public Graphics2D createPaintGraphics() {
		return new TileSurfaceGraphics(this, this.x, this.y);
	}
}
