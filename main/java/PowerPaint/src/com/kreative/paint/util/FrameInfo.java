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

package com.kreative.paint.util;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class FrameInfo implements Cloneable {
	public Rectangle outerRect;
	public Rectangle innerRect;
	public int roundOffMultipleX;
	public int roundOffOffsetX;
	public int roundOffMultipleY;
	public int roundOffOffsetY;
	
	public FrameInfo() {
		outerRect = null;
		innerRect = null;
		roundOffMultipleX = 0;
		roundOffOffsetX = 0;
		roundOffMultipleY = 0;
		roundOffOffsetY = 0;
	}
	
	public FrameInfo(Rectangle outer, Rectangle inner, int romx, int roox, int romy, int rooy) {
		outerRect = outer;
		innerRect = inner;
		roundOffMultipleX = romx;
		roundOffOffsetX = roox;
		roundOffMultipleY = romy;
		roundOffOffsetY = rooy;
	}
	
	public FrameInfo(byte[] b, BufferedImage img) {
		this();
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(b));
			int x, y, w, h;
			x = in.readShort();
			y = in.readShort();
			w = in.readShort();
			h = in.readShort();
			if (x != 0 || y != 0 || w != 0 || h != 0)
				outerRect = new Rectangle(x,y,w,h);
			else
				autoOuterRect(img);
			x = in.readShort();
			y = in.readShort();
			w = in.readShort();
			h = in.readShort();
			if (x != 0 || y != 0 || w != 0 || h != 0)
				innerRect = new Rectangle(x,y,w,h);
			else
				autoInnerRect(img);
			roundOffMultipleX = in.readShort();
			roundOffOffsetX = in.readShort();
			roundOffMultipleY = in.readShort();
			roundOffOffsetY = in.readShort();
			in.close();
		} catch (IOException ioe) {}
	}
	
	public FrameInfo(BufferedImage img) {
		autoOuterRect(img);
		autoInnerRect(img);
		roundOffMultipleX = 0;
		roundOffOffsetX = 0;
		roundOffMultipleY = 0;
		roundOffOffsetY = 0;
	}
	
	public FrameInfo clone() {
		return new FrameInfo(outerRect, innerRect, roundOffMultipleX, roundOffOffsetX, roundOffMultipleY, roundOffOffsetY);
	}
	
	private void autoOuterRect(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		int x = w/2;
		int y = h/2;
		int x1 = x, x2 = x, y1 = y, y2 = y;
		while (x1 > 0 && (img.getRGB(x1-1, y) & 0xFF000000) == 0) x1--;
		while (x2 < w-1 && (img.getRGB(x2+1, y) & 0xFF000000) == 0) x2++;
		while (y1 > 0 && (img.getRGB(x, y1-1) & 0xFF000000) == 0) y1--;
		while (y2 < h-1 && (img.getRGB(x, y2+1) & 0xFF000000) == 0) y2++;
		outerRect = new Rectangle(x1, y1, x2-x1+1, y2-y1+1);
	}
	
	private void autoInnerRect(BufferedImage img) {
		int x3 = outerRect.x, x4 = outerRect.x+outerRect.width-1;
		int y3 = outerRect.y, y4 = outerRect.y+outerRect.height-1;
		while (y3 < y4) {
			int[] rgb = new int[outerRect.width];
			img.getRGB(outerRect.x, y3, outerRect.width, 1, rgb, 0, outerRect.width);
			boolean isEmpty = true;
			for (int i : rgb) {
				if ((i & 0xFF000000) != 0) {
					isEmpty = false;
					break;
				}
			}
			if (isEmpty) break;
			else y3++;
		}
		while (y4 > y3) {
			int[] rgb = new int[outerRect.width];
			img.getRGB(outerRect.x, y4, outerRect.width, 1, rgb, 0, outerRect.width);
			boolean isEmpty = true;
			for (int i : rgb) {
				if ((i & 0xFF000000) != 0) {
					isEmpty = false;
					break;
				}
			}
			if (isEmpty) break;
			else y4--;
		}
		while (x3 < x4) {
			int[] rgb = new int[outerRect.height];
			img.getRGB(x3, outerRect.y, 1, outerRect.height, rgb, 0, 1);
			boolean isEmpty = true;
			for (int i : rgb) {
				if ((i & 0xFF000000) != 0) {
					isEmpty = false;
					break;
				}
			}
			if (isEmpty) break;
			else x3++;
		}
		while (x4 > x3) {
			int[] rgb = new int[outerRect.height];
			img.getRGB(x4, outerRect.y, 1, outerRect.height, rgb, 0, 1);
			boolean isEmpty = true;
			for (int i : rgb) {
				if ((i & 0xFF000000) != 0) {
					isEmpty = false;
					break;
				}
			}
			if (isEmpty) break;
			else x4--;
		}
		innerRect = new Rectangle(x3, y3, x4-x3+1, y4-y3+1);
	}
	
	public int hashCode() {
		return
			(outerRect == null ? 0 : outerRect.hashCode()) ^
			(innerRect == null ? 0 : innerRect.hashCode()) ^
			roundOffMultipleX ^ roundOffOffsetX ^ roundOffMultipleY ^ roundOffOffsetY;
	}
	
	public boolean equals(Object o) {
		if (o instanceof FrameInfo) {
			FrameInfo other = (FrameInfo)o;
			return equals(this.outerRect, other.outerRect) && equals(this.innerRect, other.innerRect)
				&& this.roundOffMultipleX == other.roundOffMultipleX
				&& this.roundOffOffsetX == other.roundOffOffsetX
				&& this.roundOffMultipleY == other.roundOffMultipleY
				&& this.roundOffOffsetY == other.roundOffOffsetY;
		} else {
			return false;
		}
	}
	
	private boolean equals(Rectangle a, Rectangle b) {
		if (a == null) {
			return (b == null) || (b.x == 0 && b.y == 0 && b.width == 0 && b.height == 0);
		}
		else if (b == null) {
			return (a == null) || (a.x == 0 && a.y == 0 && a.width == 0 && a.height == 0);
		}
		else {
			return a.x == b.x && a.y == b.y && a.width == b.width && a.height == b.height;
		}
	}
	
	public String toString() {
		return "com.kreative.paint.util.FrameInfo["+outerRect+","+innerRect+","+roundOffMultipleX+","+roundOffOffsetX+","+roundOffMultipleY+","+roundOffOffsetY+"]";
	}
}
