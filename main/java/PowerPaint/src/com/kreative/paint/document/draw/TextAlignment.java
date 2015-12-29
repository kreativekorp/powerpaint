package com.kreative.paint.document.draw;

import javax.swing.SwingConstants;

public enum TextAlignment {
	LEFT(SwingConstants.LEFT),
	CENTER(SwingConstants.CENTER),
	RIGHT(SwingConstants.RIGHT);
	
	public final int awtValue;
	
	private TextAlignment(int awtValue) {
		this.awtValue = awtValue;
	}
	
	public static TextAlignment forAWTValue(int value) {
		switch (value) {
		case SwingConstants.LEFT: return LEFT;
		case SwingConstants.CENTER: return CENTER;
		case SwingConstants.RIGHT: return RIGHT;
		default: return null;
		}
	}
}
