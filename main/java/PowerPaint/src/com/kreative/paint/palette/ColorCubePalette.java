package com.kreative.paint.palette;

import com.kreative.paint.PaintContext;
import com.kreative.paint.material.MaterialManager;

public class ColorCubePalette extends PaintContextPalette {
	private static final long serialVersionUID = 1L;
	
	public ColorCubePalette(PaintContext pc, MaterialManager mm) {
		super(pc);
		ColorCubePanel p = new ColorCubePanel(pc, mm);
		panels.add(p);
		setContentPane(p);
		setResizable(false);
		pack();
	}
}
