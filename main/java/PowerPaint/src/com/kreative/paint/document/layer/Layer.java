package com.kreative.paint.document.layer;

import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.document.undo.Recordable;

public abstract class Layer implements Cloneable, Recordable {
	protected String name;
	protected boolean visible;
	protected boolean locked;
	protected boolean selected;
	protected int x;
	protected int y;
	protected History history;
	protected List<LayerListener> listeners;
	
	protected Layer(String name) {
		if (name == null) name = "";
		this.name = name;
		this.visible = true;
		this.locked = false;
		this.selected = false;
		this.x = 0;
		this.y = 0;
		this.history = null;
		this.listeners = new ArrayList<LayerListener>();
	}
	
	protected Layer(Layer original) {
		this.name = original.name;
		this.visible = original.visible;
		this.locked = original.locked;
		this.selected = original.selected;
		this.x = original.x;
		this.y = original.y;
		this.history = null;
		this.listeners = new ArrayList<LayerListener>();
	}
	
	@Override
	public History getHistory() {
		return history;
	}
	
	@Override
	public void setHistory(History history) {
		this.history = history;
	}
	
	public void addLayerListener(LayerListener l) {
		listeners.add(l);
	}
	
	public void removeLayerListener(LayerListener l) {
		listeners.remove(l);
	}
	
	public LayerListener[] getLayerListeners() {
		return listeners.toArray(new LayerListener[listeners.size()]);
	}
	
	protected void notifyLayerListeners(int id) {
		if (listeners.isEmpty()) return;
		LayerEvent e = new LayerEvent(id, this);
		switch (id) {
			case LayerEvent.LAYER_NAME_CHANGED:
				for (LayerListener l : listeners)
					l.layerNameChanged(e);
				break;
			case LayerEvent.LAYER_VISIBLE_CHANGED:
				for (LayerListener l : listeners)
					l.layerVisibleChanged(e);
				break;
			case LayerEvent.LAYER_LOCKED_CHANGED:
				for (LayerListener l : listeners)
					l.layerLockedChanged(e);
				break;
			case LayerEvent.LAYER_SELECTED_CHANGED:
				for (LayerListener l : listeners)
					l.layerSelectedChanged(e);
				break;
			case LayerEvent.LAYER_LOCATION_CHANGED:
				for (LayerListener l : listeners)
					l.layerLocationChanged(e);
				break;
			case LayerEvent.LAYER_CONTENT_CHANGED:
				for (LayerListener l : listeners)
					l.layerContentChanged(e);
				break;
		}
	}
	
	public String getName() { return name; }
	
