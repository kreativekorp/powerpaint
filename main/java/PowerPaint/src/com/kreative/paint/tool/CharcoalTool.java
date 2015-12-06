package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.material.sprite.Sprite;

public class CharcoalTool extends AbstractPaintTool implements ToolOptions.CharcoalBrushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,
					K,0,0,0,K,K,K,0,0,0,K,0,K,0,0,0,
					0,K,0,0,0,0,0,0,0,K,0,K,0,K,0,K,
					0,0,K,0,K,0,K,0,K,0,K,0,0,0,K,0,
					0,K,0,K,0,K,0,K,0,K,0,0,0,0,0,0,
					0,0,K,0,0,0,K,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		Sprite brush = e.tc().getCharcoalBrush();
		brush.getChild(0).paint(g, (int)e.getX(), (int)e.getY());
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
		Sprite brush = e.tc().getCharcoalBrush();
		int n = brush.getChildCount();
		if (i >= n) i = n - 1;
		brush.getChild(i).paint(g, (int)x, (int)y);
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		mouseDragged(e);
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_LEFT:
			e.tc().prevCharcoalBrush();
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_RIGHT:
			e.tc().nextCharcoalBrush();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getCharcoalBrush().getPreparedCursor(true);
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
