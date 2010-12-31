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

package com.kreative.paint.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class DitherAlgorithm {
	private int[][] matrix;
	private int denom;
	
	public DitherAlgorithm(int[][] matrix, int denom) {
		this.matrix = matrix;
		this.denom = denom;
	}
	
	public DitherAlgorithm(Scanner sc) {
		read(sc);
	}
	
	public DitherAlgorithm(InputStream in) {
		Scanner sc = new Scanner(in);
		read(sc);
		sc.close();
	}
	
	public DitherAlgorithm(byte[] data) {
		Scanner sc = new Scanner(new ByteArrayInputStream(data));
		read(sc);
		sc.close();
	}
	
	private void read(Scanner sc) {
		int w = sc.hasNextInt() ? sc.nextInt() : 1;
		int h = sc.hasNextInt() ? sc.nextInt() : 1;
		this.matrix = new int[h][w];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				this.matrix[y][x] = sc.hasNextInt() ? sc.nextInt() : 0;
			}
		}
		this.denom = sc.hasNextInt() ? sc.nextInt() : 1;
	}
	
	public int[][] getMatrix() {
		return matrix;
	}
	
	public int getDenom() {
		return denom;
	}
	
	public BufferedImage dither(Image img, int[] colors) {
		BufferedImage nim = ImageUtils.toBufferedImage(img, true);
		int w = nim.getWidth();
		int h = nim.getHeight();
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				int op = nim.getRGB(x,y);
				int oa = (op >>> 24) & 0xFF;
				int or = (op >>> 16) & 0xFF;
				int og = (op >>>  8) & 0xFF;
				int ob = (op >>>  0) & 0xFF;
				int dp = 0;
				int dae = 256;
				int dre = 256;
				int dge = 256;
				int dbe = 256;
				int dd = 262144;
				for (int cp : colors) {
					int ca = (cp >>> 24) & 0xFF;
					int cr = (cp >>> 16) & 0xFF;
					int cg = (cp >>>  8) & 0xFF;
					int cb = (cp >>>  0) & 0xFF;
					int cae = oa-ca;
					int cre = or-cr;
					int cge = og-cg;
					int cbe = ob-cb;
					int cd = cae*cae + cre*cre + cge*cge + cbe*cbe;
					if (cd < dd) {
						dp = cp;
						dae = cae;
						dre = cre;
						dge = cge;
						dbe = cbe;
						dd = cd;
					}
				}
				nim.setRGB(x,y,dp);
				for (int k = 0; k < matrix.length; k++) {
					if (y+k < h) {
						for (int i = 0, j = -matrix[k].length/2; i < matrix[k].length; i++, j++) {
							if (x+j >= 0 && x+j < w && matrix[k][i] != 0) {
								int pp = nim.getRGB(x+j,y+k);
								int pa = (pp >>> 24) & 0xFF;
								int pr = (pp >>> 16) & 0xFF;
								int pg = (pp >>>  8) & 0xFF;
								int pb = (pp >>>  0) & 0xFF;
								int aa = (pa + dae * matrix[k][i] / denom); if (aa < 0) aa = 0; if (aa > 255) aa = 255;
								int ar = (pr + dre * matrix[k][i] / denom); if (ar < 0) ar = 0; if (ar > 255) ar = 255;
								int ag = (pg + dge * matrix[k][i] / denom); if (ag < 0) ag = 0; if (ag > 255) ag = 255;
								int ab = (pb + dbe * matrix[k][i] / denom); if (ab < 0) ab = 0; if (ab > 255) ab = 255;
								int ap = (aa << 24) | (ar << 16) | (ag << 8) | (ab);
								nim.setRGB(x+j,y+k,ap);
							}
						}
					}
				}
			}
		}
		return nim;
	}
	
	public int hashCode() {
		int hc = denom;
		for (int[] r : matrix) hc ^= Arrays.hashCode(r);
		return hc;
	}
	
	public boolean equals(Object o) {
		if (o instanceof DitherAlgorithm) {
			DitherAlgorithm other = (DitherAlgorithm)o;
			if (this.denom != other.denom) return false;
			if (this.matrix.length != other.matrix.length) return false;
			for (int i = 0; i < matrix.length; i++) {
				if (!Arrays.equals(this.matrix[i], other.matrix[i])) return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		String s = "com.kreative.paint.util.DitherAlgorithm[";
		for (int[] r : matrix) s += Arrays.toString(r) + ", ";
		return s + denom + "]";
	}
}
