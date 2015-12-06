package com.kreative.paint.material.colorpalette;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class RCPXBorder {
	public static final RCPXBorder NONE = new RCPXBorder(false, false, false, false);
	public static final RCPXBorder ALL = new RCPXBorder(true, true, true, true);
	
	public final boolean top, left, bottom, right;
	
	public RCPXBorder(boolean top, boolean left, boolean bottom, boolean right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}
	
	public static Color contrastingColor(Color c) {
		if (c.getAlpha() < 128) return Color.black;
		float gray = c.getRed()*0.3f + c.getGreen()*0.59f + c.getBlue()*0.11f;
		if (gray < 127.5f) return Color.white;
		return Color.black;
	}
	
	public void paint(Graphics g, Rectangle r, Color c) {
		// Inner border.
		if (c != null) {
			g.setColor(contrastingColor(c));
			if (top) g.fillRect(r.x, r.y+1, r.width, 1);
			if (left) g.fillRect(r.x+1, r.y, 1, r.height);
			if (bottom) g.fillRect(r.x, r.y+r.height-2, r.width, 1);
			if (right) g.fillRect(r.x+r.width-2, r.y, 1, r.height);
		}
		// Outer border.
		g.setColor(Color.black);
		if (top) g.fillRect(r.x, r.y, r.width, 1);
		if (left) g.fillRect(r.x, r.y, 1, r.height);
		if (bottom) g.fillRect(r.x, r.y+r.height-1, r.width, 1);
		if (right) g.fillRect(r.x+r.width-1, r.y, 1, r.height);
	}
}
