/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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
import java.util.*;
import com.kreative.paint.PaintContext;
import com.kreative.paint.rcp.CheckerboardPaint;
import com.kreative.paint.util.CursorUtils;

public class SNFCustomPaintPanel extends PaintContextPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Dimension min = new Dimension(0, 45);
	private static final Dimension pref = new Dimension(184, 45);
	
	private Map<Integer,Paint> m = new HashMap<Integer,Paint>();
	
	public SNFCustomPaintPanel(PaintContext pc) {
		super(pc, 0);
		setMinimumSize(min);
		setPreferredSize(pref);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				doClick(e.getX(), e.getY(), e.isMetaDown() || e.isControlDown());
				if (hasCell(e.getX(), e.getY())) {
					setCursor(CursorUtils.CURSOR_DROPPER);
					setToolTipText(PaletteUtilities.messages.getString("snf.custom.filled"));
				} else {
					setCursor(CursorUtils.CURSOR_BUCKET);
					setToolTipText(PaletteUtilities.messages.getString("snf.custom.empty"));
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (hasCell(e.getX(), e.getY())) {
					setCursor(CursorUtils.CURSOR_DROPPER);
					setToolTipText(PaletteUtilities.messages.getString("snf.custom.filled"));
				} else {
					setCursor(CursorUtils.CURSOR_BUCKET);
					setToolTipText(PaletteUtilities.messages.getString("snf.custom.empty"));
				}
			}
		});
	}
	
	public void update() {
		// nothing
	}
	
	protected void paintComponent(Graphics g) {
		Insets i = getInsets();
		int x = i.left;
		int y = i.top;
		int w = getWidth()-i.left-i.right;
		int h = getHeight()-i.top-i.bottom;
		int cs = (h+1)/2 - 1;
		for (int ix = x, j = 0; ix < x+w; ix += cs+1, j++) {
			g.setColor(Color.black);
			g.fillRect(ix, y, cs+2, h);
			if (m.containsKey(j)) {
				g.setColor(Color.white);
				g.fillRect(ix+1, y, cs, cs);
				g.setColor(Color.black);
				g.fillRect(ix+2, y+1, cs-2, cs-2);
				((Graphics2D)g).setPaint(m.get(j));
				g.fillRect(ix+3, y+2, cs-4, cs-4);
			} else {
				((Graphics2D)g).setPaint(CheckerboardPaint.LIGHT);
				g.fillRect(ix+1, y, cs, cs);
			}
			if (m.containsKey(~j)) {
				g.setColor(Color.white);
				g.fillRect(ix+1, y+cs+1, cs, cs);
				g.setColor(Color.black);
				g.fillRect(ix+2, y+cs+2, cs-2, cs-2);
				((Graphics2D)g).setPaint(m.get(~j));
				g.fillRect(ix+3, y+cs+3, cs-4, cs-4);
			} else {
				((Graphics2D)g).setPaint(CheckerboardPaint.LIGHT);
				g.fillRect(ix+1, y+cs+1, cs, cs);
			}
		}
	}
	
	private void doClick(int mx, int my, boolean alt) {
		Insets i = getInsets();
		int x = i.left;
		int y = i.top;
		int w = getWidth()-i.left-i.right;
		int h = getHeight()-i.top-i.bottom;
		if (mx >= x && my >= y && mx < x+w && my < y+h) {
			int cs = (h+1)/2 - 1;
			int j = ((mx-x) / (cs+1)) ^ (((my-y) < (cs+1)) ? 0 : -1);
			if (m.containsKey(j)) {
				if (alt) {
					m.remove(j);
					repaint();
				} else {
					pc.setEditedPaint(m.get(j));
				}
			} else {
				m.put(j, pc.getEditedPaint());
				repaint();
			}
		}
	}
	
	private boolean hasCell(int mx, int my) {
		Insets i = getInsets();
		int x = i.left;
		int y = i.top;
		int w = getWidth()-i.left-i.right;
		int h = getHeight()-i.top-i.bottom;
		if (mx >= x && my >= y && mx < x+w && my < y+h) {
			int cs = (h+1)/2 - 1;
			int j = ((mx-x) / (cs+1)) ^ (((my-y) < (cs+1)) ? 0 : -1);
			return m.containsKey(j);
		} else {
			return false;
		}
	}
}
