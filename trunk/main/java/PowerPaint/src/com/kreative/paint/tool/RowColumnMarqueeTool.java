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
import java.awt.Image;
import java.awt.geom.Rectangle2D;

public class RowColumnMarqueeTool extends MarqueeTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,K,K,K,0,0,K,K,0,K,K,K,0,0,K,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,K,0,0,K,K,K,0,K,K,0,0,K,K,K,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
			}
	);
	
	protected Image getBWIcon() {
		return icon;
	}
	
	protected boolean mousePressedImpl(ToolEvent e) {
		beginSelection(e);
		return true;
	}
	
	protected boolean mouseReleasedImpl(ToolEvent e) {
		Rectangle2D.Float r = new Rectangle2D.Float(
				Math.min(e.getCanvasX(), e.getCanvasPreviousClickedX()),
				Math.min(e.getCanvasY(), e.getCanvasPreviousClickedY()),
				Math.abs(e.getCanvasX()-e.getCanvasPreviousClickedX()),
				Math.abs(e.getCanvasY()-e.getCanvasPreviousClickedY())
		);
		if (r.width > r.height) {
			r.y = 0;
			r.height = e.getCanvas().getHeight();
		} else {
			r.x = 0;
			r.width = e.getCanvas().getWidth();
		}
		commitSelection(e, r);
		return true;
	}
	
	protected boolean paintIntermediateImpl(ToolEvent e, Graphics2D g) {
		if (isSelectionInProgress()) {
			Rectangle2D.Float r = new Rectangle2D.Float(
					Math.min(e.getCanvasX(), e.getCanvasPreviousClickedX()),
					Math.min(e.getCanvasY(), e.getCanvasPreviousClickedY()),
					Math.abs(e.getCanvasX()-e.getCanvasPreviousClickedX()),
					Math.abs(e.getCanvasY()-e.getCanvasPreviousClickedY())
			);
			if (r.width > r.height) {
				r.y = 0;
				r.height = e.getCanvas().getHeight();
			} else {
				r.x = 0;
				r.width = e.getCanvas().getWidth();
			}
			ToolUtilities.fillSelectionShape(e, g, r);
		}
		return true;
	}
	
	protected boolean shiftConstrainsCoordinatesImpl() {
		return true;
	}
}
