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
import com.kreative.paint.gradient.GradientPaint2;
import com.kreative.paint.swing.JPopupPanel;

public class SNFPaintPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Dimension psSize = new Dimension(33, 43);
	
	private ColorPalettePalette cp;
	private PaintContextPalette[] ocp;
	private TexturePalette tp;
	private GradientPalette gp;
	private SNFPaintSelector<Color> cps;
	private SNFPaintSelector<TexturePaint> tps;
	private SNFPaintSelector<GradientPaint2> gps;
	
	public SNFPaintPanel(PaintContext pc, ColorPalettePalette colorPalette, PaintContextPalette[] otherColorPalettes, TexturePalette texturePalette, GradientPalette gradientPalette) {
		super(pc, 0);
		cp = colorPalette;
		ocp = otherColorPalettes;
		tp = texturePalette;
		gp = gradientPalette;
		cps = new SNFPaintSelector<Color>(pc, Color.class, colorPalette.getColor(), colorPalette.getColor());
		tps = new SNFPaintSelector<TexturePaint>(pc, TexturePaint.class, texturePalette.getTexture(), texturePalette.getTexture());
		gps = new SNFPaintSelector<GradientPaint2>(pc, GradientPaint2.class, new GradientPaint2(gradientPalette.getGradient()), new GradientPaint2(gradientPalette.getGradient()));
		cps.setMinimumSize(psSize);
		cps.setPreferredSize(psSize);
		tps.setMinimumSize(psSize);
		tps.setPreferredSize(psSize);
		gps.setMinimumSize(psSize);
		gps.setPreferredSize(psSize);
		cps.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				switch (cps.getClickedArea(e.getX(), e.getY())) {
				case 1: cps.setToolTipText(PaletteUtilities.messages.getString("snf.fgcolor")); break;
				case 2: cps.setToolTipText(PaletteUtilities.messages.getString("snf.bgcolor")); break;
				default: cps.setToolTipText(null);
				}
			}
		});
		tps.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				switch (tps.getClickedArea(e.getX(), e.getY())) {
				case 1: tps.setToolTipText(PaletteUtilities.messages.getString("snf.fgtexture")); break;
				case 2: tps.setToolTipText(PaletteUtilities.messages.getString("snf.bgtexture")); break;
				default: tps.setToolTipText(null);
				}
			}
		});
		gps.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				switch (gps.getClickedArea(e.getX(), e.getY())) {
				case 1: gps.setToolTipText(PaletteUtilities.messages.getString("snf.fggradient")); break;
				case 2: gps.setToolTipText(PaletteUtilities.messages.getString("snf.bggradient")); break;
				default: gps.setToolTipText(null);
				}
			}
		});
		cps.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isMetaDown() || e.isControlDown() || e.getButton() == MouseEvent.BUTTON3) {
					final int px = cps.getLocationOnScreen().x;
					final int py = cps.getLocationOnScreen().y + cps.getHeight();
					final int ca = cps.getClickedArea(e.getX(), e.getY());
					if (ca == 1 || ca == 2) {
						JPopupMenu pop = new JPopupMenu();
						JMenuItem fmi = new JMenuItem(cp.getTitle());
						fmi.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								SNFPaintPanel.this.pc.setEditingBackground(ca == 2);
								cp.setLocation(px, py);
								cp.setVisible(true);
							}
						});
						pop.add(fmi);
						for (final PaintContextPalette icp : ocp) {
							JMenuItem nmi = new JMenuItem(icp.getTitle());
							nmi.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									SNFPaintPanel.this.pc.setEditingBackground(ca == 2);
									icp.setLocation(px, py);
									icp.setVisible(true);
								}
							});
							pop.add(nmi);
						}
						pop.show(cps, e.getX(), e.getY());
					}
				} else if (e.getClickCount() > 1) {
					int px = cps.getLocationOnScreen().x;
					int py = cps.getLocationOnScreen().y + cps.getHeight();
					switch (cps.getClickedArea(e.getX(), e.getY())) {
					case 1:
						SNFPaintPanel.this.pc.setEditingForeground(true);
						cp.setLocation(px, py);
						cp.setVisible(true);
						break;
					case 2:
						SNFPaintPanel.this.pc.setEditingBackground(true);
						cp.setLocation(px, py);
						cp.setVisible(true);
						break;
					}
				} else if (cps.doClick(e.getX(), e.getY())) {
					JPopupPanel jpop = cp.getPopup();
					if (jpop.isVisible()) jpop.setVisible(false);
					else jpop.show(cps, 0, cps.getHeight());
				}
			}
		});
		tps.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int px = tps.getLocationOnScreen().x;
					int py = tps.getLocationOnScreen().y + tps.getHeight();
					switch (tps.getClickedArea(e.getX(), e.getY())) {
					case 1:
						SNFPaintPanel.this.pc.setEditingForeground(true);
						tp.setLocation(px, py);
						tp.setVisible(true);
						break;
					case 2:
						SNFPaintPanel.this.pc.setEditingBackground(true);
						tp.setLocation(px, py);
						tp.setVisible(true);
						break;
					}
				} else if (tps.doClick(e.getX(), e.getY())) {
					JPopupPanel jpop = tp.getPopup();
					if (jpop.isVisible()) jpop.setVisible(false);
					else jpop.show(tps, 0, tps.getHeight());
				}
			}
		});
		gps.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					int px = gps.getLocationOnScreen().x;
					int py = gps.getLocationOnScreen().y + gps.getHeight();
					switch (gps.getClickedArea(e.getX(), e.getY())) {
					case 1:
						SNFPaintPanel.this.pc.setEditingForeground(true);
						gp.setLocation(px, py);
						gp.setVisible(true);
						break;
					case 2:
						SNFPaintPanel.this.pc.setEditingBackground(true);
						gp.setLocation(px, py);
						gp.setVisible(true);
						break;
					}
				} else if (gps.doClick(e.getX(), e.getY())) {
					boolean usePresets = (e.isMetaDown() || e.isControlDown() || e.getButton() == MouseEvent.BUTTON3);
					JPopupPanel jpop = gp.getPopup(usePresets);
					if (jpop.isVisible()) jpop.setVisible(false);
					else jpop.show(gps, 0, gps.getHeight());
				}
			}
		});
		JPanel main = new JPanel(new GridLayout(1,3,2,2));
		main.add(cps);
		main.add(tps);
		main.add(gps);
		main.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 8));
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
	}
	
	protected void pcChanged(PaintContext pc) {
		cp.setPaintContext(pc);
		for (PaintContextPalette icp : ocp) {
			icp.setPaintContext(pc);
		}
		tp.setPaintContext(pc);
		gp.setPaintContext(pc);
		cps.setPaintContext(pc);
		tps.setPaintContext(pc);
		gps.setPaintContext(pc);
	}
	
	public void update() {
		// nothing
	}
}
