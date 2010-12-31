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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import com.kreative.paint.Canvas;
import com.kreative.paint.Layer;
import com.kreative.paint.PaintContext;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.util.CursorUtils;

public class EyedropperTool extends AbstractPaintDrawTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,K,K,
					0,0,0,0,0,0,0,0,K,K,K,K,K,K,K,K,
					0,0,0,0,0,0,0,0,0,K,K,K,K,K,K,0,
					0,0,0,0,0,0,0,0,K,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,K,K,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,0,K,0,0,0,
					0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,K,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,K,0,0,0,K,0,0,0,0,0,0,0,0,0,
					0,K,0,0,0,K,0,0,0,0,0,0,0,0,0,0,
					0,K,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
					K,0,K,K,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		if (e.isInDrawMode()) {
			Canvas c = e.getCanvas();
			PaintContext pc = e.getPaintContext();
			for (int i = c.size()-1; i >= 0; i--) {
				Layer layer = c.get(i);
				if (layer.isViewable()) {
					Point2D.Float lp = new Point2D.Float(e.getCanvasX()-layer.getX(), e.getCanvasY()-layer.getY());
					for (int j = layer.size()-1; j >= 0; j--) {
						DrawObject obj = layer.get(j);
						if (obj.contains(lp)) {
							if (e.isAltDown()) {
								if (e.isShiftDown()) {
									pc.setDrawComposite(obj.getDrawComposite());
									pc.setDrawPaint(obj.getDrawPaint());
									pc.setFillComposite(obj.getFillComposite());
									pc.setFillPaint(obj.getFillPaint());
									pc.setStroke(obj.getStroke());
								} else {
									pc.setDrawComposite(obj.getDrawComposite());
									pc.setDrawPaint(obj.getDrawPaint());
									pc.setStroke(obj.getStroke());
								}
							} else {
								pc.setFillComposite(obj.getFillComposite());
								pc.setFillPaint(obj.getFillPaint());
							}
							return false;
						}
					}
				}
			}
			return false;
		} else {
			BufferedImage tmp = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
			Graphics2D tg = tmp.createGraphics();
			e.getCanvas().paint(tg, -(int)Math.floor(e.getCanvasX()), -(int)Math.floor(e.getCanvasY()));
			tg.dispose();
			PaintContext pc = e.getPaintContext();
			if (e.isAltDown()) {
				if (e.isShiftDown()) {
					pc.setDrawComposite(AlphaComposite.SrcOver);
					pc.setDrawPaint(new Color(tmp.getRGB(0, 0), true));
					pc.setFillComposite(AlphaComposite.SrcOver);
					pc.setFillPaint(new Color(tmp.getRGB(0, 0), true));
				} else {
					pc.setDrawComposite(AlphaComposite.SrcOver);
					pc.setDrawPaint(new Color(tmp.getRGB(0, 0), true));
				}
			} else {
				pc.setFillComposite(AlphaComposite.SrcOver);
				pc.setFillPaint(new Color(tmp.getRGB(0, 0), true));
			}
			return false;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		return mousePressed(e);
	}
	
	public boolean mouseReleased(ToolEvent e) {
		return mousePressed(e);
	}
	
	public Cursor getCursor(ToolEvent e) {
		return CursorUtils.CURSOR_DROPPER;
	}
}
