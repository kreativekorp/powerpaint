/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.rcp.CheckerboardPaint;

public class SNFPresetsPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Dimension size = new Dimension(67, 45);
	
	public SNFPresetsPanel(PaintContext pc) {
		super(pc, 0);
		JComponent white = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				Insets i = getInsets();
				g.setColor(Color.white);
				g.fillRect(i.left, i.top, getWidth()-i.left-i.right, getHeight()-i.top-i.bottom);
			}
		};
		white.setToolTipText(PaletteUtilities.messages.getString("snf.white"));
		white.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.black));
		white.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFPresetsPanel.this.pc.setEditedPaint(Color.white);
			}
		});
		JComponent black = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				Insets i = getInsets();
				g.setColor(Color.black);
				g.fillRect(i.left, i.top, getWidth()-i.left-i.right, getHeight()-i.top-i.bottom);
			}
		};
		black.setToolTipText(PaletteUtilities.messages.getString("snf.black"));
		black.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.black));
		black.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFPresetsPanel.this.pc.setEditedPaint(Color.black);
			}
		});
		JComponent none = new JComponent() {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				Insets i = getInsets();
				/*
				int cx = i.left + (getWidth() - i.left - i.right)/2;
				int cy = i.top + (getHeight() - i.top - i.bottom)/2;
				int s = (Math.max(getWidth(), getHeight()) + 1)/2;
				g.setColor(Color.red);
				((Graphics2D)g).setStroke(new BasicStroke(3));
				g.drawLine(cx+s, cy-s, cx-s, cy+s);
				*/
				((Graphics2D)g).setPaint(CheckerboardPaint.LIGHT);
				g.fillRect(i.left, i.top, getWidth()-i.left-i.right, getHeight()-i.top-i.bottom);
			}
		};
		none.setToolTipText(PaletteUtilities.messages.getString("snf.none"));
		none.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.black));
		none.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFPresetsPanel.this.pc.setEditedPaint(null);
			}
		});
		JLabel swapsnf = new JLabel(new ImageIcon(getToolkit().createImage(SNFPresetsPanel.class.getResource("SwapStrokeFill.png"))));
		swapsnf.setToolTipText(PaletteUtilities.messages.getString("snf.swapsnf"));
		swapsnf.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black));
		swapsnf.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Paint fp = SNFPresetsPanel.this.pc.getFillPaint();
				Composite fc = SNFPresetsPanel.this.pc.getFillComposite();
				Paint dp = SNFPresetsPanel.this.pc.getDrawPaint();
				Composite dc = SNFPresetsPanel.this.pc.getDrawComposite();
				SNFPresetsPanel.this.pc.setFillPaint(dp);
				SNFPresetsPanel.this.pc.setFillComposite(dc);
				SNFPresetsPanel.this.pc.setDrawPaint(fp);
				SNFPresetsPanel.this.pc.setDrawComposite(fc);
			}
		});
		JLabel swapfgbg = new JLabel(new ImageIcon(getToolkit().createImage(SNFPresetsPanel.class.getResource("SwapFgBg.png"))));
		swapfgbg.setToolTipText(PaletteUtilities.messages.getString("snf.swapfgbg"));
		swapfgbg.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.black));
		swapfgbg.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Paint fg = SNFPresetsPanel.this.pc.getEditedForeground();
				Paint bg = SNFPresetsPanel.this.pc.getEditedBackground();
				SNFPresetsPanel.this.pc.setEditedForeground(bg);
				SNFPresetsPanel.this.pc.setEditedBackground(fg);
			}
		});
		JPanel top = new JPanel(new GridLayout(1,3,-1,-1));
		top.add(white);
		top.add(black);
		top.add(none);
		JPanel bottom = new JPanel(new GridLayout(1,2,-1,-1));
		bottom.add(swapsnf);
		bottom.add(swapfgbg);
		JPanel main = new JPanel(new GridLayout(2,1,-1,-1));
		main.add(top);
		main.add(bottom);
		main.setMinimumSize(size);
		main.setPreferredSize(size);
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
	}
	
	public void update() {
		// nothing
	}
}
