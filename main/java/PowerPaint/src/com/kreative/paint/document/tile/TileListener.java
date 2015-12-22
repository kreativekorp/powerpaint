package com.kreative.paint.document.tile;

public interface TileListener {
	public void tileLocationChanged(TileEvent e);
	public void tileMatteChanged(TileEvent e);
	public void tileContentChanged(TileEvent e);
}
