package com.kreative.paint.palette;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import com.kreative.paint.PaintContext;
import com.kreative.paint.document.draw.TextAlignment;

public class SNFTextAlignPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private ImageIcon alk = new ImageIcon(getToolkit().createImage(SNFPresetsPanel.class.getResource("AlignLeft.png")));
	private ImageIcon alw = new ImageIcon(getToolkit().createImage(SNFPresetsPanel.class.getResource("AlignLeftI.png")));
	private ImageIcon ack = new ImageIcon(getToolkit().createImage(SNFPresetsPanel.class.getResource("AlignCenter.png")));
	private ImageIcon acw = new ImageIcon(getToolkit().createImage(SNFPresetsPanel.class.getResource("AlignCenterI.png")));
	private ImageIcon ark = new ImageIcon(getToolkit().createImage(SNFPresetsPanel.class.getResource("AlignRight.png")));
	private ImageIcon arw = new ImageIcon(getToolkit().createImage(SNFPresetsPanel.class.getResource("AlignRightI.png")));
	private JLabel l, c, r;
	
	public SNFTextAlignPanel(PaintContext pc) {
		super(pc, CHANGED_TEXT_ALIGNMENT);
		setLayout(new GridLayout(1,3));
		add(l = new JLabel(alk));
		add(c = new JLabel(ack));
		add(r = new JLabel(ark));
		l.setToolTipText(PaletteUtilities.messages.getString("snf.align.left"));
		c.setToolTipText(PaletteUtilities.messages.getString("snf.align.center"));
		r.setToolTipText(PaletteUtilities.messages.getString("snf.align.right"));
		TextAlignment a = pc.getTextAlignment();
		setL(l, alk, alw, a == TextAlignment.LEFT);
		setL(c, ack, acw, a == TextAlignment.CENTER);
		setL(r, ark, arw, a == TextAlignment.RIGHT);
		l.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFTextAlignPanel.this.pc.setTextAlignment(TextAlignment.LEFT);
			}
		});
		c.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFTextAlignPanel.this.pc.setTextAlignment(TextAlignment.CENTER);
			}
		});
		r.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFTextAlignPanel.this.pc.setTextAlignment(TextAlignment.RIGHT);
			}
		});
		update();
	}
	
	public void update() {
		TextAlignment a = pc.getTextAlignment();
		setL(l, alk, alw, a == TextAlignment.LEFT);
		setL(c, ack, acw, a == TextAlignment.CENTER);
		setL(r, ark, arw, a == TextAlignment.RIGHT);
	}
	
	private static void setL(JLabel l, ImageIcon k, ImageIcon w, boolean sel) {
		Color bg = sel ? SystemColor.textHighlight : SystemColor.text;
		l.setOpaque(true);
		l.setBackground(bg);
		Color fg = sel ? SystemColor.textHighlightText : SystemColor.textText;
		int fgk = (33 * fg.getRed() + 59 * fg.getGreen() + 11 * fg.getBlue()) / 100;
		l.setIcon((fgk < 128) ? k : w);
	}
}
