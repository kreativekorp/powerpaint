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
import com.kreative.paint.PaintContextListener;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.swing.JPopupPanel;
import com.kreative.paint.util.SwingUtils;

public class CompositePanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private static final String[] ruleNames = {
		"composite.Src",
		"composite.SrcOver",
		"composite.SrcIn",
		"composite.SrcOut",
		"composite.SrcAtop",
		"composite.Xor",
		"composite.Dst",
		"composite.DstOver",
		"composite.DstIn",
		"composite.DstOut",
		"composite.DstAtop",
		"composite.Clear",
	};
	private static final int[] rules = {
		AlphaComposite.SRC,
		AlphaComposite.SRC_OVER,
		AlphaComposite.SRC_IN,
		AlphaComposite.SRC_OUT,
		AlphaComposite.SRC_ATOP,
		AlphaComposite.XOR,
		AlphaComposite.DST,
		AlphaComposite.DST_OVER,
		AlphaComposite.DST_IN,
		AlphaComposite.DST_OUT,
		AlphaComposite.DST_ATOP,
		AlphaComposite.CLEAR,
	};
	
	private DefaultListModel cxList;
	private JList cxView;
	private JLabel opLabel;
	private JSlider opSlider;
	private JLabel opPcLabel;
	private JPopupMenu cpop;
	private JPopupPanel apop;
	private boolean eventexec = false;
	
	public CompositePanel(PaintContext pc) {
		super(pc, CHANGED_COMPOSITE|CHANGED_EDITING);
		cxList = new DefaultListModel();
		for (String rule : ruleNames) {
			cxList.addElement(" "+PaletteUtilities.messages.getString(rule)+" ");
		}
		cxView = new JList(cxList);
		cxView.setLayoutOrientation(JList.VERTICAL_WRAP);
		cxView.setVisibleRowCount((rules.length+3)/4);
		cxView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (CompositePanel.this.pc != null) {
						int rule = rules[cxView.getSelectedIndex()];
						float alpha = opSlider.getValue()/100.0f;
						AlphaComposite cx = AlphaComposite.getInstance(rule, alpha);
						CompositePanel.this.pc.setEditedComposite(cx);
					}
					eventexec = false;
				}
			}
		});
		opLabel = SwingUtils.shrink(new JLabel(PaletteUtilities.messages.getString("composite.Opacity")));
		opSlider = SwingUtils.shrink(new JSlider(0, 100, 100));
		opPcLabel = SwingUtils.shrink(new JLabel("100%"));
		opSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!eventexec) {
					eventexec = true;
					opPcLabel.setText((opSlider.getValue()+"%    ").substring(0,4));
					if (CompositePanel.this.pc != null) {
						int rule = rules[cxView.getSelectedIndex()];
						float alpha = opSlider.getValue()/100.0f;
						AlphaComposite cx = AlphaComposite.getInstance(rule, alpha);
						CompositePanel.this.pc.setEditedComposite(cx);
					}
					eventexec = false;
				}
			}
		});
		JPanel sliderPanel = new JPanel(new BorderLayout());
		sliderPanel.add(opLabel, BorderLayout.LINE_START);
		sliderPanel.add(opSlider, BorderLayout.CENTER);
		sliderPanel.add(opPcLabel, BorderLayout.LINE_END);
		sliderPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		sliderPanel.setPreferredSize(new Dimension(sliderPanel.getPreferredSize().width, 20));
		setLayout(new BorderLayout());
		add(cxView, BorderLayout.CENTER);
		add(sliderPanel, BorderLayout.SOUTH);
		
		cpop = new CompositePopup();
		apop = new AlphaPopup();
		
		update();
	}
	
	public void update() {
		if (!eventexec) {
			eventexec = true;
			if (pc != null) {
				Composite cx = pc.getEditedComposite();
				if (cx instanceof AlphaComposite) {
					int rule = ((AlphaComposite)cx).getRule();
					float alpha = ((AlphaComposite)cx).getAlpha();
					cxView.clearSelection();
					for (int i = 0; i < rules.length; i++) {
						if (rule == rules[i]) {
							cxView.setSelectedIndex(i);
							break;
						}
					}
					opSlider.setValue((int)(alpha*100));
					opPcLabel.setText(((int)(alpha*100) + "%    ").substring(0,4));
				}
			}
			eventexec = false;
		}
	}
	
	public JPopupMenu getCompositePopup() {
		return cpop;
	}
	
	public JPopupPanel getAlphaPopup() {
		return apop;
	}
	
	private class CompositePopup extends JPopupMenu implements PaintContextListener {
		private static final long serialVersionUID = 1L;
		public CompositePopup() {
			Composite cx = CompositePanel.this.pc.getEditedComposite();
			AlphaComposite acx = (cx instanceof AlphaComposite) ? (AlphaComposite)cx : null;
			for (int i = 0; i < rules.length; i++) {
				JMenuItem mi = new JCheckBoxMenuItem(PaletteUtilities.messages.getString(ruleNames[i]));
				mi.setSelected(acx != null && acx.getRule() == rules[i]);
				final int rule = rules[i];
				mi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Composite cx = CompositePanel.this.pc.getEditedComposite();
						AlphaComposite acx = (cx instanceof AlphaComposite) ? (AlphaComposite)cx : null;
						CompositePanel.this.pc.setEditedComposite(AlphaComposite.getInstance(rule, (acx != null) ? acx.getAlpha() : 1.0f));
					}
				});
				add(mi);
			}
			CompositePanel.this.pc.addPaintContextListener(CompositePopup.this);
		}
		public void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {}
		public void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
			if ((delta & (PaintContext.CHANGED_COMPOSITE | PaintContext.CHANGED_EDITING)) != 0) {
				Composite cx = src.getEditedComposite();
				AlphaComposite acx = (cx instanceof AlphaComposite) ? (AlphaComposite)cx : null;
				String sel = "";
				for (int i = 0; i < rules.length; i++) {
					if (acx != null && acx.getRule() == rules[i]) {
						sel = PaletteUtilities.messages.getString(ruleNames[i]);
					}
				}
				setChecked(CompositePopup.this, sel);
			}
		}
		private void setChecked(MenuElement mi, String sel) {
			if (mi instanceof JMenuItem) {
				((JMenuItem)mi).setSelected(((JMenuItem)mi).getText().equals(sel));
			}
			for (MenuElement smi : mi.getSubElements()) {
				setChecked(smi, sel);
			}
		}
	}
	
	private class AlphaPopup extends JPopupPanel implements PaintContextListener {
		private static final long serialVersionUID = 1L;
		private JLabel opLabel;
		private JSlider opSlider;
		private JLabel opPcLabel;
		private boolean eventexec = false;
		public AlphaPopup() {
			opLabel = SwingUtils.shrink(new JLabel(PaletteUtilities.messages.getString("composite.Opacity")));
			opSlider = SwingUtils.shrink(new JSlider(0, 100, 100));
			opPcLabel = SwingUtils.shrink(new JLabel("100%"));
			opSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (!eventexec) {
						eventexec = true;
						opPcLabel.setText((opSlider.getValue()+"%    ").substring(0,4));
						if (CompositePanel.this.pc != null) {
							int rule = rules[cxView.getSelectedIndex()];
							float alpha = opSlider.getValue()/100.0f;
							AlphaComposite cx = AlphaComposite.getInstance(rule, alpha);
							CompositePanel.this.pc.setEditedComposite(cx);
						}
						eventexec = false;
					}
				}
			});
			JPanel sliderPanel = new JPanel(new BorderLayout());
			sliderPanel.add(opLabel, BorderLayout.LINE_START);
			sliderPanel.add(opSlider, BorderLayout.CENTER);
			sliderPanel.add(opPcLabel, BorderLayout.LINE_END);
			sliderPanel.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
			setLayout(new BorderLayout());
			add(sliderPanel, BorderLayout.CENTER);
			hideOnRelease(opSlider);
			setResizable(false);
			pack();
			CompositePanel.this.pc.addPaintContextListener(AlphaPopup.this);
		}
		public void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {}
		public void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
			if ((delta & (PaintContext.CHANGED_COMPOSITE | PaintContext.CHANGED_EDITING)) != 0) {
				if (!eventexec) {
					eventexec = true;
					if (pc != null) {
						Composite cx = pc.getEditedComposite();
						if (cx instanceof AlphaComposite) {
							float alpha = ((AlphaComposite)cx).getAlpha();
							opSlider.setValue((int)(alpha*100));
							opPcLabel.setText(((int)(alpha*100) + "%    ").substring(0,4));
						}
					}
					eventexec = false;
				}
			}
		}
	}
}
