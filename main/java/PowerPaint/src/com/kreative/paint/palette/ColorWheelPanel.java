package com.kreative.paint.palette;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import javax.swing.BorderFactory;

import com.kreative.paint.PaintContext;
import com.kreative.paint.rcp.ColorChangeEvent;
import com.kreative.paint.rcp.ColorChangeListener;

public class ColorWheelPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private final ColorWheelComponent cw;
	private boolean eventexec = false;
	
	public ColorWheelPanel(PaintContext pc) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		
		// Initialize Components
		this.cw = new ColorWheelComponent();
		Paint p = pc.getEditedEditedground();
		if (p instanceof Color) cw.setColor((Color)p);
		
		// Create Layout
		setLayout(new BorderLayout(4, 4));
		add(cw, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		// Add Listeners
		ColorChangeListener ccl = new ColorChangeListener() {
			@Override
			public void colorChanged(ColorChangeEvent e) {
				if (!eventexec) {
					eventexec = true;
					Color color = e.getNewColor();
					ColorWheelPanel.this.pc.setEditedEditedground(color);
					eventexec = false;
				}
			}
		};
		cw.addColorChangeListener(ccl);
	}
	
	@Override
	public void update() {
		if (!eventexec) {
			eventexec = true;
			Paint p = pc.getEditedEditedground();
			if (p instanceof Color) cw.setColor((Color)p);
			eventexec = false;
		}
	}
}
