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

package com.kreative.paint.palette;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.util.SwingUtils;

public class ColorCubePanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	private static Image colorCurs;
	static {
		Class<?> cl = ColorCubePanel.class;
		Toolkit tk = Toolkit.getDefaultToolkit();
		colorCurs = tk.createImage(cl.getResource("ColorCursor.png"));
		tk.prepareImage(colorCurs, -1, -1, null);
	}
	private ColorCube cp;
	private boolean eventexec = false;
	
	public ColorCubePanel(PaintContext pc) {
		super(pc, CHANGED_PAINT|CHANGED_EDITING);
		cp = new ColorCube(this);
		ButtonGroup cbg = new ButtonGroup();
		JPanel cbp = new JPanel(new GridLayout(6,1,0,0));
		JRadioButton cr = SwingUtils.shrink(new JRadioButton("R")); cbg.add(cr); cbp.add(cr);
		JRadioButton cg = SwingUtils.shrink(new JRadioButton("G")); cbg.add(cg); cbp.add(cg);
		JRadioButton cb = SwingUtils.shrink(new JRadioButton("B")); cbg.add(cb); cbp.add(cb);
		JRadioButton ch = SwingUtils.shrink(new JRadioButton("H")); cbg.add(ch); cbp.add(ch);
		JRadioButton cs = SwingUtils.shrink(new JRadioButton("S")); cbg.add(cs); cbp.add(cs);
		JRadioButton cv = SwingUtils.shrink(new JRadioButton("V")); cbg.add(cv); cbp.add(cv);
		ch.setSelected(true);
		cr.addActionListener(new ColorChannelListener(cp, ColorChannel.RED));
		cg.addActionListener(new ColorChannelListener(cp, ColorChannel.GREEN));
		cb.addActionListener(new ColorChannelListener(cp, ColorChannel.BLUE));
		ch.addActionListener(new ColorChannelListener(cp, ColorChannel.HUE));
		cs.addActionListener(new ColorChannelListener(cp, ColorChannel.SATURATION));
		cv.addActionListener(new ColorChannelListener(cp, ColorChannel.VALUE));
		setLayout(new BorderLayout(4,4));
		add(cp, BorderLayout.CENTER);
		add(cbp, BorderLayout.EAST);
		setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		update();
	}
	
	public void update() {
		if (!eventexec) {
			eventexec = true;
			Paint p = pc.getEditedEditedground();
			if (p instanceof Color) {
				Color c = (Color)p;
				cp.setColor(c);
			}
			eventexec = false;
		}
	}
	
	private static enum ColorChannel {
		RED, GREEN, BLUE, HUE, SATURATION, VALUE
	}
	
	private static class ColorChannelListener implements ActionListener {
		private ColorCube cp;
		private ColorChannel cc;
		public ColorChannelListener(ColorCube cp, ColorChannel cc) {
			this.cp = cp; this.cc = cc;
		}
		public void actionPerformed(ActionEvent e) {
			cp.setColorChannel(cc);
		}
	}
	
	private static class ColorCube extends JComponent {
		private static final long serialVersionUID = 1L;
		private Color color;
		private float cr, cg, cb, ch, cs, cv, ca;
		private ColorChannel channel;
		public ColorCube(ColorCubePanel pp) {
			this.color = Color.black;
			this.cr = 0.0f;
			this.cg = 0.0f;
			this.cb = 0.0f;
			this.ch = 0.0f;
			this.cs = 0.0f;
			this.cv = 0.0f;
			this.ca = 0.0f;
			this.channel = ColorChannel.HUE;
			ColorCubeListener l = new ColorCubeListener(this, pp);
			addMouseListener(l);
			addMouseMotionListener(l);
		}
		/*
		public ColorCube(Color color, ColorChannel channel, ColorCubePanel pp) {
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
			this.channel = channel;
			ColorCubeListener l = new ColorCubeListener(this, pp);
			addMouseListener(l);
			addMouseMotionListener(l);
		}
		*/
		public Color getColor() {
			return color;
		}
		/*
		public ColorChannel getColorChannel() {
			return channel;
		}
		*/
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
		public void setColorChannel(ColorChannel channel) {
			this.channel = channel;
			repaint();
		}
		public Dimension getMinimumSize() {
			Insets i = getInsets();
			return new Dimension(32+15+i.left+i.right, 32+i.top+i.bottom);
		}
		public Dimension getPreferredSize() {
			Insets i = getInsets();
			return new Dimension(128+15+i.left+i.right, 128+i.top+i.bottom);
		}
		protected void paintComponent(Graphics gr) {
			Insets i = getInsets();
			int w = getWidth()-i.left-i.right;
			int h = getHeight()-i.top-i.bottom;
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setColor(Color.gray);
			g.fillRect(0, 0, w-15, h);
			g.fillRect(w-10, 0, 10, h);
			switch (channel) {
			case RED:
				g.drawImage(PaletteUtilities.ARROW_4R, w-14, 1+(int)Math.round((h-3)*cr)-3, null);
				for (int dy = 1; dy < h-1; dy++) {
					g.setColor(new Color((float)(dy-1)/(float)(h-3), cg, cb));
					g.fillRect(w-9, dy, 8, 1);
					for (int dx = 1; dx < w-16; dx++) {
						bi.setRGB(dx, dy, new Color(cr, (float)(dx-1)/(float)(w-18), (float)(dy-1)/(float)(h-3)).getRGB());
					}
				}
				g.drawImage(colorCurs, 1+(int)Math.round((w-18)*cg)-3, 1+(int)Math.round((h-3)*cb)-3, null);
				break;
			case GREEN:
				g.drawImage(PaletteUtilities.ARROW_4R, w-14, 1+(int)Math.round((h-3)*cg)-3, null);
				for (int dy = 1; dy < h-1; dy++) {
					g.setColor(new Color(cr, (float)(dy-1)/(float)(h-3), cb));
					g.fillRect(w-9, dy, 8, 1);
					for (int dx = 1; dx < w-16; dx++) {
						bi.setRGB(dx, dy, new Color((float)(dx-1)/(float)(w-18), cg, (float)(dy-1)/(float)(h-3)).getRGB());
					}
				}
				g.drawImage(colorCurs, 1+(int)Math.round((w-18)*cr)-3, 1+(int)Math.round((h-3)*cb)-3, null);
				break;
			case BLUE:
				g.drawImage(PaletteUtilities.ARROW_4R, w-14, 1+(int)Math.round((h-3)*cb)-3, null);
				for (int dy = 1; dy < h-1; dy++) {
					g.setColor(new Color(cr, cg, (float)(dy-1)/(float)(h-3)));
					g.fillRect(w-9, dy, 8, 1);
					for (int dx = 1; dx < w-16; dx++) {
						bi.setRGB(dx, dy, new Color((float)(dx-1)/(float)(w-18), (float)(dy-1)/(float)(h-3), cb).getRGB());
					}
				}
				g.drawImage(colorCurs, 1+(int)Math.round((w-18)*cr)-3, 1+(int)Math.round((h-3)*cg)-3, null);
				break;
			case HUE:
				g.drawImage(PaletteUtilities.ARROW_4R, w-14, 1+(int)Math.round((h-3)*ch)-3, null);
				for (int dy = 1; dy < h-1; dy++) {
					g.setColor(new Color(Color.HSBtoRGB((float)(dy-1)/(float)(h-3), cs, cv)));
					g.fillRect(w-9, dy, 8, 1);
					for (int dx = 1; dx < w-16; dx++) {
						bi.setRGB(dx, dy, new Color(Color.HSBtoRGB(ch, (float)(dx-1)/(float)(w-18), (float)(dy-1)/(float)(h-3))).getRGB());
					}
				}
				g.drawImage(colorCurs, 1+(int)Math.round((w-18)*cs)-3, 1+(int)Math.round((h-3)*cv)-3, null);
				break;
			case SATURATION:
				g.drawImage(PaletteUtilities.ARROW_4R, w-14, 1+(int)Math.round((h-3)*cs)-3, null);
				for (int dy = 1; dy < h-1; dy++) {
					g.setColor(new Color(Color.HSBtoRGB(ch, (float)(dy-1)/(float)(h-3), cv)));
					g.fillRect(w-9, dy, 8, 1);
					for (int dx = 1; dx < w-16; dx++) {
						bi.setRGB(dx, dy, new Color(Color.HSBtoRGB((float)(dx-1)/(float)(w-18), cs, (float)(dy-1)/(float)(h-3))).getRGB());
					}
				}
				g.drawImage(colorCurs, 1+(int)Math.round((w-18)*ch)-3, 1+(int)Math.round((h-3)*cv)-3, null);
				break;
			case VALUE:
				g.drawImage(PaletteUtilities.ARROW_4R, w-14, 1+(int)Math.round((h-3)*cv)-3, null);
				for (int dy = 1; dy < h-1; dy++) {
					g.setColor(new Color(Color.HSBtoRGB(ch, cs, (float)(dy-1)/(float)(h-3))));
					g.fillRect(w-9, dy, 8, 1);
					for (int dx = 1; dx < w-16; dx++) {
						bi.setRGB(dx, dy, new Color(Color.HSBtoRGB((float)(dx-1)/(float)(w-18), (float)(dy-1)/(float)(h-3), cv)).getRGB());
					}
				}
				g.drawImage(colorCurs, 1+(int)Math.round((w-18)*ch)-3, 1+(int)Math.round((h-3)*cs)-3, null);
				break;
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
			if (mx > x+w-15) {
				float ay = (float)(my-y-1)/(float)(h-3); if (ay < 0.0f) ay = 0.0f; if (ay > 1.0f) ay = 1.0f;
				switch (channel) {
				case RED: {
					cr = ay;
					color = new Color(ay, cg, cb, ca);
					float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
					ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
					repaint();
					return color;
					}
				case GREEN: {
					cg = ay;
					color = new Color(cr, ay, cb, ca);
					float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
					ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
					repaint();
					return color;
					}
				case BLUE: {
					cb = ay;
					color = new Color(cr, cg, ay, ca);
					float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
					ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
					repaint();
					return color;
					}
				case HUE: {
					ch = ay;
					color = new Color((Color.HSBtoRGB(ay, cs, cv) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
					float[] rgb = color.getRGBColorComponents(null);
					cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
					repaint();
					return color;
					}
				case SATURATION: {
					cs = ay;
					color = new Color((Color.HSBtoRGB(ch, ay, cv) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
					float[] rgb = color.getRGBColorComponents(null);
					cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
					repaint();
					return color;
					}
				case VALUE: {
					cv = ay;
					color = new Color((Color.HSBtoRGB(ch, cs, ay) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
					float[] rgb = color.getRGBColorComponents(null);
					cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
					repaint();
					return color;
					}
				default: return color;
				}
			} else {
				float ax = (float)(mx-x-1)/(float)(w-18); if (ax < 0.0f) ax = 0.0f; if (ax > 1.0f) ax = 1.0f;
				float ay = (float)(my-y-1)/(float)(h-3); if (ay < 0.0f) ay = 0.0f; if (ay > 1.0f) ay = 1.0f;
				switch (channel) {
				case RED: {
					cg = ax; cb = ay;
					color = new Color(cr, ax, ay, ca);
					float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
					ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
					repaint();
					return color;
					}
				case GREEN: {
					cr = ax; cb = ay;
					color = new Color(ax, cg, ay, ca);
					float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
					ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
					repaint();
					return color;
					}
				case BLUE: {
					cr = ax; cg = ay;
					color = new Color(ax, ay, cb, ca);
					float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
					ch = hsv[0]; cs = hsv[1]; cv = hsv[2];
					repaint();
					return color;
					}
				case HUE: {
					cs = ax; cv = ay;
					color = new Color((Color.HSBtoRGB(ch, ax, ay) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
					float[] rgb = color.getRGBColorComponents(null);
					cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
					repaint();
					return color;
					}
				case SATURATION: {
					ch = ax; cv = ay;
					color = new Color((Color.HSBtoRGB(ax, cs, ay) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
					float[] rgb = color.getRGBColorComponents(null);
					cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
					repaint();
					return color;
					}
				case VALUE: {
					ch = ax; cs = ay;
					color = new Color((Color.HSBtoRGB(ax, ay, cv) & 0xFFFFFF) | ((int)Math.round(ca*255.0) << 24), true);
					float[] rgb = color.getRGBColorComponents(null);
					cr = rgb[0]; cg = rgb[1]; cb = rgb[2];
					repaint();
					return color;
					}
				default: return color;
				}
			}
		}
	}
	
	private static class ColorCubeListener implements MouseListener, MouseMotionListener {
		private ColorCube cp;
		private ColorCubePanel pp;
		public ColorCubeListener(ColorCube cp, ColorCubePanel pp) {
			this.cp = cp;
			this.pp = pp;
		}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				pp.pc.setEditedEditedground(cp.clickColor(e.getX(), e.getY()));
				pp.eventexec = false;
			}
		}
		public void mouseReleased(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				pp.pc.setEditedEditedground(cp.clickColor(e.getX(), e.getY()));
				pp.eventexec = false;
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (!pp.eventexec) {
				pp.eventexec = true;
				pp.pc.setEditedEditedground(cp.clickColor(e.getX(), e.getY()));
				pp.eventexec = false;
			}
		}
		public void mouseMoved(MouseEvent e) {}
	}
	
	public Color getColor() {
		return cp.getColor();
	}
}
