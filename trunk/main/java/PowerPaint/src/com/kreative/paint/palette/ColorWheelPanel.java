/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.palette;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import com.kreative.paint.PaintContext;
import com.kreative.paint.gradient.AngularGradientShape;
import com.kreative.paint.gradient.GradientColorMap;
import com.kreative.paint.gradient.GradientPaint2;
import com.kreative.paint.gradient.GradientShape;

public class ColorWheelPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	private static Image colorCurs;
	static {
		Class<?> cl = ColorCubePanel.class;
		Toolkit tk = Toolkit.getDefaultToolkit();
		colorCurs = tk.createImage(cl.getResource("ColorCursor.png"));
		tk.prepareImage(colorCurs, -1, -1, null);
	}
	private ColorWheel cw;
	private boolean eventexec = false;
	
	public ColorWheelPanel(PaintContext pc) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		cw = new ColorWheel(this);
		setLayout(new BorderLayout(4,4));
		add(cw, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		update();
	}
	
	public void update() {
		if (!eventexec) {
			eventexec = true;
			Paint p = pc.getEditedEditedground();
			if (p instanceof Color) {
				Color c = (Color)p;
				cw.setColor(c);
			}
			eventexec = false;
		}
	}
	
	private static class ColorWheel extends JComponent {
		private static final long serialVersionUID = 1L;
		private static final GradientShape gs = new AngularGradientShape(0.5, 0.5, 1.0, 0.5, false, false, true);
		private static final GradientColorMap gcm = new GradientColorMap();
		static {
			gcm.put(0.0/6.0, Color.red);
			gcm.put(1.0/6.0, Color.yellow);
			gcm.put(2.0/6.0, Color.green);
			gcm.put(3.0/6.0, Color.cyan);
			gcm.put(4.0/6.0, Color.blue);
			gcm.put(5.0/6.0, Color.magenta);
			gcm.put(6.0/6.0, Color.red);
		}
		private static final GradientPaint2 gp2 = new GradientPaint2(gs, gcm);
		private static final Stroke bs1 = new BasicStroke(1);
		private static final Stroke bs2 = new BasicStroke(2);
		private Color color;
		private float ch, cs, cv, ca;
		public ColorWheel(ColorWheelPanel pp) {
			this.color = Color.black;
			this.ch = 0.0f;
			this.cs = 0.0f;
			this.cv = 0.0f;
			this.ca = 0.0f;
			ColorWheelListener l = new ColorWheelListener(this, pp);
			addMouseListener(l);
			addMouseMotionListener(l);
		}
		/*
		public ColorWheel(Color color, ColorWheelPanel pp) {
			float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			this.color = color;
			this.ch = hsv[0];
			this.cs = hsv[1];
			this.cv = hsv[2];
			this.ca = color.getAlpha()/255.0f;
			ColorWheelListener l = new ColorWheelListener(this, pp);
			addMouseListener(l);
			addMouseMotionListener(l);
		}
		*/
		public Color getColor() {
			return color;
		}
		public void setColor(Color color) {
			float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			this.color = color;
			this.ch = hsv[0];
			this.cs = hsv[1];
			this.cv = hsv[2];
			this.ca = color.getAlpha()/255.0f;
			repaint();
		}
		public Dimension getMinimumSize() {
			Insets i = getInsets();
			return new Dimension(i.left+i.right+100, i.top+i.bottom+100);
		}
		public Dimension getPreferredSize() {
			Insets i = getInsets();
			return new Dimension(i.left+i.right+180, i.top+i.bottom+180);
		}
		protected void paintComponent(Graphics gr) {
			// start
			Insets i = getInsets();
			int w = getWidth()-i.left-i.right;
			int h = getHeight()-i.top-i.bottom;
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			// HUE WHEEL
			// wheel shape
			Area a = new Area(new Ellipse2D.Float(1.0f, 1.0f, w-3.0f, h-3.0f));
			a.subtract(new Area(new Ellipse2D.Float(17.0f, 17.0f, w-35.0f, h-35.0f)));
			// wheel fill
			g.setStroke(bs1); g.setPaint(gp2);
			g.fill(a);
			// wheel boundary
			g.setStroke(bs1); g.setPaint(Color.gray);
			g.drawOval(1, 1, w-3, h-3);
			g.drawOval(17, 17, w-35, h-35);
			// HUE MARKER
			// marker shape
			Shape hm = new Rectangle2D.Float(w-18.0f, h/2.0f-3.0f, 17.0f, 6.0f);
			hm = AffineTransform.getRotateInstance(-ch*Math.PI*2.0, w/2.0, h/2.0).createTransformedShape(hm);
			// marker boundary
			g.setStroke(bs2); g.setPaint(Color.black);
			g.draw(hm);
			// SAT/VAL TRIANGLE
			// center
			Point2D.Float c = new Point2D.Float(i.left + w/2.0f, i.top + h/2.0f);
			// color point
			Point2D.Float cp = new Point2D.Float(c.x + (w/2.0f-30.0f)*(float)Math.cos(ch*Math.PI*2.0), c.y - (w/2.0f-30.0f)*(float)Math.sin(ch*Math.PI*2.0));
			// white point
			Point2D.Float wp = new Point2D.Float(c.x + (w/2.0f-30.0f)*(float)Math.cos(ch*Math.PI*2.0 + Math.PI*2.0/3.0), c.y - (w/2.0f-30.0f)*(float)Math.sin(ch*Math.PI*2.0 + Math.PI*2.0/3.0));
			// black point
			Point2D.Float kp = new Point2D.Float(c.x + (w/2.0f-30.0f)*(float)Math.cos(ch*Math.PI*2.0 - Math.PI*2.0/3.0), c.y - (w/2.0f-30.0f)*(float)Math.sin(ch*Math.PI*2.0 - Math.PI*2.0/3.0));
			// angle of center from black point; all other angles here must be within pi radians of this for things to work right
			double pole = Math.atan2(kp.y-c.y, c.x-kp.x);
			// angle of color point from black point (pa == this implies s = 1.0)
			double ca = Math.atan2(kp.y-cp.y, cp.x-kp.x); if (ca > pole+Math.PI) ca -= Math.PI*2.0; if (ca < pole-Math.PI) ca += Math.PI*2.0;
			// angle of white point from black point (pa == this implies s = 0.0)
			double wa = Math.atan2(kp.y-wp.y, wp.x-kp.x); if (wa > pole+Math.PI) wa -= Math.PI*2.0; if (wa < pole-Math.PI) wa += Math.PI*2.0;
			// distance of black point from tint line (pd == this implies v = 0.0; pd == 0 implies v = 1.0)
			double kd = Math.abs((cp.x-wp.x)*(wp.y-kp.y)-(cp.y-wp.y)*(wp.x-kp.x))/Math.hypot(cp.x-wp.x, cp.y-wp.y);
			// triangle shape
			GeneralPath tri = new GeneralPath();
			tri.moveTo(cp.x, cp.y);
			tri.lineTo(wp.x, wp.y);
			tri.lineTo(kp.x, kp.y);
			tri.closePath();
			// triangle fill
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if (tri.contains(x, y)) {
						// angle of this point from black point (this == ca implies s = 1.0; this == wa implies s = 0.0)
						double pa = Math.atan2(kp.y-y, x-kp.x); if (pa > pole+Math.PI) pa -= Math.PI*2.0; if (pa < pole-Math.PI) pa += Math.PI*2.0;
						// distance of this point from tint line (this == kd implies v = 0.0; this == 0 implies v = 1.0)
						double pd = ((cp.y-wp.y)*(wp.x-x)-(cp.x-wp.x)*(wp.y-y))/Math.hypot(cp.x-wp.x, cp.y-wp.y);
						// saturation of this point
						double s = (pa-wa)/(ca-wa);
						if (Double.isNaN(s) || Double.isInfinite(s)) s = 0.0;
						if (s < 0.0) s = 0.0; if (s > 1.0) s = 1.0;
						// value of this point
						double v = 1.0 - pd/kd;
						if (Double.isNaN(v) || Double.isInfinite(v)) v = 0.0;
						if (v < 0.0) v = 0.0; if (v > 1.0) v = 1.0;
						// draw point
						bi.setRGB(x, y, Color.HSBtoRGB(ch, (float)s, (float)v) | 0xFF000000);
					}
				}
			}
			// triangle boundary
			g.setStroke(bs1); g.setPaint(Color.gray);
			g.draw(tri);
			// color point visual
			g.setStroke(bs1); g.setPaint(new Color(Color.HSBtoRGB(ch, 1, 1)));
			g.fill(new Ellipse2D.Float(cp.x-3, cp.y-3, 6, 6));
			// white point visual
			g.setStroke(bs1); g.setPaint(Color.white);
			g.fill(new Ellipse2D.Float(wp.x-3, wp.y-3, 6, 6));
			// black point visual
			g.setStroke(bs1); g.setPaint(Color.black);
			g.fill(new Ellipse2D.Float(kp.x-3, kp.y-3, 6, 6));
			// SAT/VAL MARKER
			// angle of current point from black point
			double pa = cs * (ca-wa) + wa;
			// x coordinate of current point projected onto tint line from black point
			double px = wp.x + (cp.x-wp.x) * cs;
			// y coordinate of current point projected onto tint line from black point
			double py = wp.y + (cp.y-wp.y) * cs;
			// distance of black point from current point projected onto tint line
			double pd = Math.hypot(kp.y-py, px-kp.x);
			double x = kp.x + pd * cv * Math.cos(pa);
			double y = kp.y - pd * cv * Math.sin(pa);
			// marker boundary
			g.drawImage(colorCurs, (int)x-3, (int)y-3, null);
			// finish
			g.dispose();
			gr.drawImage(bi, i.left, i.top, null);
		}
		public Color clickColor(int mx, int my) {
			// start
			Insets i = getInsets();
			int w = getWidth()-i.left-i.right;
			int h = getHeight()-i.top-i.bottom;
			double cx = i.left + w/2.0;
			double cy = i.top + h/2.0;
			double mr = Math.hypot(cy-my, mx-cx);
			double ma = Math.atan2(cy-my, mx-cx);
			if (mr >= w/2-18) {
				// HUE
				// set color
				ch = (float)(ma/(Math.PI*2.0));
				while (ch < 0.0) ch += 1.0;
				while (ch > 1.0) ch -= 1.0;
				color = new Color((Color.HSBtoRGB(ch, cs, cv) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
				repaint();
				return color;
			} else {
				// SAT/VAL
				// center
				Point2D.Float c = new Point2D.Float(i.left + w/2.0f, i.top + h/2.0f);
				// color point
				Point2D.Float cp = new Point2D.Float(c.x + (w/2.0f-30.0f)*(float)Math.cos(ch*Math.PI*2.0), c.y - (w/2.0f-30.0f)*(float)Math.sin(ch*Math.PI*2.0));
				// white point
				Point2D.Float wp = new Point2D.Float(c.x + (w/2.0f-30.0f)*(float)Math.cos(ch*Math.PI*2.0 + Math.PI*2.0/3.0), c.y - (w/2.0f-30.0f)*(float)Math.sin(ch*Math.PI*2.0 + Math.PI*2.0/3.0));
				// black point
				Point2D.Float kp = new Point2D.Float(c.x + (w/2.0f-30.0f)*(float)Math.cos(ch*Math.PI*2.0 - Math.PI*2.0/3.0), c.y - (w/2.0f-30.0f)*(float)Math.sin(ch*Math.PI*2.0 - Math.PI*2.0/3.0));
				// angle of center from black point; all other angles here must be within pi radians of this for things to work right
				double pole = Math.atan2(kp.y-c.y, c.x-kp.x);
				// angle of color point from black point (pa == this implies s = 1.0)
				double ca = Math.atan2(kp.y-cp.y, cp.x-kp.x); if (ca > pole+Math.PI) ca -= Math.PI*2.0; if (ca < pole-Math.PI) ca += Math.PI*2.0;
				// angle of white point from black point (pa == this implies s = 0.0)
				double wa = Math.atan2(kp.y-wp.y, wp.x-kp.x); if (wa > pole+Math.PI) wa -= Math.PI*2.0; if (wa < pole-Math.PI) wa += Math.PI*2.0;
				// distance of black point from tint line (pd == this implies v = 0.0; pd == 0 implies v = 1.0)
				double kd = Math.abs((cp.x-wp.x)*(wp.y-kp.y)-(cp.y-wp.y)*(wp.x-kp.x))/Math.hypot(cp.x-wp.x, cp.y-wp.y);
				// angle of this point from black point (this == ca implies s = 1.0; this == wa implies s = 0.0)
				double pa = Math.atan2(kp.y-my, mx-kp.x); if (pa > pole+Math.PI) pa -= Math.PI*2.0; if (pa < pole-Math.PI) pa += Math.PI*2.0;
				// distance of this point from tint line (this == kd implies v = 0.0; this == 0 implies v = 1.0)
				double pd = ((cp.y-wp.y)*(wp.x-mx)-(cp.x-wp.x)*(wp.y-my))/Math.hypot(cp.x-wp.x, cp.y-wp.y);
				// saturation of this point
				double s = (pa-wa)/(ca-wa);
				if (Double.isNaN(s) || Double.isInfinite(s)) s = 0.0;
				if (s < 0.0) s = 0.0; if (s > 1.0) s = 1.0;
				// value of this point
				double v = 1.0 - pd/kd;
				if (Double.isNaN(v) || Double.isInfinite(v)) v = 0.0;
				if (v < 0.0) v = 0.0; if (v > 1.0) v = 1.0;
				// set color
				cs = (float)s;
				cv = (float)v;
				color = new Color((Color.HSBtoRGB(ch, cs, cv) & 0xFFFFFF) | ((int)Math.round(this.ca*255.0) << 24), true);
				repaint();
				return color;
			}
		}
	}
	
	private static class ColorWheelListener implements MouseListener, MouseMotionListener {
		private ColorWheel cs;
		private ColorWheelPanel pp;
		public ColorWheelListener(ColorWheel cp, ColorWheelPanel pp) {
			this.cs = cp;
			this.pp = pp;
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				pp.pc.setEditedEditedground(cs.clickColor(e.getX(), e.getY()));
				pp.eventexec = false;
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				pp.pc.setEditedEditedground(cs.clickColor(e.getX(), e.getY()));
				pp.eventexec = false;
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				pp.pc.setEditedEditedground(cs.clickColor(e.getX(), e.getY()));
				pp.eventexec = false;
			}
		}
		public void mouseMoved(MouseEvent e) {}
	}
	
	public Color getColor() {
		return cw.getColor();
	}
}
