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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.form.PreviewGenerator;
import com.kreative.paint.util.ImageUtils;

public class ChannelMixerFilter extends RGBFilter {
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
			public String getName() { return FilterUtilities.messages.getString("channel.Alpha"); }
			public int getMaximum() { return 255; }
			public int getMinimum() { return -255; }
			public int getStep() { return 1; }
			public int getValue() { return da; }
			public void setValue(int v) { da = v; if (ui != null) ui.update(); }
			public boolean useSlider() { return true; }
		});
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("channel.Red"); }
			public int getMaximum() { return 255; }
			public int getMinimum() { return -255; }
			public int getStep() { return 1; }
			public int getValue() { return dr; }
			public void setValue(int v) { dr = v; if (ui != null) ui.update(); }
			public boolean useSlider() { return true; }
		});
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("channel.Green"); }
			public int getMaximum() { return 255; }
			public int getMinimum() { return -255; }
			public int getStep() { return 1; }
			public int getValue() { return dg; }
			public void setValue(int v) { dg = v; if (ui != null) ui.update(); }
			public boolean useSlider() { return true; }
		});
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("channel.Blue"); }
			public int getMaximum() { return 255; }
			public int getMinimum() { return -255; }
			public int getStep() { return 1; }
			public int getValue() { return db; }
			public void setValue(int v) { db = v; if (ui != null) ui.update(); }
			public boolean useSlider() { return true; }
		});
		f.add(new BooleanOption() {
			public String getName() { return ""; }
			public String getLabel(boolean v) { return FilterUtilities.messages.getString("channel.Highlights"); }
			public boolean getValue() { return hl; }
			public void setValue(boolean v) { hl = v; if (ui != null) ui.update(); }
			public boolean useTrueFalseLabels() { return false; }
		});
		f.add(new BooleanOption() {
			public String getName() { return FilterUtilities.messages.getString("channel.Affect"); }
			public String getLabel(boolean v) { return FilterUtilities.messages.getString("channel.Midtones"); }
			public boolean getValue() { return mt; }
			public void setValue(boolean v) { mt = v; if (ui != null) ui.update(); }
			public boolean useTrueFalseLabels() { return false; }
		});
		f.add(new BooleanOption() {
			public String getName() { return ""; }
			public String getLabel(boolean v) { return FilterUtilities.messages.getString("channel.Shadows"); }
			public boolean getValue() { return sh; }
			public void setValue(boolean v) { sh = v; if (ui != null) ui.update(); }
			public boolean useTrueFalseLabels() { return false; }
		});
		return f;
	}
	
	private int da = 0;
	private int dr = 0;
	private int dg = 0;
	private int db = 0;
	private boolean sh = true;
	private boolean mt = true;
	private boolean hl = true;
	
	protected int filterRGB(int x, int y, int color) {
		int a = ((color >>> 24) & 0xFF);
		int r = ((color >>> 16) & 0xFF);
		int g = ((color >>>  8) & 0xFF);
		int b = ((color >>>  0) & 0xFF);
		int k = (30*r + 59*g + 11*b) / 100;
		if ((k < 0x40) ? sh : (k >= 0xC0) ? hl : mt) {
			a += da; r += dr; g += dg; b += db;
			int a2 = (a < 0) ? 0 : (a > 255) ? 255 : a;
			int r2 = (r < 0) ? 0 : (r > 255) ? 255 : r;
			int g2 = (g < 0) ? 0 : (g > 255) ? 255 : g;
			int b2 = (b < 0) ? 0 : (b > 255) ? 255 : b;
			return (a2 << 24) | (r2 << 16) | (g2 << 8) | b2;
		} else {
			return color;
		}
	}
}
