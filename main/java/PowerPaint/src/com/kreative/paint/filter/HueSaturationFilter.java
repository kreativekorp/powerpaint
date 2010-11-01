/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.util.ImageUtils;

public class HueSaturationFilter extends RGBFilter {
	public boolean usesOptionForm() {
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
			public String getName() { return FilterUtilities.messages.getString("channel.Hue"); }
			public int getMaximum() { return 360; }
			public int getMinimum() { return -360; }
			public int getStep() { return 1; }
			public int getValue() { return (int)(dh*360); }
			public void setValue(int v) { dh = v/360.0f; if (ui != null) ui.update(); }
			public boolean useSlider() { return true; }
		});
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("channel.Saturation"); }
			public int getMaximum() { return 100; }
			public int getMinimum() { return -100; }
			public int getStep() { return 1; }
			public int getValue() { return (int)(ds*100); }
			public void setValue(int v) { ds = v/100.0f; if (ui != null) ui.update(); }
			public boolean useSlider() { return true; }
		});
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("channel.Value"); }
			public int getMaximum() { return 100; }
			public int getMinimum() { return -100; }
			public int getStep() { return 1; }
			public int getValue() { return (int)(dv*100); }
			public void setValue(int v) { dv = v/100.0f; if (ui != null) ui.update(); }
			public boolean useSlider() { return true; }
		});
		return f;
	}
	
	private float dh = 0;
	private float ds = 0;
	private float dv = 0;
	
	protected int filterRGB(int x, int y, int color) {
		float[] hsv = Color.RGBtoHSB((color >>> 16) & 0xFF, (color >>> 8) & 0xFF, color & 0xFF, null);
		hsv[0] += dh; while (hsv[0] < 0) hsv[0] += 1; while (hsv[0] >= 1) hsv[0] -= 1;
		hsv[1] += ds; if (hsv[1] < 0) hsv[1] = 0; if (hsv[1] > 1) hsv[1] = 1;
		hsv[2] += dv; if (hsv[2] < 0) hsv[2] = 0; if (hsv[2] > 1) hsv[2] = 1;
		int rgb = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
		return (color & 0xFF000000) | (rgb & 0xFFFFFF);
	}
}
