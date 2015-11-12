package com.kreative.paint.palette;

import com.kreative.paint.PaintContext;

public class ColorWheelPalette extends PaintContextPalette {
	private static final long serialVersionUID = 1L;
	
	public ColorWheelPalette(PaintContext pc) {
		super(pc);
		ColorWheelPanel p = new ColorWheelPanel(pc);
		panels.add(p);
		setContentPane(p);
		setResizable(false);
		pack();
	}
}
