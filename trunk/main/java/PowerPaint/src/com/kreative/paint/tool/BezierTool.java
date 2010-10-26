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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.draw.QuickShadowDrawObject;
import com.kreative.paint.draw.ShapeDrawObject;

public class BezierTool extends AbstractPaintDrawTool implements ToolOptions.QuickShadow {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,
					0,0,0,0,0,K,K,K,0,0,K,K,0,0,0,0,
					0,0,0,K,K,0,0,0,K,0,0,0,0,0,0,K,
					0,0,K,0,0,0,0,0,K,0,0,0,0,0,K,K,
					0,K,0,0,0,0,0,K,0,0,0,0,0,K,0,K,
					0,K,0,0,0,0,0,K,0,0,0,K,K,0,0,K,
					0,K,0,0,K,K,0,0,K,K,K,0,0,0,0,K,
					0,K,0,0,K,K,0,0,0,0,0,0,0,0,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
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
	
	private GeneralPath currentPath = null;
	private float[] currentPathSegment = new float[4];
	private int currentPathSize = 0;
	
	public boolean toolSelected(ToolEvent e) {
		currentPath = null;
		currentPathSegment = new float[4];
		currentPathSize = 0;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPath = null;
		currentPathSegment = new float[4];
		currentPathSize = 0;
		return false;
	}
	
