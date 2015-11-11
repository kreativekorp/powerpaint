package com.kreative.paint.palette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.kreative.paint.rcp.CheckerboardPaint;
import com.kreative.paint.rcp.ColorChangeEvent;
import com.kreative.paint.rcp.ColorChangeListener;
import com.kreative.paint.util.ColorModel;
import com.kreative.paint.util.ColorModel.ColorChannel;

public class ColorCubeModelComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	
	public static final int SLIDER_WIDTH = 20;
	private static final Image colorCurs;
	static {
		Class<?> cl = ColorCubeModelComponent.class;
		Toolkit tk = Toolkit.getDefaultToolkit();
		colorCurs = tk.createImage(cl.getResource("ColorCursor.png"));
		tk.prepareImage(colorCurs, -1, -1, null);
	}
	
	private final ColorModel cm;
	private final ColorChannel[] cd;
	private final List<ColorChangeListener> listeners;
	private Color color;
	private float[] cc;
	private int xci, yci;
	private final int numSliders;
	
	public ColorCubeModelComponent(ColorModel cm) {
		this.cm = cm;
		this.cd = cm.getChannels();
		this.listeners = new ArrayList<ColorChangeListener>();
		this.color = Color.black;
		this.cc = cm.unmakeColor(Color.black, null);
		this.xci = (cd.length < 3) ? 0 : 1;
		this.yci = (cd.length < 2) ? 0 : (cd.length < 3) ? 1 : 2;
		this.numSliders = (cd.length < 3) ? 0 : (cd.length - 2);
		InternalMouseListener ml = new InternalMouseListener();
		this.addMouseListener(ml);
		this.addMouseMotionListener(ml);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		this.cc = cm.unmakeColor(color, cc);
		notifyColorChangeListeners(color);
		repaint();
	}
	
	public int getChannelCount() {
		return cd.length;
	}
	
	public ColorChannel getChannel(int i) {
		return cd[i];
	}
	
	public float getChannelValue(int i) {
		return cc[i];
	}
	
	public int getSelectedIndexX() {
		return xci;
	}
	
	public int getSelectedIndexY() {
		return yci;
	}
	
	public ColorChannel getSelectedChannelX() {
		return cd[xci];
	}
	
	public ColorChannel getSelectedChannelY() {
		return cd[yci];
	}
	
	public ColorChannel[] getSliderChannels() {
		ColorChannel[] sd = new ColorChannel[numSliders];
		for (int ci = 0, si = 0; ci < cd.length && si < numSliders; ci++) {
			if (ci == xci) continue;
			if (ci == yci) continue;
			sd[si] = cd[ci];
			si++;
		}
		return sd;
	}
	
	public void setSelectedIndexX(int xci) {
		if (cd.length < 2) return;
		xci %= cd.length;
		if (xci == this.xci) return;
		if (xci == this.yci) this.yci = this.xci;
		this.xci = xci;
		repaint();
	}
	
	public void setSelectedIndexY(int yci) {
		if (cd.length < 2) return;
		yci %= cd.length;
		if (yci == this.yci) return;
		if (yci == this.xci) this.xci = this.yci;
		this.yci = yci;
		repaint();
	}
	
	@Override
	public Dimension getMinimumSize() {
		Insets i = getInsets();
		int w = 32 + numSliders * SLIDER_WIDTH + i.left + i.right;
		int h = 32 + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	public Dimension getPreferredSize() {
		Insets i = getInsets();
		int w = 128 + numSliders * SLIDER_WIDTH + i.left + i.right;
		int h = 128 + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		int rw = w - numSliders * SLIDER_WIDTH;
		float[] ncc = new float[cd.length];
		for (int j = 0; j < cd.length; j++) ncc[j] = cc[j];
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		// Paint Rectangular Area
		g.setPaint(Color.gray);
		g.fillRect(0, 0, rw, h);
		for (int sy = 1; sy < h - 1; sy++) {
			float yv = ((float)(sy - 1) / (float)(h - 3));
			ncc[yci] = yv * (cd[yci].max - cd[yci].min) + cd[yci].min;
			for (int sx = 1; sx < rw - 1; sx++) {
				float xv = ((float)(sx - 1) / (float)(rw - 3));
				ncc[xci] = xv * (cd[xci].max - cd[xci].min) + cd[xci].min;
				Color nc = cm.makeColor(ncc);
				// Do the alpha blending with the checkerboard pattern manually
				// because g.fillRect() performs much better without alpha.
				int nca = nc.getAlpha();
				int oc = (((sx & 8) == (sy & 8)) ? 0xFF : 0xCC) * (255 - nca);
				int ncr = (nc.getRed() * nca + oc) / 255;
				int ncg = (nc.getGreen() * nca + oc) / 255;
				int ncb = (nc.getBlue() * nca + oc) / 255;
				g.setPaint(new Color(ncr, ncg, ncb));
				g.fillRect(sx, sy, 1, 1);
			}
			ncc[xci] = cc[xci];
		}
		ncc[yci] = cc[yci];
		float xv = (cc[xci] - cd[xci].min) / (cd[xci].max - cd[xci].min);
		float yv = (cc[yci] - cd[yci].min) / (cd[yci].max - cd[yci].min);
		if (xv < 0) xv = 0; if (xv > 1) xv = 1;
		if (yv < 0) yv = 0; if (yv > 1) yv = 1;
		int cx = (int)Math.round((rw - 3) * xv) - 2;
		int cy = (int)Math.round((h - 3) * yv) - 2;
		g.drawImage(colorCurs, cx, cy, null);
		// Paint Sliders
		for (int ci = 0, x = rw + SLIDER_WIDTH - 10; ci < cd.length; ci++) {
			if (ci == xci) continue;
			if (ci == yci) continue;
			g.setPaint(Color.gray);
			g.fillRect(x, 0, 10, h);
			g.setPaint(CheckerboardPaint.LIGHT);
			g.fillRect(x + 1, 1, 8, h - 2);
			float v = (cc[ci] - cd[ci].min) / (cd[ci].max - cd[ci].min);
			if (v < 0) v = 0; if (v > 1) v = 1;
			int ay = (int)Math.round((h - 3) * v) - 2;
			g.drawImage(PaletteUtilities.ARROW_4R, x - 4, ay, null);
			for (int sy = 1; sy < h - 1; sy++) {
				v = ((float)(sy - 1) / (float)(h - 3));
				ncc[ci] = v * (cd[ci].max - cd[ci].min) + cd[ci].min;
				g.setPaint(cm.makeColor(ncc));
				g.fillRect(x + 1, sy, 8, 1);
			}
			ncc[ci] = cc[ci];
			x += SLIDER_WIDTH;
		}
		g.dispose();
		gr.drawImage(bi, i.left, i.top, null);
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
	
	private void notifyColorChangeListeners(Color c) {
		ColorChangeEvent e = new ColorChangeEvent(this, c);
		for (ColorChangeListener l : listeners) l.colorChanged(e);
	}
	
	private int sliderIndexToChannelIndex(int si) {
		if (si < 0) return -1;
		for (int ci = 0; ci < cd.length; ci++) {
			if (ci == xci) continue;
			if (ci == yci) continue;
			if (si == 0) return ci;
			si--;
		}
		return cd.length;
	}
	
	private boolean doClick(int mx, int my) {
		Insets i = getInsets();
		int x = i.left;
		int y = i.top;
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		int rw = w - numSliders * SLIDER_WIDTH;
		if (mx > x + rw) {
			int ci = sliderIndexToChannelIndex((mx - x - rw) / SLIDER_WIDTH);
			if (ci < 0 || ci >= cd.length) return true;
			float v = (float)(my - y - 1) / (float)(h - 3);
			v *= (cd[ci].max - cd[ci].min);
			v += cd[ci].min;
			v = Math.round(v / cd[ci].step) * cd[ci].step;
			if (v < cd[ci].min) v = cd[ci].min;
			if (v > cd[ci].max) v = cd[ci].max;
			cc[ci] = v;
			color = cm.makeColor(cc);
			notifyColorChangeListeners(color);
			repaint();
			return true;
		} else {
			float xv = (float)(mx - x - 1) / (float)(rw - 3);
			float yv = (float)(my - y - 1) / (float)(h - 3);
			xv *= (cd[xci].max - cd[xci].min);
			yv *= (cd[yci].max - cd[yci].min);
			xv += cd[xci].min;
			yv += cd[yci].min;
			xv = Math.round(xv / cd[xci].step) * cd[xci].step;
			yv = Math.round(yv / cd[yci].step) * cd[yci].step;
			if (xv < cd[xci].min) xv = cd[xci].min;
			if (xv > cd[xci].max) xv = cd[xci].max;
			if (yv < cd[yci].min) yv = cd[yci].min;
			if (yv > cd[yci].max) yv = cd[yci].max;
			cc[xci] = xv;
			cc[yci] = yv;
			color = cm.makeColor(cc);
			notifyColorChangeListeners(color);
			repaint();
			return false;
		}
	}
	
	private class InternalMouseListener implements MouseListener, MouseMotionListener {
		private boolean xlock;
		private int x;
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseMoved(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
		@Override public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {
			if (xlock = doClick(e.getX(), e.getY())) x = e.getX();
			else x = getWidth() - getInsets().right - numSliders * SLIDER_WIDTH;
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			doClick(((xlock || e.getX() > x) ? x : e.getX()), e.getY());
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			doClick(((xlock || e.getX() > x) ? x : e.getX()), e.getY());
		}
	}
}
