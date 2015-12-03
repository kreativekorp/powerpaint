package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.sprite.Sprite;

public class SprinklerTool extends AbstractPaintTool implements ToolOptions.Sprinkles {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,K,K,0,K,0,0,0,0,0,0,0,0,0,
					0,0,K,K,0,0,0,K,0,0,0,0,0,0,0,0,
					0,K,K,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,K,0,K,0,K,0,K,0,K,0,0,0,0,0,0,
					0,0,K,0,K,0,K,0,K,K,0,0,0,0,0,0,
					0,0,0,K,0,K,0,K,0,K,K,K,0,0,0,0,
					0,0,0,0,K,0,K,0,K,K,0,0,K,0,0,0,
					0,0,0,0,0,K,K,K,K,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,K,0,K,0,0,0,
					0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,0,K,0,0,
					0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,K,0,K,0,0,
					0,0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,
			}
	);
	
	private float lastX, lastY;
	
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
		Sprite sprinkle = e.tc().getSprinkle();
		float x = e.getX();
		float y = e.getY();
		lastX = x;
		lastY = y;
		// command but NOT option will inhibit drawing
		if (!e.isCtrlDown() || e.isAltDown()) {
			sprinkle.paint(g, (int)x, (int)y);
		}
		if (!e.tc().sprinkleBrushMode()) {
			// command will go through the complete set in order
			if (e.isCtrlDown()) {
				e.tc().nextSprinkle();
			}
			// option will repeat with the same shape
			else if (!e.isAltDown()) {
				e.tc().randomSprinkle();
			}
		}
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		Graphics2D g = e.getPaintGraphics();
		e.getPaintSettings().applyFill(g);
		Sprite sprinkle = e.tc().getSprinkle();
		float x = e.getX();
		float y = e.getY();
		if (
			e.tc().sprinkleBrushMode() ||
			(Math.hypot(x - lastX, y - lastY) > (sprinkle.getWidth() * 3 / 4))
		) {
			float px = lastX;
			float py = lastY;
			lastX = x;
			lastY = y;
			// command but NOT option will inhibit drawing
			if (!e.isCtrlDown() || e.isAltDown()) {
				if (e.tc().sprinkleBrushMode()) {
					drag(sprinkle, g, px, py, x, y);
				} else {
					sprinkle.paint(g, (int)x, (int)y);
				}
			}
			if (!e.tc().sprinkleBrushMode()) {
				// command will go through the complete set in order
				if (e.isCtrlDown()) {
					e.tc().nextSprinkle();
				}
				// option will repeat with the same shape
				else if (!e.isAltDown()) {
					e.tc().randomSprinkle();
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private void drag(Sprite sprinkle, Graphics2D g, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m;
			float y = sy + ((dy-sy)*i)/m;
			sprinkle.paint(g, (int)x, (int)y);
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (e.tc().sprinkleBrushMode()) {
			// command will go through the complete set in order
			if (e.isCtrlDown()) {
				e.tc().nextSprinkle();
			}
			// option will repeat with the same shape
			else if (!e.isAltDown()) {
				e.tc().randomSprinkle();
			}
		}
		e.commitTransaction();
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			e.tc().prevSprinkle();
			break;
		case KeyEvent.VK_RIGHT:
			e.tc().nextSprinkle();
			break;
		case KeyEvent.VK_UP:
			e.tc().prevSprinkleSet();
			break;
		case KeyEvent.VK_DOWN:
			e.tc().nextSprinkleSet();
			break;
		case KeyEvent.VK_BACK_QUOTE:
			e.tc().toggleSprinkleBrushMode();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getSprinkle().getPreparedCursor(true);
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
