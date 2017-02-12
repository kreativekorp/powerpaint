package com.kreative.paint.palette;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import com.kreative.paint.material.colorpalette.ColorChangeEvent;
import com.kreative.paint.material.colorpalette.ColorChangeListener;
import com.kreative.paint.material.gradient.GradientPaint2;

public class ColorWheelComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private static final Stroke bs1 = new BasicStroke(1);
	private static final Stroke bs2 = new BasicStroke(2);
	private static final Image colorCurs;
	static {
		Class<?> cl = ColorWheelComponent.class;
		Toolkit tk = Toolkit.getDefaultToolkit();
		colorCurs = tk.createImage(cl.getResource("ColorCursor.png"));
		tk.prepareImage(colorCurs, -1, -1, null);
	}
	
	private final List<ColorChangeListener> listeners;
	private Color color;
	private float[] cc;
	
	public ColorWheelComponent() {
		this.listeners = new ArrayList<ColorChangeListener>();
		this.color = Color.black;
		this.cc = new float[4];
		InternalMouseListener ml = new InternalMouseListener();
		this.addMouseListener(ml);
		this.addMouseMotionListener(ml);
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), cc);
		cc[3] = color.getAlpha() / 255f;
		notifyColorChangeListeners(color);
		repaint();
	}
	
	@Override
	public Dimension getMinimumSize() {
		Insets i = getInsets();
		int w = 100 + i.left + i.right;
		int h = 100 + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	public Dimension getPreferredSize() {
		Insets i = getInsets();
		int w = 180 + i.left + i.right;
		int h = 180 + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		// construct marker & triangle
		double cx = w / 2.0;
		double cy = h / 2.0;
		double acc = cc[0] * Math.PI * 2.0;
		double a120 = Math.PI * 2.0 / 3.0;
		AffineTransform rot = AffineTransform.getRotateInstance(-acc, cx, cy);
		Shape marker = new Rectangle2D.Float(w - 18f, h / 2f - 3f, 17f, 6f);
		Shape hm = rot.createTransformedShape(marker);
		float cpx = (float)(cx + (cx - 30) * Math.cos(acc));
		float cpy = (float)(cy - (cy - 30) * Math.sin(acc));
		float wpx = (float)(cx + (cx - 30) * Math.cos(acc + a120));
		float wpy = (float)(cy - (cy - 30) * Math.sin(acc + a120));
		float kpx = (float)(cx + (cx - 30) * Math.cos(acc - a120));
		float kpy = (float)(cy - (cy - 30) * Math.sin(acc - a120));
		GeneralPath tri = new GeneralPath();
		tri.moveTo(cpx, cpy);
		tri.lineTo(wpx, wpy);
		tri.lineTo(kpx, kpy);
		tri.closePath();
		double pole = Math.atan2(kpy - cy, cx - kpx);
		double ca = Math.atan2(kpy - cpy, cpx - kpx);
		if (ca > pole + Math.PI) ca -= Math.PI * 2.0;
		if (ca < pole - Math.PI) ca += Math.PI * 2.0;
		double wa = Math.atan2(kpy - wpy, wpx - kpx);
		if (wa > pole + Math.PI) wa -= Math.PI * 2.0;
		if (wa < pole - Math.PI) wa += Math.PI * 2.0;
		double kd =
			Math.abs((cpx - wpx) * (wpy - kpy) - (cpy - wpy) * (wpx - kpx)) /
			Math.hypot(cpx - wpx, cpy - wpy);
		// create buffer
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		// paint wheel & triangle
		g.drawImage(makeWheel(w, h), null, 0, 0);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (tri.contains(x, y)) {
					double pa = Math.atan2(kpy - y, x - kpx);
					if (pa > pole + Math.PI) pa -= Math.PI * 2.0;
					if (pa < pole - Math.PI) pa += Math.PI * 2.0;
					double pd =
						((cpy - wpy) * (wpx - x) - (cpx - wpx) * (wpy - y)) /
						Math.hypot(cpx - wpx, cpy - wpy);
					double s = (pa - wa) / (ca - wa);
					if (Double.isNaN(s) || Double.isInfinite(s)) s = 0;
					if (s < 0) s = 0; if (s > 1) s = 1;
					double v = 1 - pd / kd;
					if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;
					if (v < 0) v = 0; if (v > 1) v = 1;
					g.setPaint(Color.getHSBColor(cc[0], (float)s, (float)v));
					g.fillRect(x, y, 1, 1);
				}
			}
		}
		// paint markers
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setStroke(bs2);
		g.setPaint(Color.black);
		g.draw(hm);
		g.setStroke(bs1);
		g.setPaint(Color.gray);
		g.draw(tri);
		g.setPaint(Color.getHSBColor(cc[0], 1, 1));
		g.fill(new Ellipse2D.Float(cpx - 3, cpy - 3, 6, 6));
		g.setPaint(Color.white);
		g.fill(new Ellipse2D.Float(wpx - 3, wpy - 3, 6, 6));
		g.setPaint(Color.black);
		g.fill(new Ellipse2D.Float(kpx - 3, kpy - 3, 6, 6));
		// triangle marker
		double pa = cc[1] * (ca - wa) + wa;
		double px = wpx + (cpx - wpx) * cc[1];
		double py = wpy + (cpy - wpy) * cc[1];
		double pd = Math.hypot(kpy - py, px - kpx);
		double x = kpx + pd * cc[2] * Math.cos(pa);
		double y = kpy - pd * cc[2] * Math.sin(pa);
		g.drawImage(colorCurs, (int)x - 3, (int)y - 3, null);
		// flush buffer
		g.dispose();
		gr.drawImage(bi, i.left, i.top, null);
	}
	
	private BufferedImage wheelCache = null;
	private BufferedImage makeWheel(int w, int h) {
		if (wheelCache == null || wheelCache.getWidth() != w || wheelCache.getHeight() != h) {
			wheelCache = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = wheelCache.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Area a = new Area(new Ellipse2D.Float(1f, 1f, w - 3f, h - 3f));
			a.subtract(new Area(new Ellipse2D.Float(17f, 17f, w - 35f, h - 35f)));
			g.setStroke(bs1);
			g.setPaint(GradientPaint2.RGB_WHEEL);
			g.fill(a);
			g.setPaint(Color.gray);
			g.drawOval(1, 1, w - 3, h - 3);
			g.drawOval(17, 17, w - 35, h - 35);
			g.dispose();
		}
		return wheelCache;
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
	
	private int doClick(int mx, int my, int region) {
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		float cx = i.left + w / 2f;
		float cy = i.top + h / 2f;
		double mr = Math.hypot(cy - my, mx - cx);
		double ma = Math.atan2(cy - my, mx - cx);
		if (region == 0) {
			region = (mr >= w / 2 - 20) ? 1 : 2;
		}
		if (region == 1) {
			cc[0] = (float)(ma / (Math.PI * 2.0));
			while (cc[0] < 0) cc[0] += 1;
			while (cc[0] > 1) cc[0] -= 1;
		}
		if (region == 2) {
			float cpx = cx + (w / 2f - 30f) * (float)Math.cos(cc[0] * Math.PI * 2.0);
			float cpy = cy - (w / 2f - 30f) * (float)Math.sin(cc[0] * Math.PI * 2.0);
			float wpx = cx + (w / 2f - 30f) * (float)Math.cos(cc[0] * Math.PI * 2.0 + Math.PI * 2.0/3.0);
			float wpy = cy - (w / 2f - 30f) * (float)Math.sin(cc[0] * Math.PI * 2.0 + Math.PI * 2.0/3.0);
			float kpx = cx + (w / 2f - 30f) * (float)Math.cos(cc[0] * Math.PI * 2.0 - Math.PI * 2.0/3.0);
			float kpy = cy - (w / 2f - 30f) * (float)Math.sin(cc[0] * Math.PI * 2.0 - Math.PI * 2.0/3.0);
			double pole = Math.atan2(kpy - cy, cx - kpx);
			double ca = Math.atan2(kpy - cpy, cpx - kpx);
			if (ca > pole + Math.PI) ca -= Math.PI * 2.0;
			if (ca < pole - Math.PI) ca += Math.PI * 2.0;
			double wa = Math.atan2(kpy - wpy, wpx - kpx);
			if (wa > pole + Math.PI) wa -= Math.PI * 2.0;
			if (wa < pole - Math.PI) wa += Math.PI * 2.0;
			double kd =
				Math.abs((cpx - wpx) * (wpy - kpy) - (cpy - wpy) * (wpx - kpx)) /
				Math.hypot(cpx - wpx, cpy - wpy);
			double pa = Math.atan2(kpy - my, mx - kpx);
			if (pa > pole + Math.PI) pa -= Math.PI * 2.0;
			if (pa < pole - Math.PI) pa += Math.PI * 2.0;
			double pd =
				((cpy - wpy) * (wpx - mx) - (cpx - wpx) * (wpy - my)) /
				Math.hypot(cpx - wpx, cpy - wpy);
			double s = (pa - wa) / (ca - wa);
			if (Double.isNaN(s) || Double.isInfinite(s)) s = 0;
			if (s < 0) s = 0; if (s > 1) s = 1;
			double v = 1 - pd / kd;
			if (Double.isNaN(v) || Double.isInfinite(v)) v = 0;
			if (v < 0) v = 0; if (v > 1) v = 1;
			cc[1] = (float)s;
			cc[2] = (float)v;
		}
		int rgb = Color.HSBtoRGB(cc[0], cc[1], cc[2]) & 0xFFFFFF;
		rgb |= ((int)Math.round(cc[3] * 255f) << 24);
		color = new Color(rgb, true);
		notifyColorChangeListeners(color);
		repaint();
		return region;
	}
	
	private class InternalMouseListener implements MouseListener, MouseMotionListener {
		private int region;
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseMoved(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
		@Override public void mouseClicked(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {
			region = doClick(e.getX(), e.getY(), 0);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			doClick(e.getX(), e.getY(), region);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			doClick(e.getX(), e.getY(), region);
		}
	}
}
