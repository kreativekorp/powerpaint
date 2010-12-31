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
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import com.kreative.paint.powerbrush.BrushSettings;
import com.kreative.paint.util.CursorUtils;

public class PowerEraserTool extends AbstractPaintTool implements ToolOptions.PowerBrush {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,K,K,
					0,0,0,K,K,0,0,0,0,0,0,0,0,K,K,0,
					0,0,K,K,K,0,0,0,0,0,0,0,K,K,K,0,
					0,K,K,K,0,0,0,0,0,0,0,K,K,K,0,0,
					K,K,K,K,K,K,0,0,K,K,K,K,K,K,K,K,
					0,0,K,K,K,0,0,K,0,0,0,0,K,K,K,K,
					0,K,K,K,0,0,K,0,0,0,0,K,K,K,0,K,
					0,K,K,0,0,K,0,0,0,0,0,K,K,0,K,K,
					K,K,0,0,K,0,0,0,0,0,K,K,0,K,K,0,
					K,0,0,K,0,0,0,0,0,0,K,0,K,K,0,0,
					0,0,K,0,0,0,0,0,0,K,0,K,K,0,0,0,
					0,K,0,0,0,0,0,0,K,0,K,K,0,0,0,0,
					K,K,K,K,K,K,K,K,0,K,K,0,0,0,0,0,
					K,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					K,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor none = CursorUtils.makeCursor(1, 1, new int[] {0}, 0, 0, "None");
	private static final Cursor cross = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private BrushSettings cache = null;
	private Shape bscache = null;
	private Cursor ccache = null;
	
	private void makeCache(ToolEvent e) {
		cache = e.tc().getPowerBrushSettings();
		bscache = cache.getBrushShape().makeBrush(0, 0, cache.getOuterWidth(), cache.getOuterHeight());
		ccache = (cache.getOuterWidth() > 32 || cache.getOuterHeight() > 32) ? cross : none;
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
		if (cache == null) makeCache(e);
		e.beginTransaction(getName());
		Graphics2D g = e.getPaintGraphics();
		g.setPaint(new Color(e.getLayer().getMatte()));
		g.setComposite(AlphaComposite.Clear);
		cache.paint(g, e.getX(), e.getY());
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (cache == null) makeCache(e);
		Graphics2D g = e.getPaintGraphics();
		g.setPaint(new Color(e.getLayer().getMatte()));
		g.setComposite(AlphaComposite.Clear);
		cache.paint(g, false, e.getPreviousX(), e.getPreviousY(), e.getX(), e.getY());
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (cache == null) makeCache(e);
		Graphics2D g = e.getPaintGraphics();
		g.setPaint(new Color(e.getLayer().getMatte()));
		g.setComposite(AlphaComposite.Clear);
		cache.paint(g, false, e.getPreviousX(), e.getPreviousY(), e.getX(), e.getY());
		e.commitTransaction();
		return true;
	}
	
	private static final Color CC = new Color(0x80808080, true);
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (bscache == null) makeCache(e);
		if (e.isMouseOnCanvas()) {
			g.setComposite(AlphaComposite.SrcOver);
			g.setPaint(CC);
			g.draw(AffineTransform.getTranslateInstance(e.getX(), e.getY()).createTransformedShape(bscache));
		}
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_OPEN_BRACKET:
			e.tc().decrementPowerBrush();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_CLOSE_BRACKET:
			e.tc().incrementPowerBrush();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		if (ccache == null) makeCache(e);
		return ccache;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
