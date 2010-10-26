/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.palette;

import java.awt.*;
import javax.swing.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.res.MaterialsManager;

public class StrokePalette extends PaintContextPalette {
	private static final long serialVersionUID = 1L;
	
	private StrokePresetPanel spp;
	private StrokeCapJoinPanel cjp;
	
	public StrokePalette(PaintContext pc, MaterialsManager mm) {
		super(pc);
		panels.add(spp = new StrokePresetPanel(pc, mm));
		panels.add(cjp = new StrokeCapJoinPanel(pc));
		JPanel bottom = new JPanel(new GridLayout(0,1,4,4));
		bottom.add(cjp);
		bottom.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		JPanel main = new JPanel(new BorderLayout());
		main.add(spp, BorderLayout.CENTER);
		main.add(bottom, BorderLayout.SOUTH);
		setContentPane(main);
		setResizable(false);
		pack();
	}
	
	public JPopupMenu getStrokePopup() {
		return spp.getStrokePopup();
	}
	
	public JPopupMenu getArrowPopup1() {
		return spp.getArrowPopup1();
	}
	
	public JPopupMenu getArrowPopup2() {
		return spp.getArrowPopup2();
	}
}
