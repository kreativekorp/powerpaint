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

package com.kreative.paint.util;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ImageUtils {
	private ImageUtils() {}
	
	public static boolean prepImage(Image img) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		long t = System.currentTimeMillis();
		while (true) {
			if (tk.prepareImage(img, -1, -1, null)) return true;
			if ((System.currentTimeMillis()-t) > 1000L) return false;
		}
	}
	
	public static boolean prepImage(Toolkit tk, Image img) {
		long t = System.currentTimeMillis();
		while (true) {
			if (tk.prepareImage(img, -1, -1, null)) return true;
			if ((System.currentTimeMillis()-t) > 1000L) return false;
		}
	}
	
	public static BufferedImage toBufferedImage(Image img, boolean clone) {
		if (img == null) return null;
		if (img instanceof BufferedImage && !clone) return (BufferedImage)img;
		if (!prepImage(img)) return null;
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		while (!g.drawImage(img, 0, 0, null));
		g.dispose();
		return bi;
	}
	
	public static BufferedImage toBufferedImage(Image img, int maxw, int maxh) {
		if (img == null) return null;
		if (!prepImage(img)) return null;
		int iw = img.getWidth(null);
		int ih = img.getHeight(null);
		int w = Math.min(iw, maxw);
		int h = Math.min(ih, maxh);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		while (!g.drawImage(img, (w-iw)/2, (h-ih)/2, null));
		g.dispose();
		return bi;
	}
}
