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
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.PaintContextListener;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.material.stroke.Arrowhead;
import com.kreative.paint.material.stroke.EndCap;
import com.kreative.paint.material.stroke.PowerStroke;
import com.kreative.paint.res.MaterialsManager;

public class StrokePresetPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	private static final int ROW_HEIGHT = 16;
	private static final int ROW_WIDTH = 100;
	private static final int ARROW_OFFSET = 16;
	
	private TreeSet<Float> widths;
	private TreeSet<Integer> mults;
	private LinkedHashSet<float[]> dashes;
	private LinkedHashSet<Arrowhead> arrows;
	private JList widthView;
	private JList multView;
	private JList dashView;
	private JList arrowView1;
	private JList arrowView2;
	private JPopupMenu strokePopup;
	private JPopupMenu arrowPopup1;
	private JPopupMenu arrowPopup2;
	private boolean eventexec = false;
	
	public StrokePresetPanel(PaintContext pc, MaterialsManager mm) {
		super(pc, CHANGED_STROKE);
		widths = mm.getLineWidths();
		mults = mm.getLineMultiplicies();
		dashes = mm.getLineDashes();
		arrows = mm.getLineArrowheads();
		widthView = new JList(widths.toArray(new Float[0]));
		multView = new JList(mults.toArray(new Integer[0]));
		dashView = new JList(dashes.toArray(new float[0][]));
		arrowView1 = new JList(arrows.toArray(new Arrowhead[0]));
		arrowView2 = new JList(arrows.toArray(new Arrowhead[0]));
		widthView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		multView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dashView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		arrowView1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		arrowView2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		widthView.setVisibleRowCount(10);
		multView.setVisibleRowCount(10);
		dashView.setVisibleRowCount(10);
		arrowView1.setVisibleRowCount(10);
		arrowView2.setVisibleRowCount(10);
		widthView.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
				JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, sel, focus);
				l.setText(null);
				l.setBorder(BorderFactory.createEmptyBorder());
				l.setOpaque(true);
				l.setBackground(new Color((sel ? SystemColor.textHighlight : SystemColor.text).getRGB(), true));
				l.setToolTipText(PaletteUtilities.messages.getString("stroke.tooltip.width").replace("$", value.toString().replaceFirst("\\.0$", "")));
				BufferedImage bi = new BufferedImage(Math.max(ROW_WIDTH, list.getWidth()), ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.setPaint(new Color((sel ? SystemColor.textHighlightText : SystemColor.textText).getRGB(), true));
				g.setStroke(PowerStroke.DEFAULT.deriveLineWidth(((Number)value).floatValue()));
				g.drawLine(0, bi.getHeight()/2, bi.getWidth()-1, bi.getHeight()/2);
				g.dispose();
				l.setIcon(new ImageIcon(bi));
				return l;
			}
		});
		multView.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
				JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, sel, focus);
				l.setText(null);
				l.setBorder(BorderFactory.createEmptyBorder());
				l.setOpaque(true);
				l.setBackground(new Color((sel ? SystemColor.textHighlight : SystemColor.text).getRGB(), true));
				l.setToolTipText(PaletteUtilities.messages.getString("stroke.tooltip.multiplicity").replace("$", value.toString()));
				BufferedImage bi = new BufferedImage(Math.max(ROW_WIDTH, list.getWidth()), ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.setPaint(new Color((sel ? SystemColor.textHighlightText : SystemColor.textText).getRGB(), true));
				g.setStroke(PowerStroke.DEFAULT.deriveMultiplicity(((Number)value).intValue()));
				g.drawLine(0, bi.getHeight()/2, bi.getWidth()-1, bi.getHeight()/2);
				g.dispose();
				l.setIcon(new ImageIcon(bi));
				return l;
			}
		});
		dashView.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
				JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, sel, focus);
				l.setText(null);
				l.setBorder(BorderFactory.createEmptyBorder());
				l.setOpaque(true);
				l.setBackground(new Color((sel ? SystemColor.textHighlight : SystemColor.text).getRGB(), true));
				BufferedImage bi = new BufferedImage(Math.max(ROW_WIDTH, list.getWidth()), ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.setPaint(new Color((sel ? SystemColor.textHighlightText : SystemColor.textText).getRGB(), true));
				g.setStroke(PowerStroke.DEFAULT.deriveEndCap(EndCap.BUTT).deriveDashArray((float[])value));
				g.drawLine(0, bi.getHeight()/2, bi.getWidth()-1, bi.getHeight()/2);
				g.dispose();
				l.setIcon(new ImageIcon(bi));
				return l;
			}
		});
		arrowView1.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
				JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, sel, focus);
				l.setText(null);
				l.setBorder(BorderFactory.createEmptyBorder());
				l.setOpaque(true);
				l.setBackground(new Color((sel ? SystemColor.textHighlight : SystemColor.text).getRGB(), true));
				BufferedImage bi = new BufferedImage(Math.max(ROW_WIDTH, list.getWidth()), ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.setPaint(new Color((sel ? SystemColor.textHighlightText : SystemColor.textText).getRGB(), true));
				g.setStroke(PowerStroke.DEFAULT.deriveArrowOnStart((Arrowhead)value));
				g.drawLine(ARROW_OFFSET, bi.getHeight()/2, bi.getWidth()-1, bi.getHeight()/2);
				g.dispose();
				l.setIcon(new ImageIcon(bi));
				return l;
			}
		});
		arrowView2.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
				JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, sel, focus);
				l.setText(null);
				l.setBorder(BorderFactory.createEmptyBorder());
				l.setOpaque(true);
				l.setBackground(new Color((sel ? SystemColor.textHighlight : SystemColor.text).getRGB(), true));
				BufferedImage bi = new BufferedImage(Math.max(ROW_WIDTH, list.getWidth()), ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.setPaint(new Color((sel ? SystemColor.textHighlightText : SystemColor.textText).getRGB(), true));
				g.setStroke(PowerStroke.DEFAULT.deriveArrowOnEnd((Arrowhead)value));
				g.drawLine(0, bi.getHeight()/2, bi.getWidth()-1-ARROW_OFFSET, bi.getHeight()/2);
				g.dispose();
				l.setIcon(new ImageIcon(bi));
				return l;
			}
		});
		widthView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokePresetPanel.this.pc != null) {
						Stroke s = StrokePresetPanel.this.pc.getStroke();
						if (s instanceof PowerStroke || s instanceof BasicStroke) {
							PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
							float width = ((Number)widthView.getSelectedValue()).floatValue();
							StrokePresetPanel.this.pc.setStroke(ds.deriveLineWidth(width));
						}
					}
					eventexec = false;
				}
			}
		});
		multView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokePresetPanel.this.pc != null) {
						Stroke s = StrokePresetPanel.this.pc.getStroke();
						if (s instanceof PowerStroke || s instanceof BasicStroke) {
							PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
							int mult = ((Number)multView.getSelectedValue()).intValue();
							StrokePresetPanel.this.pc.setStroke(ds.deriveMultiplicity(mult));
						}
					}
					eventexec = false;
				}
			}
		});
		dashView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokePresetPanel.this.pc != null) {
						Stroke s = StrokePresetPanel.this.pc.getStroke();
						if (s instanceof PowerStroke || s instanceof BasicStroke) {
							PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
							float[] dash = (float[])dashView.getSelectedValue();
							StrokePresetPanel.this.pc.setStroke(ds.deriveDashArray(dash));
						}
					}
					eventexec = false;
				}
			}
		});
		arrowView1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokePresetPanel.this.pc != null) {
						Stroke s = StrokePresetPanel.this.pc.getStroke();
						if (s instanceof PowerStroke || s instanceof BasicStroke) {
							PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
							Arrowhead a = (Arrowhead)arrowView1.getSelectedValue();
							StrokePresetPanel.this.pc.setStroke(ds.deriveArrowOnStart(a));
						}
					}
					eventexec = false;
				}
			}
		});
		arrowView2.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (StrokePresetPanel.this.pc != null) {
						Stroke s = StrokePresetPanel.this.pc.getStroke();
						if (s instanceof PowerStroke || s instanceof BasicStroke) {
							PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
							Arrowhead a = (Arrowhead)arrowView2.getSelectedValue();
							StrokePresetPanel.this.pc.setStroke(ds.deriveArrowOnEnd(a));
						}
					}
					eventexec = false;
				}
			}
		});
		setLayout(new GridLayout(1,0));
		add(new JScrollPane(widthView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		//add(new JScrollPane(multView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		add(new JScrollPane(dashView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		add(new JScrollPane(arrowView1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		add(new JScrollPane(arrowView2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		strokePopup = new StrokePopup();
		arrowPopup1 = new ArrowPopup(false);
		arrowPopup2 = new ArrowPopup(true);
		update();
	}
	
	public void update() {
		if (!eventexec) {
			eventexec = true;
			if (pc != null) {
				widthView.clearSelection();
				multView.clearSelection();
				dashView.clearSelection();
				arrowView1.clearSelection();
				arrowView2.clearSelection();
				Stroke s = pc.getStroke();
				if (s instanceof PowerStroke) {
					PowerStroke ds = (PowerStroke)s;
					widthView.setSelectedValue(ds.lineWidth, true);
					multView.setSelectedValue(ds.multiplicity, true);
					{
						ListModel m = dashView.getModel();
						for (int i = 0; i < m.getSize(); i++) {
							if (Arrays.equals((float[])m.getElementAt(i), ds.dashArray)) {
								dashView.setSelectedIndex(i);
								dashView.ensureIndexIsVisible(i);
								break;
							}
						}
					}
					if (ds.arrowOnStart == null) {
						ListModel m = arrowView1.getModel();
						for (int i = 0; i < m.getSize(); i++) {
							if (m.getElementAt(i) == null) {
								arrowView1.setSelectedIndex(i);
								arrowView1.ensureIndexIsVisible(i);
								break;
							}
						}
					} else {
						arrowView1.setSelectedValue(ds.arrowOnStart, true);
					}
					if (ds.arrowOnEnd == null) {
						ListModel m = arrowView2.getModel();
						for (int i = 0; i < m.getSize(); i++) {
							if (m.getElementAt(i) == null) {
								arrowView2.setSelectedIndex(i);
								arrowView2.ensureIndexIsVisible(i);
								break;
							}
						}
					} else {
						arrowView2.setSelectedValue(ds.arrowOnEnd, true);
					}
				} else if (s instanceof BasicStroke) {
					BasicStroke bs = (BasicStroke)s;
					widthView.setSelectedValue(bs.getLineWidth(), true);
					multView.setSelectedValue(1, true);
					if (bs.getDashArray() == null) {
						ListModel m = dashView.getModel();
						for (int i = 0; i < m.getSize(); i++) {
							if (m.getElementAt(i) == null) {
								dashView.setSelectedIndex(i);
								dashView.ensureIndexIsVisible(i);
								break;
							}
						}
					} else {
						dashView.setSelectedValue(bs.getDashArray(), true);
					}
					ListModel m;
					m = arrowView1.getModel();
					for (int i = 0; i < m.getSize(); i++) {
						if (m.getElementAt(i) == null) {
							arrowView1.setSelectedIndex(i);
							arrowView1.ensureIndexIsVisible(i);
							break;
						}
					}
					m = arrowView2.getModel();
					for (int i = 0; i < m.getSize(); i++) {
						if (m.getElementAt(i) == null) {
							arrowView2.setSelectedIndex(i);
							arrowView2.ensureIndexIsVisible(i);
							break;
						}
					}
				}
			}
			eventexec = false;
		}
		repaint();
	}
	
	public JPopupMenu getStrokePopup() {
		return strokePopup;
	}
	
	public JPopupMenu getArrowPopup1() {
		return arrowPopup1;
	}
	
	public JPopupMenu getArrowPopup2() {
		return arrowPopup2;
	}
	
	private class WidthMenuItem extends JCheckBoxMenuItem implements PaintContextListener {
		private static final long serialVersionUID = 1L;
		private float width;
		public WidthMenuItem(float width) {
			this.width = width;
			BufferedImage bi = new BufferedImage(ROW_WIDTH, ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setPaint(Color.black);
			g.setStroke(PowerStroke.DEFAULT.deriveLineWidth(width));
			g.drawLine(0, bi.getHeight()/2, bi.getWidth()-1, bi.getHeight()/2);
			g.dispose();
			setIcon(new ImageIcon(bi));
			setText(null);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (StrokePresetPanel.this.pc != null) {
						Stroke s = StrokePresetPanel.this.pc.getStroke();
						if (s instanceof PowerStroke || s instanceof BasicStroke) {
							PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
							StrokePresetPanel.this.pc.setStroke(ds.deriveLineWidth(WidthMenuItem.this.width));
						}
					}
				}
			});
			StrokePresetPanel.this.pc.addPaintContextListener(WidthMenuItem.this);
			paintSettingsChanged(StrokePresetPanel.this.pc, StrokePresetPanel.this.pc.getPaintSettings(), -1);
		}
		public void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {}
		public void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
			if ((delta & CHANGED_STROKE) != 0) {
				Stroke s = ps.getStroke();
				if (s instanceof PowerStroke) {
					WidthMenuItem.this.setSelected(((PowerStroke)s).lineWidth == width);
				} else if (s instanceof BasicStroke) {
					WidthMenuItem.this.setSelected(((BasicStroke)s).getLineWidth() == width);
				} else {
					WidthMenuItem.this.setSelected(false);
				}
			}
		}
	}
	
	private class DashMenuItem extends JCheckBoxMenuItem implements PaintContextListener {
		private static final long serialVersionUID = 1L;
		private float[] dash;
		public DashMenuItem(float[] dash) {
			this.dash = dash;
			BufferedImage bi = new BufferedImage(ROW_WIDTH, ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setPaint(Color.black);
			g.setStroke(PowerStroke.DEFAULT.deriveEndCap(EndCap.BUTT).deriveDashArray(dash));
			g.drawLine(0, bi.getHeight()/2, bi.getWidth()-1, bi.getHeight()/2);
			g.dispose();
			setIcon(new ImageIcon(bi));
			setText(null);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Stroke s = StrokePresetPanel.this.pc.getStroke();
					if (s instanceof PowerStroke || s instanceof BasicStroke) {
						PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
						StrokePresetPanel.this.pc.setStroke(ds.deriveDashArray(DashMenuItem.this.dash));
					}
				}
			});
			StrokePresetPanel.this.pc.addPaintContextListener(DashMenuItem.this);
			paintSettingsChanged(StrokePresetPanel.this.pc, StrokePresetPanel.this.pc.getPaintSettings(), -1);
		}
		public void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {}
		public void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
			if ((delta & CHANGED_STROKE) != 0) {
				Stroke s = ps.getStroke();
				if (s instanceof PowerStroke) {
					DashMenuItem.this.setSelected(Arrays.equals(((PowerStroke)s).dashArray, dash));
				} else if (s instanceof BasicStroke) {
					DashMenuItem.this.setSelected(Arrays.equals(((BasicStroke)s).getDashArray(), dash));
				} else {
					DashMenuItem.this.setSelected(false);
				}
			}
		}
	}
	
	private class ArrowMenuItem extends JCheckBoxMenuItem implements PaintContextListener {
		private static final long serialVersionUID = 1L;
		private Arrowhead arrow;
		private boolean right;
		private ArrowMenuItem(Arrowhead arrow, boolean right) {
			this.arrow = arrow;
			this.right = right;
			BufferedImage bi = new BufferedImage(ROW_WIDTH, ROW_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setPaint(Color.black);
			if (right) {
				g.setStroke(PowerStroke.DEFAULT.deriveArrowOnEnd(arrow));
				g.drawLine(0, bi.getHeight()/2, bi.getWidth()-1-ARROW_OFFSET, bi.getHeight()/2);
			} else {
				g.setStroke(PowerStroke.DEFAULT.deriveArrowOnStart(arrow));
				g.drawLine(ARROW_OFFSET, bi.getHeight()/2, bi.getWidth()-1, bi.getHeight()/2);
			}
			g.dispose();
			setIcon(new ImageIcon(bi));
			setText(null);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Stroke s = StrokePresetPanel.this.pc.getStroke();
					if (s instanceof PowerStroke || s instanceof BasicStroke) {
						PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
						if (ArrowMenuItem.this.right) {
							StrokePresetPanel.this.pc.setStroke(ds.deriveArrowOnEnd(ArrowMenuItem.this.arrow));
						} else {
							StrokePresetPanel.this.pc.setStroke(ds.deriveArrowOnStart(ArrowMenuItem.this.arrow));
						}
					}
				}
			});
			StrokePresetPanel.this.pc.addPaintContextListener(ArrowMenuItem.this);
			paintSettingsChanged(StrokePresetPanel.this.pc, StrokePresetPanel.this.pc.getPaintSettings(), -1);
		}
		public void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {}
		public void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
			if ((delta & CHANGED_STROKE) != 0) {
				Stroke s = ps.getStroke();
				if (s instanceof PowerStroke || s instanceof BasicStroke) {
					PowerStroke ds = (s instanceof BasicStroke) ? PowerStroke.DEFAULT.deriveBasicStroke((BasicStroke)s) : (PowerStroke)s;
					Arrowhead psa = right ? ds.arrowOnEnd : ds.arrowOnStart;
					ArrowMenuItem.this.setSelected(arrow == null ? psa == null : psa == null ? arrow == null : arrow.equals(psa));
				} else {
					ArrowMenuItem.this.setSelected(false);
				}
			}
		}
	}
	
	private class ShowStrokeMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ShowStrokeMenuItem(final JPopupMenu parent) {
			super(PaletteUtilities.messages.getString("stroke.show"));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Container c = StrokePresetPanel.this.getParent();
					while (true) {
						if (c == null) {
							return;
						} else if (c instanceof Window) {
							Point p = MouseInfo.getPointerInfo().getLocation();
							c.setLocation(p.x-64, p.y-8);
							c.setVisible(true);
							return;
						} else {
							c = c.getParent();
						}
					}
				}
			});
		}
	}
	
	private class StrokePopup extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		public StrokePopup() {
			add(new ShowStrokeMenuItem(this));
			addSeparator();
			for (Float width : widths) {
				add(new WidthMenuItem(width));
			}
			addSeparator();
			for (float[] dash : dashes) {
				add(new DashMenuItem(dash));
			}
		}
	}
	
	private class ArrowPopup extends JPopupMenu {
		private static final long serialVersionUID = 1L;
		public ArrowPopup(boolean right) {
			add(new ShowStrokeMenuItem(this));
			addSeparator();
			for (Arrowhead arrow : arrows) {
				add(new ArrowMenuItem(arrow, right));
			}
		}
	}
}
