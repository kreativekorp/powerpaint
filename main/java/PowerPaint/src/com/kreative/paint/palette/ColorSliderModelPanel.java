package com.kreative.paint.palette;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kreative.paint.rcp.ColorChangeEvent;
import com.kreative.paint.rcp.ColorChangeListener;
import com.kreative.paint.util.ColorModel;
import com.kreative.paint.util.SwingUtils;

public class ColorSliderModelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final ColorSliderModelComponent cs;
	private final JLabel[] vl;
	private final JLabel[] hl;
	
	public ColorSliderModelPanel(ColorModel cm) {
		// Sliders
		this.cs = new ColorSliderModelComponent(cm);
		int nc = cs.getChannelCount();
		boolean hex = false;
		for (int ci = 0; ci < nc; ci++) {
			if (cs.getChannel(ci).hex) {
				hex = true;
			}
		}
		// Value Labels
		this.vl = new JLabel[nc];
		JPanel lp = new JPanel(new GridLayout(nc, 1));
		JPanel vp = new JPanel(new GridLayout(nc, 1));
		for (int ci = 0; ci < nc; ci++) {
			ColorModel.ColorChannel cc = cs.getChannel(ci);
			vl[ci] = new JLabel(toDecString(cc.max));
			lp.add(shrink(new JLabel(cc.symbol), cc.name, true));
			vp.add(shrink(vl[ci], null, false));
		}
		// Hex Labels
		if (hex) {
			this.hl = new JLabel[nc];
			JPanel hp = new JPanel(new GridLayout(nc, 1));
			for (int ci = 0; ci < nc; ci++) {
				ColorModel.ColorChannel cc = cs.getChannel(ci);
				hl[ci] = new JLabel(toHexString(cc.max, cc.max, cc.hex));
				hp.add(shrink(hl[ci], null, false));
			}
			JPanel rp = new JPanel(new GridLayout(1, 2, 4, 4));
			rp.add(vp);
			rp.add(hp);
			vp = rp;
		} else {
			this.hl = null;
		}
		// Layout
		setLayout(new BorderLayout(4, 4));
		add(lockWidth(lp), BorderLayout.LINE_START);
		add(lockWidth(cs), BorderLayout.CENTER);
		add(lockWidth(vp), BorderLayout.LINE_END);
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		cs.addColorChangeListener(new ColorChangeListener() {
			@Override
			public void colorChanged(ColorChangeEvent e) {
				updateLabels();
			}
		});
	}
	
	public ColorSliderModelComponent getModelComponent() {
		return cs;
	}
	
	private void updateLabels() {
		int nc = cs.getChannelCount();
		for (int ci = 0; ci < nc; ci++) {
			ColorModel.ColorChannel cc = cs.getChannel(ci);
			float cv = cs.getChannelValue(ci);
			vl[ci].setText(toDecString(cv));
			if (hl != null) hl[ci].setText(toHexString(cv, cc.max, cc.hex));
		}
	}
	
	private static String toDecString(float v) {
		if (v == (int)v) return Integer.toString((int)v);
		return Float.toString(v);
	}
	
	private static String toHexString(float v, float max, boolean sel) {
		if (v == (int)v && max == (int)max && sel) {
			String h = Integer.toHexString((int)v).toUpperCase();
			int hl = Integer.toHexString((int)max).length();
			while (h.length() < hl) h = "0" + h;
			return h;
		} else {
			return null;
		}
	}
	
	private static <C extends JComponent> C shrink(C c, String tooltip, boolean center) {
		SwingUtils.shrink(c);
		c.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		if (tooltip != null) {
			c.setToolTipText(tooltip);
		}
		if (center && c instanceof JLabel) {
			JLabel l = (JLabel)c;
			l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setHorizontalTextPosition(JLabel.CENTER);
		}
		return c;
	}
	
	private static <C extends JComponent> C lockWidth(C c) {
		int w = c.getPreferredSize().width;
		int minh = c.getMinimumSize().height;
		int prefh = c.getPreferredSize().height;
		int maxh = c.getMaximumSize().height;
		c.setMinimumSize(new Dimension(w, minh));
		c.setPreferredSize(new Dimension(w, prefh));
		c.setMaximumSize(new Dimension(w, maxh));
		return c;
	}
}
