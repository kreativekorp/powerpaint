package com.kreative.paint.palette;

import com.kreative.paint.PaintContext;

public class ColorSliderPalette extends PaintContextPalette {
	private static final long serialVersionUID = 1L;
	
	public ColorSliderPalette(PaintContext pc) {
		super(pc);
		ColorSliderPanel p = new ColorSliderPanel(pc);
		panels.add(p);
		setContentPane(p);
		setResizable(false);
		pack();
	}
}
