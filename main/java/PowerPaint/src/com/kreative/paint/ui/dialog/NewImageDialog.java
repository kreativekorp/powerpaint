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

package com.kreative.paint.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.kreative.paint.Canvas;
import com.kreative.paint.Layer;
import com.kreative.paint.datatransfer.ClipboardUtilities;
import com.kreative.paint.ui.UIUtilities;
import com.kreative.paint.util.ImageUtils;
import com.kreative.paint.util.SwingUtils;

public class NewImageDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private Canvas canvas = null;
	
	public NewImageDialog(Frame parent) {
		super(parent, UIUtilities.messages.getString("newimage.title"), true);
		makeGUI();
	}
	
	public NewImageDialog(Dialog parent) {
		super(parent, UIUtilities.messages.getString("newimage.title"), true);
		makeGUI();
	}
	
	private void makeGUI() {
		final boolean cb = ClipboardUtilities.clipboardHasImage();
		final Image cbi = cb ? ClipboardUtilities.getClipboardImage() : null;
		if (cbi != null) ImageUtils.prepImage(cbi);
		int w = (cbi != null) ? cbi.getWidth(null) : 800;
		int h = (cbi != null) ? cbi.getHeight(null) : 600;
		JLabel wl = new JLabel(UIUtilities.messages.getString("newimage.width")); wl.setHorizontalAlignment(JLabel.RIGHT);
		JLabel hl = new JLabel(UIUtilities.messages.getString("newimage.height")); hl.setHorizontalAlignment(JLabel.RIGHT);
		JLabel xl = new JLabel(UIUtilities.messages.getString("newimage.dpix")); xl.setHorizontalAlignment(JLabel.RIGHT);
		JLabel yl = new JLabel(UIUtilities.messages.getString("newimage.dpiy")); yl.setHorizontalAlignment(JLabel.RIGHT);
		JLabel cl = new JLabel(UIUtilities.messages.getString("newimage.contents")); cl.setHorizontalAlignment(JLabel.RIGHT);
		final JTextField wf = new JTextField(Integer.toString(w));
		final JTextField hf = new JTextField(Integer.toString(h));
		final JTextField xf = new JTextField("72");
		final JTextField yf = new JTextField("72");
		final JComboBox cf = new JComboBox(cb ? new String[] {
				UIUtilities.messages.getString("newimage.contents.transparent"),
				UIUtilities.messages.getString("newimage.contents.white"),
				UIUtilities.messages.getString("newimage.contents.black"),
				UIUtilities.messages.getString("newimage.contents.clipboard")
		} : new String[] {
				UIUtilities.messages.getString("newimage.contents.transparent"),
				UIUtilities.messages.getString("newimage.contents.white"),
				UIUtilities.messages.getString("newimage.contents.black")
		});
		cf.setEditable(false);
		cf.setMaximumRowCount(20);
		final JComboBox wu = new JComboBox(new String[] {
				UIUtilities.messages.getString("newimage.units.pixels"),
				UIUtilities.messages.getString("newimage.units.inches"),
				UIUtilities.messages.getString("newimage.units.cm"),
				UIUtilities.messages.getString("newimage.units.mm")
		});
		wu.setEditable(false);
		wu.setMaximumRowCount(20);
		final JComboBox hu = new JComboBox(new String[] {
				UIUtilities.messages.getString("newimage.units.pixels"),
				UIUtilities.messages.getString("newimage.units.inches"),
				UIUtilities.messages.getString("newimage.units.cm"),
				UIUtilities.messages.getString("newimage.units.mm")
		});
		hu.setEditable(false);
		hu.setMaximumRowCount(20);
		JLabel xu = new JLabel("dpi"); xu.setHorizontalAlignment(JLabel.LEFT);
		JLabel yu = new JLabel("dpi"); yu.setHorizontalAlignment(JLabel.LEFT);
		JPanel cu = new JPanel();
		JButton ok = new JButton(UIUtilities.messages.getString("newimage.ok"));
		JButton cancel = new JButton(UIUtilities.messages.getString("newimage.cancel"));
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(ok);
		buttons.add(cancel);
		JPanel form = new JPanel(new GridLayout(5,3,8,4));
		form.add(wl); form.add(wf); form.add(wu);
		form.add(hl); form.add(hf); form.add(hu);
		form.add(xl); form.add(xf); form.add(xu);
		form.add(yl); form.add(yf); form.add(yu);
		form.add(cl); form.add(cf); form.add(cu);
		JPanel main = new JPanel(new BorderLayout(8,8));
		main.add(form, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.SOUTH);
		main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(main);
		SwingUtils.setDefaultButton(getRootPane(), ok);
		SwingUtils.setCancelButton(getRootPane(), cancel);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double w, h, x, y;
				try {
					w = Double.parseDouble(wf.getText());
					if (w <= 0) throw new NumberFormatException();
				} catch (NumberFormatException nfe) {
					wf.setSelectionStart(0);
					wf.setSelectionEnd(wf.getText().length());
					JOptionPane.showMessageDialog(NewImageDialog.this, UIUtilities.messages.getString("newimage.error"));
					return;
				}
				try {
					h = Double.parseDouble(hf.getText());
					if (h <= 0) throw new NumberFormatException();
				} catch (NumberFormatException nfe) {
					hf.setSelectionStart(0);
					hf.setSelectionEnd(hf.getText().length());
					JOptionPane.showMessageDialog(NewImageDialog.this, UIUtilities.messages.getString("newimage.error"));
					return;
				}
				try {
					x = Double.parseDouble(xf.getText());
					if (x <= 0) throw new NumberFormatException();
				} catch (NumberFormatException nfe) {
					xf.setSelectionStart(0);
					xf.setSelectionEnd(xf.getText().length());
					JOptionPane.showMessageDialog(NewImageDialog.this, UIUtilities.messages.getString("newimage.error"));
					return;
				}
				try {
					y = Double.parseDouble(yf.getText());
					if (y <= 0) throw new NumberFormatException();
				} catch (NumberFormatException nfe) {
					yf.setSelectionStart(0);
					yf.setSelectionEnd(yf.getText().length());
					JOptionPane.showMessageDialog(NewImageDialog.this, UIUtilities.messages.getString("newimage.error"));
					return;
				}
				int wi, hi, xi, yi;
				switch (wu.getSelectedIndex()) {
				case 0: wi = (int)Math.ceil(w); break;
				case 1: wi = (int)Math.ceil(w*x); break;
				case 2: wi = (int)Math.ceil(w*x/2.54); break;
				case 3: wi = (int)Math.ceil(w*x/25.4); break;
				default: wi = (int)Math.ceil(w); break;
				}
				switch (hu.getSelectedIndex()) {
				case 0: hi = (int)Math.ceil(h); break;
				case 1: hi = (int)Math.ceil(h*y); break;
				case 2: hi = (int)Math.ceil(h*y/2.54); break;
				case 3: hi = (int)Math.ceil(h*y/25.4); break;
				default: hi = (int)Math.ceil(h); break;
				}
				xi = (int)Math.ceil(x);
				yi = (int)Math.ceil(y);
				canvas = new Canvas(wi, hi, xi, yi);
				Layer l; Graphics2D g;
				switch (cf.getSelectedIndex()) {
				case 1:
					l = new Layer();
					g = l.createPaintGraphics();
					g.setPaint(Color.white);
					g.fillRect(0, 0, wi, hi);
					g.dispose();
					l.setName("Background");
					l.setLocked(true);
					l.setSelected(false);
					canvas.add(0, l);
					break;
				case 2:
					l = new Layer();
					g = l.createPaintGraphics();
					g.setPaint(Color.black);
					g.fillRect(0, 0, wi, hi);
					g.dispose();
					l.setName("Background");
					l.setLocked(true);
					l.setSelected(false);
					canvas.add(0, l);
					break;
				case 3:
					if (ClipboardUtilities.clipboardHasDrawObjects()) {
						canvas.get(0).addAll(ClipboardUtilities.getClipboardDrawObjects());
					} else {
						g = canvas.get(0).createPaintGraphics();
						while (!g.drawImage(cbi, 0, 0, null));
						g.dispose();
					}
					break;
				}
				dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canvas = null;
				dispose();
			}
		});
	}
	
	public Canvas showDialog() {
		canvas = null;
		setVisible(true);
		return canvas;
	}
}
