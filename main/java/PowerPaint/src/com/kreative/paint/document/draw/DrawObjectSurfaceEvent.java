package com.kreative.paint.document.draw;

public class DrawObjectSurfaceEvent {
	public static final int DRAW_OBJECT_SURFACE_LOCATION_CHANGED = 1;
	public static final int DRAW_OBJECT_SURFACE_CONTENT_CHANGED = 2;
	
	private final int id;
	private final DrawObjectSurface ds;
	
	public DrawObjectSurfaceEvent(int id, DrawObjectSurface ds) {
		this.id = id;
		this.ds = ds;
	}
	
	public int getID() {
		return id;
	}
	
	public DrawObjectSurface getDrawObjectSurface() {
		return ds;
	}
}
