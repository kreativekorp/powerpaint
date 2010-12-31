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

package com.kreative.paint.gradient;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class GradientPaint2 implements Paint, Cloneable {
	private GradientShape gs;
	private GradientColorMap gcm;
	private Rectangle2D gb;
	
	public GradientPaint2(Gradient g) {
		this.gs = (g.shape == null) ? null : g.shape.clone();
		this.gcm = (g.colorMap == null) ? null : g.colorMap.clone();
		this.gb = (g.boundingRect == null) ? null : (Rectangle2D)g.boundingRect.clone();
	}
	
	public GradientPaint2(GradientShape gs, GradientColorMap gcm) {
		this.gs = (gs == null) ? null : gs.clone();
		this.gcm = (gcm == null) ? null : gcm.clone();
		this.gb = null;
	}
	
	public GradientPaint2(GradientShape gs, GradientColorMap gcm, Rectangle2D gb) {
		this.gs = (gs == null) ? null : gs.clone();
		this.gcm = (gcm == null) ? null : gcm.clone();
		this.gb = (gb == null) ? null : (Rectangle2D)gb.clone();
	}
	
	public GradientPaint2(GradientPaint2 gp) {
		this.gs = (gp.gs == null) ? null : gp.gs.clone();
		this.gcm = (gp.gcm == null) ? null : gp.gcm.clone();
		this.gb = (gp.gb == null) ? null : (Rectangle2D)gp.gb.clone();
	}
	
	public GradientPaint2 clone() {
		return new GradientPaint2(this);
	}
	
	public Gradient getGradient() {
		return new Gradient(
				((this.gs == null) ? null : this.gs.clone()),
				((this.gcm == null) ? null : this.gcm.clone()),
				((this.gb == null) ? null : (Rectangle2D)this.gb.clone())
		);
	}
	
	public GradientShape getGradientShape() {
		return (this.gs == null) ? null : this.gs.clone();
	}
	
	public GradientColorMap getGradientColorMap() {
		return (this.gcm == null) ? null : this.gcm.clone();
	}
	
	public Rectangle2D getGradientBounds() {
		return (this.gb == null) ? null : (Rectangle2D)this.gb.clone();
	}
	
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		return new GradientPaintContext(this.gs, this.gcm, this.gb, cm, deviceBounds, userBounds, xform, hints);
	}

	public int getTransparency() {
		return TRANSLUCENT;
	}
	
	private static class GradientPaintContext implements PaintContext {
		private GradientShape gs;
		private GradientColorMap gcm;
		private Rectangle2D gb;
		//private ColorModel cm;
		private Rectangle deviceBounds;
		//private Rectangle2D userBounds;
		private AffineTransform xform;
		//private RenderingHints hints;
		
		public GradientPaintContext(GradientShape gs, GradientColorMap gcm, Rectangle2D gb, ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
			this.gs = gs;
			this.gcm = gcm;
			this.gb = gb;
			//this.cm = cm;
			this.deviceBounds = deviceBounds;
			//this.userBounds = userBounds;
			this.xform = xform;
			//this.hints = hints;
		}
		
		public Raster getRaster(int x, int y, int w, int h) {
			Rectangle dgb;
			if (gb != null) {
				dgb = xform.createTransformedShape(gb).getBounds();
			} else {
				dgb = deviceBounds;
			}
			double[] gx = new double[w*h];
			double[] gy = new double[w*h];
			for (int ay = 0, ry = y; ay < w*h; ay += w, ry++) {
				for (int ax = 0, rx = x; ax < w; ax++, rx++) {
					gx[ay+ax] = (double)(rx - dgb.x) / (double)dgb.width;
					gy[ay+ax] = (double)(ry - dgb.y) / (double)dgb.height;
				}
			}
			double[] gp = this.gs.getGradientPositions(gx, gy, w*h);
			int[] rgb = this.gcm.getRGB(gp, this.gs.repeat, this.gs.reflect, this.gs.reverse);
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, w, h, rgb, 0, w);
			return img.getData();
		}
		
		public ColorModel getColorModel() {
			BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
			return img.getColorModel();
		}
		
		public void dispose() {
			// nothing
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof Gradient) {
			Gradient other = (Gradient)o;
			return (
					((this.gs == null) ? (other.shape == null) : (other.shape == null) ? (this.gs == null) : this.gs.equals(other.shape)) &&
					((this.gcm == null) ? (other.colorMap == null) : (other.colorMap == null) ? (this.gcm == null) : this.gcm.equals(other.colorMap)) &&
					((this.gb == null) ? (other.boundingRect == null) : (other.boundingRect == null) ? (this.gb == null) : this.gb.equals(other.boundingRect))
			);
		} else if (o instanceof GradientPaint2) {
			GradientPaint2 other = (GradientPaint2)o;
			return (
					((this.gs == null) ? (other.gs == null) : (other.gs == null) ? (this.gs == null) : this.gs.equals(other.gs)) &&
					((this.gcm == null) ? (other.gcm == null) : (other.gcm == null) ? (this.gcm == null) : this.gcm.equals(other.gcm)) &&
					((this.gb == null) ? (other.gb == null) : (other.gb == null) ? (this.gb == null) : this.gb.equals(other.gb))
			);
		}
		return false;
	}
}
