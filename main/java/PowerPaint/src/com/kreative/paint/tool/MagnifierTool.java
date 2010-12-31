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
import java.awt.Image;
import com.kreative.paint.util.CursorUtils;

public class MagnifierTool extends AbstractViewTool {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,K,K,0,0,0,0,K,K,0,0,0,0,0,0,
					0,0,K,0,0,0,0,0,0,K,0,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,K,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,K,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,K,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,K,0,0,0,0,0,
					0,0,K,0,0,0,0,0,0,K,0,0,0,0,0,0,
					0,0,K,K,0,0,0,0,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,
			}
	);
	private static final Cursor cursIn = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,K,K,W,W,W,W,K,K,0,0,0,0,0,0,
					0,0,K,W,W,K,K,W,W,K,0,0,0,0,0,0,
					0,K,W,W,W,K,K,W,W,W,K,0,0,0,0,0,
					0,K,W,K,K,K,K,K,K,W,K,0,0,0,0,0,
					0,K,W,K,K,K,K,K,K,W,K,0,0,0,0,0,
					0,K,W,W,W,K,K,W,W,W,K,0,0,0,0,0,
					0,0,K,W,W,K,K,W,W,K,0,0,0,0,0,0,
					0,0,K,K,W,W,W,W,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,
			},
			6, 6,
			"Magnifier+"
	);
	private static final Cursor cursOut = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,K,K,W,W,W,W,K,K,0,0,0,0,0,0,
					0,0,K,W,W,W,W,W,W,K,0,0,0,0,0,0,
					0,K,W,W,W,W,W,W,W,W,K,0,0,0,0,0,
					0,K,W,K,K,K,K,K,K,W,K,0,0,0,0,0,
					0,K,W,K,K,K,K,K,K,W,K,0,0,0,0,0,
					0,K,W,W,W,W,W,W,W,W,K,0,0,0,0,0,
					0,0,K,W,W,W,W,W,W,K,0,0,0,0,0,0,
					0,0,K,K,W,W,W,W,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,
			},
			6, 6,
			"Magnifier-"
	);
	private static final Cursor curs100 = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,K,K,W,W,W,W,K,K,0,0,0,0,0,0,
					0,0,K,W,W,W,W,W,W,K,0,0,0,0,0,0,
					0,K,W,W,W,W,W,W,W,W,K,0,0,0,0,0,
					0,K,W,W,W,W,W,W,W,W,K,0,0,0,0,0,
					0,K,W,W,W,W,W,W,W,W,K,0,0,0,0,0,
					0,K,W,W,W,W,W,W,W,W,K,0,0,0,0,0,
					0,0,K,W,W,W,W,W,W,K,0,0,0,0,0,0,
					0,0,K,K,W,W,W,W,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,
			},
			6, 6,
			"Magnifier100"
	);
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean toolDoubleClicked(ToolEvent e) {
		e.getCanvasView().setScale(1.0f);
		return false;
	}
	
	public boolean mouseClicked(ToolEvent e) {
		if (e.isCtrlDown()) {
			e.getCanvasView().setScale(1.0f);
		} else if (e.isAltDown() || e.isShiftDown()) {
			e.getCanvasView().setScale(e.getCanvasView().getScale()/2);
		} else {
			e.getCanvasView().setScale(e.getCanvasView().getScale()*2);
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		if (e.isCtrlDown()) {
			return curs100;
		} else if (e.isAltDown() || e.isShiftDown()) {
			return cursOut;
		} else {
			return cursIn;
		}
	}
}
