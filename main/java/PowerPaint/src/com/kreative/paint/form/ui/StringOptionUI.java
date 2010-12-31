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

import javax.swing.*;
import javax.swing.event.*;
import com.kreative.paint.form.StringOption;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class StringOptionUI extends JTextField implements FormOptionUI {
	private static final long serialVersionUID = 1L;
	private StringOption o;
	private UpdateLock u = new UpdateLock();
	
	public StringOptionUI(StringOption opt, boolean mini) {
		this.o = opt;
		setText(o.getValue());
		if (o.getWidth() > 0) setColumns(o.getWidth());
		getDocument().addDocumentListener(new DocumentListener() {
			public void doit(DocumentEvent e) {
				if (u.lock()) {
					o.setValue(getText());
					u.unlock();
				}
			}
			public void changedUpdate(DocumentEvent e) { doit(e); }
			public void insertUpdate(DocumentEvent e) { doit(e); }
			public void removeUpdate(DocumentEvent e) { doit(e); }
		});
		if (mini) SwingUtils.shrink(this);
	}
	
	public void update() {
		if (u.lock()) {
			setText(o.getValue());
			u.unlock();
		}
	}
}
