package com.kreative.paint.stroke;

import java.awt.BasicStroke;

public enum EndCap {
	BUTT(BasicStroke.CAP_BUTT),
	ROUND(BasicStroke.CAP_ROUND),
	SQUARE(BasicStroke.CAP_SQUARE);
	
	public final int awtValue;
	
	private EndCap(int awtValue) {
		this.awtValue = awtValue;
	}
	
	public static EndCap forAWTValue(int value) {
		switch (value) {
		case BasicStroke.CAP_BUTT: return BUTT;
		case BasicStroke.CAP_ROUND: return ROUND;
		case BasicStroke.CAP_SQUARE: return SQUARE;
		default: return null;
		}
	}
}
