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

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

public class GradientColorMap extends TreeMap<Double,Color> implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	public GradientColorMap() {
		super();
	}
	
	public GradientColorMap(Color... colors) {
		super();
		for (int i = 0; i < colors.length; i++) {
			put((double)i/(double)(colors.length-1), colors[i]);
		}
	}
	
	public GradientColorMap(int... colors) {
		super();
		for (int i = 0; i < colors.length; i++) {
			put((double)i/(double)(colors.length-1), new Color(colors[i], true));
		}
	}
	
	public GradientColorMap clone() {
		GradientColorMap clone = new GradientColorMap();
		clone.putAll(this);
		return clone;
	}
	
	public int getRGB(double pos) {
		try {
			double prevPos = Double.NEGATIVE_INFINITY;
			Color  prevCol = null;
			double nextPos = Double.POSITIVE_INFINITY;
			Color  nextCol = null;
			for (Map.Entry<Double,Color> e : entrySet()) {
				double thisPos = e.getKey();
				Color  thisCol = e.getValue();
				if (thisPos <= pos && thisPos > prevPos) {
					prevPos = thisPos;
					prevCol = thisCol;
				}
				if (thisPos >= pos && thisPos < nextPos) {
					nextPos = thisPos;
					nextCol = thisCol;
				}
			}
			if (prevCol == null && nextCol == null) return 0;
			else if (prevPos == pos || nextCol == null) return prevCol.getRGB();
			else if (nextPos == pos || prevCol == null) return nextCol.getRGB();
			else {
				float f = (float)((pos - prevPos) / (nextPos - prevPos));
				float[] c1 = prevCol.getRGBComponents(new float[4]);
				float[] c2 = nextCol.getRGBComponents(new float[4]);
				float r = c1[0] + (c2[0]-c1[0])*f;
				float g = c1[1] + (c2[1]-c1[1])*f;
				float b = c1[2] + (c2[2]-c1[2])*f;
				float a = c1[3] + (c2[3]-c1[3])*f;
				int ri = (int)Math.round(r*255.0f) & 0xFF;
				int gi = (int)Math.round(g*255.0f) & 0xFF;
				int bi = (int)Math.round(b*255.0f) & 0xFF;
				int ai = (int)Math.round(a*255.0f) & 0xFF;
				return ((ai << 24) | (ri << 16) | (gi << 8) | bi);
			}
		} catch (NullPointerException npe) {
			return 0;
		}
	}
	
	public int getRGB(double pos, boolean repeat, boolean reflect, boolean reverse) {
		// repeating a reflected gradient gives an undesirable result,
		// so repeat has to be done BEFORE reflect
		if (repeat) {
			pos -= Math.floor(pos);
		}
		// reflecting a reversed gradient gives a non-unique result,
		// so reflect has to be done BEFORE reverse
		if (reflect) {
			if (pos <= 0.5) pos = pos * 2.0;
			else pos = (1.0 - pos) * 2.0;
		}
		// reversing any gradient always gives the expected result,
		// so reverse can be done last
		if (reverse) {
			pos = 1.0 - pos;
		}
		// the two restrictions above impose this specific
		// topological order on these three transformations
		return getRGB(pos);
	}
	
	public int[] getRGB(double[] pos) {
		// this is no more efficient than the caller doing the same thing,
		// but having this method opens the possibility of a more efficient
		// implementation in the future
		int[] ret = new int[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = getRGB(pos[i]);
		}
		return ret;
	}
	
	public int[] getRGB(double[] pos, boolean repeat, boolean reflect, boolean reverse) {
		// this is no more efficient than the caller doing the same thing,
		// but having this method opens the possibility of a more efficient
		// implementation in the future
		int[] ret = new int[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = getRGB(pos[i], repeat, reflect, reverse);
		}
		return ret;
	}
	
	public Color getColor(double pos) {
		double prevPos = Double.NEGATIVE_INFINITY;
		Color  prevCol = null;
		double nextPos = Double.POSITIVE_INFINITY;
		Color  nextCol = null;
		for (Map.Entry<Double,Color> e : entrySet()) {
			double thisPos = e.getKey();
			Color  thisCol = e.getValue();
			if (thisPos <= pos && thisPos > prevPos) {
				prevPos = thisPos;
				prevCol = thisCol;
			}
			if (thisPos >= pos && thisPos < nextPos) {
				nextPos = thisPos;
				nextCol = thisCol;
			}
		}
		if (prevCol == null && nextCol == null) return null;
		else if (prevPos == pos || nextCol == null) return prevCol;
		else if (nextPos == pos || prevCol == null) return nextCol;
		else {
			float f = (float)((pos - prevPos) / (nextPos - prevPos));
			float[] c1 = prevCol.getRGBComponents(new float[4]);
			float[] c2 = nextCol.getRGBComponents(new float[4]);
			float r = c1[0] + (c2[0]-c1[0])*f;
			float g = c1[1] + (c2[1]-c1[1])*f;
			float b = c1[2] + (c2[2]-c1[2])*f;
			float a = c1[3] + (c2[3]-c1[3])*f;
			return new Color(r,g,b,a);
		}
	}
	
	public Color getColor(double pos, boolean repeat, boolean reflect, boolean reverse) {
		// repeating a reflected gradient gives an undesirable result,
		// so repeat has to be done BEFORE reflect
		if (repeat) {
			pos -= Math.floor(pos);
		}
		// reflecting a reversed gradient gives a non-unique result,
		// so reflect has to be done BEFORE reverse
		if (reflect) {
			if (pos <= 0.5) pos = pos * 2.0;
			else pos = (1.0 - pos) * 2.0;
		}
		// reversing any gradient always gives the expected result,
		// so reverse can be done last
		if (reverse) {
			pos = 1.0 - pos;
		}
		// the two restrictions above impose this specific
		// topological order on these three transformations
		return getColor(pos);
	}
	
	public Color[] getColors(double[] pos) {
		// this is no more efficient than the caller doing the same thing,
		// but having this method opens the possibility of a more efficient
		// implementation in the future
		Color[] ret = new Color[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = getColor(pos[i]);
		}
		return ret;
	}
	
	public Color[] getColors(double[] pos, boolean repeat, boolean reflect, boolean reverse) {
		// this is no more efficient than the caller doing the same thing,
		// but having this method opens the possibility of a more efficient
		// implementation in the future
		Color[] ret = new Color[pos.length];
		for (int i = 0; i < pos.length; i++) {
			ret[i] = getColor(pos[i], repeat, reflect, reverse);
		}
		return ret;
	}
}
