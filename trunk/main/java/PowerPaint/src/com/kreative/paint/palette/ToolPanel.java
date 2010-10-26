/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import com.kreative.paint.ToolContext;
import com.kreative.paint.tool.ArrowTool;
import com.kreative.paint.tool.BrushTool;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.tool.ToolManager;
import com.kreative.paint.util.OSUtils;

public class ToolPanel extends ToolContextPanel {
	private static final long serialVersionUID = 1L;
	
	private List<Tool> tools;
	private List<ToolButton> buttons;
	private ButtonGroup bg;
	
	public ToolPanel(ToolContext tc, ToolManager tm) {
		super(tc, CHANGED_MODE|CHANGED_TOOL);
		tools = tm.getTools();
		buttons = new Vector<ToolButton>();
		for (Tool t : tools) buttons.add(new ToolButton(t));
		bg = new ButtonGroup();
		for (ToolButton b : buttons) bg.add(b);
		setLayout(new GridLayout(0,4));
		for (ToolButton b : buttons) add(b);
		update();
	}
	
	public void update() {
		if (tc != null) {
			Tool currentTool = tc.getTool();
			if (
					currentTool == null ||
					(tc.isInPaintMode() && !currentTool.validForPaintMode()) ||
					(tc.isInDrawMode() && !currentTool.validForDrawMode())
			) {
				for (Tool t : tools) {
					if ((tc.isInPaintMode() && t instanceof BrushTool) || (tc.isInDrawMode() && t instanceof ArrowTool)) {
						tc.setTool(currentTool = t);
					}
				}
			}
			if (
					currentTool == null ||
					(tc.isInPaintMode() && !currentTool.validForPaintMode()) ||
					(tc.isInDrawMode() && !currentTool.validForDrawMode())
			) {
				for (Tool t : tools) {
					if ((tc.isInPaintMode() && t.validForPaintMode()) || (tc.isInDrawMode() && t.validForDrawMode())) {
						tc.setTool(currentTool = t);
					}
				}
			}
			if (currentTool != null) {
				for (ToolButton b : buttons) {
					Tool bt = b.getTool();
					b.setSelected(bt.getClass().equals(currentTool.getClass()));
					b.setEnabled((tc.isInPaintMode() && bt.validForPaintMode()) || (tc.isInDrawMode() && bt.validForDrawMode()));
				}
			}
		}
	}
	
	private class ToolButton extends JToggleButton {
		private static final long serialVersionUID = 1L;
		private Tool t;
		public ToolButton(Tool t) {
			super(new ImageIcon(t.getIcon()));
			setToolTipText(t.getName());
			setHorizontalTextPosition(JToggleButton.CENTER);
			setVerticalTextPosition(JToggleButton.CENTER);
			squareOffButton(this);
			ToolButtonListener tbl = new ToolButtonListener(t);
			addMouseListener(tbl);
			this.t = t;
		}
		public Tool getTool() {
			return t;
		}
	}
	
	private class ToolButtonListener extends MouseAdapter {
		private Tool t;
		public ToolButtonListener(Tool t) {
			this.t = t;
		}
		public void mouseClicked(MouseEvent e) {
			if (tc != null) {
				if (e.getClickCount() > 1) {
					tc.doubleClickTool(t);
				} else {
					tc.setTool(t);
				}
			}
		}
	}
	
	private static void squareOffButton(JComponent c) {
		int h = OSUtils.isWindows() ? (c.getPreferredSize().height+1) : c.getPreferredSize().height;
		c.setMinimumSize(new Dimension(h,h));
		c.setPreferredSize(new Dimension(h,h));
		c.setMaximumSize(new Dimension(h,h));
	}
}
