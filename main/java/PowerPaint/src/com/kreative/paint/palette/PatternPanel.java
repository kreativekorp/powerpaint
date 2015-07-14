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
import java.util.*;
import javax.swing.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.pattern.Pattern;
import com.kreative.paint.pattern.PatternPaint;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.swing.*;
import com.kreative.paint.util.Pair;
import com.kreative.paint.util.PairList;
import com.kreative.paint.util.SwingUtils;

public class PatternPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;

	private boolean eventexec = false;
	private CellSelector<Pattern> palcomp;
	private PairList<String,CellSelectorModel<Pattern>> palmap;
	private JComboBox list;
	
	public PatternPanel(PaintContext pc, MaterialsManager mm, String initialSelection) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		palmap = new PairList<String,CellSelectorModel<Pattern>>();
		for (Pair<String,Vector<Pattern>> e : mm.getPatterns()) {
			palmap.add(e.getFormer(), new DefaultCellSelectorModel<Pattern>(e.getLatter()));
		}
		for (CellSelectorModel<Pattern> m : palmap.latterList()) {
			m.setSelectedObject(Pattern.FOREGROUND);
			m.addCellSelectionListener(new CellSelectionListener<Pattern>() {
				public void cellSelected(CellSelectionEvent<Pattern> e) {
					if (!PatternPanel.this.eventexec) PatternPanel.this.pc.setEditedPattern(e.getObject());
				}
			});
		}
		
		list = new JComboBox(palmap.toFormerArray(new String[0]));
		list.setEditable(false);
		list.setMaximumRowCount(48);
		SwingUtils.shrink(list);
		list.setMinimumSize(new Dimension(1, list.getMinimumSize().height));
		list.setPreferredSize(new Dimension(1, list.getPreferredSize().height));
		
		palcomp = new CellSelector<Pattern>(null, new CellSelectorRenderer<Pattern>() {
			public int getCellHeight() { return 15; }
			public int getCellWidth() { return 15; }
			public int getColumns() { return 0; }
			public int getRows() { return 0; }
			public boolean isFixedHeight() { return true; }
			public boolean isFixedWidth() { return true; }
			public void paint(Graphics g, Pattern object, int x, int y, int w, int h) {
				((Graphics2D)g).setPaint(new PatternPaint(Color.black, Color.white, object));
				g.fillRect(x, y, w, h);
			}
		});
		
		setLayout(new BorderLayout());
		add(list, BorderLayout.PAGE_START);
		add(palcomp, BorderLayout.CENTER);
		ItemListener il = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					palcomp.setModel(palmap.getLatter(list.getSelectedIndex()));
					palcomp.pack();
					Container c = PatternPanel.this;
					while (!(c == null || c instanceof Window || c instanceof Frame || c instanceof Dialog)) c = c.getParent();
					if (c instanceof Window) { ((Window)c).validate(); ((Window)c).pack(); }
					if (c instanceof Frame) { ((Frame)c).validate(); ((Frame)c).pack(); }
					if (c instanceof Dialog) { ((Dialog)c).validate(); ((Dialog)c).pack(); }
					PatternPanel.this.repaint();
				}
			}
		};
		list.addItemListener(il);
		list.setSelectedItem(initialSelection);
		palcomp.setModel(palmap.getLatter(list.getSelectedIndex()));
		palcomp.pack();
		update();
	}
	
	public void update() {
		if (!eventexec) {
			eventexec = true;
			Pattern p = pc.getEditedPattern();
			for (CellSelectorModel<Pattern> m : palmap.latterList()) {
				m.setSelectedObject(p);
			}
			Container c = PatternPanel.this;
			while (!(c == null || c instanceof Window || c instanceof Frame || c instanceof Dialog)) c = c.getParent();
			if (c instanceof Window) { ((Window)c).validate(); ((Window)c).pack(); }
			if (c instanceof Frame) { ((Frame)c).validate(); ((Frame)c).pack(); }
			if (c instanceof Dialog) { ((Dialog)c).validate(); ((Dialog)c).pack(); }
			PatternPanel.this.repaint();
			eventexec = false;
		}
	}
	
	private JPopupPanel jpop = new JPopupPanel();
	public JPopupPanel getPopup() {
		CellSelector<Pattern> sel = palcomp.asPopup();
		jpop.setContentPane(sel);
		jpop.hideOnRelease(sel);
		jpop.setResizable(false);
		jpop.pack();
		return jpop;
	}
	
	public Pattern getPattern() {
		return pc.getEditedPattern();
	}
}
