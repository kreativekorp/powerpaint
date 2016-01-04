package com.kreative.paint.document.draw;

import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.document.undo.Recordable;

public abstract class DrawObject implements Cloneable, Recordable {
	protected PaintSettings ps;
	protected AffineTransform tx;
	protected boolean visible;
	protected boolean locked;
	protected boolean selected;
	protected History history;
	protected List<DrawObjectListener> listeners;
	
	protected DrawObject(PaintSettings ps) {
		this.ps = ps;
		this.tx = null;
		this.visible = true;
		this.locked = false;
		this.selected = false;
		this.history = null;
		this.listeners = new ArrayList<DrawObjectListener>();
	}
	
	protected DrawObject(DrawObject original) {
		this.ps = original.ps;
		this.tx = original.tx;
		this.visible = original.visible;
		this.locked = original.locked;
		this.selected = original.selected;
		this.history = null;
		this.listeners = new ArrayList<DrawObjectListener>();
	}
	
	@Override
	public History getHistory() {
		return history;
	}
	
	@Override
	public void setHistory(History history) {
		this.history = history;
	}
	
	public void addDrawObjectListener(DrawObjectListener l) {
		listeners.add(l);
	}
	
	public void removeDrawObjectListener(DrawObjectListener l) {
		listeners.remove(l);
	}
	
	public DrawObjectListener[] getDrawObjectListeners() {
		return listeners.toArray(new DrawObjectListener[listeners.size()]);
	}
	
	protected void notifyDrawObjectListeners(int id) {
		if (listeners.isEmpty()) return;
		DrawObjectEvent e = new DrawObjectEvent(id, this);
		switch (id) {
			case DrawObjectEvent.DRAW_OBJECT_PAINT_SETTINGS_CHANGED:
				for (DrawObjectListener l : listeners)
					l.drawObjectPaintSettingsChanged(e);
				break;
			case DrawObjectEvent.DRAW_OBJECT_TRANSFORM_CHANGED:
				for (DrawObjectListener l : listeners)
					l.drawObjectTransformChanged(e);
				break;
			case DrawObjectEvent.DRAW_OBJECT_VISIBLE_CHANGED:
				for (DrawObjectListener l : listeners)
					l.drawObjectVisibleChanged(e);
				break;
			case DrawObjectEvent.DRAW_OBJECT_LOCKED_CHANGED:
				for (DrawObjectListener l : listeners)
					l.drawObjectLockedChanged(e);
				break;
			case DrawObjectEvent.DRAW_OBJECT_SELECTED_CHANGED:
				for (DrawObjectListener l : listeners)
					l.drawObjectSelectedChanged(e);
				break;
			case DrawObjectEvent.DRAW_OBJECT_CONTROL_POINT_CHANGED:
				for (DrawObjectListener l : listeners)
					l.drawObjectControlPointChanged(e);
				break;
			case DrawObjectEvent.DRAW_OBJECT_LOCATION_CHANGED:
				for (DrawObjectListener l : listeners)
					l.drawObjectLocationChanged(e);
				break;
			default:
				for (DrawObjectListener l : listeners)
					l.drawObjectImplementationPropertyChanged(e);
				break;
		}
	}
	
	public PaintSettings getPaintSettings() { return ps; }
	
