/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class PathGraph {
	private static final long serialVersionUID = 1L;
	private static final int size = 10;
	private HashMap<ImmutablePoint, HashSet<ImmutablePoint>> graph;
	
	public PathGraph() {
		graph = new HashMap<ImmutablePoint, HashSet<ImmutablePoint>>();
	}

	public PathGraph(PathGraph pg) {
		graph = new HashMap<ImmutablePoint, HashSet<ImmutablePoint>>();
		for (Map.Entry<ImmutablePoint, HashSet<ImmutablePoint>> e : pg.graph.entrySet()) {
			HashSet<ImmutablePoint> s = new HashSet<ImmutablePoint>();
			s.addAll(e.getValue());
			graph.put(e.getKey(), s);
		}
	}
	
	public void plot(int x, int y) {
		ImmutablePoint bl = new ImmutablePoint(x*size, y*size);
		ImmutablePoint tl = new ImmutablePoint(x*size, y*size+size);
		ImmutablePoint tr = new ImmutablePoint(x*size+size, y*size+size);
		ImmutablePoint br = new ImmutablePoint(x*size+size, y*size);
		add(bl, tl); add(tl, tr); add(tr, br); add(br, bl);
	}
	
	public GeneralPath makePath() {
		removeOverlap();
		simplifyPaths();
		GeneralPath p = new GeneralPath();
		ImmutablePoint[][] contours = getContours();
		for (ImmutablePoint[] contour : contours) {
			p.moveTo((float)contour[0].x/(float)size, (float)contour[0].y/(float)size);
			for (int i = 1; i < contour.length; i++) {
				p.lineTo((float)contour[i].x/(float)size, (float)contour[i].y/(float)size);
			}
			p.closePath();
		}
		return p;
	}
	
	private void add(ImmutablePoint src, ImmutablePoint dst) {
		if (graph.containsKey(src)) {
			graph.get(src).add(dst);
		} else {
			HashSet<ImmutablePoint> s = new HashSet<ImmutablePoint>();
			s.add(dst);
			graph.put(src, s);
		}
	}
	
	private boolean contains(ImmutablePoint src, ImmutablePoint dst) {
		return (graph.containsKey(src) && graph.get(src).contains(dst));
	}
	
	private boolean isEmpty() {
		if (!graph.isEmpty()) {
			for (HashSet<ImmutablePoint> s : graph.values()) {
				if (!s.isEmpty()) return false;
			}
		}
		return true;
	}
	
	private void remove(ImmutablePoint src, ImmutablePoint dst) {
		if (graph.containsKey(src)) {
			graph.get(src).remove(dst);
			if (graph.get(src).size() < 1) {
				graph.remove(src);
			}
		}
	}
	
	private ImmutablePoint[] getAllPoints() {
		HashSet<ImmutablePoint> s = new HashSet<ImmutablePoint>();
		s.addAll(graph.keySet());
		for (HashSet<ImmutablePoint> t : graph.values()) {
			s.addAll(t);
		}
		return s.toArray(new ImmutablePoint[0]);
	}
	
	private ImmutablePoint[] getAdjPoints(ImmutablePoint src) {
		if (graph.containsKey(src)) {
			return graph.get(src).toArray(new ImmutablePoint[0]);
		} else {
			return new ImmutablePoint[0];
		}
	}
	
	private ImmutablePoint[][] getContours() {
		Vector<Vector<ImmutablePoint>> v = new Vector<Vector<ImmutablePoint>>();
		PathGraph tmp = new PathGraph(this);
		while (!tmp.isEmpty()) {
			ImmutablePoint[] firsts = tmp.getAllPoints();
			for (ImmutablePoint first : firsts) {
				if (tmp.graph.containsKey(first) && !tmp.graph.get(first).isEmpty()) {
					Vector<ImmutablePoint> w = new Vector<ImmutablePoint>();
					int lastdx = 0, lastdy = 0;
					ImmutablePoint p = first;
					w.add(p);
					while (true) {
						ImmutablePoint[] candidates = tmp.getAdjPoints(p);
						if (candidates == null || candidates.length < 1) break;
						ImmutablePoint q = null;
						if (lastdx == 0 && lastdy == 0) {
							q = candidates[0];
						} else {
							// looking for point going counterclockwise
							for (ImmutablePoint candidate : candidates) {
								int dy = signum(candidate.y - p.y);
								int dx = signum(candidate.x - p.x);
								if (dy == -lastdx && dx == lastdy && !candidate.equals(p)) {
									q = candidate; break;
								}
							}
							// looking for point going straight
							if (q == null) {
								for (ImmutablePoint candidate : candidates) {
									int dy = signum(candidate.y - p.y);
									int dx = signum(candidate.x - p.x);
									if (dy == lastdy && dx == lastdx && !candidate.equals(p)) {
										q = candidate; break;
									}
								}
							}
							// looking for point going clockwise
							if (q == null) {
								for (ImmutablePoint candidate : candidates) {
									int dy = signum(candidate.y - p.y);
									int dx = signum(candidate.x - p.x);
									if (dy == lastdx && dx == -lastdy && !candidate.equals(p)) {
										q = candidate; break;
									}
								}
							}
							if (q == null) {
								for (ImmutablePoint candidate : candidates) {
									if (!candidate.equals(p)) {
										q = candidate; break;
									}
								}
							}
							if (q == null) break;
						}
						w.add(q);
						tmp.remove(p, q);
						if (first.equals(q)) break;
						else {
							lastdy = signum(q.y - p.y);
							lastdx = signum(q.x - p.x);
							p = q;
						}
					}
					if (!w.isEmpty()) v.add(w);
				}
			}
		}
		ImmutablePoint[][] a = new ImmutablePoint[v.size()][];
		for (int i = 0; i < v.size(); i++) {
			a[i] = v.get(i).toArray(new ImmutablePoint[0]);
		}
		return a;
	}
	
	private void removeOverlap() {
		Iterator<ImmutablePoint> i = graph.keySet().iterator();
		while (i.hasNext()) {
			ImmutablePoint src = i.next();
			Iterator<ImmutablePoint> j = graph.get(src).iterator();
			while (j.hasNext()) {
				ImmutablePoint dst = j.next();
				if (contains(dst, src)) {
					remove(src, dst);
					remove(dst, src);
					i = graph.keySet().iterator();
					break;
				}
			}
		}
	}
	
	private void simplifyPaths() {
		Iterator<ImmutablePoint> i = graph.keySet().iterator();
		while (i.hasNext()) {
			ImmutablePoint src = i.next();
			Iterator<ImmutablePoint> j = graph.get(src).iterator();
			while (j.hasNext()) {
				ImmutablePoint dst = j.next();
				int yd = signum(dst.y - src.y);
				int xd = signum(dst.x - src.x);
				ImmutablePoint newdst = dst;
				boolean foundEnd = false;
				while (!foundEnd) {
					foundEnd = true;
					ImmutablePoint[] candidates = getAdjPoints(newdst);
					for (ImmutablePoint candidate : candidates) {
						int cyd = signum(candidate.y - newdst.y);
						int cxd = signum(candidate.x - newdst.x);
						if (cyd == yd && cxd == xd) {
							remove(newdst, candidate);
							newdst = candidate;
							foundEnd = false;
						}
					}
				}
				if (newdst != dst) {
					remove(src, dst);
					add(src, newdst);
					i = graph.keySet().iterator();
					break;
				}
			}
		}
	}
	
	private static int signum(int v) {
		return (v < 0) ? -1 : (v > 0) ? 1 : 0;
	}
	
	private static class ImmutablePoint {
		private int x;
		private int y;
		public ImmutablePoint(int x, int y) {
			this.x = x; this.y = y;
		}
	}
}
