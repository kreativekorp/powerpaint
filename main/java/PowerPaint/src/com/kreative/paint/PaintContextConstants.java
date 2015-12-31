package com.kreative.paint;

public interface PaintContextConstants {
	public static final int CHANGED_DRAW_COMPOSITE = 0x0001;
	public static final int CHANGED_DRAW_PAINT = 0x0002;
	public static final int CHANGED_FILL_COMPOSITE = 0x0004;
	public static final int CHANGED_FILL_PAINT = 0x0008;
	public static final int CHANGED_STROKE = 0x0010;
	public static final int CHANGED_FONT = 0x0100;
	public static final int CHANGED_TEXT_ALIGNMENT = 0x0200;
	public static final int CHANGED_ANTI_ALIASED = 0x1000;
	public static final int CHANGED_COMPOSITE = CHANGED_DRAW_COMPOSITE | CHANGED_FILL_COMPOSITE;
	public static final int CHANGED_PAINT = CHANGED_DRAW_PAINT | CHANGED_FILL_PAINT;
	public static final int CHANGED_EDITING_STROKE = 0x40000000;
	public static final int CHANGED_EDITING_BKGND = 0x80000000;
	public static final int CHANGED_EDITING = CHANGED_EDITING_STROKE | CHANGED_EDITING_BKGND;
}
