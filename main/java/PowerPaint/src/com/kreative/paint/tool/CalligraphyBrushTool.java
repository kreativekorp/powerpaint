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

public class CalligraphyBrushTool extends AbstractPaintTool implements ToolOptions.CalligraphyBrushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,K,K,K,K,K,0,0,
					0,0,0,0,0,0,K,K,K,K,K,K,K,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,0,0,0,0,
					0,K,K,K,K,K,K,K,K,K,K,0,0,0,0,0,
					K,K,K,K,K,K,K,K,K,K,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,0,
					K,K,K,K,K,K,K,0,0,0,0,0,0,0,0,0,
					0,K,K,K,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
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
	
	private int bi = 0;
	private Bitmap[] brushCache;
	private Cursor[] brushCursor;
	private boolean ccache;
	private void makeCache(ToolEvent e) {
		brushCache = e.tc().getCalligraphyBrush().toArray(new Bitmap[0]);
		brushCursor = new Cursor[brushCache.length];
		for (int i = 0; i < brushCache.length; i++) brushCursor[i] = brushCache[i].getCursor();
		ccache = e.tc().calligraphyContinuous();
	}
	
	public boolean toolSelected(ToolEvent e) {
		makeCache(e);
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		makeCache(e);
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		if (brushCache == null) makeCache(e);
		e.beginTransaction(getName());
		PaintSettings ps = e.getPaintSettings();
		Graphics2D g = e.getPaintGraphics();
		float x = e.getX();
		float y = e.getY();
		ps.applyFill(g);
		Bitmap brush = brushCache[bi = 0];
		x -= brush.getWidth()/2;
		y -= brush.getHeight()/2;
		brush.paint(e.getPaintSurface(), g, (int)x, (int)y);
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (brushCache == null) makeCache(e);
		PaintSettings ps = e.getPaintSettings();
		Graphics2D g = e.getPaintGraphics();
		float x = e.getX();
		float y = e.getY();
		float px = e.getPreviousX();
		float py = e.getPreviousY();
		int i = (int)Math.hypot(px-x, py-y);
		if (i >= brushCache.length) i = brushCache.length-1;
		ps.applyFill(g);
		if (ccache) {
			drag(e.getPaintSurface(), g, px, py, x, y, bi, i);
		} else {
			Bitmap brush = brushCache[bi = i];
			x -= brush.getWidth()/2;
			y -= brush.getHeight()/2;
			brush.paint(e.getPaintSurface(), g, (int)x, (int)y);
		}
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (brushCache == null) makeCache(e);
		PaintSettings ps = e.getPaintSettings();
		Graphics2D g = e.getPaintGraphics();
		float x = e.getX();
		float y = e.getY();
		float px = e.getPreviousX();
		float py = e.getPreviousY();
		int i = (int)Math.hypot(px-x, py-y);
		if (i >= brushCache.length) i = brushCache.length-1;
		ps.applyFill(g);
		if (ccache) {
			drag(e.getPaintSurface(), g, px, py, x, y, bi, i);
		} else {
			Bitmap brush = brushCache[bi = i];
			x -= brush.getWidth()/2;
			y -= brush.getHeight()/2;
			brush.paint(e.getPaintSurface(), g, (int)x, (int)y);
		}
		bi = 0;
		e.commitTransaction();
		return true;
	}
	
	private void drag(PaintSurface srf, Graphics2D g, float sx, float sy, float dx, float dy, int si, int di) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			int j = si + ((di-si)*i)/m;
			float x = sx + ((dx-sx)*i)/m;
			float y = sy + ((dy-sy)*i)/m;
			Bitmap brush = brushCache[bi = j];
			x -= brush.getWidth()/2;
			y -= brush.getHeight()/2;
			brush.paint(srf, g, (int)x, (int)y);
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_LEFT:
			e.tc().prevCalligraphyBrush();
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_RIGHT:
			e.tc().nextCalligraphyBrush();
			break;
		case KeyEvent.VK_BACK_QUOTE:
			e.tc().toggleCalligraphyContinuous();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		if (brushCursor == null) makeCache(e);
		return brushCursor[bi];
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
