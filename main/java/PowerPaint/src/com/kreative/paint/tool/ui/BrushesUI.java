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
import com.kreative.paint.material.sprite.Sprite;
import com.kreative.paint.material.sprite.SpriteSheet;
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
			labels = new HashSet<BrushLabel>();
			SpriteSheet ss = tc.getBrushSets().getLatter(n);
			int cells = ss.getSpriteCount();
			int rows = ss.rows;
			int cols = ss.columns;
			if (rows <= 0) {
				if (cols <= 0) cols = ((cells < 12) ? cells : 12);
				rows = ((cells + cols - 1) / cols);
			} else if (cols <= 0) {
				cols = ((cells + rows - 1) / rows);
			}
			setLayout(new GridLayout(rows, cols));
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					int i = ss.order.getIndex(rows, cols, y, x);
					if (i < cells) {
						Sprite s = ss.getSprite(i);
						BrushLabel l = new BrushLabel(s, n, i);
						add(l);
						labels.add(l);
					} else {
						add(new JPanel());
					}
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
		public BrushLabel(Sprite s, int n, int i) {
			super(new ImageIcon(s.getPreparedImage()));
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
