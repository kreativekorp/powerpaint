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
import com.kreative.paint.form.CharacterOption;
import com.kreative.paint.util.SwingUtils;
import com.kreative.paint.util.UpdateLock;

public class CharacterOptionUI extends JTextField implements FormOptionUI {
	private static final long serialVersionUID = 1L;
	private CharacterOption o;
	private UpdateLock u = new UpdateLock();
	
	public CharacterOptionUI(CharacterOption opt, boolean mini) {
		this.o = opt;
		setText(Character.toString(o.getValue()));
		setColumns(2);
		setEditable(false);
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				e.consume();
			}
			public void keyReleased(KeyEvent e) {
				e.consume();
			}
			public void keyTyped(KeyEvent e) {
				if ((e.getKeyChar() >= 0x20 && e.getKeyChar() < 0x7F) || (e.getKeyChar() >= 0xA0 && e.getKeyChar() < 0xFFFD)) {
					if (u.lock()) {
						setText(Character.toString(e.getKeyChar()));
						o.setValue(e.getKeyChar());
						u.unlock();
					}
				}
				e.consume();
			}
		});
		if (mini) SwingUtils.shrink(this);
	}
	
	public void update() {
		if (u.lock()) {
			setText(Character.toString(o.getValue()));
			u.unlock();
		}
	}
}
