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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import com.kreative.paint.PaintContext;
import com.kreative.paint.swing.JPopupPanel;

public class SNFPCSFAPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Dimension pSize = new Dimension(32, 18);
	private static final Dimension cSize = new Dimension(100, 18);
	private static final Dimension sSize = new Dimension(75, 18);
	private static final Dimension taSize = new Dimension(54, 18);
	private static final Dimension aaSize = new Dimension(18, 18);
	
	private PatternPalette ppal;
	private CompositePalette cpal;
	private StrokePalette spal;
	private FontPalette fpal;
	private SNFPatternSelector ps;
	private SNFCompositeSelector cs;
	private SNFStrokeSelector ss;
	private SNFFontSelector fs;
	private SNFTextAlignPanel tap;
	private SNFAntiAliasPanel aap;
	
	public SNFPCSFAPanel(PaintContext pc, PatternPalette ppal, CompositePalette cpal, StrokePalette spal, FontPalette fpal) {
		super(pc, 0);
		this.ppal = ppal;
		this.cpal = cpal;
		this.spal = spal;
		this.fpal = fpal;
		this.ps = new SNFPatternSelector(pc);
		this.cs = new SNFCompositeSelector(pc);
		this.ss = new SNFStrokeSelector(pc);
		this.fs = new SNFFontSelector(pc);
		this.tap = new SNFTextAlignPanel(pc);
		this.aap = new SNFAntiAliasPanel(pc);
		this.ps.setMinimumSize(pSize);
		this.ps.setPreferredSize(pSize);
		this.cs.setMinimumSize(cSize);
		this.cs.setPreferredSize(cSize);
		this.ss.setMinimumSize(sSize);
		this.ss.setPreferredSize(sSize);
		this.tap.setMinimumSize(taSize);
		this.tap.setPreferredSize(taSize);
		this.aap.setMinimumSize(aaSize);
		this.aap.setPreferredSize(aaSize);
		this.ps.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int px = SNFPCSFAPanel.this.ps.getLocationOnScreen().x;
					int py = SNFPCSFAPanel.this.ps.getLocationOnScreen().y + SNFPCSFAPanel.this.ps.getHeight();
					SNFPCSFAPanel.this.ppal.setLocation(px, py);
					SNFPCSFAPanel.this.ppal.setVisible(true);
				} else {
					int ph = SNFPCSFAPanel.this.ps.getHeight();
					JPopupPanel jpop = SNFPCSFAPanel.this.ppal.getPopup();
					if (jpop.isVisible()) jpop.setVisible(false);
					else jpop.show(SNFPCSFAPanel.this.ps, 0, ph);
				}
			}
		});
		this.cs.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int px = SNFPCSFAPanel.this.cs.getLocationOnScreen().x;
					int py = SNFPCSFAPanel.this.cs.getLocationOnScreen().y + SNFPCSFAPanel.this.cs.getHeight();
					SNFPCSFAPanel.this.cpal.setLocation(px, py);
					SNFPCSFAPanel.this.cpal.setVisible(true);
				} else {
					int ph = SNFPCSFAPanel.this.cs.getHeight();
					int pw = SNFPCSFAPanel.this.cs.getWidth();
					if (e.getX() < pw-32) {
						JPopupMenu jpop = SNFPCSFAPanel.this.cpal.getCompositePopup();
						if (jpop.isVisible()) jpop.setVisible(false);
						else jpop.show(SNFPCSFAPanel.this.cs, 0, ph);
					} else {
						JPopupPanel jpop = SNFPCSFAPanel.this.cpal.getAlphaPopup();
						if (jpop.isVisible()) jpop.setVisible(false);
						else jpop.show(SNFPCSFAPanel.this.cs, pw-jpop.getPreferredSize().width, ph);
					}
				}
			}
		});
		this.ss.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int px = SNFPCSFAPanel.this.ss.getLocationOnScreen().x;
					int py = SNFPCSFAPanel.this.ss.getLocationOnScreen().y + SNFPCSFAPanel.this.ss.getHeight();
					SNFPCSFAPanel.this.spal.setLocation(px, py);
					SNFPCSFAPanel.this.spal.setVisible(true);
				} else {
					int ph = SNFPCSFAPanel.this.ss.getHeight();
					int pw = SNFPCSFAPanel.this.ss.getWidth();
					if (e.getX() < 17) {
						JPopupMenu jpop = SNFPCSFAPanel.this.spal.getArrowPopup1();
						if (jpop.isVisible()) jpop.setVisible(false);
						else jpop.show(SNFPCSFAPanel.this.ss, 0, ph);
					} else if (e.getX() < pw-18) {
						JPopupMenu jpop = SNFPCSFAPanel.this.spal.getStrokePopup();
						if (jpop.isVisible()) jpop.setVisible(false);
						else jpop.show(SNFPCSFAPanel.this.ss, (pw-jpop.getPreferredSize().width)/2, ph);
					} else {
						JPopupMenu jpop = SNFPCSFAPanel.this.spal.getArrowPopup2();
						if (jpop.isVisible()) jpop.setVisible(false);
						else jpop.show(SNFPCSFAPanel.this.ss, pw-jpop.getPreferredSize().width, ph);
					}
				}
			}
		});
		this.fs.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int px = SNFPCSFAPanel.this.fs.getLocationOnScreen().x;
					int py = SNFPCSFAPanel.this.fs.getLocationOnScreen().y + SNFPCSFAPanel.this.fs.getHeight();
					SNFPCSFAPanel.this.fpal.setLocation(px, py);
					SNFPCSFAPanel.this.fpal.setVisible(true);
				} else {
					int ph = SNFPCSFAPanel.this.fs.getHeight();
					int pw = SNFPCSFAPanel.this.fs.getWidth();
					if (e.getX() < pw-24) {
						JPopupMenu jpop = SNFPCSFAPanel.this.fpal.getFontPopup();
						if (jpop.isVisible()) jpop.setVisible(false);
						else jpop.show(SNFPCSFAPanel.this.fs, 0, ph);
					} else {
						JPopupMenu jpop = SNFPCSFAPanel.this.fpal.getSizePopup();
						if (jpop.isVisible()) jpop.setVisible(false);
						else jpop.show(SNFPCSFAPanel.this.fs, pw-jpop.getPreferredSize().width, ph);
					}
				}
			}
		});
		JPanel top1 = new JPanel(new BorderLayout(3,3));
		top1.add(cs, BorderLayout.CENTER);
		top1.add(ss, BorderLayout.EAST);
		JPanel top2 = new JPanel(new BorderLayout(3,3));
		top2.add(top1, BorderLayout.CENTER);
		top2.add(ps, BorderLayout.WEST);
		JPanel bottom1 = new JPanel(new BorderLayout(3,3));
		bottom1.add(fs, BorderLayout.CENTER);
		bottom1.add(tap, BorderLayout.EAST);
		JPanel bottom2 = new JPanel(new BorderLayout(3,3));
		bottom2.add(bottom1, BorderLayout.CENTER);
		bottom2.add(aap, BorderLayout.EAST);
		JPanel main = new JPanel(new GridLayout(2,1,3,3));
		main.add(top2);
		main.add(bottom2);
		Border inner = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		Border outer = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.black);
		main.setBorder(BorderFactory.createCompoundBorder(outer, inner));
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
	}
	
	protected void pcChanged(PaintContext pc) {
		ppal.setPaintContext(pc);
		cpal.setPaintContext(pc);
		spal.setPaintContext(pc);
		fpal.setPaintContext(pc);
		ps.setPaintContext(pc);
		cs.setPaintContext(pc);
		ss.setPaintContext(pc);
		fs.setPaintContext(pc);
		tap.setPaintContext(pc);
		aap.setPaintContext(pc);
	}
	
	public void update() {
		// nothing
	}
}
