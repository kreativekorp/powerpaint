package com.kreative.paint.document.draw;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;
import com.kreative.paint.document.undo.Recordable;

public class DrawObjectSurface implements Cloneable, Recordable, DrawSurface {
	private int x;
	private int y;
	private List<DrawObject> objects;
	private History history;
	private List<DrawObjectSurfaceListener> listeners;
	private DrawObjectListener objectListener;
	
	public DrawObjectSurface(int x, int y) {
		this.x = x;
		this.y = y;
		this.objects = new ArrayList<DrawObject>();
		this.history = null;
		this.listeners = new ArrayList<DrawObjectSurfaceListener>();
		this.objectListener = new DrawObjectSurfaceDrawObjectListener(this);
	}
	
	private DrawObjectSurface(DrawObjectSurface o) {
		this.x = o.x;
		this.y = o.y;
		this.objects = new ArrayList<DrawObject>();
		for (DrawObject d : o.objects) {
			this.objects.add(d.clone());
		}
		this.history = null;
		this.listeners = new ArrayList<DrawObjectSurfaceListener>();
		this.objectListener = new DrawObjectSurfaceDrawObjectListener(this);
		for (DrawObject d : this.objects) {
			d.addDrawObjectListener(objectListener);
		}
	}
	
	@Override
	public DrawObjectSurface clone() {
		return new DrawObjectSurface(this);
	}
	
	@Override
	public History getHistory() {
		return history;
	}
	
	@Override
	public void setHistory(History history) {
		this.history = history;
		for (DrawObject o : objects) o.setHistory(history);
	}
	
	public void addDrawObjectSurfaceListener(DrawObjectSurfaceListener l) {
		listeners.add(l);
	}
	
	public void removeDrawObjectSurfaceListener(DrawObjectSurfaceListener l) {
		listeners.remove(l);
	}
	
	public DrawObjectSurfaceListener[] getDrawObjectSurfaceListeners() {
		return listeners.toArray(new DrawObjectSurfaceListener[listeners.size()]);
	}
	
	protected void notifyDrawObjectSurfaceListeners(int id) {
		if (listeners.isEmpty()) return;
		DrawObjectSurfaceEvent e = new DrawObjectSurfaceEvent(id, this);
		switch (id) {
			case DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_LOCATION_CHANGED:
				for (DrawObjectSurfaceListener l : listeners)
					l.drawObjectSurfaceLocationChanged(e);
				break;
			case DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED:
				for (DrawObjectSurfaceListener l : listeners)
					l.drawObjectSurfaceContentChanged(e);
				break;
		}
	}
	
