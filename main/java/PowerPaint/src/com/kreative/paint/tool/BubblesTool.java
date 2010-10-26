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

package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.draw.ShapeDrawObject;
import com.kreative.paint.util.CursorUtils;

public class BubblesTool extends AbstractPaintDrawTool {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,K,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,K,0,0,0,0,K,
					0,0,0,0,0,K,K,K,0,0,K,0,0,0,0,K,
					0,0,0,0,K,0,0,0,K,0,K,0,0,0,0,K,
					0,0,0,0,K,0,0,0,K,0,K,0,0,0,0,K,
					0,K,0,0,K,0,0,0,K,0,0,K,K,K,K,0,
					K,0,K,0,0,K,K,K,0,0,0,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,K,K,0,0,0,K,0,0,0,0,K,K,0,
					0,0,K,0,0,K,0,K,0,K,0,0,K,0,0,0,
					0,0,K,0,0,K,0,0,K,0,0,0,K,0,0,0,
					0,0,0,K,K,0,0,0,0,0,0,0,0,K,K,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			13, 13,
			new int[] {
					0,0,0,0,0,0,0,W,W,W,0,0,0,
					0,0,0,0,0,0,W,K,K,K,W,0,0,
					0,0,0,0,0,W,K,W,W,W,K,W,0,
					0,0,0,0,W,K,W,0,0,0,W,K,W,
					0,0,0,0,W,K,W,0,0,0,W,K,W,
					0,0,0,0,W,K,W,0,0,0,W,K,W,
					0,0,0,0,0,W,K,W,W,W,K,W,0,
					0,0,0,0,W,K,W,K,K,K,W,0,0,
					0,0,0,W,K,W,0,W,W,W,0,0,0,
					0,0,W,K,W,0,0,0,0,0,0,0,0,
					0,W,K,W,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,
					0,W,0,0,0,0,0,0,0,0,0,0,0,
			},
			8, 4,
			"Bubbles"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}

	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		float x, y, w, h;
		if (e.isAltDown()) {
			x = Math.min(e.getPreviousX(), e.getX());
			y = Math.min(e.getPreviousY(), e.getY());
			w = Math.max(e.getPreviousX(), e.getX())-x;
			h = Math.max(e.getPreviousY(), e.getY())-y;
		} else {
			w = h = Math.max(Math.abs(e.getPreviousX()-e.getX()), Math.abs(e.getPreviousY()-e.getY())) - 2.0f;
			if (w <= 0.0f || h <= 0.0f) return false;
			x = (e.getPreviousX()+e.getX()-w)/2.0f;
			y = (e.getPreviousY()+e.getY()-h)/2.0f;
		}
		Shape sh;
		if (e.isCtrlDown()) {
			sh = new Rectangle2D.Float(x, y, w, h);
		} else {
			sh = new Ellipse2D.Float(x, y, w, h);
		}
		ShapeDrawObject wsh = new ShapeDrawObject(sh, e.getPaintSettings());
		if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
		else wsh.paint(e.getPaintGraphics());
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.commitTransaction();
		return false;
	}

	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
}
