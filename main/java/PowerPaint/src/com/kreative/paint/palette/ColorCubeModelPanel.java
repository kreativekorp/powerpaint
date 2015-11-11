package com.kreative.paint.palette;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kreative.paint.util.ColorModel;
import com.kreative.paint.util.FSGridLayout;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.ColorModel.ColorChannel;

public class ColorCubeModelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final ColorCubeModelComponent cc;
	private final JLabel[] xl;
	private final JLabel[] yl;
	private final JLabel[] sl;
	
	public ColorCubeModelPanel(ColorModel cm) {
		this.cc = new ColorCubeModelComponent(cm);
		int nc = cc.getChannelCount();
		int ns = cc.getSliderChannels().length;
		this.xl = new JLabel[nc];
		this.yl = new JLabel[nc];
		this.sl = new JLabel[ns];
		JPanel xlp = new JPanel(new FSGridLayout(1, nc, -1, -1));
		JPanel ylp = new JPanel(new FSGridLayout(nc, 1, -1, -1));
		JPanel slp = new JPanel(new GridLayout(1, ns, 0, 0));
		for (int ci = 0; ci < nc; ci++) {
			ColorChannel ch = cc.getChannel(ci);
			xlp.add(xl[ci] = newChannelLabel(ch));
			ylp.add(yl[ci] = newChannelLabel(ch));
		}
		for (int si = 0; si < ns; si++) {
			slp.add(sl[si] = newSliderLabel());
		}
		JPanel lp = new JPanel(new BorderLayout(2, 2));
		lp.add(newCornerComponent(), BorderLayout.NORTH);
		lp.add(ylp, BorderLayout.CENTER);
		JPanel tp = new JPanel(new BorderLayout(0, 0));
		tp.add(xlp, BorderLayout.CENTER);
		tp.add(slp, BorderLayout.EAST);
		JPanel rp = new JPanel(new BorderLayout(2, 2));
		rp.add(tp, BorderLayout.NORTH);
		rp.add(cc, BorderLayout.CENTER);
		setLayout(new BorderLayout(2, 2));
		add(lp, BorderLayout.WEST);
		add(rp, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		updateLabels();
		for (int ci = 0; ci < nc; ci++) {
			final int i = ci;
			xl[ci].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					cc.setSelectedIndexX(i);
					updateLabels();
				}
			});
			yl[ci].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					cc.setSelectedIndexY(i);
					updateLabels();
				}
			});
		}
	}
	
	public ColorCubeModelComponent getModelComponent() {
		return cc;
	}
	
	private void updateLabels() {
		int nc = cc.getChannelCount();
		int xci = cc.getSelectedIndexX();
		int yci = cc.getSelectedIndexY();
		for (int ci = 0; ci < nc; ci++) {
			xl[ci].setBackground((ci == xci) ? SystemColor.textHighlight : SystemColor.text);
			xl[ci].setForeground((ci == xci) ? SystemColor.textHighlightText : SystemColor.textText);
			yl[ci].setBackground((ci == yci) ? SystemColor.textHighlight : SystemColor.text);
			yl[ci].setForeground((ci == yci) ? SystemColor.textHighlightText : SystemColor.textText);
		}
		ColorChannel[] sd = cc.getSliderChannels();
		int ns = sd.length;
		for (int si = 0; si < ns; si++) {
			sl[si].setText(sd[si].symbol);
			sl[si].setToolTipText(sd[si].name);
		}
	}
	
	private static JLabel newChannelLabel(ColorChannel cc) {
		JLabel l = new JLabel(cc.symbol);
		SwingUtils.shrink(l);
		l.setToolTipText(cc.name);
		l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		l.setHorizontalAlignment(JLabel.CENTER);
		l.setVerticalAlignment(JLabel.CENTER);
		l.setHorizontalTextPosition(JLabel.CENTER);
		l.setVerticalTextPosition(JLabel.CENTER);
		l.setOpaque(true);
		l.setBackground(SystemColor.text);
		l.setForeground(SystemColor.textText);
		l.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.gray),
			BorderFactory.createEmptyBorder(1, 2, 1, 2)
		));
		return l;
	}
	
	private static JLabel newSliderLabel() {
		JLabel l = new JLabel("X");
		SwingUtils.shrink(l);
		l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		l.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		l.setHorizontalAlignment(JLabel.CENTER);
		l.setVerticalAlignment(JLabel.CENTER);
		l.setHorizontalTextPosition(JLabel.CENTER);
		l.setVerticalTextPosition(JLabel.CENTER);
		int w = ColorCubeModelComponent.SLIDER_WIDTH;
		int h = l.getPreferredSize().height;
		Dimension d = new Dimension(w, h);
		l.setMinimumSize(d);
		l.setPreferredSize(d);
		l.setMaximumSize(d);
		l.setBorder(BorderFactory.createEmptyBorder(0, w - 10, 0, 0));
		return l;
	}
	
	private static Component newCornerComponent() {
		JLabel l = new JLabel("X");
		SwingUtils.shrink(l);
		int h = l.getPreferredSize().height + 4;
		return Box.createVerticalStrut(h);
	}
}
