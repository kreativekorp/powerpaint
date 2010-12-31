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
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import com.kreative.paint.draw.PencilStrokeDrawObject;
import com.kreative.paint.util.CursorUtils;

public class CurlPencilTool extends AbstractPaintDrawTool implements ToolOptions.Curl {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,0,K,K,K,K,0,
					0,K,0,0,0,0,0,0,0,0,0,K,0,0,0,K,
					0,K,0,K,K,0,0,0,0,0,K,0,0,0,0,K,
					0,0,K,0,0,K,0,0,0,0,K,K,0,0,K,0,
					0,K,0,K,K,0,0,0,0,K,0,0,K,K,K,0,
					0,K,0,0,0,0,0,0,0,K,0,0,0,K,0,0,
					0,K,0,K,K,0,0,0,K,0,0,0,0,K,0,0,
					0,0,K,0,0,K,0,0,K,0,0,0,K,0,0,0,
					0,K,0,K,K,0,0,K,0,0,0,0,K,0,0,0,
					0,K,0,0,0,0,0,K,0,0,0,K,0,0,0,0,
					0,K,0,K,K,0,0,K,K,0,0,K,0,0,0,0,
					0,0,K,0,0,K,0,K,K,K,K,0,0,0,0,0,
					0,K,0,K,K,0,0,K,K,K,0,0,0,0,0,0,
					0,K,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,K,K,K,K,K,K,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,K,W,W,W,K,0,0,0,
					0,0,0,0,0,0,0,K,W,W,W,W,K,0,0,0,
					0,0,0,0,0,0,0,K,K,W,W,K,0,0,0,0,
					0,0,0,0,0,0,K,W,W,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,W,W,W,K,0,0,0,0,0,
					0,0,0,0,0,K,W,W,W,W,K,0,0,0,0,0,
					0,0,0,0,0,K,W,W,W,K,0,0,0,0,0,0,
					0,0,0,0,K,W,W,W,W,K,0,0,0,0,0,0,
					0,0,0,0,K,W,W,W,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,W,W,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
			},
			4, 15,
			"Pencil"
	);
	private static final double TP = 2.0 * Math.PI;
	private static final double FP = 4.0 * Math.PI;
	
	public double angle = 0.0;
	private GeneralPath currentPath;
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean toolSelected(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		// Opening
		e.beginTransaction(getName());
		currentPath = new GeneralPath();
		// First Plot
		double radius = e.tc().getRawCurlRadius();
		double x = e.getX() + radius * Math.cos(angle);
		double y = e.getY() + radius * Math.sin(angle);
		currentPath.moveTo((float)x, (float)y);
		// Return
		return false;
	}

	public boolean mouseDragged(ToolEvent e) {
		double radius = e.tc().getRawCurlRadius();
		double spacing = e.tc().getRawCurlSpacing();
		double sx = e.getPreviousX();
		double sy = e.getPreviousY();
		double dx = e.getX();
		double dy = e.getY();
		double sa = angle;
		double da = angle + TP * Math.hypot(dx-sx, dy-sy) / spacing;
		int m = (int)Math.ceil(Math.abs(da-sa)*radius);
		for (int s = 0; s < m; s++) {
			double sx2 = sx + ((dx-sx)*s)/m;
			double sy2 = sy + ((dy-sy)*s)/m;
			double dx2 = sx + ((dx-sx)*(s+1))/m;
			double dy2 = sy + ((dy-sy)*(s+1))/m;
			double sa2 = sa + ((da-sa)*s)/m;
			double da2 = sa + ((da-sa)*(s+1))/m;
			sx2 += radius * Math.cos(sa2);
			sy2 += radius * Math.sin(sa2);
			dx2 += radius * Math.cos(da2);
			dy2 += radius * Math.sin(da2);
			currentPath.lineTo((float)dx2, (float)dy2);
		}
		angle = da;
		if (angle > FP) angle -= FP;
		if (angle < -FP) angle += FP;
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		// Last Plot
		double radius = e.tc().getRawCurlRadius();
		double x = e.getX() + radius * Math.cos(angle);
		double y = e.getY() + radius * Math.sin(angle);
		currentPath.lineTo((float)x, (float)y);
		// Closing
		PencilStrokeDrawObject o = new PencilStrokeDrawObject(currentPath, e.getPaintSettings());
		if (e.isInDrawMode()) e.getDrawSurface().add(o);
		else o.paint(e.getPaintGraphics());
		currentPath = null;
		e.commitTransaction();
		// Return
		return true;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (currentPath != null) {
			PencilStrokeDrawObject o = new PencilStrokeDrawObject(currentPath, e.getPaintSettings());
			o.paint(g);
		}
		if (e.isMouseOnCanvas()) {
			double radius = e.tc().getRawCurlRadius();
			g.setComposite(AlphaComposite.SrcOver);
			g.setStroke(new BasicStroke(1));
			g.setColor(new Color(0x80000000,true));
			g.draw(new Ellipse2D.Double(e.getX()-radius, e.getY()-radius, radius*2.0, radius*2.0));
			g.setColor(new Color(0xC0000000,true));
			GeneralPath p = new GeneralPath();
			float dx = (float)(e.getX() + radius*Math.cos(angle));
			float dy = (float)(e.getY() + radius*Math.sin(angle));
			p.moveTo(dx-2.0f, dy);
			p.lineTo(dx+2.0f, dy);
			p.moveTo(dx, dy-2.0f);
			p.lineTo(dx, dy+2.0f);
			g.draw(p);
		}
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_BACK_QUOTE:
			e.tc().toggleCurlCCW();
			break;
		case KeyEvent.VK_EQUALS:
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_ADD:
			if (e.isShiftDown()) {
				e.tc().incrementCurlSpacing();
			} else {
				e.tc().incrementCurlRadius();
			}
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			if (e.isShiftDown()) {
				e.tc().decrementCurlSpacing();
			} else {
				e.tc().decrementCurlRadius();
			}
			break;
		case KeyEvent.VK_1:
			if (e.isShiftDown()) e.tc().setCurlSpacing(5);
			else e.tc().setCurlRadius(5);
			break;
		case KeyEvent.VK_2:
			if (e.isShiftDown()) e.tc().setCurlSpacing(10);
			else e.tc().setCurlRadius(10);
			break;
		case KeyEvent.VK_3:
			if (e.isShiftDown()) e.tc().setCurlSpacing(15);
			else e.tc().setCurlRadius(15);
			break;
		case KeyEvent.VK_4:
			if (e.isShiftDown()) e.tc().setCurlSpacing(20);
			else e.tc().setCurlRadius(20);
			break;
		case KeyEvent.VK_5:
			if (e.isShiftDown()) e.tc().setCurlSpacing(25);
			else e.tc().setCurlRadius(25);
			break;
		case KeyEvent.VK_6:
			if (e.isShiftDown()) e.tc().setCurlSpacing(30);
			else e.tc().setCurlRadius(30);
			break;
		case KeyEvent.VK_7:
			if (e.isShiftDown()) e.tc().setCurlSpacing(35);
			else e.tc().setCurlRadius(35);
			break;
		case KeyEvent.VK_8:
			if (e.isShiftDown()) e.tc().setCurlSpacing(40);
			else e.tc().setCurlRadius(40);
			break;
		case KeyEvent.VK_9:
			if (e.isShiftDown()) e.tc().setCurlSpacing(45);
			else e.tc().setCurlRadius(45);
			break;
		case KeyEvent.VK_0:
			if (e.isShiftDown()) e.tc().setCurlSpacing(50);
			else e.tc().setCurlRadius(50);
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
