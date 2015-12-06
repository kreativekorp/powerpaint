package com.kreative.paint.palette;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import com.kreative.paint.PaintContext;
import com.kreative.paint.material.colorpalette.ColorChangeEvent;
import com.kreative.paint.material.colorpalette.ColorChangeListener;
import com.kreative.paint.util.CIEColorModel;
import com.kreative.paint.util.ColorModel;
import com.kreative.paint.util.SwingUtils;

public class ColorSliderPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private final ColorModel[] cm;
	private final JComboBox csl;
	private final ColorSliderModelPanel[] csp;
	private final ColorSliderModelComponent[] csc;
	private final JPanel cardPanel;
	private boolean eventexec = false;
	
	public ColorSliderPanel(PaintContext pc) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		
		// Initialize Components
		this.cm = new ColorModel[] {
			ColorModel.GRAY_4,
			ColorModel.GRAY_8,
			ColorModel.GRAY_16,
			ColorModel.GRAY_100,
			ColorModel.RGB_4,
			ColorModel.RGB_8,
			ColorModel.RGB_16,
			ColorModel.RGB_100,
			ColorModel.HSV_360_100,
			ColorModel.HSL_360_100,
			ColorModel.HWB_360_100,
			ColorModel.NAIVE_CMYK_100,
			ColorModel.YUV_SDTV,
			ColorModel.YUV_HDTV,
			ColorModel.Y_CB_CR_SDTV,
			ColorModel.Y_CB_CR_HDTV,
			ColorModel.Y_DB_DR,
			ColorModel.YIQ,
			ColorModel.Y_CG_CO,
			CIEColorModel.CIE_XYZ_100,
			CIEColorModel.CIE_xyY_100,
			CIEColorModel.CIE_RGB_100,
			CIEColorModel.CIE_Lab_D65,
			CIEColorModel.CIE_LCh_D65,
			CIEColorModel.Hunter_Lab_D65,
			CIEColorModel.Hunter_LCh_D65
		};
		this.csl = new JComboBox(cm);
		this.csl.setEditable(false);
		this.csl.setMaximumRowCount(48);
		this.csl.setSelectedItem(ColorModel.RGB_8);
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
		this.cardPanel = new JPanel(new GridLayout());
		for (int i = 0; i < cm.length; i++) {
			if (cm[i] == ColorModel.RGB_8) {
				cardPanel.add(csp[i]);
				break;
			}
		}
		setLayout(new BorderLayout());
		add(SwingUtils.shrink(csl), BorderLayout.PAGE_START);
		add(cardPanel, BorderLayout.CENTER);
		
		// Add Listeners
		csl.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int i = csl.getSelectedIndex();
					if (i >= 0 && i < csp.length) {
						cardPanel.removeAll();
						cardPanel.add(csp[i]);
						repaint();
						pack();
					}
				}
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
	
	private void pack() {
		this.invalidate();
		Component c = this.getParent();
		while (true) {
			if (c == null) {
				break;
			} else if (c instanceof Window) {
				((Window)c).pack();
				break;
			} else if (c instanceof Frame) {
				((Frame)c).pack();
				break;
			} else if (c instanceof Dialog) {
				((Dialog)c).pack();
				break;
			} else {
				c = c.getParent();
			}
		}
	}
}
