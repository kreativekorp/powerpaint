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
import javax.swing.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.util.CursorUtils;

public class ColorMixerPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	private static final int BOX_SIZE = 16;
	private static final int BOX_PSIZE = 64;
	
	private Color myColor = Color.black;
	
	public ColorMixerPanel(PaintContext pc) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		MyColorWell tl = new MyColorWell(Color.black);
		MyColorWell tr = new MyColorWell(Color.white);
		MyColorWell bl = new MyColorWell(Color.red);
		MyColorWell br = new MyColorWell(Color.blue);
		MyGradientStrip tc = new MyGradientStrip(tl, tr, false);
		MyGradientStrip bc = new MyGradientStrip(bl, br, false);
		MyGradientStrip ml = new MyGradientStrip(tl, bl, true);
		MyGradientStrip mr = new MyGradientStrip(tr, br, true);
		MyGradientField mc = new MyGradientField(tl, tr, bl, br);
		tl.setBorder(BorderFactory.createLineBorder(Color.black));
		tr.setBorder(BorderFactory.createLineBorder(Color.black));
		bl.setBorder(BorderFactory.createLineBorder(Color.black));
		br.setBorder(BorderFactory.createLineBorder(Color.black));
		tc.setBorder(BorderFactory.createLineBorder(Color.black));
		bc.setBorder(BorderFactory.createLineBorder(Color.black));
		ml.setBorder(BorderFactory.createLineBorder(Color.black));
		mr.setBorder(BorderFactory.createLineBorder(Color.black));
		mc.setBorder(BorderFactory.createLineBorder(Color.black));
		JPanel t = new JPanel(new BorderLayout(-1,-1));
		t.add(tl, BorderLayout.WEST);
		t.add(tc, BorderLayout.CENTER);
		t.add(tr, BorderLayout.EAST);
		JPanel b = new JPanel(new BorderLayout(-1,-1));
		b.add(bl, BorderLayout.WEST);
		b.add(bc, BorderLayout.CENTER);
		b.add(br, BorderLayout.EAST);
		setLayout(new BorderLayout(-1,-1));
		add(t, BorderLayout.NORTH);
		add(ml, BorderLayout.WEST);
		add(mc, BorderLayout.CENTER);
		add(mr, BorderLayout.EAST);
		add(b, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(100,100));
		update();
	}
	
	public void update() {
		Paint p = pc.getEditedEditedground();
		if (p instanceof Color) {
			myColor = (Color)p;
		}
	}
	
	private class MyColorWell extends JComponent {
		private static final long serialVersionUID = 1L;
		private Color myColor;
		public MyColorWell(Color c) {
			if (c == null) c = Color.white;
			myColor = c;
			Dimension d = new Dimension(BOX_SIZE, BOX_SIZE);
			setMinimumSize(d);
			setPreferredSize(d);
			setMaximumSize(d);
			setCursor(CursorUtils.CURSOR_BUCKET);
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					setColor(ColorMixerPanel.this.myColor);
				}
			});
		}
		public Color getColor() {
			return myColor;
		}
		public void setColor(Color c) {
			if (c == null) c = Color.white;
			myColor = c;
			repaint();
			ColorMixerPanel.this.repaint();
		}
		protected void paintComponent(Graphics g) {
			Insets i = getInsets();
			Dimension s = getSize();
			g.setColor(myColor);
			g.fillRect(i.left, i.top, s.width-i.left-i.right, s.height-i.top-i.bottom);
		}
	}
	
	private class MyGradientStrip extends JComponent {
		private static final long serialVersionUID = 1L;
		private MyColorWell cw1;
		private MyColorWell cw2;
		private boolean vertical;
		public MyGradientStrip(MyColorWell a, MyColorWell b, boolean vert) {
			cw1 = a;
			cw2 = b;
			vertical = vert;
			Dimension d = new Dimension(BOX_SIZE, BOX_SIZE);
			setMinimumSize(d);
			if (vert) d = new Dimension(BOX_SIZE, BOX_PSIZE);
			else d = new Dimension(BOX_PSIZE, BOX_SIZE);
			setPreferredSize(d);
			if (vert) d = new Dimension(BOX_SIZE, getMaximumSize().height);
			else d = new Dimension(getMaximumSize().width, BOX_SIZE);
			setMaximumSize(d);
			setCursor(CursorUtils.CURSOR_DROPPER);
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					ColorMixerPanel.this.pc.setEditedEditedground(getColor(e.getX(), e.getY()));
				}
				public void mouseReleased(MouseEvent e) {
					ColorMixerPanel.this.pc.setEditedEditedground(getColor(e.getX(), e.getY()));
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					ColorMixerPanel.this.pc.setEditedEditedground(getColor(e.getX(), e.getY()));
				}
			});
		}
		public Color getColor(int x, int y) {
			Color c1 = cw1.getColor();
			Color c2 = cw2.getColor();
			Insets i = getInsets();
			Dimension s = getSize();
			if (x < i.left) x = i.left;
			if (x > s.width-i.left-i.right) x = s.width-i.left-i.right;
			if (y < i.top) y = i.top;
			if (y > s.height-i.top-i.bottom) y = s.height-i.top-i.bottom;
			double d;
			if (vertical) {
				d = (double)(y-i.top) / (double)(s.height-i.top-i.bottom);
			} else {
				d = (double)(x-i.left) / (double)(s.width-i.left-i.right);
			}
			return blend(c1, c2, d);
		}
		protected void paintComponent(Graphics g) {
			Color c1 = cw1.getColor();
			Color c2 = cw2.getColor();
			Insets i = getInsets();
			Dimension s = getSize();
			if (vertical) {
				int m = s.height-i.top-i.bottom;
				for (int y=i.top, j=0; y<s.height-i.bottom; y++, j++) {
					g.setColor(blend(c1, c2, (double)j/(double)m));
					g.drawLine(i.left, y, s.width-i.right-1, y);
				}
			} else {
				int m = s.width-i.left-i.right;
				for (int x=i.left, j=0; x<s.width-i.right; x++, j++) {
					g.setColor(blend(c1, c2, (double)j/(double)m));
					g.drawLine(x, i.top, x, s.height-i.bottom-1);
				}
			}
		}
	}
	
	private class MyGradientField extends JComponent {
		private static final long serialVersionUID = 1L;
		private MyColorWell tl;
		private MyColorWell tr;
		private MyColorWell bl;
		private MyColorWell br;
		public MyGradientField(MyColorWell tl, MyColorWell tr, MyColorWell bl, MyColorWell br) {
			this.tl = tl;
			this.tr = tr;
			this.bl = bl;
			this.br = br;
			setMinimumSize(new Dimension(BOX_SIZE, BOX_SIZE));
			setPreferredSize(new Dimension(BOX_PSIZE, BOX_PSIZE));
			setCursor(CursorUtils.CURSOR_DROPPER);
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					ColorMixerPanel.this.pc.setEditedEditedground(getColor(e.getX(), e.getY()));
				}
				public void mouseReleased(MouseEvent e) {
					ColorMixerPanel.this.pc.setEditedEditedground(getColor(e.getX(), e.getY()));
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					ColorMixerPanel.this.pc.setEditedEditedground(getColor(e.getX(), e.getY()));
				}
			});
		}
		public Color getColor(int x, int y) {
			Color tlc = tl.getColor();
			Color trc = tr.getColor();
			Color blc = bl.getColor();
			Color brc = br.getColor();
			Insets i = getInsets();
			Dimension s = getSize();
			if (x < i.left) x = i.left;
			if (x > s.width-i.left-i.right) x = s.width-i.left-i.right;
			if (y < i.top) y = i.top;
			if (y > s.height-i.top-i.bottom) y = s.height-i.top-i.bottom;
			double yd = (double)(y-i.top) / (double)(s.height-i.top-i.bottom);
			double xd = (double)(x-i.left) / (double)(s.width-i.left-i.right);
			Color tc = blend(tlc, trc, xd);
			Color bc = blend(blc, brc, xd);
			return blend(tc, bc, yd);
		}
		protected void paintComponent(Graphics g) {
			Color tlc = tl.getColor();
			Color trc = tr.getColor();
			Color blc = bl.getColor();
			Color brc = br.getColor();
			Insets i = getInsets();
			Dimension s = getSize();
			int ym = s.height-i.top-i.bottom;
			for (int y=i.top, j=0; y<s.height-i.bottom; y++, j++) {
				int xm = s.width-i.left-i.right;
				for (int x=i.left, k=0; x<s.width-i.right; x++, k++) {
					Color tc = blend(tlc, trc, (double)k/(double)xm);
					Color bc = blend(blc, brc, (double)k/(double)xm);
					Color c = blend(tc, bc, (double)j/(double)ym);
					g.setColor(c);
					g.fillRect(x, y, 1, 1);
				}
			}
		}
	}
	
	public static Color blend(Color c1, Color c2, double frac) {
		int a = c1.getAlpha() + (int)((c2.getAlpha()-c1.getAlpha())*frac);
		int r = c1.getRed() + (int)((c2.getRed()-c1.getRed())*frac);
		int g = c1.getGreen() + (int)((c2.getGreen()-c1.getGreen())*frac);
		int b = c1.getBlue() + (int)((c2.getBlue()-c1.getBlue())*frac);
		return new Color(r,g,b,a);
	}
	
	public Color getColor() {
		return myColor;
	}
}
