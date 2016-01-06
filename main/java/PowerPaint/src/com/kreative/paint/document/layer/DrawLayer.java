package com.kreative.paint.document.layer;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.DrawObjectSurface;
import com.kreative.paint.document.draw.DrawObjectSurfaceEvent;
import com.kreative.paint.document.draw.DrawObjectSurfaceListener;
import com.kreative.paint.document.draw.DrawSurface;
import com.kreative.paint.document.undo.History;

public class DrawLayer extends Layer implements DrawSurface {
	private DrawObjectSurface ds;
	private DrawObjectSurfaceListener listener;
	
	public DrawLayer(String name) {
		super(name);
		this.ds = new DrawObjectSurface(0, 0);
		this.listener = new DrawLayerDrawObjectSurfaceListener(this);
		this.ds.addDrawObjectSurfaceListener(this.listener);
	}
	
	private DrawLayer(DrawLayer o) {
		super(o);
		this.ds = o.ds.clone();
		this.listener = new DrawLayerDrawObjectSurfaceListener(this);
		this.ds.addDrawObjectSurfaceListener(this.listener);
	}
	
	@Override
	public void setHistory(History history) {
		super.setHistory(history);
		this.ds.setHistory(history);
	}
	
	@Override
	public DrawLayer clone() {
		return new DrawLayer(this);
	}
	
	@Override
	protected void paintImpl(Graphics2D g, int gx, int gy, int gw, int gh) {
		ds.paint(g);
	}
	
	@Override public boolean hasSelection() { return ds.hasSelection(); }
	@Override public int getFirstSelectedIndex() { return ds.getFirstSelectedIndex(); }
	@Override public int getLastSelectedIndex() { return ds.getLastSelectedIndex(); }
	@Override public List<DrawObject> getSelection() { return ds.getSelection(); }
	@Override public Graphics2D createDrawGraphics() { return ds.createDrawGraphics(); }
	
	@Override public boolean add(DrawObject o) { return ds.add(o); }
	@Override public void add(int index, DrawObject o) { ds.add(index, o); }
	@Override public boolean addAll(Collection<? extends DrawObject> c) { return ds.addAll(c); }
	@Override public boolean addAll(int index, Collection<? extends DrawObject> c) { return ds.addAll(index, c); }
	@Override public void clear() { ds.clear(); }
	@Override public boolean contains(Object o) { return ds.contains(o); }
	@Override public boolean containsAll(Collection<?> c) { return ds.containsAll(c); }
	@Override public DrawObject get(int index) { return ds.get(index); }
	@Override public int indexOf(Object o) { return ds.indexOf(o); }
	@Override public boolean isEmpty() { return ds.isEmpty(); }
	@Override public Iterator<DrawObject> iterator() { return ds.iterator(); }
	@Override public int lastIndexOf(Object o) { return ds.lastIndexOf(o); }
	@Override public ListIterator<DrawObject> listIterator() { return ds.listIterator(); }
	@Override public ListIterator<DrawObject> listIterator(int index) { return ds.listIterator(index); }
	@Override public boolean remove(Object o) { return ds.remove(o); }
	@Override public DrawObject remove(int index) { return ds.remove(index); }
	@Override public boolean removeAll(Collection<?> c) { return ds.removeAll(c); }
	@Override public boolean retainAll(Collection<?> c) { return ds.retainAll(c); }
	@Override public DrawObject set(int index, DrawObject o) { return ds.set(index, o); }
	@Override public int size() { return ds.size(); }
	@Override public List<DrawObject> subList(int start, int end) { return ds.subList(start, end); }
	@Override public Object[] toArray() { return ds.toArray(); }
	@Override public <T> T[] toArray(T[] a) { return ds.toArray(a); }
	
	private static class DrawLayerDrawObjectSurfaceListener implements DrawObjectSurfaceListener {
		private final DrawLayer l;
		public DrawLayerDrawObjectSurfaceListener(DrawLayer l) {
			this.l = l;
		}
		@Override
		public void drawObjectSurfaceLocationChanged(DrawObjectSurfaceEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void drawObjectSurfaceContentChanged(DrawObjectSurfaceEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
	}
}
