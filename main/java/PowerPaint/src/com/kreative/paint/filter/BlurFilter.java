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

package com.kreative.paint.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import com.kreative.paint.util.ImageUtils;

public class BlurFilter extends AbstractFilter {
	public Image filter(Image img) {
		BufferedImage nim = ImageUtils.toBufferedImage(img, true);
		int w = nim.getWidth();
		int h = nim.getHeight();
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				int c1 = (x>0)?nim.getRGB(x-1,y):nim.getRGB(x,y);
				int c2 = (x<w-1)?nim.getRGB(x+1,y):nim.getRGB(x,y);
				int c3 = (y>0)?nim.getRGB(x,y-1):nim.getRGB(x,y);
				int c4 = (y<h-1)?nim.getRGB(x,y+1):nim.getRGB(x,y);
				int aa = (((c1>>>24)&0xFF)+((c2>>>24)&0xFF)+((c3>>>24)&0xFF)+((c4>>>24)&0xFF))/4;
				int ra = (((c1>>>16)&0xFF)+((c2>>>16)&0xFF)+((c3>>>16)&0xFF)+((c4>>>16)&0xFF))/4;
				int ga = (((c1>>>8)&0xFF)+((c2>>>8)&0xFF)+((c3>>>8)&0xFF)+((c4>>>8)&0xFF))/4;
				int ba = ((c1&0xFF)+(c2&0xFF)+(c3&0xFF)+(c4&0xFF))/4;
				int ca = ((aa&0xFF)<<24) | ((ra&0xFF)<<16) | ((ga&0xFF)<<8) | (ba&0xFF);
				nim.setRGB(x,y,ca);
			}
		}
		return nim;
	}
}
