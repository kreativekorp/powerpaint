package com.kreative.paint.rcp;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

public class ColorChangeEvent implements Serializable {
	private static final long serialVersionUID = 1;
	
	private Component src;
	private Color c;
	
	public ColorChangeEvent(Component src, Color c) {
		this.src = src;
		this.c = c;
	}
	
	public Component getSource() {
		return src;
	}
	
	public Color getNewColor() {
		return c;
	}
}
