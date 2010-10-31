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
import com.kreative.paint.PaintSettings;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.util.Bitmap;

public class SprinklerTool extends AbstractPaintTool implements ToolOptions.Sprinkles {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,K,K,0,K,0,0,0,0,0,0,0,0,0,
					0,0,K,K,0,0,0,K,0,0,0,0,0,0,0,0,
					0,K,K,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,K,0,K,0,K,0,K,0,K,0,0,0,0,0,0,
					0,0,K,0,K,0,K,0,K,K,0,0,0,0,0,0,
					0,0,0,K,0,K,0,K,0,K,K,K,0,0,0,0,
					0,0,0,0,K,0,K,0,K,K,0,0,K,0,0,0,
					0,0,0,0,0,K,K,K,K,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,K,0,K,0,0,0,
					0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,0,K,0,0,
					0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,0,K,0,0,
					0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
			}
	);
	
	private float lastX, lastY;
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		PaintSettings ps = e.getPaintSettings();
		Graphics2D g = e.getPaintGraphics();
		Bitmap sprinkle = e.tc().getSprinkle();
		float x = e.getX();
		float y = e.getY();
		lastX = x;
		lastY = y;
		// command but NOT option will inhibit drawing
		if (!e.isCtrlDown() || e.isAltDown()) {
			ps.applyFill(g);
			x -= sprinkle.getWidth()/2;
			y -= sprinkle.getHeight()/2;
			sprinkle.paint(e.getPaintSurface(), g, (int)x, (int)y);
		}
		if (!e.tc().sprinkleBrushMode()) {
			// command will go through the complete set in order
			if (e.isCtrlDown()) {
				e.tc().nextSprinkle();
			}
			// option will repeat with the same shape
			else if (!e.isAltDown()) {
				e.tc().randomSprinkle();
			}
		}
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		PaintSettings ps = e.getPaintSettings();
		Graphics2D g = e.getPaintGraphics();
		Bitmap sprinkle = e.tc().getSprinkle();
		float x = e.getX();
		float y = e.getY();
		if (e.tc().sprinkleBrushMode() || (Math.hypot(x-lastX, y-lastY) > ((3*sprinkle.getWidth())/4))) {
			float px = lastX;
			float py = lastY;
			lastX = x;
			lastY = y;
			// command but NOT option will inhibit drawing
			if (!e.isCtrlDown() || e.isAltDown()) {
				ps.applyFill(g);
				if (e.tc().sprinkleBrushMode()) {
					drag(e.getPaintSurface(), g, sprinkle, px, py, x, y);
				} else {
					x -= sprinkle.getWidth()/2;
					y -= sprinkle.getHeight()/2;
					sprinkle.paint(e.getPaintSurface(), g, (int)x, (int)y);
				}
			}
			if (!e.tc().sprinkleBrushMode()) {
				// command will go through the complete set in order
				if (e.isCtrlDown()) {
					e.tc().nextSprinkle();
				}
				// option will repeat with the same shape
				else if (!e.isAltDown()) {
					e.tc().randomSprinkle();
				}
			}
			return true;
		}
		return false;
	}
	
	private void drag(PaintSurface srf, Graphics2D g, Bitmap sprinkle, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m;
			float y = sy + ((dy-sy)*i)/m;
			x -= sprinkle.getWidth()/2;
			y -= sprinkle.getHeight()/2;
			sprinkle.paint(srf, g, (int)x, (int)y);
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (e.tc().sprinkleBrushMode()) {
			// command will go through the complete set in order
			if (e.isCtrlDown()) {
				e.tc().nextSprinkle();
			}
			// option will repeat with the same shape
			else if (!e.isAltDown()) {
				e.tc().randomSprinkle();
			}
		}
		e.commitTransaction();
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			e.tc().prevSprinkle();
			break;
		case KeyEvent.VK_RIGHT:
			e.tc().nextSprinkle();
			break;
		case KeyEvent.VK_UP:
			e.tc().prevSprinkleSet();
			break;
		case KeyEvent.VK_DOWN:
			e.tc().nextSprinkleSet();
			break;
		case KeyEvent.VK_BACK_QUOTE:
			e.tc().toggleSprinkleBrushMode();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getSprinkleCursor();
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}