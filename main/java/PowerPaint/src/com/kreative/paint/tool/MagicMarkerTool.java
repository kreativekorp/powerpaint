package com.kreative.paint.tool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import com.kreative.paint.ToolContext;
import com.kreative.paint.form.CustomOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.IntegerEnumOption;
import com.kreative.paint.util.CursorUtils;

public class MagicMarkerTool extends AbstractPaintTool implements ToolOptions.Custom {
	// <dead-alewives>I wanna implement MAGIC MARKER!</dead-alewives>
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,K,K,K,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,K,K,K,0,
					0,0,0,0,0,0,0,K,K,K,K,K,K,K,0,0,
					0,0,0,0,0,0,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,0,K,K,K,K,K,K,K,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,0,0,0,0,0,
					0,0,0,K,0,K,K,K,K,K,0,0,0,0,0,0,
					0,0,K,K,K,0,K,K,K,0,0,0,0,0,0,0,
					0,K,K,K,K,K,0,K,0,0,0,0,0,0,0,0,
					0,K,K,K,K,K,K,0,0,0,0,0,0,0,0,0,
					0,K,K,K,K,K,0,0,0,0,0,0,0,0,0,0,
					0,K,K,K,K,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Image icon1 = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Image icon2 = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,0,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Image icon3 = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,K,K,K,K,K,K,0,
					0,0,0,0,0,0,0,0,K,K,K,K,K,K,K,K,
					0,0,0,0,0,0,0,K,K,K,K,K,K,K,K,K,
					0,0,0,0,0,0,K,K,K,K,K,K,K,K,K,0,
					0,0,0,0,0,K,K,K,K,K,K,K,K,K,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,0,0,0,0,0,
					0,K,K,K,K,K,K,K,K,K,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Image icon4 = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,K,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,K,K,K,0,0,
					0,0,0,0,0,K,K,K,K,K,K,K,K,K,K,0,
					0,0,0,0,K,K,K,K,K,K,K,K,K,K,K,K,
					0,0,0,K,K,K,K,K,K,K,K,K,K,K,K,K,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					0,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,0,
					K,K,K,K,K,K,K,K,K,K,K,K,K,K,0,0,
					K,K,K,K,K,K,K,K,K,K,K,K,K,0,0,0,
					K,K,K,K,K,K,K,K,K,K,K,K,0,0,0,0,
					K,K,K,K,K,K,K,K,K,K,K,0,0,0,0,0,
					K,K,K,K,K,K,K,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs1 = CursorUtils.makeCursor(
			18, 18,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,W,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,W,K,K,W,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,K,K,K,K,W,0,0,0,0,
					0,0,0,0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,
					0,0,0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,0,
					0,0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,0,0,
					0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,0,0,0,
					0,0,0,0,W,K,K,K,K,W,0,0,0,0,0,0,0,0,
					0,0,0,0,W,K,K,K,W,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,W,W,W,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			6, 11,
			"MagicMarker1"
	);
	private static final Cursor curs2 = CursorUtils.makeCursor(
			18, 18,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,W,W,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,K,K,K,W,0,0,0,0,0,
					0,0,0,0,0,0,0,W,K,K,K,K,K,W,0,0,0,0,
					0,0,0,0,0,0,W,K,K,K,K,K,K,K,W,0,0,0,
					0,0,0,0,0,W,K,K,K,K,K,K,K,K,W,0,0,0,
					0,0,0,0,W,K,K,K,K,K,K,K,K,K,W,0,0,0,
					0,0,0,0,W,K,K,K,K,K,K,K,K,W,0,0,0,0,
					0,0,0,0,W,K,K,K,K,K,K,K,W,0,0,0,0,0,
					0,0,0,0,W,K,K,K,K,K,K,W,0,0,0,0,0,0,
					0,0,0,0,0,W,K,K,K,K,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,W,W,W,W,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			8, 8,
			"MagicMarker2"
	);
	private static final Cursor curs3 = CursorUtils.makeCursor(
			18, 18,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,W,W,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,W,K,K,W,0,0,0,
					0,0,0,0,0,0,0,0,0,0,W,K,K,K,K,W,0,0,
					0,0,0,0,0,0,0,0,0,W,K,K,K,K,K,K,W,0,
					0,0,0,0,0,0,0,0,W,K,K,K,K,K,K,K,K,W,
					0,0,0,0,0,0,0,W,K,K,K,K,K,K,K,K,K,W,
					0,0,0,0,0,0,W,K,K,K,K,K,K,K,K,K,W,0,
					0,0,0,0,0,W,K,K,K,K,K,K,K,K,K,W,0,0,
					0,0,0,0,W,K,K,K,K,K,K,K,K,K,W,0,0,0,
					0,0,0,W,K,K,K,K,K,K,K,K,K,W,0,0,0,0,
					0,0,W,K,K,K,K,K,K,K,K,K,W,0,0,0,0,0,
					0,W,K,K,K,K,K,K,K,K,K,W,0,0,0,0,0,0,
					W,K,K,K,K,K,K,K,K,K,W,0,0,0,0,0,0,0,
					W,K,K,K,K,K,K,K,K,W,0,0,0,0,0,0,0,0,
					0,W,W,W,W,W,W,W,W,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			2, 15,
			"MagicMarker3"
	);
	private static final Cursor curs4 = CursorUtils.makeCursor(
			18, 18,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,W,W,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,W,K,K,W,0,0,0,0,0,
					0,0,0,0,0,0,0,0,W,K,K,K,K,W,0,0,0,0,
					0,0,0,0,0,0,0,W,K,K,K,K,K,K,W,0,0,0,
					0,0,0,0,0,0,W,K,K,K,K,K,K,K,K,W,0,0,
					0,0,0,0,0,W,K,K,K,K,K,K,K,K,K,K,W,0,
					0,0,0,0,W,K,K,K,K,K,K,K,K,K,K,K,K,W,
					0,0,0,W,K,K,K,K,K,K,K,K,K,K,K,K,K,W,
					0,0,W,K,K,K,K,K,K,K,K,K,K,K,K,K,K,W,
					0,W,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,W,
					W,K,K,K,K,K,K,K,K,K,K,K,K,K,K,K,W,0,
					W,K,K,K,K,K,K,K,K,K,K,K,K,K,K,W,0,0,
					W,K,K,K,K,K,K,K,K,K,K,K,K,K,W,0,0,0,
					W,K,K,K,K,K,K,K,K,K,K,K,K,W,0,0,0,0,
					W,K,K,K,K,K,K,K,K,K,K,K,W,0,0,0,0,0,
					W,K,K,K,K,K,K,K,K,K,K,W,0,0,0,0,0,0,
					0,W,W,W,W,W,W,W,W,W,W,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			2, 15,
			"MagicMarker4"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private int markerTip = 0;
	private int paperType = 0;
	
	public boolean toolSelected(ToolEvent e) {
		markerTip = e.tc().getCustom(MagicMarkerTool.class, "markerTip", Integer.class, 0);
		paperType = e.tc().getCustom(MagicMarkerTool.class, "paperType", Integer.class, 0);
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		markerTip = e.tc().getCustom(MagicMarkerTool.class, "markerTip", Integer.class, 0);
		paperType = e.tc().getCustom(MagicMarkerTool.class, "paperType", Integer.class, 0);
		return false;
	}
	
	private void setMarkerTip(ToolContext tc, int markerTip) {
		tc.setCustom(MagicMarkerTool.class, "markerTip", this.markerTip = markerTip);
	}
	
	private void setPaperType(ToolContext tc, int paperType) {
		tc.setCustom(MagicMarkerTool.class, "paperType", this.paperType = paperType);
	}
	
	private Random random = new Random();
	private int curW, curH;
	private int maxW, maxH;
	private int minW, minH;
	private boolean rr, aa;
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		switch (markerTip) {
		case 0: curW = maxW =  9; curH = maxH =  9; minW = 1; minH = 1; break;
		case 1: curW = maxW = 10; curH = maxH = 11; minW = 2; minH = 3; break;
		case 2: curW = maxW = 16; curH = maxH = 12; minW = 8; minH = 4; break;
		case 3: curW = maxW = 13; curH = maxH = 13; minW = 7; minH = 7; break;
		}
		rr = (markerTip >= 2);
		aa = (paperType < 3);
		paint(g, g.getComposite(), e.getX(), e.getY(), curW, curH);
		return true;
	}
	
