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
import java.awt.geom.GeneralPath;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.draw.QuickShadowDrawObject;
import com.kreative.paint.draw.ShapeDrawObject;

// refactor this to use curves instead of lines ???

public class FreeformShapeTool extends AbstractPaintDrawTool implements ToolOptions.QuickShadow {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,K,K,0,0,0,0,0,0,K,K,K,0,0,
					0,K,0,0,0,K,K,0,0,0,K,0,0,0,K,0,
					K,0,0,0,0,0,0,K,K,K,0,0,0,0,0,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,K,K,0,0,0,0,0,K,K,0,0,0,0,
					0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,0,
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
	
	private GeneralPath currentPath;
	
	public boolean toolSelected(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		currentPath = new GeneralPath();
		currentPath.moveTo(e.getX(), e.getY());
		return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (currentPath != null) {
			currentPath.lineTo(e.getX(), e.getY());
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (currentPath != null) {
			currentPath.lineTo(e.getX(), e.getY());
			if (e.isCtrlDown()) {
				currentPath.closePath();
			}
			e.beginTransaction(getName());
			DrawObject wsh;
			if (e.tc().useShadow()) {
				wsh = new QuickShadowDrawObject(currentPath, e.tc().getShadowType(), e.tc().getShadowOpacity(), e.tc().getShadowXOffset(), e.tc().getShadowYOffset(), e.getPaintSettings());
			} else {
				wsh = new ShapeDrawObject(currentPath, e.getPaintSettings());
			}
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			e.commitTransaction();
			currentPath = null;
			return true;
		}
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (currentPath != null) {
			e.getPaintSettings().applyDraw(g);
			g.draw(currentPath);
			return true;
		}
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
}
