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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.Border;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.geom.ParameterizedShape;
import com.kreative.paint.gradient.GradientColor;
import com.kreative.paint.gradient.GradientColorMap;
import com.kreative.paint.gradient.GradientColorStop;
import com.kreative.paint.gradient.GradientPaint2;
import com.kreative.paint.gradient.GradientShape;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class PowerShapesUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private UpdateLock u = new UpdateLock();
	private ToolContext tc;
	private JComboBox spop;
	private JPanel spanel;
	private CardLayout slyt;
	private Set<ShapePanel> spanels;
	
	public PowerShapesUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		
		spop = new JComboBox(tc.getPowerShapeSets().toFormerArray(new String[0]));
		spop.setEditable(false);
		spop.setMaximumRowCount(48);
		if (mini) SwingUtils.shrink(spop);
		spop.setSelectedIndex(tc.getPowerShapeSetIndex());
		spop.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (u.lock()) {
					PowerShapesUI.this.tc.setPowerShapeSetIndex(spop.getSelectedIndex());
					slyt.show(spanel, Integer.toString(spop.getSelectedIndex()));
					u.unlock();
				}
			}
		});
		
		spanel = new JPanel(slyt = new CardLayout());
		spanels = new HashSet<ShapePanel>();
		for (int n = 0; n < tc.getPowerShapeSets().size(); n++) {
			ShapePanel p = new ShapePanel(n, mini);
			JPanel p2 = new JPanel(new BorderLayout());
			p2.add(p, BorderLayout.PAGE_START);
			spanel.add(p2, Integer.toString(n));
			spanels.add(p);
		}
		
		setLayout(new BorderLayout(4,4));
		add(spop, BorderLayout.PAGE_START);
		add(spanel, BorderLayout.CENTER);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_SHAPE_SET) != 0L) {
			if (u.lock()) {
				spop.setSelectedIndex(tc.getPowerShapeSetIndex());
				slyt.show(spanel, Integer.toString(tc.getPowerShapeSetIndex()));
				u.unlock();
			}
			for (ShapePanel b : spanels) b.updateSelection();
		} else if ((delta & ToolContextConstants.CHANGED_SHAPE) != 0L) {
			for (ShapePanel b : spanels) b.updateSelection();
		}
	}
	
	private class ShapePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Set<ShapeLabel> labels;
		public ShapePanel(int n, boolean mini) {
			super(new GridLayout(0,12));
			labels = new HashSet<ShapeLabel>();
			for (int i = 0; i < tc.getPowerShapeSets().getLatter(n).size(); i++) {
				ShapeLabel l = new ShapeLabel(n, i, mini);
				add(l);
				labels.add(l);
			}
		}
		public void updateSelection() {
			for (ShapeLabel l : labels) l.updateSelection();
		}
	}
	
	private class ShapeLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private int n;
		private int i;
		public ShapeLabel(int n, int i, boolean mini) {
			super(new ImageIcon(getShapeImage(n, i, mini)));
			setToolTipText(getShapeName(n, i));
			this.n = n;
			this.i = i;
			updateSelection();
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					tc.setPowerShapeSetIndex(ShapeLabel.this.n);
					tc.setPowerShapeIndex(ShapeLabel.this.i);
				}
			});
		}
		public void updateSelection() {
			if (tc.getPowerShapeSetIndex() == n && tc.getPowerShapeIndex() == i) {
				Border inner = BorderFactory.createEmptyBorder(2, 2, 2, 2);
				Border outer = BorderFactory.createLineBorder(Color.black, 2);
				setBorder(BorderFactory.createCompoundBorder(outer, inner));
			} else {
				setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			}
		}
	}
	
	private static final Paint fill, draw;
	static {
		GradientShape shape = new GradientShape.Linear(0.5, 0, 0.5, 1, false, false, false, null);
		GradientColorMap colorMap = new GradientColorMap(null);
		colorMap.add(new GradientColorStop(0.0, new GradientColor.RGB(0x99, 0xCC, 0xFF)));
		colorMap.add(new GradientColorStop(1.0, new GradientColor.RGB(0x66, 0x99, 0xCC)));
		fill = new GradientPaint2(shape, colorMap, null);
		draw = new Color(0x00, 0x33, 0x66);
	}
	private Image getShapeImage(int coll, int idx, boolean mini) {
		int size = mini ? 19 : 25;
		Shape ss = AffineTransform.getScaleInstance(size-1, size-1).createTransformedShape(new ParameterizedShape(tc.getPowerShapeSets().getLatter(coll).getLatter(idx)));
		BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(fill);
		g.fill(ss);
		g.setPaint(draw);
		g.draw(ss);
		g.dispose();
		return bi;
	}
	private String getShapeName(int coll, int idx) {
		return tc.getPowerShapeSets().getLatter(coll).getFormer(idx);
	}
}
