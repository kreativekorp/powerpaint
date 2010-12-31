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

package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.draw.ShapeDrawObject;
import com.kreative.paint.draw.ThreeDBoxDrawObject;

public class ThreeDBoxTool extends AbstractPaintDrawTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter, ToolOptions.DrawFilled {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,
					0,K,K,0,0,0,0,0,K,K,0,0,0,0,0,0,
					0,K,0,K,0,0,0,0,K,0,K,K,0,0,0,0,
					0,K,0,0,K,0,0,0,K,0,0,0,K,0,0,0,
					0,K,0,0,0,K,K,K,K,K,K,K,K,K,K,0,
					0,K,0,0,0,K,0,0,K,0,0,0,0,0,K,0,
					0,K,0,0,0,K,0,0,K,0,0,0,0,0,K,0,
					0,K,K,K,K,K,K,K,K,0,0,0,0,0,K,0,
					0,0,K,0,0,K,0,0,0,K,0,0,0,0,K,0,
					0,0,0,K,0,K,0,0,0,0,K,0,0,0,K,0,
					0,0,0,K,0,K,0,0,0,0,0,K,0,0,K,0,
					0,0,0,0,K,K,0,0,0,0,0,0,K,0,K,0,
					0,0,0,0,0,K,0,0,0,0,0,0,0,K,K,0,
					0,0,0,0,0,K,K,K,K,K,K,K,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}

	private ThreeDBoxDrawObject theBox = null;
	private double lastX, lastY;
	
	public boolean toolSelected(ToolEvent e) {
		theBox = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		theBox = null;
		return false;
	}
	
	private Rectangle2D getDrawnBounds(ToolEvent e) {
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
		return new Rectangle2D.Float(Math.min(sx,x), Math.min(sy,y), Math.abs(x-sx), Math.abs(y-sy));
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (theBox != null) {
			double dx = e.getX()-lastX;
			double dy = e.getY()-lastY;
			if (e.isShiftDown() != e.tc().drawSquare()) {
				double s = Math.max(Math.abs(dx), Math.abs(dy));
				dx = Math.signum(dx)*s;
				dy = Math.signum(dy)*s;
			}
			theBox.setDX(dx);
			theBox.setDY(dy);
			theBox.setPaintSettings(e.getPaintSettings());
			if (e.isCtrlDown() == e.tc().drawFilled()) {
				theBox.setFillPaint(null);
			}
			theBox.paint(g);
			return true;
		} else if (e.isMouseDown()) {
			ShapeDrawObject o = new ShapeDrawObject(getDrawnBounds(e), e.getPaintSettings());
			if (e.isCtrlDown() == e.tc().drawFilled()) {
				o.setFillPaint(null);
			}
			o.paint(g);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (theBox == null) {
			Rectangle2D f = getDrawnBounds(e);
			theBox = new ThreeDBoxDrawObject(f.getMinX(), f.getMinY(), Math.abs(f.getWidth()), Math.abs(f.getHeight()), 0, 0);
			lastX = e.getX();
			lastY = e.getY();
			return true;
		} else {
			double dx = e.getX()-lastX;
			double dy = e.getY()-lastY;
			if (e.isShiftDown() != e.tc().drawSquare()) {
				double s = Math.max(Math.abs(dx), Math.abs(dy));
				dx = Math.signum(dx)*s;
				dy = Math.signum(dy)*s;
			}
			theBox.setDX(dx);
			theBox.setDY(dy);
			theBox.setPaintSettings(e.getPaintSettings());
			if (e.isCtrlDown() == e.tc().drawFilled()) {
				theBox.setFillPaint(null);
			}
			e.beginTransaction(getName());
			if (e.isInDrawMode()) e.getDrawSurface().add(theBox);
			else theBox.paint(e.getPaintGraphics());
			e.commitTransaction();
			theBox = null;
			return true;
		}
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
