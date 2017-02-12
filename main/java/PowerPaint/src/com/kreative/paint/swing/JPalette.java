/*
 * Copyright &copy; 2007-2017 Rebecca G. Bettencourt / Kreative Software
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
 * @since KJL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class JPalette extends JFrame {
	private static final long serialVersionUID = 1L;
	private static boolean isMacOS;
	static {
		try {
			String osName = System.getProperty("os.name");
			isMacOS = (osName.toUpperCase().contains("MAC OS"));
		} catch (Exception e) {
			isMacOS = false;
		}
	}
	
	private JPanel main = null;
	private JPanel titlebar = null;
	private JLabel titlelabel = null;
	private Container contentpane = null;
	private JPanel resizefooter = null;
	
	private boolean jplte_visible = super.isVisible();
	private boolean jplte_obscured = false;
	private boolean jplte_lock = false;
	
	public JPalette() {
		makeGUI();
		makeVT();
	}
	
	public JPalette(String title) {
		super.setTitle(title);
		makeGUI();
		makeVT();
	}
	
	private void makeGUI() {
		if (isMacOS) {
			setAlwaysOnTop(true);
			setFocusable(false);
			setFocusableWindowState(false);
			getRootPane().putClientProperty("Window.style", "small");
			setMinimumSize(new Dimension(16, 16));
			
			main = new JPanel(new BorderLayout());
			titlebar = null;
			titlelabel = null;
			contentpane = super.getContentPane();
			resizefooter = new JPanel(new BorderLayout());
			
			resizefooter.add(new PaletteResizeBox(this), BorderLayout.EAST);
			resizefooter.setVisible(super.isResizable());
			
			main.add(contentpane, BorderLayout.CENTER);
			main.add(resizefooter, BorderLayout.SOUTH);
			super.setContentPane(main);
		} else {
			setAlwaysOnTop(true);
			setFocusable(false);
			setFocusableWindowState(false);
			setUndecorated(true);
			setMinimumSize(new Dimension(16, 16));
			
			main = new JPanel(new BorderLayout());
			titlebar = new JPanel(new BorderLayout()) {
				private static final long serialVersionUID = 1L;
				protected void paintComponent(Graphics g) {
					Insets i = getInsets();
					int x = i.left;
					int y = i.top;
					int w = getWidth() - i.left - i.right;
					int h = getHeight() - i.top - i.bottom;
					((Graphics2D)g).setPaint(new GradientPaint(x, y, new Color(0xFF0A246A), x+w, y, new Color(0xFFA6CAF0)));
					g.fillRect(x, y, w, h);
				}
			};
			titlelabel = new PaletteTitleLabel(this, super.getTitle());
			contentpane = super.getContentPane();
			resizefooter = null;
			
			titlebar.setOpaque(true);
			titlebar.setBackground(Color.blue.darker());
			titlebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
			titlelabel.setFont(titlelabel.getFont().deriveFont(Font.BOLD, 11.0f));
			titlelabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
			titlelabel.setForeground(Color.white);
			PaletteCloseBox c = new PaletteCloseBox(this);
			c.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			titlebar.add(titlelabel, BorderLayout.CENTER);
			titlebar.add(c, BorderLayout.EAST);
			
			main.add(titlebar, BorderLayout.NORTH);
			main.add(contentpane, BorderLayout.CENTER);
			Border inner = BorderFactory.createEmptyBorder(2, 2, 2, 2);
			Border outer = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.lightGray, Color.white, Color.darkGray, Color.gray);
			main.setBorder(BorderFactory.createCompoundBorder(outer, inner));
			super.setContentPane(main);
		}
	}
	
	private void makeVT() {
		this.jplte_visible = super.isVisible();
		this.jplte_obscured = false;
		this.jplte_lock = false;
		getToolkit().addAWTEventListener(new BoundAWTListener(), AWTEvent.WINDOW_EVENT_MASK | AWTEvent.WINDOW_FOCUS_EVENT_MASK);
	}
	
	private synchronized void setObscured(boolean obscured) {
		if (!jplte_lock) {
			jplte_lock = true;
			if (obscured) {
				this.jplte_obscured = true;
				super.setVisible(false);
			} else {
				super.setVisible(this.jplte_visible);
				this.jplte_obscured = false;
			}
			jplte_lock = false;
		}
	}
	
	// overwriting this stuff is not for the faint of heart;
	// if not done properly, the results can be disastrous
	
	public synchronized boolean isVisible() {
		return this.jplte_visible;
	}
	
	public synchronized void setVisible(boolean visible) {
		if (!jplte_lock) {
			jplte_lock = true;
			this.jplte_visible = visible;
			if (!jplte_obscured) super.setVisible(visible);
			jplte_lock = false;
		} else {
			super.setVisible(visible);
		}
	}
	
	@Deprecated
	public synchronized void show() {
		if (!jplte_lock) {
			jplte_lock = true;
			this.jplte_visible = true;
			if (!jplte_obscured) super.show();
			jplte_lock = false;
		} else {
			super.show();
		}
	}
	
	@Deprecated
	public synchronized void hide() {
		if (!jplte_lock) {
			jplte_lock = true;
			this.jplte_visible = false;
			if (!jplte_obscured) super.hide();
			jplte_lock = false;
		} else {
			super.hide();
		}
	}
	
	@Override
	public Container getContentPane() {
		return (main != null) ? contentpane : super.getContentPane();
	}
	
	@Override
	public void setContentPane(Container c) {
		if (main != null) {
			main.remove(contentpane);
			contentpane = c;
			main.add(contentpane, BorderLayout.CENTER);
		} else {
			super.setContentPane(c);
		}
	}
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		if (titlelabel != null) {
			titlelabel.setText(title);
		}
	}
	
	@Override
	public Component add(Component c) {
		return (main != null) ? contentpane.add(c) : super.add(c);
	}
	
	@Override
	public Component add(Component c, int index) {
		return (main != null) ? contentpane.add(c,index) : super.add(c,index);
	}
	
	@Override
	public void add(Component c, Object constraints) {
		if (main != null) contentpane.add(c,constraints); else super.add(c,constraints);
	}
	
	@Override
	public void add(Component c, Object constraints, int index) {
		if (main != null) contentpane.add(c,constraints,index); else super.add(c,constraints,index);
	}
	
	@Override
	public Component add(String name, Component c) {
		return (main != null) ? contentpane.add(name,c) : super.add(name,c);
	}
	
	@Override
	public void remove(Component c) {
		if (main != null) contentpane.remove(c); else super.remove(c);
	}
	
	@Override
	public void remove(int index) {
		if (main != null) contentpane.remove(index); else super.remove(index);
	}
	
	@Override
	public void removeAll() {
		if (main != null) contentpane.removeAll(); else super.removeAll();
	}
	
	@Override
	public LayoutManager getLayout() {
		return (main != null) ? contentpane.getLayout() : super.getLayout();
	}
	
	@Override
	public void setLayout(LayoutManager lm) {
		if (main != null) contentpane.setLayout(lm); else super.setLayout(lm);
	}
	
	@Override
	public void setResizable(boolean b) {
		super.setResizable(b);
		if (resizefooter != null) {
			resizefooter.setVisible(b);
		}
	}
	
	private class BoundAWTListener implements AWTEventListener {
		public void eventDispatched(AWTEvent e) {
			if (e instanceof WindowEvent) {
				switch (e.getID()) {
				case WindowEvent.WINDOW_ACTIVATED:
				case WindowEvent.WINDOW_GAINED_FOCUS:
					setObscured(false);
					break;
				case WindowEvent.WINDOW_DEACTIVATED:
				case WindowEvent.WINDOW_LOST_FOCUS:
					if (((WindowEvent)e).getOppositeWindow() == null) setObscured(true);
					break;
				}
			}
		}
	}
	
	private static class PaletteTitleLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private Window whatToMove;
		private int ox, oy;
		public PaletteTitleLabel(Window what, String s) {
			super(s);
			whatToMove = what;
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					Point p = MouseInfo.getPointerInfo().getLocation();
					ox = p.x-whatToMove.getX();
					oy = p.y-whatToMove.getY();
				}
			});
			this.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					Point p = MouseInfo.getPointerInfo().getLocation();
					int nx = p.x-ox;
					int ny = p.y-oy;
					whatToMove.setLocation(nx, ny);
				}
			});
		}
	}
	
	private static class PaletteCloseBox extends JLabel {
		private static final long serialVersionUID = 1L;
		private Window whatToClose;
		public PaletteCloseBox(Window what) {
			super(new ImageIcon(CLOSE_WIDGET));
			whatToClose = what;
			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					EventQueue eq = whatToClose.getToolkit().getSystemEventQueue();
	                eq.postEvent(new WindowEvent(whatToClose, WindowEvent.WINDOW_CLOSING));
				}
			});
		}
	}
	
	private static class PaletteResizeBox extends JLabel {
		private static final long serialVersionUID = 1L;
		private Window whatToResize;
		private int ox, oy;
		public PaletteResizeBox(Window what) {
			super(new ImageIcon(RESIZE_WIDGET));
			whatToResize = what;
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					Point p = MouseInfo.getPointerInfo().getLocation();
					ox = p.x-whatToResize.getWidth();
					oy = p.y-whatToResize.getHeight();
				}
			});
			this.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					Point p = MouseInfo.getPointerInfo().getLocation();
					int nx = Math.max(32, p.x-ox);
					int ny = Math.max(32, p.y-oy);
					whatToResize.setSize(nx, ny);
				}
			});
		}
	}
	
	private static final Image CLOSE_WIDGET;
	private static final Image RESIZE_WIDGET;
	static {
		Toolkit tk = Toolkit.getDefaultToolkit();
		CLOSE_WIDGET = tk.createImage(new byte[] {
			-119,80,78,71,13,10,26,10,0,0,0,13,73,72,68,82,
			0,0,0,13,0,0,0,13,8,6,0,0,0,114,-21,-28,
			124,0,0,0,66,73,68,65,84,120,-38,99,96,32,7,-4,
			39,17,56,56,56,-4,7,107,-70,114,-31,4,81,-72,-95,-95,
			-127,74,-102,64,-82,5,97,92,124,-100,54,-63,20,-94,107,32,
			-24,60,108,26,-88,107,19,-39,126,-94,109,-112,-125,56,-60,98,
			-80,38,16,65,42,6,0,1,-108,-69,-110,-56,40,120,-123,0,
			0,0,0,73,69,78,68,-82,66,96,-126
		});
		RESIZE_WIDGET = tk.createImage(new byte[] {
			-119,80,78,71,13,10,26,10,0,0,0,13,73,72,68,82,
			0,0,0,11,0,0,0,11,8,6,0,0,0,-87,-84,119,
			38,0,0,0,-92,73,68,65,84,120,-38,-115,-112,75,10,-124,
			48,16,68,-67,-1,73,18,93,-27,54,-30,70,18,-125,-97,104,
			76,80,-4,-20,106,-88,108,100,112,112,44,-24,38,-48,-113,-22,
			74,103,85,85,-95,-82,-21,87,-107,-79,-67,-43,43,120,28,71,
			-100,-25,-7,31,-10,-34,35,-49,115,-60,24,-97,97,58,74,41,
			-47,-74,-19,115,12,58,18,-76,-42,-90,-38,-9,-3,55,60,77,
			19,-118,-94,-128,-42,26,-61,48,-92,-9,-78,44,119,-104,67,33,
			4,-116,49,105,61,-35,67,8,-9,24,-52,72,-112,107,-5,-66,
			79,31,-101,-25,25,101,89,98,93,-41,11,118,-50,37,-105,-82,
			-21,-48,52,-51,23,-88,-108,-62,113,28,23,-52,59,-14,60,-44,
			-74,109,41,35,69,71,-126,-44,7,92,-121,-58,18,51,43,-121,
			32,0,0,0,0,73,69,78,68,-82,66,96,-126
		});
		tk.prepareImage(CLOSE_WIDGET, -1, -1, null);
		tk.prepareImage(RESIZE_WIDGET, -1, -1, null);
	}
}
