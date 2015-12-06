package com.kreative.paint.material.colorpalette;

import java.util.ArrayList;
import java.util.List;

public class RCPXPalette {
	public final String name;
	public final RCPXOrientation orientation;
	public final int hwidth, hheight;
	public final int swidth, sheight;
	public final int vwidth, vheight;
	public final List<RCPXColor> colors;
	public final boolean colorsOrdered;
	public final RCPXLayout layout;
	
	public RCPXPalette(
		String name,
		RCPXOrientation orientation,
		int hwidth, int hheight,
		int swidth, int sheight,
		int vwidth, int vheight,
		List<RCPXColor> colors,
		boolean colorsOrdered,
		RCPXLayout layout
	) {
		this.name = name;
		this.orientation = orientation;
		this.hwidth = hwidth; this.hheight = hheight;
		this.swidth = swidth; this.sheight = sheight;
		this.vwidth = vwidth; this.vheight = vheight;
		this.colors = (colors != null) ? colors : new ArrayList<RCPXColor>();
		this.colorsOrdered = colorsOrdered;
		this.layout = (layout != null) ? layout : null;
	}
	
	public boolean colorsNamed() {
		for (RCPXColor color : colors) {
			String name = color.name();
			if (name != null && name.length() > 0) {
				return true;
			}
		}
		return false;
	}
}
