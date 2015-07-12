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

public class GradientPresetPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private boolean eventexec = false;
	private CellSelectorModel<GradientPreset> palmodel;
	private CellSelector<GradientPreset> palcomp;
	private JScrollPane palsp;
	private GradientPaint2 paint;
	
	public GradientPresetPanel(PaintContext pc, MaterialsManager mm) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		palmodel = new DefaultCellSelectorModel<GradientPreset>(mm.getGradientPresets(), GradientPreset.BLACK_TO_WHITE);
		palcomp = new CellSelector<GradientPreset>(palmodel, new CellSelectorRenderer<GradientPreset>() {
			public int getCellHeight() { return 25; }
			public int getCellWidth() { return 25; }
			public int getColumns() { return 4; }
			public int getRows() { return 0; }
			public boolean isFixedHeight() { return true; }
			public boolean isFixedWidth() { return true; }
			public void paint(Graphics g, GradientPreset object, int x, int y, int w, int h) {
				((Graphics2D)g).setPaint(new GradientPaint2(object, null));
				g.fillRect(x, y, w, h);
			}
		});
		palsp = new JScrollPane(palcomp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		paint = GradientPaint2.BLACK_TO_WHITE;
		
		palmodel.addCellSelectionListener(new CellSelectionListener<GradientPreset>() {
			public void cellSelected(CellSelectionEvent<GradientPreset> e) {
				if (!eventexec) {
					paint = paint.derivePaint(e.getObject());
					GradientPresetPanel.this.pc.setEditedEditedground(paint);
				}
			}
		});
		
		setLayout(new BorderLayout());
		add(palsp, BorderLayout.CENTER);
		update();
	}
	
	public CellSelector<GradientPreset> asPopup() {
		return new CellSelector<GradientPreset>(palmodel, new CellSelectorRenderer<GradientPreset>() {
			public int getCellHeight() { return 25; }
			public int getCellWidth() { return 25; }
			public int getColumns() { return 0; }
			public int getRows() { return 0; }
			public boolean isFixedHeight() { return true; }
			public boolean isFixedWidth() { return true; }
			public void paint(Graphics g, GradientPreset object, int x, int y, int w, int h) {
				((Graphics2D)g).setPaint(new GradientPaint2(object, null));
				g.fillRect(x, y, w, h);
			}
		}, true);
	}
	
	public void update() {
		Paint p = pc.getEditedEditedground();
		if (p instanceof GradientPaint2) {
			paint = (GradientPaint2)p;
			if (!eventexec) {
				eventexec = true;
				palmodel.setSelectedObject(new GradientPreset(paint.shape, paint.colorMap, null));
				eventexec = false;
			}
		}
	}
	
	public GradientPaint2 getGradient() {
		return paint;
	}
}
