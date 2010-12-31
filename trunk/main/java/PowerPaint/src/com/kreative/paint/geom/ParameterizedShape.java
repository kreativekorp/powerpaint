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

package com.kreative.paint.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParameterizedShape implements Shape, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	private ParameterizedPath path;
	private Map<String,Point2D> aparams;
	private transient GeneralPath cache;
	
	public ParameterizedShape(ParameterizedPath path) {
		this.path = path;
		this.aparams = new HashMap<String,Point2D>();
		this.cache = null;
	}
	
	public ParameterizedShape clone() {
		ParameterizedShape clone = new ParameterizedShape(path.clone());
		for (Map.Entry<String,Point2D> e : aparams.entrySet()) {
			clone.aparams.put(e.getKey(), new Point2D.Double(e.getValue().getX(), e.getValue().getY()));
		}
		return clone;
	}
	
	public ParameterizedPath getParameterizedPath() {
		return path;
	}
	
	public int getParameterCount() {
		return path.getParameterCount();
	}
	
	public Collection<String> getParameterNames() {
		return path.getParameterNames();
	}
	
	public Collection<ParameterPoint> getParameterPoints() {
		return path.getParameterPoints();
	}
	
	public int getParameterIndex(String name) {
		return path.getParameterIndex(name);
	}
	
	public int getParameterIndex(ParameterPoint pp) {
		return path.getParameterIndex(pp);
	}
	
	public String getParameterName(int index) {
		return path.getParameterName(index);
	}
	
	public ParameterPoint getParameterPoint(int index) {
		return path.getParameterPoint(index);
	}
	
	public ParameterPoint getParameterPoint(String name) {
		return path.getParameterPoint(name);
	}
	
	public Set<Map.Entry<String,Point2D>> getParameterValues() {
		return aparams.entrySet();
	}
	
	public Point2D getParameterValue(int index) {
		return path.getParameterPoint(index).getLocation(aparams);
	}
	
	public Point2D getParameterValue(String name) {
		return path.getParameterPoint(name).getLocation(aparams);
	}
	
	public Point2D getParameterValue(ParameterPoint pp) {
		return pp.getLocation(aparams);
	}
	
	public void setParameterValue(int index, Point2D location) {
		path.getParameterPoint(index).setLocation(aparams, location.getX(), location.getY());
		cache = null;
	}
	
	public void setParameterValue(int index, double x, double y) {
		path.getParameterPoint(index).setLocation(aparams, x, y);
		cache = null;
	}
	
	public void setParameterValue(String name, Point2D location) {
		path.getParameterPoint(name).setLocation(aparams, location.getX(), location.getY());
		cache = null;
	}
	
	public void setParameterValue(String name, double x, double y) {
		path.getParameterPoint(name).setLocation(aparams, x, y);
		cache = null;
	}
	
	public void setParameterValue(ParameterPoint pp, Point2D location) {
		pp.setLocation(aparams, location.getX(), location.getY());
		cache = null;
	}
	
	public void setParameterValue(ParameterPoint pp, double x, double y) {
		pp.setLocation(aparams, x, y);
		cache = null;
	}
	
	public GeneralPath getPath() {
		return (cache == null) ? (cache = path.toShape(aparams)) : cache;
	}
	
	public boolean contains(Point2D p) {
		return getPath().contains(p);
	}
	
	public boolean contains(Rectangle2D r) {
		return getPath().contains(r);
	}
	
	public boolean contains(double x, double y) {
		return getPath().contains(x, y);
	}
	
	public boolean contains(double x, double y, double w, double h) {
		return getPath().contains(x, y, w, h);
	}
	
	public Rectangle getBounds() {
		return getPath().getBounds();
	}
	
	public Rectangle2D getBounds2D() {
		return getPath().getBounds2D();
	}
	
	public PathIterator getPathIterator(AffineTransform at) {
		return getPath().getPathIterator(at);
	}
	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getPath().getPathIterator(at, flatness);
	}
	
	public boolean intersects(Rectangle2D r) {
		return getPath().intersects(r);
	}
	
	public boolean intersects(double x, double y, double w, double h) {
		return getPath().intersects(x, y, w, h);
	}
}
