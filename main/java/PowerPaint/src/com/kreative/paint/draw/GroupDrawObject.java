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

package com.kreative.paint.draw;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import com.kreative.paint.PaintSettings;
import com.kreative.paint.io.SerializationManager;
import com.kreative.paint.undo.History;
import com.kreative.paint.undo.Atom;

// TODO make transforms on the group independent of transforms on the grouped objects

// GroupDrawObject is the only DrawObject to implement Serializable.
// This is to enable transfer of DrawObjects across VMs (Java needs a class to be Serializable for this).
// We override Java's usual serialization process with PowerPaint's, since all other DrawObjects
// are only serializable through PowerPaint's serialization process, and not through Java's.

public class GroupDrawObject implements DrawObject, List<DrawObject>, Serializable {
	private static final long serialVersionUID = 1L;
	private History history;
	private Composite drawComposite;
	private Paint drawPaint;
	private Composite fillComposite;
	private Paint fillPaint;
	private Stroke stroke;
	private Font font;
	private int textAlignment;
	private boolean antiAliased;
	private AffineTransform transform;
	private boolean visible;
	private boolean locked;
	private boolean selected;
	private List<DrawObject> internalList;
	
	public GroupDrawObject() {
		this.history = null;
		this.drawComposite = AlphaComposite.SrcOver;
		this.drawPaint = Color.black;
		this.fillComposite = AlphaComposite.SrcOver;
		this.fillPaint = Color.black;
		this.stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		this.font = new Font("SansSerif", Font.PLAIN, 12);
		this.textAlignment = LEFT;
		this.antiAliased = false;
		this.transform = null;
		this.visible = true;
		this.locked = false;
		this.selected = false;
		this.internalList = new Vector<DrawObject>();
	}
	
	public GroupDrawObject(Collection<? extends DrawObject> c) {
		this.history = null;
		this.drawComposite = AlphaComposite.SrcOver;
		this.drawPaint = Color.black;
		this.fillComposite = AlphaComposite.SrcOver;
		this.fillPaint = Color.black;
		this.stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		this.font = new Font("SansSerif", Font.PLAIN, 12);
		this.textAlignment = LEFT;
		this.antiAliased = false;
		this.transform = null;
		this.visible = true;
		this.locked = false;
		this.selected = false;
		this.internalList = new Vector<DrawObject>();
		this.internalList.addAll(c);
	}
	
	public GroupDrawObject(PaintSettings ps) {
		this.history = null;
		this.drawComposite = ps.getDrawComposite();
		this.drawPaint = ps.getDrawPaint();
		this.fillComposite = ps.getFillComposite();
		this.fillPaint = ps.getFillPaint();
		this.stroke = ps.getStroke();
		this.font = ps.getFont();
		this.textAlignment = ps.getTextAlignment();
		this.antiAliased = ps.isAntiAliased();
		this.transform = null;
		this.visible = true;
		this.locked = false;
		this.selected = false;
		this.internalList = new Vector<DrawObject>();
	}
	
	public GroupDrawObject(Collection<? extends DrawObject> c, PaintSettings ps) {
		this.history = null;
		this.drawComposite = ps.getDrawComposite();
		this.drawPaint = ps.getDrawPaint();
		this.fillComposite = ps.getFillComposite();
		this.fillPaint = ps.getFillPaint();
		this.stroke = ps.getStroke();
		this.font = ps.getFont();
		this.textAlignment = ps.getTextAlignment();
		this.antiAliased = ps.isAntiAliased();
		this.transform = null;
		this.visible = true;
		this.locked = false;
		this.selected = false;
		this.internalList = new Vector<DrawObject>();
		this.internalList.addAll(c);
	}
	
	public GroupDrawObject clone() {
		GroupDrawObject o = new GroupDrawObject();
		o.setPaintSettings(getPaintSettings());
		if (getTransform() != null) o.setTransform((AffineTransform)getTransform().clone());
		for (DrawObject d : internalList) {
			o.add(d.clone());
		}
		return o;
	}
	
