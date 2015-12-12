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
import com.kreative.paint.PaintContext;
import com.kreative.paint.material.MaterialList;
import com.kreative.paint.material.MaterialManager;
import com.kreative.paint.swing.*;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class TexturePanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private UpdateLock u = new UpdateLock();
	private CellSelector<TexturePaint> palcomp;
	private MaterialList<CellSelectorModel<TexturePaint>> palmap;
	private JComboBox list;
	
	public TexturePanel(PaintContext pc, MaterialManager mm, String initialSelection) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		palmap = new MaterialList<CellSelectorModel<TexturePaint>>();
		MaterialList<MaterialList<TexturePaint>> tl = mm.textureLoader().getTextures();
		for (int j = 0; j < tl.size(); j++) {
			String name = tl.getName(j);
			MaterialList<TexturePaint> list = tl.getValue(j);
			DefaultCellSelectorModel<TexturePaint> model = new DefaultCellSelectorModel<TexturePaint>();
			for (int k = 0; k < list.size(); k++) model.add(list.getValue(k), list.getName(k));
			palmap.add(name, model);
		}
		TexturePaint firstTexture;
		if (palmap.containsName("SuperPaint")) firstTexture = palmap.getValue(palmap.indexOfName("SuperPaint")).get(0);
		else firstTexture = palmap.getValue(0).get(0);
		for (CellSelectorModel<TexturePaint> m : palmap.valueList()) {
			m.setSelectedObject(firstTexture);
			m.addCellSelectionListener(new CellSelectionListener<TexturePaint>() {
				public void cellSelected(CellSelectionEvent<TexturePaint> e) {
					if (u.lock()) {
						TexturePanel.this.pc.setEditedEditedground(e.getObject());
						u.unlock();
					}
				}
			});
		}
		
		list = new JComboBox(palmap.toNameArray());
		list.setEditable(false);
		list.setMaximumRowCount(48);
		SwingUtils.shrink(list);
		list.setMinimumSize(new Dimension(1, list.getMinimumSize().height));
		list.setPreferredSize(new Dimension(1, list.getPreferredSize().height));
		
		palcomp = new CellSelector<TexturePaint>(null, new CellSelectorRenderer<TexturePaint>() {
			public int getCellHeight() { return 35; }
			public int getCellWidth() { return 35; }
			public int getColumns() { return 0; }
			public int getRows() { return 0; }
			public boolean isFixedHeight() { return true; }
			public boolean isFixedWidth() { return true; }
			public void paint(Graphics g, TexturePaint object, int x, int y, int w, int h) {
				((Graphics2D)g).setPaint(object);
				g.fillRect(x, y, w, h);
			}
		});
		
		setLayout(new BorderLayout());
		add(list, BorderLayout.PAGE_START);
		add(palcomp, BorderLayout.CENTER);
		ItemListener il = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					palcomp.setModel(palmap.getValue(list.getSelectedIndex()));
					palcomp.pack();
					Container c = TexturePanel.this;
					while (!(c == null || c instanceof Window || c instanceof Frame || c instanceof Dialog)) c = c.getParent();
					if (c instanceof Window) { ((Window)c).validate(); ((Window)c).pack(); }
					if (c instanceof Frame) { ((Frame)c).validate(); ((Frame)c).pack(); }
					if (c instanceof Dialog) { ((Dialog)c).validate(); ((Dialog)c).pack(); }
					TexturePanel.this.repaint();
				}
			}
		};
		list.addItemListener(il);
		if (palmap.containsName(initialSelection)) {
			int i = palmap.indexOfName(initialSelection);
			list.setSelectedIndex(i);
			palcomp.setModel(palmap.getValue(i));
		} else {
			list.setSelectedIndex(0);
			palcomp.setModel(palmap.getValue(0));
		}
		palcomp.pack();
		update();
	}
	
	public void update() {
		if (u.lock()) {
			Paint p = pc.getEditedEditedground();
			if (p instanceof TexturePaint) {
				TexturePaint tp = (TexturePaint)p;
				for (CellSelectorModel<TexturePaint> m : palmap.valueList()) {
					m.setSelectedObject(tp);
				}
				Container c = TexturePanel.this;
				while (!(c == null || c instanceof Window || c instanceof Frame || c instanceof Dialog)) c = c.getParent();
				if (c instanceof Window) { ((Window)c).validate(); ((Window)c).pack(); }
				if (c instanceof Frame) { ((Frame)c).validate(); ((Frame)c).pack(); }
				if (c instanceof Dialog) { ((Dialog)c).validate(); ((Dialog)c).pack(); }
				TexturePanel.this.repaint();
			}
			u.unlock();
		}
	}
	
	private JPopupPanel jpop = new JPopupPanel();
	public JPopupPanel getPopup() {
		CellSelector<TexturePaint> sel = palcomp.asPopup();
		jpop.setContentPane(sel);
		jpop.hideOnRelease(sel);
		jpop.setResizable(false);
		jpop.pack();
		return jpop;
	}
	
	public TexturePaint getTexture() {
		for (CellSelectorModel<TexturePaint> m : palmap.valueList()) {
			if (m.getSelectedObject() != null) {
				return m.getSelectedObject();
			}
		}
		return null;
	}
}
