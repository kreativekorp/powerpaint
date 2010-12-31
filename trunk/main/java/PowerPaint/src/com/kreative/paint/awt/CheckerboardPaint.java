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

package com.kreative.paint.awt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class CheckerboardPaint implements Paint {
	public static final CheckerboardPaint LIGHT = new CheckerboardPaint(0xFFFFFFFF, 0xFFCCCCCC);
	public static final CheckerboardPaint DARK = new CheckerboardPaint(0xFF333333, 0xFF000000);
	
	private Paint internalPaint;
	
	public CheckerboardPaint(int light, int dark) {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(new Color(light));
		g.fillRect(0, 0, 8, 8);
		g.fillRect(8, 8, 8, 8);
		g.setColor(new Color(dark));
		g.fillRect(8, 0, 8, 8);
		g.fillRect(0, 8, 8, 8);
		g.dispose();
		Rectangle2D rect = new Rectangle2D.Float(0, 0, 16, 16);
		internalPaint = new TexturePaint(img, rect);
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		return internalPaint.createContext(cm, deviceBounds, userBounds, xform, hints);
	}

	public int getTransparency() {
		return internalPaint.getTransparency();
	}
}
