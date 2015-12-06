package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.material.sprite.Sprite;

public class CalligraphyBrushTool extends AbstractPaintTool implements ToolOptions.CalligraphyBrushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,K,K,K,K,K,0,0,
					0,0,0,0,0,0,K,K,K,K,K,K,K,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,0,0,0,0,
					0,K,K,K,K,K,K,K,K,K,K,0,0,0,0,0,
					K,K,K,K,K,K,K,K,K,K,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,
					K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,0,
					K,K,K,K,K,K,K,0,0,0,0,0,0,0,0,0,
					0,K,K,K,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private int bi = 0;
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		Sprite brush = e.tc().getCalligraphyBrush();
		brush.getChild(bi = 0).paint(g, (int)e.getX(), (int)e.getY());
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		float x = e.getX();
		float y = e.getY();
		float px = e.getPreviousX();
		float py = e.getPreviousY();
		int i = (int)Math.hypot(px - x, py - y);
		Sprite brush = e.tc().getCalligraphyBrush();
		int n = brush.getChildCount();
		if (i >= n) i = n - 1;
		if (e.tc().calligraphyContinuous()) {
			drag(brush, g, px, py, x, y, bi, i);
		} else {
			brush.getChild(bi = i).paint(g, (int)x, (int)y);
		}
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		mouseDragged(e);
		bi = 0;
		e.commitTransaction();
		return true;
	}
	
	private void drag(Sprite brush, Graphics2D g, float sx, float sy, float dx, float dy, int si, int di) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			int j = si + ((di-si)*i)/m;
			float x = sx + ((dx-sx)*i)/m;
			float y = sy + ((dy-sy)*i)/m;
			brush.getChild(bi = j).paint(g, (int)x, (int)y);
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_LEFT:
			e.tc().prevCalligraphyBrush();
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_RIGHT:
			e.tc().nextCalligraphyBrush();
			break;
		case KeyEvent.VK_BACK_QUOTE:
			e.tc().toggleCalligraphyContinuous();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getCalligraphyBrush().getChild(bi).getPreparedCursor(true);
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
