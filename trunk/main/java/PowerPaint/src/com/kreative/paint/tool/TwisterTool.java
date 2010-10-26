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

package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.Random;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.ToolContext;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.util.CursorUtils;

public class TwisterTool extends AbstractPaintTool implements ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,0,0,
					K,K,K,0,0,0,0,0,0,0,0,0,K,K,K,K,
					K,K,0,K,K,K,K,K,K,K,K,K,0,0,K,K,
					0,K,K,K,K,K,0,0,0,0,K,K,K,K,K,0,
					0,0,0,0,0,K,K,K,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs1 = CursorUtils.makeCursor(
			18, 18,
			new int[] {
					0,0,0,W,W,W,W,W,W,W,W,W,W,W,W,0,0,0,
					0,W,W,K,K,K,K,K,K,K,K,K,K,K,K,W,W,0,
					W,K,K,K,W,W,W,W,W,W,W,W,W,K,K,K,K,W,
					W,K,K,W,K,K,K,K,K,K,K,K,K,W,W,K,K,W,
					0,W,K,K,K,K,K,W,W,W,W,K,K,K,K,K,W,0,
					0,0,W,W,W,W,K,K,K,K,K,K,K,W,W,W,0,0,
					0,0,0,0,0,0,W,W,K,K,K,K,K,W,0,0,0,0,
					0,0,0,0,0,0,0,0,W,W,K,K,K,K,W,0,0,0,
					0,0,0,0,0,0,0,0,0,0,W,K,K,K,W,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,W,K,K,W,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,W,K,K,W,0,0,0,
					0,0,0,0,0,0,0,0,0,0,W,K,K,W,0,0,0,0,
					0,0,0,0,0,0,0,0,0,W,K,K,W,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,K,K,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			10, 16,
			"Twister1"
	);
	private static final Cursor curs2 = CursorUtils.makeCursor(
			18, 18,
			new int[] {
					0,0,0,W,W,W,W,W,W,W,W,W,W,W,W,0,0,0,
					0,W,W,K,K,K,K,K,K,K,K,K,K,K,K,W,W,0,
					W,K,K,K,W,W,W,W,W,W,W,W,W,K,K,K,K,W,
					W,K,K,W,K,K,K,K,K,K,K,K,K,W,W,K,K,W,
					0,W,K,K,K,K,K,W,W,W,W,K,K,K,K,K,W,0,
					0,0,W,W,W,W,K,K,K,K,K,K,K,W,W,W,0,0,
					0,0,0,0,0,W,K,K,K,K,K,W,W,0,0,0,0,0,
					0,0,0,0,W,K,K,K,K,W,W,0,0,0,0,0,0,0,
					0,0,0,0,W,K,K,K,W,0,0,0,0,0,0,0,0,0,
					0,0,0,0,W,K,K,W,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,W,K,K,W,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,W,K,K,W,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,W,K,K,W,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			9, 16,
			"Twister2"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private boolean altCursor = false;
	private int acache = 2;
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		acache = e.tc().getCustom(TwisterTool.class, "twArea", Integer.class, 2);
		twist(e.getPaintSurface(), e.getX(), e.getY(), acache);
		altCursor = !altCursor;
		return true;
	}

	public boolean mouseHeld(ToolEvent e) {
		twist(e.getPaintSurface(), e.getX(), e.getY(), acache);
		altCursor = !altCursor;
		return true;
	}

	public boolean mouseDragged(ToolEvent e) {
		twist(e.getPaintSurface(), e.getX(), e.getY(), acache);
		altCursor = !altCursor;
		return true;
	}

	public boolean mouseReleased(ToolEvent e) {
		twist(e.getPaintSurface(), e.getX(), e.getY(), acache);
		altCursor = false;
		e.commitTransaction();
		return true;
	}
	
	private static final Random random = new Random();
	private static void twist(PaintSurface p, float x, float y, int size) {
		int bx = (int)x-size/2;
		int by = (int)y-size/2;
		int[] rgb = new int[size*size];
		p.getRGB(bx, by, size, size, rgb, 0, size);
		for (int i = 0, n = rgb.length; i < rgb.length && n > 0; i++, n--) {
			int j = i+random.nextInt(n);
			int tmp = rgb[i];
			rgb[i] = rgb[j];
			rgb[j] = tmp;
		}
		p.setRGB(bx, by, size, size, rgb, 0, size);
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_1:
			e.tc().setCustom(TwisterTool.class, "twArea", 2);
			break;
		case KeyEvent.VK_2:
			e.tc().setCustom(TwisterTool.class, "twArea", 4);
			break;
		case KeyEvent.VK_3:
			e.tc().setCustom(TwisterTool.class, "twArea", 8);
			break;
		case KeyEvent.VK_4:
			e.tc().setCustom(TwisterTool.class, "twArea", 16);
			break;
		case KeyEvent.VK_5:
			e.tc().setCustom(TwisterTool.class, "twArea", 32);
			break;
		case KeyEvent.VK_6:
			e.tc().setCustom(TwisterTool.class, "twArea", 3);
			break;
		case KeyEvent.VK_7:
			e.tc().setCustom(TwisterTool.class, "twArea", 6);
			break;
		case KeyEvent.VK_8:
			e.tc().setCustom(TwisterTool.class, "twArea", 12);
			break;
		case KeyEvent.VK_9:
			e.tc().setCustom(TwisterTool.class, "twArea", 24);
			break;
		case KeyEvent.VK_0:
			e.tc().setCustom(TwisterTool.class, "twArea", 5);
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			e.tc().setCustom(TwisterTool.class, "twArea", 10);
			break;
		case KeyEvent.VK_EQUALS:
		case KeyEvent.VK_ADD:
		case KeyEvent.VK_PLUS:
			e.tc().setCustom(TwisterTool.class, "twArea", 20);
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return altCursor ? curs2 : curs1;
	}
	
	public int getMouseHeldInterval() {
		return 50;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("twister.options.Area"); }
			public int getMaximum() { return Integer.MAX_VALUE; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(TwisterTool.class, "twArea", Integer.class, 2); }
			public void setValue(int v) { tc.setCustom(TwisterTool.class, "twArea", v); }
			public boolean useSlider() { return false; }
		});
		return f;
	}
}
