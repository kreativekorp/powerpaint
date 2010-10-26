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
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.draw.BrushStrokeDrawObject;
import com.kreative.paint.util.Bitmap;

public class BrushTool extends AbstractPaintDrawTool implements ToolOptions.Brushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,K,0,K,0,K,0,K,0,0,0,
					0,0,0,K,0,K,0,K,0,K,0,K,K,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private GeneralPath currentPoly;
	
	private Bitmap brushCache = null;
	private Cursor brushCursor = null;
	private int brushW = 0, brushH = 0;
	private void makeCache(ToolEvent e) {
		brushCache = e.tc().getBrush();
		brushCursor = brushCache.getCursor();
		brushW = brushCache.getWidth()/2;
		brushH = brushCache.getHeight()/2;
	}
	
	public boolean toolSelected(ToolEvent e) {
		currentPoly = null;
		makeCache(e);
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		makeCache(e);
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPoly = null;
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		if (brushCache == null) makeCache(e);
		e.beginTransaction(getName());
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			float x = e.getX() - brushW;
			float y = e.getY() - brushH;
			brushCache.paint(e.getPaintSurface(), g, (int)x, (int)y);
			return true;
		} else {
			currentPoly = new GeneralPath();
			currentPoly.moveTo(e.getX(), e.getY());
			return false;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (brushCache == null) makeCache(e);
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			float lastX = e.getPreviousX();
			float lastY = e.getPreviousY();
			float x = e.getX();
			float y = e.getY();
			drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			return true;
		} else if (currentPoly != null) {
			float x = e.getX();
			float y = e.getY();
			currentPoly.lineTo(x, y);
			return false;
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (brushCache == null) makeCache(e);
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			float lastX = e.getPreviousX();
			float lastY = e.getPreviousY();
			float x = e.getX();
			float y = e.getY();
			drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			e.commitTransaction();
			return true;
		} else if (currentPoly != null) {
			float x = e.getX();
			float y = e.getY();
			currentPoly.lineTo(x, y);
			BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPoly, e.getPaintSettings());
			e.getDrawSurface().add(o);
			currentPoly = null;
			e.commitTransaction();
			return true;
		}
		e.commitTransaction();
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (brushCache == null) makeCache(e);
		if (e.isInDrawMode() && currentPoly != null) {
			BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPoly, e.getPaintSettings());
			o.paint(g);
			return true;
		}
		return false;
	}
	
	private void drag(PaintSurface srf, Graphics2D g, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m - brushW;
			float y = sy + ((dy-sy)*i)/m - brushH;
			brushCache.paint(srf, g, (int)x, (int)y);
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
			e.tc().prevBrush();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
			e.tc().nextBrush();
			break;
		}
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		if (brushCursor == null) makeCache(e);
		return brushCursor;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
}
