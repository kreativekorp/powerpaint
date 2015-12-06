package com.kreative.paint.material.dither;

import java.awt.image.BufferedImage;
import java.util.Random;

public class RandomDitherAlgorithm extends DitherAlgorithm {
	public static final RandomDitherAlgorithm RANDOM = new RandomDitherAlgorithm("Random");
	
	private final Random random;
	
	public RandomDitherAlgorithm(String name) {
		super(name);
		this.random = new Random();
	}
	
	@Override
	public void dither(BufferedImage image, int[] colors) {
		int[] ca = new int[colors.length];
		int[] cr = new int[colors.length];
		int[] cg = new int[colors.length];
		int[] cb = new int[colors.length];
		for (int i = 0; i < colors.length; i++) {
			ca[i] = (colors[i] >> 24) & 0xFF;
			cr[i] = (colors[i] >> 16) & 0xFF;
			cg[i] = (colors[i] >>  8) & 0xFF;
			cb[i] = (colors[i] >>  0) & 0xFF;
		}
		int h = image.getHeight();
		int w = image.getWidth();
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int o = image.getRGB(x, y);
				int oa = (o >> 24) & 0xFF;
				int or = (o >> 16) & 0xFF;
				int og = (o >>  8) & 0xFF;
				int ob = (o >>  0) & 0xFF;
				// Closest color.
				int m1 = 0;
				int m1i = 0;
				int m1d = 262144;
				// Second-closest color.
				int m2 = 0;
				int m2i = 0;
				int m2d = 262144;
				for (int i = 0; i < colors.length; i++) {
					int cda = oa - ca[i];
					int cdr = or - cr[i];
					int cdg = og - cg[i];
					int cdb = ob - cb[i];
					int cd = cda*cda + cdr*cdr + cdg*cdg + cdb*cdb;
					if (cd < m1d) {
						// This color is closer than either m1 or m2.
						// Replace m2 with m1, and m1 with this color.
						m2 = m1;
						m2i = m1i;
						m2d = m1d;
						m1 = colors[i];
						m1i = i;
						m1d = cd;
					} else if (cd < m2d) {
						// This color is closer than m2 but not m1.
						// Replace m2 with this color.
						m2 = colors[i];
						m2i = i;
						m2d = cd;
					}
				}
				// Sort by index.
				if (m1i > m2i) {
					int m3 = m1;
					int m3i = m1i;
					int m3d = m1d;
					m1 = m2;
					m1i = m2i;
					m1d = m2d;
					m2 = m3;
					m2i = m3i;
					m2d = m3d;
				}
				image.setRGB(x, y, (m1d < random.nextInt(m1d + m2d)) ? m1 : m2);
			}
		}
	}
}
