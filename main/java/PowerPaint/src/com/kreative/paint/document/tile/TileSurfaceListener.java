package com.kreative.paint.document.tile;

public interface TileSurfaceListener {
	public void tileSurfaceLocationChanged(TileSurfaceEvent e);
	public void tileSurfaceMatteChanged(TileSurfaceEvent e);
	public void tileSurfaceContentChanged(TileSurfaceEvent e);
}
