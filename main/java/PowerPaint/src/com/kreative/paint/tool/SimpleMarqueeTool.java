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

import java.awt.Graphics2D;
import java.awt.Shape;

public abstract class SimpleMarqueeTool extends MarqueeTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter {
	protected abstract Shape makeShape(ToolEvent e, float x1, float y1, float x2, float y2);
	
	private Shape makeShape1(ToolEvent e) {
		float sx = e.getCanvasPreviousClickedX();
		float sy = e.getCanvasPreviousClickedY();
		float x = e.getCanvasX();
		float y = e.getCanvasY();
		if (e.isShiftDown() != e.tc().drawSquare()) {
			float w = Math.abs(x-sx);
			float h = Math.abs(y-sy);
			float s = Math.max(w, h);
			if (y > sy) y = sy+s;
			else y = sy-s;
			if (x > sx) x = sx+s;
			else x = sx-s;
		}
		if (e.tc().drawFromCenter()) {
			sx -= (x-sx);
			sy -= (y-sy);
		}
		return makeShape(e, sx, sy, x, y);
	}
	
	protected boolean mousePressedImpl(ToolEvent e) {
		beginSelection(e);
		return true;
	}
	
	protected boolean mouseReleasedImpl(ToolEvent e) {
		commitSelection(e, makeShape1(e));
		return true;
	}
	
	protected boolean paintIntermediateImpl(ToolEvent e, Graphics2D g) {
		if (isSelectionInProgress()) {
			ToolUtilities.fillSelectionShape(e, g, makeShape1(e));
		}
		return true;
	}
}
