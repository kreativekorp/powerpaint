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
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class FramesUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private UpdateLock u = new UpdateLock();
	private ToolContext tc;
	private JList l;
	
	public FramesUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		l = new JList(tc.getFrames().toNameArray());
		l.setCellRenderer(new FrameCellRenderer());
		l.setVisibleRowCount(mini ? 3 : 5);
		l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		l.setSelectedIndex(tc.getFrameIndex());
		l.ensureIndexIsVisible(tc.getFrameIndex());
		l.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (u.lock()) {
					FramesUI.this.tc.setFrameIndex(l.getSelectedIndex());
					u.unlock();
				}
			}
		});
		final JScrollPane lp = new JScrollPane(l, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		if (mini) SwingUtils.shrink(lp);
		setLayout(new GridLayout(1,1));
		add(lp);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_FRAME) != 0L) {
			if (u.lock()) {
				l.setSelectedIndex(tc.getFrameIndex());
				l.ensureIndexIsVisible(tc.getFrameIndex());
				u.unlock();
			}
		}
	}
	
	private class FrameCellRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final int WIDTH = 80;
		private static final int HEIGHT = 56;
		private static final int BORDER = 4;
		public FrameCellRenderer() {
			setOpaque(true);
		}
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus
		) {
			BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setColor(Color.white);
			tc.getFrames().getValue(index).paintWithin(g, 0, 0, WIDTH, HEIGHT);
			g.dispose();
			setIcon(new ImageIcon(img));
			setText(value.toString());
			setBackground(isSelected ? SystemColor.textHighlight : SystemColor.text);
			setForeground(isSelected ? SystemColor.textHighlightText : SystemColor.textText);
			setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
			return this;
		}
	}
}