	private static class PaintSettingsAtom implements Atom {
		private DrawObject d;
		private PaintSettings oldPs;
		private PaintSettings newPs;
		public PaintSettingsAtom(DrawObject d, PaintSettings newPs) {
			this.d = d;
			this.oldPs = d.ps;
			this.newPs = newPs;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof PaintSettingsAtom)
			    && (((PaintSettingsAtom)prev).d == this.d);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldPs = ((PaintSettingsAtom)prev).oldPs;
			return this;
		}
		@Override
		public void undo() {
			d.ps = oldPs;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_PAINT_SETTINGS_CHANGED);
		}
		@Override
		public void redo() {
			d.ps = newPs;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_PAINT_SETTINGS_CHANGED);
		}
	}
	
	public void setPaintSettings(PaintSettings ps) {
		if (equals(this.ps, ps)) return;
		if (history != null) history.add(new PaintSettingsAtom(this, ps));
		this.ps = ps;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_PAINT_SETTINGS_CHANGED);
	}
	
	public AffineTransform getTransform() { return tx; }
	
	private static class TransformAtom implements Atom {
		private DrawObject d;
		private AffineTransform oldTx;
		private AffineTransform newTx;
		public TransformAtom(DrawObject d, AffineTransform newTx) {
			this.d = d;
			this.oldTx = d.tx;
			this.newTx = newTx;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof TransformAtom)
			    && (((TransformAtom)prev).d == this.d);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldTx = ((TransformAtom)prev).oldTx;
			return this;
		}
		@Override
		public void undo() {
			d.tx = oldTx;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_TRANSFORM_CHANGED);
		}
		@Override
		public void redo() {
			d.tx = newTx;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_TRANSFORM_CHANGED);
		}
	}
	
	public void setTransform(AffineTransform tx) {
		if (tx != null && tx.isIdentity()) tx = null;
		if (equals(this.tx, tx)) return;
		if (history != null) history.add(new TransformAtom(this, tx));
		this.tx = tx;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_TRANSFORM_CHANGED);
	}
	
	public boolean isVisible() { return visible; }
	public boolean isLocked() { return locked; }
	public boolean isSelected() { return selected; }
	
	private static class VisibleAtom implements Atom {
		private DrawObject d;
		private boolean oldVisible;
		private boolean newVisible;
		public VisibleAtom(DrawObject d, boolean newVisible) {
			this.d = d;
			this.oldVisible = d.visible;
			this.newVisible = newVisible;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof VisibleAtom)
			    && (((VisibleAtom)prev).d == this.d);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldVisible = ((VisibleAtom)prev).oldVisible;
			return this;
		}
		@Override
		public void undo() {
			d.visible = oldVisible;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_VISIBLE_CHANGED);
		}
		@Override
		public void redo() {
			d.visible = newVisible;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_VISIBLE_CHANGED);
		}
	}
	
	private static class LockedAtom implements Atom {
		private DrawObject d;
		private boolean oldLocked;
		private boolean newLocked;
		public LockedAtom(DrawObject d, boolean newLocked) {
			this.d = d;
			this.oldLocked = d.locked;
			this.newLocked = newLocked;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof LockedAtom)
			    && (((LockedAtom)prev).d == this.d);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldLocked = ((LockedAtom)prev).oldLocked;
			return this;
		}
		@Override
		public void undo() {
			d.locked = oldLocked;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_LOCKED_CHANGED);
		}
		@Override
		public void redo() {
			d.locked = newLocked;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_LOCKED_CHANGED);
		}
	}
	
	private static class SelectedAtom implements Atom {
		private DrawObject d;
		private boolean oldSelected;
		private boolean newSelected;
		public SelectedAtom(DrawObject d, boolean newSelected) {
			this.d = d;
			this.oldSelected = d.selected;
			this.newSelected = newSelected;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof SelectedAtom)
			    && (((SelectedAtom)prev).d == this.d);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldSelected = ((SelectedAtom)prev).oldSelected;
			return this;
		}
		@Override
		public void undo() {
			d.selected = oldSelected;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_SELECTED_CHANGED);
		}
		@Override
		public void redo() {
			d.selected = newSelected;
			d.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_SELECTED_CHANGED);
		}
	}
	
	public void setVisible(boolean visible) {
		if (this.visible == visible) return;
		if (history != null) history.add(new VisibleAtom(this, visible));
		this.visible = visible;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_VISIBLE_CHANGED);
	}
	
	public void setLocked(boolean locked) {
		if (this.locked == locked) return;
		if (history != null) history.add(new LockedAtom(this, locked));
		this.locked = locked;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_LOCKED_CHANGED);
	}
	
	public void setSelected(boolean selected) {
		if (this.selected == selected) return;
		if (history != null) history.add(new SelectedAtom(this, selected));
		this.selected = selected;
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_SELECTED_CHANGED);
	}
	
	/**
	 * Returns a clone of this DrawObject. The clone may be a shallow copy
	 * or a deep copy, but this method should guarantee that mutations to
	 * the cloned DrawObject will not affect the original DrawObject.
	 * @return a clone of this DrawObject
	 */
	@Override public abstract DrawObject clone();
	
	/**
	 * Returns the boundary of this DrawObject, given as if no
	 * transformation has been applied to the DrawObject. This
	 * will be used mainly for adjusting transformations.
	 * @return the boundary of this DrawObject
	 */
	protected abstract Shape getBoundaryImpl();
	
	public Rectangle getPreTxBounds() {
		Shape ba = getBoundaryImpl();
		if (ba == null) return null;
		return ba.getBounds();
	}
	
	public Rectangle2D getPreTxBounds2D() {
		Shape ba = getBoundaryImpl();
		if (ba == null) return null;
		return ba.getBounds2D();
	}
	
	public Rectangle getBounds() {
		Shape ba = getBoundaryImpl();
		if (ba == null) return null;
		if (tx == null) return ba.getBounds();
		return tx.createTransformedShape(ba).getBounds();
	}
	
	public Rectangle2D getBounds2D() {
		Shape ba = getBoundaryImpl();
		if (ba == null) return null;
		if (tx == null) return ba.getBounds2D();
		return tx.createTransformedShape(ba).getBounds2D();
	}
	
	/**
	 * Returns the area of a graphics context that will be painted on
	 * by this DrawObject, given as if no transformation has been
	 * applied to the DrawObject. This will be used mainly for hit
	 * detection. It is acceptable for the returned area to be approximate.
	 * @return the area of a graphics context that will be painted on
	 */
	protected Shape getHitAreaImpl() { return null; }
	
	/**
	 * Returns the area of a graphics context that will be painted on
	 * by this DrawObject, after all transformations are applied.
	 * This will be used mainly for hit detection. It is acceptable
	 * for the returned area to be approximate.
	 * @param tx the transformation applied to the DrawObject
	 * @return the area of a graphics context that will be painted on
	 */
	protected Shape getPostTxHitAreaImpl(AffineTransform tx) {
		Shape ha = getHitAreaImpl();
		if (tx == null || ha == null) return ha;
		return tx.createTransformedShape(ha);
	}
	
	/**
	 * Returns the area of a graphics context that will be painted on
	 * by this DrawObject, after all transformations are applied.
	 * Implementations of this class should override getHitAreaImpl()
	 * or getPostTxHitAreaImpl() instead.
	 * @return the area of a graphics context that will be painted on
	 */
	public Shape getHitArea() {
		return getPostTxHitAreaImpl(tx);
	}
	
	/**
	 * Returns the area of a graphics context that will be painted on
	 * by this DrawObject, after all transformations are applied.
	 * Implementations of this class should override getHitAreaImpl()
	 * or getPostTxHitAreaImpl() instead.
	 * @param x an additional X coordinate translation to apply
	 * @param y an additional Y coordinate translation to apply
	 * @return the area of a graphics context that will be painted on
	 */
	public Shape getHitArea(double x, double y) {
		AffineTransform htx = new AffineTransform();
		htx.translate(x, y);
		if (this.tx != null) htx.concatenate(this.tx);
		return getPostTxHitAreaImpl(htx);
	}
	
	/**
	 * Returns the area of a graphics context that will be painted on
	 * by this DrawObject, after all transformations are applied.
	 * Implementations of this class should override getHitAreaImpl()
	 * or getPostTxHitAreaImpl() instead.
	 * @param tx an additional transformation to apply
	 * @return the area of a graphics context that will be painted on
	 */
	public Shape getHitArea(AffineTransform tx) {
		AffineTransform htx = new AffineTransform();
		if (tx != null) htx.concatenate(tx);
		if (this.tx != null) htx.concatenate(this.tx);
		return getPostTxHitAreaImpl(htx);
	}
	
	public boolean contains(double x, double y) {
		Shape ha = getHitArea();
		if (ha == null) return false;
		return ha.contains(x, y);
	}
	
	public boolean contains(double x, double y, double width, double height) {
		Shape ha = getHitArea();
		if (ha == null) return false;
		return ha.contains(x, y, width, height);
	}
	
	public boolean contains(Point2D p) {
		Shape ha = getHitArea();
		if (ha == null) return false;
		return ha.contains(p);
	}
	
	public boolean contains(Rectangle2D r) {
		Shape ha = getHitArea();
		if (ha == null) return false;
		return ha.contains(r);
	}
	
	public boolean intersects(double x, double y, double width, double height) {
		Shape ha = getHitArea();
		if (ha == null) return false;
		return ha.intersects(x, y, width, height);
	}
	
	public boolean intersects(Rectangle2D r) {
		Shape ha = getHitArea();
		if (ha == null) return false;
		return ha.intersects(r);
	}
	
	/**
	 * Returns an opaque object representing all variables that can be
	 * affected by modifying control points or the location of this
	 * DrawObject. An object returned from this method may be passed to
	 * setControlState() at a later time, for example when the Undo or
	 * Redo command is invoked. Every DrawObject subclass which overrides
	 * setControlPointImpl() or setLocationImpl() should also override
	 * this method.
	 * @return an opaque object to be passed to setControlState()
	 */
	protected abstract Object getControlState();
	
	/**
	 * Restores all variables affected by modifying control points or
	 * the location of this DrawObject from an opaque object produced by
	 * getControlState(). An object returned from getControlState() may be
	 * passed to this method at a later time, for example when the Undo or
	 * Redo command is invoked. Every DrawObject subclass which overrides
	 * setControlPointImpl() or setLocationImpl() should also override
	 * this method.
	 * @param state an opaque object returned from getControlState()
	 */
	protected abstract void setControlState(Object state);
	
	private static class ControlStateAtom implements Atom {
		private DrawObject d;
		private Object oldState;
		private Object newState;
		private int eventID;
		public ControlStateAtom(DrawObject d, Object oldState, Object newState, int eventID) {
			this.d = d;
			this.oldState = oldState;
			this.newState = newState;
			this.eventID = eventID;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof ControlStateAtom)
			    && (((ControlStateAtom)prev).d == this.d)
			    && (((ControlStateAtom)prev).eventID == this.eventID);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldState = ((ControlStateAtom)prev).oldState;
			return this;
		}
		@Override
		public void redo() {
			d.setControlState(newState);
			d.notifyDrawObjectListeners(eventID);
		}
		@Override
		public void undo() {
			d.setControlState(oldState);
			d.notifyDrawObjectListeners(eventID);
		}
	}
	
	/**
	 * Returns the number of control points on this DrawObject.
	 * A control point can be any variable that affects the rendering
	 * of a DrawObject that can be represented by a geometric point.
	 * @return the number of control points
	 */
	public abstract int getControlPointCount();
	
	/**
	 * Returns a control point on this DrawObject. The position of
	 * the control point is given as if no transformation has been
	 * applied to the DrawObject. A control point can be any
	 * variable that affects the rendering of a DrawObject
	 * that can be represented by a geometric point.
	 * @param i the index of a control point
	 * @return the control point
	 */
	protected abstract ControlPoint getControlPointImpl(int i);
	
	/**
	 * Returns a list of control points on this DrawObject. The
	 * position of each control point is given as if no transformation
	 * has been applied to the DrawObject. A control point can be any
	 * variable that affects the rendering of a DrawObject that
	 * can be represented by a geometric point.
	 * @return a list of control points
	 */
	protected abstract List<ControlPoint> getControlPointsImpl();
	
	/**
	 * Returns a set of lines that should be drawn when control points are
	 * drawn. For example, the line between the endpoint of a Bezier curve
	 * and its control point. The endpoints of each line are given as if
	 * no transformation has been applied to the DrawObject.
	 * @return a set of lines
	 */
	protected abstract Collection<Line2D> getControlLinesImpl();
	
	/**
	 * Sets the position of a control point on this DrawObject.
	 * The new position is given as if no transformation has been
	 * applied to the DrawObject. In most cases, this method should
	 * return the index which was passed in.
	 * @param i the index of a control point
	 * @param x the new X coordinate of the control point
	 * @param y the new Y coordinate of the control point
	 * @return the index of the control point
	 */
	protected abstract int setControlPointImpl(int i, double x, double y);
	
	/**
	 * Returns a control point on this DrawObject,
	 * transformed according to the transformation applied
	 * to this DrawObject. Implementations of this class
	 * should override getControlPointImpl() instead.
	 * @param i the index of a control point
	 * @return the control point
	 */
	public ControlPoint getControlPoint(int i) {
		ControlPoint cp = getControlPointImpl(i);
		if (tx == null || cp == null) return cp;
		tx.transform(cp, cp);
		return cp;
	}
	
	/**
	 * Returns a list of control points on this DrawObject,
	 * transformed according to the transformation applied
	 * to this DrawObject. Implementations of this class
	 * should override getControlPointsImpl() instead.
	 * @return a list of control points
	 */
	public List<ControlPoint> getControlPoints() {
		List<ControlPoint> cpts = getControlPointsImpl();
		if (cpts == null) return Collections.emptyList();
		if (tx == null || cpts.isEmpty()) return cpts;
		List<ControlPoint> txCpts = new ArrayList<ControlPoint>();
		for (ControlPoint cp : cpts) {
			tx.transform(cp, cp);
			txCpts.add(cp);
		}
		return txCpts;
	}
	
	/**
	 * Returns a set of lines that should be drawn when control points
	 * are drawn, transformed according to the transformation applied
	 * to this DrawObject. Implementations of this class
	 * should override getControlLinesImpl() instead.
	 * @return a set of lines
	 */
	public Collection<Line2D> getControlLines() {
		Collection<Line2D> lines = getControlLinesImpl();
		if (lines == null) return Collections.emptySet();
		if (tx == null || lines.isEmpty()) return lines;
		Collection<Line2D> txLines = new HashSet<Line2D>();
		for (Line2D line : lines) {
			Point2D p1 = line.getP1();
			Point2D p2 = line.getP2();
			tx.transform(p1, p1);
			tx.transform(p2, p2);
			txLines.add(new Line2D.Double(p1, p2));
		}
		return txLines;
	}
	
	/**
	 * Sets the position of a control point on this DrawObject.
	 * The new position is given transformed according to the
	 * transformation applied to this DrawObject. Implementations
	 * of this class should override setControlPointImpl() instead.
	 * @param i the index of a control point
	 * @param x the new X coordinate of the control point
	 * @param y the new Y coordinate of the control point
	 * @return the index of the control point
	 */
	public int setControlPoint(int i, double x, double y) {
		if (tx != null) {
			try {
				Point2D p = new Point2D.Double(x, y);
				tx.inverseTransform(p, p);
				x = p.getX(); y = p.getY();
			} catch (Exception e) {
				return i;
			}
		}
		if (history != null) {
			Object oldState = this.getControlState();
			i = this.setControlPointImpl(i, x, y);
			Object newState = this.getControlState();
			history.add(new ControlStateAtom(this, oldState, newState, DrawObjectEvent.DRAW_OBJECT_CONTROL_POINT_CHANGED));
		} else {
			i = this.setControlPointImpl(i, x, y);
		}
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_CONTROL_POINT_CHANGED);
		return i;
	}
	
	/**
	 * Sets the position of a control point on this DrawObject.
	 * The new position is given transformed according to the
	 * transformation applied to this DrawObject. Implementations
	 * of this class should override setControlPointImpl() instead.
	 * @param i the index of a control point
	 * @param p the new location of the control point
	 * @return the index of the control point
	 */
	public int setControlPoint(int i, Point2D p) {
		return setControlPoint(i, p.getX(), p.getY());
	}
	
	/**
	 * Returns the location of this DrawObject, given as if no
	 * transformation has been applied to the DrawObject.
	 * This may be a control point, but does not have to be.
	 * The location point is never actually shown; it is used
	 * only to translate the DrawObject.
	 * @return the location of this DrawObject
	 */
	protected abstract Point2D getLocationImpl();
	
	/**
	 * Sets the location of this DrawObject. The new location
	 * is given as if no transformation has been applied to the
	 * DrawObject. Setting the location of a DrawObject should
	 * result only in a translation; no other transformations or
	 * mutations should occur.
	 * @param x the new X coordinate of the location
	 * @param y the new Y coordinate of the location
	 */
	protected abstract void setLocationImpl(double x, double y);
	
	/**
	 * Returns the location of this DrawObject,
	 * transformed according to the transformation applied
	 * to this DrawObject. Implementations of this class
	 * should override getLocationImpl() instead.
	 * @return the location of this DrawObject
	 */
	public Point2D getLocation() {
		Point2D p = getLocationImpl();
		if (tx == null || p == null) return p;
		tx.transform(p, p);
		return p;
	}
	
	/**
	 * Sets the location of this DrawObject. The new location
	 * is given transformed according to the transformation
	 * applied to this DrawObject. Implementations of this
	 * class should override setLocationImpl() instead.
	 * @param x the new X coordinate of the location
	 * @param y the new Y coordinate of the location
	 */
	public void setLocation(double x, double y) {
		if (tx != null) {
			try {
				Point2D p = new Point2D.Double(x, y);
				tx.inverseTransform(p, p);
				x = p.getX(); y = p.getY();
			} catch (Exception e) {
				return;
			}
		}
		if (history != null) {
			Object oldState = this.getControlState();
			this.setLocationImpl(x, y);
			Object newState = this.getControlState();
			history.add(new ControlStateAtom(this, oldState, newState, DrawObjectEvent.DRAW_OBJECT_LOCATION_CHANGED));
		} else {
			this.setLocationImpl(x, y);
		}
		this.notifyDrawObjectListeners(DrawObjectEvent.DRAW_OBJECT_LOCATION_CHANGED);
	}
	
	/**
	 * Sets the location of this DrawObject. The new location
	 * is given transformed according to the transformation
	 * applied to this DrawObject. Implementations of this
	 * class should override setLocationImpl() instead.
	 * @param p the new location
	 */
	public void setLocation(Point2D p) {
		setLocation(p.getX(), p.getY());
	}
	
	/**
	 * Paints this DrawObject to a graphics context,
	 * as if no transformations have been applied
	 * to the DrawObject.
	 * @param g the graphics context
	 */
	protected void paintImpl(Graphics2D g) {}
	
	/**
	 * Paints this DrawObject to a graphics context,
	 * with all transformations applied to the DrawObject.
	 * @param g the graphics context
	 * @param tx the transformation applied to the DrawObject
	 */
	protected void preTxPaintImpl(Graphics2D g, AffineTransform tx) {
		if (tx != null) g.transform(tx);
		paintImpl(g);
	}
	
	/**
	 * Paints this DrawObject to a graphics context,
	 * with all transformations applied to the DrawObject.
	 * Implementations of this class should override
	 * paintImpl() or preTxPaintImpl() instead.
	 * @param g the graphics context
	 */
	public void paint(Graphics2D g) {
		Paint p = g.getPaint();
		Composite c = g.getComposite();
		Stroke s = g.getStroke();
		Font f = g.getFont();
		RenderingHints h = g.getRenderingHints();
		
		AffineTransform gtx = g.getTransform();
		preTxPaintImpl(g, tx);
		g.setTransform(gtx);
		
		g.setPaint(p);
		g.setComposite(c);
		g.setStroke(s);
		g.setFont(f);
		g.setRenderingHints(h);
	}
	
	/**
	 * Paints this DrawObject to a graphics context,
	 * with all transformations applied to the DrawObject.
	 * Implementations of this class should override
	 * paintImpl() or preTxPaintImpl() instead.
	 * @param g the graphics context
	 * @param x an additional X coordinate translation to apply
	 * @param y an additional Y coordinate translation to apply
	 */
	public void paint(Graphics2D g, int x, int y) {
		Paint p = g.getPaint();
		Composite c = g.getComposite();
		Stroke s = g.getStroke();
		Font f = g.getFont();
		RenderingHints h = g.getRenderingHints();
		
		AffineTransform gtx = g.getTransform();
		AffineTransform dtx = new AffineTransform();
		dtx.translate(x, y);
		if (this.tx != null) dtx.concatenate(this.tx);
		preTxPaintImpl(g, dtx);
		g.setTransform(gtx);
		
		g.setPaint(p);
		g.setComposite(c);
		g.setStroke(s);
		g.setFont(f);
		g.setRenderingHints(h);
	}
	
	/**
	 * Paints this DrawObject to a graphics context,
	 * with all transformations applied to the DrawObject.
	 * Implementations of this class should override
	 * paintImpl() or preTxPaintImpl() instead.
	 * @param g the graphics context
	 * @param tx an additional transformation to apply
	 */
	public void paint(Graphics2D g, AffineTransform tx) {
		Paint p = g.getPaint();
		Composite c = g.getComposite();
		Stroke s = g.getStroke();
		Font f = g.getFont();
		RenderingHints h = g.getRenderingHints();
		
		AffineTransform gtx = g.getTransform();
		AffineTransform dtx = new AffineTransform();
		if (tx != null) dtx.concatenate(tx);
		if (this.tx != null) dtx.concatenate(this.tx);
		preTxPaintImpl(g, dtx);
		g.setTransform(gtx);
		
		g.setPaint(p);
		g.setComposite(c);
		g.setStroke(s);
		g.setFont(f);
		g.setRenderingHints(h);
	}
	
	private static boolean equals(Object dis, Object dat) {
		if (dis == null) return (dat == null);
		if (dat == null) return (dis == null);
		return dis.equals(dat);
	}
}
