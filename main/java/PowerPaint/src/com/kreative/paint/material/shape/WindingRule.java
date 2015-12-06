package com.kreative.paint.material.shape;

import java.awt.geom.GeneralPath;

public enum WindingRule {
	EVEN_ODD(GeneralPath.WIND_EVEN_ODD),
	NON_ZERO(GeneralPath.WIND_NON_ZERO);
	
	public final int awtValue;
	
	private WindingRule(int awtValue) {
		this.awtValue = awtValue;
	}
	
	public static WindingRule forAWTValue(int value) {
		switch (value) {
		case GeneralPath.WIND_EVEN_ODD: return EVEN_ODD;
		case GeneralPath.WIND_NON_ZERO: return NON_ZERO;
		default: return null;
		}
	}
}
