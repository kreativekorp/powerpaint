package com.kreative.paint.document.tile;

public class TileEvent {
	public static final int TILE_LOCATION_CHANGED = 1;
	public static final int TILE_MATTE_CHANGED = 2;
	public static final int TILE_CONTENT_CHANGED = 3;
	
	private final int id;
	private final Tile tile;
	
	public TileEvent(int id, Tile tile) {
		this.id = id;
		this.tile = tile;
	}
	
	public int getID() {
		return id;
	}
	
	public Tile getTile() {
		return tile;
	}
}
