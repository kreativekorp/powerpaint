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

public class SNFPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private SNFStrokeFillPanel sfp;
	private SNFPresetsPanel psp;
	private SNFPaintPanel ptp;
	private SNFPCSFAPanel pcsfap;
	private SNFCustomPaintPanel cpp;
	
	public SNFPanel(
			PaintContext pc,
			ColorPalettePalette cpal, PaintContextPalette[] ocpal, TexturePalette tpal, GradientPalette gpal,
			PatternPalette ppal, CompositePalette cxpal, StrokePalette spal, FontPalette fpal
	) {
		super(pc, 0);
		sfp = new SNFStrokeFillPanel(pc);
		psp = new SNFPresetsPanel(pc);
		ptp = new SNFPaintPanel(pc, cpal, ocpal, tpal, gpal);
		pcsfap = new SNFPCSFAPanel(pc, ppal, cxpal, spal, fpal);
		cpp = new SNFCustomPaintPanel(pc);
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(cpp, BorderLayout.CENTER);
		p1.add(pcsfap, BorderLayout.WEST);
		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(p1, BorderLayout.CENTER);
		p2.add(ptp, BorderLayout.WEST);
		JPanel p3 = new JPanel(new BorderLayout());
		p3.add(p2, BorderLayout.CENTER);
		p3.add(psp, BorderLayout.WEST);
		JPanel p4 = new JPanel(new BorderLayout());
		p4.add(p3, BorderLayout.CENTER);
		p4.add(sfp, BorderLayout.WEST);
		setLayout(new BorderLayout());
		add(p4, BorderLayout.CENTER);
	}
	
	protected void cpChanged(PaintContext pc) {
		sfp.setPaintContext(pc);
		psp.setPaintContext(pc);
		ptp.setPaintContext(pc);
		pcsfap.setPaintContext(pc);
		cpp.setPaintContext(pc);
	}
	
	public void update() {
		// nothing
	}
}