	public boolean mouseHeld(ToolEvent e) {
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		curW++; curH++;
		paint(g, g.getComposite(), e.getX(), e.getY(), curW, curH);
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		float x = e.getX();
		float y = e.getY();
		float px = e.getPreviousX();
		float py = e.getPreviousY();
		int i = (int)Math.hypot(px - x, py - y) / 4;
		int lastW = Math.min(curW, maxW);
		int lastH = Math.min(curH, maxH);
		curW = Math.max(maxW - i, minW);
		curH = Math.max(maxH - i, minH);
		drag(g, g.getComposite(), px, py, x, y, lastW, lastH, curW, curH);
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		mouseDragged(e);
		e.commitTransaction();
		return true;
	}
	
	private void drag(
		Graphics2D g,
		Composite cx,
		float sx, float sy,
		float dx, float dy,
		float sw, float sh,
		float dw, float dh
	) {
		Composite altcx = (aa && cx instanceof AlphaComposite) ?
			AlphaComposite.getInstance(
				((AlphaComposite)cx).getRule(),
				((AlphaComposite)cx).getAlpha() / 2
			) : cx;
		int m = (int)Math.ceil(Math.max(Math.abs(dx - sx), Math.abs(dy - sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx - sx) * i) / m;
			float y = sy + ((dy - sy) * i) / m;
			float w = sw + ((dw - sw) * i) / m;
			float h = sh + ((dh - sh) * i) / m;
			if (random.nextBoolean()) {
				w = Math.max(w - 2, minW);
				h = Math.max(h - 2, minH);
			}
			paint(g, (random.nextBoolean() ? altcx : cx), x, y, w, h);
		}
	}
	