	private void paint(ToolEvent e) {
		DrawObject wsh;
		if (e.tc().useShadow()) {
			wsh = new QuickShadowDrawObject(currentPath, e.tc().getShadowType(), e.tc().getShadowOpacity(), e.tc().getShadowXOffset(), e.tc().getShadowYOffset(), e.getPaintSettings());
		} else {
			wsh = new ShapeDrawObject(currentPath, e.getPaintSettings());
		}
		if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
		else wsh.paint(e.getPaintGraphics());
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (currentPath != null) {
			e.getPaintSettings().applyDraw(g);
			g.draw(currentPath);
			float x = e.getX();
			float y = e.getY();
			if (e.isShiftDown()) {
				float sx, sy;
				switch (currentPathSize) {
				case 0:
					sx = (float)currentPath.getCurrentPoint().getX();
					sy = (float)currentPath.getCurrentPoint().getY();
					break;
				case 1:
					sx = currentPathSegment[0];
					sy = currentPathSegment[1];
					break;
				case 2:
					sx = currentPathSegment[2];
					sy = currentPathSegment[3];
					break;
				default:
					sx = (float)currentPath.getCurrentPoint().getX();
					sy = (float)currentPath.getCurrentPoint().getY();
					break;
				}
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
			float sx = (float)currentPath.getCurrentPoint().getX();
			float sy = (float)currentPath.getCurrentPoint().getY();
			switch (currentPathSize) {
			case 0:
				e.getPaintSettings().applyDraw(g);
				g.draw(new Line2D.Float(sx, sy, x, y));
				break;
			case 1:
				g.setColor(Color.lightGray);
				g.setStroke(new BasicStroke(1));
				g.draw(new Line2D.Float(sx, sy, currentPathSegment[0], currentPathSegment[1]));
				g.draw(new Line2D.Float(currentPathSegment[0], currentPathSegment[1], x, y));
				e.getPaintSettings().applyDraw(g);
				g.draw(new QuadCurve2D.Float(sx, sy, currentPathSegment[0], currentPathSegment[1], x, y));
				break;
			case 2:
				g.setColor(Color.lightGray);
				g.setStroke(new BasicStroke(1));
				g.draw(new Line2D.Float(sx, sy, currentPathSegment[0], currentPathSegment[1]));
				g.draw(new Line2D.Float(currentPathSegment[0], currentPathSegment[1], currentPathSegment[2], currentPathSegment[3]));
				g.draw(new Line2D.Float(currentPathSegment[2], currentPathSegment[3], x, y));
				e.getPaintSettings().applyDraw(g);
				g.draw(new CubicCurve2D.Float(sx, sy, currentPathSegment[0], currentPathSegment[1], currentPathSegment[2], currentPathSegment[3], x, y));
				break;
			}
			return true;
		}
		return false;
	}
	
	public boolean mouseClicked(ToolEvent e) {
		float x = e.getX();
		float y = e.getY();
		if (currentPath == null) {
			currentPath = new GeneralPath();
			currentPathSize = 0;
			currentPath.moveTo(x, y);
			return false;
		}
		else if (e.getClickCount() > 1) {
			if (e.isShiftDown()) {
				float sx, sy;
				switch (currentPathSize) {
				case 0:
					sx = (float)currentPath.getCurrentPoint().getX();
					sy = (float)currentPath.getCurrentPoint().getY();
					break;
				case 1:
					sx = currentPathSegment[0];
					sy = currentPathSegment[1];
					break;
				case 2:
					sx = currentPathSegment[2];
					sy = currentPathSegment[3];
					break;
				default:
					sx = (float)currentPath.getCurrentPoint().getX();
				sy = (float)currentPath.getCurrentPoint().getY();
				break;
				}
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
			switch (currentPathSize) {
			case 0:
				currentPath.lineTo(x, y);
				break;
			case 1:
				currentPath.quadTo(currentPathSegment[0], currentPathSegment[1], x, y);
				break;
			case 2:
				currentPath.curveTo(currentPathSegment[0], currentPathSegment[1], currentPathSegment[2], currentPathSegment[3], x, y);
				break;
			}
			currentPathSize = 0;
			if (e.isCtrlDown()) {
				currentPath.closePath();
			}
			e.beginTransaction(getName());
			paint(e);
			e.commitTransaction();
			currentPath = null;
			return true;
		}
		else {
			if (e.isShiftDown()) {
				float sx, sy;
				switch (currentPathSize) {
				case 0:
					sx = (float)currentPath.getCurrentPoint().getX();
					sy = (float)currentPath.getCurrentPoint().getY();
					break;
				case 1:
					sx = currentPathSegment[0];
					sy = currentPathSegment[1];
					break;
				case 2:
					sx = currentPathSegment[2];
					sy = currentPathSegment[3];
					break;
				default:
					sx = (float)currentPath.getCurrentPoint().getX();
				sy = (float)currentPath.getCurrentPoint().getY();
				break;
				}
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
			if (currentPathSize >= 2 || e.isCtrlDown()) {
				switch (currentPathSize) {
				case 0:
					currentPath.lineTo(x, y);
					break;
				case 1:
					currentPath.quadTo(currentPathSegment[0], currentPathSegment[1], x, y);
					break;
				case 2:
					currentPath.curveTo(currentPathSegment[0], currentPathSegment[1], currentPathSegment[2], currentPathSegment[3], x, y);
					break;
				}
				currentPathSize = 0;
			}
			else {
				switch (currentPathSize) {
				case 0:
					currentPathSegment[0] = x;
					currentPathSegment[1] = y;
					currentPathSize = 1;
					break;
				case 1:
					currentPathSegment[2] = x;
					currentPathSegment[3] = y;
					currentPathSize = 2;
					break;
				}
			}
			return false;
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_CLEAR:
			currentPath = null;
			return false;
		case KeyEvent.VK_ENTER:
			if (currentPath != null) {
				float x = e.getX();
				float y = e.getY();
				if (e.isShiftDown()) {
					float sx, sy;
					switch (currentPathSize) {
					case 0:
						sx = (float)currentPath.getCurrentPoint().getX();
						sy = (float)currentPath.getCurrentPoint().getY();
						break;
					case 1:
						sx = currentPathSegment[0];
						sy = currentPathSegment[1];
						break;
					case 2:
						sx = currentPathSegment[2];
						sy = currentPathSegment[3];
						break;
					default:
						sx = (float)currentPath.getCurrentPoint().getX();
					sy = (float)currentPath.getCurrentPoint().getY();
					break;
					}
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
				switch (currentPathSize) {
				case 0:
					currentPath.lineTo(x, y);
					break;
				case 1:
					currentPath.quadTo(currentPathSegment[0], currentPathSegment[1], x, y);
					break;
				case 2:
					currentPath.curveTo(currentPathSegment[0], currentPathSegment[1], currentPathSegment[2], currentPathSegment[3], x, y);
					break;
				}
				currentPathSize = 0;
				if (e.isCtrlDown()) {
					currentPath.closePath();
				}
				e.beginTransaction(getName());
				paint(e);
				e.commitTransaction();
				currentPath = null;
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
