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

package com.kreative.paint;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.document.undo.Recordable;

public class Tile implements PaintSurface, Paintable, Recordable {
	private History history;
	private int x;
	private int y;
	private int width;
	private int height;
	private BufferedImage image;
	
	public Tile(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	public History getHistory() {
		return history;
	}
	
	public void setHistory(History history) {
		this.history = history;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Point getLocation() {
		return new Point(x, y);
	}
	
	private static class LocationAtom implements Atom {
		private Tile t;
		private int oldX, oldY;
		private int newX, newY;
		public LocationAtom(Tile t, int nx, int ny) {
			this.t = t;
			this.oldX = t.x;
			this.oldY = t.y;
			this.newX = nx;
			this.newY = ny;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldX = ((LocationAtom)previousAtom).oldX;
			this.oldY = ((LocationAtom)previousAtom).oldY;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof LocationAtom) && ((LocationAtom)previousAtom).t == this.t;
		}
		public void redo() {
			t.x = newX;
			t.y = newY;
		}
		public void undo() {
			t.x = oldX;
			t.y = oldY;
		}
	}
	
	public void setX(int x) {
		if (this.x == x) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.x = x;
	}
	
	public void setY(int y) {
		if (this.y == y) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.y = y;
	}
	
	public void setLocation(Point p) {
		if (this.x == p.x && this.y == p.y) return;
		if (history != null) history.add(new LocationAtom(this, p.x, p.y));
		this.x = p.x;
		this.y = p.y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void paint(Graphics2D g) {
		g.drawImage(image, null, x, y);
	}
	
	public void paint(Graphics2D g, int tx, int ty) {
		g.drawImage(image, null, x+tx, y+ty);
	}
	
	public Graphics2D createPaintGraphics() {
		return new TileGraphics(history, image);
	}
	
	public int getMinX() {
		return 0;
	}
	
	public int getMinY() {
		return 0;
	}
	
	public int getMaxX() {
		return image.getWidth();
	}
	
	public int getMaxY() {
		return image.getHeight();
	}
	
	public boolean contains(int x, int y) {
		return x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight();
	}
	
	public boolean contains(int x, int y, int width, int height) {
		return x >= 0 && y >= 0 && x+width <= image.getWidth() && y+height <= image.getHeight();
	}
	
	public int getRGB(int x, int y) {
		try {
			int w = image.getWidth();
			WritableRaster r = image.getRaster();
			DataBufferInt b = (DataBufferInt)r.getDataBuffer();
			int[] d = b.getData();
			return d[w*y + x];
		} catch (Exception e) {
			System.err.println("Error: "+e.getMessage());
			return image.getRGB(x, y);
		}
	}
	
	public int[] getRGB(int bx, int by, int width, int height, int[] rgb, int offset, int rowCount) {
		try {
			int w = image.getWidth();
			WritableRaster r = image.getRaster();
			DataBufferInt b = (DataBufferInt)r.getDataBuffer();
			int[] d = b.getData();
			if (rgb == null) rgb = new int[offset + rowCount * height];
			for (int sa = w*by + bx, da = offset, y = 0; y < height; sa += w, da += rowCount, y++) {
				for (int saa = sa, daa = da, x = 0; x < width; saa++, daa++, x++) {
					rgb[daa] = d[saa];
				}
			}
			return rgb;
		} catch (Exception e) {
			System.err.println("Error: "+e.getMessage());
			return image.getRGB(bx, by, width, height, rgb, offset, rowCount);
		}
	}
	
	private static class SetRGBAtom implements Atom {
		private Tile t;
		private int x, y, width, height;
		private int[] oldRGB;
		private int[] newRGB;
		private int offset, rowCount;
		public SetRGBAtom(Tile t, int x, int y, int width, int height, int[] rgb, int offset, int rowCount) {
			this.t = t;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.oldRGB = new int[width*height];
			t.getRGB(x, y, width, height, this.oldRGB, 0, width);
			this.newRGB = rgb;
			this.offset = offset;
			this.rowCount = rowCount;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldRGB = ((SetRGBAtom)previousAtom).oldRGB;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof SetRGBAtom) && (((SetRGBAtom)previousAtom).t == this.t)
				&& (((SetRGBAtom)previousAtom).x == this.x) && (((SetRGBAtom)previousAtom).y == this.y)
				&& (((SetRGBAtom)previousAtom).width == this.width) && (((SetRGBAtom)previousAtom).height == this.height);
		}
		public void redo() {
			t.image.setRGB(x, y, width, height, newRGB, offset, rowCount);
		}
		public void undo() {
			t.image.setRGB(x, y, width, height, oldRGB, 0, width);
		}
	}
	
	public void setRGB(int x, int y, int rgb) {
		if (history != null) history.add(new SetRGBAtom(this, x, y, 1, 1, new int[]{rgb}, 0, 1));
		image.setRGB(x, y, rgb);
	}
	
	public void setRGB(int bx, int by, int width, int height, int[] rgb, int offset, int rowCount) {
		if (history != null) history.add(new SetRGBAtom(this, bx, by, width, height, rgb, offset, rowCount));
		image.setRGB(bx, by, width, height, rgb, offset, rowCount);
	}
	
	public void clear(int x, int y, int width, int height) {
		int[] b = new int[width*height];
		if (history != null) history.add(new SetRGBAtom(this, x, y, width, height, b, 0, width));
		image.setRGB(x, y, width, height, b, 0, width);
	}
	
	private static class ClearAllAtom implements Atom {
		private Tile t;
		private BufferedImage oldbi;
		private BufferedImage newbi;
		public ClearAllAtom(Tile t, BufferedImage bi) {
			this.t = t;
			this.oldbi = t.image;
			this.newbi = bi;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldbi = ((ClearAllAtom)previousAtom).oldbi;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof ClearAllAtom) && ((ClearAllAtom)previousAtom).t == this.t;
		}
		public void redo() {
			t.image = newbi;
		}
		public void undo() {
			t.image = oldbi;
		}
	}
	
	public void clearAll() {
		BufferedImage newbi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		if (history != null) history.add(new ClearAllAtom(this, newbi));
		image = newbi;
	}
	
	public String toString() {
		return "com.kreative.paint.Tile["+x+","+y+","+width+","+height+","+image+"]";
	}
}
