/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class CellSelector<T> extends JComponent implements Scrollable {
	private static final long serialVersionUID = 1L;
	
	private CellSelectorModel<T> model;
	private CellSelectorRenderer<T> renderer;
	private CellSelectionListener<T> listener;
	private Dimension minsz, prefsz, maxsz;
	
	public CellSelector(CellSelectorModel<T> model, CellSelectorRenderer<T> renderer) {
		this.model = model;
		this.renderer = renderer;
		this.listener = new CellSelectionListener<T>() {
			public void cellSelected(CellSelectionEvent<T> e) {
				repaint();
			}
		};
		this.minsz = this.prefsz = this.maxsz = null;
		if (this.model != null) this.model.addCellSelectionListener(listener);
		InternalMouseListener iml = new InternalMouseListener(false);
		this.addMouseListener(iml);
		this.addMouseMotionListener(iml);
	}
	
	public CellSelector(CellSelectorModel<T> model, CellSelectorRenderer<T> renderer, boolean popup) {
		this.model = model;
		this.renderer = renderer;
		this.listener = new CellSelectionListener<T>() {
			public void cellSelected(CellSelectionEvent<T> e) {
				repaint();
			}
		};
		this.minsz = this.prefsz = this.maxsz = null;
		if (this.model != null) this.model.addCellSelectionListener(listener);
		InternalMouseListener iml = new InternalMouseListener(popup);
		this.addMouseListener(iml);
		this.addMouseMotionListener(iml);
	}
	
	public CellSelector<T> asPopup() {
		CellSelector<T> pop = new CellSelector<T>(model, renderer, true);
		pop.minsz = this.minsz;
		pop.prefsz = this.prefsz;
		pop.maxsz = this.maxsz;
		return pop;
	}
	
	public void pack() {
		Component c = this;
		c.invalidate();
		while (c != null) {
			if (c instanceof Window) { ((Window)c).pack(); break; }
			else if (c instanceof Frame) { ((Frame)c).pack(); break; }
			else if (c instanceof Dialog) { ((Dialog)c).pack(); break; }
			else c = c.getParent();
		}
	}
	
	public void dispose() {
		if (this.model != null) this.model.removeCellSelectionListener(listener);
	}
	
	public CellSelectorModel<T> getModel() {
		return model;
	}
	
	public void setModel(CellSelectorModel<T> model) {
		if (this.model != null) this.model.removeCellSelectionListener(listener);
		this.model = model;
		if (this.model != null) this.model.addCellSelectionListener(listener);
	}
	
	public CellSelectorRenderer<T> getRenderer() {
		return renderer;
	}
	
	public void setRenderer(CellSelectorRenderer<T> renderer) {
		this.renderer = renderer;
	}
	
	public Dimension getMinimumSize() {
		if (minsz != null) return minsz;
		Insets i = getInsets();
		int protocols = renderer.getColumns();
		int protorows = renderer.getRows();
		int cols = (protocols > 0) ? protocols : (protorows > 0) ? ((model.size() + protorows-1)/protorows) : optimumColumns(model.size());
		int rows = (protorows > 0) ? protorows : (protocols > 0) ? ((model.size() + protocols-1)/protocols) : ((model.size() + cols-1)/cols);
		return new Dimension(i.left + i.right + 7*cols + 3, i.top + i.bottom + 7*rows + 3);
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		if (prefsz != null) return prefsz;
		Insets i = getInsets();
		int protocols = renderer.getColumns();
		int protorows = renderer.getRows();
		int cols = (protocols > 0) ? protocols : (protorows > 0) ? ((model.size() + protorows-1)/protorows) : optimumColumns(model.size());
		int rows = (protorows > 0) ? protorows : (protocols > 0) ? ((model.size() + protocols-1)/protocols) : ((model.size() + cols-1)/cols);
		if (rows > 8) rows = 8;
		int protocw = renderer.getCellWidth();
		int protoch = renderer.getCellHeight();
		int cw = (protocw > 0) ? protocw : (protoch > 0) ? protoch : 24;
		int ch = (protoch > 0) ? protoch : (protocw > 0) ? protocw : 24;
		return new Dimension(i.left + i.right + (5+cw)*cols + 3, i.top + i.bottom + (5+ch)*rows + 3);
	}
	
	public Dimension getPreferredSize() {
		if (prefsz != null) return prefsz;
		Insets i = getInsets();
		int protocols = renderer.getColumns();
		int protorows = renderer.getRows();
		int cols = (protocols > 0) ? protocols : (protorows > 0) ? ((model.size() + protorows-1)/protorows) : optimumColumns(model.size());
		int rows = (protorows > 0) ? protorows : (protocols > 0) ? ((model.size() + protocols-1)/protocols) : ((model.size() + cols-1)/cols);
		int protocw = renderer.getCellWidth();
		int protoch = renderer.getCellHeight();
		int cw = (protocw > 0) ? protocw : (protoch > 0) ? protoch : 24;
		int ch = (protoch > 0) ? protoch : (protocw > 0) ? protocw : 24;
		return new Dimension(i.left + i.right + (5+cw)*cols + 3, i.top + i.bottom + (5+ch)*rows + 3);
	}
	
	public Dimension getMaximumSize() {
		if (maxsz != null) return maxsz;
		return super.getMaximumSize();
	}
	
	public void setMinimumSize(Dimension d) {
		minsz = d;
	}
	
	public void setPreferredSize(Dimension d) {
		prefsz = d;
	}
	
	public void setMaximumSize(Dimension d) {
		maxsz = d;
	}
	
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		int ui = getScrollableUnitIncrement(visibleRect, orientation, direction);
		return ui*(((orientation == SwingConstants.HORIZONTAL) ? visibleRect.width : visibleRect.height)/ui);
	}
	
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		Insets i = getInsets();
		int protocols = renderer.getColumns();
		int protorows = renderer.getRows();
		int cols = (protocols > 0) ? protocols : (protorows > 0) ? ((model.size() + protorows-1)/protorows) : optimumColumns(model.size());
		int rows = (protorows > 0) ? protorows : (protocols > 0) ? ((model.size() + protocols-1)/protocols) : ((model.size() + cols-1)/cols);
		int cw = (((getWidth() - i.left - i.right - 3)/cols) - 5);
		int ch = (((getHeight() - i.top - i.bottom - 3)/rows) - 5);
		return ((orientation == SwingConstants.HORIZONTAL) ? cw : ch) + 5;
	}
	
	public boolean getScrollableTracksViewportHeight() {
		return !renderer.isFixedHeight();
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return !renderer.isFixedWidth();
	}
	
	protected void paintComponent(Graphics g) {
		Insets i = getInsets();
		int protocols = renderer.getColumns();
		int protorows = renderer.getRows();
		int cols = (protocols > 0) ? protocols : (protorows > 0) ? ((model.size() + protorows-1)/protorows) : optimumColumns(model.size());
		int rows = (protorows > 0) ? protorows : (protocols > 0) ? ((model.size() + protocols-1)/protocols) : ((model.size() + cols-1)/cols);
		int cw = (((getWidth() - i.left - i.right - 3)/cols) - 5);
		int ch = (((getHeight() - i.top - i.bottom - 3)/rows) - 5);
		int s = model.getSelectedIndex();
		int n = model.size();
		g.clearRect(i.left, i.top, getWidth()-i.left-i.right, getHeight()-i.top-i.bottom);
		for (int ly = 0, y = i.top+4, iy = 0; ly < rows && iy < n; ly++, y += ch+5, iy += cols) {
			for (int lx = 0, x = i.left+4, ix = iy; lx < cols && ix < n; lx++, x += cw+5, ix++) {
				if (ix == s) {
					((Graphics2D)g).setPaint(Color.black);
					g.fillRect(x-3, y-3, cw+6, ch+6);
					((Graphics2D)g).setPaint(Color.lightGray);
					g.fillRect(x-1, y-1, cw+2, ch+2);
				} else {
					((Graphics2D)g).setPaint(Color.gray);
					g.fillRect(x-1, y-1, cw+2, ch+2);
				}
				renderer.paint(g, model.get(ix), x, y, cw, ch);
			}
		}
	}
	
	public int getIndexAt(int x, int y) {
		Insets i = getInsets();
		int protocols = renderer.getColumns();
		int protorows = renderer.getRows();
		int cols = (protocols > 0) ? protocols : (protorows > 0) ? ((model.size() + protorows-1)/protorows) : optimumColumns(model.size());
		int rows = (protorows > 0) ? protorows : (protocols > 0) ? ((model.size() + protocols-1)/protocols) : ((model.size() + cols-1)/cols);
		int cw = (((getWidth() - i.left - i.right - 3)/cols) - 5);
		int ch = (((getHeight() - i.top - i.bottom - 3)/rows) - 5);
		x -= i.left+2; x /= cw+5; if (x < 0) x = 0; if (x >= cols) x = cols-1;
		y -= i.top+2; y /= ch+5; if (y < 0) y = 0; if (y >= rows) y = rows-1;
		int idx = y*cols + x; if (idx < 0) idx = 0; if (idx >= model.size()) idx = model.size()-1;
		return idx;
	}
	
	public T getObjectAt(int x, int y) {
		return model.get(getIndexAt(x, y));
	}
	
	private class InternalMouseListener implements MouseListener, MouseMotionListener {
		private boolean popup;
		private T last;
		public InternalMouseListener(boolean popup) {
			this.popup = popup;
			this.last = (model == null) ? null : model.getSelectedObject();
		}
		public void mouseEntered(MouseEvent e) {
			if (model != null) {
				this.last = model.getSelectedObject();
				doHover(e);
			}
		}
		public void mouseExited(MouseEvent e) {
			if (model != null) {
				doHover(e);
				if (popup) model.setSelectedObject(this.last);
			}
		}
		public void mouseMoved(MouseEvent e) { doHover(e); }
		public void mouseClicked(MouseEvent e) { doClick(e); }
		public void mouseDragged(MouseEvent e) { doClick(e); }
		public void mousePressed(MouseEvent e) { doClick(e); }
		public void mouseReleased(MouseEvent e) { doClick(e); }
		private void doHover(MouseEvent e) {
			if (model != null) {
				int i = getIndexAt(e.getX(), e.getY());
				setToolTipText(model.getToolTipText(i));
				if (popup) model.setSelectedIndex(i);
			}
		}
		private void doClick(MouseEvent e) {
			if (model != null) {
				int i = getIndexAt(e.getX(), e.getY());
				model.setSelectedIndex(i);
				this.last = model.get(i);
			}
		}
	}
	
	private static int optimumColumns(int n) {
		if (n < 8) return n;
		else {
			int lower = (int)Math.ceil(Math.sqrt(n));
			int upper = (int)Math.ceil(Math.sqrt(n) * 1.618034);
			int cols = lower;
			int diff = lower - (n % lower);
			for (int i = lower; i <= upper; i++) {
				if ((n % i) == 0) return i;
				else if ((i - (n % i)) < diff) {
					cols = i;
					diff = i - (n % i);
				}
			}
			return cols;
		}
	}
}
