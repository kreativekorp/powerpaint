/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageJoin {
	public static void main(String[] args) throws IOException {
		Toolkit tk = Toolkit.getDefaultToolkit();
		int size = 0;
		LinkedHashMap<String,Image> imgs = new LinkedHashMap<String,Image>();
		System.out.println("Calculating image sizes...");
		for (int i = 0; i < args.length-1; i++) {
			String arg = args[i];
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
			} else {
				System.out.println(w + "x" + h);
				if (w > size) size = w;
				if (h > size) size = h;
				imgs.put(arg, img);
			}
		}
		if (size == 0) {
			System.out.println("No readable images specified; done");
		} else {
			System.out.println("Final size: " + size*imgs.size() + "x" + size);
			System.out.println("Building image...");
			BufferedImage bi = new BufferedImage(size * imgs.size(), size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			int x = 0;
			for (Map.Entry<String,Image> e : imgs.entrySet()) {
				System.out.print(e.getKey() + "... ");
				int ix = x+(size-e.getValue().getWidth(null))/2;
				int iy = (size-e.getValue().getHeight(null))/2;
				while (!g.drawImage(e.getValue(), ix, iy, null));
				System.out.println(x);
				x += size;
			}
			g.dispose();
			System.out.print(args[args.length-1] + "... ");
			ImageIO.write(bi, "png", new File(args[args.length-1]));
			System.out.println("done");
		}
	}
}
