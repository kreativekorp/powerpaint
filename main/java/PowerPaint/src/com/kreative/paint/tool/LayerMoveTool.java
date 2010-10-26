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
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import com.kreative.paint.Layer;
import com.kreative.paint.util.CursorUtils;

public class LayerMoveTool extends AbstractPaintDrawSelectionTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,K,0,0,0,0,K,0,0,0,0,K,0,0,
					0,0,K,K,0,0,0,0,K,0,0,0,0,K,K,0,
					0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					0,0,K,K,0,0,0,0,K,0,0,0,0,K,K,0,
					0,0,0,K,0,0,0,0,K,0,0,0,0,K,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
			}
	);
	
	private Map<Layer, Point2D.Float> o = new HashMap<Layer, Point2D.Float>();
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		e.getCanvas().pushPaintSelection();
		o.clear();
		for (Layer l : e.getCanvas()) {
			if (l.isEditable()) {
				o.put(l, new Point2D.Float(l.getX()-e.getCanvasX(), l.getY()-e.getCanvasY()));
			}
		}
		return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		for (Layer l : o.keySet()) {
			Point2D.Float op = o.get(l);
			float x = e.getCanvasX();
			float y = e.getCanvasY();
			if (e.isShiftDown()) {
				float sx = e.getCanvasPreviousClickedX();
				float sy = e.getCanvasPreviousClickedY();
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
			l.setLocation(new Point((int)(x+op.x), (int)(y+op.y)));
		}
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		for (Layer l : o.keySet()) {
			Point2D.Float op = o.get(l);
			float x = e.getCanvasX();
			float y = e.getCanvasY();
			if (e.isShiftDown()) {
				float sx = e.getCanvasPreviousClickedX();
				float sy = e.getCanvasPreviousClickedY();
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
			l.setLocation(new Point((int)(x+op.x), (int)(y+op.y)));
		}
		o.clear();
		e.getCanvas().pushPaintSelection();
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		if (!e.isMouseDown()) {
			Point p;
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				e.beginTransaction(getName());
				e.getCanvas().pushPaintSelection();
				for (Layer l : e.getCanvas()) {
					if (l.isEditable()) {
						p = l.getLocation();
						p.x--;
						l.setLocation(p);
					}
				}
				e.getCanvas().pushPaintSelection();
				e.commitTransaction();
				return true;
			case KeyEvent.VK_RIGHT:
				e.beginTransaction(getName());
				e.getCanvas().pushPaintSelection();
				for (Layer l : e.getCanvas()) {
					if (l.isEditable()) {
						p = l.getLocation();
						p.x++;
						l.setLocation(p);
					}
				}
				e.getCanvas().pushPaintSelection();
				e.commitTransaction();
				return true;
			case KeyEvent.VK_UP:
				e.beginTransaction(getName());
				e.getCanvas().pushPaintSelection();
				for (Layer l : e.getCanvas()) {
					if (l.isEditable()) {
						p = l.getLocation();
						p.y--;
						l.setLocation(p);
					}
				}
				e.getCanvas().pushPaintSelection();
				e.commitTransaction();
				return true;
			case KeyEvent.VK_DOWN:
				e.beginTransaction(getName());
				e.getCanvas().pushPaintSelection();
				for (Layer l : e.getCanvas()) {
					if (l.isEditable()) {
						p = l.getLocation();
						p.y++;
						l.setLocation(p);
					}
				}
				e.getCanvas().pushPaintSelection();
				e.commitTransaction();
				return true;
			}
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return CursorUtils.CURSOR_MOVE;
	}
}
