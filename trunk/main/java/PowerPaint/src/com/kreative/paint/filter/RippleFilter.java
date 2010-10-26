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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;

public class RippleFilter extends AbstractFilter {
	public boolean usesOptionForm() {
		return true;
	}
	
	public Form getOptionForm(Image src) {
		Form f = new Form();
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("ripple.Amount"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return amt; }
			public void setValue(int v) { amt = v; }
			public boolean useSlider() { return false; }
		});
		return f;
	}
	
	public Image filter(Image src) {
		return filter(src, amt);
	}
	
	private int amt = 1;

	private Image filter(Image img, int amt) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		int d = Math.abs(amt);
		boolean b = (amt >= 0);
		int i = 0, x, y;
		BufferedImage p = new BufferedImage(w+d+d, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = p.getGraphics();
		for (x = d, y = 0; y <= h; y++) {
			if (b) {
				i++;
				if (i == d+1) {
					b = false;
					i = d;
				}
			} else {
				i--;
				if (i == -d-1) {
					b = true;
					i = -d;
				}
			}
			g.drawImage(img, x+i, y, x+i+w, y+1, 0, y, w, y+1, null);
		}
		BufferedImage p2 = new BufferedImage(w+d+d,h+d+d,BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = p2.getGraphics();
		for (y = d, x = 0; x <= w+d+d; x++) {
			if (b) {
				i++;
				if (i == d+1) {
					b = false;
					i = d;
				}
			} else {
				i--;
				if (i == -d-1) {
					b = true;
					i = -d;
				}
			}
			g2.drawImage(p, x, y+i, x+1, y+i+h, x, 0, x+1, h, null);
		}
		p = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g = p.getGraphics();
		g.drawImage(p2, 0, 0, w, h, d, d, d+w, d+h, null);
		return p;
	}
}
