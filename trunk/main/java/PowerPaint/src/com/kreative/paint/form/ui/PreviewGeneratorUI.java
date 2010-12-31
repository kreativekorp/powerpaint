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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import com.kreative.paint.form.PreviewGenerator;

public class PreviewGeneratorUI extends JComponent implements FormOptionUI, FormMetrics {
	private static final long serialVersionUID = 1L;
	private PreviewGenerator pg;
	
	public PreviewGeneratorUI(PreviewGenerator opt, boolean mini) {
		this.pg = opt;
		Border inner = BorderFactory.createEmptyBorder(mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING, mini ? GRID_SPACING_MINI : GRID_SPACING);
		Border outer = BorderFactory.createLineBorder(Color.black);
		setBorder(BorderFactory.createCompoundBorder(outer, inner));
		Dimension d = new Dimension(mini ? PREVIEW_WIDTH_MINI : PREVIEW_WIDTH, mini ? PREVIEW_WIDTH_MINI : PREVIEW_WIDTH);
		setMinimumSize(d);
		setPreferredSize(d);
		setMaximumSize(d);
	}
	
	protected void paintComponent(Graphics g) {
		int w = getWidth();
		int h = getHeight();
		Insets i = getInsets();
		Rectangle r = new Rectangle(i.left, i.top, w-i.left-i.right, h-i.top-i.bottom);
		Graphics2D g2 = (Graphics2D)g;
		pg.generatePreview(g2, r);
	}
	
	public void update() {
		repaint();
	}
}
