package com.kreative.paint.tool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.tile.PaintSurface;
import com.kreative.paint.draw.PencilStrokeDrawObject;
import com.kreative.paint.material.pattern.PatternPaint;
import com.kreative.paint.util.CursorUtils;

public class PencilTool extends AbstractPaintDrawTool {
	private static final int K = 0xFF000000;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,K,0,0,0,0,K,0,0,0,
					0,0,0,0,0,0,0,K,K,0,0,K,0,0,0,0,
					0,0,0,0,0,0,K,0,0,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,K,0,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,K,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,K,K,0,0,0,0,
					0,0,0,0,0,0,0,0,K,W,W,W,K,0,0,0,
					0,0,0,0,0,0,0,K,W,W,W,W,K,0,0,0,
					0,0,0,0,0,0,0,K,K,W,W,K,0,0,0,0,
					0,0,0,0,0,0,K,W,W,K,K,K,0,0,0,0,
					0,0,0,0,0,0,K,W,W,W,K,0,0,0,0,0,
					0,0,0,0,0,K,W,W,W,W,K,0,0,0,0,0,
					0,0,0,0,0,K,W,W,W,K,0,0,0,0,0,0,
					0,0,0,0,K,W,W,W,W,K,0,0,0,0,0,0,
					0,0,0,0,K,W,W,W,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,W,W,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
			},
			4, 15,
			"Pencil"
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private boolean eraseMode;
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
			PaintSettings ps = e.getPaintSettings();
			PaintSurface p = e.getPaintSurface();
			int x = (int)Math.floor(e.getX());
			int y = (int)Math.floor(e.getY());
			Paint pnt = ps.fillPaint;
			if (pnt instanceof PatternPaint) {
				pnt = ((PatternPaint)pnt).foreground;
			}
			if (pnt instanceof Color) {
				eraseMode = (p.getRGB(x,y) == ((Color)pnt).getRGB());
			} else {
				eraseMode = false;
			}
			if (eraseMode) {
				p.clear(x, y, 1, 1);
			} else {
				Graphics2D g = e.getPaintGraphics();
				ps.applyFill(g);
				g.setStroke(new BasicStroke(1));
				g.fillRect(x, y, 1, 1);
			}
			return true;
		} else {
			currentPoly = new GeneralPath();
			currentPoly.moveTo(e.getX(), e.getY());
			return false;
		}
	}
	
	private void drag(PaintSurface p, int sx, int sy, int dx, int dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			int x = sx + ((dx-sx)*i)/m;
			int y = sy + ((dy-sy)*i)/m;
			p.clear(x, y, 1, 1);
		}
	}

	public boolean mouseDragged(ToolEvent e) {
		if (e.isInPaintMode()) {
			int px = (int)Math.floor(e.getPreviousX());
			int py = (int)Math.floor(e.getPreviousY());
			int x = (int)Math.floor(e.getX());
			int y = (int)Math.floor(e.getY());
			if (eraseMode) {
				drag(e.getPaintSurface(), px, py, x, y);
			} else {
				Graphics2D g = e.getPaintGraphics();
				PaintSettings ps = e.getPaintSettings();
				ps.applyFill(g);
				g.setStroke(new BasicStroke(1));
				g.drawLine(px, py, x, y);
			}
			return true;
		} else if (currentPoly != null) {
			currentPoly.lineTo(e.getX(), e.getY());
			return false;
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (e.isInDrawMode() && currentPoly != null) {
			currentPoly.lineTo(e.getX(), e.getY());
			PencilStrokeDrawObject o = new PencilStrokeDrawObject(e.getPaintSettings(), currentPoly);
			e.getDrawSurface().add(o);
			currentPoly = null;
			e.commitTransaction();
			return true;
		}
		e.commitTransaction();
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isInDrawMode() && currentPoly != null) {
			PencilStrokeDrawObject o = new PencilStrokeDrawObject(e.getPaintSettings(), currentPoly);
			o.paint(g);
			return true;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
}
