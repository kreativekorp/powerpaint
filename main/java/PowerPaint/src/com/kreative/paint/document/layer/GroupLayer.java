package com.kreative.paint.document.layer;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import com.kreative.paint.document.undo.Atom;
import com.kreative.paint.document.undo.History;

public class GroupLayer extends Layer implements List<Layer> {
	private List<Layer> layers;
	private LayerListener listener;
	
	public GroupLayer(String name) {
		super(name);
		this.layers = new ArrayList<Layer>();
		this.listener = new GroupLayerLayerListener(this);
	}
	
	private GroupLayer(GroupLayer o) {
		super(o);
		this.layers = new ArrayList<Layer>();
		for (Layer l : o.layers) this.layers.add(l.clone());
		this.listener = new GroupLayerLayerListener(this);
		for (Layer l : this.layers) l.addLayerListener(listener);
	}
	
	@Override
	public void setHistory(History history) {
		super.setHistory(history);
		for (Layer l : this.layers) l.setHistory(history);
	}
	
	@Override
	public GroupLayer clone() {
		return new GroupLayer(this);
	}
	
	@Override
	protected void paintImpl(Graphics2D g, int gx, int gy, int gw, int gh) {
		for (Layer l : layers) {
			if (l.visible) {
				l.paint(g, gx, gy, gw, gh, -gx, -gy);
			}
		}
	}
	
	private static class SetLayersAtom implements Atom {
		private GroupLayer l;
		private List<Layer> oldLayers;
		private List<Layer> newLayers;
		public SetLayersAtom(
			GroupLayer l,
			List<Layer> oldLayers,
			List<Layer> newLayers
		) {
			this.l = l;
			this.oldLayers = oldLayers;
			this.newLayers = newLayers;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof SetLayersAtom)
			    && (((SetLayersAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldLayers = ((SetLayersAtom)prev).oldLayers;
			return this;
		}
		@Override
		public void redo() {
			l.layers.clear();
			l.layers.addAll(newLayers);
			for (Layer layer : oldLayers) {
				layer.setHistory(null);
				layer.removeLayerListener(l.listener);
			}
			for (Layer layer : newLayers) {
				layer.setHistory(l.history);
				layer.addLayerListener(l.listener);
			}
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void undo() {
			l.layers.clear();
			l.layers.addAll(oldLayers);
			for (Layer layer : newLayers) {
				layer.setHistory(null);
				layer.removeLayerListener(l.listener);
			}
			for (Layer layer : oldLayers) {
				layer.setHistory(l.history);
				layer.addLayerListener(l.listener);
			}
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
	}
	
	private List<Layer> changing() {
		List<Layer> oldLayers = new ArrayList<Layer>();
		oldLayers.addAll(layers);
		return oldLayers;
	}
	
	private void changed(List<Layer> oldLayers) {
		List<Layer> newLayers = new ArrayList<Layer>();
		newLayers.addAll(layers);
		if (history != null) history.add(new SetLayersAtom(this, oldLayers, newLayers));
		
		for (Layer layer : oldLayers) {
			layer.setHistory(null);
			layer.removeLayerListener(listener);
		}
		for (Layer layer : newLayers) {
			layer.setHistory(history);
			layer.addLayerListener(listener);
		}
		this.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
	}
	
	@Override
	public boolean add(Layer o) {
		List<Layer> c = changing();
		boolean r = layers.add(o);
		changed(c);
		return r;
	}
	
	@Override
	public void add(int index, Layer o) {
		List<Layer> c = changing();
		layers.add(index, o);
		changed(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends Layer> coll) {
		List<Layer> c = changing();
		boolean r = layers.addAll(coll);
		changed(c);
		return r;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Layer> coll) {
		List<Layer> c = changing();
		boolean r = layers.addAll(index, coll);
		changed(c);
		return r;
	}
	
	@Override
	public void clear() {
		List<Layer> c = changing();
		layers.clear();
		changed(c);
	}
	
	@Override
	public boolean contains(Object o) {
		return layers.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> coll) {
		return layers.containsAll(coll);
	}
	
	@Override
	public Layer get(int index) {
		return layers.get(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return layers.indexOf(o);
	}
	
	@Override
	public boolean isEmpty() {
		return layers.isEmpty();
	}
	
	@Override
	public Iterator<Layer> iterator() {
		return Collections.unmodifiableList(layers).iterator();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return layers.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<Layer> listIterator() {
		return Collections.unmodifiableList(layers).listIterator();
	}
	
	@Override
	public ListIterator<Layer> listIterator(int index) {
		return Collections.unmodifiableList(layers).listIterator(index);
	}
	
	@Override
	public boolean remove(Object o) {
		List<Layer> c = changing();
		boolean r = layers.remove(o);
		changed(c);
		return r;
	}
	
	@Override
	public Layer remove(int index) {
		List<Layer> c = changing();
		Layer r = layers.remove(index);
		changed(c);
		return r;
	}
	
	@Override
	public boolean removeAll(Collection<?> coll) {
		List<Layer> c = changing();
		boolean r = layers.removeAll(coll);
		changed(c);
		return r;
	}
	
	@Override
	public boolean retainAll(Collection<?> coll) {
		List<Layer> c = changing();
		boolean r = layers.retainAll(coll);
		changed(c);
		return r;
	}
	
	@Override
	public Layer set(int index, Layer o) {
		List<Layer> c = changing();
		Layer r = layers.set(index, o);
		changed(c);
		return r;
	}
	
	@Override
	public int size() {
		return layers.size();
	}
	
	@Override
	public List<Layer> subList(int fromIndex, int toIndex) {
		return Collections.unmodifiableList(layers).subList(fromIndex, toIndex);
	}
	
	@Override
	public Object[] toArray() {
		return layers.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return layers.toArray(a);
	}
	
	private static class GroupLayerLayerListener implements LayerListener {
		private final GroupLayer l;
		public GroupLayerLayerListener(GroupLayer l) {
			this.l = l;
		}
		@Override
		public void layerNameChanged(LayerEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void layerVisibleChanged(LayerEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void layerLockedChanged(LayerEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void layerSelectedChanged(LayerEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void layerLocationChanged(LayerEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void layerContentChanged(LayerEvent e) {
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
	}
}
