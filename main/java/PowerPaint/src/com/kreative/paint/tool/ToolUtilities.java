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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public class ToolUtilities {
	public static final ResourceBundle messages = ResourceBundle.getBundle("com.kreative.paint.messages.ToolMessages");
	
	private ToolUtilities() {}
	
	static Image makeIcon(int width, int height, int[] rgb) {
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		i.setRGB(0, 0, width, height, rgb, 0, width);
		return i;
	}
	
	private static final Color FC = new Color(0x66CCCCFF, true);
	private static final Color SC = new Color(0xCCCCCCFF, true);
	private static final Color LC = new Color(0xCC9999CC, true);
	
	static void fillSelectionShape(ToolEvent e, Graphics2D g, Shape dr) {
		Stroke st = new BasicStroke(1.0f/e.getCanvasView().getScale());
		g.setComposite(AlphaComposite.SrcOver);
		g.setStroke(st);
		g.setColor(FC);
		g.fill(dr);
		g.setColor(SC);
		g.draw(dr);
	}
	
	static void drawSelectionShape(ToolEvent e, Graphics2D g, Shape dr) {
		Stroke st = new BasicStroke(1.0f/e.getCanvasView().getScale());
		g.setComposite(AlphaComposite.SrcOver);
		g.setStroke(st);
		g.setColor(LC);
		g.draw(dr);
	}
}
