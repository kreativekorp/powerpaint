package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import com.kreative.paint.document.draw.PathDrawObject;
import com.kreative.paint.document.draw.ShadowSettings;

public class PolygonTool extends AbstractPaintDrawTool implements ToolOptions.QuickShadow {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,K,K,K,K,K,K,
					0,0,0,0,0,0,K,0,0,0,0,0,0,0,K,0,
					0,0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,K,0,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,0,K,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.SHAPE;
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
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (currentPoly != null) {
			e.getPaintSettings().applyDraw(g);
			g.draw(currentPoly);
			float sx = (float)currentPoly.getCurrentPoint().getX();
			float sy = (float)currentPoly.getCurrentPoint().getY();
			float x = e.getX();
			float y = e.getY();
			if (e.isShiftDown()) {
				float w = Math.abs(x-sx);
				float h = Math.abs(y-sy);
				if (w > h*2) y = sy;
				else if (h > w*2) x = sx;
				else {
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
			}
			g.draw(new Line2D.Float(sx, sy, x, y));
			return true;
		}
		return false;
	}
	
	public boolean mouseClicked(ToolEvent e) {
		if (currentPoly == null) {
			currentPoly = new GeneralPath();
			currentPoly.moveTo(e.getX(), e.getY());
			return false;
		}
		else if (currentPoly.getCurrentPoint().getX() == e.getX() || currentPoly.getCurrentPoint().getY() == e.getY()) {
			currentPoly.closePath();
			e.beginTransaction(getName());
			PathDrawObject wsh = new PathDrawObject(e.getPaintSettings(), currentPoly);
			if (e.tc().useShadow()) {
				wsh.setShadowSettings(new ShadowSettings(
					e.tc().getShadowType(),
					e.tc().getShadowOpacity(),
					e.tc().getShadowXOffset(),
					e.tc().getShadowYOffset()
				));
			}
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			e.commitTransaction();
			currentPoly = null;
			return true;
		}
		else if (e.getClickCount() > 1) {
			float x = e.getX();
			float y = e.getY();
			if (e.isShiftDown()) {
				float sx = (float)currentPoly.getCurrentPoint().getX();
				float sy = (float)currentPoly.getCurrentPoint().getY();
				float w = Math.abs(x-sx);
				float h = Math.abs(y-sy);
				if (w > h*2) y = sy;
				else if (h > w*2) x = sx;
				else {
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
			}
			currentPoly.lineTo(x, y);
			currentPoly.closePath();
			e.beginTransaction(getName());
			PathDrawObject wsh = new PathDrawObject(e.getPaintSettings(), currentPoly);
			if (e.tc().useShadow()) {
				wsh.setShadowSettings(new ShadowSettings(
					e.tc().getShadowType(),
					e.tc().getShadowOpacity(),
					e.tc().getShadowXOffset(),
					e.tc().getShadowYOffset()
				));
			}
			if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
			else wsh.paint(e.getPaintGraphics());
			e.commitTransaction();
			currentPoly = null;
			return true;
		}
		else {
			float x = e.getX();
			float y = e.getY();
			if (e.isShiftDown()) {
				float sx = (float)currentPoly.getCurrentPoint().getX();
				float sy = (float)currentPoly.getCurrentPoint().getY();
				float w = Math.abs(x-sx);
				float h = Math.abs(y-sy);
				if (w > h*2) y = sy;
				else if (h > w*2) x = sx;
				else {
					float s = Math.max(w, h);
					if (y > sy) y = sy+s;
					else y = sy-s;
					if (x > sx) x = sx+s;
					else x = sx-s;
				}
			}
			currentPoly.lineTo(x, y);
			return false;
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_CLEAR:
			currentPoly = null;
			return false;
		case KeyEvent.VK_ENTER:
			if (currentPoly != null) {
				float x = e.getX();
				float y = e.getY();
				if (e.isShiftDown()) {
					float sx = (float)currentPoly.getCurrentPoint().getX();
					float sy = (float)currentPoly.getCurrentPoint().getY();
					float w = Math.abs(x-sx);
					float h = Math.abs(y-sy);
					if (w > h*2) y = sy;
					else if (h > w*2) x = sx;
					else {
						float s = Math.max(w, h);
						if (y > sy) y = sy+s;
						else y = sy-s;
						if (x > sx) x = sx+s;
						else x = sx-s;
					}
				}
				currentPoly.lineTo(x, y);
				currentPoly.closePath();
				e.beginTransaction(getName());
				PathDrawObject wsh = new PathDrawObject(e.getPaintSettings(), currentPoly);
				if (e.tc().useShadow()) {
					wsh.setShadowSettings(new ShadowSettings(
						e.tc().getShadowType(),
						e.tc().getShadowOpacity(),
						e.tc().getShadowXOffset(),
						e.tc().getShadowYOffset()
					));
				}
				if (e.isInDrawMode()) e.getDrawSurface().add(wsh);
				else wsh.paint(e.getPaintGraphics());
				e.commitTransaction();
				currentPoly = null;
				return true;
			}
			else return false;
		}
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
}
