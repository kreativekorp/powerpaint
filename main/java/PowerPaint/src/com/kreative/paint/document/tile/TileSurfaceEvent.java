package com.kreative.paint.document.tile;

public class TileSurfaceEvent {
	public static final int TILE_SURFACE_LOCATION_CHANGED = 1;
	public static final int TILE_SURFACE_MATTE_CHANGED = 2;
	public static final int TILE_SURFACE_CONTENT_CHANGED = 3;
	
	private final int id;
	private final TileSurface ts;
	
	public TileSurfaceEvent(int id, TileSurface ts) {
		this.id = id;
		this.ts = ts;
	}
	
	public int getID() {
		return id;
	}
	
	public TileSurface getTileSurface() {
		return ts;
	}
}
