package com.kreative.paint.document.draw;

import java.awt.geom.Arc2D;

public enum ArcType {
	OPEN(Arc2D.OPEN),
	CHORD(Arc2D.CHORD),
	PIE(Arc2D.PIE);
	
	public final int awtValue;
	
	private ArcType(int awtValue) {
		this.awtValue = awtValue;
	}
	
	public static ArcType forAWTValue(int value) {
		switch (value) {
		case Arc2D.OPEN: return OPEN;
		case Arc2D.CHORD: return CHORD;
		case Arc2D.PIE: return PIE;
		default: return null;
		}
	}
}
