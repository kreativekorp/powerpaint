package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.draw.ThreeDBoxDrawObject;

public class ThreeDBoxTool extends AbstractPaintDrawTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter, ToolOptions.DrawFilled {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,K,K,K,K,K,K,K,0,0,0,0,0,0,0,
					0,K,K,0,0,0,0,0,K,K,0,0,0,0,0,0,
					0,K,0,K,0,0,0,0,K,0,K,K,0,0,0,0,
					0,K,0,0,K,0,0,0,K,0,0,0,K,0,0,0,
					0,K,0,0,0,K,K,K,K,K,K,K,K,K,K,0,
					0,K,0,0,0,K,0,0,K,0,0,0,0,0,K,0,
					0,K,0,0,0,K,0,0,K,0,0,0,0,0,K,0,
					0,K,K,K,K,K,K,K,K,0,0,0,0,0,K,0,
					0,0,K,0,0,K,0,0,0,K,0,0,0,0,K,0,
					0,0,0,K,0,K,0,0,0,0,K,0,0,0,K,0,
					0,0,0,K,0,K,0,0,0,0,0,K,0,0,K,0,
					0,0,0,0,K,K,0,0,0,0,0,0,K,0,K,0,
					0,0,0,0,0,K,0,0,0,0,0,0,0,K,K,0,
					0,0,0,0,0,K,K,K,K,K,K,K,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private Rectangle2D theBoundingArea = null;
	private double lastX, lastY;
	
	public boolean toolSelected(ToolEvent e) {
		theBoundingArea = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		theBoundingArea = null;
		return false;
	}
	
	private Rectangle2D getDrawnBounds(ToolEvent e) {
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
		return new Rectangle2D.Float(Math.min(sx,x), Math.min(sy,y), Math.abs(x-sx), Math.abs(y-sy));
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (theBoundingArea != null) {
			double dx = e.getX()-lastX;
			double dy = e.getY()-lastY;
			if (e.isShiftDown() != e.tc().drawSquare()) {
				double s = Math.max(Math.abs(dx), Math.abs(dy));
				dx = Math.signum(dx)*s;
				dy = Math.signum(dy)*s;
			}
			PaintSettings ps = e.getPaintSettings();
			if (e.isCtrlDown() == e.tc().drawFilled()) {
				ps = ps.deriveFillPaint(null);
			}
			new ThreeDBoxDrawObject(
				ps,
				theBoundingArea.getX(),
				theBoundingArea.getY(),
				theBoundingArea.getWidth(),
				theBoundingArea.getHeight(),
				dx,
				dy
			).paint(g);
			return true;
		} else if (e.isMouseDown()) {
			Rectangle2D r = getDrawnBounds(e);
			PaintSettings ps = e.getPaintSettings();
			if (e.isCtrlDown() == e.tc().drawFilled()) {
				ps = ps.deriveFillPaint(null);
			}
			new ShapeDrawObject.Rectangle(
				ps,
				r.getX(),
				r.getY(),
				r.getWidth(),
				r.getHeight()
			).paint(g);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (theBoundingArea == null) {
			theBoundingArea = getDrawnBounds(e);
			lastX = e.getX();
			lastY = e.getY();
			return true;
		} else {
			double dx = e.getX()-lastX;
			double dy = e.getY()-lastY;
			if (e.isShiftDown() != e.tc().drawSquare()) {
				double s = Math.max(Math.abs(dx), Math.abs(dy));
				dx = Math.signum(dx)*s;
				dy = Math.signum(dy)*s;
			}
			PaintSettings ps = e.getPaintSettings();
			if (e.isCtrlDown() == e.tc().drawFilled()) {
				ps = ps.deriveFillPaint(null);
			}
			ThreeDBoxDrawObject theBox = new ThreeDBoxDrawObject(
				ps,
				theBoundingArea.getX(),
				theBoundingArea.getY(),
				theBoundingArea.getWidth(),
				theBoundingArea.getHeight(),
				dx,
				dy
			);
			e.beginTransaction(getName());
			if (e.isInDrawMode()) e.getDrawSurface().add(theBox);
			else theBox.paint(e.getPaintGraphics());
			e.commitTransaction();
			theBoundingArea = null;
			return true;
		}
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
