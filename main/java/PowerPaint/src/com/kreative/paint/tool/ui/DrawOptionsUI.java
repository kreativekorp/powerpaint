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
import javax.swing.*;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.tool.ToolUtilities;
import com.kreative.paint.tool.ToolOptions.*;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class DrawOptionsUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private UpdateLock u = new UpdateLock();
	private ToolContext tc;
	private JCheckBox pcb, scb, ccb, fcb, mcb;
	
	public DrawOptionsUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		setLayout(new GridLayout(0,3));
		pcb = new JCheckBox(ToolUtilities.messages.getString("options.DrawPerpendicular"));
		pcb.setSelected(tc.drawPerpendicular());
		pcb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (u.lock()) {
					DrawOptionsUI.this.tc.setDrawPerpendicular(pcb.isSelected());
					u.unlock();
				}
			}
		});
		if (mini) SwingUtils.shrink(pcb);
		if (tc.getTool() instanceof DrawPerpendicular) add(pcb);
		scb = new JCheckBox(ToolUtilities.messages.getString("options.DrawSquare"));
		scb.setSelected(tc.drawSquare());
		scb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (u.lock()) {
					DrawOptionsUI.this.tc.setDrawSquare(scb.isSelected());
					u.unlock();
				}
			}
		});
		if (mini) SwingUtils.shrink(scb);
		if (tc.getTool() instanceof DrawSquare) add(scb);
		ccb = new JCheckBox(ToolUtilities.messages.getString("options.DrawFromCenter"));
		ccb.setSelected(tc.drawFromCenter());
		ccb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (u.lock()) {
					DrawOptionsUI.this.tc.setDrawFromCenter(ccb.isSelected());
					u.unlock();
				}
			}
		});
		if (mini) SwingUtils.shrink(ccb);
		if (tc.getTool() instanceof DrawFromCenter) add(ccb);
		fcb = new JCheckBox(ToolUtilities.messages.getString("options.DrawFilled"));
		fcb.setSelected(tc.drawFilled());
		fcb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (u.lock()) {
					DrawOptionsUI.this.tc.setDrawFilled(fcb.isSelected());
					u.unlock();
				}
			}
		});
		if (mini) SwingUtils.shrink(fcb);
		if (tc.getTool() instanceof DrawFilled) add(fcb);
		mcb = new JCheckBox(ToolUtilities.messages.getString("options.DrawMultiple"));
		mcb.setSelected(tc.drawMultiple());
		mcb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (u.lock()) {
					DrawOptionsUI.this.tc.setDrawMultiple(mcb.isSelected());
					u.unlock();
				}
			}
		});
		if (mini) SwingUtils.shrink(mcb);
		if (tc.getTool() instanceof DrawMultiple) add(mcb);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {
		removeAll();
		if (tc.getTool() instanceof DrawPerpendicular) add(pcb);
		if (tc.getTool() instanceof DrawSquare) add(scb);
		if (tc.getTool() instanceof DrawFromCenter) add(ccb);
		if (tc.getTool() instanceof DrawFilled) add(fcb);
		if (tc.getTool() instanceof DrawMultiple) add(mcb);
		invalidate();
	}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_DRAW_OPTIONS) != 0L) {
			if (u.lock()) {
				pcb.setSelected(src.drawPerpendicular());
				scb.setSelected(src.drawSquare());
				ccb.setSelected(src.drawFromCenter());
				fcb.setSelected(src.drawFilled());
				mcb.setSelected(src.drawMultiple());
				u.unlock();
			}
		}
	}
}
