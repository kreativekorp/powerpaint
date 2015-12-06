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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.PaintContextListener;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.material.fontlist.FontList;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.util.Pair;
import com.kreative.paint.util.SwingUtils;

public class FontPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int[] menusizes = new int[] {
		9, 10, 12, 14, 18, 24, 36, 48, 56, 64, 72
	};
	private static final int[] sizes = new int[]{
		7, 8, 9, 10, 11, 12, 13, 14, 16, 18,
		20, 22, 24, 28, 32, 36, 40, 44, 48,
		56, 64, 72, 80, 88, 96, 120, 144
	};
	private static final String[] styleNames = new String[]{
		PaletteUtilities.messages.getString("fonts.plain"),
		PaletteUtilities.messages.getString("fonts.bold"),
		PaletteUtilities.messages.getString("fonts.italic"),
		PaletteUtilities.messages.getString("fonts.bolditalic")
	};
	private static final int[] styles = new int[]{
		Font.PLAIN,
		Font.BOLD,
		Font.ITALIC,
		Font.BOLD|Font.ITALIC
	};
	
	private String allFontsName;
	private SortedMap<String,Font> allFonts;
	private SortedMap<String,FontList> fontLists;
	private DefaultListModel collectionList;
	private JList collectionView;
	private DefaultListModel fontList;
	private JList fontView;
	private DefaultListModel styleList;
	private JList styleView;
	private DefaultListModel sizeList;
	private JList sizeView;
	private SpinnerModel sizeSpin;
	private JSpinner sizeSpinView;
	private JPopupMenu fpop;
	private JPopupMenu spop;
	private boolean eventexec = false;
	
	public FontPanel(PaintContext pc, MaterialsManager mm) {
		super(pc, CHANGED_FONT);
		allFontsName = PaletteUtilities.messages.getString("fonts.all");
		allFonts = mm.getFonts();
		String[] allFontNames = allFonts.keySet().toArray(new String[allFonts.size()]);
		FontList allFontsList = new FontList(allFontsName, allFontNames);
		fontLists = new TreeMap<String,FontList>();
		fontLists.put(allFontsName, allFontsList);
		for (Pair<String,FontList> p : mm.getFontLists()) {
			String listName = p.getFormer();
			FontList oldList = p.getLatter();
			FontList newList = new FontList(listName);
			for (int i = 0, n = oldList.size(); i < n; i++) {
				Integer fontId = oldList.getId(i);
				String fontName = oldList.getName(i);
				if (allFonts.containsKey(fontName)) {
					newList.add(fontId, fontName);
				}
			}
			if (!newList.isEmpty()) {
				fontLists.put(listName, newList);
			}
		}
		
		collectionList = new DefaultListModel();
		for (String coll : fontLists.keySet()) collectionList.addElement(coll);
		collectionView = new JList(collectionList);
		collectionView.setSelectedValue(allFontsName, true);
		JScrollPane collectionPane = new JScrollPane(collectionView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		fontList = new DefaultListModel();
		for (String font : allFonts.keySet()) fontList.addElement(font);
		fontView = new JList(fontList);
		JScrollPane fontPane = new JScrollPane(fontView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		styleList = new DefaultListModel();
		for (String style : styleNames) styleList.addElement(style);
		styleView = new JList(styleList);
		styleView.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
				JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, sel, focus);
				l.setFont(l.getFont().deriveFont(styles[index]));
				return l;
			}
		});
		JScrollPane stylePane = new JScrollPane(styleView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		sizeList = new DefaultListModel();
		for (int size : sizes) sizeList.addElement(size);
		sizeView = new JList(sizeList);
		JScrollPane sizePane = new JScrollPane(sizeView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		sizeSpin = new SpinnerNumberModel(12, 1, 32767, 1);
		sizeSpinView = new JSpinner(sizeSpin);
		SwingUtils.shrink(sizeSpinView);
		
		collectionView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					fontList.removeAllElements();
					FontList coll = fontLists.get(collectionView.getSelectedValue().toString());
					for (String font : coll.uniqueNamesToArray()) fontList.addElement(font);
					if (FontPanel.this.pc != null) {
						Font f = FontPanel.this.pc.getFont();
						fontView.setSelectedValue(f.getFamily(), true);
						int style = f.getStyle();
						for (int i=0; i<styleNames.length && i<styles.length; i++) {
							if (styles[i] == style) styleView.setSelectedValue(styleNames[i], true);
						}
						sizeView.setSelectedValue(f.getSize(), true);
					}
					eventexec = false;
				}
			}
		});
		
		fontView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (FontPanel.this.pc != null) {
						String fn = fontView.getSelectedValue().toString();
						int st = styles[styleView.getSelectedIndex()];
						int sz = ((Number)sizeView.getSelectedValue()).intValue();
						Font font = allFonts.containsKey(fn) ?
						            allFonts.get(fn).deriveFont(st, (float)sz) :
						            new Font(fn, st, sz);
						FontPanel.this.pc.setFont(font);
					}
					eventexec = false;
				}
			}
		});
		
		styleView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (FontPanel.this.pc != null) {
						String fn = fontView.getSelectedValue().toString();
						int st = styles[styleView.getSelectedIndex()];
						int sz = ((Number)sizeView.getSelectedValue()).intValue();
						Font font = allFonts.containsKey(fn) ?
						            allFonts.get(fn).deriveFont(st, (float)sz) :
						            new Font(fn, st, sz);
						FontPanel.this.pc.setFont(font);
					}
					eventexec = false;
				}
			}
		});
		
		sizeView.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (FontPanel.this.pc != null) {
						String fn = fontView.getSelectedValue().toString();
						int st = styles[styleView.getSelectedIndex()];
						int sz = ((Number)sizeView.getSelectedValue()).intValue();
						Font font = allFonts.containsKey(fn) ?
						            allFonts.get(fn).deriveFont(st, (float)sz) :
						            new Font(fn, st, sz);
						FontPanel.this.pc.setFont(font);
						sizeSpin.setValue(sz);
					}
					eventexec = false;
				}
			}
		});
		
		sizeSpinView.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!eventexec) {
					eventexec = true;
					if (FontPanel.this.pc != null) {
						String fn = fontView.getSelectedValue().toString();
						int st = styles[styleView.getSelectedIndex()];
						int sz = ((Number)sizeSpin.getValue()).intValue();
						Font font = allFonts.containsKey(fn) ?
						            allFonts.get(fn).deriveFont(st, (float)sz) :
						            new Font(fn, st, sz);
						FontPanel.this.pc.setFont(font);
						sizeView.clearSelection();
						sizeView.setSelectedValue(sz, true);
					}
					eventexec = false;
				}
			}
		});
		
		JPanel fontPanel = new JPanel(new GridLayout(1,3));
		fontPanel.add(collectionPane);
		fontPanel.add(fontPane);
		fontPanel.add(stylePane);
		
		JPanel sizePanel = new JPanel(new BorderLayout());
		sizePanel.add(sizePane, BorderLayout.CENTER);
		sizePanel.add(sizeSpinView, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		add(fontPanel, BorderLayout.CENTER);
		add(sizePanel, BorderLayout.EAST);
		
		fpop = new FontPopup();
		spop = new SizePopup();
		
		update();
	}
	
	public void update() {
		if (!eventexec) {
			eventexec = true;
			if (pc != null) {
				Font f = pc.getFont();
				if (!fontList.contains(f.getFamily())) {
					collectionView.setSelectedValue(allFontsName, true);
					fontList.removeAllElements();
					for (String font : allFonts.keySet()) fontList.addElement(font);
				}
				fontView.setSelectedValue(f.getFamily(), true);
				int style = f.getStyle();
				for (int i=0; i<styleNames.length && i<styles.length; i++) {
					if (styles[i] == style) styleView.setSelectedValue(styleNames[i], true);
				}
				sizeView.clearSelection();
				sizeView.setSelectedValue(f.getSize(), true);
				sizeSpin.setValue(f.getSize());
			}
			eventexec = false;
		}
	}
	
	public JPopupMenu getFontPopup() {
		return fpop;
	}
	
	public JPopupMenu getSizePopup() {
		return spop;
	}
	
	private static final String[] ALPHABET_LETTERS = new String[] {
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
		"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
		"#"
	};
	private class FontPopup extends JPopupMenu implements PaintContextListener {
		private static final long serialVersionUID = 1L;
		public FontPopup() {
			add(new ShowFontsMenuItem(this));
			addSeparator();
			String sel = FontPanel.this.pc.getFont().getFamily();
			for (Map.Entry<String,FontList> c : fontLists.entrySet()) {
				if (c.getKey().equals(allFontsName)) {
					// All Fonts is handled specially because Java is terrible at handling long menus.
					JMenu m = new JMenu(c.getKey());
					JMenu[] sm = new JMenu[ALPHABET_LETTERS.length];
					for (int i = 0; i < ALPHABET_LETTERS.length; i++) {
						sm[i] = new JMenu(ALPHABET_LETTERS[i]);
						m.add(sm[i]);
					}
					for (final String fontName : c.getValue().uniqueNamesToArray()) {
						JMenuItem mi = new JCheckBoxMenuItem(fontName);
						mi.setSelected(fontName.equals(sel));
						mi.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								Font pf = FontPanel.this.pc.getFont();
								int st = pf.getStyle();
								int sz = pf.getSize();
								Font font = allFonts.containsKey(fontName) ?
								            allFonts.get(fontName).deriveFont(st, (float)sz) :
								            new Font(fontName, st, sz);
								FontPanel.this.pc.setFont(font);
							}
						});
						boolean found = false;
						for (int i = 0; i < ALPHABET_LETTERS.length; i++) {
							if (fontName.toLowerCase().startsWith(ALPHABET_LETTERS[i].toLowerCase())) {
								sm[i].add(mi);
								found = true; break;
							}
						}
						if (!found) sm[ALPHABET_LETTERS.length-1].add(mi);
					}
					add(m);
				} else {
					JMenu m = new JMenu(c.getKey());
					for (final String fontName : c.getValue().uniqueNamesToArray()) {
						JMenuItem mi = new JCheckBoxMenuItem(fontName);
						mi.setSelected(fontName.equals(sel));
						mi.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								Font pf = FontPanel.this.pc.getFont();
								int st = pf.getStyle();
								int sz = pf.getSize();
								Font font = allFonts.containsKey(fontName) ?
								            allFonts.get(fontName).deriveFont(st, (float)sz) :
								            new Font(fontName, st, sz);
								FontPanel.this.pc.setFont(font);
							}
						});
						m.add(mi);
					}
					add(m);
				}
			}
			FontPanel.this.pc.addPaintContextListener(FontPopup.this);
		}
		public void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {}
		public void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
			if ((delta & PaintContext.CHANGED_FONT) != 0) {
				setChecked(FontPopup.this, ps.getFont().getFamily());
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
	
	private class SizePopup extends JPopupMenu implements PaintContextListener {
		private static final long serialVersionUID = 1L;
		public SizePopup() {
			add(new ShowFontsMenuItem(this));
			addSeparator();
			int selst = FontPanel.this.pc.getFont().getStyle();
			int selsz = FontPanel.this.pc.getFont().getSize();
			for (int i = 0; i < styles.length; i++) {
				JMenuItem mi = new JCheckBoxMenuItem(styleNames[i]);
				mi.setSelected(styles[i] == selst);
				mi.setFont(mi.getFont().deriveFont(styles[i]));
				final int st = styles[i];
				mi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FontPanel.this.pc.setFont(FontPanel.this.pc.getFont().deriveFont(st));
					}
				});
				add(mi);
			}
			addSeparator();
			for (int size : menusizes) {
				JMenuItem mi = new JCheckBoxMenuItem(Integer.toString(size));
				mi.setSelected(size == selsz);
				final float sz = (float)size;
				mi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FontPanel.this.pc.setFont(FontPanel.this.pc.getFont().deriveFont(sz));
					}
				});
				add(mi);
			}
			JMenuItem mi = new JMenuItem(PaletteUtilities.messages.getString("fonts.other"));
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Font f = FontPanel.this.pc.getFont();
					String nz = JOptionPane.showInputDialog(PaletteUtilities.messages.getString("fonts.other.prompt"), Integer.toString(f.getSize()));
					if (nz != null) {
						try {
							int sz = Integer.parseInt(nz);
							FontPanel.this.pc.setFont(f.deriveFont((float)sz));
						} catch (NumberFormatException nfe) {}
					}
				}
			});
			add(mi);
			FontPanel.this.pc.addPaintContextListener(SizePopup.this);
		}
		public void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {}
		public void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
			if ((delta & PaintContext.CHANGED_FONT) != 0) {
				setChecked(this, ps.getFont().getStyle(), ps.getFont().getSize());
			}
		}
		private void setChecked(MenuElement mi, int selst, int selsz) {
			String selstst = "";
			for (int i = 0; i < styles.length; i++) {
				if (styles[i] == selst) selstst = styleNames[i];
			}
			String selszst = Integer.toString(selsz);
			setChecked(mi, selstst, selszst);
		}
		private void setChecked(MenuElement mi, String selst, String selsz) {
			if (mi instanceof JMenuItem) {
				((JMenuItem)mi).setSelected(((JMenuItem)mi).getText().equals(selst) || ((JMenuItem)mi).getText().equals(selsz));
			}
			for (MenuElement smi : mi.getSubElements()) {
				setChecked(smi, selst, selsz);
			}
		}
	}
	
	private class ShowFontsMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		public ShowFontsMenuItem(final JPopupMenu parent) {
			super(PaletteUtilities.messages.getString("fonts.show"));
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Container c = FontPanel.this.getParent();
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
}
