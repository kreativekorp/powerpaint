package com.kreative.paint.rcp;

import java.awt.Color;
import java.io.Serializable;

public class ColorChangeEvent implements Serializable {
	private static final long serialVersionUID = 1;
	
	private RCPXComponent src;
	private Color c;
	
	public ColorChangeEvent(RCPXComponent src, Color c) {
		this.src = src;
		this.c = c;
	}
	
	public RCPXComponent getSource() {
		return src;
	}
	
	public Color getNewColor() {
		return c;
	}
}
