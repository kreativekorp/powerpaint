/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.rcp.ColorChangeEvent;
import com.kreative.paint.rcp.ColorChangeListener;
import com.kreative.paint.rcp.RCPXComponent;
import com.kreative.paint.rcp.RCPXOrientation;
import com.kreative.paint.rcp.RCPXPalette;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.swing.JPopupPanel;
import com.kreative.paint.util.PairList;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class ColorPalettePanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	private static final Dimension BUTTON_SIZE = new Dimension(16,16);
	
	private UpdateLock u = new UpdateLock();
	private RCPXComponent palcomp;
	private PairList<String,RCPXPalette> palmap;
	private JPanel top, buttons;
	private JButton hb, sb, vb;
	private JComboBox list;
	
	public ColorPalettePanel(PaintContext pc, MaterialsManager mm, String initialSelection) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		palmap = mm.getColorPalettes();
		
		list = new JComboBox(palmap.toFormerArray(new String[0]));
		list.setEditable(false);
		list.setMaximumRowCount(48);
		SwingUtils.shrink(list);
		
		buttons = new JPanel(new GridLayout(1,3,-1,-1));
		buttons.add(hb = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("RCPSizeHoriz.png")))));
		buttons.add(sb = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("RCPSizeSquare.png")))));
		buttons.add(vb = new JButton(new ImageIcon(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("RCPSizeVert.png")))));
		hb.addActionListener(new SizeActionListener(RCPXOrientation.HORIZONTAL));
		sb.addActionListener(new SizeActionListener(RCPXOrientation.SQUARE));
		vb.addActionListener(new SizeActionListener(RCPXOrientation.VERTICAL));
		hb.putClientProperty("JButton.buttonType", "toolbar"); squareOffButton(hb);
		sb.putClientProperty("JButton.buttonType", "toolbar"); squareOffButton(sb);
		vb.putClientProperty("JButton.buttonType", "toolbar"); squareOffButton(vb);
		
		top = new JPanel(new BorderLayout(-1,-1));
		top.add(list, BorderLayout.CENTER);
		top.add(buttons, BorderLayout.LINE_END);
		
		palcomp = new RCPXComponent();
		palcomp.addColorChangeListener(new ColorChangeListener() {
			public void colorChanged(ColorChangeEvent e) {
				if (u.lock()) {
					ColorPalettePanel.this.pc.setEditedEditedground(e.getNewColor());
					u.unlock();
				}
			}
		});
		
		int th = top.getPreferredSize().height;
		top.setMinimumSize(new Dimension(1, th));
		top.setPreferredSize(new Dimension(1, th));
		
		setLayout(new BorderLayout());
		add(top, BorderLayout.PAGE_START);
		add(palcomp, BorderLayout.CENTER);
		
		ItemListener il = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					palcomp.setPalette(palmap.getLatter(list.getSelectedIndex()));
					palcomp.setOrientation(null);
					palcomp.pack();
				}
			}
		};
		list.addItemListener(il);
		if (!palmap.containsFormer(initialSelection)) {
			initialSelection = palmap.getFormer(0);
		}
		list.setSelectedItem(initialSelection);
		palcomp.setPalette(palmap.getLatter(list.getSelectedIndex()));
		palcomp.setOrientation(null);
		palcomp.pack();
		update();
	}
	
	public void update() {
		if (u.lock()) {
			Paint p = pc.getEditedEditedground();
			if (p instanceof Color) {
				palcomp.setSelectedColor((Color)p);
			}
			u.unlock();
		}
	}
	
	private JPopupPanel jpop = new JPopupPanel();
	public JPopupPanel getPopup() {
		RCPXComponent rcp = palcomp.asPopup();
		jpop.setContentPane(rcp);
		jpop.hideOnRelease(rcp);
		jpop.setResizable(false);
		jpop.pack();
		return jpop;
	}
	
	private class SizeActionListener implements ActionListener {
		private RCPXOrientation orientation;
		public SizeActionListener(RCPXOrientation orientation) {
			this.orientation = orientation;
		}
		public void actionPerformed(ActionEvent e) {
			palcomp.setOrientation(orientation);
			palcomp.pack();
		}
	}
	
	private static void squareOffButton(JComponent c) {
		c.setMinimumSize(BUTTON_SIZE);
		c.setPreferredSize(BUTTON_SIZE);
		c.setMaximumSize(BUTTON_SIZE);
	}
	
	public Color getColor() {
		return palcomp.getSelectedColor();
	}
}
