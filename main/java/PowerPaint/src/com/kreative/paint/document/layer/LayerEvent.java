package com.kreative.paint.document.layer;

public class LayerEvent {
	public static final int LAYER_NAME_CHANGED = 1;
	public static final int LAYER_VISIBLE_CHANGED = 2;
	public static final int LAYER_LOCKED_CHANGED = 3;
	public static final int LAYER_SELECTED_CHANGED = 4;
	public static final int LAYER_LOCATION_CHANGED = 5;
	public static final int LAYER_CONTENT_CHANGED = 6;
	
	private final int id;
	private final Layer layer;
	
	public LayerEvent(int id, Layer layer) {
		this.id = id;
		this.layer = layer;
	}
	
	public int getID() {
		return id;
	}
	
	public Layer getLayer() {
		return layer;
	}
}
