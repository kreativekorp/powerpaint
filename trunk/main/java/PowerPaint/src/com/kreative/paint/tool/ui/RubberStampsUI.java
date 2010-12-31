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
import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class RubberStampsUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private UpdateLock u = new UpdateLock();
	private ToolContext tc;
	private JList stampl, ssetl;
	
	public RubberStampsUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		
		ssetl = new JList(tc.getRubberStampSets().toFormerArray(new String[0]));
		ssetl.setCellRenderer(new StampSetCellRenderer(mini));
		ssetl.setVisibleRowCount(mini ? 3 : 5);
		ssetl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ssetl.setSelectedIndex(tc.getRubberStampSetIndex());
		ssetl.ensureIndexIsVisible(tc.getRubberStampSetIndex());
		ssetl.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (u.lock()) {
					RubberStampsUI.this.tc.setRubberStampSetIndex(ssetl.getSelectedIndex());
					stampl.setListData(new String[RubberStampsUI.this.tc.getRubberStamps().size()]);
					stampl.setSelectedIndex(RubberStampsUI.this.tc.getRubberStampIndex());
					stampl.ensureIndexIsVisible(RubberStampsUI.this.tc.getRubberStampIndex());
					u.unlock();
				}
			}
		});
		JScrollPane ssetlp = new JScrollPane(ssetl, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		if (mini) SwingUtils.shrink(ssetlp);
		
		stampl = new JList(new String[tc.getRubberStamps().size()]);
		stampl.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		stampl.setVisibleRowCount(0);
		stampl.setCellRenderer(new StampCellRenderer());
		stampl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stampl.setSelectedIndex(tc.getRubberStampIndex());
		stampl.ensureIndexIsVisible(tc.getRubberStampIndex());
		stampl.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (u.lock()) {
					RubberStampsUI.this.tc.setRubberStampIndex(stampl.getSelectedIndex());
					u.unlock();
				}
			}
		});
		JScrollPane stamplp = new JScrollPane(stampl, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		if (mini) SwingUtils.shrink(stamplp);
		stamplp.setMinimumSize(new Dimension(1,1));
		stamplp.setPreferredSize(new Dimension(1,1));
		
		setLayout(new GridLayout(2, 1, mini ? 4 : 8, mini ? 4 : 8));
		add(ssetlp);
		add(stamplp);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_STAMP_SET) != 0L) {
			if (u.lock()) {
				ssetl.setSelectedIndex(tc.getRubberStampSetIndex());
				ssetl.ensureIndexIsVisible(tc.getRubberStampSetIndex());
				stampl.setListData(new String[tc.getRubberStamps().size()]);
				stampl.setSelectedIndex(tc.getRubberStampIndex());
				stampl.ensureIndexIsVisible(tc.getRubberStampIndex());
				u.unlock();
			}
		} else if ((delta & ToolContextConstants.CHANGED_STAMP) != 0L) {
			if (u.lock()) {
				stampl.setSelectedIndex(tc.getRubberStampIndex());
				stampl.ensureIndexIsVisible(tc.getRubberStampIndex());
				u.unlock();
			}
		}
	}
	
	private class StampCellRenderer implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final int BORDER = 4;
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus
		) {
			Image img = tc.getRubberStamps().get(index);
			JLabel l = new JLabel(new ImageIcon(img));
			l.setOpaque(true);
			l.setBackground(isSelected ? SystemColor.textHighlight : SystemColor.text);
			l.setForeground(isSelected ? SystemColor.textHighlightText : SystemColor.textText);
			l.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
			return l;
		}
	}
	
	private class StampSetCellRenderer implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final int BORDER = 4;
		private boolean mini;
		public StampSetCellRenderer(boolean mini) {
			this.mini = mini;
		}
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus
		) {
			JLabel l = new JLabel();
			Vector<Image> ss = tc.getRubberStampSets().getLatter(index);
			BufferedImage img = new BufferedImage(mini ? 300 : 600, 32, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			for (int i = 0, x = 0; i < ss.size() && x < img.getWidth(); i++) {
				Image ii = ss.get(i);
				while (!g.drawImage(ii, x, 0, null));
				x += ii.getWidth(null) + 1;
			}
			g.dispose();
			l.setIcon(new ImageIcon(img));
			l.setText(value.toString());
			l.setFont(l.getFont().deriveFont(9.0f));
			l.setVerticalAlignment(JLabel.CENTER);
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setVerticalTextPosition(JLabel.BOTTOM);
			l.setHorizontalTextPosition(JLabel.CENTER);
			l.setOpaque(true);
			l.setBackground(isSelected ? SystemColor.textHighlight : SystemColor.text);
			l.setForeground(isSelected ? SystemColor.textHighlightText : SystemColor.textText);
			l.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
			return l;
		}
	}
}
