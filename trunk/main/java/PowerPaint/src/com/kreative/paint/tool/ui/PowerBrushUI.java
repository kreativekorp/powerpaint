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
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.Border;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.form.DoubleOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.FormUI;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.powerbrush.BrushSettings;
import com.kreative.paint.powerbrush.BrushShape;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.tool.ToolUtilities;

public class PowerBrushUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private ToolContext tc;
	private FormUI fui;
	private Set<PowerBrushLabel> brushLabels;
	
	public PowerBrushUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		Form f = new Form();
		f.add(new PreviewGenerator() {
			public String getName() { return null; }
			public void generatePreview(Graphics2D g, Rectangle r) {
				g.clearRect(r.x, r.y, r.width, r.height);
				g.setPaint(Color.black);
				g.setComposite(AlphaComposite.SrcOver);
				PowerBrushUI.this.tc.getPowerBrushSettings().paint(g, (float)r.getCenterX(), (float)r.getCenterY());
			}
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("options.OuterWidth"); }
			public double getMaximum() { return Float.MAX_VALUE; }
			public double getMinimum() { return 1.0f; }
			public double getStep() { return 1.0f; }
			public double getValue() { return PowerBrushUI.this.tc.getPowerBrushOuterWidth(); }
			public void setValue(double v) { PowerBrushUI.this.tc.setPowerBrushOuterWidth((float)v); }
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("options.OuterHeight"); }
			public double getMaximum() { return Float.MAX_VALUE; }
			public double getMinimum() { return 1.0f; }
			public double getStep() { return 1.0f; }
			public double getValue() { return PowerBrushUI.this.tc.getPowerBrushOuterHeight(); }
			public void setValue(double v) { PowerBrushUI.this.tc.setPowerBrushOuterHeight((float)v); }
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("options.InnerWidth"); }
			public double getMaximum() { return Float.MAX_VALUE; }
			public double getMinimum() { return 1.0f; }
			public double getStep() { return 1.0f; }
			public double getValue() { return PowerBrushUI.this.tc.getPowerBrushInnerWidth(); }
			public void setValue(double v) { PowerBrushUI.this.tc.setPowerBrushInnerWidth((float)v); }
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("options.InnerHeight"); }
			public double getMaximum() { return Float.MAX_VALUE; }
			public double getMinimum() { return 1.0f; }
			public double getStep() { return 1.0f; }
			public double getValue() { return PowerBrushUI.this.tc.getPowerBrushInnerHeight(); }
			public void setValue(double v) { PowerBrushUI.this.tc.setPowerBrushInnerHeight((float)v); }
		});
		fui = new FormUI(f, mini);
		brushLabels = new HashSet<PowerBrushLabel>();
		JPanel brushPanel = new JPanel(new GridLayout(1,0));
		for (final BrushShape bs : BrushSettings.SHAPES) {
			PowerBrushLabel l = new PowerBrushLabel(bs);
			l.setSelected(PowerBrushUI.this.tc.getPowerBrushShape() == bs);
			l.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					PowerBrushUI.this.tc.setPowerBrushShape(bs);
					for (PowerBrushLabel ll : brushLabels) {
						ll.setSelected(PowerBrushUI.this.tc.getPowerBrushShape() == ll.getBrushShape());
					}
				}
			});
			brushLabels.add(l);
			brushPanel.add(l);
		}
		setLayout(new BorderLayout(mini ? 4 : 8, mini ? 4 : 8));
		add(brushPanel, BorderLayout.PAGE_START);
		add(fui, BorderLayout.CENTER);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_POWERBRUSH) != 0) {
			fui.update();
			for (PowerBrushLabel ll : brushLabels) {
				ll.setSelected(PowerBrushUI.this.tc.getPowerBrushShape() == ll.getBrushShape());
			}
		}
	}
	
	private class PowerBrushLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private BrushShape bs;
		public PowerBrushLabel(BrushShape bs) {
			super(new ImageIcon(makePowerBrushPreview(bs)));
			this.bs = bs;
			setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		}
		public void setSelected(boolean b) {
			if (b) {
				Border inner = BorderFactory.createEmptyBorder(2, 2, 2, 2);
				Border outer = BorderFactory.createLineBorder(Color.black, 2);
				setBorder(BorderFactory.createCompoundBorder(outer, inner));
			} else {
				setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			}
		}
		public BrushShape getBrushShape() {
			return bs;
		}
	}
	
	private BufferedImage makePowerBrushPreview(BrushShape bs) {
		BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(Color.black);
		g.setComposite(AlphaComposite.SrcOver);
		g.fill(bs.makeBrush(8.0f, 8.0f, 16.0f, 16.0f));
		g.dispose();
		return bi;
	}
}
