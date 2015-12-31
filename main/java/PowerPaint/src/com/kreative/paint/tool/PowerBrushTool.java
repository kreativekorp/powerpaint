package com.kreative.paint.tool;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import com.kreative.paint.draw.PowerBrushStrokeDrawObject;
import com.kreative.paint.powerbrush.BrushSettings;
import com.kreative.paint.util.CursorUtils;

public class PowerBrushTool extends AbstractPaintDrawTool implements ToolOptions.PowerBrush {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,K,K,
					0,0,0,K,K,0,0,K,K,K,0,0,0,K,K,0,
					0,0,K,K,K,0,0,K,0,K,0,0,K,K,K,0,
					0,K,K,K,0,0,0,K,K,K,0,K,K,K,0,0,
					K,K,K,K,K,K,0,K,K,K,K,K,K,K,K,K,
					0,0,K,K,K,0,0,K,K,K,0,0,K,K,K,0,
					0,K,K,K,0,0,0,K,K,K,0,K,K,K,0,0,
					0,K,K,0,K,K,K,K,K,K,K,K,K,0,0,0,
					K,K,0,0,K,0,0,0,0,0,K,K,K,0,0,0,
					K,0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,K,0,K,0,K,0,K,0,0,0,
					0,0,0,K,0,K,0,K,0,K,0,K,K,0,0,0,
					0,0,K,K,K,K,K,K,K,K,K,K,0,0,0,0,
			}
	);
	private static final Cursor none = CursorUtils.makeCursor(1, 1, new int[] {0}, 0, 0, "None");
	private static final Cursor cross = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.PAINT;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private GeneralPath currentPoly;
	
	private BrushSettings cache = null;
	private Shape bscache = null;
	private Cursor ccache = null;
	
	private void makeCache(ToolEvent e) {
		cache = e.tc().getPowerBrushSettings();
		bscache = cache.getBrushShape().makeBrush(0, 0, cache.getOuterWidth(), cache.getOuterHeight());
		ccache = (cache.getOuterWidth() > 32 || cache.getOuterHeight() > 32) ? cross : none;
	}
	
	public boolean toolSelected(ToolEvent e) {
		makeCache(e);
		currentPoly = null;
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		makeCache(e);
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPoly = null;
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		if (cache == null) makeCache(e);
		e.beginTransaction(getName());
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			cache.paint(g, e.getX(), e.getY());
			return true;
		} else {
			currentPoly = new GeneralPath();
			currentPoly.moveTo(e.getX(), e.getY());
			return false;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (cache == null) makeCache(e);
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			cache.paint(g, false, e.getPreviousX(), e.getPreviousY(), e.getX(), e.getY());
			return true;
		} else if (currentPoly != null) {
			currentPoly.lineTo(e.getX(), e.getY());
			return false;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (cache == null) makeCache(e);
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			cache.paint(g, false, e.getPreviousX(), e.getPreviousY(), e.getX(), e.getY());
			e.commitTransaction();
			return true;
		} else if (currentPoly != null) {
			currentPoly.lineTo(e.getX(), e.getY());
			PowerBrushStrokeDrawObject o = new PowerBrushStrokeDrawObject(e.getPaintSettings(), currentPoly, cache);
			e.getDrawSurface().add(o);
			currentPoly = null;
			e.commitTransaction();
			return true;
		} else {
			e.commitTransaction();
			return false;
		}
	}
	
	private static final Color CC = new Color(0x80808080, true);
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (cache == null || bscache == null) makeCache(e);
		if (e.isInDrawMode() && currentPoly != null) {
			PowerBrushStrokeDrawObject o = new PowerBrushStrokeDrawObject(e.getPaintSettings(), currentPoly, cache);
			o.paint(g);
			return true;
		}
		if (e.isMouseOnCanvas()) {
			g.setComposite(AlphaComposite.SrcOver);
			g.setPaint(CC);
			g.draw(AffineTransform.getTranslateInstance(e.getX(), e.getY()).createTransformedShape(bscache));
		}
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_OPEN_BRACKET:
			e.tc().decrementPowerBrush();
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_CLOSE_BRACKET:
			e.tc().incrementPowerBrush();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		if (ccache == null) makeCache(e);
		return ccache;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