	private void paint(
		Graphics2D g,
		Composite cx,
		float x, float y,
		float w, float h
	) {
		x = Math.round(x - w / 2);
		y = Math.round(y - h / 2);
		w = Math.round(w);
		h = Math.round(h);
		Shape s = rr ?
			new RoundRectangle2D.Float(x, y, w, h, 6, 6) :
			new Ellipse2D.Float(x, y, w, h);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa ?
			RenderingHints.VALUE_ANTIALIAS_ON :
			RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setComposite(cx);
		g.fill(s);
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_4: setMarkerTip(e.tc(), 0); break;
			case KeyEvent.VK_3: setMarkerTip(e.tc(), 1); break;
			case KeyEvent.VK_2: setMarkerTip(e.tc(), 2); break;
			case KeyEvent.VK_1: setMarkerTip(e.tc(), 3); break;
			case KeyEvent.VK_5: setPaperType(e.tc(), 0); break;
			case KeyEvent.VK_6: setPaperType(e.tc(), 1); break;
			case KeyEvent.VK_7: setPaperType(e.tc(), 2); break;
			case KeyEvent.VK_8: setPaperType(e.tc(), 3); break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		switch (markerTip) {
			case 0: return curs1;
			case 1: return curs2;
			case 2: return curs3;
			case 3: return curs4;
		}
		return null;
	}
	
	public int getMouseHeldInterval() {
		switch (paperType) {
			case 0: return (curH < 32) ? 5 : 10;
			case 1: return (curH < 32) ? 8 : 16;
			case 2: return 38;
			case 3: return 0;
		}
		return 0;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new CustomOption<MarkerTipPanel>() {
			public String getName() { return ToolUtilities.messages.getString("magicmarker.options.MarkerTip"); }
			public MarkerTipPanel makeUI(boolean mini) { return new MarkerTipPanel(tc); }
			public void update(MarkerTipPanel ui) { ui.updateSelection(); }
		});
		f.add(new IntegerEnumOption() {
			public String getName() { return ToolUtilities.messages.getString("magicmarker.options.PaperType"); }
			public int getValue() { return paperType; }
			public void setValue(int v) { setPaperType(tc, v); }
			public int[] values() { return new int[]{ 0, 1, 2, 3 }; }
			public String getLabel(int v) {
				switch (v) {
				case 0: return ToolUtilities.messages.getString("magicmarker.options.PaperType.Soft");
				case 1: return ToolUtilities.messages.getString("magicmarker.options.PaperType.Medium");
				case 2: return ToolUtilities.messages.getString("magicmarker.options.PaperType.Hard");
				case 3: return ToolUtilities.messages.getString("magicmarker.options.PaperType.Screen");
				default: return null;
				}
			}
		});
		return f;
	}
	
	private class MarkerTipPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private final Set<MarkerTipLabel> labels;
		public MarkerTipPanel(ToolContext tc) {
			super(new GridLayout(1,0));
			labels = new HashSet<MarkerTipLabel>();
			MarkerTipLabel l;
			l = new MarkerTipLabel(tc, 0, icon1); add(l); labels.add(l);
			l = new MarkerTipLabel(tc, 1, icon2); add(l); labels.add(l);
			l = new MarkerTipLabel(tc, 2, icon3); add(l); labels.add(l);
			l = new MarkerTipLabel(tc, 3, icon4); add(l); labels.add(l);
		}
		public void updateSelection() {
			for (MarkerTipLabel l : labels) l.updateSelection();
		}
	}
	
	private class MarkerTipLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private final ToolContext tc;
		private final int i;
		public MarkerTipLabel(ToolContext tc, int i, Image icon) {
			super(new ImageIcon(icon));
			this.tc = tc; this.i = i;
			updateSelection();
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					setMarkerTip(MarkerTipLabel.this.tc, MarkerTipLabel.this.i);
				}
			});
		}
		public void updateSelection() {
			if (markerTip == i) {
				Border inner = BorderFactory.createEmptyBorder(2, 2, 2, 2);
				Border outer = BorderFactory.createLineBorder(Color.black, 2);
				setBorder(BorderFactory.createCompoundBorder(outer, inner));
			} else {
				setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			}
		}
	}
}
