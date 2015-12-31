package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import com.kreative.paint.ToolContext;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.GroupDrawObject;
import com.kreative.paint.draw.BrushStrokeDrawObject;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.form.EnumOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.material.sprite.Sprite;

public class MirrorBrushTool extends AbstractPaintDrawTool implements ToolOptions.Brushes, ToolOptions.Custom {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,K,K,K,K,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,K,0,0,0,
					0,0,0,0,K,K,K,K,0,K,K,K,K,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,K,0,0,0,
					0,0,0,0,K,0,0,0,K,0,0,0,K,0,0,0,
					0,0,0,0,K,0,K,0,0,0,K,0,K,0,0,0,
					0,0,0,K,0,K,0,K,K,K,0,K,0,K,0,0,
					0,0,K,K,K,K,K,K,0,K,K,K,K,K,K,0,
			}
	);
	
	public static enum CenterPoint {
		CANVAS_CENTER,
		CANVAS_HOTSPOT,
		CANVAS_ORIGIN,
		LAYER_ORIGIN,
		DRAG_START;
	}
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private GeneralPath currentPath;
	
	private boolean rvc = false, rhc = false;
	private CenterPoint cpc = null;
	private void makeCache(ToolEvent e) {
		rvc = e.tc().getCustom(MirrorBrushTool.class, "refVert", Boolean.class, true);
		rhc = e.tc().getCustom(MirrorBrushTool.class, "refHoriz", Boolean.class, true);
		cpc = e.tc().getCustom(MirrorBrushTool.class, "centerPoint", CenterPoint.class, CenterPoint.CANVAS_CENTER);
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
		e.beginTransaction(getName());
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			Sprite brush = e.tc().getBrush();
			Point2D center = getCenterPoint(e);
			{
				float x = e.getX();
				float y = e.getY();
				brush.paint(g, (int)x, (int)y);
			}
			if (rvc) {
				float x = (float)center.getX() * 2 - e.getX();
				float y = e.getY();
				brush.paint(g, (int)x, (int)y);
			}
			if (rhc) {
				float x = e.getX();
				float y = (float)center.getY() * 2 - e.getY();
				brush.paint(g, (int)x, (int)y);
			}
			if (rvc && rhc) {
				float x = (float)center.getX() * 2 - e.getX();
				float y = (float)center.getY() * 2 - e.getY();
				brush.paint(g, (int)x, (int)y);
			}
			return true;
		} else {
			currentPath = new GeneralPath();
			currentPath.moveTo(e.getX(), e.getY());
			return false;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			Sprite brush = e.tc().getBrush();
			Point2D center = getCenterPoint(e);
			{
				float lastX = e.getPreviousX();
				float lastY = e.getPreviousY();
				float x = e.getX();
				float y = e.getY();
				drag(brush, g, lastX, lastY, x, y);
			}
			if (rvc) {
				float lastX = (float)center.getX() * 2 - e.getPreviousX();
				float lastY = e.getPreviousY();
				float x = (float)center.getX() * 2 - e.getX();
				float y = e.getY();
				drag(brush, g, lastX, lastY, x, y);
			}
			if (rhc) {
				float lastX = e.getPreviousX();
				float lastY = (float)center.getY() * 2 - e.getPreviousY();
				float x = e.getX();
				float y = (float)center.getY() * 2 - e.getY();
				drag(brush, g, lastX, lastY, x, y);
			}
			if (rvc && rhc) {
				float lastX = (float)center.getX() * 2 - e.getPreviousX();
				float lastY = (float)center.getY() * 2 - e.getPreviousY();
				float x = (float)center.getX() * 2 - e.getX();
				float y = (float)center.getY() * 2 - e.getY();
				drag(brush, g, lastX, lastY, x, y);
			}
			return true;
		} else if (currentPath != null) {
			currentPath.lineTo(e.getX(), e.getY());
			return false;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			Sprite brush = e.tc().getBrush();
			Point2D center = getCenterPoint(e);
			{
				float lastX = e.getPreviousX();
				float lastY = e.getPreviousY();
				float x = e.getX();
				float y = e.getY();
				drag(brush, g, lastX, lastY, x, y);
			}
			if (rvc) {
				float lastX = (float)center.getX() - e.getPreviousX();
				float lastY = e.getPreviousY();
				float x = (float)center.getX() - e.getX();
				float y = e.getY();
				drag(brush, g, lastX, lastY, x, y);
			}
			if (rhc) {
				float lastX = e.getPreviousX();
				float lastY = (float)center.getY() - e.getPreviousY();
				float x = e.getX();
				float y = (float)center.getY() - e.getY();
				drag(brush, g, lastX, lastY, x, y);
			}
			if (rvc && rhc) {
				float lastX = (float)center.getX() - e.getPreviousX();
				float lastY = (float)center.getY() - e.getPreviousY();
				float x = (float)center.getX() - e.getX();
				float y = (float)center.getY() - e.getY();
				drag(brush, g, lastX, lastY, x, y);
			}
			e.commitTransaction();
			return true;
		} else if (currentPath != null) {
			float x = e.getX();
			float y = e.getY();
			currentPath.lineTo(x, y);
			Sprite brush = e.tc().getBrush();
			Point2D center = getCenterPoint(e);
			List<DrawObject> objects = new ArrayList<DrawObject>();
			{
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(e.getPaintSettings(), currentPath, brush);
				objects.add(o);
			}
			if (rvc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(e.getPaintSettings(), currentPath, brush);
				o.setTransform(new AffineTransform(
					-1, 0, 0, 1, (float)center.getX() * 2, 0
				));
				objects.add(o);
			}
			if (rhc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(e.getPaintSettings(), currentPath, brush);
				o.setTransform(new AffineTransform(
					1, 0, 0, -1, 0, (float)center.getY() * 2
				));
				objects.add(o);
			}
			if (rvc && rhc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(e.getPaintSettings(), currentPath, brush);
				o.setTransform(new AffineTransform(
					-1, 0, 0, -1,
					(float)center.getX() * 2,
					(float)center.getY() * 2
				));
				objects.add(o);
			}
			GroupDrawObject group = new GroupDrawObject(objects);
			e.getDrawSurface().add(group);
			currentPath = null;
			e.commitTransaction();
			return true;
		} else {
			e.commitTransaction();
			return false;
		}
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (e.isInDrawMode() && currentPath != null) {
			Sprite brush = e.tc().getBrush();
			Point2D center = getCenterPoint(e);
			{
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(e.getPaintSettings(), currentPath, brush);
				o.paint(g);
			}
			if (rvc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(e.getPaintSettings(), currentPath, brush);
				o.setTransform(new AffineTransform(
					-1, 0, 0, 1, (float)center.getX() * 2, 0
				));
				o.paint(g);
			}
			if (rhc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(e.getPaintSettings(), currentPath, brush);
				o.setTransform(new AffineTransform(
					1, 0, 0, -1, 0, (float)center.getY() * 2
				));
				o.paint(g);
			}
			if (rvc && rhc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(e.getPaintSettings(), currentPath, brush);
				o.setTransform(new AffineTransform(
					-1, 0, 0, -1,
					(float)center.getX() * 2,
					(float)center.getY() * 2
				));
				o.paint(g);
			}
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
		case KeyEvent.VK_MINUS:
		case KeyEvent.VK_SUBTRACT:
			e.tc().setCustom(MirrorBrushTool.class, "centerPoint", CenterPoint.DRAG_START);
			break;
		case KeyEvent.VK_EQUALS:
		case KeyEvent.VK_ADD:
			e.tc().setCustom(MirrorBrushTool.class, "centerPoint", CenterPoint.CANVAS_CENTER);
			break;
		case KeyEvent.VK_OPEN_BRACKET:
			e.tc().toggleCustom(MirrorBrushTool.class, "refVert", true);
			break;
		case KeyEvent.VK_CLOSE_BRACKET:
			e.tc().toggleCustom(MirrorBrushTool.class, "refHoriz", true);
			break;
		}
		return false;
	}
	
	private Point2D getCenterPoint(ToolEvent e) {
		switch (cpc) {
		case CANVAS_CENTER:
			return new Point2D.Float(
					e.getCanvas().getWidth()/2 - e.getLayer().getX(),
					e.getCanvas().getHeight()/2 - e.getLayer().getY()
			);
		case CANVAS_HOTSPOT:
			return new Point2D.Float(
					e.getCanvas().getHotspotX() - e.getLayer().getX(),
					e.getCanvas().getHotspotY() - e.getLayer().getY()
			);
		case CANVAS_ORIGIN:
			return new Point2D.Float(
					-e.getLayer().getX(),
					-e.getLayer().getY()
			);
		case LAYER_ORIGIN:
			return new Point2D.Float(0,0);
		case DRAG_START:
			return new Point2D.Float(
					e.getPreviousClickedX(),
					e.getPreviousClickedY()
			);
		default:
			return new Point2D.Float(
					e.getCanvas().getWidth()/2 - e.getLayer().getX(),
					e.getCanvas().getHeight()/2 - e.getLayer().getY()
			);
		}
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

	public Form getCustomOptionsForm(final ToolContext tc) {
		Form f = new Form();
		f.add(new EnumOption<CenterPoint>() {
			public String getName() { return ToolUtilities.messages.getString("mirror.options.CenterPoint"); }
			public CenterPoint getValue() { return tc.getCustom(MirrorBrushTool.class, "centerPoint", CenterPoint.class, CenterPoint.CANVAS_CENTER); }
			public void setValue(CenterPoint v) { tc.setCustom(MirrorBrushTool.class, "centerPoint", v); }
			public CenterPoint[] values() { return CenterPoint.values(); }
			public String getLabel(CenterPoint cp) { return ToolUtilities.messages.getString("mirror.options.CenterPoint."+cp.name()); }
		});
		f.add(new BooleanOption() {
			public String getName() { return ""; }
			public boolean getValue() { return tc.getCustom(MirrorBrushTool.class, "refVert", Boolean.class, true); }
			public void setValue(boolean v) { tc.setCustom(MirrorBrushTool.class, "refVert", v); }
			public boolean useTrueFalseLabels() { return false; }
			public String getLabel(boolean v) { return ToolUtilities.messages.getString("mirror.options.ReflectVertical"); }
		});
		f.add(new BooleanOption() {
			public String getName() { return ""; }
			public boolean getValue() { return tc.getCustom(MirrorBrushTool.class, "refHoriz", Boolean.class, true); }
			public void setValue(boolean v) { tc.setCustom(MirrorBrushTool.class, "refHoriz", v); }
			public boolean useTrueFalseLabels() { return false; }
			public String getLabel(boolean v) { return ToolUtilities.messages.getString("mirror.options.ReflectHorizontal"); }
		});
		return f;
	}
}
