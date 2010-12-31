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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import com.kreative.paint.util.CursorUtils;

public class HotspotTool extends AbstractViewTool {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,K,K,K,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,K,K,K,0,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,0,K,K,K,0,K,K,K,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,K,K,K,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,W,W,0,0,0,0,0,W,W,0,0,0,
					0,0,0,W,K,K,W,0,0,0,W,K,K,W,0,0,
					0,0,0,W,K,K,K,W,0,W,K,K,K,W,0,0,
					0,0,0,0,W,K,K,K,W,K,K,K,W,0,0,0,
					0,0,0,0,0,W,K,W,W,W,K,W,0,0,0,0,
					0,0,0,0,0,0,W,W,0,W,W,0,0,0,0,0,
					0,0,0,0,0,W,K,W,W,W,K,W,0,0,0,0,
					0,0,0,0,W,K,K,K,W,K,K,K,W,0,0,0,
					0,0,0,W,K,K,K,W,0,W,K,K,K,W,0,0,
					0,0,0,W,K,K,W,0,0,0,W,K,K,W,0,0,
					0,0,0,0,W,W,0,0,0,0,0,W,W,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			8, 8,
			"Hotspot"
	);
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		e.getCanvas().setHotspot((int)Math.floor(e.getCanvasX()), (int)Math.floor(e.getCanvasY()));
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		e.getCanvas().setHotspot((int)Math.floor(e.getCanvasX()), (int)Math.floor(e.getCanvasY()));
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.getCanvas().setHotspot((int)Math.floor(e.getCanvasX()), (int)Math.floor(e.getCanvasY()));
		e.commitTransaction();
		return true;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		Point hs = e.getCanvas().getHotspot();
		g.setStroke(new BasicStroke(0.25f));
		g.setPaint(new Color(0x66FF0000, true));
		g.setComposite(AlphaComposite.SrcOver);
		GeneralPath p = new GeneralPath();
		p.moveTo(hs.x+0.2f, hs.y+0.2f);
		p.lineTo(hs.x+0.8f, hs.y+0.8f);
		p.moveTo(hs.x+0.8f, hs.y+0.2f);
		p.lineTo(hs.x+0.2f, hs.y+0.8f);
		g.draw(p);
		return true;
	}
	
	public boolean paintIntermediateUsesCanvasCoordinates() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
