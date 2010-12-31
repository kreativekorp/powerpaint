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

package com.kreative.paint.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;

public class SwingUtils {
	private SwingUtils() {}
	
	public static <C extends JComponent> C shrink(C c) {
		c.putClientProperty("JComponent.sizeVariant", "mini");
		if (!(c instanceof JSlider)) {
			c.setFont(c.getFont().deriveFont(9.0f));
		}
		if (c instanceof JSpinner) {
			JComponent e = ((JSpinner)c).getEditor();
			if (e != null) shrink(e);
		}
		if (c instanceof JSpinner.DefaultEditor) {
			JFormattedTextField f = ((JSpinner.DefaultEditor)c).getTextField();
			if (f != null) shrink(f);
		}
		if (c instanceof JSpinner.DateEditor) {
			JFormattedTextField f = ((JSpinner.DateEditor)c).getTextField();
			if (f != null) shrink(f);
		}
		if (c instanceof JSpinner.NumberEditor) {
			JFormattedTextField f = ((JSpinner.NumberEditor)c).getTextField();
			if (f != null) shrink(f);
		}
		if (c instanceof JSpinner.ListEditor) {
			JFormattedTextField f = ((JSpinner.ListEditor)c).getTextField();
			if (f != null) shrink(f);
		}
		if (c instanceof JScrollPane) {
			JScrollBar v = ((JScrollPane)c).getVerticalScrollBar();
			if (v != null) shrink(v);
			JScrollBar h = ((JScrollPane)c).getHorizontalScrollBar();
			if (h != null) shrink(h);
		}
		for (Component c2 : c.getComponents()) {
			if (c2 instanceof JComponent) shrink((JComponent)c2);
		}
		return c;
	}
	
	public static void setDefaultButton(final JRootPane rp, final JButton b) {
		rp.setDefaultButton(b);
	}
	
	public static void setCancelButton(final JRootPane rp, final JButton b) {
		rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		rp.getActionMap().put("cancel", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ev) {
				b.doClick();
			}
		});
	}
	
	public static void setDontSaveButton(final JRootPane rp, final JButton b) {
		rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "dontSave");
		rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, rp.getToolkit().getMenuShortcutKeyMask()), "dontSave");
		rp.getActionMap().put("dontSave", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ev) {
				b.doClick();
			}
		});
	}
}
