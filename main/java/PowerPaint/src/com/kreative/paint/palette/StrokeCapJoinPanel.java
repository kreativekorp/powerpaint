/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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
import javax.swing.event.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.material.stroke.EndCap;
import com.kreative.paint.material.stroke.LineJoin;
import com.kreative.paint.material.stroke.PowerStroke;
import com.kreative.paint.util.SwingUtils;

public class StrokeCapJoinPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private JSpinner width;
	private JToggleButton cb, cr, cs;
	private JToggleButton jm, jr, jb;
	private JSpinner limit;
	private boolean eventexec = false;
	
	public StrokeCapJoinPanel(PaintContext pc) {
		super(pc, CHANGED_STROKE);
		SpinnerModel widthm = new SpinnerNumberModel(1.0f, 0.0f, Float.MAX_VALUE, 1.0f);
		width = shrink(new JSpinner(widthm));
		width.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokeCapJoinPanel.this.pc != null) {
						float f = ((Number)((JSpinner)e.getSource()).getModel().getValue()).floatValue();
						Stroke s = StrokeCapJoinPanel.this.pc.getStroke();
						if (s instanceof PowerStroke) {
							PowerStroke ds = (PowerStroke)s;
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveLineWidth(f));
						} else if (s instanceof BasicStroke) {
							PowerStroke ds = PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s);
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveLineWidth(f));
						}
					}
					eventexec = false;
				}
			}
		});
		cb = squareOffButton(new JToggleButton(new ImageIcon(this.getToolkit().createImage(this.getClass().getResource("CJCapButt.png")))));
		cr = squareOffButton(new JToggleButton(new ImageIcon(this.getToolkit().createImage(this.getClass().getResource("CJCapRound.png")))));
		cs = squareOffButton(new JToggleButton(new ImageIcon(this.getToolkit().createImage(this.getClass().getResource("CJCapSquare.png")))));
		jm = squareOffButton(new JToggleButton(new ImageIcon(this.getToolkit().createImage(this.getClass().getResource("CJJoinMiter.png")))));
		jr = squareOffButton(new JToggleButton(new ImageIcon(this.getToolkit().createImage(this.getClass().getResource("CJJoinRound.png")))));
		jb = squareOffButton(new JToggleButton(new ImageIcon(this.getToolkit().createImage(this.getClass().getResource("CJJoinBevel.png")))));
		cb.setToolTipText(PaletteUtilities.messages.getString("stroke.cap.butt"));
		cr.setToolTipText(PaletteUtilities.messages.getString("stroke.cap.round"));
		cs.setToolTipText(PaletteUtilities.messages.getString("stroke.cap.square"));
		jm.setToolTipText(PaletteUtilities.messages.getString("stroke.join.miter"));
		jr.setToolTipText(PaletteUtilities.messages.getString("stroke.join.round"));
		jb.setToolTipText(PaletteUtilities.messages.getString("stroke.join.bevel"));
		ButtonGroup cg = new ButtonGroup(); cg.add(cb); cg.add(cr); cg.add(cs);
		JPanel cp = new JPanel(new GridLayout(1,3,-1,-1)); cp.add(cb); cp.add(cr); cp.add(cs);
		ButtonGroup jg = new ButtonGroup(); jg.add(jm); jg.add(jr); jg.add(jb);
		JPanel jp = new JPanel(new GridLayout(1,3,-1,-1)); jp.add(jm); jp.add(jr); jp.add(jb);
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokeCapJoinPanel.this.pc != null) {
						Stroke s = StrokeCapJoinPanel.this.pc.getStroke();
						if (s instanceof PowerStroke) {
							PowerStroke ds = (PowerStroke)s;
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveEndCap(EndCap.BUTT));
						} else if (s instanceof BasicStroke) {
							PowerStroke ds = PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s);
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveEndCap(EndCap.BUTT));
						}
					}
					eventexec = false;
				}
			}
		});
		cr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokeCapJoinPanel.this.pc != null) {
						Stroke s = StrokeCapJoinPanel.this.pc.getStroke();
						if (s instanceof PowerStroke) {
							PowerStroke ds = (PowerStroke)s;
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveEndCap(EndCap.ROUND));
						} else if (s instanceof BasicStroke) {
							PowerStroke ds = PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s);
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveEndCap(EndCap.ROUND));
						}
					}
					eventexec = false;
				}
			}
		});
		cs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokeCapJoinPanel.this.pc != null) {
						Stroke s = StrokeCapJoinPanel.this.pc.getStroke();
						if (s instanceof PowerStroke) {
							PowerStroke ds = (PowerStroke)s;
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveEndCap(EndCap.SQUARE));
						} else if (s instanceof BasicStroke) {
							PowerStroke ds = PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s);
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveEndCap(EndCap.SQUARE));
						}
					}
					eventexec = false;
				}
			}
		});
		jm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokeCapJoinPanel.this.pc != null) {
						Stroke s = StrokeCapJoinPanel.this.pc.getStroke();
						if (s instanceof PowerStroke) {
							PowerStroke ds = (PowerStroke)s;
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveLineJoin(LineJoin.MITER));
						} else if (s instanceof BasicStroke) {
							PowerStroke ds = PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s);
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveLineJoin(LineJoin.MITER));
						}
					}
					eventexec = false;
				}
			}
		});
		jr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokeCapJoinPanel.this.pc != null) {
						Stroke s = StrokeCapJoinPanel.this.pc.getStroke();
						if (s instanceof PowerStroke) {
							PowerStroke ds = (PowerStroke)s;
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveLineJoin(LineJoin.ROUND));
						} else if (s instanceof BasicStroke) {
							PowerStroke ds = PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s);
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveLineJoin(LineJoin.ROUND));
						}
					}
					eventexec = false;
				}
			}
		});
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokeCapJoinPanel.this.pc != null) {
						Stroke s = StrokeCapJoinPanel.this.pc.getStroke();
						if (s instanceof PowerStroke) {
							PowerStroke ds = (PowerStroke)s;
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveLineJoin(LineJoin.BEVEL));
						} else if (s instanceof BasicStroke) {
							PowerStroke ds = PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s);
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveLineJoin(LineJoin.BEVEL));
						}
					}
					eventexec = false;
				}
			}
		});
		SpinnerModel limitm = new SpinnerNumberModel(10.0f, 0.0f, Float.MAX_VALUE, 1.0f);
		limit = shrink(new JSpinner(limitm));
		limit.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokeCapJoinPanel.this.pc != null) {
						float f = ((Number)((JSpinner)e.getSource()).getModel().getValue()).floatValue();
						Stroke s = StrokeCapJoinPanel.this.pc.getStroke();
						if (s instanceof PowerStroke) {
							PowerStroke ds = (PowerStroke)s;
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveMiterLimit(f));
						} else if (s instanceof BasicStroke) {
							PowerStroke ds = PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s);
							StrokeCapJoinPanel.this.pc.setStroke(ds.deriveMiterLimit(f));
						}
					}
					eventexec = false;
				}
			}
		});
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(Box.createHorizontalGlue());
		add(SwingUtils.shrink(new JLabel(PaletteUtilities.messages.getString("stroke.width"))));
		add(Box.createHorizontalStrut(4));
		add(width);
		add(Box.createHorizontalStrut(4));
		add(cp);
		add(Box.createHorizontalStrut(4));
		add(jp);
		add(Box.createHorizontalStrut(4));
		add(SwingUtils.shrink(new JLabel(PaletteUtilities.messages.getString("stroke.miterlimit"))));
		add(Box.createHorizontalStrut(4));
		add(limit);
		add(Box.createHorizontalGlue());
		update();
	}
	
	public void update() {
		if (!eventexec) {
			eventexec = true;
			if (pc != null) {
				Stroke s = pc.getStroke();
				if (s instanceof PowerStroke) {
					PowerStroke ds = (PowerStroke)s;
					width.setValue((double)ds.lineWidth);
					cb.setSelected(ds.endCap == EndCap.BUTT);
					cr.setSelected(ds.endCap == EndCap.ROUND);
					cs.setSelected(ds.endCap == EndCap.SQUARE);
					jm.setSelected(ds.lineJoin == LineJoin.MITER);
					jr.setSelected(ds.lineJoin == LineJoin.ROUND);
					jb.setSelected(ds.lineJoin == LineJoin.BEVEL);
					limit.setValue((double)ds.miterLimit);
				} else if (s instanceof BasicStroke) {
					BasicStroke bs = (BasicStroke)s;
					width.setValue((double)bs.getLineWidth());
					cb.setSelected(bs.getEndCap() == BasicStroke.CAP_BUTT);
					cr.setSelected(bs.getEndCap() == BasicStroke.CAP_ROUND);
					cs.setSelected(bs.getEndCap() == BasicStroke.CAP_SQUARE);
					jm.setSelected(bs.getLineJoin() == BasicStroke.JOIN_MITER);
					jr.setSelected(bs.getLineJoin() == BasicStroke.JOIN_ROUND);
					jb.setSelected(bs.getLineJoin() == BasicStroke.JOIN_BEVEL);
					limit.setValue((double)bs.getMiterLimit());
				}
			}
			eventexec = false;
		}
	}
	
	private static <C extends JComponent> C squareOffButton(C c) {
		int h = c.getPreferredSize().height;
		c.setMinimumSize(new Dimension(h,h));
		c.setPreferredSize(new Dimension(h,h));
		c.setMaximumSize(new Dimension(h,h));
		return c;
	}
	
	private static JSpinner shrink(JSpinner s) {
		SwingUtils.shrink(s);
		s.setMinimumSize(new Dimension(80, s.getMinimumSize().height));
		s.setPreferredSize(new Dimension(80, s.getPreferredSize().height));
		s.setMaximumSize(new Dimension(80, s.getMaximumSize().height));
		return s;
	}
}
