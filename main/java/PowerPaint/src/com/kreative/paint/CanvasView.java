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

package com.kreative.paint;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import com.kreative.paint.rcp.CheckerboardPaint;

public class CanvasView extends JComponent implements Scrollable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_BUFFER_SIZE = 4096;

	private Canvas theCanvas;
	private float scale;
	private BufferedImage tmpImg;
	private Graphics2D tmpGr;
	private List<CanvasPaintListener> listeners;
	private Dimension min, pref, max;
	
	public CanvasView(Canvas theCanvas) {
		this.theCanvas = theCanvas;
		this.scale = 1.0f;
		this.tmpImg = new BufferedImage(
				Math.min(MAX_BUFFER_SIZE, theCanvas.getWidth()),
				Math.min(MAX_BUFFER_SIZE, theCanvas.getHeight()),
				BufferedImage.TYPE_INT_ARGB
		);
		this.tmpGr = tmpImg.createGraphics();
		this.listeners = new Vector<CanvasPaintListener>();
		this.min = null;
		this.pref = null;
		this.max = null;
		this.setFocusable(true);
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent me) {
				CanvasView.this.requestFocusInWindow();
			}
			public void mousePressed(MouseEvent me) {
				CanvasView.this.requestFocusInWindow();
			}
		});
	}
	
	public Canvas getCanvas() {
		return theCanvas;
	}
	
	public void setCanvas(Canvas theCanvas) {
		this.theCanvas = theCanvas;
		if (tmpGr != null) tmpGr.dispose();
		this.tmpImg = new BufferedImage(
				Math.min(MAX_BUFFER_SIZE, (int)Math.ceil(theCanvas.getWidth()*scale)),
				Math.min(MAX_BUFFER_SIZE, (int)Math.ceil(theCanvas.getHeight()*scale)),
				BufferedImage.TYPE_INT_ARGB
		);
		this.tmpGr = tmpImg.createGraphics();
		this.revalidate();
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		if (tmpGr != null) tmpGr.dispose();
		this.tmpImg = new BufferedImage(
				Math.min(MAX_BUFFER_SIZE, (int)Math.ceil(theCanvas.getWidth()*scale)),
				Math.min(MAX_BUFFER_SIZE, (int)Math.ceil(theCanvas.getHeight()*scale)),
				BufferedImage.TYPE_INT_ARGB
		);
		this.tmpGr = tmpImg.createGraphics();
		this.revalidate();
	}
	
	protected void finalize() {
		if (tmpGr != null) tmpGr.dispose();
		this.tmpImg = null;
		this.tmpGr = null;
	}
	
	public void addPaintListener(CanvasPaintListener pl) {
		listeners.add(pl);
	}
	
	public void removePaintListener(CanvasPaintListener pl) {
		listeners.remove(pl);
	}
	
	public CanvasPaintListener[] getPaintListeners() {
		return listeners.toArray(new CanvasPaintListener[0]);
	}
	
	public Dimension getMinimumSize() {
		if (min != null) return min;
		else {
			Insets i = this.getInsets();
			Dimension d = theCanvas.getSize();
			return new Dimension((int)(d.width*scale)+i.left+i.right, (int)(d.height*scale)+i.top+i.bottom);
		}
	}
	
	public Dimension getPreferredSize() {
		if (pref != null) return pref;
		else {
			Insets i = this.getInsets();
			Dimension d = theCanvas.getSize();
			return new Dimension((int)(d.width*scale)+i.left+i.right, (int)(d.height*scale)+i.top+i.bottom);
		}
	}
	
	public Dimension getMaximumSize() {
		if (max != null) return max;
		else {
			Insets i = this.getInsets();
			Dimension d = theCanvas.getSize();
			return new Dimension((int)(d.width*scale)+i.left+i.right, (int)(d.height*scale)+i.top+i.bottom);
		}
	}
	
	public void setMinimumSize(Dimension d) {
		min = d;
	}
	
	public void setPreferredSize(Dimension d) {
		pref = d;
	}
	
	public void setMaximumSize(Dimension d) {
		max = d;
	}
	
	private boolean painting = false;
	
	public void paintNow() {
		if (!painting) {
			paintComponent(this.getGraphics());
		}
	}
	
	protected void paintComponent(Graphics g) {
		// see also CanvasController.canvasPainted(Graphics2D g)
		painting = true;
		int bw = Math.min(MAX_BUFFER_SIZE, (int)Math.ceil(theCanvas.getWidth()*scale));
		int bh = Math.min(MAX_BUFFER_SIZE, (int)Math.ceil(theCanvas.getHeight()*scale));
		if (tmpImg == null || tmpImg.getWidth() != bw || tmpImg.getHeight() != bh) {
			if (tmpGr != null) tmpGr.dispose();
			this.tmpImg = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);
			this.tmpGr = tmpImg.createGraphics();
		}
		Rectangle vr = (getParent() instanceof JViewport) ? snap16(((JViewport)getParent()).getViewRect()) : new Rectangle(0, 0, bw, bh);
		int w = Math.min(vr.width, bw);
		int h = Math.min(vr.height, bh);
		tmpGr.setComposite(AlphaComposite.SrcOver);
		tmpGr.setPaint(CheckerboardPaint.LIGHT);
		tmpGr.fillRect(0, 0, w, h);
		if (theCanvas != null) {
			Shape cl = tmpGr.getClip();
			AffineTransform tx = tmpGr.getTransform();
			tmpGr.clipRect(0, 0, w, h);
			tmpGr.translate(-vr.x, -vr.y);
			tmpGr.scale(scale, scale);
			theCanvas.paint(tmpGr);
			for (CanvasPaintListener pl : listeners) {
				pl.canvasPainted(tmpGr);
			}
			tmpGr.setTransform(tx);
			tmpGr.setClip(cl);
		}
		Insets i = this.getInsets();
		int x1 = i.left+vr.x;
		int y1 = i.top+vr.y;
		int x2 = Math.min(x1+w, this.getWidth()-i.right);
		int y2 = Math.min(y1+h, this.getHeight()-i.bottom);
		Shape clip = g.getClip();
		g.clipRect(x1, y1, x2-x1, y2-y1);
		g.drawImage(tmpImg, x1, y1, null);
		g.setClip(clip);
		painting = false;
	}
	
	private Rectangle snap16(Rectangle r) {
		int left = r.x & ~0x0F;
		int top = r.y & ~0x0F;
		int right = r.x+r.width;
		if ((right & 0x0F) != 0) { right |= 0x0F; right++; }
		int bottom = r.y+r.height;
		if ((bottom & 0x0F) != 0) { bottom |= 0x0F; bottom++; }
		return new Rectangle(left, top, right-left, bottom-top);
	}
	
	public Point2D viewCoordinateToCanvasGraphicsCoordinate(Point p) {
		Insets i = this.getInsets();
		return new Point2D.Float(((p.x-i.left)/scale), ((p.y-i.top)/scale));
	}
	
	public Point2D viewCoordinateToLayerGraphicsCoordinate(Point p, Layer l) {
		Insets i = this.getInsets();
		Point loc = l.getLocation();
		return new Point2D.Float(((p.x-i.left)/scale) - loc.x, ((p.y-i.top)/scale) - loc.y);
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return Math.max(1, (int)scale);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		switch (orientation) {
		case SwingConstants.HORIZONTAL:
			return visibleRect.width;
		case SwingConstants.VERTICAL:
		default:
			return visibleRect.height;
		}
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public String toString() {
		return "com.kreative.paint.CanvasView["+theCanvas+","+scale+"]";
	}
}
