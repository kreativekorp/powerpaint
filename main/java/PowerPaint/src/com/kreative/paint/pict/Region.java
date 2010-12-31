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

package com.kreative.paint.pict;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Region implements Shape {
	public int rgnSize = 10;
	public Rect rgnBBox = new Rect();
	public List<Short> rgnData = new Vector<Short>();
	
	public static Region read(DataInputStream in) throws IOException {
		Region r = new Region();
		r.rgnSize = in.readUnsignedShort();
		r.rgnBBox = Rect.read(in);
		r.rgnData = new Vector<Short>();
		for (int i = 10; i < r.rgnSize; i += 2) {
			r.rgnData.add(in.readShort());
		}
		return r;
	}
	
	public Region() {
		this.rgnSize = 10;
		this.rgnBBox = new Rect();
		this.rgnData = new Vector<Short>();
	}
	
	public Region(int x, int y, int width, int height) {
		this.rgnSize = 10;
		this.rgnBBox = new Rect(x, y, width, height);
		this.rgnData = new Vector<Short>();
	}
	
	public void write(DataOutputStream out) throws IOException {
		if (rgnData.isEmpty()) {
			out.writeShort(rgnSize = 10);
			rgnBBox.write(out);
		} else {
			int minx = Integer.MAX_VALUE, maxx = Integer.MIN_VALUE;
			int miny = Integer.MAX_VALUE, maxy = Integer.MIN_VALUE;
			Iterator<Short> i = rgnData.iterator();
			while (i.hasNext()) {
				int row = i.next();
				if (row == 0x7FFF) break;
				else {
					if (row < miny) miny = row;
					if (row > maxy) maxy = row;
					while (i.hasNext()) {
						int col = i.next();
						if (col == 0x7FFF) break;
						else {
							if (col < minx) minx = col;
							if (col > maxx) maxx = col;
						}
					}
				}
			}
			if (minx < maxx && miny < maxy) {
				out.writeShort(rgnSize = 10+rgnData.size()*2);
				(rgnBBox = new Rect(minx, miny, maxx-minx, maxy-miny)).write(out);
				for (Short s : rgnData) {
					out.writeShort(s);
				}
			} else {
				out.writeShort(rgnSize = 10);
				(rgnBBox = new Rect(rgnBBox.left, rgnBBox.top, 0, 0)).write(out);
				rgnData.clear();
			}
		}
	}
	
	public static Region fromAlpha(int x, int y, int width, int height, int[] rgb, int offset, int scanwidth) {
		byte[] tmp = new byte[(width+1)*(height+1)];
		for (int ly = 0, py = offset, ty = 0; ly < height; ly++, py += scanwidth, ty += width+1) {
			for (int lx = 0, px = py, tx = ty; lx < width; lx++, px++, tx++) {
				if (rgb[px] < 0) {
					tmp[tx] ^= 1;
					tmp[tx+1] ^= 1;
					tmp[tx+width+1] ^= 1;
					tmp[tx+width+2] ^= 1;
				}
			}
		}
		return fromBits(x, y, width, height, tmp);
	}
	
	public static Region fromRGB(int x, int y, int width, int height, int[] rgb, int offset, int scanwidth) {
		byte[] tmp = new byte[(width+1)*(height+1)];
		for (int ly = 0, py = offset, ty = 0; ly < height; ly++, py += scanwidth, ty += width+1) {
			for (int lx = 0, px = py, tx = ty; lx < width; lx++, px++, tx++) {
				if (rgb[px] < 0) {
					int r = (rgb[px] >>> 16) & 0xFF;
					int g = (rgb[px] >>> 8) & 0xFF;
					int b = rgb[px] & 0xFF;
					int k = (30*r + 59*g + 11*b) / 100;
					if (k < 0x80) {
						tmp[tx] ^= 1;
						tmp[tx+1] ^= 1;
						tmp[tx+width+1] ^= 1;
						tmp[tx+width+2] ^= 1;
					}
				}
			}
		}
		return fromBits(x, y, width, height, tmp);
	}
	
	public static Region fromShape(Shape s) {
		Rectangle r = s.getBounds();
		byte[] tmp = new byte[(r.width+1)*(r.height+1)];
		for (int ly = 0, sy = r.y, ty = 0; ly < r.height; ly++, sy++, ty += r.width+1) {
			for (int lx = 0, sx = r.x, tx = ty; lx < r.width; lx++, sx++, tx++) {
				if (s.contains(sx, sy)) {
					tmp[tx] ^= 1;
					tmp[tx+1] ^= 1;
					tmp[tx+r.width+1] ^= 1;
					tmp[tx+r.width+2] ^= 1;
				}
			}
		}
		return fromBits(r.x, r.y, r.width, r.height, tmp);
	}
	
	public static Region fromImage(int x, int y, Image i, ImageObserver obs) {
		int w = i.getWidth(obs);
		int h = i.getHeight(obs);
		if (w < 0 || h < 0) return null;
		BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bg = bimg.createGraphics();
		if (!bg.drawImage(i, 0, 0, obs)) return null;
		bg.dispose();
		int[] rgb = new int[w*h];
		bimg.getRGB(0, 0, w, h, rgb, 0, w);
		return fromRGB(x, y, w, h, rgb, 0, w);
	}
	
	private static Region fromBits(int x, int y, int width, int height, byte[] tmp) {
		int minx = x+width;
		int miny = y+height;
		int maxx = x;
		int maxy = y;
		List<Short> data = new Vector<Short>();
		for (int ly = 0, ty = 0; ly < height+1; ly++, ty += width+1) {
			boolean wroteRow = false;
			for (int lx = 0, tx = ty; lx < width+1; lx++, tx++) {
				if (tmp[tx] != 0) {
					if (!wroteRow) {
						data.add((short)(y+ly));
						wroteRow = true;
					}
					data.add((short)(x+lx));
					if (x+lx < minx) minx = x+lx;
					if (x+lx > maxx) maxx = x+lx;
				}
			}
			if (wroteRow) {
				data.add((short)0x7FFF);
				if (y+ly < miny) miny = y+ly;
				if (y+ly > maxy) maxy = y+ly;
			}
		}
		data.add((short)0x7FFF);
		if (maxx < minx || maxy < miny) {
			minx = maxx = x;
			miny = maxy = y;
			data.clear();
		}
		Region r = new Region();
		r.rgnSize = 10+data.size()*2;
		r.rgnBBox = new Rect(minx, miny, maxx-minx, maxy-miny);
		r.rgnData = data;
		return r;
	}
	
	public Area toArea() {
		if (rgnData.isEmpty()) {
			return new Area(new Rectangle(rgnBBox.left, rgnBBox.top, rgnBBox.right-rgnBBox.left, rgnBBox.bottom-rgnBBox.top));
		} else {
			Area a = new Area();
			Iterator<Short> i = rgnData.iterator();
			while (i.hasNext()) {
				int row = i.next();
				if (row == 0x7FFF) break;
				else {
					while (i.hasNext()) {
						int col = i.next();
						if (col == 0x7FFF) break;
						else {
							a.exclusiveOr(new Area(new Rectangle(col, row, rgnBBox.right-col, rgnBBox.bottom-row)));
						}
					}
				}
			}
			return a;
		}
	}
	
	public BufferedImage toBufferedImage() {
		int w = rgnBBox.right-rgnBBox.left;
		int h = rgnBBox.bottom-rgnBBox.top;
		BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bg = bimg.createGraphics();
		Shape s = AffineTransform.getTranslateInstance(-rgnBBox.left, -rgnBBox.top).createTransformedShape(toArea());
		bg.setPaint(Color.black);
		bg.fill(s);
		bg.dispose();
		return bimg;
	}
	
	public void toRGB(int x, int y, int width, int height, int[] rgb, int offset, int scanwidth) {
		BufferedImage bimg = toBufferedImage();
		bimg.getRGB(x-rgnBBox.left, y-rgnBBox.top, width, height, rgb, offset, scanwidth);
	}
	
	public String toString() {
		return "Region[" + rgnSize + "," + rgnBBox.toString() + "]";
	}

	public boolean contains(Point2D p) {
		return toArea().contains(p);
	}

	public boolean contains(Rectangle2D r) {
		return toArea().contains(r);
	}

	public boolean contains(double x, double y) {
		return toArea().contains(x,y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return toArea().contains(x,y,w,h);
	}

	public Rectangle getBounds() {
		return toArea().getBounds();
	}

	public Rectangle2D getBounds2D() {
		return toArea().getBounds2D();
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return toArea().getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return toArea().getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		return toArea().intersects(r);
	}

	public boolean intersects(double x, double y, double w, double h) {
		return toArea().intersects(x,y,w,h);
	}
}
