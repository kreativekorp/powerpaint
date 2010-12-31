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

package com.kreative.paint.awt;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

public class PatternPaint implements Paint {
	private Paint forePaint;
	private Paint backPaint;
	private long pattern;
	
	public PatternPaint(Paint fore, Paint back, long pattern) {
		this.forePaint = fore;
		this.backPaint = back;
		this.pattern = pattern;
	}
	
	public Paint getForeground() {
		return forePaint;
	}
	
	public Paint getBackground() {
		return backPaint;
	}
	
	public long getPattern() {
		return pattern;
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		PaintContext fore = forePaint.createContext(cm, deviceBounds, userBounds, xform, hints);
		PaintContext back = backPaint.createContext(cm, deviceBounds, userBounds, xform, hints);
		return new PatternPaintContext(fore, back, pattern);
	}
	
	public int getTransparency() {
		int fore = forePaint.getTransparency();
		int back = backPaint.getTransparency();
		if (fore == back) return fore;
		else if (fore == TRANSLUCENT || back == TRANSLUCENT) return TRANSLUCENT;
		else if (fore == BITMASK || back == BITMASK) return BITMASK;
		else if (fore == OPAQUE || back == OPAQUE) return OPAQUE;
		else return OPAQUE;
	}
	
	private static class PatternPaintContext implements PaintContext {
		private PaintContext foreContext;
		private PaintContext backContext;
		private long pattern;
		
		public PatternPaintContext(PaintContext fore, PaintContext back, long pattern) {
			this.foreContext = fore;
			this.backContext = back;
			this.pattern = pattern;
		}

		public Raster getRaster(int x, int y, int w, int h) {
			if (pattern == -1) {
				return foreContext.getRaster(x, y, w, h);
			}
			else if (pattern == 0) {
				return backContext.getRaster(x, y, w, h);
			}
			else {
				Raster foreRaster = foreContext.getRaster(x, y, w, h);
				Raster backRaster = backContext.getRaster(x, y, w, h);
				ColorModel foreModel = foreContext.getColorModel();
				ColorModel backModel = backContext.getColorModel();
				BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				for (int ry = y, ny = 0; ny < h; ry++, ny++) {
					for (int rx = x, nx = 0; nx < w; rx++, nx++) {
						int rgb;
						if (((pattern >> (((ry & 7) << 3) | (rx & 7))) & 1) != 0) {
							rgb = foreModel.getRGB(foreRaster.getDataElements(nx, ny, null));
						} else {
							rgb = backModel.getRGB(backRaster.getDataElements(nx, ny, null));
						}
						img.setRGB(nx, ny, rgb);
					}
				}
				return img.getData();
			}
		}

		public ColorModel getColorModel() {
			if (pattern == -1) {
				return foreContext.getColorModel();
			}
			else if (pattern == 0) {
				return backContext.getColorModel();
			}
			else {
				BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
				return img.getColorModel();
			}
		}

		public void dispose() {
			foreContext.dispose();
			backContext.dispose();
		}
	}
}
