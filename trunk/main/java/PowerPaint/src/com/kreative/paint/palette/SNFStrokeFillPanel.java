/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

public class SNFStrokeFillPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Dimension sfsSize = new Dimension(31, 43);
	
	private SNFStrokeFillSelector f, s;
	
	public SNFStrokeFillPanel(PaintContext pc) {
		super(pc, 0);
		f = new SNFStrokeFillSelector(pc, false);
		s = new SNFStrokeFillSelector(pc, true);
		f.setMinimumSize(sfsSize);
		f.setPreferredSize(sfsSize);
		s.setMinimumSize(sfsSize);
		s.setPreferredSize(sfsSize);
		JPanel main = new JPanel(new GridLayout(1,2,6,6));
		main.add(f);
		main.add(s);
		main.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 8));
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
	}
	
	protected void pcChanged(PaintContext pc) {
		f.setPaintContext(pc);
		s.setPaintContext(pc);
	}
	
	public void update() {
		// nothing
	}
}
