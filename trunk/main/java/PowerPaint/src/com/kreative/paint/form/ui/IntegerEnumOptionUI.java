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

import java.awt.event.*;
import javax.swing.*;
import com.kreative.paint.form.IntegerEnumOption;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class IntegerEnumOptionUI extends JComboBox implements FormOptionUI {
	private static final long serialVersionUID = 1L;
	private IntegerEnumOption o;
	private UpdateLock u = new UpdateLock();
	
	public IntegerEnumOptionUI(IntegerEnumOption opt, boolean mini) {
		this.o = opt;
		int sel = o.getValue();
		int selidx = 0;
		final int[] ints = o.values();
		final String[] strings = new String[ints.length];
		for (int i = 0; i < ints.length; i++) {
			strings[i] = o.getLabel(ints[i]);
			if (ints[i] == sel) selidx = i;
		}
		setModel(new DefaultComboBoxModel(strings));
		setEditable(false);
		setMaximumRowCount(32);
		setSelectedIndex(selidx);
		addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (u.lock()) {
					o.setValue(ints[getSelectedIndex()]);
					u.unlock();
				}
			}
		});
		if (mini) SwingUtils.shrink(this);
	}
	
	public void update() {
		if (u.lock()) {
			int sel = o.getValue();
			int selidx = 0;
			final int[] ints = o.values();
			for (int i = 0; i < ints.length; i++) {
				if (ints[i] == sel) selidx = i;
			}
			setSelectedIndex(selidx);
			u.unlock();
		}
	}
}