	private static class NameAtom implements Atom {
		private Layer l;
		private String oldName;
		private String newName;
		public NameAtom(Layer l, String newName) {
			this.l = l;
			this.oldName = l.name;
			this.newName = newName;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof NameAtom)
			    && (((NameAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldName = ((NameAtom)prev).oldName;
			return this;
		}
		@Override
		public void undo() {
			l.name = oldName;
			l.notifyLayerListeners(LayerEvent.LAYER_NAME_CHANGED);
		}
		@Override
		public void redo() {
			l.name = newName;
			l.notifyLayerListeners(LayerEvent.LAYER_NAME_CHANGED);
		}
	}
	
	public void setName(String name) {
		if (name == null) name = "";
		if (this.name.equals(name)) return;
		if (history != null) history.add(new NameAtom(this, name));
		this.name = name;
		this.notifyLayerListeners(LayerEvent.LAYER_NAME_CHANGED);
	}
	
	public boolean isVisible() { return visible; }
	public boolean isLocked() { return locked; }
	public boolean isSelected() { return selected; }
	public boolean isEditable() { return visible && selected && !locked; }
	
	private static class VisibleAtom implements Atom {
		private Layer l;
		private boolean oldVisible;
		private boolean newVisible;
		public VisibleAtom(Layer l, boolean newVisible) {
			this.l = l;
			this.oldVisible = l.visible;
			this.newVisible = newVisible;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof VisibleAtom)
			    && (((VisibleAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldVisible = ((VisibleAtom)prev).oldVisible;
			return this;
		}
		@Override
		public void undo() {
			l.visible = oldVisible;
			l.notifyLayerListeners(LayerEvent.LAYER_VISIBLE_CHANGED);
		}
		@Override
		public void redo() {
			l.visible = newVisible;
			l.notifyLayerListeners(LayerEvent.LAYER_VISIBLE_CHANGED);
		}
	}
	
	private static class LockedAtom implements Atom {
		private Layer l;
		private boolean oldLocked;
		private boolean newLocked;
		public LockedAtom(Layer l, boolean newLocked) {
			this.l = l;
			this.oldLocked = l.locked;
			this.newLocked = newLocked;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof LockedAtom)
			    && (((LockedAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldLocked = ((LockedAtom)prev).oldLocked;
			return this;
		}
		@Override
		public void undo() {
			l.locked = oldLocked;
			l.notifyLayerListeners(LayerEvent.LAYER_LOCKED_CHANGED);
		}
		@Override
		public void redo() {
			l.locked = newLocked;
			l.notifyLayerListeners(LayerEvent.LAYER_LOCKED_CHANGED);
		}
	}
	
	private static class SelectedAtom implements Atom {
		private Layer l;
		private boolean oldSelected;
		private boolean newSelected;
		public SelectedAtom(Layer l, boolean newSelected) {
			this.l = l;
			this.oldSelected = l.selected;
			this.newSelected = newSelected;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof SelectedAtom)
			    && (((SelectedAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldSelected = ((SelectedAtom)prev).oldSelected;
			return this;
		}
		@Override
		public void undo() {
			l.selected = oldSelected;
			l.notifyLayerListeners(LayerEvent.LAYER_SELECTED_CHANGED);
		}
		@Override
		public void redo() {
			l.selected = newSelected;
			l.notifyLayerListeners(LayerEvent.LAYER_SELECTED_CHANGED);
		}
	}
	
	public void setVisible(boolean visible) {
		if (this.visible == visible) return;
		if (history != null) history.add(new VisibleAtom(this, visible));
		this.visible = visible;
		this.notifyLayerListeners(LayerEvent.LAYER_VISIBLE_CHANGED);
	}
	
	public void setLocked(boolean locked) {
		if (this.locked == locked) return;
		if (history != null) history.add(new LockedAtom(this, locked));
		this.locked = locked;
		this.notifyLayerListeners(LayerEvent.LAYER_LOCKED_CHANGED);
	}
	
	public void setSelected(boolean selected) {
		if (this.selected == selected) return;
		if (history != null) history.add(new SelectedAtom(this, selected));
		this.selected = selected;
		this.notifyLayerListeners(LayerEvent.LAYER_SELECTED_CHANGED);
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public Point getLocation() { return new Point(x, y); }
	
	private static class LocationAtom implements Atom {
		private Layer l;
		private int oldX, oldY;
		private int newX, newY;
		public LocationAtom(Layer l, int newX, int newY) {
			this.l = l;
			this.oldX = l.x;
			this.oldY = l.y;
			this.newX = newX;
			this.newY = newY;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof LocationAtom)
			    && (((LocationAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldX = ((LocationAtom)prev).oldX;
			this.oldY = ((LocationAtom)prev).oldY;
			return this;
		}
		@Override
		public void undo() {
			l.x = oldX;
			l.y = oldY;
			l.notifyLayerListeners(LayerEvent.LAYER_LOCATION_CHANGED);
		}
		@Override
		public void redo() {
			l.x = newX;
			l.y = newY;
			l.notifyLayerListeners(LayerEvent.LAYER_LOCATION_CHANGED);
		}
	}
	
	public void setX(int x) {
		if (this.x == x) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.x = x;
		this.notifyLayerListeners(LayerEvent.LAYER_LOCATION_CHANGED);
	}
	
	public void setY(int y) {
		if (this.y == y) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.y = y;
		this.notifyLayerListeners(LayerEvent.LAYER_LOCATION_CHANGED);
	}
	
	public void setLocation(int x, int y) {
		if (this.x == x && this.y == y) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.x = x;
		this.y = y;
		this.notifyLayerListeners(LayerEvent.LAYER_LOCATION_CHANGED);
	}
	
	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}
	
	@Override public abstract Layer clone();
	
	protected abstract void paintImpl(Graphics2D g, int gx, int gy, int gw, int gh);
	
	public void paint(Graphics2D g, int gx, int gy, int gw, int gh, int tx, int ty) {
		Paint p = g.getPaint();
		Composite c = g.getComposite();
		Stroke s = g.getStroke();
		Font f = g.getFont();
		RenderingHints h = g.getRenderingHints();
		
		Shape gclip = g.getClip();
		g.clipRect(gx, gy, gw, gh);
		AffineTransform gtx = g.getTransform();
		g.translate(gx + x + tx, gy + y + ty);
		paintImpl(g, -x -tx, -y -ty, gw, gh);
		g.setTransform(gtx);
		g.setClip(gclip);
		
		g.setPaint(p);
		g.setComposite(c);
		g.setStroke(s);
		g.setFont(f);
		g.setRenderingHints(h);
	}
	
	public void paint(Graphics2D g, int gx, int gy, int gw, int gh) {
		paint(g, gx, gy, gw, gh, 0, 0);
	}
}
