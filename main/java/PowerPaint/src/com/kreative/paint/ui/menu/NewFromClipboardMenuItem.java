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

package com.kreative.paint.ui.menu;

import java.awt.Toolkit;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import com.kreative.paint.ui.CKPApplication;
import com.kreative.paint.ui.CKPDocument;

public class NewFromClipboardMenuItem extends CKPMenuItem {
	private static final long serialVersionUID = 1L;
	
	public NewFromClipboardMenuItem(CKPApplication app, CKPDocument doc) {
		super("File.NewFromClipboard", app, doc);
		setEnabled(app.enableNewFromClipboard());
		Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
			public void flavorsChanged(FlavorEvent e) {
				setEnabled(NewFromClipboardMenuItem.this.app.enableNewFromClipboard());
			}
		});
	}
	
	public void actionPerformed(ActionEvent e) {
		app.doNewFromClipboard();
	}
}
