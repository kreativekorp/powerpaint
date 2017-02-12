/*
 * Copyright &copy; 2009-2017 Rebecca G. Bettencourt / Kreative Software
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
import com.kreative.paint.material.MaterialList;
import com.kreative.paint.material.MaterialManager;
import com.kreative.paint.material.colorpalette.ColorChangeEvent;
import com.kreative.paint.material.colorpalette.ColorChangeListener;
import com.kreative.paint.material.colorpalette.RCPXComponent;
import com.kreative.paint.material.colorpalette.RCPXOrientation;
import com.kreative.paint.material.colorpalette.RCPXPalette;
import com.kreative.paint.swing.JPopupPanel;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class ColorPalettePanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	private static final Dimension BUTTON_SIZE = new Dimension(16,16);
	
	private static final Image SIZE_IMAGE;
	private static final Image SPINNER_IMAGE;
	static {
		Toolkit tk = Toolkit.getDefaultToolkit();
		SIZE_IMAGE = tk.createImage(ColorPalettePanel.class.getResource("RCPSize.png"));
		SPINNER_IMAGE = tk.createImage(ColorPalettePanel.class.getResource("RCPSpinner.png"));
	}
	
	private UpdateLock u = new UpdateLock();
	private RCPXComponent palcomp;
	private MaterialList<RCPXPalette> palmap;
	private JPanel top, buttons;
	private JButton size, spin;
	private JComboBox list;
	
	public ColorPalettePanel(PaintContext pc, MaterialManager mm, String initialSelection) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		palmap = mm.colorPaletteLoader().getColorPalettes();
		
		list = new JComboBox(palmap.toNameArray());
		list.setEditable(false);
		list.setMaximumRowCount(48);
		SwingUtils.shrink(list);
		
		buttons = new JPanel(new GridLayout(1,0,-1,-1));
		buttons.add(size = new JButton(new ImageIcon(SIZE_IMAGE)));
		buttons.add(spin = new JButton(new ImageIcon(SPINNER_IMAGE)));
		size.addActionListener(new SizeActionListener());
		spin.addActionListener(new SpinActionListener());
		size.putClientProperty("JButton.buttonType", "toolbar"); squareOffButton(size);
		spin.putClientProperty("JButton.buttonType", "toolbar"); squareOffButton(spin);
		
		top = new JPanel(new BorderLayout(-1,-1));
		top.add(buttons, BorderLayout.LINE_START);
		top.add(list, BorderLayout.CENTER);
		
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
					palcomp.setPalette(palmap.getValue(list.getSelectedIndex()));
					palcomp.setOrientation(null);
					palcomp.pack();
				}
			}
		};
		list.addItemListener(il);
		if (!palmap.containsName(initialSelection)) {
			initialSelection = palmap.getName(0);
		}
		list.setSelectedItem(initialSelection);
		palcomp.setPalette(palmap.getValue(list.getSelectedIndex()));
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
		public void actionPerformed(ActionEvent e) {
			int w = palcomp.getWidth();
			int h = palcomp.getHeight();
			if (w > (h + h / 2)) {
				palcomp.setOrientation(RCPXOrientation.SQUARE);
			} else if (h <= (w + w / 2)) {
				palcomp.setOrientation(RCPXOrientation.VERTICAL);
			} else {
				palcomp.setOrientation(RCPXOrientation.HORIZONTAL);
			}
			palcomp.pack();
		}
	}
	
	private class SpinActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Component c = (Component)e.getSource();
			int center = c.getLocationOnScreen().y + c.getHeight() / 2;
			int cy = MouseInfo.getPointerInfo().getLocation().y;
			if (cy < center) {
				int i = list.getSelectedIndex() - 1;
				if (i >= 0) list.setSelectedIndex(i);
			} else {
				int i = list.getSelectedIndex() + 1;
				int n = list.getItemCount();
				if (i < n) list.setSelectedIndex(i);
			}
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
