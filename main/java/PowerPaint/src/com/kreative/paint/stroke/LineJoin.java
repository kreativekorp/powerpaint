package com.kreative.paint.stroke;

import java.awt.BasicStroke;

public enum LineJoin {
	MITER(BasicStroke.JOIN_MITER),
	ROUND(BasicStroke.JOIN_ROUND),
	BEVEL(BasicStroke.JOIN_BEVEL);
	
	public final int awtValue;
	
	private LineJoin(int awtValue) {
		this.awtValue = awtValue;
	}
	
	public static LineJoin forAWTValue(int value) {
		switch (value) {
		case BasicStroke.JOIN_MITER: return MITER;
		case BasicStroke.JOIN_ROUND: return ROUND;
		case BasicStroke.JOIN_BEVEL: return BEVEL;
		default: return null;
		}
	}
}
