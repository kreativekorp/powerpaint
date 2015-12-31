package com.kreative.paint.tool;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import com.kreative.paint.draw.BrushStrokeDrawObject;

public class CurlBrushTool extends AbstractPaintDrawTool implements ToolOptions.Brushes, ToolOptions.Curl {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,K,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,K,0,0,0,0,0,0,0,0,K,0,K,0,0,0,
					0,K,0,K,K,0,0,0,0,0,K,K,K,0,0,0,
					0,0,K,0,0,K,0,0,0,0,K,K,K,0,0,0,
					0,K,0,K,K,0,0,0,0,0,K,K,K,0,0,0,
					0,K,0,0,0,0,0,0,0,0,K,K,K,0,0,0,
					0,K,0,K,K,0,0,K,K,K,K,K,K,K,K,K,
					0,0,K,0,0,K,0,K,0,0,0,0,0,0,0,K,
					0,K,0,K,K,0,0,K,K,K,K,K,K,K,K,K,
					0,K,0,0,0,0,0,K,0,0,0,0,0,0,0,K,
					0,K,0,K,K,0,0,K,0,0,0,0,0,0,0,K,
					0,0,K,0,0,K,0,K,0,0,0,0,0,0,0,K,
					0,K,0,K,K,0,0,K,0,K,0,K,0,K,0,K,
					0,K,0,0,0,0,K,0,K,0,K,0,K,0,K,K,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,K,0,
			}
	);
	private static final double TP = 2.0 * Math.PI;
	private static final double FP = 4.0 * Math.PI;
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public double angle = 0.0;
	private GeneralPath currentPath;
	
	private double rc = 0.0, sc = 0.0;
	private void makeCache(ToolEvent e) {
		rc = e.tc().getRawCurlRadius();
		sc = e.tc().getRawCurlSpacing();
	}
	
	public boolean toolSelected(ToolEvent e) {
		currentPath = null;
		makeCache(e);
		return false;
	}
	
	public boolean toolSettingsChanged(ToolEvent e) {
		makeCache(e);
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		currentPath = null;
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		// Opening
		e.beginTransaction(getName());
		currentPath = new GeneralPath();
		// First Plot
		double x = e.getX() + rc * Math.cos(angle);
		double y = e.getY() + rc * Math.sin(angle);
		currentPath.moveTo((float)x, (float)y);
		// Return
		return false;
	}

	public boolean mouseDragged(ToolEvent e) {
		double sx = e.getPreviousX();
		double sy = e.getPreviousY();
		double dx = e.getX();
		double dy = e.getY();
		double sa = angle;
		double da = angle + TP * Math.hypot(dx-sx, dy-sy) / sc;
		int m = (int)Math.ceil(Math.abs(da-sa)*rc);
		for (int s = 0; s < m; s++) {
			double dx2 = sx + ((dx-sx)*(s+1))/m;
			double dy2 = sy + ((dy-sy)*(s+1))/m;
			double da2 = sa + ((da-sa)*(s+1))/m;
			dx2 += rc * Math.cos(da2);
			dy2 += rc * Math.sin(da2);
			currentPath.lineTo((float)dx2, (float)dy2);
		}
		angle = da;
		if (angle > FP) angle -= FP;
		if (angle < -FP) angle += FP;
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		// Last Plot
		double x = e.getX() + rc * Math.cos(angle);
		double y = e.getY() + rc * Math.sin(angle);
		currentPath.lineTo((float)x, (float)y);
		// Closing
		BrushStrokeDrawObject o = new BrushStrokeDrawObject(
			e.getPaintSettings(), currentPath, e.tc().getBrush());
		if (e.isInDrawMode()) e.getDrawSurface().add(o);
		else o.paint(e.getPaintGraphics());
		currentPath = null;
		e.commitTransaction();
		// Return
		return true;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (currentPath != null) {
			BrushStrokeDrawObject o = new BrushStrokeDrawObject(
				e.getPaintSettings(), currentPath, e.tc().getBrush());
			o.paint(g);
		}
		if (e.isMouseOnCanvas()) {
			g.setComposite(AlphaComposite.SrcOver);
			g.setStroke(new BasicStroke(1));
			g.setColor(new Color(0x80000000,true));
			g.draw(new Ellipse2D.Double(e.getX()-rc, e.getY()-rc, rc*2.0, rc*2.0));
			g.setColor(new Color(0xC0000000,true));
			GeneralPath p = new GeneralPath();
			float dx = (float)(e.getX() + rc*Math.cos(angle));
			float dy = (float)(e.getY() + rc*Math.sin(angle));
			p.moveTo(dx-2.0f, dy);
			p.lineTo(dx+2.0f, dy);
			p.moveTo(dx, dy-2.0f);
			p.lineTo(dx, dy+2.0f);
			g.draw(p);
		}
		return true;
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
		case KeyEvent.VK_BACK_QUOTE:
			e.tc().toggleCurlCCW();
			break;
		case KeyEvent.VK_EQUALS:
		case KeyEvent.VK_PLUS:
		case KeyEvent.VK_ADD:
			if (e.isShiftDown()) {
				e.tc().incrementCurlSpacing();
			} else {
				e.tc().incrementCurlRadius();
			}
			break;
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			if (e.isShiftDown()) {
				e.tc().decrementCurlSpacing();
			} else {
				e.tc().decrementCurlRadius();
			}
			break;
		case KeyEvent.VK_1:
			if (e.isShiftDown()) e.tc().setCurlSpacing(5);
			else e.tc().setCurlRadius(5);
			break;
		case KeyEvent.VK_2:
			if (e.isShiftDown()) e.tc().setCurlSpacing(10);
			else e.tc().setCurlRadius(10);
			break;
		case KeyEvent.VK_3:
			if (e.isShiftDown()) e.tc().setCurlSpacing(15);
			else e.tc().setCurlRadius(15);
			break;
		case KeyEvent.VK_4:
			if (e.isShiftDown()) e.tc().setCurlSpacing(20);
			else e.tc().setCurlRadius(20);
			break;
		case KeyEvent.VK_5:
			if (e.isShiftDown()) e.tc().setCurlSpacing(25);
			else e.tc().setCurlRadius(25);
			break;
		case KeyEvent.VK_6:
			if (e.isShiftDown()) e.tc().setCurlSpacing(30);
			else e.tc().setCurlRadius(30);
			break;
		case KeyEvent.VK_7:
			if (e.isShiftDown()) e.tc().setCurlSpacing(35);
			else e.tc().setCurlRadius(35);
			break;
		case KeyEvent.VK_8:
			if (e.isShiftDown()) e.tc().setCurlSpacing(40);
			else e.tc().setCurlRadius(40);
			break;
		case KeyEvent.VK_9:
			if (e.isShiftDown()) e.tc().setCurlSpacing(45);
			else e.tc().setCurlRadius(45);
			break;
		case KeyEvent.VK_0:
			if (e.isShiftDown()) e.tc().setCurlSpacing(50);
			else e.tc().setCurlRadius(50);
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getBrush().getPreparedCursor(true);
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
}
