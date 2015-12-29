package com.kreative.paint.document.draw;

public class DrawObjectEvent {
	public static final int DRAW_OBJECT_PAINT_SETTINGS_CHANGED = 1;
	public static final int DRAW_OBJECT_TRANSFORM_CHANGED = 2;
	public static final int DRAW_OBJECT_VISIBLE_CHANGED = 3;
	public static final int DRAW_OBJECT_LOCKED_CHANGED = 4;
	public static final int DRAW_OBJECT_SELECTED_CHANGED = 5;
	public static final int DRAW_OBJECT_CONTROL_POINT_CHANGED = 6;
	public static final int DRAW_OBJECT_LOCATION_CHANGED = 7;
	public static final int DRAW_OBJECT_IMPLEMENTATION_PROPERTY_CHANGED = 8;
	
	private final int id;
	private final DrawObject drawObject;
	
	public DrawObjectEvent(int id, DrawObject drawObject) {
		this.id = id;
		this.drawObject = drawObject;
	}
	
	public int getID() {
		return id;
	}
	
	public DrawObject getDrawObject() {
		return drawObject;
	}
}
