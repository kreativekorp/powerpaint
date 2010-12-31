/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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
import java.awt.event.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.awt.CheckerboardPaint;

public class SNFStrokeFillSelector extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private boolean myStroke;
	
	public SNFStrokeFillSelector(PaintContext pc, boolean myStroke) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		this.myStroke = myStroke;
		setToolTipText(PaletteUtilities.messages.getString(myStroke ? "snf.stroke" : "snf.fill"));
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				SNFStrokeFillSelector.this.pc.setEditingStroke(SNFStrokeFillSelector.this.myStroke);
			}
		});
	}
	
	public void update() {
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		Insets i = getInsets();
		int x = i.left;
		int y = i.top;
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		g.clearRect(x, y, w, h);
		int bx = x;
		int by = y+6;
		int bw = w;
		int bh = h-12;
		int cdx = bw/3;
		int cdy = bh/3;
		if (myStroke) {
			if (pc.isEditingStroke()) {
				g.drawImage(PaletteUtilities.ARROW_5U, bx+(bw-9)/2, y+h-5, null);
			}
			g.setColor(Color.black);
			g.fillRect(bx, by, bw, bh);
			g.setColor(Color.white);
			g.fillRect(bx+1, by+1, bw-2, bh-2);
			g.setColor(Color.black);
			g.fillPolygon(
					new int[]{bx+bw-cdx-1, bx+bw-1, bx+bw-1, bx+cdx, bx, bx},
					new int[]{by, by, by+cdy, by+bh-1, by+bh-1, by+bh-cdy-1},
					6);
			((Graphics2D)g).setPaint(CheckerboardPaint.LIGHT);
			g.fillPolygon(
					new int[]{bx+bw-cdx-1, bx+bw-1, bx+bw-1, bx+cdx-1, bx+1, bx+1},
					new int[]{by+1, by+1, by+cdy-1, by+bh-1, by+bh-1, by+bh-cdy-1},
					6);
			if (pc.isDrawn()) {
				((Graphics2D)g).setPaint(pc.getDrawPaint());
				g.fillPolygon(
						new int[]{bx+bw-cdx-1, bx+bw-1, bx+bw-1, bx+cdx-1, bx+1, bx+1},
						new int[]{by+1, by+1, by+cdy-1, by+bh-1, by+bh-1, by+bh-cdy-1},
						6);
			}
		} else {
			if (pc.isEditingFill()) {
				g.drawImage(PaletteUtilities.ARROW_5U, bx+(bw-9)/2, y+h-5, null);
			}
			g.setColor(Color.black);
			g.fillRect(bx, by, bw, bh);
			((Graphics2D)g).setPaint(CheckerboardPaint.LIGHT);
			g.fillRect(bx+1, by+1, bw-2, bh-2);
			if (pc.isFilled()) {
				((Graphics2D)g).setPaint(pc.getFillPaint());
				g.fillRect(bx+1, by+1, bw-2, bh-2);
			}
		}
	}
}
