package com.kreative.paint.document.layer;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import com.kreative.paint.document.undo.Atom;

public class FillLayer extends Layer {
	private Paint fill;
	private Composite comp;
	
	public FillLayer(String name, Paint fill, Composite comp) {
		super(name);
		this.fill = fill;
		this.comp = comp;
	}
	
	private FillLayer(FillLayer o) {
		super(o);
		this.fill = o.fill;
		this.comp = o.comp;
	}
	
	@Override
	public FillLayer clone() {
		return new FillLayer(this);
	}
	
	@Override
	protected void paintImpl(Graphics2D g, int gx, int gy, int gw, int gh) {
		g.setPaint(fill);
		g.setComposite(comp);
		g.fillRect(gx, gy, gw, gh);
	}
	
	public Paint getFill() { return fill; }
	public Composite getComposite() { return comp; }
	
	private static class FillAtom implements Atom {
		private FillLayer l;
		private Paint oldFill;
		private Paint newFill;
		public FillAtom(FillLayer l, Paint newFill) {
			this.l = l;
			this.oldFill = l.fill;
			this.newFill = newFill;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof FillAtom)
			    && (((FillAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldFill = ((FillAtom)prev).oldFill;
			return this;
		}
		@Override
		public void undo() {
			l.fill = oldFill;
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void redo() {
			l.fill = newFill;
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
	}
	
	private static class CompositeAtom implements Atom {
		private FillLayer l;
		private Composite oldComp;
		private Composite newComp;
		public CompositeAtom(FillLayer l, Composite newComp) {
			this.l = l;
			this.oldComp = l.comp;
			this.newComp = newComp;
		}
		@Override
		public boolean canBuildUpon(Atom prev) {
			return (prev instanceof CompositeAtom)
			    && (((CompositeAtom)prev).l == this.l);
		}
		@Override
		public Atom buildUpon(Atom prev) {
			this.oldComp = ((CompositeAtom)prev).oldComp;
			return this;
		}
		@Override
		public void undo() {
			l.comp = oldComp;
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
		@Override
		public void redo() {
			l.comp = newComp;
			l.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
		}
	}
	
	public void setFill(Paint fill) {
		if (this.fill == fill) return;
		if (history != null) history.add(new FillAtom(this, fill));
		this.fill = fill;
		this.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
	}
	
	public void setComposite(Composite comp) {
		if (this.comp == comp) return;
		if (history != null) history.add(new CompositeAtom(this, comp));
		this.comp = comp;
		this.notifyLayerListeners(LayerEvent.LAYER_CONTENT_CHANGED);
	}
}
