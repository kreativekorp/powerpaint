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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Line2D;
import com.kreative.paint.draw.ShapeDrawObject;

public class LineTool extends AbstractPaintDrawTool
implements ToolOptions.DrawPerpendicular, ToolOptions.DrawFromCenter, ToolOptions.DrawMultiple {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,K,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.SHAPE;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private Shape makeShape1(ToolEvent e) {
		float sx = e.getPreviousClickedX();
		float sy = e.getPreviousClickedY();
		float x = e.getX();
		float y = e.getY();
		if (e.isShiftDown() != e.tc().drawPerpendicular()) {
			double t = Math.toDegrees(Math.atan2(y-sy, x-sx));
			int q = (int)Math.round(t / 15.0);
			switch (q) {
			case -12: case 0: case 12:
				y = sy;
				break;
			case -18: case -6: case 6: case 18:
				x = sx;
				break;
			case -21: case -15: case -9: case -3:
			case 3: case 9: case 15: case 21:
				{
					float w = Math.abs(x-sx);
					float h = Math.abs(y-sy);
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
				break;
			case -23: case -22: case -14: case -13:
			case -11: case -10: case -2: case -1:
			case 1: case 2: case 10: case 11:
			case 13: case 14: case 22: case 23:
				{
					// x is fine, but find a new y
					double a = Math.toRadians(Math.round(t / 15.0) * 15.0);
					y = sy + (x-sx) * (float)Math.tan(a);
				}
				break;
			case -20: case -19: case -17: case -16:
			case -8: case -7: case -5: case -4:
			case 4: case 5: case 7: case 8:
			case 16: case 17: case 19: case 20:
				{
					// y is fine, but find a new x
					double a = Math.toRadians(Math.round(t / 15.0) * 15.0);
					x = sx + (y-sy) / (float)Math.tan(a);
				}
				break;
			}
		}
		if (e.isAltDown() != e.tc().drawFromCenter()) {
			sx -= (x-sx);
			sy -= (y-sy);
		}
		return new Line2D.Float(sx, sy, x, y);
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		if (e.isCtrlDown() != e.tc().drawMultiple()) {
			Shape sh = makeShape1(e);
			ShapeDrawObject wsh = new ShapeDrawObject(sh, e.getPaintSettings());
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (e.isCtrlDown() != e.tc().drawMultiple()) {
			Shape sh = makeShape1(e);
			ShapeDrawObject wsh = new ShapeDrawObject(sh, e.getPaintSettings());
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		Shape sh = makeShape1(e);
		ShapeDrawObject wsh = new ShapeDrawObject(sh, e.getPaintSettings());
		if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
		else wsh.paint(e.getPaintGraphics());
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		if (e.isMouseDown() && e.isCtrlDown() != e.tc().drawMultiple()) {
			Shape sh = makeShape1(e);
			ShapeDrawObject wsh = new ShapeDrawObject(sh, e.getPaintSettings());
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean keyReleased(ToolEvent e) {
		if (e.isMouseDown() && e.isCtrlDown() != e.tc().drawMultiple()) {
			Shape sh = makeShape1(e);
			ShapeDrawObject wsh = new ShapeDrawObject(sh, e.getPaintSettings());
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			Shape sh = makeShape1(e);
			ShapeDrawObject wsh = new ShapeDrawObject(sh, e.getPaintSettings());
			wsh.paint(g);
			return true;
		}
		else return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
