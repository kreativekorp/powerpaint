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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.util.ImageUtils;

public class BrightnessContrastFilter extends RGBFilter {
	public boolean usesOptionForm() {
		return true;
	}
	
	public Form getOptionForm(final Image src) {
		prep(src);
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
			public String getName() { return FilterUtilities.messages.getString("channel.Brightness"); }
			public int getMaximum() { return 100; }
			public int getMinimum() { return -100; }
			public int getStep() { return 1; }
			public int getValue() { return getBrightness(); }
			public void setValue(int v) { setBrightness(v); }
			public boolean useSlider() { return true; }
		});
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("channel.Contrast"); }
			public int getMaximum() { return 100; }
			public int getMinimum() { return -100; }
			public int getStep() { return 1; }
			public int getValue() { return getContrast(); }
			public void setValue(int v) { setContrast(v); }
			public boolean useSlider() { return true; }
		});
		return f;
	}
	
	private int db;
	private int dc;
	private int minr, ming, minb;
	private int avgr, avgg, avgb;
	private int maxr, maxg, maxb;
	private int[] amr = new int[256];
	private int[] amg = new int[256];
	private int[] amb = new int[256];
	
	public BrightnessContrastFilter() {
		db = 0;
		dc = 0;
		minr = 0;
		ming = 0;
		minb = 0;
		avgr = 128;
		avgg = 128;
		avgb = 128;
		maxr = 255;
		maxg = 255;
		maxb = 255;
		for (int i = 0; i < 256; i++) { amr[i] = i; amg[i] = i; amb[i] = i; }
	}
	
	protected void prep(Image img) {
		BufferedImage bi = ImageUtils.toBufferedImage(img, false);
		int w = bi.getWidth();
		int h = bi.getHeight();
		int[] pixels = new int[w*h];
		bi.getRGB(0, 0, w, h, pixels, 0, w);
		int t = h;
		minr = 255;
		ming = 255;
		minb = 255;
		avgr = 0;
		avgg = 0;
		avgb = 0;
		maxr = 0;
		maxg = 0;
		maxb = 0;
		for (int y = 0, py = 0; y < h && py < w*h; y++, py += w) {
			int rt = w;
			int rar = 0;
			int rag = 0;
			int rab = 0;
			for (int x = 0, px = py; x < w && px < w*h; x++, px += w) {
				int c = pixels[px];
				int a = (c >>> 24) & 0xFF;
				int r = (c >>> 16) & 0xFF;
				int g = (c >>>  8) & 0xFF;
				int b = (c >>>  0) & 0xFF;
				if (a < 0x80) {
					rt--;
				} else {
					rar += r;
					rag += g;
					rab += b;
					if (r < minr) minr = r;
					if (g < ming) ming = g;
					if (b < minb) minb = b;
					if (r > maxr) maxr = r;
					if (g > maxg) maxg = g;
					if (b > maxb) maxb = b;
				}
			}
			if (rt == 0) {
				t--;
			} else {
				rar /= rt;
				rag /= rt;
				rab /= rt;
				avgr += rar;
				avgg += rag;
				avgb += rab;
			}
		}
		if (t == 0) {
			avgr = 128;
			avgg = 128;
			avgb = 128;
		} else {
			avgr /= t;
			avgg /= t;
			avgb /= t;
		}
		makeAdjustmentMap();
	}
	
	protected int getBrightness() {
		return db;
	}
	
	protected int getContrast() {
		return dc;
	}
	
	protected void setBrightness(int db) {
		this.db = db;
		makeAdjustmentMap();
		if (ui != null) ui.update();
	}
	
	protected void setContrast(int dc) {
		this.dc = dc;
		makeAdjustmentMap();
		if (ui != null) ui.update();
	}
	
	private void makeAdjustmentMap() {
		double ra = avgr/255.0;
		double ga = avgg/255.0;
		double ba = avgb/255.0;
		double b = db/100.0;
		double c = (dc <= 0) ? ((dc+100)/100.0) : (1.0+(dc/20.0));
		for (int i = 0; i < 256; i++) {
			double x = i/255.0;
			double r2 = ra+((x-ra)*c)+b;
			double g2 = ga+((x-ga)*c)+b;
			double b2 = ba+((x-ba)*c)+b;
			int r3 = (int)Math.round(r2*255.0);
			int g3 = (int)Math.round(g2*255.0);
			int b3 = (int)Math.round(b2*255.0);
			amr[i] = (r3 < 0) ? 0 : (r3 > 255) ? 255 : r3;
			amg[i] = (g3 < 0) ? 0 : (g3 > 255) ? 255 : g3;
			amb[i] = (b3 < 0) ? 0 : (b3 > 255) ? 255 : b3;
		}
	}
	
	protected int filterRGB(int x, int y, int color) {
		int a = (color >>> 24) & 0xFF;
		int r = (color >>> 16) & 0xFF;
		int g = (color >>>  8) & 0xFF;
		int b = (color >>>  0) & 0xFF;
		return (a << 24) | (amr[r] << 16) | (amg[g] << 8) | amb[b];
	}
}
