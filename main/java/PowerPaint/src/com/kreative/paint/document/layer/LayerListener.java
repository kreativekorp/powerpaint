package com.kreative.paint.document.layer;

public interface LayerListener {
	public void layerNameChanged(LayerEvent e);
	public void layerVisibleChanged(LayerEvent e);
	public void layerLockedChanged(LayerEvent e);
	public void layerSelectedChanged(LayerEvent e);
	public void layerLocationChanged(LayerEvent e);
	public void layerContentChanged(LayerEvent e);
}
