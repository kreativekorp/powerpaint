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

package com.kreative.paint.tool.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.Border;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class BrushesUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private UpdateLock u = new UpdateLock();
	private ToolContext tc;
	private JComboBox bpop;
	private JPanel bpanel;
	private CardLayout blyt;
	private Set<BrushPanel> bpanels;
	
	public BrushesUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		
		bpop = new JComboBox(tc.getBrushSets().toFormerArray(new String[0]));
		bpop.setEditable(false);
		bpop.setMaximumRowCount(48);
		if (mini) SwingUtils.shrink(bpop);
		bpop.setSelectedIndex(tc.getBrushSetIndex());
		bpop.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (u.lock()) {
					BrushesUI.this.tc.setBrushSetIndex(bpop.getSelectedIndex());
					blyt.show(bpanel, Integer.toString(bpop.getSelectedIndex()));
					u.unlock();
				}
			}
		});
		
		bpanel = new JPanel(blyt = new CardLayout());
		bpanels = new HashSet<BrushPanel>();
		for (int n = 0; n < tc.getBrushSets().size(); n++) {
			BrushPanel p = new BrushPanel(n);
			JPanel p2 = new JPanel(new BorderLayout());
			p2.add(p, BorderLayout.PAGE_START);
			bpanel.add(p2, Integer.toString(n));
			bpanels.add(p);
		}
		
		setLayout(new BorderLayout(4,4));
		add(bpop, BorderLayout.PAGE_START);
		add(bpanel, BorderLayout.CENTER);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_BRUSH_SET) != 0L) {
			if (u.lock()) {
				bpop.setSelectedIndex(tc.getBrushSetIndex());
				blyt.show(bpanel, Integer.toString(tc.getBrushSetIndex()));
				u.unlock();
			}
			for (BrushPanel b : bpanels) b.updateSelection();
		} else if ((delta & ToolContextConstants.CHANGED_BRUSH) != 0L) {
			for (BrushPanel b : bpanels) b.updateSelection();
		}
	}
	
	private class BrushPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Set<BrushLabel> labels;
		public BrushPanel(int n) {
			super(new GridLayout(4,0));
			labels = new HashSet<BrushLabel>();
			for (int j = 0; j < 4; j++) {
				for (int i = j; i < tc.getBrushSets().getLatter(n).size(); i += 4) {
					BrushLabel l = new BrushLabel(n, i);
					add(l);
					labels.add(l);
				}
			}
		}
		public void updateSelection() {
			for (BrushLabel l : labels) l.updateSelection();
		}
	}
	
	private class BrushLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private int n;
		private int i;
		public BrushLabel(int n, int i) {
			super(new ImageIcon(tc.getBrushSets().getLatter(n).get(i).getImage()));
			this.n = n;
			this.i = i;
			updateSelection();
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					tc.setBrushSetIndex(BrushLabel.this.n);
					tc.setBrushIndex(BrushLabel.this.i);
				}
			});
		}
		public void updateSelection() {
			if (tc.getBrushSetIndex() == n && tc.getBrushIndex() == i) {
				Border inner = BorderFactory.createEmptyBorder(2, 2, 2, 2);
				Border outer = BorderFactory.createLineBorder(Color.black, 2);
				setBorder(BorderFactory.createCompoundBorder(outer, inner));
			} else {
				setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			}
		}
	}
}
