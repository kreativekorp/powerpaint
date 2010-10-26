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
import java.awt.image.BufferedImage;
import javax.swing.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.awt.CheckerboardPaint;
import com.kreative.paint.util.SwingUtils;

public class ColorSliderPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	private ColorSlider cs;
	private JLabel rl, gl, bl, hl, sl, vl, al;
	private boolean eventexec = false;
	
	public ColorSliderPanel(PaintContext pc) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		cs = new ColorSlider(this);
		JPanel lp = new JPanel(new GridLayout(7,1));
		lp.add(shrink(new JLabel("R"), true));
		lp.add(shrink(new JLabel("G"), true));
		lp.add(shrink(new JLabel("B"), true));
		lp.add(shrink(new JLabel("H"), true));
		lp.add(shrink(new JLabel("S"), true));
		lp.add(shrink(new JLabel("V"), true));
		lp.add(shrink(new JLabel("A"), true));
		JPanel vp = new JPanel(new GridLayout(7,1));
		vp.add(shrink(rl = new JLabel("255"), false));
		vp.add(shrink(gl = new JLabel("255"), false));
		vp.add(shrink(bl = new JLabel("255"), false));
		vp.add(shrink(hl = new JLabel("255"), false));
		vp.add(shrink(sl = new JLabel("255"), false));
		vp.add(shrink(vl = new JLabel("255"), false));
		vp.add(shrink(al = new JLabel("255"), false));
		setLayout(new BorderLayout(4,4));
		add(lp, BorderLayout.LINE_START);
		add(cs, BorderLayout.CENTER);
		add(vp, BorderLayout.LINE_END);
		setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		update();
	}
	
	public void update() {
		if (!eventexec) {
			eventexec = true;
			Paint p = pc.getEditedEditedground();
			if (p instanceof Color) {
				Color c = (Color)p;
				cs.setColor(c);
				updateLabels(c);
			}
			eventexec = false;
		}
	}
	
	private void updateLabels(Color c) {
		rl.setText(Integer.toString(c.getRed()));
		gl.setText(Integer.toString(c.getGreen()));
		bl.setText(Integer.toString(c.getBlue()));
		float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		hl.setText(Integer.toString((int)Math.round(hsv[0]*360)));
		sl.setText(Integer.toString((int)Math.round(hsv[1]*100)));
		vl.setText(Integer.toString((int)Math.round(hsv[2]*100)));
		al.setText(Integer.toString(c.getAlpha()));
	}
	
	private static class ColorSlider extends JComponent {
		private static final long serialVersionUID = 1L;
		private Color color;
		private float cr, cg, cb, ch, cs, cv, ca;
		public ColorSlider(ColorSliderPanel pp) {
			this.color = Color.black;
			this.cr = 0.0f;
			this.cg = 0.0f;
			this.cb = 0.0f;
			this.ch = 0.0f;
			this.cs = 0.0f;
			this.cv = 0.0f;
			this.ca = 0.0f;
			ColorSliderListener l = new ColorSliderListener(this, pp);
			addMouseListener(l);
			addMouseMotionListener(l);
		}
		/*
		public ColorSlider(Color color, ColorSliderPanel pp) {
			float[] rgb = color.getRGBColorComponents(null);
			float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			this.color = color;
			this.cr = rgb[0];
			this.cg = rgb[1];
			this.cb = rgb[2];
			this.ch = hsv[0];
			this.cs = hsv[1];
			this.cv = hsv[2];
			this.ca = color.getAlpha()/255.0f;
			ColorSliderListener l = new ColorSliderListener(this, pp);
			addMouseListener(l);
			addMouseMotionListener(l);
		}
		*/
		public Color getColor() {
			return color;
		}
		public void setColor(Color color) {
			float[] rgb = color.getRGBColorComponents(null);
			float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			this.color = color;
			this.cr = rgb[0];
			this.cg = rgb[1];
			this.cb = rgb[2];
			this.ch = hsv[0];
			this.cs = hsv[1];
			this.cv = hsv[2];
			this.ca = color.getAlpha()/255.0f;
			repaint();
		}
		public Dimension getMinimumSize() {
			Insets i = getInsets();
			return new Dimension(258+i.left+i.right, 140+i.top+i.bottom);
		}
		public Dimension getPreferredSize() {
			Insets i = getInsets();
			return new Dimension(258+i.left+i.right, 140+i.top+i.bottom);
		}
		protected void paintComponent(Graphics gr) {
			Insets i = getInsets();
			int w = getWidth()-i.left-i.right;
			int h = getHeight()-i.top-i.bottom;
			int sh = h/7;
			int sy = (sh-14)/2;
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setPaint(Color.gray);
			g.fillRect(0, sy+sh*0, w, 10);
			g.fillRect(0, sy+sh*1, w, 10);
			g.fillRect(0, sy+sh*2, w, 10);
			g.fillRect(0, sy+sh*3, w, 10);
			g.fillRect(0, sy+sh*4, w, 10);
			g.fillRect(0, sy+sh*5, w, 10);
			g.fillRect(0, sy+sh*6, w, 10);
			g.setPaint(CheckerboardPaint.LIGHT);
			g.fillRect(1, sy+sh*0+1, w-2, 8);
			g.fillRect(1, sy+sh*1+1, w-2, 8);
			g.fillRect(1, sy+sh*2+1, w-2, 8);
			g.fillRect(1, sy+sh*3+1, w-2, 8);
			g.fillRect(1, sy+sh*4+1, w-2, 8);
			g.fillRect(1, sy+sh*5+1, w-2, 8);
			g.fillRect(1, sy+sh*6+1, w-2, 8);
			g.drawImage(PaletteUtilities.ARROW_4U, 1+(int)Math.round((w-3)*cr)-3, sy+sh*0+10, null);
			g.drawImage(PaletteUtilities.ARROW_4U, 1+(int)Math.round((w-3)*cg)-3, sy+sh*1+10, null);
			g.drawImage(PaletteUtilities.ARROW_4U, 1+(int)Math.round((w-3)*cb)-3, sy+sh*2+10, null);
			g.drawImage(PaletteUtilities.ARROW_4U, 1+(int)Math.round((w-3)*ch)-3, sy+sh*3+10, null);
			g.drawImage(PaletteUtilities.ARROW_4U, 1+(int)Math.round((w-3)*cs)-3, sy+sh*4+10, null);
			g.drawImage(PaletteUtilities.ARROW_4U, 1+(int)Math.round((w-3)*cv)-3, sy+sh*5+10, null);
			g.drawImage(PaletteUtilities.ARROW_4U, 1+(int)Math.round((w-3)*ca)-3, sy+sh*6+10, null);
			for (int sx = 1; sx < w-1; sx++) {
				float v = (float)(sx-1)/(float)(w-3);
				g.setPaint(new Color(v, cg, cb, ca));
				g.fillRect(sx, sy+sh*0+1, 1, 8);
				g.setPaint(new Color(cr, v, cb, ca));
				g.fillRect(sx, sy+sh*1+1, 1, 8);
				g.setPaint(new Color(cr, cg, v, ca));
				g.fillRect(sx, sy+sh*2+1, 1, 8);
				g.setPaint(new Color((Color.HSBtoRGB(v, cs, cv) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true));
				g.fillRect(sx, sy+sh*3+1, 1, 8);
				g.setPaint(new Color((Color.HSBtoRGB(ch, v, cv) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true));
				g.fillRect(sx, sy+sh*4+1, 1, 8);
				g.setPaint(new Color((Color.HSBtoRGB(ch, cs, v) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true));
				g.fillRect(sx, sy+sh*5+1, 1, 8);
				g.setPaint(new Color(cr, cg, cb, v));
				g.fillRect(sx, sy+sh*6+1, 1, 8);
			}
			g.dispose();
			gr.drawImage(bi, i.left, i.top, null);
		}
		public Color clickColor(int mx, int my) {
			Insets i = getInsets();
			int x = i.left;
			int y = i.top;
			int w = getWidth()-i.left-i.right;
			int h = getHeight()-i.top-i.bottom;
			int sh = h/7;
			float v = (float)(mx-x-1)/(float)(w-3);
			if (v < 0.0f) v = 0.0f;
			if (v > 1.0f) v = 1.0f;
			switch ((my-y)/sh) {
			case 0: {
				cr = v;
				color = new Color(v, cg, cb, ca);
				float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
				repaint();
				return color;
				}
			case 1: {
				cg = v;
				color = new Color(cr, v, cb, ca);
				float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
				repaint();
				return color;
				}
			case 2: {
				cb = v;
				color = new Color(cr, cg, v, ca);
				float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
				repaint();
				return color;
				}
			case 3: {
				ch = v;
				color = new Color((Color.HSBtoRGB(v, cs, cv) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
				float[] rgb = color.getRGBColorComponents(null);
				cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
				repaint();
				return color;
				}
			case 4: {
				cs = v;
				color = new Color((Color.HSBtoRGB(ch, v, cv) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
				float[] rgb = color.getRGBColorComponents(null);
				cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
				repaint();
				return color;
				}
			case 5: {
				cv = v;
				color = new Color((Color.HSBtoRGB(ch, cs, v) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
				float[] rgb = color.getRGBColorComponents(null);
				cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
				repaint();
				return color;
				}
			case 6: {
				ca = v;
				color = new Color(cr, cg, cb, v);
				repaint();
				return color;
				}
			default: return color;
			}
		}
	}
	
	private static class ColorSliderListener implements MouseListener, MouseMotionListener {
		private ColorSlider cs;
		private ColorSliderPanel pp;
		private int y;
		public ColorSliderListener(ColorSlider cp, ColorSliderPanel pp) {
			this.cs = cp;
			this.pp = pp;
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				y = e.getY();
				pp.pc.setEditedEditedground(cs.clickColor(e.getX(), y));
				pp.updateLabels(cs.color);
				pp.eventexec = false;
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				pp.pc.setEditedEditedground(cs.clickColor(e.getX(), y));
				pp.updateLabels(cs.color);
				pp.eventexec = false;
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				pp.pc.setEditedEditedground(cs.clickColor(e.getX(), y));
				pp.updateLabels(cs.color);
				pp.eventexec = false;
			}
		}
		public void mouseMoved(MouseEvent e) {}
	}
	
	public Color getColor() {
		return cs.getColor();
	}
	
	private static <C extends JComponent> C shrink(C c, boolean center) {
		SwingUtils.shrink(c);
		if (center && c instanceof JLabel) {
			JLabel l = (JLabel)c;
			l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setHorizontalTextPosition(JLabel.CENTER);
		}
		c.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		return c;
	}
}
