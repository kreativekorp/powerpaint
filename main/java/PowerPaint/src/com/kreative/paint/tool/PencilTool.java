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

package com.kreative.paint.tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.document.tile.PaintSurface;
import com.kreative.paint.draw.PencilStrokeDrawObject;
import com.kreative.paint.material.pattern.PatternPaint;
import com.kreative.paint.util.CursorUtils;

public class PencilTool extends AbstractPaintDrawTool {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,K,K,0,0,K,0,0,0,0,
					0,0,0,0,0,0,K,0,0,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,K,0,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,K,W,W,W,K,0,0,0,
					0,0,0,0,0,0,0,K,W,W,W,W,K,0,0,0,
					0,0,0,0,0,0,0,K,K,W,W,K,0,0,0,0,
					0,0,0,0,0,0,K,W,W,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,W,W,W,K,0,0,0,0,0,
					0,0,0,0,0,K,W,W,W,W,K,0,0,0,0,0,
					0,0,0,0,0,K,W,W,W,K,0,0,0,0,0,0,
					0,0,0,0,K,W,W,W,W,K,0,0,0,0,0,0,
					0,0,0,0,K,W,W,W,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,W,W,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
			},
			4, 15,
			"Pencil"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private boolean eraseMode;
	private GeneralPath currentPoly;
	
	public boolean toolSelected(ToolEvent e) {
		currentPoly = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPoly = null;
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		if (e.isInPaintMode()) {
			PaintSettings ps = e.getPaintSettings();
			PaintSurface p = e.getPaintSurface();
			int x = (int)Math.floor(e.getX());
			int y = (int)Math.floor(e.getY());
			Paint pnt = ps.getFillPaint();
			if (pnt instanceof PatternPaint) {
				pnt = ((PatternPaint)pnt).foreground;
			}
			if (pnt instanceof Color) {
				eraseMode = (p.getRGB(x,y) == ((Color)pnt).getRGB());
			} else {
				eraseMode = false;
			}
			if (eraseMode) {
				p.clear(x, y, 1, 1);
			} else {
				Graphics2D g = e.getPaintGraphics();
				ps.applyFill(g);
				g.setStroke(new BasicStroke(1));
				g.fillRect(x, y, 1, 1);
			}
			return true;
		} else {
			currentPoly = new GeneralPath();
			currentPoly.moveTo(e.getX(), e.getY());
			return false;
		}
	}
	
	private void drag(PaintSurface p, int sx, int sy, int dx, int dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			int x = sx + ((dx-sx)*i)/m;
			int y = sy + ((dy-sy)*i)/m;
			p.clear(x, y, 1, 1);
		}
	}

	public boolean mouseDragged(ToolEvent e) {
		if (e.isInPaintMode()) {
			int px = (int)Math.floor(e.getPreviousX());
			int py = (int)Math.floor(e.getPreviousY());
			int x = (int)Math.floor(e.getX());
			int y = (int)Math.floor(e.getY());
			if (eraseMode) {
				drag(e.getPaintSurface(), px, py, x, y);
			} else {
				Graphics2D g = e.getPaintGraphics();
				PaintSettings ps = e.getPaintSettings();
				ps.applyFill(g);
				g.setStroke(new BasicStroke(1));
				g.drawLine(px, py, x, y);
			}
			return true;
		} else if (currentPoly != null) {
			currentPoly.lineTo(e.getX(), e.getY());
			return false;
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (e.isInDrawMode() && currentPoly != null) {
			currentPoly.lineTo(e.getX(), e.getY());
			PencilStrokeDrawObject o = new PencilStrokeDrawObject(currentPoly, e.getPaintSettings());
			e.getDrawSurface().add(o);
			currentPoly = null;
			e.commitTransaction();
			return true;
		}
		e.commitTransaction();
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isInDrawMode() && currentPoly != null) {
			PencilStrokeDrawObject o = new PencilStrokeDrawObject(currentPoly, e.getPaintSettings());
			o.paint(g);
			return true;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
}
