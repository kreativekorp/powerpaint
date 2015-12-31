package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import com.kreative.paint.document.draw.PathDrawObject;
import com.kreative.paint.document.draw.ShadowSettings;

// refactor this to use curves instead of lines ???

public class FreeformShapeTool extends AbstractPaintDrawTool implements ToolOptions.QuickShadow {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,K,K,0,0,0,0,0,0,K,K,K,0,0,
					0,K,0,0,0,K,K,0,0,0,K,0,0,0,K,0,
					K,0,0,0,0,0,0,K,K,K,0,0,0,0,0,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					K,0,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,K,K,0,0,0,0,0,K,K,0,0,0,0,
					0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,0,
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
	
	private GeneralPath currentPath;
	
	public boolean toolSelected(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		currentPath = new GeneralPath();
		currentPath.moveTo(e.getX(), e.getY());
		return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (currentPath != null) {
			currentPath.lineTo(e.getX(), e.getY());
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (currentPath != null) {
			currentPath.lineTo(e.getX(), e.getY());
			if (e.isCtrlDown()) {
				currentPath.closePath();
			}
			e.beginTransaction(getName());
			PathDrawObject wsh = new PathDrawObject(e.getPaintSettings(), currentPath);
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
			currentPath = null;
			return true;
		}
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (currentPath != null) {
			e.getPaintSettings().applyDraw(g);
			g.draw(currentPath);
			return true;
		}
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
}
