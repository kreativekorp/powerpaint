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
import com.kreative.paint.util.Bitmap;

public class CharcoalTool extends AbstractPaintTool implements ToolOptions.CharcoalBrushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,
					K,0,0,0,K,K,K,0,0,0,K,0,K,0,0,0,
					0,K,0,0,0,0,0,0,0,K,0,K,0,K,0,K,
					0,0,K,0,K,0,K,0,K,0,K,0,0,0,K,0,
					0,K,0,K,0,K,0,K,0,K,0,0,0,0,0,0,
					0,0,K,0,0,0,K,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private Bitmap[] brushCache = null;
	private Cursor brushCursor = null;
	private void makeCache(ToolEvent e) {
		brushCache = e.tc().getCharcoalBrush().toArray(new Bitmap[0]);
		brushCursor = e.tc().getCharcoalBrushCursor();
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
		Bitmap brush = brushCache[1];
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
		int i = (int)Math.hypot(px-x, py-y)+1;
		if (i >= brushCache.length) i = brushCache.length-1;
		ps.applyFill(g);
		Bitmap brush = brushCache[i];
		x -= brush.getWidth()/2;
		y -= brush.getHeight()/2;
		brush.paint(e.getPaintSurface(), g, (int)x, (int)y);
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
		int i = (int)Math.hypot(px-x, py-y)+1;
		if (i >= brushCache.length) i = brushCache.length-1;
		ps.applyFill(g);
		Bitmap brush = brushCache[i];
		x -= brush.getWidth()/2;
		y -= brush.getHeight()/2;
		brush.paint(e.getPaintSurface(), g, (int)x, (int)y);
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_LEFT:
			e.tc().prevCharcoalBrush();
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_RIGHT:
			e.tc().nextCharcoalBrush();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		if (brushCursor == null) makeCache(e);
		return brushCursor;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
