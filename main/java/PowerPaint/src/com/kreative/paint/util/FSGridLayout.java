package com.kreative.paint.util;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Insets;

public class FSGridLayout extends GridLayout {
	private static final long serialVersionUID = 1L;
	
	public FSGridLayout() {
		super();
	}
	
	public FSGridLayout(int rows, int cols) {
		super(rows, cols);
	}
	
	public FSGridLayout(int rows, int cols, int hgap, int vgap) {
		super(rows, cols, hgap, vgap);
	}
	
	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = getRows();
			int ncols = getColumns();
			if (ncomponents == 0) return;
			if (nrows > 0) ncols = (ncomponents + nrows - 1) / nrows;
			else nrows = (ncomponents + ncols - 1) / ncols;
			int hgap = getHgap();
			int vgap = getVgap();
			double w = (double)(parent.getWidth()-insets.left-insets.right+vgap)/(double)ncols;
			double h = (double)(parent.getHeight()-insets.top-insets.bottom+hgap)/(double)nrows;
			for (int c = 0; c < ncols; c ++) {
				int x1 = insets.left+(int)Math.floor(w*c);
				int x2 = insets.left+(int)Math.floor(w*(c+1));
				for (int r = 0; r < nrows; r ++) {
					int y1 = insets.top+(int)Math.floor(h*r);
					int y2 = insets.top+(int)Math.floor(h*(r+1));
					int i = r * ncols + c;
					if (i < ncomponents) {
						parent.getComponent(i).setBounds(x1, y1, x2-x1-vgap, y2-y1-hgap);
					}
				}
			}
		}
	}
}
