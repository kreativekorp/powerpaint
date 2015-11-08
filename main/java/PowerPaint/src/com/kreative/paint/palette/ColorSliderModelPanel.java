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
			lp.add(shrink(new JLabel(cc.symbol), cc.name, JLabel.CENTER));
			vp.add(shrink(vl[ci] = newDecLabel(cc), null, JLabel.RIGHT));
		}
		// Hex Labels
		if (hex) {
			this.hl = new JLabel[nc];
			JPanel hp = new JPanel(new GridLayout(nc, 1));
			for (int ci = 0; ci < nc; ci++) {
				ColorModel.ColorChannel cc = cs.getChannel(ci);
				hp.add(shrink(hl[ci] = newHexLabel(cc), null, JLabel.LEFT));
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
			String ds = toDecString(cv, cc.min);
			vl[ci].setText(ds);
			vl[ci].setToolTipText(ds);
			if (hl != null) {
				String hs = toHexString(cv, cc.max, cc.hex);
				hl[ci].setText(hs);
				hl[ci].setToolTipText(hs);
			}
		}
	}
	
	private static String toDecString(float v, float min) {
		v = Math.round(v * 1000f) / 1000f;
		String s = (v == (int)v) ? Integer.toString((int)v) : Float.toString(v);
		return (v > 0 && min < 0) ? ("+" + s) : s;
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
	
	private static JLabel newDecLabel(ColorModel.ColorChannel cc) {
		String[] candidateStrings = new String[] {
			toDecString(cc.min, cc.min),
			toDecString(cc.min + cc.step, cc.min),
			toDecString(cc.max, cc.min),
			toDecString(cc.max - cc.step, cc.min)
		};
		JLabel l = new JLabel();
		String maxString = "";
		int maxWidth = 0;
		for (String candidateString : candidateStrings) {
			l.setText(candidateString);
			int candidateWidth = l.getPreferredSize().width;
			if (candidateWidth > maxWidth) {
				maxString = candidateString;
				maxWidth = candidateWidth;
			}
		}
		l.setText(maxString);
		return l;
	}
	
	private static JLabel newHexLabel(ColorModel.ColorChannel cc) {
		String[] candidateStrings = new String[] {
			toHexString(cc.min, cc.max, cc.hex),
			toHexString(cc.min + cc.step, cc.max, cc.hex),
			toHexString(cc.max, cc.max, cc.hex),
			toHexString(cc.max - cc.step, cc.max, cc.hex)
		};
		JLabel l = new JLabel();
		String maxString = "";
		int maxWidth = 0;
		for (String candidateString : candidateStrings) {
			l.setText(candidateString);
			int candidateWidth = l.getPreferredSize().width;
			if (candidateWidth > maxWidth) {
				maxString = candidateString;
				maxWidth = candidateWidth;
			}
		}
		l.setText(maxString);
		return l;
	}
	
	private static JLabel shrink(JLabel c, String tooltip, int align) {
		SwingUtils.shrink(c);
		c.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		if (tooltip != null) {
			c.setToolTipText(tooltip);
		}
		switch (align) {
			case JLabel.LEFT:
				c.setAlignmentX(JLabel.LEFT_ALIGNMENT);
				c.setHorizontalAlignment(JLabel.LEFT);
				c.setHorizontalTextPosition(JLabel.LEFT);
				break;
			case JLabel.CENTER:
				c.setAlignmentX(JLabel.CENTER_ALIGNMENT);
				c.setHorizontalAlignment(JLabel.CENTER);
				c.setHorizontalTextPosition(JLabel.CENTER);
				break;
			case JLabel.RIGHT:
				c.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
				c.setHorizontalAlignment(JLabel.RIGHT);
				c.setHorizontalTextPosition(JLabel.RIGHT);
				break;
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
