package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.document.draw.ShadowSettings;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.geom.draw.RegularPolygonDrawObject;

public class RegularPolygonTool extends AbstractPaintDrawTool
implements ToolOptions.DrawPerpendicular, ToolOptions.DrawFromCenter, ToolOptions.DrawMultiple,
ToolOptions.QuickShadow, ToolOptions.RegPolygon {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
					0,0,0,0,0,K,K,0,0,K,K,0,0,0,0,0,
					0,0,0,K,K,0,0,0,0,0,0,K,K,0,0,0,
					0,K,K,0,0,0,0,0,0,0,0,0,0,K,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,K,0,0,0,0,0,0,0,0,0,0,K,K,0,
					0,0,0,K,K,0,0,0,0,0,0,K,K,0,0,0,
					0,0,0,0,0,K,K,0,0,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.SHAPE;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private ShapeDrawObject makeShape(ToolEvent e) {
		float sx = e.getPreviousClickedX();
		float sy = e.getPreviousClickedY();
		float x = e.getX();
		float y = e.getY();
		if (e.isShiftDown() != e.tc().drawPerpendicular()) {
			double t = Math.toDegrees(Math.atan2(y-sy, x-sx));
			int q = (int)Math.round(t / 15.0);
			switch (q) {
			case -12: case 0: case 12:
				y = sy;
				break;
			case -18: case -6: case 6: case 18:
				x = sx;
				break;
			case -21: case -15: case -9: case -3:
			case 3: case 9: case 15: case 21:
				{
					float w = Math.abs(x-sx);
					float h = Math.abs(y-sy);
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
				break;
			case -23: case -22: case -14: case -13:
			case -11: case -10: case -2: case -1:
			case 1: case 2: case 10: case 11:
			case 13: case 14: case 22: case 23:
				{
					// x is fine, but find a new y
					double a = Math.toRadians(Math.round(t / 15.0) * 15.0);
					y = sy + (x-sx) * (float)Math.tan(a);
				}
				break;
			case -20: case -19: case -17: case -16:
			case -8: case -7: case -5: case -4:
			case 4: case 5: case 7: case 8:
			case 16: case 17: case 19: case 20:
				{
					// y is fine, but find a new x
					double a = Math.toRadians(Math.round(t / 15.0) * 15.0);
					x = sx + (y-sy) / (float)Math.tan(a);
				}
				break;
			}
		}
		return new RegularPolygonDrawObject(
			e.getPaintSettings(),
			e.tc().getPolygonSides(),
			e.tc().getPolygonStellation(),
			sx, sy, x, y,
			e.isAltDown() != e.tc().drawFromCenter()
		);
	}
	
	private ShapeDrawObject makeObject(ToolEvent e) {
		ShapeDrawObject sh = makeShape(e);
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
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isMouseDown()) {
			ShapeDrawObject wsh = makeObject(e);
			wsh.paint(g);
			return true;
		}
		else return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
			e.tc().decrementPolygonSides();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
			e.tc().incrementPolygonSides();
			break;
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_ADD:
		case KeyEvent.VK_EQUALS:
			e.tc().incrementPolygonStellation();
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			e.tc().decrementPolygonStellation();
			break;
		case KeyEvent.VK_3:
			e.tc().setPolygonSides(3);
			break;
		case KeyEvent.VK_4:
			e.tc().setPolygonSides(4);
			break;
		case KeyEvent.VK_5:
			e.tc().setPolygonSides(5);
			break;
		case KeyEvent.VK_6:
			e.tc().setPolygonSides(6);
			break;
		case KeyEvent.VK_7:
			e.tc().setPolygonSides(7);
			break;
		case KeyEvent.VK_8:
			e.tc().setPolygonSides(8);
			break;
		case KeyEvent.VK_9:
			e.tc().setPolygonSides(9);
			break;
		case KeyEvent.VK_0:
			e.tc().setPolygonSides(10);
			break;
		case KeyEvent.VK_1:
			e.tc().setPolygonSides(11);
			break;
		case KeyEvent.VK_2:
			e.tc().setPolygonSides(12);
			break;
		}
		if (e.isMouseDown() && e.isCtrlDown() != e.tc().drawMultiple()) {
			ShapeDrawObject wsh = makeObject(e);
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			return true;
		}
		return false;
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
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
