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
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import com.kreative.paint.Layer;
import com.kreative.paint.geom.BitmapShape;
import com.kreative.paint.util.CursorUtils;

public class LassoTool extends MarqueeTool {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,0,0,K,K,0,0,
					0,0,0,K,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,K,0,0,0,0,0,0,0,0,0,K,K,0,0,0,
					0,0,K,K,K,0,0,0,K,K,K,0,0,0,0,0,
					0,K,K,0,0,K,K,K,0,0,0,0,0,0,0,0,
					0,K,0,K,0,K,0,0,0,0,0,0,0,0,0,0,
					0,0,K,K,K,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,W,W,W,W,W,0,0,0,0,
					0,0,0,0,W,W,W,K,K,K,K,K,W,W,0,0,
					0,0,0,W,K,K,K,W,W,W,W,W,K,K,W,0,
					0,0,W,K,W,W,W,0,0,0,0,0,W,W,K,W,
					0,W,K,W,0,0,0,0,0,0,0,0,0,W,K,W,
					W,K,W,0,0,0,0,0,0,0,0,0,0,W,K,W,
					W,K,W,0,0,0,0,0,0,0,0,W,W,K,W,0,
					W,K,W,W,W,0,0,0,W,W,W,K,K,W,0,0,
					0,W,K,K,K,W,W,W,K,K,K,W,W,0,0,0,
					W,K,K,W,W,K,K,K,W,W,W,0,0,0,0,0,
					W,K,W,K,W,K,W,W,0,0,0,0,0,0,0,0,
					0,W,K,K,K,W,0,0,0,0,0,0,0,0,0,0,
					0,0,W,W,K,W,0,0,0,0,0,0,0,0,0,0,
					0,0,0,W,K,W,0,0,0,0,0,0,0,0,0,0,
					0,0,W,K,W,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			3, 14,
			"Lasso"
	);
	
	protected Image getBWIcon() {
		return icon;
	}
	
	protected boolean selectAllLasso() {
		return true;
	}
	
	private GeneralPath currentPath;
	
	protected boolean toolSelectedImpl(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	protected boolean toolDeselectedImpl(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	protected boolean mousePressedImpl(ToolEvent e) {
		beginSelection(e);
		currentPath = new GeneralPath();
		currentPath.moveTo(e.getCanvasX(), e.getCanvasY());
		return false;
	}
	
	protected boolean mouseDraggedImpl(ToolEvent e) {
		if (currentPath != null) {
			currentPath.lineTo(e.getCanvasX(), e.getCanvasY());
		}
		return false;
	}
	
	protected boolean mouseReleasedImpl(ToolEvent e) {
		if (currentPath != null) {
			currentPath.lineTo(e.getCanvasX(), e.getCanvasY());
			currentPath.closePath();
			Layer l = e.getLayer();
			if (l == null) {
				commitSelection(e, null);
			} else {
				Rectangle r = currentPath.getBounds();
				boolean empty = true;
				int[] rgb = new int[r.width * r.height];
				l.getRGB(r.x-l.getX(), r.y-l.getY(), r.width, r.height, rgb, 0, r.width);
				for (int y = r.y, ay = 0; ay < rgb.length; y++, ay += r.width) {
					for (int x = r.x, ax = 0; ax < r.width; x++, ax++) {
						if (((rgb[ay+ax] & 0xFF000000) != 0) && currentPath.contains(x, y)) {
							empty = false;
							rgb[ay+ax] = 0xFF000000;
						} else {
							rgb[ay+ax] = 0;
						}
					}
				}
				if (empty) {
					commitSelection(e, null);
				} else {
					commitSelection(e, new BitmapShape(rgb, r.x, r.y, r.width, r.height));
				}
			}
			currentPath = null;
			return true;
		}
		return false;
	}
	
	protected boolean paintIntermediateImpl(ToolEvent e, Graphics2D g) {
		if (isSelectionInProgress() && currentPath != null) {
			ToolUtilities.drawSelectionShape(e, g, currentPath);
			return true;
		}
		return false;
	}
	
	protected boolean shiftConstrainsCoordinatesImpl() {
		return (isSelectionInProgress() && currentPath != null);
	}
	
	protected Cursor getDefaultCursor() {
		return curs;
	}
}
