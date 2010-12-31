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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.*;

public class Rect extends Rectangle2D {
	public int top = 0;
	public int left = 0;
	public int bottom = 0;
	public int right = 0;
	
	public static Rect read(DataInputStream in) throws IOException {
		Rect r = new Rect();
		r.top = in.readShort();
		r.left = in.readShort();
		r.bottom = in.readShort();
		r.right = in.readShort();
		return r;
	}
	
	public Rect() {
		this.left = 0;
		this.top = 0;
		this.right = 0;
		this.bottom = 0;
	}
	
	public Rect(int x, int y, int width, int height) {
		this.left = x;
		this.top = y;
		this.right = x+width;
		this.bottom = y+height;
	}
	
	public Rect(Rectangle r) {
		this.left = r.x;
		this.top = r.y;
		this.right = r.x+r.width;
		this.bottom = r.y+r.height;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeShort(top);
		out.writeShort(left);
		out.writeShort(bottom);
		out.writeShort(right);
	}
	
	public Rectangle toRectangle() {
		return new Rectangle(left, top, right-left, bottom-top);
	}
	
	public String toString() {
		return left+","+top+","+right+","+bottom;
	}
	
	public Rectangle2D createIntersection(Rectangle2D r) {
		return toRectangle().createIntersection(r);
	}
	
	public Rectangle2D createUnion(Rectangle2D r) {
		return toRectangle().createUnion(r);
	}
	
	public int outcode(double x, double y) {
		return toRectangle().outcode(x, y);
	}
	
	public void setRect(double x, double y, double w, double h) {
		this.top = (int)y;
		this.left = (int)x;
		this.bottom = (int)(y+h);
		this.right = (int)(x+w);
	}
	
	public double getHeight() {
		return bottom-top;
	}
	
	public double getWidth() {
		return right-left;
	}
	
	public double getX() {
		return left;
	}
	
	public double getY() {
		return top;
	}
	
	public boolean isEmpty() {
		return (left == right || top == bottom);
	}
}
