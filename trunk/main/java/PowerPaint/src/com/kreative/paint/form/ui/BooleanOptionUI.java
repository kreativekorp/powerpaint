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

package com.kreative.paint.form.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class BooleanOptionUI extends JPanel implements FormOptionUI {
	private static final long serialVersionUID = 1L;
	private JCheckBox cb;
	private JRadioButton fb;
	private JRadioButton tb;
	private BooleanOption o;
	private UpdateLock u = new UpdateLock();
	
	public BooleanOptionUI(BooleanOption opt, boolean mini) {
		this.o = opt;
		if (o.useTrueFalseLabels()) {
			cb = null;
			fb = new JRadioButton(o.getLabel(false));
			fb.setSelected(!o.getValue());
			fb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (u.lock()) {
						o.setValue(false);
						u.unlock();
					}
				}
			});
			tb = new JRadioButton(o.getLabel(true));
			tb.setSelected(o.getValue());
			tb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (u.lock()) {
						o.setValue(true);
						u.unlock();
					}
				}
			});
			if (mini) {
				SwingUtils.shrink(fb);
				SwingUtils.shrink(tb);
			}
			ButtonGroup bg = new ButtonGroup();
			bg.add(fb);
			bg.add(tb);
			setLayout(new GridLayout(2,1));
			add(fb);
			add(tb);
		} else {
			cb = new JCheckBox(o.getLabel(o.getValue()));
			cb.setSelected(o.getValue());
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (u.lock()) {
						boolean v = cb.isSelected();
						cb.setText(o.getLabel(v));
						o.setValue(v);
						u.unlock();
					}
				}
			});
			fb = null;
			tb = null;
			if (mini) SwingUtils.shrink(cb);
			setLayout(new GridLayout(1,1));
			add(cb);
		}
	}
	
	public void update() {
		if (u.lock()) {
			if (cb != null) cb.setSelected(o.getValue());
			if (fb != null) fb.setSelected(!o.getValue());
			if (tb != null) tb.setSelected(o.getValue());
			u.unlock();
		}
	}
}
