package com.kreative.paint.palette;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.kreative.paint.PaintContext;
import com.kreative.paint.rcp.ColorChangeEvent;
import com.kreative.paint.rcp.ColorChangeListener;
import com.kreative.paint.util.ColorModel;
import com.kreative.paint.util.SwingUtils;

public class ColorSliderPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private final ColorModel[] cm;
	private final JList csl;
	private final ColorSliderModelPanel[] csp;
	private final ColorSliderModelComponent[] csc;
	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	private boolean eventexec = false;
	
	public ColorSliderPanel(PaintContext pc) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		
		// Initialize Components
		this.cm = new ColorModel[] {
			ColorModel.GRAY_ALPHA_4,
			ColorModel.GRAY_ALPHA_8,
			ColorModel.GRAY_ALPHA_100,
			ColorModel.RGBA_4,
			ColorModel.RGBA_8,
			ColorModel.RGBA_16,
			ColorModel.RGBA_100,
			ColorModel.HSVA_360_100,
			ColorModel.HSLA_360_100,
			ColorModel.HWBA_360_100,
			ColorModel.NAIVE_CMYKA_100
		};
		this.csl = new JList(cm);
		this.csl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.csl.setSelectedValue(ColorModel.RGBA_8, false);
		this.csl.setVisibleRowCount(1);
		this.csp = new ColorSliderModelPanel[cm.length];
		this.csc = new ColorSliderModelComponent[cm.length];
		for (int i = 0; i < cm.length; i++) {
			this.csp[i] = new ColorSliderModelPanel(cm[i]);
			this.csc[i] = csp[i].getModelComponent();
		}
		Paint p = pc.getEditedEditedground();
		if (p instanceof Color) {
			Color color = (Color)p;
			for (ColorSliderModelComponent c : csc) {
				c.setColor(color);
			}
		}
		
		// Create Layout
		this.cardLayout = new CardLayout();
		this.cardPanel = new JPanel(cardLayout);
		for (int i = 0; i < cm.length; i++) {
			cardPanel.add(csp[i], cm[i].getName());
		}
		cardLayout.show(cardPanel, ColorModel.RGBA_8.getName());
		setLayout(new BorderLayout());
		add(SwingUtils.shrink(new JScrollPane(csl,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		)), BorderLayout.LINE_START);
		add(cardPanel, BorderLayout.CENTER);
		
		// Add Listeners
		csl.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				ColorModel scm = (ColorModel)csl.getSelectedValue();
				if (scm != null) cardLayout.show(cardPanel, scm.getName());
			}
		});
		ColorChangeListener ccl = new ColorChangeListener() {
			@Override
			public void colorChanged(ColorChangeEvent e) {
				if (!eventexec) {
					eventexec = true;
					Component src = e.getSource();
					Color color = e.getNewColor();
					for (ColorSliderModelComponent c : csc) {
						if (c != src) {
							c.setColor(color);
						}
					}
					ColorSliderPanel.this.pc.setEditedEditedground(color);
					eventexec = false;
				}
			}
		};
		for (ColorSliderModelComponent c : csc) {
			c.addColorChangeListener(ccl);
		}
	}
	
	@Override
	public void update() {
		if (!eventexec) {
			eventexec = true;
			Paint p = pc.getEditedEditedground();
			if (p instanceof Color) {
				Color color = (Color)p;
				for (ColorSliderModelComponent c : csc) {
					c.setColor(color);
				}
			}
			eventexec = false;
		}
	}
}
