package com.kreative.paint.material.colorpalette;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

public abstract class RCPXLayoutOrSwatch {
	public abstract boolean isLayout();
	public abstract RCPXLayout asLayout();
	public abstract boolean isSwatch();
	public abstract RCPXSwatch asSwatch();
	public abstract int repeatCount();
	public abstract Color awtColor(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p);
	public abstract String name(List<RCPXColor> colors, int repeatIndex, Rectangle r, Point p);
	public abstract void paint(List<RCPXColor> colors, int repeatIndex, Graphics g, Rectangle r, Color currCol);
}
