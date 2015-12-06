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
import com.kreative.paint.PaintContext;
import com.kreative.paint.material.colorpalette.CheckerboardPaint;

public class SNFPaintSelector<T extends Paint> extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private Class<T> paintclass;
	private T lastfg;
	private T lastbg;
	
	public SNFPaintSelector(PaintContext pc, Class<T> paintclass, T deffg, T defbg) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		this.paintclass = paintclass;
		this.lastfg = deffg;
		this.lastbg = defbg;
		update();
	}
	
	public void update() {
		Paint fg = pc.getEditedForeground();
		Paint bg = pc.getEditedBackground();
		if (fg != null && paintclass.isAssignableFrom(fg.getClass())) lastfg = paintclass.cast(fg);
		if (bg != null && paintclass.isAssignableFrom(bg.getClass())) lastbg = paintclass.cast(bg);
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
		int by = y+5;
		int bw = w;
		int bh = h-10;
		int cw = bw * 2 / 3;
		int ch = bh * 2 / 3;
		Paint fg = pc.getEditedForeground();
		Paint bg = pc.getEditedBackground();
		if (fg != null && paintclass.isAssignableFrom(fg.getClass())) {
			g.drawImage(pc.isEditingForeground() ? PaletteUtilities.ARROW_4U : PaletteUtilities.ARROW_4UH, bx+(cw-7)/2, y+h-4, null);
		}
		if (bg != null && paintclass.isAssignableFrom(bg.getClass())) {
			g.drawImage(pc.isEditingBackground() ? PaletteUtilities.ARROW_4D : PaletteUtilities.ARROW_4DH, bx+bw-(cw+7)/2, y, null);
		}
		g.setColor(Color.black);
		g.fillRect(bx+bw-cw+1, by+1, cw-1, ch-1);
		g.fillRect(bx+bw-cw, by, cw-1, ch-1);
		g.setColor(Color.white);
		g.fillRect(bx+bw-cw+1, by+1, cw-3, ch-3);
		((Graphics2D)g).setPaint(CheckerboardPaint.LIGHT);
		g.fillRect(bx+bw-cw+2, by+2, cw-5, ch-5);
		((Graphics2D)g).setPaint(lastbg);
		g.fillRect(bx+bw-cw+2, by+2, cw-5, ch-5);
		g.setColor(Color.black);
		g.fillRect(bx+1, by+bh-ch+1, cw-1, ch-1);
		g.fillRect(bx, by+bh-ch, cw-1, ch-1);
		g.setColor(Color.white);
		g.fillRect(bx+1, by+bh-ch+1, cw-3, ch-3);
		((Graphics2D)g).setPaint(CheckerboardPaint.LIGHT);
		g.fillRect(bx+2, by+bh-ch+2, cw-5, ch-5);
		((Graphics2D)g).setPaint(lastfg);
		g.fillRect(bx+2, by+bh-ch+2, cw-5, ch-5);
	}
	
	public int getClickedArea(int mx, int my) {
		Insets i = getInsets();
		int x = i.left;
		int y = i.top;
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		int bx = x;
		int by = y+5;
		int bw = w;
		int bh = h-10;
		int cw = bw * 2 / 3;
		int ch = bh * 2 / 3;
		if (mx < bx || my < by || mx >= bx+bw || my >= by+bh) return 0;
		else if (mx < bx+cw && my >= by+bh-ch) return 1;
		else if (mx >= bx+bw-cw && my < by+ch) return 2;
		else return 0;
	}
	
	public boolean doClick(int mx, int my) {
		switch (getClickedArea(mx, my)) {
		case 1:
			pc.setEditingForeground(true);
			pc.setEditedForeground(lastfg);
			return true;
		case 2:
			pc.setEditingBackground(true);
			pc.setEditedBackground(lastbg);
			return true;
		default:
			return false;
		}
	}
}
