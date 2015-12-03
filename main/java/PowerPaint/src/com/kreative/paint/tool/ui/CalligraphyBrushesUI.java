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
			super(new ImageIcon(tc.getCalligraphyBrushes().get(i).getPreparedImage()));
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
