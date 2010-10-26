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
import java.awt.Shape;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.draw.QuickShadowDrawObject;
import com.kreative.paint.draw.ShapeDrawObject;

public abstract class SimpleShapeTool extends AbstractPaintDrawTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter, ToolOptions.DrawMultiple, ToolOptions.QuickShadow {
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	protected abstract Shape makeShape(ToolEvent e, float x1, float y1, float x2, float y2);
	
	private Shape makeShape1(ToolEvent e) {
		float sx = e.getPreviousClickedX();
		float sy = e.getPreviousClickedY();
		float x = e.getX();
		float y = e.getY();
		if (e.isShiftDown() != e.tc().drawSquare()) {
			float w = Math.abs(x-sx);
			float h = Math.abs(y-sy);
			float s = Math.max(w, h);
			if (y > sy) y = sy+s;
			else y = sy-s;
			if (x > sx) x = sx+s;
			else x = sx-s;
		}
		if (e.isAltDown() != e.tc().drawFromCenter()) {
			sx -= (x-sx);
			sy -= (y-sy);
		}
		return makeShape(e, sx, sy, x, y);
	}
	
	private DrawObject makeObject(ToolEvent e) {
		Shape sh = makeShape1(e);
		if (e.tc().useShadow()) {
			return new QuickShadowDrawObject(sh, e.tc().getShadowType(), e.tc().getShadowOpacity(), e.tc().getShadowXOffset(), e.tc().getShadowYOffset(), e.getPaintSettings());
		} else {
			return new ShapeDrawObject(sh, e.getPaintSettings());
		}
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		if (e.isCtrlDown() != e.tc().drawMultiple()) {
			DrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (e.isCtrlDown() != e.tc().drawMultiple()) {
			DrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		DrawObject wsh = makeObject(e);
		if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
		else wsh.paint(e.getPaintGraphics());
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		if (e.isMouseDown() && e.isCtrlDown() != e.tc().drawMultiple()) {
			DrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean keyReleased(ToolEvent e) {
		if (e.isMouseDown() && e.isCtrlDown() != e.tc().drawMultiple()) {
			DrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			DrawObject wsh = makeObject(e);
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
