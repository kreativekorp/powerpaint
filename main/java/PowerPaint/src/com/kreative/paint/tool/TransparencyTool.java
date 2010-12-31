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
import java.awt.Rectangle;
import com.kreative.paint.Canvas;
import com.kreative.paint.Layer;
import com.kreative.paint.util.CursorUtils;

public class TransparencyTool extends AbstractPaintTool {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,K,0,0,0,K,K,K,K,K,K,K,K,K,
					0,K,0,0,0,K,0,0,0,0,0,0,0,0,0,K,
					0,0,K,0,K,0,0,0,0,0,0,0,0,0,0,K,
					K,0,0,K,0,0,K,0,0,K,K,K,K,K,K,K,
					0,0,K,0,K,0,0,0,0,K,0,0,0,0,0,0,
					0,K,0,0,0,K,0,0,0,K,0,0,0,0,0,0,
					0,0,0,K,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,K,0,0,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,K,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,K,0,0,K,0,0,K,K,K,0,
					0,0,0,0,0,0,K,0,0,K,0,0,0,K,K,K,
					0,0,0,0,0,0,K,K,K,K,0,0,0,0,K,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,0,W,0,K,0,0,0,0,0,0,0,0,0,0,
					0,0,K,W,K,0,0,0,0,0,0,0,0,0,0,0,
					K,W,W,K,W,W,K,0,0,0,0,0,0,0,0,0,
					0,0,K,W,K,0,0,0,0,0,0,0,0,0,0,0,
					0,K,0,W,0,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,K,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,K,W,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,W,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
			},
			3, 3,
			"Wand"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		Canvas cv = e.getCanvas();
		Layer layer = e.getLayer();
		if (layer == null) return false;
		e.beginTransaction(getName());
		Rectangle bounds = new Rectangle(-layer.getX(), -layer.getY(), cv.getWidth(), cv.getHeight());
		int x = (int)Math.floor(e.getCanvasX()-layer.getX());
		int y = (int)Math.floor(e.getCanvasY()-layer.getY());
		int targetcolor = layer.getRGB(x, y);
		int[] rgb = new int[bounds.width*bounds.height];
		layer.getRGB(bounds.x, bounds.y, bounds.width, bounds.height, rgb, 0, bounds.width);
		for (int ai = 0, ly = bounds.y; ai < rgb.length; ai += bounds.width, ly++) {
			for (int aj = 0, lx = bounds.x; aj < bounds.width; aj++, lx++) {
				if (rgb[ai+aj] == targetcolor) {
					rgb[ai+aj] = targetcolor & 0xFFFFFF;
				}
			}
		}
		layer.setRGB(bounds.x, bounds.y, bounds.width, bounds.height, rgb, 0, bounds.width);
		e.commitTransaction();
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
