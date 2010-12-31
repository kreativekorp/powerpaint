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

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.draw.QuickShadowDrawObject;
import com.kreative.paint.draw.ShapeDrawObject;

public class PolygonTool extends AbstractPaintDrawTool implements ToolOptions.QuickShadow {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,K,K,K,K,
					0,0,0,0,0,0,K,0,0,0,0,0,0,0,K,0,
					0,0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,K,0,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,K,0,
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
	
	private GeneralPath currentPoly;
	
	public boolean toolSelected(ToolEvent e) {
		currentPoly = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPoly = null;
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (currentPoly != null) {
			e.getPaintSettings().applyDraw(g);
			g.draw(currentPoly);
			float sx = (float)currentPoly.getCurrentPoint().getX();
			float sy = (float)currentPoly.getCurrentPoint().getY();
			float x = e.getX();
			float y = e.getY();
			if (e.isShiftDown()) {
				float w = Math.abs(x-sx);
				float h = Math.abs(y-sy);
				if (w > h*2) y = sy;
				else if (h > w*2) x = sx;
				else {
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
			}
			g.draw(new Line2D.Float(sx, sy, x, y));
			return true;
		}
		return false;
	}
	
	public boolean mouseClicked(ToolEvent e) {
		if (currentPoly == null) {
			currentPoly = new GeneralPath();
			currentPoly.moveTo(e.getX(), e.getY());
			return false;
		}
		else if (currentPoly.getCurrentPoint().getX() == e.getX() || currentPoly.getCurrentPoint().getY() == e.getY()) {
			currentPoly.closePath();
			e.beginTransaction(getName());
			DrawObject wsh;
			if (e.tc().useShadow()) {
				wsh = new QuickShadowDrawObject(currentPoly, e.tc().getShadowType(), e.tc().getShadowOpacity(), e.tc().getShadowXOffset(), e.tc().getShadowYOffset(), e.getPaintSettings());
			} else {
				wsh = new ShapeDrawObject(currentPoly, e.getPaintSettings());
			}
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			e.commitTransaction();
			currentPoly = null;
			return true;
		}
		else if (e.getClickCount() > 1) {
			float x = e.getX();
			float y = e.getY();
			if (e.isShiftDown()) {
				float sx = (float)currentPoly.getCurrentPoint().getX();
				float sy = (float)currentPoly.getCurrentPoint().getY();
				float w = Math.abs(x-sx);
				float h = Math.abs(y-sy);
				if (w > h*2) y = sy;
				else if (h > w*2) x = sx;
				else {
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
			}
			currentPoly.lineTo(x, y);
			currentPoly.closePath();
			e.beginTransaction(getName());
			DrawObject wsh;
			if (e.tc().useShadow()) {
				wsh = new QuickShadowDrawObject(currentPoly, e.tc().getShadowType(), e.tc().getShadowOpacity(), e.tc().getShadowXOffset(), e.tc().getShadowYOffset(), e.getPaintSettings());
			} else {
				wsh = new ShapeDrawObject(currentPoly, e.getPaintSettings());
			}
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			e.commitTransaction();
			currentPoly = null;
			return true;
		}
		else {
			float x = e.getX();
			float y = e.getY();
			if (e.isShiftDown()) {
				float sx = (float)currentPoly.getCurrentPoint().getX();
				float sy = (float)currentPoly.getCurrentPoint().getY();
				float w = Math.abs(x-sx);
				float h = Math.abs(y-sy);
				if (w > h*2) y = sy;
				else if (h > w*2) x = sx;
				else {
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
			}
			currentPoly.lineTo(x, y);
			return false;
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_CLEAR:
			currentPoly = null;
			return false;
		case KeyEvent.VK_ENTER:
			if (currentPoly != null) {
				float x = e.getX();
				float y = e.getY();
				if (e.isShiftDown()) {
					float sx = (float)currentPoly.getCurrentPoint().getX();
					float sy = (float)currentPoly.getCurrentPoint().getY();
					float w = Math.abs(x-sx);
					float h = Math.abs(y-sy);
					if (w > h*2) y = sy;
					else if (h > w*2) x = sx;
					else {
						float s = Math.max(w, h);
						if (y > sy) y = sy+s;
						else y = sy-s;
						if (x > sx) x = sx+s;
						else x = sx-s;
					}
				}
				currentPoly.lineTo(x, y);
				currentPoly.closePath();
				e.beginTransaction(getName());
				DrawObject wsh;
				if (e.tc().useShadow()) {
					wsh = new QuickShadowDrawObject(currentPoly, e.tc().getShadowType(), e.tc().getShadowOpacity(), e.tc().getShadowXOffset(), e.tc().getShadowYOffset(), e.getPaintSettings());
				} else {
					wsh = new ShapeDrawObject(currentPoly, e.getPaintSettings());
				}
				if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
				else wsh.paint(e.getPaintGraphics());
				e.commitTransaction();
				currentPoly = null;
				return true;
			}
			else return false;
		}
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
