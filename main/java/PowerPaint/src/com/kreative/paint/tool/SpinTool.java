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

package com.kreative.paint.tool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import com.kreative.paint.PaintContext;
import com.kreative.paint.ToolContext;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerEnumOption;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.util.Bitmap;
import com.kreative.paint.util.CursorUtils;

public class SpinTool extends AbstractPaintTool implements ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,0,0,
					0,0,0,K,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,0,0,0,0,
					0,0,0,0,0,K,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,K,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,0,0,0,0,
					0,0,0,K,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			13, 13,
			new int[] {
					0,W,W,0,0,0,0,0,0,0,0,0,0,
					W,K,K,W,0,0,0,0,0,0,0,0,0,
					W,K,K,K,W,0,0,0,0,0,0,0,0,
					0,W,K,K,K,W,0,0,0,0,0,0,0,
					0,0,W,K,K,W,0,0,0,0,0,0,0,
					0,0,0,W,W,K,W,0,0,0,0,0,0,
					0,0,0,0,0,W,K,W,0,0,0,0,0,
					0,0,0,0,0,0,W,K,W,W,0,0,0,
					0,0,0,0,0,0,0,W,K,K,W,0,0,
					0,0,0,0,0,0,0,W,K,K,K,W,0,
					0,0,0,0,0,0,0,0,W,K,K,K,W,
					0,0,0,0,0,0,0,0,0,W,K,K,W,
					0,0,0,0,0,0,0,0,0,0,W,W,0,
			},
			6, 6,
			"Baton Rouge"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private int phase = 0;
	private int[][] cache = null;
	private int lc = 16;
	private int dc = 4;
	private SpinShape sc = SPIN;
	
	private void buildCache(ToolContext tc, PaintContext cx) {
		cache = new int[64][];
		lc = tc.getCustom(SpinTool.class, "length", Integer.class, 16);
		dc = tc.getCustom(SpinTool.class, "delay", Integer.class, 4);
		sc = tc.getCustom(SpinTool.class, "shape", SpinShape.class, SPIN);
		for (int i = 0; i < 64; i++) {
			BufferedImage bi = new BufferedImage(lc*4, lc*4, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setStroke(cx.getStroke());
			g.setPaint(Color.black);
			sc.drawPhase(g, lc*2, lc*2, lc, i);
			g.dispose();
			cache[i] = new int[lc*4*lc*4];
			bi.getRGB(0, 0, lc*4, lc*4, cache[i], 0, lc*4);
		}
	}
	
	public boolean toolSelected(ToolEvent e) {
		buildCache(e.tc(), e.getPaintContext());
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		buildCache(e.tc(), e.getPaintContext());
		return false;
	}
	
	public boolean paintSettingsChanged(ToolEvent e) {
		buildCache(e.tc(), e.getPaintContext());
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		e.getPaintContext().applyFill(e.getPaintGraphics());
		spin(e, true);
		return true;
	}
	
	public boolean mouseHeld(ToolEvent e) {
		spin(e, false);
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		spin(e, false);
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		spin(e, false);
		e.commitTransaction();
		return true;
	}
	
	private long etime = 0L;
	private long ptime = 0L;
	private void spin(ToolEvent e, boolean reset) {
		if (cache == null) buildCache(e.tc(), e.getPaintContext());
		long ctime = System.currentTimeMillis();
		if (reset) {
			etime = 0L;
			ptime = ctime;
		} else {
			etime += (ctime-ptime);
			ptime = ctime;
		}
		while (etime >= dc) {
			new Bitmap(lc*4, lc*4, cache[(phase++) & 63]).paint(
				e.getPaintGraphics(),
				(int)e.getX()-lc*2,
				(int)e.getY()-lc*2
			);
			etime -= dc;
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_1:
			e.tc().decrementCustom(SpinTool.class, "length", Integer.class, 16, 1);
			break;
		case KeyEvent.VK_2:
			e.tc().setCustom(SpinTool.class, "length", 16);
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_3:
			e.tc().incrementCustom(SpinTool.class, "length", Integer.class, 16);
			break;
		case KeyEvent.VK_4:
			e.tc().setCustom(SpinTool.class, "length", 8);
			break;
		case KeyEvent.VK_5:
			e.tc().setCustom(SpinTool.class, "length", 12);
			break;
		case KeyEvent.VK_6:
			e.tc().setCustom(SpinTool.class, "length", 16);
			break;
		case KeyEvent.VK_7:
			e.tc().setCustom(SpinTool.class, "length", 24);
			break;
		case KeyEvent.VK_8:
			e.tc().setCustom(SpinTool.class, "length", 32);
			break;
		case KeyEvent.VK_9:
			e.tc().setCustom(SpinTool.class, "length", 48);
			break;
		case KeyEvent.VK_0:
			e.tc().setCustom(SpinTool.class, "length", 64);
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			e.tc().setCustom(SpinTool.class, "shape", SPIN);
			phase = 0;
			break;
		case KeyEvent.VK_EQUALS:
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_ADD:
			e.tc().setCustom(SpinTool.class, "shape", CLOCK);
			phase = 0;
			break;
		case KeyEvent.VK_OPEN_BRACKET:
			e.tc().setCustom(SpinTool.class, "shape", BFLY);
			phase = 0;
			break;
		case KeyEvent.VK_CLOSE_BRACKET:
			e.tc().setCustom(SpinTool.class, "shape", FENCE);
			phase = 0;
			break;
		case KeyEvent.VK_SEMICOLON:
			e.tc().setCustom(SpinTool.class, "shape", RBROW);
			phase = 0;
			break;
		case KeyEvent.VK_QUOTE:
		case KeyEvent.VK_QUOTEDBL:
			e.tc().setCustom(SpinTool.class, "shape", LBROW);
			phase = 0;
			break;
		case KeyEvent.VK_PERIOD:
		case KeyEvent.VK_DECIMAL:
			e.tc().setCustom(SpinTool.class, "shape", BIRD);
			phase = 0;
			break;
		case KeyEvent.VK_SLASH:
		case KeyEvent.VK_DIVIDE:
			e.tc().setCustom(SpinTool.class, "shape", CRADLE);
			phase = 0;
			break;
		case KeyEvent.VK_COMMA:
		case KeyEvent.VK_SEPARATOR:
			phase = 0;
			cache = null;
			break;
		}
		return false;
	}

	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public int getMouseHeldInterval() {
		return dc;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new IntegerEnumOption() {
			public String getName() { return ToolUtilities.messages.getString("spin.options.Shape"); }
			public int getValue() {
				SpinShape shape = tc.getCustom(SpinTool.class, "shape", SpinShape.class, SPIN);
				if (shape == SPIN) return 0;
				if (shape == BFLY) return 1;
				if (shape == FENCE) return 2;
				if (shape == RBROW) return 3;
				if (shape == LBROW) return 4;
				if (shape == BIRD) return 5;
				if (shape == CRADLE) return 6;
				if (shape == CLOCK) return 7;
				return 0;
			}
			public void setValue(int v) {
				SpinShape shape;
				switch (v) {
				case 0: shape = SPIN; phase = 0; break;
				case 1: shape = BFLY; phase = 0; break;
				case 2: shape = FENCE; phase = 0; break;
				case 3: shape = RBROW; phase = 0; break;
				case 4: shape = LBROW; phase = 0; break;
				case 5: shape = BIRD; phase = 0; break;
				case 6: shape = CRADLE; phase = 0; break;
				case 7: shape = CLOCK; phase = 0; break;
				default: shape = SPIN; phase = 0; break;
				}
				tc.setCustom(SpinTool.class, "shape", shape);
			}
			public int[] values() {
				return new int[]{ 0, 1, 2, 3, 4, 5, 6, 7 };
			}
			public String getLabel(int v) {
				switch (v) {
				case 0: return ToolUtilities.messages.getString("spin.options.Shape.Spin");
				case 1: return ToolUtilities.messages.getString("spin.options.Shape.Bfly");
				case 2: return ToolUtilities.messages.getString("spin.options.Shape.Fence");
				case 3: return ToolUtilities.messages.getString("spin.options.Shape.Rbrow");
				case 4: return ToolUtilities.messages.getString("spin.options.Shape.Lbrow");
				case 5: return ToolUtilities.messages.getString("spin.options.Shape.Bird");
				case 6: return ToolUtilities.messages.getString("spin.options.Shape.Cradle");
				case 7: return ToolUtilities.messages.getString("spin.options.Shape.Clock");
				default: return null;
				}
			}
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("spin.options.LineLength"); }
			public int getMaximum() { return 128; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(SpinTool.class, "length", Integer.class, 16); }
			public void setValue(int v) { tc.setCustom(SpinTool.class, "length", v); }
			public boolean useSlider() { return false; }
		});
		f.add(new IntegerOption() {
			public String getName() { return ToolUtilities.messages.getString("spin.options.Delay"); }
			public int getMaximum() { return 500; }
			public int getMinimum() { return 1; }
			public int getStep() { return 1; }
			public int getValue() { return tc.getCustom(SpinTool.class, "delay", Integer.class, 4); }
			public void setValue(int v) { tc.setCustom(SpinTool.class, "delay", v); }
			public boolean useSlider() { return false; }
		});
		return f;
	}
	
	public static interface SpinShape {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p);
	}
	
	public static final SpinShape SPIN = new SpinShape() {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p) {
			p &= 63;
			int x1 = (p < 32) ? (cx+l*(p-16)/16) : (cx-l*(p-48)/16);
			int y1 = (p < 16) ? (cy-l*p/16) : (p < 48) ? (cy+l*(p-32)/16) : (cy-l*(p-64)/16);
			int x2 = (p < 32) ? (cx-l*(p-16)/16) : (cx+l*(p-48)/16);
			int y2 = (p < 16) ? (cy+l*p/16) : (p < 48) ? (cy-l*(p-32)/16) : (cy+l*(p-64)/16);
			g.drawLine(x1, y1, x2, y2);
		}
	};
	
	public static final SpinShape BFLY = new SpinShape() {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p) {
			p &= 63;
			int x1 = cx-l;
			int y1 = (p < 16) ? (cy-l*p/16) : (p < 48) ? (cy+l*(p-32)/16) : (cy-l*(p-64)/16);
			int x2 = cx+l;
			int y2 = (p < 16) ? (cy+l*p/16) : (p < 48) ? (cy-l*(p-32)/16) : (cy+l*(p-64)/16);
			g.drawLine(x1, y1, x2, y2);
		}
	};
	
	public static final SpinShape FENCE = new SpinShape() {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p) {
			p &= 63;
			int x1 = (p < 16) ? (cx-l*p/16) : (p < 48) ? (cx+l*(p-32)/16) : (cx-l*(p-64)/16);
			int y1 = cy-l;
			int x2 = (p < 16) ? (cx+l*p/16) : (p < 48) ? (cx-l*(p-32)/16) : (cx+l*(p-64)/16);
			int y2 = cy+l;
			g.drawLine(x1, y1, x2, y2);
		}
	};
	
	public static final SpinShape RBROW = new SpinShape() {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p) {
			p &= 31;
			int x1 = (p < 16) ? (cx-l*p/16) : (cx+l*(p-32)/16);
			int y1 = (p < 16) ? (cy+l*(p-16)/16) : (cy-l*(p-16)/16);
			int x2 = (p < 16) ? (cx+l*p/16) : (cx-l*(p-32)/16);
			int y2 = (p < 16) ? (cy-l*(p-16)/16) : (cy+l*(p-16)/16);
			g.drawLine(x1, y1, x2, y2);
		}
	};
	
	public static final SpinShape LBROW = new SpinShape() {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p) {
			p &= 31;
			int x1 = (p < 16) ? (cx+l*(p-16)/16) : (cx-l*(p-16)/16);
			int y1 = (p < 16) ? (cy+l*p/16) : (cy-l*(p-32)/16);
			int x2 = (p < 16) ? (cx-l*(p-16)/16) : (cx+l*(p-16)/16);
			int y2 = (p < 16) ? (cy-l*p/16) : (cy+l*(p-32)/16);
			g.drawLine(x1, y1, x2, y2);
		}
	};
	
	public static final SpinShape BIRD = new SpinShape() {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p) {
			p &= 63;
			int x1 = (p < 32) ? (cx+l*(p-16)/16) : (cx-l*(p-48)/16);
			int y1 = (p < 16) ? (cy-l*p/16) : (p < 48) ? (cy+l*(p-32)/16) : (cy-l*(p-64)/16);
			int x2 = (p < 32) ? (cx-l*(p-16)/16) : (cx+l*(p-48)/16);
			int y2 = (p < 16) ? (cy-l*p/16) : (p < 48) ? (cy+l*(p-32)/16) : (cy-l*(p-64)/16);
			g.drawLine(x1, y1, cx, cy);
			g.drawLine(cx, cy, x2, y2);
		}
	};
	
	public static final SpinShape CRADLE = new SpinShape() {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p) {
			p &= 63;
			int x1 = (p < 32) ? (cx+l*(p-16)/16) : (cx-l*(p-48)/16);
			int y1 = (p < 16) ? (cy-l*p/16) : (p < 48) ? (cy+l*(p-32)/16) : (cy-l*(p-64)/16);
			int x2 = (p < 32) ? (cx+l*(p-16)/16) : (cx-l*(p-48)/16);
			int y2 = (p < 16) ? (cy+l*p/16) : (p < 48) ? (cy-l*(p-32)/16) : (cy+l*(p-64)/16);
			g.drawLine(x1, y1, cx, cy);
			g.drawLine(cx, cy, x2, y2);
		}
	};
	
	public static final SpinShape CLOCK = new SpinShape() {
		public void drawPhase(Graphics g, int cx, int cy, int l, int p) {
			p &= 63;
			int x1 = (p < 16) ? (cx+l*p/16) : (p < 48) ? (cx-l*(p-32)/16) : (cx+l*(p-64)/16);
			int y1 = (p < 32) ? (cy+l*(p-16)/16) : (cy-l*(p-48)/16);
			g.drawLine(x1, y1, cx, cy);
		}
	};
}
