/*
 * Copyright &copy; 2009-2010 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since PowerPaint 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import com.kreative.paint.PaintSurface;
import com.kreative.paint.ToolContext;
import com.kreative.paint.draw.BrushStrokeDrawObject;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.form.EnumOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.util.Bitmap;

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
	
	private Bitmap brushCache = null;
	private Cursor brushCursor = null;
	private int brushW = 0, brushH = 0;
	private boolean rvc = false, rhc = false;
	private CenterPoint cpc = null;
	private void makeCache(ToolEvent e) {
		brushCache = e.tc().getBrush();
		brushCursor = brushCache.getCursor();
		brushW = brushCache.getWidth()/2;
		brushH = brushCache.getHeight()/2;
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
		if (brushCache == null) makeCache(e);
		e.beginTransaction(getName());
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			Point2D center = getCenterPoint(e);
			{
				float x = e.getX() - brushW;
				float y = e.getY() - brushH;
				brushCache.paint(e.getPaintSurface(), g, (int)x, (int)y);
			}
			if (rvc) {
				float x = 2.0f*(float)center.getX() - e.getX() - brushW;
				float y = e.getY() - brushH;
				brushCache.paint(e.getPaintSurface(), g, (int)x, (int)y);
			}
			if (rhc) {
				float x = e.getX() - brushW;
				float y = 2.0f*(float)center.getY() - e.getY() - brushH;
				brushCache.paint(e.getPaintSurface(), g, (int)x, (int)y);
			}
			if (rvc && rhc) {
				float x = 2.0f*(float)center.getX() - e.getX() - brushW;
				float y = 2.0f*(float)center.getY() - e.getY() - brushH;
				brushCache.paint(e.getPaintSurface(), g, (int)x, (int)y);
			}
			return true;
		} else {
			currentPath = new GeneralPath();
			currentPath.moveTo(e.getX(), e.getY());
			return false;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (brushCache == null) makeCache(e);
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			Point2D center = getCenterPoint(e);
			{
				float lastX = e.getPreviousX();
				float lastY = e.getPreviousY();
				float x = e.getX();
				float y = e.getY();
				drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			}
			if (rvc) {
				float lastX = 2.0f*(float)center.getX() - e.getPreviousX();
				float lastY = e.getPreviousY();
				float x = 2.0f*(float)center.getX() - e.getX();
				float y = e.getY();
				drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			}
			if (rhc) {
				float lastX = e.getPreviousX();
				float lastY = 2.0f*(float)center.getY() - e.getPreviousY();
				float x = e.getX();
				float y = 2.0f*(float)center.getY() - e.getY();
				drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			}
			if (rvc && rhc) {
				float lastX = 2.0f*(float)center.getX() - e.getPreviousX();
				float lastY = 2.0f*(float)center.getY() - e.getPreviousY();
				float x = 2.0f*(float)center.getX() - e.getX();
				float y = 2.0f*(float)center.getY() - e.getY();
				drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			}
			return true;
		} else if (currentPath != null) {
			float x = e.getX();
			float y = e.getY();
			currentPath.lineTo(x, y);
			return false;
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (brushCache == null) makeCache(e);
		if (e.isInPaintMode()) {
			Graphics2D g = e.getPaintGraphics();
			e.getPaintSettings().applyFill(g);
			Point2D center = getCenterPoint(e);
			{
				float lastX = e.getPreviousX();
				float lastY = e.getPreviousY();
				float x = e.getX();
				float y = e.getY();
				drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			}
			if (rvc) {
				float lastX = (float)center.getX() - e.getPreviousX();
				float lastY = e.getPreviousY();
				float x = (float)center.getX() - e.getX();
				float y = e.getY();
				drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			}
			if (rhc) {
				float lastX = e.getPreviousX();
				float lastY = (float)center.getY() - e.getPreviousY();
				float x = e.getX();
				float y = (float)center.getY() - e.getY();
				drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			}
			if (rvc && rhc) {
				float lastX = (float)center.getX() - e.getPreviousX();
				float lastY = (float)center.getY() - e.getPreviousY();
				float x = (float)center.getX() - e.getX();
				float y = (float)center.getY() - e.getY();
				drag(e.getPaintSurface(), g, lastX, lastY, x, y);
			}
			e.commitTransaction();
			return true;
		} else if (currentPath != null) {
			float x = e.getX();
			float y = e.getY();
			currentPath.lineTo(x, y);
			Point2D center = getCenterPoint(e);
			{
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPath, e.getPaintSettings());
				e.getDrawSurface().add(o);
			}
			if (rvc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPath, e.getPaintSettings());
				o.setTransform(new AffineTransform(-1, 0, 0, 1, 2.0f*(float)center.getX(), 0));
				e.getDrawSurface().add(o);
			}
			if (rhc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPath, e.getPaintSettings());
				o.setTransform(new AffineTransform(1, 0, 0, -1, 0, 2.0f*(float)center.getY()));
				e.getDrawSurface().add(o);
			}
			if (rvc && rhc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPath, e.getPaintSettings());
				o.setTransform(new AffineTransform(-1, 0, 0, -1, 2.0f*(float)center.getX(), 2.0f*(float)center.getY()));
				e.getDrawSurface().add(o);
			}
			currentPath = null;
			e.commitTransaction();
			return true;
		}
		e.commitTransaction();
		return false;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (brushCache == null) makeCache(e);
		if (e.isInDrawMode() && currentPath != null) {
			Point2D center = getCenterPoint(e);
			{
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPath, e.getPaintSettings());
				o.paint(g);
			}
			if (rvc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPath, e.getPaintSettings());
				o.setTransform(new AffineTransform(-1, 0, 0, 1, 2.0f*(float)center.getX(), 0));
				o.paint(g);
			}
			if (rhc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPath, e.getPaintSettings());
				o.setTransform(new AffineTransform(1, 0, 0, -1, 0, 2.0f*(float)center.getY()));
				o.paint(g);
			}
			if (rvc && rhc) {
				BrushStrokeDrawObject o = new BrushStrokeDrawObject(brushCache, currentPath, e.getPaintSettings());
				o.setTransform(new AffineTransform(-1, 0, 0, -1, 2.0f*(float)center.getX(), 2.0f*(float)center.getY()));
				o.paint(g);
			}
			return true;
		}
		return false;
	}
	
	private void drag(PaintSurface srf, Graphics2D g, float sx, float sy, float dx, float dy) {
		int m = (int)Math.ceil(Math.max(Math.abs(dx-sx),Math.abs(dy-sy)));
		for (int i = 1; i <= m; i++) {
			float x = sx + ((dx-sx)*i)/m - brushW;
			float y = sy + ((dy-sy)*i)/m - brushH;
			brushCache.paint(srf, g, (int)x, (int)y);
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
		if (brushCursor == null) makeCache(e);
		return brushCursor;
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