	public Graphics2D createDrawGraphics() {
		return new GroupDrawObjectGraphics(this);
	}

	private static class AddObjectAtom implements Atom {
		private GroupDrawObject gr;
		private int index;
		private DrawObject obj;
		public AddObjectAtom(GroupDrawObject gr, DrawObject o) {
			this.gr = gr;
			this.index = -1;
			this.obj = o;
		}
		public AddObjectAtom(GroupDrawObject gr, int index, DrawObject o) {
			this.gr = gr;
			this.index = index;
			this.obj = o;
		}
		public Atom buildUpon(Atom previousAtom) {
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return false;
		}
		public void redo() {
			if (index < 0) {
				gr.internalList.add(obj);
			} else {
				gr.internalList.add(index, obj);
			}
		}
		public void undo() {
			if (index < 0) {
				gr.internalList.remove(obj);
			} else {
				if (gr.internalList.get(index) == obj) gr.internalList.remove(index);
				else gr.internalList.remove(obj);
			}
		}
	}

	private static class ChangeObjectsAtom implements Atom {
		private GroupDrawObject gr;
		private Vector<DrawObject> oldObjects;
		private Vector<DrawObject> newObjects;
		public ChangeObjectsAtom(GroupDrawObject gr, List<DrawObject> oldobj, List<DrawObject> newobj) {
			this.gr = gr;
			this.oldObjects = new Vector<DrawObject>();
			this.oldObjects.addAll(oldobj);
			this.newObjects = new Vector<DrawObject>();
			this.newObjects.addAll(newobj);
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldObjects = ((ChangeObjectsAtom)previousAtom).oldObjects;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof ChangeObjectsAtom) && ((ChangeObjectsAtom)previousAtom).gr == this.gr;
		}
		public void redo() {
			gr.internalList.clear();
			gr.internalList.addAll(newObjects);
		}
		public void undo() {
			gr.internalList.clear();
			gr.internalList.addAll(oldObjects);
		}
	}

	private static class RemoveObjectAtom implements Atom {
		private GroupDrawObject gr;
		private int index;
		private Object o;
		public RemoveObjectAtom(GroupDrawObject gr, int index, Object o) {
			this.gr = gr;
			this.index = index;
			this.o = o;
		}
		public Atom buildUpon(Atom previousAtom) {
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return false;
		}
		public void redo() {
			if (gr.internalList.get(index) == o) gr.internalList.remove(index);
			else gr.internalList.remove(o);
		}
		public void undo() {
			gr.internalList.add(index, (DrawObject)o);
		}
	}

	private static class SetObjectAtom implements Atom {
		private GroupDrawObject gr;
		private int index;
		private DrawObject oldo;
		private DrawObject newo;
		public SetObjectAtom(GroupDrawObject gr, int index, DrawObject o) {
			this.gr = gr;
			this.index = index;
			this.oldo = gr.internalList.get(index);
			this.newo = o;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldo = ((SetObjectAtom)previousAtom).oldo;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof SetObjectAtom) && (((SetObjectAtom)previousAtom).gr == this.gr)
				&& (((SetObjectAtom)previousAtom).index == this.index);
		}
		public void redo() {
			gr.internalList.set(index, newo);
		}
		public void undo() {
			gr.internalList.set(index, oldo);
		}
	}
	
	private Vector<DrawObject> objectsChangTmp;
	
	private void objectsChanging() {
		if (history != null) {
			objectsChangTmp = new Vector<DrawObject>();
			objectsChangTmp.addAll(internalList);
		}
	}
	
	private void objectsChanged() {
		if (history != null) {
			history.add(new ChangeObjectsAtom(this, objectsChangTmp, this.internalList));
			for (DrawObject d : this.internalList) d.setHistory(history);
			objectsChangTmp = null;
		}
	}

	public boolean add(DrawObject o) {
		if (history != null) {
			history.add(new AddObjectAtom(this, o));
			o.setHistory(history);
		}
		return internalList.add(o);
	}

	public void add(int index, DrawObject element) {
		if (history != null) {
			history.add(new AddObjectAtom(this, index, element));
			element.setHistory(history);
		}
		internalList.add(index, element);
	}

	public boolean addAll(Collection<? extends DrawObject> c) {
		objectsChanging();
		boolean ret = internalList.addAll(c);
		objectsChanged();
		return ret;
	}

	public boolean addAll(int index, Collection<? extends DrawObject> c) {
		objectsChanging();
		boolean ret = internalList.addAll(index, c);
		objectsChanged();
		return ret;
	}

	public void clear() {
		objectsChanging();
		internalList.clear();
		objectsChanged();
	}

	public boolean contains(Object o) {
		return internalList.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return internalList.containsAll(c);
	}

	public DrawObject get(int index) {
		return internalList.get(index);
	}

	public int indexOf(Object o) {
		return internalList.indexOf(o);
	}

	public boolean isEmpty() {
		return internalList.isEmpty();
	}

	public Iterator<DrawObject> iterator() {
		return internalList.iterator();
	}

	public int lastIndexOf(Object o) {
		return internalList.lastIndexOf(o);
	}

	public ListIterator<DrawObject> listIterator() {
		return internalList.listIterator();
	}

	public ListIterator<DrawObject> listIterator(int index) {
		return internalList.listIterator(index);
	}

	public boolean remove(Object o) {
		if (history != null) history.add(new RemoveObjectAtom(this, internalList.indexOf(o), o));
		return internalList.remove(o);
	}

	public DrawObject remove(int index) {
		if (history != null) history.add(new RemoveObjectAtom(this, index, internalList.get(index)));
		return internalList.remove(index);
	}

	public boolean removeAll(Collection<?> c) {
		objectsChanging();
		boolean ret = internalList.removeAll(c);
		objectsChanged();
		return ret;
	}

	public boolean retainAll(Collection<?> c) {
		objectsChanging();
		boolean ret = internalList.retainAll(c);
		objectsChanged();
		return ret;
	}

	public DrawObject set(int index, DrawObject element) {
		if (history != null) history.add(new SetObjectAtom(this, index, element));
		return internalList.set(index, element);
	}

	public int size() {
		return internalList.size();
	}

	public List<DrawObject> subList(int fromIndex, int toIndex) {
		return internalList.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return internalList.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return internalList.toArray(a);
	}

	public Point2D getAnchor() {
		if (!internalList.isEmpty()) {
			return internalList.get(0).getAnchor();
		}
		return new Point2D.Float();
	}

	public ControlPoint getControlPoint(int i) {
		for (DrawObject o : internalList) {
			int c = o.getControlPointCount();
			if (i < c) return o.getControlPoint(i);
			else i -= c;
		}
		return null;
	}

	public int getControlPointCount() {
		int i = 0;
		for (DrawObject o : internalList) {
			i += o.getControlPointCount();
		}
		return i;
	}

	public ControlPoint[] getControlPoints() {
		List<ControlPoint> cp = new Vector<ControlPoint>();
		for (DrawObject o : internalList) {
			cp.addAll(Arrays.asList(o.getControlPoints()));
		}
		return cp.toArray(new ControlPoint[0]);
	}
	
	public double[][] getControlLines() {
		List<double[]> cl = new Vector<double[]>();
		for (DrawObject o : internalList) {
			cl.addAll(Arrays.asList(o.getControlLines()));
		}
		return cl.toArray(new double[0][]);
	}

	public Composite getDrawComposite() {
		return drawComposite;
	}

	public Paint getDrawPaint() {
		return drawPaint;
	}

	public Composite getFillComposite() {
		return fillComposite;
	}

	public Paint getFillPaint() {
		return fillPaint;
	}

	public Font getFont() {
		return font;
	}

	public PaintSettings getPaintSettings() {
		return new PaintSettings(
				drawComposite,
				drawPaint,
				fillComposite,
				fillPaint,
				stroke,
				font,
				textAlignment,
				antiAliased
		);
	}

	public Stroke getStroke() {
		return stroke;
	}

	public int getTextAlignment() {
		return textAlignment;
	}

	public AffineTransform getTransform() {
		return transform;
	}
	
	public boolean isAntiAliased() {
		return antiAliased;
	}

	public boolean isDrawn() {
		return (drawPaint != null);
	}

	public boolean isFilled() {
		return (fillPaint != null);
	}
	
	public boolean isLocked() {
		return locked;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setAnchor(Point2D p) {
		if (!internalList.isEmpty()) {
			Point2D a = internalList.get(0).getAnchor();
			double dx = p.getX() - a.getX();
			double dy = p.getY() - a.getY();
			for (DrawObject o : internalList) {
				if (!o.isLocked()) {
					Point2D oa = o.getAnchor();
					double ox = oa.getX()+dx;
					double oy = oa.getY()+dy;
					o.setAnchor(new Point2D.Double(ox,oy));
				}
			}
		}
	}

	public int setControlPoint(int i, Point2D p) {
		int n = 0;
		for (DrawObject o : internalList) {
			int c = o.getControlPointCount();
			if (i < c) {
				if (!o.isLocked()) {
					return n+o.setControlPoint(i,p);
				} else {
					return n+i;
				}
			}
			else {
				i -= c;
				n += c;
			}
		}
		return i;
	}
	
	private static class PaintSettingsAtom implements Atom {
		private GroupDrawObject d;
		private PaintSettings oldps;
		private PaintSettings newps;
		public PaintSettingsAtom(GroupDrawObject d, PaintSettings ps) {
			this.d = d;
			this.oldps = d.getPaintSettings();
			this.newps = ps;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldps = ((PaintSettingsAtom)previousAtom).oldps;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof PaintSettingsAtom) && ((PaintSettingsAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.drawComposite = newps.getDrawComposite();
			d.drawPaint = newps.getDrawPaint();
			d.fillComposite = newps.getFillComposite();
			d.fillPaint = newps.getFillPaint();
			d.stroke = newps.getStroke();
			d.font = newps.getFont();
			d.textAlignment = newps.getTextAlignment();
			d.antiAliased = newps.isAntiAliased();
		}
		public void undo() {
			d.drawComposite = oldps.getDrawComposite();
			d.drawPaint = oldps.getDrawPaint();
			d.fillComposite = oldps.getFillComposite();
			d.fillPaint = oldps.getFillPaint();
			d.stroke = oldps.getStroke();
			d.font = oldps.getFont();
			d.textAlignment = oldps.getTextAlignment();
			d.antiAliased = oldps.isAntiAliased();
		}
	}

	private static class DrawCompositeAtom implements Atom {
		private GroupDrawObject d;
		private Composite olddc;
		private Composite newdc;
		public DrawCompositeAtom(GroupDrawObject d, Composite dc) {
			this.d = d;
			this.olddc = d.drawComposite;
			this.newdc = dc;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.olddc = ((DrawCompositeAtom)previousAtom).olddc;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof DrawCompositeAtom) && ((DrawCompositeAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.drawComposite = newdc;
		}
		public void undo() {
			d.drawComposite = olddc;
		}
	}

	private static class DrawPaintAtom implements Atom {
		private GroupDrawObject d;
		private Paint olddp;
		private Paint newdp;
		public DrawPaintAtom(GroupDrawObject d, Paint dp) {
			this.d = d;
			this.olddp = d.drawPaint;
			this.newdp = dp;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.olddp = ((DrawPaintAtom)previousAtom).olddp;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof DrawPaintAtom) && ((DrawPaintAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.drawPaint = newdp;
		}
		public void undo() {
			d.drawPaint = olddp;
		}
	}

	private static class FillCompositeAtom implements Atom {
		private GroupDrawObject d;
		private Composite oldfc;
		private Composite newfc;
		public FillCompositeAtom(GroupDrawObject d, Composite fc) {
			this.d = d;
			this.oldfc = d.fillComposite;
			this.newfc = fc;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldfc = ((FillCompositeAtom)previousAtom).oldfc;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof FillCompositeAtom) && ((FillCompositeAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.fillComposite = newfc;
		}
		public void undo() {
			d.fillComposite = oldfc;
		}
	}

	private static class FillPaintAtom implements Atom {
		private GroupDrawObject d;
		private Paint oldfp;
		private Paint newfp;
		public FillPaintAtom(GroupDrawObject d, Paint fp) {
			this.d = d;
			this.oldfp = d.fillPaint;
			this.newfp = fp;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldfp = ((FillPaintAtom)previousAtom).oldfp;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof FillPaintAtom) && ((FillPaintAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.fillPaint = newfp;
		}
		public void undo() {
			d.fillPaint = oldfp;
		}
	}

	private static class StrokeAtom implements Atom {
		private GroupDrawObject d;
		private Stroke oldst;
		private Stroke newst;
		public StrokeAtom(GroupDrawObject d, Stroke st) {
			this.d = d;
			this.oldst = d.stroke;
			this.newst = st;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldst = ((StrokeAtom)previousAtom).oldst;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof StrokeAtom) && ((StrokeAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.stroke = newst;
		}
		public void undo() {
			d.stroke = oldst;
		}
	}

	private static class FontAtom implements Atom {
		private GroupDrawObject d;
		private Font oldf;
		private Font newf;
		public FontAtom(GroupDrawObject d, Font f) {
			this.d = d;
			this.oldf = d.font;
			this.newf = f;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldf = ((FontAtom)previousAtom).oldf;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof FontAtom) && ((FontAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.font = newf;
		}
		public void undo() {
			d.font = oldf;
		}
	}

	private static class TextAlignmentAtom implements Atom {
		private GroupDrawObject d;
		private int oldta;
		private int newta;
		public TextAlignmentAtom(GroupDrawObject d, int ta) {
			this.d = d;
			this.oldta = d.textAlignment;
			this.newta = ta;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldta = ((TextAlignmentAtom)previousAtom).oldta;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof TextAlignmentAtom) && ((TextAlignmentAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.textAlignment = newta;
		}
		public void undo() {
			d.textAlignment = oldta;
		}
	}
	
	private static class AntiAliasedAtom implements Atom {
		private GroupDrawObject d;
		private boolean oldaa;
		private boolean newaa;
		public AntiAliasedAtom(GroupDrawObject d, boolean aa) {
			this.d = d;
			this.oldaa = d.antiAliased;
			this.newaa = aa;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldaa = ((AntiAliasedAtom)previousAtom).oldaa;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof AntiAliasedAtom) && ((AntiAliasedAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.antiAliased = newaa;
		}
		public void undo() {
			d.antiAliased = oldaa;
		}
	}

	private static class TransformAtom implements Atom {
		private GroupDrawObject d;
		private AffineTransform oldtx;
		private AffineTransform newtx;
		public TransformAtom(GroupDrawObject d, AffineTransform tx) {
			this.d = d;
			this.oldtx = d.transform;
			this.newtx = tx;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldtx = ((TransformAtom)previousAtom).oldtx;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof TransformAtom) && ((TransformAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.transform = newtx;
		}
		public void undo() {
			d.transform = oldtx;
		}
	}
	
	private static class AttributeAtom implements Atom {
		private GroupDrawObject d;
		private boolean oldv, oldl, olds;
		private boolean newv, newl, news;
		public AttributeAtom(GroupDrawObject d, boolean vis, boolean lok, boolean sel) {
			this.d = d;
			this.oldv = d.visible;
			this.oldl = d.locked;
			this.olds = d.selected;
			this.newv = vis;
			this.newl = lok;
			this.news = sel;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldv = ((AttributeAtom)previousAtom).oldv;
			this.oldl = ((AttributeAtom)previousAtom).oldl;
			this.olds = ((AttributeAtom)previousAtom).olds;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof AttributeAtom) && ((AttributeAtom)previousAtom).d == this.d;
		}
		public void redo() {
			d.visible = newv;
			d.locked = newl;
			d.selected = news;
		}
		public void undo() {
			d.visible = oldv;
			d.locked = oldl;
			d.selected = olds;
		}
	}
	
	public void setAntiAliased(boolean antiAliased) {
		if (history != null) history.add(new AntiAliasedAtom(this, antiAliased));
		this.antiAliased = antiAliased;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setAntiAliased(antiAliased);
	}

	public void setDrawComposite(Composite drawComposite) {
		if (drawComposite == null) drawComposite = AlphaComposite.SrcOver;
		if (history != null) history.add(new DrawCompositeAtom(this, drawComposite));
		this.drawComposite = drawComposite;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setDrawComposite(drawComposite);
	}

	public void setDrawPaint(Paint drawPaint) {
		if (history != null) history.add(new DrawPaintAtom(this, drawPaint));
		this.drawPaint = drawPaint;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setDrawPaint(drawPaint);
	}

	public void setFillComposite(Composite fillComposite) {
		if (fillComposite == null) fillComposite = AlphaComposite.SrcOver;
		if (history != null) history.add(new FillCompositeAtom(this, fillComposite));
		this.fillComposite = fillComposite;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setFillComposite(fillComposite);
	}

	public void setFillPaint(Paint fillPaint) {
		if (history != null) history.add(new FillPaintAtom(this, fillPaint));
		this.fillPaint = fillPaint;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setFillPaint(fillPaint);
	}

	public void setFont(Font font) {
		if (font == null) font = new Font("SansSerif", Font.PLAIN, 12);
		if (history != null) history.add(new FontAtom(this, font));
		this.font = font;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setFont(font);
	}

	public void setPaintSettings(PaintSettings ps) {
		if (history != null) history.add(new PaintSettingsAtom(this, ps));
		this.drawComposite = ps.getDrawComposite();
		this.drawPaint = ps.getDrawPaint();
		this.fillComposite = ps.getFillComposite();
		this.fillPaint = ps.getFillPaint();
		this.stroke = ps.getStroke();
		this.font = ps.getFont();
		this.textAlignment = ps.getTextAlignment();
		this.antiAliased = ps.isAntiAliased();
		for (DrawObject o : internalList) if (!o.isLocked()) o.setPaintSettings(ps);
	}

	public void setVisible(boolean visible) {
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.visible = visible;
	}

	public void setLocked(boolean locked) {
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.locked = locked;
	}

	public void setSelected(boolean selected) {
		if (history != null) history.add(new AttributeAtom(this, visible, locked, selected));
		this.selected = selected;
		for (DrawObject o : internalList) o.setSelected(selected);
	}

	public void setStroke(Stroke stroke) {
		if (stroke == null) stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		if (history != null) history.add(new StrokeAtom(this, stroke));
		this.stroke = stroke;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setStroke(stroke);
	}

	public void setTextAlignment(int textAlignment) {
		if (history != null) history.add(new TextAlignmentAtom(this, textAlignment));
		this.textAlignment = textAlignment;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setTextAlignment(textAlignment);
	}

	public void setTransform(AffineTransform transform) {
		if (history != null) history.add(new TransformAtom(this, transform));
		this.transform = transform;
		for (DrawObject o : internalList) if (!o.isLocked()) o.setTransform(transform);
	}

	public void paint(Graphics2D g) {
		for (DrawObject o : internalList) if (o.isVisible()) o.paint(g);
	}

	public void paint(Graphics2D g, int tx, int ty) {
		for (DrawObject o : internalList) if (o.isVisible()) o.paint(g, tx, ty);
	}

	public boolean contains(Point2D p) {
		for (DrawObject o : internalList) {
			if (o.contains(p)) return true;
		}
		return false;
	}

	public boolean contains(Rectangle2D r) {
		for (DrawObject o : internalList) {
			if (o.contains(r)) return true;
		}
		return false;
	}

	public boolean contains(double x, double y) {
		for (DrawObject o : internalList) {
			if (o.contains(x, y)) return true;
		}
		return false;
	}

	public boolean contains(double x, double y, double w, double h) {
		for (DrawObject o : internalList) {
			if (o.contains(x, y, w, h)) return true;
		}
		return false;
	}

	public Rectangle getBounds() {
		int x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE;
		int x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE;
		for (DrawObject o : internalList) {
			Rectangle ob = o.getBounds();
			if (ob.x < x1) x1 = ob.x;
			if (ob.y < y1) y1 = ob.y;
			if (ob.x+ob.width > x2) x2 = ob.x+ob.width;
			if (ob.y+ob.height > y2) y2 = ob.y+ob.height;
		}
		if (x2 < x1 || y2 < y1) {
			return new Rectangle();
		} else {
			return new Rectangle(x1, y1, x2-x1, y2-y1);
		}
	}

	public Rectangle2D getBounds2D() {
		double x1 = Double.POSITIVE_INFINITY, y1 = Double.POSITIVE_INFINITY;
		double x2 = Double.NEGATIVE_INFINITY, y2 = Double.NEGATIVE_INFINITY;
		for (DrawObject o : internalList) {
			Rectangle2D ob = o.getBounds2D();
			if (ob.getMinX() < x1) x1 = ob.getMinX();
			if (ob.getMinY() < y1) y1 = ob.getMinY();
			if (ob.getMaxX() > x2) x2 = ob.getMaxX();
			if (ob.getMaxY() > y2) y2 = ob.getMaxY();
		}
		if (x2 < x1 || y2 < y1) {
			return new Rectangle2D.Double();
		} else {
			return new Rectangle2D.Double(x1, y1, x2-x1, y2-y1);
		}
	}

	public PathIterator getPathIterator(AffineTransform at) {
		GeneralPath p = new GeneralPath();
		for (DrawObject o : internalList) {
			p.append(o, false);
		}
		return p.getPathIterator(at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		GeneralPath p = new GeneralPath();
		for (DrawObject o : internalList) {
			p.append(o, false);
		}
		return p.getPathIterator(at, flatness);
	}

	public boolean intersects(Rectangle2D r) {
		for (DrawObject o : internalList) {
			if (o.intersects(r)) return true;
		}
		return false;
	}

	public boolean intersects(double x, double y, double w, double h) {
		for (DrawObject o : internalList) {
			if (o.intersects(x, y, w, h)) return true;
		}
		return false;
	}

	public History getHistory() {
		return history;
	}

	public void setHistory(History history) {
		this.history = history;
		for (DrawObject o : internalList) {
			o.setHistory(history);
		}
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		SerializationManager.open(dout);
		SerializationManager.writeObject(getPaintSettings(), dout);
		SerializationManager.writeObject(transform, dout);
		dout.writeBoolean(!visible);
		dout.writeBoolean(locked);
		dout.writeBoolean(selected);
		dout.writeByte(0);
		dout.writeInt(internalList.size());
		for (DrawObject d : internalList) {
			SerializationManager.writeObject(d, dout);
		}
		SerializationManager.close(dout);
		dout.close();
		bout.close();
		
		byte[] stuff = bout.toByteArray();
		out.writeInt(stuff.length);
		out.write(stuff);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int l = in.readInt();
		int rl = 0, rp = 0;
		byte[] stuff = new byte[l];
		while (rp < l && (rl = in.read(stuff, rp, l-rp)) >= 0) rp += rl;
		
		ByteArrayInputStream bin = new ByteArrayInputStream(stuff);
		DataInputStream din = new DataInputStream(bin);
		SerializationManager.open(din);
		internalList = new Vector<DrawObject>();
		setPaintSettings((PaintSettings)SerializationManager.readObject(din));
		transform = (AffineTransform)SerializationManager.readObject(din);
		visible = !din.readBoolean();
		locked = din.readBoolean();
		selected = din.readBoolean();
		din.readByte();
		int nobjs = din.readInt();
		for (int i = 0; i < nobjs; i++) {
			internalList.add((DrawObject)SerializationManager.readObject(din));
		}
		SerializationManager.close(din);
		din.close();
		bin.close();
	}
}
