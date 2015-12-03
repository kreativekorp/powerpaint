package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import com.kreative.paint.draw.BrushStrokeDrawObject;
import com.kreative.paint.sprite.Sprite;

public class BrushTool extends AbstractPaintDrawTool implements ToolOptions.Brushes {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,K,0,K,0,K,0,K,0,0,0,
					0,0,0,K,0,K,0,K,0,K,0,K,K,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private GeneralPath currentPoly;
	
	public boolean toolSelected(ToolEvent e) {
		currentPoly = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPoly = null;
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			e.tc().getBrush().paint(g, (int)e.getX(), (int)e.getY());
			return true;
		} else {
			currentPoly = new GeneralPath();
			currentPoly.moveTo(e.getX(), e.getY());
			return false;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			float lastX = e.getPreviousX();
			float lastY = e.getPreviousY();
			float x = e.getX();
			float y = e.getY();
			drag(e.tc().getBrush(), g, lastX, lastY, x, y);
			return true;
		} else if (currentPoly != null) {
			currentPoly.lineTo(e.getX(), e.getY());
			return false;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			float lastX = e.getPreviousX();
			float lastY = e.getPreviousY();
			float x = e.getX();
			float y = e.getY();
			drag(e.tc().getBrush(), g, lastX, lastY, x, y);
			e.commitTransaction();
			return true;
		} else if (currentPoly != null) {
			currentPoly.lineTo(e.getX(), e.getY());
			BrushStrokeDrawObject o = new BrushStrokeDrawObject(
				e.tc().getBrush(), currentPoly, e.getPaintSettings());
			e.getDrawSurface().add(o);
			currentPoly = null;
			e.commitTransaction();
			return true;
		} else {
			e.commitTransaction();
			return false;
		}
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isInDrawMode() && currentPoly != null) {
			BrushStrokeDrawObject o = new BrushStrokeDrawObject(
				e.tc().getBrush(), currentPoly, e.getPaintSettings());
			o.paint(g);
			return true;
		} else {
			return false;
		}
	}
	
	private void drag(Sprite brush, Graphics2D g, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m;
			float y = sy + ((dy-sy)*i)/m;
			brush.paint(g, (int)x, (int)y);
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
			e.tc().prevBrush();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
			e.tc().nextBrush();
			break;
		}
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getBrush().getPreparedCursor(true);
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
}
