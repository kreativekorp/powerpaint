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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerEnumOption;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.util.ImageUtils;

public class MosaicFilter extends AbstractFilter {
	public boolean usesOptionForm() {
		return true;
	}
	
	public Form getOptionForm(Image src) {
		Form f = new Form();
		f.add(new IntegerOption() {
			public String getName() { return FilterUtilities.messages.getString("mosaic.Size"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 2; }
			public int getStep() { return 1; }
			public int getValue() { return size; }
			public void setValue(int v) { size = v; }
			public boolean useSlider() { return false; }
		});
		f.add(new IntegerEnumOption() {
			public String getName() { return FilterUtilities.messages.getString("mosaic.Grid"); }
			public int getValue() { return gcol; }
			public void setValue(int v) { gcol = v; }
			public int[] values() { return new int[]{0,1,2,3,4,5}; }
			public String getLabel(int v) {
				switch (v) {
				case 0: return FilterUtilities.messages.getString("mosaic.Grid.None");
				case 1: return FilterUtilities.messages.getString("mosaic.Grid.White");
				case 2: return FilterUtilities.messages.getString("mosaic.Grid.LightGray");
				case 3: return FilterUtilities.messages.getString("mosaic.Grid.Gray");
				case 4: return FilterUtilities.messages.getString("mosaic.Grid.DarkGray");
				case 5: return FilterUtilities.messages.getString("mosaic.Grid.Black");
				default: return null;
				}
			}
		});
		return f;
	}
	
	public Image filter(Image src) {
		return filter(src, size, gcol != 0, gcol());
	}
	
	private int size = 8;
	private int gcol = 0;
	
	private Color gcol() {
		switch (gcol) {
		case 1: return Color.white;
		case 2: return Color.lightGray;
		case 3: return Color.gray;
		case 4: return Color.darkGray;
		case 5: return Color.black;
		default: return Color.white;
		}
	}

	private Image filter(Image img, int amt, boolean grid, Color gcol) {
		BufferedImage bmg = ImageUtils.toBufferedImage(img);
		int w = bmg.getWidth();
	    int h = bmg.getHeight();
	    int sw = w/amt;
	    int sh = h/amt;
	    BufferedImage bmg2 = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = bmg2.createGraphics();
		g2.drawImage(bmg, new AffineTransformOp(AffineTransform.getScaleInstance(1.0/amt,1.0/amt),AffineTransformOp.TYPE_BICUBIC), 0, 0);
		g2.dispose();
	    BufferedImage bmg3 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g3 = bmg3.createGraphics();
	    g3.drawImage(bmg2, new AffineTransformOp(AffineTransform.getScaleInstance((double)amt,(double)amt),AffineTransformOp.TYPE_NEAREST_NEIGHBOR), 0, 0);
	    if (grid) {
	    	g3.setColor(gcol);
	    	for (int x=0; x<w; x+=amt) {
	    		g3.drawLine(x, 0, x, h-1);
	    	}
	    	for (int y=0; y<h; y+=amt) {
	    		g3.drawLine(0, y, w-1, y);
	    	}
	    }
	    g3.dispose();
	    return bmg3;
	}
}
