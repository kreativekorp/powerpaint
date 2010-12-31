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

package com.kreative.paint.draw;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;

public abstract class ControlPoint extends Point2D implements Cloneable {
	public abstract ControlPointType getType();
	public abstract void setType(ControlPointType t);
	
	public Shape getShape() {
		return getType().getShape((float)getX(), (float)getY());
	}
	
	public Shape getShape(float tx, float ty) {
		return getType().getShape((float)getX()+tx, (float)getY()+ty);
	}
	
	public boolean equals(Object o) {
		if (o instanceof ControlPoint) {
			ControlPoint other = (ControlPoint)o;
			return (this.getX() == other.getX() && this.getY() == other.getY() && this.getType() == other.getType());
		}
		return false;
	}
	
	public int hashCode() {
		int x = java.lang.Float.floatToRawIntBits((float)getX());
		int y = java.lang.Float.floatToRawIntBits((float)getY());
		int t = getType().getSerializedForm();
		return x ^ Integer.reverse(y) ^ t;
	}
	
	public static class Double extends ControlPoint {
		public double x, y;
		public ControlPointType type;
		
		public Double(ControlPointType t) {
			this.x = 0;
			this.y = 0;
			this.type = t;
		}
		
		public Double(Point p, ControlPointType t) {
			this.x = p.x;
			this.y = p.y;
			this.type = t;
		}
		
		public Double(Point2D p, ControlPointType t) {
			this.x = p.getX();
			this.y = p.getY();
			this.type = t;
		}
		
		public Double(double x, double y, ControlPointType t) {
			this.x = x;
			this.y = y;
			this.type = t;
		}
		
		public Double() {
			this.x = 0;
			this.y = 0;
			this.type = ControlPointType.GENERIC;
		}
		
		public Double(Point p) {
			this.x = p.x;
			this.y = p.y;
			this.type = ControlPointType.GENERIC;
		}
		
		public Double(Point2D p) {
			this.x = p.getX();
			this.y = p.getY();
			this.type = ControlPointType.GENERIC;
		}
		
		public Double(double x, double y) {
			this.x = x;
			this.y = y;
			this.type = ControlPointType.GENERIC;
		}
		
		public Double clone() {
			return new Double(x, y, type);
		}
		
		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public void setLocation(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public ControlPointType getType() {
			return type;
		}
		
		public void setType(ControlPointType t) {
			this.type = t;
		}
	}
	
	public static class Float extends ControlPoint {
		public float x, y;
		public ControlPointType type;
		
		public Float(ControlPointType t) {
			this.x = 0;
			this.y = 0;
			this.type = t;
		}
		
		public Float(Point p, ControlPointType t) {
			this.x = p.x;
			this.y = p.y;
			this.type = t;
		}
		
		public Float(Point2D p, ControlPointType t) {
			this.x = (float)p.getX();
			this.y = (float)p.getY();
			this.type = t;
		}
		
		public Float(float x, float y, ControlPointType t) {
			this.x = x;
			this.y = y;
			this.type = t;
		}
		
		public Float() {
			this.x = 0;
			this.y = 0;
			this.type = ControlPointType.GENERIC;
		}
		
		public Float(Point p) {
			this.x = p.x;
			this.y = p.y;
			this.type = ControlPointType.GENERIC;
		}
		
		public Float(Point2D p) {
			this.x = (float)p.getX();
			this.y = (float)p.getY();
			this.type = ControlPointType.GENERIC;
		}
		
		public Float(float x, float y) {
			this.x = x;
			this.y = y;
			this.type = ControlPointType.GENERIC;
		}
		
		public Float clone() {
			return new Float(x, y, type);
		}
		
		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public void setLocation(double x, double y) {
			this.x = (float)x;
			this.y = (float)y;
		}
		
		public ControlPointType getType() {
			return type;
		}
		
		public void setType(ControlPointType t) {
			this.type = t;
		}
	}
}
