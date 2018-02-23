package com.kreative.paint.palette;

import com.kreative.paint.PaintContext;
import com.kreative.paint.material.MaterialManager;

public class ColorSliderPalette extends PaintContextPalette {
	private static final long serialVersionUID = 1L;
	
	public ColorSliderPalette(PaintContext pc, MaterialManager mm) {
		super(pc);
		ColorSliderPanel p = new ColorSliderPanel(pc, mm);
		panels.add(p);
		setContentPane(p);
		setResizable(false);
		pack();
	}
}
