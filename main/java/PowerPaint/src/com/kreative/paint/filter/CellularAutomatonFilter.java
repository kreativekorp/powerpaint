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

package com.kreative.paint.filter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.util.ImageUtils;

public abstract class CellularAutomatonFilter extends AbstractFilter {
	public abstract int[] getStateColors();
	public abstract int getNeighborhoodXStart();
	public abstract int getNeighborhoodXEnd();
	public abstract int getNeighborhoodYStart();
	public abstract int getNeighborhoodYEnd();
	public abstract int getNextState(int[][] prevState);
	
	private int iterationCount = 1;
	
	public final boolean usesOptionForm() {
		return true;
	}
	
	public Form getOptionForm(final Image src) {
		Form f = new Form();
		f.add(new PreviewGenerator() {
			public String getName() { return null; }
			public void generatePreview(Graphics2D g, Rectangle r) {
				Shape clip = g.getClip();
				BufferedImage i = (BufferedImage)filter(ImageUtils.toBufferedImage(src,200,200));
				g.setClip(r);
				g.drawImage(i, null, r.x + (r.width-i.getWidth())/2, r.y + (r.height-i.getHeight())/2);
				g.setClip(clip);
			}
		});
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("cellauto.IterationCount"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 0; }
			public int getStep() { return 1; }
			public int getValue() { return iterationCount; }
			public void setValue(int v) { iterationCount = v; if (ui != null) ui.update(); }
			public boolean useSlider() { return false; }
		});
		return f;
	}
	
	public final Image filter(Image src) {
		int[] states = getStateColors();
		int nxs = getNeighborhoodXStart();
		int nxe = getNeighborhoodXEnd();
		int nys = getNeighborhoodYStart();
		int nye = getNeighborhoodYEnd();
		int[][] n = new int[nye-nys+1][nxe-nxs+1];
		BufferedImage bi = ImageUtils.toBufferedImage(src, true);
		int w = bi.getWidth();
		int h = bi.getHeight();
		int[] oldpixels = new int[w*h];
		bi.getRGB(0, 0, w, h, oldpixels, 0, w);
		for (int i = 0; i < oldpixels.length; i++) {
			int oa = (oldpixels[i] >>> 24) & 0xFF;
			int or = (oldpixels[i] >>> 16) & 0xFF;
			int og = (oldpixels[i] >>>  8) & 0xFF;
			int ob = (oldpixels[i] >>>  0) & 0xFF;
			int dp = 0;
			int dd = 262144;
			for (int j = 0; j < states.length; j++) {
				int ca = (states[j] >>> 24) & 0xFF;
				int cr = (states[j] >>> 16) & 0xFF;
				int cg = (states[j] >>>  8) & 0xFF;
				int cb = (states[j] >>>  0) & 0xFF;
				int cae = oa-ca;
				int cre = or-cr;
				int cge = og-cg;
				int cbe = ob-cb;
				int cd = cae*cae + cre*cre + cge*cge + cbe*cbe;
				if (cd < dd) {
					dp = j;
					dd = cd;
				}
			}
			oldpixels[i] = dp;
		}
		for (int i = 0; i < iterationCount; i++) {
			int[] newpixels = new int[oldpixels.length];
			for (int y = 0, py = 0; y < h && py < oldpixels.length; y++, py += w) {
				for (int x = 0, px = py; x < w && px < oldpixels.length; x++, px++) {
					for (int ny = nys, nay = 0; ny <= nye && nay < n.length; ny++, nay++) {
						for (int nx = nxs, nax = 0; nx <= nxe && nax < n[nay].length; nx++, nax++) {
							if (y+ny < 0 || y+ny >= h || x+nx < 0 || x+nx >= w) {
								n[nay][nax] = 0;
							} else {
								n[nay][nax] = oldpixels[px + ny*w + nx];
							}
						}
					}
					newpixels[px] = getNextState(n);
				}
			}
			oldpixels = newpixels;
		}
		for (int i = 0; i < oldpixels.length; i++) {
			oldpixels[i] = states[oldpixels[i]];
		}
		bi.setRGB(0, 0, w, h, oldpixels, 0, w);
		return bi;
	}
}
