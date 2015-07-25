package com.kreative.paint.dither;

import java.awt.image.BufferedImage;

public class DiffusionDitherAlgorithm extends MatrixDitherAlgorithm {
	public static final DiffusionDitherAlgorithm THRESHOLD = new DiffusionDitherAlgorithm(1, 1, 1, "Threshold");
	public static final DiffusionDitherAlgorithm FLOYD_STEINBERG = new DiffusionDitherAlgorithm(2, 3, 16, "Floyd-Steinberg");
	static {
		THRESHOLD.values[0][0] = 0;
		FLOYD_STEINBERG.values[0][0] = 0;
		FLOYD_STEINBERG.values[0][1] = 0;
		FLOYD_STEINBERG.values[0][2] = 7;
		FLOYD_STEINBERG.values[1][0] = 3;
		FLOYD_STEINBERG.values[1][1] = 5;
		FLOYD_STEINBERG.values[1][2] = 1;
	}
	
	public DiffusionDitherAlgorithm(int rows, int columns, int denominator, String name) {
		super(rows, columns, denominator, name);
	}
	
	public DiffusionDitherAlgorithm(int rows, int columns, int denominator, String text, String name) {
		super(rows, columns, denominator, text, name);
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
				int m = 0;
				int mda = 256;
				int mdr = 256;
				int mdg = 256;
				int mdb = 256;
				int md = 262144;
				for (int i = 0; i < colors.length; i++) {
					int cda = oa - ca[i];
					int cdr = or - cr[i];
					int cdg = og - cg[i];
					int cdb = ob - cb[i];
					int cd = cda*cda + cdr*cdr + cdg*cdg + cdb*cdb;
					if (cd < md) {
						m = colors[i];
						mda = cda;
						mdr = cdr;
						mdg = cdg;
						mdb = cdb;
						md = cd;
					}
				}
				image.setRGB(x, y, m);
				for (int k = 0; k < rows; k++) {
					if (y + k < h) {
						for (int i = 0, j = -columns/2; i < columns; i++, j++) {
							if (x + j >= 0 && x + j < w && values[k][i] != 0) {
								int p = image.getRGB(x + j, y + k);
								int pa = (p >> 24) & 0xFF;
								int pr = (p >> 16) & 0xFF;
								int pg = (p >>  8) & 0xFF;
								int pb = (p >>  0) & 0xFF;
								int aa = r255(pa + mda * values[k][i] / denominator);
								int ar = r255(pr + mdr * values[k][i] / denominator);
								int ag = r255(pg + mdg * values[k][i] / denominator);
								int ab = r255(pb + mdb * values[k][i] / denominator);
								int a = (aa << 24) | (ar << 16) | (ag << 8) | ab;
								image.setRGB(x + j, y + k, a);
							}
						}
					}
				}
			}
		}
	}
	
	private static int r255(int v) {
		if (v < 0) return 0;
		if (v > 255) return 255;
		return v;
	}
}