	private static class DrawObjectSurfaceDrawObjectListener implements DrawObjectListener {
		private final DrawObjectSurface ds;
		public DrawObjectSurfaceDrawObjectListener(DrawObjectSurface ds) {
			this.ds = ds;
		}
		@Override
		public void drawObjectPaintSettingsChanged(DrawObjectEvent e) {
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void drawObjectTransformChanged(DrawObjectEvent e) {
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void drawObjectVisibleChanged(DrawObjectEvent e) {
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void drawObjectLockedChanged(DrawObjectEvent e) {
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void drawObjectSelectedChanged(DrawObjectEvent e) {
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void drawObjectControlPointChanged(DrawObjectEvent e) {
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void drawObjectLocationChanged(DrawObjectEvent e) {
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void drawObjectImplementationPropertyChanged(DrawObjectEvent e) {
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public Point getLocation() { return new Point(x, y); }
	
	private static class LocationAtom implements Atom {
		private DrawObjectSurface ds;
		private int oldX, oldY;
		private int newX, newY;
		public LocationAtom(DrawObjectSurface ds, int newX, int newY) {
			this.ds = ds;
			this.oldX = ds.x; this.oldY = ds.y;
			this.newX = newX; this.newY = newY;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof LocationAtom)
			    && (((LocationAtom)prev).ds == this.ds);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldX = ((LocationAtom)prev).oldX;
			this.oldY = ((LocationAtom)prev).oldY;
			return this;
		}
		@Override
		public void redo() {
			ds.x = newX;
			ds.y = newY;
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_LOCATION_CHANGED);
		}
		@Override
		public void undo() {
			ds.x = oldX;
			ds.y = oldY;
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_LOCATION_CHANGED);
		}
	}
	
	public void setX(int x) {
		if (this.x == x) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.x = x;
		this.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_LOCATION_CHANGED);
	}
	
	public void setY(int y) {
		if (this.y == y) return;
		if (history != null) history.add(new LocationAtom(this, x, y));
		this.y = y;
		this.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_LOCATION_CHANGED);
	}
	
	public void setLocation(Point p) {
		if (this.x == p.x && this.y == p.y) return;
		if (history != null) history.add(new LocationAtom(this, p.x, p.y));
		this.x = p.x;
		this.y = p.y;
		this.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_LOCATION_CHANGED);
	}
	
	public void paint(Graphics2D g) {
		AffineTransform tx = g.getTransform();
		g.translate(x, y);
		for (DrawObject o : objects) {
			if (o.visible) {
				o.paint(g);
			}
		}
		g.setTransform(tx);
	}
	
	public void paint(Graphics2D g, int x, int y) {
		AffineTransform tx = g.getTransform();
		g.translate(this.x + x, this.y + y);
		for (DrawObject o : objects) {
			if (o.visible) {
				o.paint(g);
			}
		}
		g.setTransform(tx);
	}
	
	@Override
	public boolean hasSelection() {
		for (DrawObject o : objects) {
			if (o.selected) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int getFirstSelectedIndex() {
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).selected) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int getLastSelectedIndex() {
		for (int i = objects.size() - 1; i >= 0; i--) {
			if (objects.get(i).selected) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public List<DrawObject> getSelection() {
		List<DrawObject> selected = new ArrayList<DrawObject>();
		for (DrawObject o : objects) {
			if (o.selected) {
				selected.add(o);
			}
		}
		return selected;
	}
	
	@Override
	public Graphics2D createDrawGraphics() {
		return new DrawObjectSurfaceGraphics(this, this.x, this.y);
	}
	
	private static class SetObjectsAtom implements Atom {
		private DrawObjectSurface ds;
		private List<DrawObject> oldObjects;
		private List<DrawObject> newObjects;
		public SetObjectsAtom(
			DrawObjectSurface ds,
			List<DrawObject> oldObjects,
			List<DrawObject> newObjects
		) {
			this.ds = ds;
			this.oldObjects = oldObjects;
			this.newObjects = newObjects;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof SetObjectsAtom)
			    && (((SetObjectsAtom)prev).ds == this.ds);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldObjects = ((SetObjectsAtom)prev).oldObjects;
			return this;
		}
		@Override
		public void redo() {
			ds.objects.clear();
			ds.objects.addAll(newObjects);
			for (DrawObject o : oldObjects) {
				o.setHistory(null);
				o.removeDrawObjectListener(ds.objectListener);
			}
			for (DrawObject o : newObjects) {
				o.setHistory(ds.history);
				o.addDrawObjectListener(ds.objectListener);
			}
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
		@Override
		public void undo() {
			ds.objects.clear();
			ds.objects.addAll(oldObjects);
			for (DrawObject o : newObjects) {
				o.setHistory(null);
				o.removeDrawObjectListener(ds.objectListener);
			}
			for (DrawObject o : oldObjects) {
				o.setHistory(ds.history);
				o.addDrawObjectListener(ds.objectListener);
			}
			ds.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
		}
	}
	
	private List<DrawObject> changing() {
		List<DrawObject> oldObjects = new ArrayList<DrawObject>();
		oldObjects.addAll(objects);
		return oldObjects;
	}
	
	private void changed(List<DrawObject> oldObjects) {
		List<DrawObject> newObjects = new ArrayList<DrawObject>();
		newObjects.addAll(objects);
		if (history != null) history.add(new SetObjectsAtom(this, oldObjects, newObjects));
		
		for (DrawObject o : oldObjects) {
			o.setHistory(null);
			o.removeDrawObjectListener(objectListener);
		}
		for (DrawObject o : newObjects) {
			o.setHistory(history);
			o.addDrawObjectListener(objectListener);
		}
		this.notifyDrawObjectSurfaceListeners(DrawObjectSurfaceEvent.DRAW_OBJECT_SURFACE_CONTENT_CHANGED);
	}
	
	@Override
	public boolean add(DrawObject o) {
		List<DrawObject> c = changing();
		boolean r = objects.add(o);
		changed(c);
		return r;
	}
	
	@Override
	public void add(int index, DrawObject o) {
		List<DrawObject> c = changing();
		objects.add(index, o);
		changed(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends DrawObject> coll) {
		List<DrawObject> c = changing();
		boolean r = objects.addAll(coll);
		changed(c);
		return r;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends DrawObject> coll) {
		List<DrawObject> c = changing();
		boolean r = objects.addAll(index, coll);
		changed(c);
		return r;
	}
	
	@Override
	public void clear() {
		List<DrawObject> c = changing();
		objects.clear();
		changed(c);
	}
	
	@Override
	public boolean contains(Object o) {
		return objects.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> coll) {
		return objects.containsAll(coll);
	}
	
	@Override
	public DrawObject get(int index) {
		return objects.get(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return objects.indexOf(o);
	}
	
	@Override
	public boolean isEmpty() {
		return objects.isEmpty();
	}
	
	@Override
	public Iterator<DrawObject> iterator() {
		return Collections.unmodifiableList(objects).iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return objects.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<DrawObject> listIterator() {
		return Collections.unmodifiableList(objects).listIterator();
	}
	
	@Override
	public ListIterator<DrawObject> listIterator(int index) {
		return Collections.unmodifiableList(objects).listIterator(index);
	}
	
	@Override
	public boolean remove(Object o) {
		List<DrawObject> c = changing();
		boolean r = objects.remove(o);
		changed(c);
		return r;
	}
	
	@Override
	public DrawObject remove(int index) {
		List<DrawObject> c = changing();
		DrawObject r = objects.remove(index);
		changed(c);
		return r;
	}
	
	@Override
	public boolean removeAll(Collection<?> coll) {
		List<DrawObject> c = changing();
		boolean r = objects.removeAll(coll);
		changed(c);
		return r;
	}
	
	@Override
	public boolean retainAll(Collection<?> coll) {
		List<DrawObject> c = changing();
		boolean r = objects.retainAll(coll);
		changed(c);
		return r;
	}
	
	@Override
	public DrawObject set(int index, DrawObject o) {
		List<DrawObject> c = changing();
		DrawObject r = objects.set(index, o);
		changed(c);
		return r;
	}
	
	@Override
	public int size() {
		return objects.size();
	}
	
	@Override
	public List<DrawObject> subList(int fromIndex, int toIndex) {
		return Collections.unmodifiableList(objects).subList(fromIndex, toIndex);
	}
	
	@Override
	public Object[] toArray() {
		return objects.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return objects.toArray(a);
	}
}
