package com.kreative.paint.rcp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import javax.swing.JComponent;

public class RCPXComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private RCPXPalette palette;
	private RCPXOrientation orientation;
	private Color currCol;
	private Dimension minSize, prefSize, maxSize;
	private HashSet<ColorChangeListener> listeners;
	
	public RCPXComponent() {
		this(null, null, null, false);
	}
	
	public RCPXComponent(RCPXPalette palette) {
		this(palette, null, null, false);
	}
	
	public RCPXComponent(RCPXPalette palette, RCPXOrientation orientation, Color currCol, boolean popup) {
		this.palette = palette;
		this.orientation = orientation;
		this.currCol = currCol;
		this.minSize = null;
		this.prefSize = null;
		this.maxSize = null;
		this.listeners = new HashSet<ColorChangeListener>();
		InternalMouseListener ml = new InternalMouseListener(popup);
		this.addMouseListener(ml);
		this.addMouseMotionListener(ml);
	}
	
	public RCPXComponent asPopup() {
		RCPXComponent popup = new RCPXComponent(
			this.palette,
			this.orientation,
			this.currCol,
			true
		);
		popup.minSize = this.minSize;
		popup.prefSize = this.prefSize;
		popup.maxSize = this.maxSize;
		popup.listeners.addAll(this.listeners);
		return popup;
	}
	
	public void pack() {
		this.invalidate();
		Component c = this.getParent();
		while (true) {
			if (c == null) {
				break;
			} else if (c instanceof Window) {
				((Window)c).pack();
				break;
			} else if (c instanceof Frame) {
				((Frame)c).pack();
				break;
			} else if (c instanceof Dialog) {
				((Dialog)c).pack();
				break;
			} else {
				c = c.getParent();
			}
		}
	}
	
	public RCPXPalette getPalette() {
		return palette;
	}
	
	public void setPalette(RCPXPalette palette) {
		this.palette = palette;
		repaint();
	}
	
	public RCPXOrientation getOrientation() {
		return orientation;
	}
	
	public void setOrientation(RCPXOrientation orientation) {
		this.orientation = orientation;
		repaint();
	}
	
	public Color getSelectedColor() {
		return currCol;
	}
	
	public void setSelectedColor(Color currCol) {
		this.currCol = currCol;
		repaint();
	}
	
	public void addColorChangeListener(ColorChangeListener l) {
		listeners.add(l);
	}
	
	public void removeColorChangeListener(ColorChangeListener l) {
		listeners.remove(l);
	}
	
	public ColorChangeListener[] getColorChangeListeners() {
		return listeners.toArray(new ColorChangeListener[listeners.size()]);
	}
	
	protected void notifyColorChangeListeners(Color c) {
		ColorChangeEvent e = new ColorChangeEvent(this, c);
		for (ColorChangeListener l : listeners) l.colorChanged(e);
	}
	
	private Dimension getRawXSize() {
		if (orientation != null) {
			switch (orientation) {
				case HORIZONTAL: return new Dimension(palette.hwidth, palette.hheight);
				case SQUARE    : return new Dimension(palette.swidth, palette.sheight);
				case VERTICAL  : return new Dimension(palette.vwidth, palette.vheight);
			}
		}
		if (palette.orientation != null) {
			switch (palette.orientation) {
				case HORIZONTAL: return new Dimension(palette.hwidth, palette.hheight);
				case SQUARE    : return new Dimension(palette.swidth, palette.sheight);
				case VERTICAL  : return new Dimension(palette.vwidth, palette.vheight);
			}
		}
		return new Dimension(palette.hwidth, palette.hheight);
	}
	
	private Dimension getXSize() {
		Insets i = getInsets();
		Dimension d = getRawXSize();
		d.width += i.left + i.right;
		d.height += i.top + i.bottom;
		return d;
	}
	
	@Override
	public Dimension getMinimumSize() {
		if (minSize != null) return minSize;
		if (palette == null) return super.getMinimumSize();
		return getXSize();
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (prefSize != null) return prefSize;
		if (palette == null) return super.getPreferredSize();
		return getXSize();
	}
	
	@Override
	public Dimension getMaximumSize() {
		if (maxSize != null) return maxSize;
		if (palette == null) return super.getMaximumSize();
		return getXSize();
	}
	
	@Override
	public void setMinimumSize(Dimension d) {
		this.minSize = d;
	}
	
	@Override
	public void setPreferredSize(Dimension d) {
		this.prefSize = d;
	}
	
	@Override
	public void setMaximumSize(Dimension d) {
		this.maxSize = d;
	}
	
	private Rectangle calcRect() {
		if (palette == null) return null;
		if (palette.layout == null) return null;
		if (palette.colors == null) return null;
		Insets i = getInsets();
		Dimension d = getSize();
		return new Rectangle(
			i.left, i.top,
			d.width-i.left-i.right,
			d.height-i.top-i.bottom
		);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Rectangle r = calcRect();
		if (r == null) return;
		palette.layout.paint(palette.colors, 0, g, r, currCol);
	}
	
	private class InternalMouseListener implements MouseListener, MouseMotionListener {
		private boolean popup;
		private Color lastCol;
		public InternalMouseListener(boolean popup) {
			this.popup = popup;
			this.lastCol = currCol;
		}
		public void mouseEntered(MouseEvent e) {
			this.lastCol = currCol;
			doHover(e);
		}
		public void mouseMoved(MouseEvent e) {
			doHover(e);
		}
		public void mouseExited(MouseEvent e) {
			doHover(e);
			if (popup && this.lastCol != null) {
				currCol = this.lastCol;
				repaint();
				notifyColorChangeListeners(currCol);
			}
		}
		public void mousePressed(MouseEvent e) {
			doClick(e);
		}
		public void mouseClicked(MouseEvent e) {
			doClick(e);
		}
		public void mouseDragged(MouseEvent e) {
			if (popup) doHover(e);
			else doClick(e);
		}
		public void mouseReleased(MouseEvent e) {
			doClick(e);
		}
		private void doHover(MouseEvent e) {
			Rectangle r = calcRect();
			if (r == null) return;
			setToolTipText(palette.layout.name(palette.colors, 0, r, e.getPoint()));
			if (popup) {
				Color c = palette.layout.awtColor(palette.colors, 0, r, e.getPoint());
				if (c != null) {
					currCol = c;
					repaint();
					notifyColorChangeListeners(c);
				}
			}
		}
		private void doClick(MouseEvent e) {
			Rectangle r = calcRect();
			if (r == null) return;
			setToolTipText(palette.layout.name(palette.colors, 0, r, e.getPoint()));
			Color c = palette.layout.awtColor(palette.colors, 0, r, e.getPoint());
			if (c != null) {
				currCol = c;
				repaint();
				notifyColorChangeListeners(c);
			}
		}
	}
}
