/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Like a JPopupMenu, but for anything you want.
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class JPopupPanel extends JFrame {
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
	private Container contentpane = null;
	private HideOnReleaseListener hideOnReleaseListener;
	private PopupAWTListener popupAWTListener;
	
	public JPopupPanel() {
		setAlwaysOnTop(true);
		setFocusable(false);
		setFocusableWindowState(false);
		setUndecorated(true);
		
		main = new JPanel(new BorderLayout());
		contentpane = super.getContentPane();
		main.add(contentpane, BorderLayout.CENTER);
		if (!isMacOS) {
			Border inner = BorderFactory.createEmptyBorder(2, 2, 2, 2);
			Border outer = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.lightGray, Color.white, Color.darkGray, Color.gray);
			main.setBorder(BorderFactory.createCompoundBorder(outer, inner));
		}
		super.setContentPane(main);
		
		hideOnReleaseListener = new HideOnReleaseListener();
		popupAWTListener = new PopupAWTListener();
		getToolkit().addAWTEventListener(popupAWTListener, AWTEvent.MOUSE_EVENT_MASK);
	}
	
	public Container getContentPane() {
		return (main != null) ? contentpane : super.getContentPane();
	}
	
	public void setContentPane(Container c) {
		if (main != null) {
			main.remove(contentpane);
			contentpane = c;
			main.add(contentpane, BorderLayout.CENTER);
		} else {
			super.setContentPane(c);
		}
	}
	
	public Component add(Component c) {
		return (main != null) ? contentpane.add(c) : super.add(c);
	}
	
	public Component add(Component c, int index) {
		return (main != null) ? contentpane.add(c,index) : super.add(c,index);
	}
	
	public void add(Component c, Object constraints) {
		if (main != null) contentpane.add(c,constraints); else super.add(c,constraints);
	}
	
	public void add(Component c, Object constraints, int index) {
		if (main != null) contentpane.add(c,constraints,index); else super.add(c,constraints,index);
	}
	
	public Component add(String name, Component c) {
		return (main != null) ? contentpane.add(name,c) : super.add(name,c);
	}
	
	public void remove(Component c) {
		if (main != null) contentpane.remove(c); else super.remove(c);
	}
	
	public void remove(int index) {
		if (main != null) contentpane.remove(index); else super.remove(index);
	}
	
	public void removeAll() {
		if (main != null) contentpane.removeAll(); else super.removeAll();
	}
	
	public LayoutManager getLayout() {
		return (main != null) ? contentpane.getLayout() : super.getLayout();
	}
	
	public void setLayout(LayoutManager lm) {
		if (main != null) contentpane.setLayout(lm); else super.setLayout(lm);
	}
	
	public void hideOnRelease(Component c) {
		c.addMouseListener(hideOnReleaseListener);
	}
	
	public void dispose() {
		getToolkit().removeAWTEventListener(popupAWTListener);
		super.dispose();
	}
	
	public void show(Component invoker, int x, int y) {
		if (invoker == null) {
			setLocation(x, y);
		} else {
			Point b = invoker.getLocationOnScreen();
			setLocation(b.x + x, b.y + y);
		}
		setVisible(true);
	}
	
	private class HideOnReleaseListener extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			JPopupPanel.this.setVisible(false);
		}
	}
	
	private class PopupAWTListener implements AWTEventListener {
		public void eventDispatched(AWTEvent e) {
			if (e instanceof MouseEvent) {
				MouseEvent m = (MouseEvent)e;
				if (m.getID() == MouseEvent.MOUSE_PRESSED) {
					Component c = m.getComponent();
					while (c != null) {
						if (c == JPopupPanel.this) return;
						else c = c.getParent();
					}
					JPopupPanel.this.setVisible(false);
				}
			}
		}
	}
}
