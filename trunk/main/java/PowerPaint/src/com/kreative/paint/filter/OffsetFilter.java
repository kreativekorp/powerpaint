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
import java.awt.image.BufferedImage;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;

public class OffsetFilter extends AbstractFilter {
	public boolean usesOptionForm() {
		return true;
	}
	
	public Form getOptionForm(Image src) {
		Form f = new Form();
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("offset.Horiz"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return Integer.MIN_VALUE; }
			public int getStep() { return 1; }
			public int getValue() { return hoff; }
			public void setValue(int v) { hoff = v; }
			public boolean useSlider() { return false; }
		});
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("offset.Vert"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return Integer.MIN_VALUE; }
			public int getStep() { return 1; }
			public int getValue() { return voff; }
			public void setValue(int v) { voff = v; }
			public boolean useSlider() { return false; }
		});
		f.add(new BooleanOption() {
			public String getName() { return ""; }
			public boolean getValue() { return wrap; }
			public void setValue(boolean v) { wrap = v; }
			public boolean useTrueFalseLabels() { return false; }
			public String getLabel(boolean v) { return FilterUtilities.messages.getString("offset.Wrap"); }
		});
		return f;
	}
	
	private int hoff = 0;
	private int voff = 0;
	private boolean wrap = false;
	
	public Image filter(Image src) {
		int w = src.getWidth(null);
		int h = src.getHeight(null);
		int dx = hoff;
		int dy = voff;
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		if (wrap) {
			while (dx < 0) dx += w; dx %= w;
			while (dy < 0) dy += h; dy %= h;
			while (!g.drawImage(src, dx, dy, null));
			while (!g.drawImage(src, dx-w, dy, null));
			while (!g.drawImage(src, dx, dy-h, null));
			while (!g.drawImage(src, dx-w, dy-h, null));
		} else {
			while (!g.drawImage(src, dx, dy, null));
		}
		g.dispose();
		return bi;
	}
}
