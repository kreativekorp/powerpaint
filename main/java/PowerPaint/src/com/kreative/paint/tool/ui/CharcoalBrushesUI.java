package com.kreative.paint.tool.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;

public class CharcoalBrushesUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private ToolContext tc;
	private Set<BrushLabel> labels;
	
	public CharcoalBrushesUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		labels = new HashSet<BrushLabel>();
		setLayout(new GridLayout(1,0));
		for (int i = 0; i < tc.getCharcoalBrushes().size(); i++) {
			BrushLabel l = new BrushLabel(i);
			labels.add(l);
			add(l);
		}
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_CHARCOAL_BRUSH) != 0L) {
			for (BrushLabel l : labels) l.updateSelection();
		}
	}
	
	private class BrushLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private int i;
		public BrushLabel(int i) {
			super(new ImageIcon(tc.getCharcoalBrushes().get(i).getPreparedImage()));
			this.i = i;
			updateSelection();
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					tc.setCharcoalBrushIndex(BrushLabel.this.i);
				}
			});
		}
		public void updateSelection() {
			if (tc.getCharcoalBrushIndex() == i) {
				Border inner = BorderFactory.createEmptyBorder(2, 2, 2, 2);
				Border outer = BorderFactory.createLineBorder(Color.black, 2);
				setBorder(BorderFactory.createCompoundBorder(outer, inner));
			} else {
				setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			}
		}
	}
}
