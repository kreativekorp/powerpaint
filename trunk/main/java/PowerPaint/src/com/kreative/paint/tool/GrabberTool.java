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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import javax.swing.JScrollPane;
import com.kreative.paint.util.CursorUtils;

public class GrabberTool extends AbstractViewTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,K,K,0,K,0,0,K,K,K,0,0,0,0,
					0,0,K,0,0,K,K,0,0,K,0,0,K,0,0,0,
					0,0,K,0,0,K,K,0,0,K,0,0,K,0,K,0,
					0,0,0,K,0,0,K,0,0,K,0,0,K,K,0,K,
					0,0,0,K,0,0,K,0,0,K,0,0,K,0,0,K,
					0,K,K,0,K,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,K,0,0,0,0,0,0,0,0,0,0,K,
					K,0,0,0,K,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,0,K,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,0,K,0,0,0,0,0,0,K,0,0,0,
			}
	);
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private JScrollPane scrp = null;
	private float ox, oy;
	private float osx, osy;
	
	public boolean mousePressed(ToolEvent e) {
		Component c = e.getCanvasView();
		while (true) {
			if (c == null) {
				scrp = null;
				break;
			}
			if (c instanceof JScrollPane) {
				scrp = (JScrollPane)c;
				ox = e.getCanvasX();
				oy = e.getCanvasY();
				osx = scrp.getHorizontalScrollBar().getValue();
				osy = scrp.getVerticalScrollBar().getValue();
				break;
			}
			else {
				c = c.getParent();
			}
		}
		return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (scrp != null) {
			float xd = e.getCanvasX()-ox;
			float yd = e.getCanvasY()-oy;
			scrp.getHorizontalScrollBar().setValue((int)(osx-xd));
			scrp.getVerticalScrollBar().setValue((int)(osy-yd));
			osx = scrp.getHorizontalScrollBar().getValue();
			osy = scrp.getVerticalScrollBar().getValue();
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (scrp != null) {
			float xd = e.getCanvasX()-ox;
			float yd = e.getCanvasY()-oy;
			scrp.getHorizontalScrollBar().setValue((int)(osx-xd));
			scrp.getVerticalScrollBar().setValue((int)(osy-yd));
			osx = scrp.getHorizontalScrollBar().getValue();
			osy = scrp.getVerticalScrollBar().getValue();
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return (e.isMouseDown() ? CursorUtils.CURSOR_HAND_CLOSED : CursorUtils.CURSOR_HAND_OPEN);
	}
}
