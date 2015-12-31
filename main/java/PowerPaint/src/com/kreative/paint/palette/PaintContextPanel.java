package com.kreative.paint.palette;

import javax.swing.JPanel;
import com.kreative.paint.PaintContext;
import com.kreative.paint.PaintContextConstants;
import com.kreative.paint.PaintContextListener;
import com.kreative.paint.document.draw.PaintSettings;

public abstract class PaintContextPanel extends JPanel implements PaintContextListener, PaintContextConstants {
	private static final long serialVersionUID = 1L;
	protected PaintContext pc;
	protected int eventMask;
	
	public PaintContextPanel(PaintContext pc, int eventMask) {
		pc.addPaintContextListener(this);
		this.pc = pc;
		this.eventMask = eventMask;
	}
	
	public final PaintContext getPaintContext() {
		return pc;
	}
	
	public final void setPaintContext(PaintContext pc) {
		this.pc.removePaintContextListener(this);
		pc.addPaintContextListener(this);
		this.pc = pc;
		pcChanged(pc);
	}
	
	protected void pcChanged(PaintContext pc) {}
	
	public final void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
		if ((eventMask & delta) != 0) update();
	}

	public final void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {
		if ((eventMask & CHANGED_EDITING) != 0) update();
	}
	
	protected abstract void update();
}
