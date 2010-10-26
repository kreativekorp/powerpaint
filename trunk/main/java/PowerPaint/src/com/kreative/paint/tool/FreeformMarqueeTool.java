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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.GeneralPath;

public class FreeformMarqueeTool extends MarqueeTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,K,K,0,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,0,0,0,K,0,
					K,0,0,0,0,0,0,K,K,0,0,0,0,0,0,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	protected Image getBWIcon() {
		return icon;
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
			commitSelection(e, currentPath);
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
}
