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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import com.kreative.paint.DrawSurface;
import com.kreative.paint.Layer;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.undo.Atom;
import com.kreative.paint.util.CursorUtils;

public class TransformTool extends AbstractPaintDrawSelectionTool {
	private static final int K = 0xFF000000;
	private static final int A = 0xFF808080;
	private static final int W = 0xFFFFFFFF;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,K,K,0,0,0,K,K,K,0,0,0,K,K,K,
					0,K,0,K,0,K,0,K,0,K,0,K,0,K,0,K,
					0,K,K,K,0,0,0,K,K,K,0,0,0,K,K,K,
					0,0,0,0,0,0,K,0,0,0,0,0,0,0,0,0,
					0,0,K,0,0,0,K,K,0,0,0,0,0,0,K,0,
					0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,0,
					0,K,K,K,0,0,K,K,K,K,0,0,0,K,K,K,
					0,K,0,K,0,0,K,K,K,K,K,0,0,K,0,K,
					0,K,K,K,0,0,K,K,K,K,K,K,0,K,K,K,
					0,0,0,0,0,0,K,K,0,0,0,0,0,0,0,0,
					0,0,K,0,0,0,K,0,0,0,0,0,0,0,K,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,K,K,0,0,0,K,K,K,0,0,0,K,K,K,
					0,K,0,K,0,K,0,K,0,K,0,K,0,K,0,K,
					0,K,K,K,0,0,0,K,K,K,0,0,0,K,K,K,
			}
	);
	private static final Cursor rotate_cursor = CursorUtils.makeCursor(
			17, 15,
			new int[] {
					0,0,0,0,0,0,0,W,W,W,W,W,0,0,0,0,0,
					0,0,0,0,0,W,W,K,K,K,K,K,W,W,0,0,0,
					0,0,0,0,W,K,K,W,W,W,W,W,K,K,W,0,0,
					0,0,0,W,K,W,W,0,0,0,0,0,W,W,K,W,0,
					0,0,0,W,K,W,0,0,0,0,0,0,0,W,K,W,0,
					0,W,W,K,W,W,0,0,0,0,0,0,0,0,W,K,W,
					W,K,W,K,W,K,W,0,0,0,0,0,0,0,W,K,W,
					0,W,K,K,K,W,0,0,0,0,0,0,0,0,W,K,W,
					0,W,K,K,K,W,0,0,0,0,0,0,0,0,W,K,W,
					0,0,W,K,W,0,0,0,0,0,0,0,0,0,W,K,W,
					0,0,W,K,W,0,0,0,0,0,0,0,0,W,K,W,0,
					0,0,0,W,0,0,0,0,0,0,0,0,W,W,K,W,0,
					0,0,0,0,0,0,0,0,0,W,W,W,K,K,W,0,0,
					0,0,0,0,0,0,0,0,0,W,K,K,W,W,0,0,0,
					0,0,0,0,0,0,0,0,0,W,W,W,0,0,0,0,0,
			},
			9, 7,
			"Rotate"
	);
	public static final Cursor half_arrow_gray = CursorUtils.makeCursor(
			16, 16,
			new int[] {
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,A,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,A,A,W,0,0,0,0,0,0,0,0,0,0,0,0,
					W,A,A,A,W,0,0,0,0,0,0,0,0,0,0,0,
					W,A,A,A,A,W,0,0,0,0,0,0,0,0,0,0,
					W,A,A,A,A,A,W,0,0,0,0,0,0,0,0,0,
					W,A,A,A,A,A,A,W,0,0,0,0,0,0,0,0,
					W,A,A,A,A,A,A,A,W,0,0,0,0,0,0,0,
					W,A,A,A,A,A,A,A,A,W,0,0,0,0,0,0,
					W,A,A,A,W,W,W,W,W,W,W,0,0,0,0,0,
					W,A,A,W,0,0,0,0,0,0,0,0,0,0,0,0,
					W,A,W,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					W,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			},
			1, 1,
			"HalfArrowGray"
	);
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean toolSelected(ToolEvent e) {
		e.beginTransaction(getName());
		if (isSelection(e)) pop(e);
		else reset(e);
		e.commitTransaction();
		return true;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		e.beginTransaction(getName());
		reset(e);
		e.commitTransaction();
		return true;
	}
	
	public boolean mouseEntered(ToolEvent e) {
		if (!e.isMouseDown()) updateCursor(e);
		return false;
	}
	
	public boolean mouseMoved(ToolEvent e) {
		if (!e.isMouseDown()) updateCursor(e);
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		if (!e.isMouseDown()) updateCursor(e);
		return false;
	}
	
	public boolean keyReleased(ToolEvent e) {
		if (!e.isMouseDown()) updateCursor(e);
		return false;
	}
	
	public boolean paintIntermediateUsesCanvasCoordinates() {
		return true;
	}

	private int clickedArea = 0;
	private boolean altDown = false;
	private boolean definingSelection = false;
	private boolean draggingSelection = false;
	private AffineTransform dragTransform = null;
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		if (isSelection(e)) update(e);
		else reset(e);
		updateCursor(e);
		clickedArea = getCurrentArea(e);
		altDown = e.isAltDown();
		if (clickedArea == 0) {
			reset(e);
			updateCursor(e);
			e.getCanvas().pushPaintSelection();
			definingSelection = true;
			draggingSelection = false;
			dragTransform = null;
		} else {
			definingSelection = false;
			draggingSelection = true;
			dragTransform = AffineTransform.getTranslateInstance(0, 0);
			if (clickedArea == 5 || clickedArea == 10) {
				dragTransform.concatenate(translateTransform);
			} else if (!altDown) {
				dragTransform.concatenate(scaleTransform);
			} else if (clickedArea == 2 || clickedArea == 4 || clickedArea == 6 || clickedArea == 8) {
				dragTransform.concatenate(shearTransform);
			} else {
				dragTransform.concatenate(rotateTransform);
			}
		}
		return true;
	}
	
	private void doDragTransform(ToolEvent e) {
		Point2D vc = getVisibleCenter();
		AffineTransform tx = AffineTransform.getTranslateInstance(0, 0);
		tx.concatenate(dragTransform);
		if (clickedArea == 5 || clickedArea == 10) {
			tx.translate(e.getCanvasX()-e.getCanvasPreviousClickedX(), e.getCanvasY()-e.getCanvasPreviousClickedY());
		} else if (!altDown) {
			if (clickedArea == 2 || clickedArea == 8) {
				tx.scale(1.0, Math.abs(e.getCanvasY()-vc.getY())/Math.abs(e.getCanvasPreviousClickedY()-vc.getY()));
			} else if (clickedArea == 4 || clickedArea == 6) {
				tx.scale(Math.abs(e.getCanvasX()-vc.getX())/Math.abs(e.getCanvasPreviousClickedX()-vc.getX()), 1.0);
			} else if (e.isShiftDown()) {
				double sx = Math.abs(e.getCanvasX()-vc.getX())/Math.abs(e.getCanvasPreviousClickedX()-vc.getX());
				double sy = Math.abs(e.getCanvasY()-vc.getY())/Math.abs(e.getCanvasPreviousClickedY()-vc.getY());
				double s = (sx+sy)/2.0;
				tx.scale(s, s);
			} else {
				double sx = Math.abs(e.getCanvasX()-vc.getX())/Math.abs(e.getCanvasPreviousClickedX()-vc.getX());
				double sy = Math.abs(e.getCanvasY()-vc.getY())/Math.abs(e.getCanvasPreviousClickedY()-vc.getY());
				tx.scale(sx, sy);
			}
		} else if (clickedArea == 2) {
			tx.shear((e.getCanvasPreviousClickedX()-e.getCanvasX())*1.8/baseRect.getWidth(), 0.0);
		} else if (clickedArea == 8) {
			tx.shear((e.getCanvasX()-e.getCanvasPreviousClickedX())*1.8/baseRect.getWidth(), 0.0);
		} else if (clickedArea == 4) {
			tx.shear(0.0, (e.getCanvasPreviousClickedY()-e.getCanvasY())*1.8/baseRect.getHeight());
		} else if (clickedArea == 6) {
			tx.shear(0.0, (e.getCanvasY()-e.getCanvasPreviousClickedY())*1.8/baseRect.getHeight());
		} else {
			double a = Math.atan2(e.getCanvasPreviousClickedY()-vc.getY(), e.getCanvasPreviousClickedX()-vc.getX());
			double b = Math.atan2(e.getCanvasY()-vc.getY(), e.getCanvasX()-vc.getX());
			tx.rotate(b-a);
		}
		if (clickedArea == 5 || clickedArea == 10) {
			if (e.getHistory() != null) e.getHistory().add(new TranslateTransformAtom(this, tx));
			translateTransform = tx;
		} else if (!altDown) {
			if (e.getHistory() != null) e.getHistory().add(new ScaleTransformAtom(this, tx));
			scaleTransform = tx;
		} else if (clickedArea == 2 || clickedArea == 4 || clickedArea == 6 || clickedArea == 8) {
			if (e.getHistory() != null) e.getHistory().add(new ShearTransformAtom(this, tx));
			shearTransform = tx;
		} else {
			if (e.getHistory() != null) e.getHistory().add(new RotateTransformAtom(this, tx));
			rotateTransform = tx;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (draggingSelection) {
			doDragTransform(e);
			push(e);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (definingSelection) {
			Shape s = new Rectangle2D.Float(
					Math.min(e.getCanvasX(),e.getCanvasPreviousClickedX()),
					Math.min(e.getCanvasY(),e.getCanvasPreviousClickedY()),
					Math.abs(e.getCanvasX()-e.getCanvasPreviousClickedX()),
					Math.abs(e.getCanvasY()-e.getCanvasPreviousClickedY())
			);
			Rectangle2D r = new Rectangle2D.Float(
					Math.min(e.getX(),e.getPreviousClickedX()),
					Math.min(e.getY(),e.getPreviousClickedY()),
					Math.abs(e.getX()-e.getPreviousClickedX()),
					Math.abs(e.getY()-e.getPreviousClickedY())
			);
			if (e.isCtrlDown() && e.isAltDown()) e.getCanvas().xorPaintSelection(s);
			else if (e.isCtrlDown()) e.getCanvas().addPaintSelection(s);
			else if (e.isAltDown()) e.getCanvas().subtractPaintSelection(s);
			else e.getCanvas().setPaintSelection(s);
			e.getCanvas().pushPaintSelection();
			DrawSurface d = e.getDrawSurface();
			Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
			if (r.isEmpty()) {
				if (e.isCtrlDown() && e.isAltDown()) {
					ListIterator<DrawObject> oi = d.listIterator(d.size());
					while (oi.hasPrevious()) {
						DrawObject o = oi.previous();
						if (o.contains(p)) {
							o.setSelected(!o.isSelected());
							break;
						}
					}
				} else if (e.isCtrlDown()) {
					ListIterator<DrawObject> oi = d.listIterator(d.size());
					while (oi.hasPrevious()) {
						DrawObject o = oi.previous();
						if (o.contains(p)) {
							o.setSelected(true);
							break;
						}
					}
				} else if (e.isAltDown()) {
					ListIterator<DrawObject> oi = d.listIterator(d.size());
					while (oi.hasPrevious()) {
						DrawObject o = oi.previous();
						if (o.contains(p)) {
							o.setSelected(false);
							break;
						}
					}
				} else {
					for (DrawObject o : d) {
						o.setSelected(o.contains(p));
					}
				}
			} else {
				if (e.isCtrlDown() && e.isAltDown()) {
					for (DrawObject o : d) {
						if (o.intersects(r)) {
							o.setSelected(!o.isSelected());
						}
					}
				} else if (e.isCtrlDown()) {
					for (DrawObject o : d) {
						if (o.intersects(r)) {
							o.setSelected(true);
						}
					}
				} else if (e.isAltDown()) {
					for (DrawObject o : d) {
						if (o.intersects(r)) {
							o.setSelected(false);
						}
					}
				} else {
					for (DrawObject o : d) {
						o.setSelected(o.intersects(r));
					}
				}
			}
			if (isSelection(e)) pop(e);
			else reset(e);
		} else if (draggingSelection) {
			doDragTransform(e);
			push(e);
		}
		dragTransform = null;
		definingSelection = false;
		draggingSelection = false;
		clickedArea = 0;
		altDown = false;
		updateCursor(e);
		e.commitTransaction();
		return true;
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		drawTransformRect(g);
		if (definingSelection) {
			ToolUtilities.fillSelectionShape(e, g, new Rectangle2D.Float(
					Math.min(e.getCanvasX(),e.getCanvasPreviousClickedX()),
					Math.min(e.getCanvasY(),e.getCanvasPreviousClickedY()),
					Math.abs(e.getCanvasX()-e.getCanvasPreviousClickedX()),
					Math.abs(e.getCanvasY()-e.getCanvasPreviousClickedY())
			));
		}
		return true;
	}
	
	public boolean respondsToCommand(ToolEvent e) {
		switch (e.getCommand()) {
		case SELECT_ALL:
		case DESELECT_ALL:
		case INVERT_SELECTION:
			return true;
		default:
			return false;
		}
	}
	
	public boolean enableCommand(ToolEvent e) {
		switch (e.getCommand()) {
		case SELECT_ALL:
		case DESELECT_ALL:
		case INVERT_SELECTION:
			return true;
		default:
			return false;
		}
	}
	
	public boolean doCommand(ToolEvent e) {
		switch (e.getCommand()) {
		case SELECT_ALL:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.SelectAll"));
			reset(e);
			e.getCanvas().pushPaintSelection();
			e.getCanvas().setPaintSelection(new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight()));
			e.getCanvas().pushPaintSelection();
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(true);
			}
			if (isSelection(e)) pop(e);
			else reset(e);
			e.commitTransaction();
			return true;
		case DESELECT_ALL:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.DeselectAll"));
			reset(e);
			e.getCanvas().clearPaintSelection();
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(false);
			}
			if (isSelection(e)) pop(e);
			else reset(e);
			e.commitTransaction();
			return true;
		case INVERT_SELECTION:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.InvertSelection"));
			reset(e);
			e.getCanvas().pushPaintSelection();
			Area invertSelection = new Area(new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight()));
			invertSelection.exclusiveOr(new Area(e.getCanvas().getPaintSelection()));
			e.getCanvas().setPaintSelection(invertSelection);
			e.getCanvas().pushPaintSelection();
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(!o.isSelected());
			}
			if (isSelection(e)) pop(e);
			else reset(e);
			e.commitTransaction();
			return true;
		default:
			return false;
		}
	}
	
	private void updateCursor(ToolEvent e) {
		switch (getCurrentArea(e)) {
		case 0: setCursor(CursorUtils.CURSOR_SELECT); break;
		case 1: setCursor(e.isAltDown() ? rotate_cursor : Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR)); break;
		case 2: setCursor(e.isAltDown() ? half_arrow_gray : Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)); break;
		case 3: setCursor(e.isAltDown() ? rotate_cursor : Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)); break;
		case 4: setCursor(e.isAltDown() ? half_arrow_gray : Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)); break;
		case 5: setCursor(CursorUtils.CURSOR_MOVE); break;
		case 6: setCursor(e.isAltDown() ? half_arrow_gray : Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)); break;
		case 7: setCursor(e.isAltDown() ? rotate_cursor : Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)); break;
		case 8: setCursor(e.isAltDown() ? half_arrow_gray : Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)); break;
		case 9: setCursor(e.isAltDown() ? rotate_cursor : Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)); break;
		case 10: setCursor(CursorUtils.CURSOR_MOVE); break;
		}
	}
	
	private Cursor cursor = CursorUtils.CURSOR_SELECT;
	
	private void setCursor(Cursor c) {
		if (cursor != c) {
			cursor = c;
		}
	}

	public Cursor getCursor(ToolEvent e) {
		return cursor;
	}
	
	// - - - BEGIN PRIVATE INTERFACE - - - //
	
	private static final Color BOUNDS_COLOR = new Color(0x8000BB00);
	private static final Color HANDLE_COLOR = new Color(0xFF00BB00);
	private static final BasicStroke BH_STROKE = new BasicStroke(1);
	private static final Composite BH_COMPOSITE = AlphaComposite.SrcOver;
	private Shape basePaintSelection = null;
	private Shape baseClip = null;
	private AffineTransform basePoppedImageTransform = null;
	private Map<DrawObject,AffineTransform> baseObjectTransform = new HashMap<DrawObject,AffineTransform>();
	private Rectangle2D baseRect = null;
	private AffineTransform baseRectTransform = null;
	private AffineTransform scaleTransform = null;
	private AffineTransform shearTransform = null;
	private AffineTransform rotateTransform = null;
	private AffineTransform translateTransform = null;
	
	private boolean isSelection(ToolEvent e) {
		if (e.getCanvas().getPaintSelection() != null) return true;
		for (DrawObject o : e.getDrawSurface()) {
			if (o.isSelected() && !o.isLocked()) {
				return true;
			}
		}
		return false;
	}
	
	private void reset(ToolEvent e) {
		basePaintSelection = null;
		baseClip = null;
		basePoppedImageTransform = null;
		baseObjectTransform.clear();
		if (e != null && e.getHistory() != null) e.getHistory().add(new BaseRectAtom(this, null, null));
		baseRect = null;
		baseRectTransform = null;
		if (e != null && e.getHistory() != null) e.getHistory().add(new NullTransformAtom(this));
		scaleTransform = null;
		shearTransform = null;
		rotateTransform = null;
		translateTransform = null;
	}
	
	private void update(ToolEvent e) {
		if (e.getCanvas().getPaintSelection() != null && !e.getCanvas().isPaintSelectionPopped()) {
			pop(e);
		}
	}
	
	private void pop(ToolEvent e) {
		if (e.getCanvas().getPaintSelection() != null && !e.getCanvas().isPaintSelectionPopped()) {
			e.getCanvas().popPaintSelection(false, false);
		}
		basePaintSelection = e.getCanvas().getPaintSelection();
		baseClip = e.getLayer().getClip();
		basePoppedImageTransform = e.getLayer().getPoppedImageTransform();
		baseObjectTransform.clear();
		for (DrawObject o : e.getDrawSurface()) {
			if (o.isSelected() && !o.isLocked()) {
				baseObjectTransform.put(o, o.getTransform());
			}
		}
		if (basePaintSelection != null) {
			if (basePoppedImageTransform != null) {
				Rectangle2D br = new Rectangle(0, 0, e.getLayer().getPoppedImage().getWidth(), e.getLayer().getPoppedImage().getHeight());
				AffineTransform brt = AffineTransform.getTranslateInstance(0, 0);
				brt.translate(e.getLayer().getX(), e.getLayer().getY());
				brt.concatenate(basePoppedImageTransform);
				if (e.getHistory() != null) e.getHistory().add(new BaseRectAtom(this, br, brt));
				baseRect = br;
				baseRectTransform = brt;
			} else {
				Rectangle2D br = basePaintSelection.getBounds2D();
				AffineTransform brt = AffineTransform.getTranslateInstance(0, 0);
				if (e.getHistory() != null) e.getHistory().add(new BaseRectAtom(this, br, brt));
				baseRect = br;
				baseRectTransform = brt;
			}
		} else {
			if (baseObjectTransform.size() == 1) {
				DrawObject o = baseObjectTransform.keySet().iterator().next();
				AffineTransform otx = o.getTransform();
				o.setTransform(AffineTransform.getTranslateInstance(0, 0));
				Rectangle2D br = o.getBounds2D();
				o.setTransform(otx);
				AffineTransform brt = AffineTransform.getTranslateInstance(0, 0);
				brt.translate(e.getLayer().getX(), e.getLayer().getY());
				if (otx != null) brt.concatenate(otx);
				if (e.getHistory() != null) e.getHistory().add(new BaseRectAtom(this, br, brt));
				baseRect = br;
				baseRectTransform = brt;
			} else {
				float minX = Float.POSITIVE_INFINITY;
				float minY = Float.POSITIVE_INFINITY;
				float maxX = Float.NEGATIVE_INFINITY;
				float maxY = Float.NEGATIVE_INFINITY;
				for (DrawObject o : baseObjectTransform.keySet()) {
					Rectangle2D ob = o.getBounds2D();
					if (ob.getMinX() < minX) minX = (float)ob.getMinX();
					if (ob.getMinY() < minY) minY = (float)ob.getMinY();
					if (ob.getMaxX() > maxX) maxX = (float)ob.getMaxX();
					if (ob.getMaxY() > maxY) maxY = (float)ob.getMaxY();
				}
				if (Float.isInfinite(minX) || Float.isInfinite(maxX) || Float.isInfinite(minY) || Float.isInfinite(maxY)) {
					if (e.getHistory() != null) e.getHistory().add(new BaseRectAtom(this, null, null));
					baseRect = null;
					baseRectTransform = null;
				} else {
					Rectangle2D br = new Rectangle2D.Float(minX, minY, maxX-minX, maxY-minY);
					AffineTransform brt = AffineTransform.getTranslateInstance(0, 0);
					brt.translate(e.getLayer().getX(), e.getLayer().getY());
					if (e.getHistory() != null) e.getHistory().add(new BaseRectAtom(this, br, brt));
					baseRect = br;
					baseRectTransform = brt;
				}
			}
		}
		if (e.getHistory() != null) e.getHistory().add(new BaseTransformAtom(this));
		scaleTransform = AffineTransform.getTranslateInstance(0, 0);
		shearTransform = AffineTransform.getTranslateInstance(0, 0);
		rotateTransform = AffineTransform.getTranslateInstance(0, 0);
		translateTransform = AffineTransform.getTranslateInstance(0, 0);
	}
	
	private AffineTransform accumulateTransform(Layer l, AffineTransform base) {
		AffineTransform btx = AffineTransform.getTranslateInstance(0, 0);
		/* - - - - PART I - UN-LAYER - - - - */
		if (l != null) btx.translate(-l.getX(), -l.getY());
		/* - - - - PART II - UN-BASERECT - - - - */
		btx.concatenate(baseRectTransform);
		btx.translate(baseRect.getCenterX(), baseRect.getCenterY());
		/* - - - - PART III - DRAG TRANSFORM - - - - */
		if (translateTransform != null) btx.concatenate(translateTransform);
		if (rotateTransform != null) btx.concatenate(rotateTransform);
		if (scaleTransform != null) btx.concatenate(scaleTransform);
		if (shearTransform != null) btx.concatenate(shearTransform);
		/* - - - - PART VI - BASERECT - - - - */
		btx.translate(-baseRect.getCenterX(), -baseRect.getCenterY());
		try {
			btx.concatenate(baseRectTransform.createInverse());
		} catch (NoninvertibleTransformException e) {
			Rectangle2D brx = baseRectTransform.createTransformedShape(baseRect).getBounds2D();
			btx.translate(-brx.getCenterX(), -brx.getCenterY());
		}
		/* - - - - PART V - LAYER - - - - */
		if (l != null) btx.translate(l.getX(), l.getY());
		/* - - - - PART VI - BASE - - - - */
		if (base != null) btx.concatenate(base);
		return btx;
	}
	
	private void push(ToolEvent e) {
		if (basePaintSelection != null) {
			e.getCanvas().setPaintSelection(accumulateTransform(null, null).createTransformedShape(basePaintSelection));
		}
		if (baseClip != null) {
			e.getLayer().setClip(accumulateTransform(e.getLayer(), null).createTransformedShape(baseClip));
		}
		if (basePoppedImageTransform != null) {
			e.getLayer().setPoppedImage(e.getLayer().getPoppedImage(), accumulateTransform(e.getLayer(), basePoppedImageTransform));
		}
		for (Map.Entry<DrawObject,AffineTransform> entry : baseObjectTransform.entrySet()) {
			entry.getKey().setTransform(accumulateTransform(e.getLayer(), entry.getValue()));
		}
	}
	
	private int getCurrentArea(ToolEvent e) {
		if (baseRect != null && baseRectTransform != null) {
			AffineTransform btx = accumulateTransform(null, baseRectTransform);
			Shape s;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMinX()-5.0, baseRect.getMinY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 1;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getCenterX()-5.0, baseRect.getMinY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 2;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMaxX()-5.0, baseRect.getMinY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 3;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMinX()-5.0, baseRect.getCenterY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 4;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getCenterX()-5.0, baseRect.getCenterY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 5;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMaxX()-5.0, baseRect.getCenterY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 6;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMinX()-5.0, baseRect.getMaxY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 7;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getCenterX()-5.0, baseRect.getMaxY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 8;
			s = (btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMaxX()-5.0, baseRect.getMaxY()-5.0, 10.0, 10.0)));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 9;
			s = (btx.createTransformedShape(baseRect));
			if (s.contains(e.getCanvasX(), e.getCanvasY())) return 10;
			return 0;
		} else {
			return 0;
		}
	}
	
	private void drawTransformRect(Graphics2D g) {
		if (baseRect != null && baseRectTransform != null) {
			AffineTransform btx = accumulateTransform(null, baseRectTransform);
			g.setStroke(BH_STROKE);
			g.setComposite(BH_COMPOSITE);
			g.setPaint(BOUNDS_COLOR);
			g.draw(btx.createTransformedShape(baseRect));
			g.setPaint(HANDLE_COLOR);
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMinX()-3.0, baseRect.getMinY()-3.0, 6.0, 6.0)));
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getCenterX()-3.0, baseRect.getMinY()-3.0, 6.0, 6.0)));
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMaxX()-3.0, baseRect.getMinY()-3.0, 6.0, 6.0)));
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMinX()-3.0, baseRect.getCenterY()-3.0, 6.0, 6.0)));
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getCenterX()-3.0, baseRect.getCenterY()-3.0, 6.0, 6.0)));
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMaxX()-3.0, baseRect.getCenterY()-3.0, 6.0, 6.0)));
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMinX()-3.0, baseRect.getMaxY()-3.0, 6.0, 6.0)));
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getCenterX()-3.0, baseRect.getMaxY()-3.0, 6.0, 6.0)));
			g.draw(btx.createTransformedShape(new Rectangle2D.Double(baseRect.getMaxX()-3.0, baseRect.getMaxY()-3.0, 6.0, 6.0)));
		}
	}
	
	private Point2D getVisibleCenter() {
		Point2D p = new Point2D.Double(baseRect.getCenterX(), baseRect.getCenterY());
		baseRectTransform.transform(p, p);
		translateTransform.transform(p, p);
		return p;
	}
	
	private static class BaseRectAtom implements Atom {
		private TransformTool tt;
		private Rectangle2D oldBR;
		private AffineTransform oldBRT;
		private Rectangle2D newBR;
		private AffineTransform newBRT;
		public BaseRectAtom(TransformTool tt, Rectangle2D newBR, AffineTransform newBRT) {
			this.tt = tt;
			this.oldBR = tt.baseRect;
			this.oldBRT = tt.baseRectTransform;
			this.newBR = newBR;
			this.newBRT = newBRT;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldBR = ((BaseRectAtom)previousAtom).oldBR;
			this.oldBRT = ((BaseRectAtom)previousAtom).oldBRT;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof BaseRectAtom) && (((BaseRectAtom)previousAtom).tt == this.tt);
		}
		public void redo() {
			tt.baseRect = newBR;
			tt.baseRectTransform = newBRT;
		}
		public void undo() {
			tt.baseRect = oldBR;
			tt.baseRectTransform = newBRT;
		}
	}
	
	private static class NullTransformAtom implements Atom {
		private TransformTool tt;
		private AffineTransform oldScale;
		private AffineTransform oldShear;
		private AffineTransform oldRotate;
		private AffineTransform oldTrans;
		public NullTransformAtom(TransformTool tt) {
			this.tt = tt;
			this.oldScale = tt.scaleTransform;
			this.oldShear = tt.shearTransform;
			this.oldRotate = tt.rotateTransform;
			this.oldTrans = tt.translateTransform;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldScale = ((NullTransformAtom)previousAtom).oldScale;
			this.oldShear = ((NullTransformAtom)previousAtom).oldShear;
			this.oldRotate = ((NullTransformAtom)previousAtom).oldRotate;
			this.oldTrans = ((NullTransformAtom)previousAtom).oldTrans;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof NullTransformAtom) && (((NullTransformAtom)previousAtom).tt == this.tt);
		}
		public void redo() {
			tt.scaleTransform = null;
			tt.shearTransform = null;
			tt.rotateTransform = null;
			tt.translateTransform = null;
		}
		public void undo() {
			tt.scaleTransform = oldScale;
			tt.shearTransform = oldShear;
			tt.rotateTransform = oldRotate;
			tt.translateTransform = oldTrans;
		}
	}
	
	private static class BaseTransformAtom implements Atom {
		private TransformTool tt;
		private AffineTransform oldScale;
		private AffineTransform oldShear;
		private AffineTransform oldRotate;
		private AffineTransform oldTrans;
		public BaseTransformAtom(TransformTool tt) {
			this.tt = tt;
			this.oldScale = tt.scaleTransform;
			this.oldShear = tt.shearTransform;
			this.oldRotate = tt.rotateTransform;
			this.oldTrans = tt.translateTransform;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldScale = ((BaseTransformAtom)previousAtom).oldScale;
			this.oldShear = ((BaseTransformAtom)previousAtom).oldShear;
			this.oldRotate = ((BaseTransformAtom)previousAtom).oldRotate;
			this.oldTrans = ((BaseTransformAtom)previousAtom).oldTrans;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof BaseTransformAtom) && (((BaseTransformAtom)previousAtom).tt == this.tt);
		}
		public void redo() {
			tt.scaleTransform = AffineTransform.getTranslateInstance(0, 0);
			tt.shearTransform = AffineTransform.getTranslateInstance(0, 0);
			tt.rotateTransform = AffineTransform.getTranslateInstance(0, 0);
			tt.translateTransform = AffineTransform.getTranslateInstance(0, 0);
		}
		public void undo() {
			tt.scaleTransform = oldScale;
			tt.shearTransform = oldShear;
			tt.rotateTransform = oldRotate;
			tt.translateTransform = oldTrans;
		}
	}
	
	private static class ScaleTransformAtom implements Atom {
		private TransformTool tt;
		private AffineTransform oldtx;
		private AffineTransform newtx;
		public ScaleTransformAtom(TransformTool tt, AffineTransform newtx) {
			this.tt = tt;
			this.oldtx = tt.scaleTransform;
			this.newtx = newtx;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldtx = ((ScaleTransformAtom)previousAtom).oldtx;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof ScaleTransformAtom) && (((ScaleTransformAtom)previousAtom).tt == this.tt);
		}
		public void redo() {
			tt.scaleTransform = newtx;
		}
		public void undo() {
			tt.scaleTransform = oldtx;
		}
	}
	
	private static class ShearTransformAtom implements Atom {
		private TransformTool tt;
		private AffineTransform oldtx;
		private AffineTransform newtx;
		public ShearTransformAtom(TransformTool tt, AffineTransform newtx) {
			this.tt = tt;
			this.oldtx = tt.shearTransform;
			this.newtx = newtx;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldtx = ((ShearTransformAtom)previousAtom).oldtx;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof ShearTransformAtom) && (((ShearTransformAtom)previousAtom).tt == this.tt);
		}
		public void redo() {
			tt.shearTransform = newtx;
		}
		public void undo() {
			tt.shearTransform = oldtx;
		}
	}
	
	private static class RotateTransformAtom implements Atom {
		private TransformTool tt;
		private AffineTransform oldtx;
		private AffineTransform newtx;
		public RotateTransformAtom(TransformTool tt, AffineTransform newtx) {
			this.tt = tt;
			this.oldtx = tt.rotateTransform;
			this.newtx = newtx;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldtx = ((RotateTransformAtom)previousAtom).oldtx;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof RotateTransformAtom) && (((RotateTransformAtom)previousAtom).tt == this.tt);
		}
		public void redo() {
			tt.rotateTransform = newtx;
		}
		public void undo() {
			tt.rotateTransform = oldtx;
		}
	}
	
	private static class TranslateTransformAtom implements Atom {
		private TransformTool tt;
		private AffineTransform oldtx;
		private AffineTransform newtx;
		public TranslateTransformAtom(TransformTool tt, AffineTransform newtx) {
			this.tt = tt;
			this.oldtx = tt.translateTransform;
			this.newtx = newtx;
		}
		public Atom buildUpon(Atom previousAtom) {
			this.oldtx = ((TranslateTransformAtom)previousAtom).oldtx;
			return this;
		}
		public boolean canBuildUpon(Atom previousAtom) {
			return (previousAtom instanceof TranslateTransformAtom) && (((TranslateTransformAtom)previousAtom).tt == this.tt);
		}
		public void redo() {
			tt.translateTransform = newtx;
		}
		public void undo() {
			tt.translateTransform = oldtx;
		}
	}
}
