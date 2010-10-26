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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.Canvas;

public class RulerTool extends AbstractViewTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,0,K,0,0,0,0,0,0,0,0,0,0,0,
					0,K,0,0,0,K,0,0,0,0,0,0,0,0,0,0,
					K,0,0,0,0,0,K,0,0,0,0,0,0,0,0,0,
					0,K,0,K,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,K,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,K,0,K,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,K,0,K,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,K,0,0,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,K,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,K,0,
					0,0,0,0,0,0,0,0,0,K,0,K,0,0,0,K,
					0,0,0,0,0,0,0,0,0,0,K,0,0,0,K,0,
					0,0,0,0,0,0,0,0,0,0,0,K,0,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			float sx = e.getCanvasPreviousClickedX();
			float sy = e.getCanvasPreviousClickedY();
			float x = e.getCanvasX();
			float y = e.getCanvasY();
			Canvas cv = e.getCanvas();
			if (e.isShiftDown()) {
				float w = Math.abs(x-sx);
				float h = Math.abs(y-sy);
				if (w > h*2) y = sy;
				else if (h > w*2) x = sx;
				else {
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
			}
			float r = (float)Math.hypot(x-sx, y-sy);
			g.setComposite(AlphaComposite.SrcOver);
			g.setPaint(new Color(0x80808080, true));
			g.setStroke(new BasicStroke(1.0f/e.getCanvasView().getScale()));
			g.draw(new Line2D.Float(sx, sy, x, y));
			g.draw(new Rectangle2D.Float(Math.min(sx,x), Math.min(sy,y), Math.abs(x-sx), Math.abs(y-sy)));
			g.draw(new Ellipse2D.Float(Math.min(sx,x)-Math.abs(x-sx), Math.min(sy,y)-Math.abs(y-sy), Math.abs(x-sx)*2, Math.abs(y-sy)*2));
			g.draw(new Ellipse2D.Float(sx-r, sy-r, r+r, r+r));
			g.setPaint(Color.black);
			g.setFont(new Font("SansSerif", Font.PLAIN, 8).deriveFont(8.0f/e.getCanvasView().getScale()));
			int a = g.getFontMetrics().getHeight();
			g.drawString("x\u2081 = "+toString(sx, cv.getDPIX(), e), x+2, y-8*a);
			g.drawString("y\u2081 = "+toString(sy, cv.getDPIY(), e), x+2, y-7*a);
			g.drawString("x\u2082 = "+toString(x, cv.getDPIX(), e), x+2, y-6*a);
			g.drawString("y\u2082 = "+toString(y, cv.getDPIY(), e), x+2, y-5*a);
			g.drawString("\u2206x = "+toString(x-sx, cv.getDPIX(), e), x+2, y-4*a);
			g.drawString("\u2206y = "+toString(y-sy, cv.getDPIY(), e), x+2, y-3*a);
			g.drawString("r = "+toStringR(x-sx, y-sy, cv.getDPIX(), cv.getDPIY(), e), x+2, y-2*a);
			g.drawString("\u03B8 = "+toStringTHD(x-sx, y-sy, cv.getDPIX(), cv.getDPIY(), e), x+2, y-a);
			g.drawString("\u03B8 = "+toStringTHR(x-sx, y-sy, cv.getDPIX(), cv.getDPIY(), e), x+2, y);
			g.drawString("p("+ToolUtilities.messages.getString("ruler.rectangle")+") = "+toStringP(x-sx, y-sy, cv.getDPIX(), cv.getDPIY(), e), x+2, y+a);
			g.drawString("c("+ToolUtilities.messages.getString("ruler.circle")+") = "+toStringCC(x-sx, y-sy, cv.getDPIX(), cv.getDPIY(), e), x+2, y+2*a);
			g.drawString("a("+ToolUtilities.messages.getString("ruler.rectangle")+") = "+toStringA(x-sx, y-sy, cv.getDPIX(), cv.getDPIY(), e), x+2, y+3*a);
			g.drawString("a("+ToolUtilities.messages.getString("ruler.ellipse")+") = "+toStringEA(x-sx, y-sy, cv.getDPIX(), cv.getDPIY(), e), x+2, y+4*a);
			g.drawString("a("+ToolUtilities.messages.getString("ruler.circle")+") = "+toStringCA(x-sx, y-sy, cv.getDPIX(), cv.getDPIY(), e), x+2, y+5*a);
			return true;
		}
		else return false;
	}
	
	private static String toString(float f, int dpi, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			return (f * 72.0f / (float)dpi) + " pt";
		}
		else if (e.isCtrlDown()) {
			return (f * 2.54f / (float)dpi) + " cm";
		}
		else if (e.isAltDown()) {
			return (f / (float)dpi) + " in";
		}
		else {
			return f + " px";
		}
	}
	
	private static String toStringP(float dx, float dy, int dpix, int dpiy, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			float r = 2*(dy * 72.0f / (float)dpiy) + 2*(dx * 72.0f / (float)dpix);
			return (float)r + " pt";
		}
		else if (e.isCtrlDown()) {
			float r = 2*(dy * 2.54f / (float)dpiy) + 2*(dx * 2.54f / (float)dpix);
			return (float)r + " cm";
		}
		else if (e.isAltDown()) {
			float r = 2*(dy / (float)dpiy) + 2*(dx / (float)dpix);
			return (float)r + " in";
		}
		else {
			float r = 2*dy + 2*dx;
			return (float)r + " px";
		}
	}
	
	private static String toStringA(float dx, float dy, int dpix, int dpiy, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			float r = (dy * 72.0f / (float)dpiy) * (dx * 72.0f / (float)dpix);
			return (float)r + " pt\u00B2";
		}
		else if (e.isCtrlDown()) {
			float r = (dy * 2.54f / (float)dpiy) * (dx * 2.54f / (float)dpix);
			return (float)r + " cm\u00B2";
		}
		else if (e.isAltDown()) {
			float r = (dy / (float)dpiy) * (dx / (float)dpix);
			return (float)r + " in\u00B2";
		}
		else {
			float r = dy * dx;
			return (float)r + " px\u00B2";
		}
	}
	
	private static String toStringEA(float dx, float dy, int dpix, int dpiy, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			float r = (float)Math.PI * (dy * 72.0f / (float)dpiy) * (dx * 72.0f / (float)dpix);
			return (float)r + " pt\u00B2";
		}
		else if (e.isCtrlDown()) {
			float r = (float)Math.PI * (dy * 2.54f / (float)dpiy) * (dx * 2.54f / (float)dpix);
			return (float)r + " cm\u00B2";
		}
		else if (e.isAltDown()) {
			float r = (float)Math.PI * (dy / (float)dpiy) * (dx / (float)dpix);
			return (float)r + " in\u00B2";
		}
		else {
			float r = (float)Math.PI * dy * dx;
			return (float)r + " px\u00B2";
		}
	}
	
	private static String toStringCC(float dx, float dy, int dpix, int dpiy, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			double r = Math.hypot((dy * 72.0f / (float)dpiy), (dx * 72.0f / (float)dpix));
			r *= 2.0f * (float)Math.PI;
			return (float)r + " pt";
		}
		else if (e.isCtrlDown()) {
			double r = Math.hypot((dy * 2.54f / (float)dpiy), (dx * 2.54f / (float)dpix));
			r *= 2.0f * (float)Math.PI;
			return (float)r + " cm";
		}
		else if (e.isAltDown()) {
			double r = Math.hypot((dy / (float)dpiy), (dx / (float)dpix));
			r *= 2.0f * (float)Math.PI;
			return (float)r + " in";
		}
		else {
			double r = Math.hypot(dy, dx);
			r *= 2.0f * (float)Math.PI;
			return (float)r + " px";
		}
	}
	
	private static String toStringCA(float dx, float dy, int dpix, int dpiy, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			double r = Math.hypot((dy * 72.0f / (float)dpiy), (dx * 72.0f / (float)dpix));
			r *= r * (float)Math.PI;
			return (float)r + " pt\u00B2";
		}
		else if (e.isCtrlDown()) {
			double r = Math.hypot((dy * 2.54f / (float)dpiy), (dx * 2.54f / (float)dpix));
			r *= r * (float)Math.PI;
			return (float)r + " cm\u00B2";
		}
		else if (e.isAltDown()) {
			double r = Math.hypot((dy / (float)dpiy), (dx / (float)dpix));
			r *= r * (float)Math.PI;
			return (float)r + " in\u00B2";
		}
		else {
			double r = Math.hypot(dy, dx);
			r *= r * (float)Math.PI;
			return (float)r + " px\u00B2";
		}
	}
	
	private static String toStringR(float dx, float dy, int dpix, int dpiy, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			double r = Math.hypot((dy * 72.0f / (float)dpiy), (dx * 72.0f / (float)dpix));
			return (float)r + " pt";
		}
		else if (e.isCtrlDown()) {
			double r = Math.hypot((dy * 2.54f / (float)dpiy), (dx * 2.54f / (float)dpix));
			return (float)r + " cm";
		}
		else if (e.isAltDown()) {
			double r = Math.hypot((dy / (float)dpiy), (dx / (float)dpix));
			return (float)r + " in";
		}
		else {
			double r = Math.hypot(dy, dx);
			return (float)r + " px";
		}
	}
	
	private static String toStringTHD(float dx, float dy, int dpix, int dpiy, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			double a = Math.atan2((dy / (float)dpiy), (dx / (float)dpix));
			return (float)Math.toDegrees(a) + "\u00B0";
		}
		else if (e.isCtrlDown()) {
			double a = Math.atan2((dy / (float)dpiy), (dx / (float)dpix));
			return (float)Math.toDegrees(a) + "\u00B0";
		}
		else if (e.isAltDown()) {
			double a = Math.atan2((dy / (float)dpiy), (dx / (float)dpix));
			return (float)Math.toDegrees(a) + "\u00B0";
		}
		else {
			double a = Math.atan2((dy / (float)dpiy), (dx / (float)dpix));
			return (float)Math.toDegrees(a) + "\u00B0";
		}
	}
	
	private static String toStringTHR(float dx, float dy, int dpix, int dpiy, ToolEvent e) {
		if (e.isAltDown() && e.isCtrlDown()) {
			double a = Math.atan2((dy / (float)dpiy), (dx / (float)dpix));
			return (float)a + " rad";
		}
		else if (e.isCtrlDown()) {
			double a = Math.atan2((dy / (float)dpiy), (dx / (float)dpix));
			return (float)a + " rad";
		}
		else if (e.isAltDown()) {
			double a = Math.atan2((dy / (float)dpiy), (dx / (float)dpix));
			return (float)a + " rad";
		}
		else {
			double a = Math.atan2((dy / (float)dpiy), (dx / (float)dpix));
			return (float)a + " rad";
		}
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}

	public boolean paintIntermediateUsesCanvasCoordinates() {
		return true;
	}
}
