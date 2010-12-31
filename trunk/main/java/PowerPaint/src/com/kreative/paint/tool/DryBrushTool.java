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
import java.awt.event.KeyEvent;
import java.util.Random;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.util.Bitmap;

public class DryBrushTool extends AbstractPaintTool implements ToolOptions.Brushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,0,0,
					0,K,K,K,K,K,K,K,K,0,K,K,0,K,K,0,
					K,K,K,K,K,K,K,K,0,K,K,K,K,0,0,0,
					K,K,K,K,K,0,K,0,K,K,K,0,K,K,0,0,
					K,K,K,K,K,K,K,0,0,0,K,K,K,K,K,K,
					K,K,K,K,K,K,K,K,K,K,0,K,K,K,K,0,
					K,K,K,K,K,K,0,0,K,K,K,0,K,0,0,0,
					0,K,K,K,K,K,K,K,0,0,K,K,K,K,0,0,
					0,0,K,K,K,K,K,K,K,K,0,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private Random random = new Random();
	private int w, h;
	private int[] rgb;
	
	public boolean mousePressed(ToolEvent e) {
		Bitmap brush = e.tc().getBrush();
		w = brush.getWidth();
		h = brush.getHeight();
		rgb = brush.getRGB();
		e.beginTransaction(getName());
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		float x = e.getX();
		float y = e.getY();
		x -= w/2;
		y -= h/2;
		new Bitmap(w, h, rgb).paint(e.getPaintSurface(), g, (int)x, (int)y);
		return true;
	}
	
	private void drag(PaintSurface srf, Graphics2D g, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m;
			float y = sy + ((dy-sy)*i)/m;
			x -= w/2;
			y -= h/2;
			rgb[random.nextInt(rgb.length)] = 0;
			new Bitmap(w, h, rgb).paint(srf, g, (int)x, (int)y);
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		float lastX = e.getPreviousX();
		float lastY = e.getPreviousY();
		float x = e.getX();
		float y = e.getY();
		drag(e.getPaintSurface(), g, lastX, lastY, x, y);
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		float lastX = e.getPreviousX();
		float lastY = e.getPreviousY();
		float x = e.getX();
		float y = e.getY();
		drag(e.getPaintSurface(), g, lastX, lastY, x, y);
		e.commitTransaction();
		return true;
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
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getBrushCursor();
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
