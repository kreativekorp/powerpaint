package com.kreative.paint.filter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Random;
import com.kreative.paint.util.ImageUtils;

public class DiffuseFilter extends AbstractFilter {
	private static final Random random = new Random();
	
	public Image filter(Image src) {
		BufferedImage bi = ImageUtils.toBufferedImage(src, true);
		int w = bi.getWidth();
		int h = bi.getHeight();
		int[] pixels = new int[w*h];
		int[] tmp = new int[4];
		bi.getRGB(0, 0, w, h, pixels, 0, w);
		for (int y = 0, ay = 0; y < h-1; y++, ay += w) {
			for (int x = 0, ax = ay; x < w-1; x++, ax++) {
				tmp[0] = pixels[ax];
				tmp[1] = pixels[ax+1];
				tmp[2] = pixels[ax+w];
				tmp[3] = pixels[ax+w+1];
				for (int i = 0, n = 4; i < 4; i++, n--) {
					int j = i+random.nextInt(n);
					int t = tmp[i];
					tmp[i] = tmp[j];
					tmp[j] = t;
				}
				pixels[ax] = tmp[0];
				pixels[ax+1] = tmp[1];
				pixels[ax+w] = tmp[2];
				pixels[ax+w+1] = tmp[3];
			}
		}
		bi.setRGB(0, 0, w, h, pixels, 0, w);
		return bi;
	}
}
