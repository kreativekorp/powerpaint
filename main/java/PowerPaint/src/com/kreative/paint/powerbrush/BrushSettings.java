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

package com.kreative.paint.powerbrush;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.BitSet;
import java.util.Random;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.util.Bitmap;

public class BrushSettings {
	public static final BrushShape[] SHAPES = new BrushShape[] {
		RoundBrushShape.instance,
		SquareBrushShape.instance,
		RoundRectBrushShape.instance,
		DiamondBrushShape.instance,
		YNXDiagonalBrushShape.instance,
		YXDiagonalBrushShape.instance,
		XBrushShape.instance,
		VerticalBrushShape.instance,
		HorizontalBrushShape.instance,
	};
	
	private BrushShape shape;
	private float outerWidth;
	private float outerHeight;
	private float innerWidth;
	private float innerHeight;
	private int flowRate;

	public BrushSettings() {
		this.shape = RoundBrushShape.instance;
		this.outerWidth = 16.0f;
		this.outerHeight = 16.0f;
		this.innerWidth = 16.0f;
		this.innerHeight = 16.0f;
		this.flowRate = 20;
		setCache();
	}
	
	public BrushSettings(BrushShape shape, float outerWidth, float outerHeight, float innerWidth, float innerHeight, int flowRate) {
		this.shape = shape;
		this.outerWidth = outerWidth;
		this.outerHeight = outerHeight;
		this.innerWidth = innerWidth;
		this.innerHeight = innerHeight;
		this.flowRate = flowRate;
		setCache();
	}
	
	private float m;
	private int mx;
	private Shape br;
	private Shape[] brs;
	private int bmw, bmh;
	private BitSet bmi;
	private Random bmr;
	
	private void setCache() {
		float minw = Math.min(innerWidth, outerWidth);
		float minh = Math.min(innerHeight, outerHeight);
		float maxw = Math.max(innerWidth, outerWidth);
		float maxh = Math.max(innerHeight, outerHeight);
		m = Math.max(maxw-minw, maxh-minh);
		mx = (int)Math.ceil(m);
		br = shape.makeBrush(0, 0, maxw, maxh);
		brs = new Shape[mx+1];
		for (int i = 0; i <= mx; i++) {
			float d = (float)i/(float)mx;
			float w = maxw + (minw-maxw)*d;
			float h = maxh + (minh-maxh)*d;
			brs[i] = shape.makeBrush(0, 0, w, h);
		}
		bmw = (int)Math.ceil(maxw/2) * 2;
		bmh = (int)Math.ceil(maxh/2) * 2;
		bmi = new BitSet();
		Shape bms = shape.makeBrush(bmw/2, bmh/2, maxw, maxh);
		for (int by = 0; by < bmh; by++) {
			for (int bx = 0; bx < bmw; bx++) {
				if (bms.intersects(bx, by, 1, 1)) {
					bmi.set(by*bmw + bx);
				}
			}
		}
		bmr = new Random();
	}
	
	public BrushShape getBrushShape() {
		return shape;
	}
	
	public float getOuterWidth() {
		return outerWidth;
	}
	
	public float getOuterHeight() {
		return outerHeight;
	}
	
	public float getInnerWidth() {
		return innerWidth;
	}
	
	public float getInnerHeight() {
		return innerHeight;
	}
	
	public int getFlowRate() {
		return flowRate;
	}
	
	public void paint(Graphics2D g, float x, float y) {
		if (br == null || brs == null) setCache();
		boolean single = m < 1.0 || !(g.getComposite() instanceof AlphaComposite);
		AffineTransform tx = AffineTransform.getTranslateInstance(x, y);
		if (single) {
			g.fill(tx.createTransformedShape(br));
		} else {
			AlphaComposite c = (AlphaComposite)g.getComposite();
			g.setComposite(AlphaComposite.getInstance(c.getRule(), c.getAlpha()/mx));
			for (int i = 0; i <= mx; i++) {
				g.fill(tx.createTransformedShape(brs[i]));
			}
			g.setComposite(c);
		}
	}
	
	public void paint(Graphics2D g, boolean includeStart, float sx, float sy, float dx, float dy) {
		if (br == null || brs == null) setCache();
		boolean single = m < 1.0 || !(g.getComposite() instanceof AlphaComposite);
		AffineTransform tx = AffineTransform.getTranslateInstance(sx, sy);
		if (includeStart) {
			if (single) {
				g.fill(tx.createTransformedShape(br));
			} else {
				AlphaComposite c = (AlphaComposite)g.getComposite();
				g.setComposite(AlphaComposite.getInstance(c.getRule(), c.getAlpha()/mx));
				for (int i = 0; i <= mx; i++) {
					g.fill(tx.createTransformedShape(brs[i]));
				}
				g.setComposite(c);
			}
		}
		int n = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int j = 1; j <= n; j++) {
			float x = sx + ((dx-sx)*j)/n;
			float y = sy + ((dy-sy)*j)/n;
			tx.setToTranslation(x, y);
			if (single) {
				g.fill(tx.createTransformedShape(br));
			} else {
				AlphaComposite c = (AlphaComposite)g.getComposite();
				g.setComposite(AlphaComposite.getInstance(c.getRule(), c.getAlpha()/mx));
				for (int i = 0; i <= mx; i++) {
					g.fill(tx.createTransformedShape(brs[i]));
				}
				g.setComposite(c);
			}
		}
	}
	
	public void spray(PaintSurface srf, Graphics2D g, float x, float y) {
		int[] rgb = new int[bmw*bmh];
		for (int i = 0, k = flowRate; k >= 0; k--) {
			i = bmr.nextInt(rgb.length); if (bmi.get(i)) rgb[i] = 0xFF000000;
			i = bmr.nextInt(rgb.length); if (bmi.get(i)) rgb[i] = 0xFF000000;
			i = bmr.nextInt(rgb.length); if (bmi.get(i)) rgb[i] = 0xFF000000;
			i = bmr.nextInt(rgb.length); if (bmi.get(i)) rgb[i] = 0xFF000000;
			/*
			do { i = bmr.nextInt(rgb.length); } while (!bmi.get(i));
			rgb[i] = 0xFF000000;
			do { i = bmr.nextInt(rgb.length); } while (!bmi.get(i));
			rgb[i] = 0xFF000000;
			do { i = bmr.nextInt(rgb.length); } while (!bmi.get(i));
			rgb[i] = 0xFF000000;
			do { i = bmr.nextInt(rgb.length); } while (!bmi.get(i));
			rgb[i] = 0xFF000000;
			*/
		}
		new Bitmap(bmw, bmh, rgb).paint(srf, g, (int)x-bmw/2, (int)y-bmh/2);
	}
}
