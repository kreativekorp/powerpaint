package com.kreative.paint.palette;

import com.kreative.paint.PaintContext;

public class ColorCubePalette extends PaintContextPalette {
	private static final long serialVersionUID = 1L;
	
	public ColorCubePalette(PaintContext pc) {
		super(pc);
		ColorCubePanel p = new ColorCubePanel(pc);
		panels.add(p);
		setContentPane(p);
		setResizable(false);
		pack();
	}
}
