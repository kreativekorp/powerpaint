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
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.awt.CheckerboardPaint;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.util.Pair;
import com.kreative.paint.util.PairList;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class ColorListPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private UpdateLock u = new UpdateLock();
	private PairList<String,LinkedHashMap<Color,String>> colorMap;
	private DefaultListModel colorsModel;
	private JList colorsList;
	private JComboBox list;
	
	public ColorListPanel(PaintContext pc, MaterialsManager mm) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		colorMap = mm.getColorLists();
		
		list = new JComboBox(colorMap.toFormerArray(new String[0]));
		list.setEditable(false);
		list.setMaximumRowCount(48);
		SwingUtils.shrink(list);

		colorsModel = new DefaultListModel();
		colorsList = new JList(colorsModel);
		colorsList.setFixedCellHeight(17);
		colorsList.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean sel, boolean focus) {
				JLabel l = (JLabel)super.getListCellRendererComponent(list, value, index, sel, focus);
				Pair<?,?> p = (Pair<?,?>)value;
				BufferedImage bi = new BufferedImage(25, 15, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.setPaint(CheckerboardPaint.LIGHT);
				g.fillRect(0, 0, 25, 15);
				g.setPaint((Color)p.getFormer());
				g.fillRect(0, 0, 25, 15);
				g.setPaint(((Color)p.getFormer()).darker());
				g.drawRect(0, 0, 24, 14);
				g.dispose();
				l.setIcon(new ImageIcon(bi));
				l.setText(p.getLatter().toString());
				l.setFont(l.getFont().deriveFont(10.0f));
				l.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
				return l;
			}
		});
		colorsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (u.lock()) {
					Pair<?,?> p = (Pair<?,?>)colorsList.getSelectedValue();
					if (p != null) ColorListPanel.this.pc.setEditedEditedground((Color)p.getFormer());
					u.unlock();
				}
			}
		});
		
		setLayout(new BorderLayout());
		add(list, BorderLayout.PAGE_START);
		add(new JScrollPane(colorsList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		
		ItemListener il = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					LinkedHashMap<Color,String> v = colorMap.getLatter(list.getSelectedIndex());
					colorsModel.clear();
					for (Map.Entry<Color,String> p : v.entrySet()) {
						colorsModel.addElement(new Pair<Color,String>(p.getKey(), p.getValue()));
					}
					update();
				}
			}
		};
		list.addItemListener(il);
		list.setSelectedIndex(0);
		il.itemStateChanged(new ItemEvent(list, ItemEvent.SELECTED, list.getItemAt(0), ItemEvent.SELECTED));
		update();
	}
	
	public void update() {
		if (u.lock()) {
			Paint p = pc.getEditedEditedground();
			if (p instanceof Color) {
				colorsList.clearSelection();
				for (int i = 0; i < colorsModel.getSize(); i++) {
					Pair<?,?> pr = (Pair<?,?>)colorsModel.getElementAt(i);
					if (pr.getFormer().equals(p)) {
						colorsList.setSelectedIndex(i);
						colorsList.ensureIndexIsVisible(i);
						break;
					}
				}
			}
			u.unlock();
		}
	}
}
