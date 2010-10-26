/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.palette;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import com.kreative.paint.PaintContext;
import com.kreative.paint.PaintSettings;

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
		int a = pc.getTextAlignment();
		setL(l, alk, alw, a == PaintSettings.LEFT);
		setL(c, ack, acw, a == PaintSettings.CENTER);
		setL(r, ark, arw, a == PaintSettings.RIGHT);
		l.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFTextAlignPanel.this.pc.setTextAlignment(PaintSettings.LEFT);
			}
		});
		c.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFTextAlignPanel.this.pc.setTextAlignment(PaintSettings.CENTER);
			}
		});
		r.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFTextAlignPanel.this.pc.setTextAlignment(PaintSettings.RIGHT);
			}
		});
		update();
	}
	
	public void update() {
		int a = pc.getTextAlignment();
		setL(l, alk, alw, a == PaintSettings.LEFT);
		setL(c, ack, acw, a == PaintSettings.CENTER);
		setL(r, ark, arw, a == PaintSettings.RIGHT);
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
