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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.tool.ToolUtilities;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class CalligraphyBrushesUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private UpdateLock u = new UpdateLock();
	private ToolContext tc;
	private Set<BrushLabel> labels;
	private JRadioButton crb, drb;
	
	public CalligraphyBrushesUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		labels = new HashSet<BrushLabel>();
		JPanel lp = new JPanel(new GridLayout(1,0));
		for (int i = 0; i < tc.getCalligraphyBrushes().size(); i++) {
			BrushLabel l = new BrushLabel(i);
			labels.add(l);
			lp.add(l);
		}
		crb = new JRadioButton(ToolUtilities.messages.getString("options.Calligraphy.Continuous"));
		drb = new JRadioButton(ToolUtilities.messages.getString("options.Calligraphy.Discontinuous"));
		crb.setSelected(tc.calligraphyContinuous());
		drb.setSelected(!tc.calligraphyContinuous());
		crb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (u.lock()) {
					CalligraphyBrushesUI.this.tc.setCalligraphyContinuous(true);
					u.unlock();
				}
			}
		});
		drb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (u.lock()) {
					CalligraphyBrushesUI.this.tc.setCalligraphyContinuous(false);
					u.unlock();
				}
			}
		});
		if (mini) {
			SwingUtils.shrink(crb);
			SwingUtils.shrink(drb);
		}
		ButtonGroup rbg = new ButtonGroup();
		JPanel rbp = new JPanel(new GridLayout(2,1));
		rbg.add(crb);
		rbg.add(drb);
		rbp.add(crb);
		rbp.add(drb);
		JPanel rbp2 = new JPanel(new FlowLayout());
		rbp2.add(rbp);
		setLayout(new BorderLayout());
		add(lp, BorderLayout.PAGE_START);
		add(rbp2, BorderLayout.CENTER);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_CALLIGRAPHY_BRUSH) != 0L) {
			for (BrushLabel l : labels) l.updateSelection();
		}
		if ((delta & ToolContextConstants.CHANGED_CALLIGRAPHY_CONTINUOUS) != 0L) {
			if (u.lock()) {
				crb.setSelected(src.calligraphyContinuous());
				drb.setSelected(!src.calligraphyContinuous());
				u.unlock();
			}
		}
	}
	
	private class BrushLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private int i;
		public BrushLabel(int i) {
			super(new ImageIcon(tc.getCalligraphyBrushes().getLatter(i).get(0).getImage()));
			this.i = i;
			updateSelection();
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					tc.setCalligraphyBrushIndex(BrushLabel.this.i);
				}
			});
		}
		public void updateSelection() {
			if (tc.getCalligraphyBrushIndex() == i) {
				Border inner = BorderFactory.createEmptyBorder(2, 2, 2, 2);
				Border outer = BorderFactory.createLineBorder(Color.black, 2);
				setBorder(BorderFactory.createCompoundBorder(outer, inner));
			} else {
				setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			}
		}
	}
}
