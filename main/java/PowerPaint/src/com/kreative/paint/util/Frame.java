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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class Frame {
	private BufferedImage frame;
	private FrameInfo finfo;
	
	public Frame(Image i, FrameInfo info) {
		this.frame = ImageUtils.toBufferedImage(i, false);
		this.finfo = info;
	}
	
	public Frame(Image i) {
		this.frame = ImageUtils.toBufferedImage(i, false);
		this.finfo = new FrameInfo(this.frame);
	}
	
	public Frame(byte[] imgdata) {
		this.frame = ImageUtils.toBufferedImage(Toolkit.getDefaultToolkit().createImage(imgdata), false);
		this.finfo = null;
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(imgdata));
			if (in.readLong() == 0x89504E470D0A1A0AL) {
				while (true) {
					try {
						int cl = in.readInt();
						int ct = in.readInt();
						byte[] cd = new byte[cl];
						in.read(cd);
						in.readInt();
						if (ct == 0x66724E46) {
							this.finfo = new FrameInfo(cd, this.frame);
						}
					} catch (EOFException eof) {
						break;
					}
				}
			}
			in.close();
		} catch (IOException ioe) {}
		if (this.finfo == null) {
			this.finfo = new FrameInfo(this.frame);
		}
	}
	
	public BufferedImage getRawImage() {
		return frame;
	}
	
	public FrameInfo getRawFrameInfo() {
		return finfo;
	}
	
	public void paintUnrestricted(Graphics g, int x1, int y1, int x2, int y2) {
		int sx = Math.min(x1, x2);
		int sy = Math.min(y1, y2);
		int x = Math.max(x1, x2);
		int y = Math.max(y1, y2);
		
		// left outer x
		int lox = sx-(finfo.outerRect.x);
		// top outer y
		int toy = sy-(finfo.outerRect.y);
		// left inner x
		int lix = sx+(finfo.innerRect.x-finfo.outerRect.x);
		// top inner y
		int tiy = sy+(finfo.innerRect.y-finfo.outerRect.y);
		// right inner x
		int rix = x-((finfo.outerRect.x+finfo.outerRect.width)-(finfo.innerRect.x+finfo.innerRect.width));
		// bottom inner y
		int biy = y-((finfo.outerRect.y+finfo.outerRect.height)-(finfo.innerRect.y+finfo.innerRect.height));
		// right outer x
		int rox = x+(frame.getWidth()-(finfo.outerRect.x+finfo.outerRect.width));
		// bottom outer y
		int boy = y+(frame.getHeight()-(finfo.outerRect.y+finfo.outerRect.height));
		
		// top/bottom edges
		for (int xx = lix; xx < rix; xx += finfo.innerRect.width) {
			g.drawImage(
					frame, xx, toy, xx+Math.min(finfo.innerRect.width, rix-xx), tiy,
					finfo.innerRect.x,
					0,
					finfo.innerRect.x+Math.min(finfo.innerRect.width, rix-xx),
					finfo.innerRect.y,
					null
			);
			g.drawImage(
					frame, xx, biy, xx+Math.min(finfo.innerRect.width, rix-xx), boy,
					finfo.innerRect.x,
					finfo.innerRect.y+finfo.innerRect.height,
					finfo.innerRect.x+Math.min(finfo.innerRect.width, rix-xx),
					frame.getHeight(),
					null
			);
		}
		
		// left/right edges
		for (int yy = tiy; yy < biy; yy += finfo.innerRect.height) {
			g.drawImage(
					frame, lox, yy, lix, yy+Math.min(finfo.innerRect.height, biy-yy),
					0,
					finfo.innerRect.y,
					finfo.innerRect.x,
					finfo.innerRect.y+Math.min(finfo.innerRect.height, biy-yy),
					null
			);
			g.drawImage(
					frame, rix, yy, rox, yy+Math.min(finfo.innerRect.height, biy-yy),
					finfo.innerRect.x+finfo.innerRect.width,
					finfo.innerRect.y,
					frame.getWidth(),
					finfo.innerRect.y+Math.min(finfo.innerRect.height, biy-yy),
					null
			);
		}
		
		// top left corner
		g.drawImage(
				frame, lox, toy, lix, tiy,
				0,
				0,
				finfo.innerRect.x,
				finfo.innerRect.y,
				null
		);
		// top right corner
		g.drawImage(
				frame, rix, toy, rox, tiy,
				finfo.innerRect.x+finfo.innerRect.width,
				0,
				frame.getWidth(),
				finfo.innerRect.y,
				null
		);
		// bottom left corner
		g.drawImage(
				frame, lox, biy, lix, boy,
				0,
				finfo.innerRect.y+finfo.innerRect.height,
				finfo.innerRect.x,
				frame.getHeight(),
				null
		);
		// bottom right corner
		g.drawImage(
				frame, rix, biy, rox, boy,
				finfo.innerRect.x+finfo.innerRect.width,
				finfo.innerRect.y+finfo.innerRect.height,
				frame.getWidth(),
				frame.getHeight(),
				null
		);
	}
	
	public void paintRestricted(Graphics g, int x1, int y1, int x2, int y2, boolean filled) {
		int w = x2-x1;
		int h = y2-y1;
		int mw = finfo.outerRect.width-finfo.innerRect.width;
		int mh = finfo.outerRect.height-finfo.innerRect.height;
		boolean nw = (w < 0);
		boolean nh = (h < 0);
		if (nw) w =- w;
		if (nh) h =- h;
		if (finfo.roundOffMultipleX > 0) {
			w -= finfo.roundOffOffsetX;
			w = finfo.roundOffMultipleX*(int)Math.round((double)w/(double)finfo.roundOffMultipleX);
			w += finfo.roundOffOffsetX;
		}
		if (finfo.roundOffMultipleY > 0) {
			h -= finfo.roundOffOffsetY;
			h = finfo.roundOffMultipleY*(int)Math.round((double)h/(double)finfo.roundOffMultipleY);
			h += finfo.roundOffOffsetY;
		}
		if (w <= mw) {
			w = mw+1;
		}
		if (h <= mh) {
			h = mh+1;
		}
		if (nw) w =- w;
		if (nh) h =- h;
		if (filled) g.fillRect((nw ? (x1+w) : x1), (nh ? (y1+h) : y1), Math.abs(w), Math.abs(h));
		paintUnrestricted(g, x1, y1, x1+w, y1+h);
	}
	
	public void paintWithin(Graphics g, int x, int y, int w, int h) {
		paintUnrestricted(
				g, x+finfo.outerRect.x, y+finfo.outerRect.y,
				x+w-(frame.getWidth()-finfo.outerRect.x-finfo.outerRect.width),
				y+h-(frame.getHeight()-finfo.outerRect.y-finfo.outerRect.height)
		);
	}
	
	public int hashCode() {
		return frame.hashCode() ^ finfo.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Frame) {
			Frame other = (Frame)o;
			return (this.frame.equals(other.frame) && this.finfo.equals(other.finfo));
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "com.kreative.paint.util.Frame["+frame.toString()+","+finfo.toString()+"]";
	}
}
