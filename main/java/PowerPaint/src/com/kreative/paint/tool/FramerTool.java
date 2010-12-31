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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

public class FramerTool extends AbstractPaintTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter, ToolOptions.Frames {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,K,0,0,0,0,0,0,0,0,0,0,0,0,K,K,
					K,0,K,0,0,0,0,0,0,0,0,0,0,K,0,K,
					K,0,0,K,K,K,K,K,K,K,K,K,K,0,0,K,
					K,0,0,K,0,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,0,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,0,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,0,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,0,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,0,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,0,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,0,0,0,0,0,0,0,0,K,0,0,K,
					K,0,0,K,K,K,K,K,K,K,K,K,K,0,0,K,
					K,0,K,0,0,0,0,0,0,0,0,0,0,K,0,K,
					K,K,0,0,0,0,0,0,0,0,0,0,0,0,K,K,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}

	private void paintFrameC(Graphics2D g, ToolEvent e) {
		float sx = e.getPreviousClickedX();
		float sy = e.getPreviousClickedY();
		float x = e.getX();
		float y = e.getY();
		if (e.isShiftDown() != e.tc().drawSquare()) {
			float w = Math.abs(x-sx);
			float h = Math.abs(y-sy);
			float s = Math.max(w, h);
			if (y > sy) y = sy+s;
			else y = sy-s;
			if (x > sx) x = sx+s;
			else x = sx-s;
		}
		if (e.isAltDown() != e.tc().drawFromCenter()) {
			sx -= (x-sx);
			sy -= (y-sy);
		}
		e.getPaintSettings().applyFill(g);
		e.tc().getFrame().paintRestricted(g, (int)sx, (int)sy, (int)x, (int)y, true);
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		paintFrameC(e.getPaintGraphics(), e);
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
			e.tc().prevFrame();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
			e.tc().nextFrame();
			break;
		}
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			paintFrameC(g, e);
			return true;
		} else if (e.isMouseOnCanvas()) {
			int x1 = (int)e.getX();
			int y1 = (int)e.getY();
			int x2 = x1 + 24;
			int y2 = y1 + 24;
			g.setComposite(AlphaComposite.SrcOver);
			g.setPaint(new Color(0,true));
			e.tc().getFrame().paintRestricted(g, x1, y1, x2, y2, true);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}

	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
