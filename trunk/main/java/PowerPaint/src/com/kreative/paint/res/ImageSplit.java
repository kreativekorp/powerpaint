/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.res;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageSplit {
	public static void main(String[] args) throws IOException {
		Toolkit tk = Toolkit.getDefaultToolkit();
		for (String arg : args) {
			System.out.print(arg + "... ");
			Image img = tk.createImage(arg);
			long time = System.currentTimeMillis();
			while (!tk.prepareImage(img, -1, -1, null)) {
				if ((System.currentTimeMillis() - time) > 1000L) break;
			}
			int w = img.getWidth(null);
			int h = img.getHeight(null);
			if (w <= 0 || h <= 0) {
				System.out.println("cannot read");
			} else if (w <= h) {
				System.out.println("already in one piece");
			} else {
				File fork = new File(arg);
				fork = new File(fork.getParentFile(), fork.getName()+".d");
				fork.mkdir();
				int n = ((w+h-1)/h);
				for (int i = 0, x = 0; i < n; i++, x += h) {
					System.out.print("\r" + arg + "... " + i + "/" + n + "...");
					BufferedImage bi = new BufferedImage(h, h, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = bi.createGraphics();
					while (!g.drawImage(img, 0, 0, h, h, x, 0, x+h, h, null));
					g.dispose();
					String fn = "0000" + i;
					fn = fn.substring(fn.length()-4);
					ImageIO.write(bi, "png", new File(fork, fn + ".png"));
				}
				System.out.println("done");
			}
		}
	}
}
