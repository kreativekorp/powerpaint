package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import com.kreative.paint.document.draw.ShadowSettings;
import com.kreative.paint.document.draw.ShapeDrawObject;

public abstract class SimpleShapeTool extends AbstractPaintDrawTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter, ToolOptions.DrawMultiple, ToolOptions.QuickShadow {
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	protected abstract ShapeDrawObject makeShape(ToolEvent e, float x1, float y1, float x2, float y2);
	
	private ShapeDrawObject makeShape1(ToolEvent e) {
		float sx = e.getPreviousClickedX();
		float sy = e.getPreviousClickedY();
		float x = e.getX();
		float y = e.getY();
		if (e.isShiftDown() != e.tc().drawSquare()) {
			float w = Math.abs(x-sx);
			float h = Math.abs(y-sy);
			float s = Math.max(w, h);
			if (y > sy) y = sy+s;
			else y = sy-s;
			if (x > sx) x = sx+s;
			else x = sx-s;
		}
		if (e.isAltDown() != e.tc().drawFromCenter()) {
			sx -= (x-sx);
			sy -= (y-sy);
		}
		return makeShape(e, sx, sy, x, y);
	}
	
	private ShapeDrawObject makeObject(ToolEvent e) {
		ShapeDrawObject sh = makeShape1(e);
		if (e.tc().useShadow()) {
			sh.setShadowSettings(new ShadowSettings(
				e.tc().getShadowType(),
				e.tc().getShadowOpacity(),
				e.tc().getShadowXOffset(),
				e.tc().getShadowYOffset()
			));
		}
		return sh;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		if (e.isCtrlDown() != e.tc().drawMultiple()) {
			ShapeDrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (e.isCtrlDown() != e.tc().drawMultiple()) {
			ShapeDrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		ShapeDrawObject wsh = makeObject(e);
		if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
		else wsh.paint(e.getPaintGraphics());
		e.commitTransaction();
		return true;
	}
	
	public boolean keyPressed(ToolEvent e) {
		if (e.isMouseDown() && e.isCtrlDown() != e.tc().drawMultiple()) {
			ShapeDrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean keyReleased(ToolEvent e) {
		if (e.isMouseDown() && e.isCtrlDown() != e.tc().drawMultiple()) {
			ShapeDrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		else return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			ShapeDrawObject wsh = makeObject(e);
			wsh.paint(g);
			return true;
		}
		else return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
