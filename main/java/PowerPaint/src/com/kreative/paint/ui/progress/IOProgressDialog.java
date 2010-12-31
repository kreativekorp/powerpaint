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

package com.kreative.paint.ui.progress;

import java.awt.*;
import javax.swing.*;
import com.kreative.paint.format.*;
import com.kreative.paint.io.Monitor;
import com.kreative.paint.ui.UIUtilities;

public class IOProgressDialog extends JDialog implements Monitor {
	private static final long serialVersionUID = 1L;
	
	private DocumentIconProgressIndicator ind;
	private JLabel txt;
	
	public IOProgressDialog(Frame parent, Format fmt, int min, int max, boolean write) {
		super(parent, UIUtilities.messages.getString(write ? "savedlg.title" : "opendlg.title"), false);
		ind = new DocumentIconProgressIndicator(fmt.getExtension(), min, max, min);
		String txts = fmt.getName();
		if (!fmt.getExpandedName().equals(txts)) txts += " (" + fmt.getExpandedName() + ")";
		txt = new JLabel(UIUtilities.messages.getString(write ? "savedlg.progress" : "opendlg.progress").replace("$", txts));
		JPanel p = new JPanel(new BorderLayout(16,16));
		p.add(ind, BorderLayout.LINE_START);
		p.add(txt, BorderLayout.CENTER);
		p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		setContentPane(p);
		pack();
		setLocationRelativeTo(null);
		setLocation(getX(), getY()/2);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}
	
	public void startMonitoring() { setVisible(true); }
	public void stopMonitoring() { dispose(); }
	public int getMinimum() { return ind.getMinimum(); }
	public int getValue() { return ind.getValue(); }
	public int getMaximum() { return ind.getMaximum(); }
	public void setMinimum(int v) { ind.setMinimum(v); }
	public void setValue(int v) { ind.setValue(v); }
	public void setMaximum(int v) { ind.setMaximum(v); }
}
