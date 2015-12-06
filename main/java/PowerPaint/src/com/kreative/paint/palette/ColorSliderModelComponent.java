package com.kreative.paint.palette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import com.kreative.paint.material.colorpalette.CheckerboardPaint;
import com.kreative.paint.material.colorpalette.ColorChangeEvent;
import com.kreative.paint.material.colorpalette.ColorChangeListener;
import com.kreative.paint.util.ColorModel;
import com.kreative.paint.util.ColorModel.ColorChannel;

public class ColorSliderModelComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private final ColorModel cm;
	private final ColorChannel[] cd;
	private final List<ColorChangeListener> listeners;
	private Color color;
	private float[] cc;
	
	public ColorSliderModelComponent(ColorModel cm) {
		this.cm = cm;
		this.cd = cm.getChannels();
		this.listeners = new ArrayList<ColorChangeListener>();
		this.color = Color.black;
		this.cc = cm.unmakeColor(Color.black, null);
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
	
	@Override
	public Dimension getMinimumSize() {
		Insets i = getInsets();
		int w = 258 + i.left + i.right;
		int h = (cd.length * 20) + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	public Dimension getPreferredSize() {
		Insets i = getInsets();
		int w = 258 + i.left + i.right;
		int h = (cd.length * 20) + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		int sh = h / cd.length;
		int sy = (sh - 14) / 2;
		float[] ncc = new float[cd.length];
		for (int j = 0; j < cd.length; j++) ncc[j] = cc[j];
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		for (int ci = 0; ci < cd.length; ci++) {
			g.setPaint(Color.gray);
			g.fillRect(0, sh * ci + sy, w, 10);
			g.setPaint(CheckerboardPaint.LIGHT);
			g.fillRect(1, sh * ci + sy + 1, w - 2, 8);
			float v = (cc[ci] - cd[ci].min) / (cd[ci].max - cd[ci].min);
			if (v < 0) v = 0; if (v > 1) v = 1;
			int ax = (int)Math.round((w - 3) * v) - 2;
			int ay = sh * ci + sy + 10;
			g.drawImage(PaletteUtilities.ARROW_4U, ax, ay, null);
			for (int sx = 1; sx < w - 1; sx++) {
				v = ((float)(sx - 1) / (float)(w - 3));
				ncc[ci] = v * (cd[ci].max - cd[ci].min) + cd[ci].min;
				g.setPaint(cm.makeColor(ncc));
				g.fillRect(sx, sh * ci + sy + 1, 1, 8);
			}
			ncc[ci] = cc[ci];
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
	
	private void doClick(int mx, int my) {
		Insets i = getInsets();
		int x = i.left;
		int y = i.top;
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		int sh = h / cd.length;
		int ci = (my - y) / sh;
		if (ci < 0 || ci >= cd.length) return;
		float v = (float)(mx - x - 1) / (float)(w - 3);
		v *= (cd[ci].max - cd[ci].min);
		v += cd[ci].min;
		v = Math.round(v / cd[ci].step) * cd[ci].step;
		if (v < cd[ci].min) v = cd[ci].min;
		if (v > cd[ci].max) v = cd[ci].max;
		cc[ci] = v;
		color = cm.makeColor(cc);
		notifyColorChangeListeners(color);
		repaint();
	}
	
	private class InternalMouseListener implements MouseListener, MouseMotionListener {
		private int y;
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseMoved(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
		@Override public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {
			doClick(e.getX(), y = e.getY());
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			doClick(e.getX(), y);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			doClick(e.getX(), y);
		}
	}
}
