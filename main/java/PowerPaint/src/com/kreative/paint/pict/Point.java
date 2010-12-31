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

import java.awt.geom.Point2D;
import java.io.*;

public class Point extends Point2D {
	public int y;
	public int x;
	
	public static Point read(DataInputStream in) throws IOException {
		Point p = new Point();
		p.y = in.readShort();
		p.x = in.readShort();
		return p;
	}
	
	public Point() {
		this.x = 0;
		this.y = 0;
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(java.awt.Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeShort(y);
		out.writeShort(x);
	}
	
	public java.awt.Point toPoint() {
		return new java.awt.Point(x, y);
	}
	
	public String toString() {
		return x+","+y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setLocation(double x, double y) {
		this.y = (int)y;
		this.x = (int)x;
	}
}
