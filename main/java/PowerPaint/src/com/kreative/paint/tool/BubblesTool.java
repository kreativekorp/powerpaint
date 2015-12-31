package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Image;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.util.CursorUtils;

public class BubblesTool extends AbstractPaintDrawTool {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,K,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,K,0,0,0,0,K,
					0,0,0,0,0,K,K,K,0,0,K,0,0,0,0,K,
					0,0,0,0,K,0,0,0,K,0,K,0,0,0,0,K,
					0,0,0,0,K,0,0,0,K,0,K,0,0,0,0,K,
					0,K,0,0,K,0,0,0,K,0,0,K,K,K,K,0,
					K,0,K,0,0,K,K,K,0,0,0,0,0,0,0,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,K,K,0,0,0,K,0,0,0,0,K,K,0,
					0,0,K,0,0,K,0,K,0,K,0,0,K,0,0,0,
					0,0,K,0,0,K,0,0,K,0,0,0,K,0,0,0,
					0,0,0,K,K,0,0,0,0,0,0,0,0,K,K,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			13, 13,
			new int[] {
					0,0,0,0,0,0,0,W,W,W,0,0,0,
					0,0,0,0,0,0,W,K,K,K,W,0,0,
					0,0,0,0,0,W,K,W,W,W,K,W,0,
					0,0,0,0,W,K,W,0,0,0,W,K,W,
					0,0,0,0,W,K,W,0,0,0,W,K,W,
					0,0,0,0,W,K,W,0,0,0,W,K,W,
					0,0,0,0,0,W,K,W,W,W,K,W,0,
					0,0,0,0,W,K,W,K,K,K,W,0,0,
					0,0,0,W,K,W,0,W,W,W,0,0,0,
					0,0,W,K,W,0,0,0,0,0,0,0,0,
					0,W,K,W,0,0,0,0,0,0,0,0,0,
					W,K,W,0,0,0,0,0,0,0,0,0,0,
					0,W,0,0,0,0,0,0,0,0,0,0,0,
			},
			8, 4,
			"Bubbles"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}

	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		float x, y, w, h;
		if (e.isAltDown()) {
			x = Math.min(e.getPreviousX(), e.getX());
			y = Math.min(e.getPreviousY(), e.getY());
			w = Math.max(e.getPreviousX(), e.getX())-x;
			h = Math.max(e.getPreviousY(), e.getY())-y;
		} else {
			w = h = Math.max(Math.abs(e.getPreviousX()-e.getX()), Math.abs(e.getPreviousY()-e.getY())) - 2.0f;
			if (w <= 0.0f || h <= 0.0f) return false;
			x = (e.getPreviousX()+e.getX()-w)/2.0f;
			y = (e.getPreviousY()+e.getY()-h)/2.0f;
		}
		ShapeDrawObject wsh;
		if (e.isCtrlDown()) {
			wsh = new ShapeDrawObject.Rectangle(e.getPaintSettings(), x, y, w, h);
		} else {
			wsh = new ShapeDrawObject.Ellipse(e.getPaintSettings(), x, y, w, h);
		}
		if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
		else wsh.paint(e.getPaintGraphics());
		return true;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.commitTransaction();
		return false;
	}

	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
}
