/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

public class RCPComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	
	public static final int ORIENTATION_AUTO = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;
	public static final int ORIENTATION_SQUARE = 2;
	public static final int ORIENTATION_VERTICAL = 3;
	
	private RCPSwatch swatch;
	private int orientation;
	private Color currcol;
	private Dimension minsz, prefsz, maxsz;
	private HashSet<ColorChangeListener> myListeners = new HashSet<ColorChangeListener>();
	
	public RCPComponent() {
		this.swatch = null;
		this.orientation = ORIENTATION_AUTO;
		this.currcol = null;
		this.minsz = this.prefsz = this.maxsz = null;
		InternalMouseListener iml = new InternalMouseListener(false);
		this.addMouseListener(iml);
		this.addMouseMotionListener(iml);
	}
	
	public RCPComponent(RCPSwatch swatch) {
		this.swatch = swatch;
		this.orientation = ORIENTATION_AUTO;
		this.currcol = null;
		this.minsz = this.prefsz = this.maxsz = null;
		InternalMouseListener iml = new InternalMouseListener(false);
		this.addMouseListener(iml);
		this.addMouseMotionListener(iml);
	}
	
	public RCPComponent(RCPSwatch swatch, int orientation) {
		this.swatch = swatch;
		this.orientation = orientation;
		this.currcol = null;
		this.minsz = this.prefsz = this.maxsz = null;
		InternalMouseListener iml = new InternalMouseListener(false);
		this.addMouseListener(iml);
		this.addMouseMotionListener(iml);
	}

	public RCPComponent(RCPSwatch swatch, Color color) {
		this.swatch = swatch;
		this.orientation = ORIENTATION_AUTO;
		this.currcol = color;
		this.minsz = this.prefsz = this.maxsz = null;
		InternalMouseListener iml = new InternalMouseListener(false);
		this.addMouseListener(iml);
		this.addMouseMotionListener(iml);
	}

	public RCPComponent(RCPSwatch swatch, int orientation, Color color) {
		this.swatch = swatch;
		this.orientation = orientation;
		this.currcol = color;
		this.minsz = this.prefsz = this.maxsz = null;
		InternalMouseListener iml = new InternalMouseListener(false);
		this.addMouseListener(iml);
		this.addMouseMotionListener(iml);
	}
	
	public RCPComponent(RCPSwatch swatch, int orientation, Color color, boolean popup) {
		this.swatch = swatch;
		this.orientation = orientation;
		this.currcol = color;
		this.minsz = this.prefsz = this.maxsz = null;
		InternalMouseListener iml = new InternalMouseListener(popup);
		this.addMouseListener(iml);
		this.addMouseMotionListener(iml);
	}
	
	public RCPComponent asPopup() {
		RCPComponent pop = new RCPComponent(swatch, orientation, currcol, true);
		pop.minsz = this.minsz;
		pop.prefsz = this.prefsz;
		pop.maxsz = this.maxsz;
		pop.myListeners.addAll(this.myListeners);
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
	
	public RCPSwatch getSwatch() {
		return swatch;
	}
	
	public void setSwatch(RCPSwatch swatch) {
		this.swatch = swatch;
		repaint();
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	public void setOrientation(int orientation) {
		this.orientation = orientation;
		repaint();
	}
	
	public Color getSelectedColor() {
		return currcol;
	}
	
	public void setSelectedColor(Color color) {
		this.currcol = color;
		repaint();
	}
	
	public void addColorChangeListener(ColorChangeListener l) {
		myListeners.add(l);
	}
	
	public void removeColorChangeListener(ColorChangeListener l) {
		myListeners.remove(l);
	}
	
	public ColorChangeListener[] getColorChangeListeners() {
		return myListeners.toArray(new ColorChangeListener[0]);
	}
	
	protected void notifyColorChangeListeners(Color c) {
		ColorChangeEvent ee = new ColorChangeEvent(this, c);
		for (ColorChangeListener ll : myListeners) ll.colorChanged(ee);
	}
	
	private Dimension getXSize() {
		Insets i = getInsets();
		Dimension d;
		switch (orientation) {
		case ORIENTATION_HORIZONTAL:
			d = swatch.getPreferredHorizontalSize();
			if (d == null) d = RCPSwatch.DEFAULT_HORIZ_SIZE;
			break;
		case ORIENTATION_SQUARE:
			d = swatch.getPreferredSquareSize();
			if (d == null) d = RCPSwatch.DEFAULT_SQUARE_SIZE;
			break;
		case ORIENTATION_VERTICAL:
			d = swatch.getPreferredVerticalSize();
			if (d == null) d = RCPSwatch.DEFAULT_VERT_SIZE;
			break;
		default:
			d = swatch.getPreferredSize();
			if (d == null) d = swatch.getPreferredHorizontalSize();
			if (d == null) d = swatch.getPreferredSquareSize();
			if (d == null) d = swatch.getPreferredVerticalSize();
			if (d == null) d = RCPSwatch.DEFAULT_HORIZ_SIZE;
			break;
		}
		return new Dimension(i.left + d.width + i.right, i.top + d.height + i.bottom);
	}
	
	public Dimension getMinimumSize() {
		if (minsz != null) return minsz;
		else if (swatch == null) return super.getMinimumSize();
		else return getXSize();
	}
	
	public Dimension getPreferredSize() {
		if (prefsz != null) return prefsz;
		else if (swatch == null) return super.getPreferredSize();
		else return getXSize();
	}
	
	public Dimension getMaximumSize() {
		if (maxsz != null) return maxsz;
		else if (swatch == null) return super.getMaximumSize();
		else return getXSize();
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
	
	protected void paintComponent(Graphics g) {
		if (swatch != null) {
			Insets i = getInsets();
			Dimension d = getSize();
			Rectangle r = new Rectangle(i.left, i.top, d.width-i.left-i.right, d.height-i.top-i.bottom);
			swatch.paint(g, r, currcol);
		}
	}
	
	private class InternalMouseListener implements MouseListener, MouseMotionListener {
		private boolean popup;
		private Color last;
		public InternalMouseListener(boolean popup) {
			this.popup = popup;
			this.last = currcol;
		}
		public void mouseEntered(MouseEvent e) {
			this.last = currcol;
			doHover(e);
		}
		public void mouseMoved(MouseEvent e) {
			doHover(e);
		}
		public void mouseExited(MouseEvent e) {
			doHover(e);
			if (popup && this.last != null) {
				currcol = this.last;
				repaint();
				notifyColorChangeListeners(currcol);
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
			if (swatch != null) {
				Insets i = getInsets();
				Dimension d = getSize();
				Rectangle r = new Rectangle(i.left, i.top, d.width-i.left-i.right, d.height-i.top-i.bottom);
				setToolTipText(swatch.getName(r, e.getPoint()));
				if (popup) {
					Color c = swatch.getColor(r, e.getPoint());
					if (c != null) {
						currcol = c;
						repaint();
						notifyColorChangeListeners(c);
					}
				}
			}
		}
		private void doClick(MouseEvent e) {
			if (swatch != null) {
				Insets i = getInsets();
				Dimension d = getSize();
				Rectangle r = new Rectangle(i.left, i.top, d.width-i.left-i.right, d.height-i.top-i.bottom);
				setToolTipText(swatch.getName(r, e.getPoint()));
				Color c = swatch.getColor(r, e.getPoint());
				if (c != null) {
					this.last = currcol = c;
					repaint();
					notifyColorChangeListeners(c);
				}
			}
		}
	}
}
