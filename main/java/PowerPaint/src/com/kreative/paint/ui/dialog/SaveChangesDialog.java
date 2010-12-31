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
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kreative.paint.ui.UIUtilities;
import com.kreative.paint.util.SwingUtils;

public class SaveChangesDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public static enum Action {
		SAVE,
		CANCEL,
		DONT_SAVE
	}
	
	private Action action = Action.CANCEL;
	
	public SaveChangesDialog(Frame parent, String document) {
		super(parent, UIUtilities.messages.getString("savechanges.title"), true);
		makeGUI(document);
	}
	
	public SaveChangesDialog(Dialog parent, String document) {
		super(parent, UIUtilities.messages.getString("savechanges.title"), true);
		makeGUI(document);
	}
	
	private void makeGUI(String document) {
		JLabel cta = new JLabel(UIUtilities.messages.getString("savechanges.cta").replace("$", document));
		JLabel scta = new JLabel(UIUtilities.messages.getString("savechanges.scta").replace("$", document));
		JButton dontsave = new JButton(UIUtilities.messages.getString("savechanges.dontsave"));
		JButton cancel = new JButton(UIUtilities.messages.getString("savechanges.cancel"));
		JButton save = new JButton(UIUtilities.messages.getString("savechanges.save"));
		JPanel buttons_left = new JPanel(new FlowLayout());
		buttons_left.add(dontsave);
		JPanel buttons_right = new JPanel(new FlowLayout());
		buttons_right.add(cancel);
		buttons_right.add(save);
		JPanel buttons = new JPanel(new BorderLayout());
		buttons.add(buttons_left, BorderLayout.WEST);
		buttons.add(buttons_right, BorderLayout.EAST);
		JPanel cta_panel = new JPanel(new BorderLayout(8,8));
		cta_panel.add(cta, BorderLayout.CENTER);
		cta_panel.add(scta, BorderLayout.SOUTH);
		JPanel main = new JPanel(new BorderLayout(8,8));
		main.add(cta_panel, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.SOUTH);
		main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(main);
		SwingUtils.setDefaultButton(getRootPane(), save);
		SwingUtils.setCancelButton(getRootPane(), cancel);
		SwingUtils.setDontSaveButton(getRootPane(), dontsave);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setLocation(getX(), getY()/2);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action = Action.SAVE;
				dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action = Action.CANCEL;
				dispose();
			}
		});
		dontsave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action = Action.DONT_SAVE;
				dispose();
			}
		});
	}
	
	public Action showDialog() {
		action = Action.CANCEL;
		setVisible(true);
		return action;
	}
}
