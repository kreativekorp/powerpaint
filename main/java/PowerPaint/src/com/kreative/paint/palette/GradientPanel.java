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

package com.kreative.paint.palette;

import java.awt.*;
import javax.swing.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.gradient.*;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.swing.*;

public class GradientPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	private GradientPresetPanel gpp;
	private GradientShapePanel gsp;
	private GradientColorPanel gcp;
	
	public GradientPanel(PaintContext pc, MaterialsManager mm) {
		super(pc, 0);
		JPanel gppp = new JPanel(new BorderLayout(4,4));
		gppp.add(shrink(new JLabel(PaletteUtilities.messages.getString("gradients.presets"))), BorderLayout.PAGE_START);
		gppp.add((gpp = new GradientPresetPanel(pc, mm)), BorderLayout.CENTER);
		JPanel gspp = new JPanel(new BorderLayout(4,4));
		gspp.add(shrink(new JLabel(PaletteUtilities.messages.getString("gradients.shapes"))), BorderLayout.PAGE_START);
		gspp.add((gsp = new GradientShapePanel(pc, mm)), BorderLayout.CENTER);
		JPanel gcpp = new JPanel(new BorderLayout(4,4));
		gcpp.add(shrink(new JLabel(PaletteUtilities.messages.getString("gradients.colors"))), BorderLayout.PAGE_START);
		gcpp.add((gcp = new GradientColorPanel(pc, mm)), BorderLayout.CENTER);
		setLayout(new BorderLayout(8,8));
		add(gppp, BorderLayout.WEST);
		add(gspp, BorderLayout.CENTER);
		add(gcpp, BorderLayout.EAST);
		setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
	}
	
	protected void pcChanged(PaintContext pc) {
		gpp.setPaintContext(pc);
		gsp.setPaintContext(pc);
		gcp.setPaintContext(pc);
	}
	
	public void update() {
		// nothing
	}
	
	private JPopupPanel jpop = new JPopupPanel();
	public JPopupPanel getPopup(boolean usePresets) {
		if (usePresets) {
			CellSelector<Gradient> sel = gpp.asPopup();
			jpop.setContentPane(sel);
			jpop.hideOnRelease(sel);
		} else {
			CellSelector<GradientShape> ssel = gsp.asPopup();
			CellSelector<GradientColorMap> csel = gcp.asPopup();
			csel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
			JPanel p = new JPanel(new BorderLayout());
			p.add(ssel, BorderLayout.NORTH);
			p.add(csel, BorderLayout.CENTER);
			jpop.setContentPane(p);
			jpop.hideOnRelease(ssel);
			jpop.hideOnRelease(csel);
		}
		jpop.setResizable(false);
		jpop.pack();
		return jpop;
	}
	
	public Gradient getGradient() {
		return gpp.getGradient();
	}
	
	private static JLabel shrink(JLabel l) {
		l.setFont(l.getFont().deriveFont(10.0f));
		return l;
	}
}
