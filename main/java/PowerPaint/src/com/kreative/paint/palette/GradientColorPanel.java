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
import com.kreative.paint.gradient.*;
import com.kreative.paint.res.MaterialsManager;
import com.kreative.paint.swing.*;

public class GradientColorPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private boolean eventexec = false;
	private CellSelectorModel<GradientColorMap> palmodel;
	private CellSelector<GradientColorMap> palcomp;
	private JScrollPane palsp;
	private Gradient gradient;
	
	public GradientColorPanel(PaintContext pc, MaterialsManager mm) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		palmodel = new DefaultCellSelectorModel<GradientColorMap>(mm.getGradientColors(), GradientManager.DEFAULT_COLORS);
		palcomp = new CellSelector<GradientColorMap>(palmodel, new CellSelectorRenderer<GradientColorMap>() {
			public int getCellHeight() { return 15; }
			public int getCellWidth() { return 96; }
			public int getColumns() { return 1; }
			public int getRows() { return 0; }
			public boolean isFixedHeight() { return true; }
			public boolean isFixedWidth() { return false; }
			public void paint(Graphics g, GradientColorMap object, int x, int y, int w, int h) {
				((Graphics2D)g).setPaint(new GradientPaint2(GradientManager.DEFAULT_SHAPE, object));
				g.fillRect(x, y, w, h);
			}
		});
		palsp = new JScrollPane(palcomp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gradient = GradientManager.DEFAULT_GRADIENT;
		
		palmodel.addCellSelectionListener(new CellSelectionListener<GradientColorMap>() {
			public void cellSelected(CellSelectionEvent<GradientColorMap> e) {
				if (!eventexec) {
					gradient = new Gradient(gradient.shape, e.getObject());
					GradientColorPanel.this.pc.setEditedEditedground(new GradientPaint2(gradient));
				}
			}
		});
		
		setLayout(new BorderLayout());
		add(palsp, BorderLayout.CENTER);
		update();
	}
	
	public CellSelector<GradientColorMap> asPopup() {
		return palcomp.asPopup();
	}
	
	public void update() {
		Paint p = pc.getEditedEditedground();
		if (p instanceof GradientPaint2) {
			gradient = ((GradientPaint2)p).getGradient();
			if (!eventexec) {
				eventexec = true;
				palmodel.setSelectedObject(gradient.colorMap);
				eventexec = false;
			}
		}
	}
	
	public Gradient getGradient() {
		return gradient;
	}
}
