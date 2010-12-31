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

package com.kreative.paint.filter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import com.kreative.paint.form.FormUI;
import com.kreative.paint.util.SwingUtils;

public class FilterUI extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private boolean canceled = false;
	
	public FilterUI(Frame parentFrame, Filter f, Image src) {
		super(parentFrame, FilterUtilities.messages.getString("options.title").replace("$", f.getName()), true);
		JPanel main = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new FlowLayout());
		JButton ok = new JButton(FilterUtilities.messages.getString("options.OK"));
		JButton cancel = new JButton(FilterUtilities.messages.getString("options.Cancel"));
		buttons.add(ok);
		buttons.add(cancel);
		FormUI fui = new FormUI(f.getOptionForm(src), false);
		f.setFormUI(fui);
		main.add(fui, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.SOUTH);
		main.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		setContentPane(main);
		SwingUtils.setDefaultButton(getRootPane(), ok);
		SwingUtils.setCancelButton(getRootPane(), cancel);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canceled = false;
				dispose();
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canceled = true;
				dispose();
			}
		});
	}
	
	public boolean showOptions() {
		canceled = false;
		setVisible(true);
		return !canceled;
	}
}
