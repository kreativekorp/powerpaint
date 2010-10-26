/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import com.kreative.paint.BufferedImagePaintSurface;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.tool.ToolUtilities;
import com.kreative.paint.util.Bitmap;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class SprinklesUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private UpdateLock u = new UpdateLock();
	private ToolContext tc;
	private JList l;
	private JCheckBox bmcb;
	
	public SprinklesUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		l = new JList(tc.getSprinkleSets().toFormerArray(new String[0]));
		l.setCellRenderer(new SprinkleSetCellRenderer());
		l.setVisibleRowCount(mini ? 4 : 8);
		l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		l.setSelectedIndex(tc.getSprinkleSetIndex());
		l.ensureIndexIsVisible(tc.getSprinkleSetIndex());
		l.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (u.lock()) {
					SprinklesUI.this.tc.setSprinkleSetIndex(l.getSelectedIndex());
					u.unlock();
				}
			}
		});
		JScrollPane lp = new JScrollPane(l, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		if (mini) SwingUtils.shrink(lp);
		bmcb = new JCheckBox(ToolUtilities.messages.getString("options.Sprinkles.BrushMode"));
		bmcb.setSelected(tc.sprinkleBrushMode());
		bmcb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (u.lock()) {
					SprinklesUI.this.tc.setSprinkleBrushMode(bmcb.isSelected());
					u.unlock();
				}
			}
		});
		if (mini) SwingUtils.shrink(bmcb);
		JPanel bmp = new JPanel(new FlowLayout());
		bmp.add(bmcb);
		setLayout(new BorderLayout());
		add(lp, BorderLayout.CENTER);
		add(bmp, BorderLayout.PAGE_END);
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_SPRINKLE_SET) != 0L) {
			if (u.lock()) {
				l.setSelectedIndex(src.getSprinkleSetIndex());
				l.ensureIndexIsVisible(src.getSprinkleSetIndex());
				u.unlock();
			}
		}
		if ((delta & ToolContextConstants.CHANGED_SPRINKLE_BRUSH_MODE) != 0L) {
			if (u.lock()) {
				bmcb.setSelected(src.sprinkleBrushMode());
				u.unlock();
			}
		}
	}
	
	private class SprinkleSetCellRenderer implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final int BORDER = 4;
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus
		) {
			JLabel l = new JLabel();
			Vector<Bitmap> ss = tc.getSprinkleSets().getLatter(index);
			int w = -1;
			int h = 0;
			for (Bitmap s : ss) {
				w += s.getWidth()+1;
				if (s.getHeight() > h) h = s.getHeight();
			}
			if (w > 400) w = 400;
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			PaintSurface srf = new BufferedImagePaintSurface(img);
			Graphics2D g = img.createGraphics();
			g.setColor(new Color((isSelected ? SystemColor.textHighlightText : SystemColor.textText).getRGB()));
			for (int i = 0, x = 0; i < ss.size() && x < w; i++) {
				ss.get(i).paint(srf, g, x, (h-ss.get(i).getHeight())/2);
				x += ss.get(i).getWidth()+1;
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
