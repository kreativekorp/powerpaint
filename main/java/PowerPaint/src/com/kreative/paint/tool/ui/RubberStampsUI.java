package com.kreative.paint.tool.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.sprite.Sprite;
import com.kreative.paint.sprite.SpriteSheet;
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
		ssetl.setCellRenderer(new StampSetCellRenderer());
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
		private static final int BORDER = 4;
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus
		) {
			Image img = tc.getRubberStamps().get(index).getPreparedImage();
			JLabel l = new JLabel(new ImageIcon(img));
			l.setOpaque(true);
			l.setBackground(isSelected ? SystemColor.textHighlight : SystemColor.text);
			l.setForeground(isSelected ? SystemColor.textHighlightText : SystemColor.textText);
			l.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
			return l;
		}
	}
	
	private class StampSetCellRenderer implements ListCellRenderer {
		private static final int BORDER = 4;
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus
		) {
			JLabel l = new JLabel();
			SpriteSheet ss = tc.getRubberStampSets().getLatter(index);
			int n = ss.getSpriteCount();
			int w = -1;
			int h = 0;
			for (int i = 0; i < n; i++) {
				Sprite s = ss.getSprite(i);
				w += s.getWidth() + 1;
				if (s.getHeight() > h) h = s.getHeight();
			}
			if (w > 400) w = 400;
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			g.setColor(new Color((isSelected ? SystemColor.textHighlightText : SystemColor.textText).getRGB()));
			for (int i = 0, x = 0; i < n && x < w; i++) {
				Sprite s = ss.getSprite(i);
				s.paint(g, x + s.getHotspotX(), ((h - s.getHeight()) / 2) + s.getHotspotY());
				x += s.getWidth() + 1;
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
