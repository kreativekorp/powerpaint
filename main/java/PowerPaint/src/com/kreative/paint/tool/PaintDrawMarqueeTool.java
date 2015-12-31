package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import com.kreative.paint.Canvas;
import com.kreative.paint.Layer;
import com.kreative.paint.datatransfer.ClipboardUtilities;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.DrawSurface;
import com.kreative.paint.document.draw.ImageDrawObject;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.TextDrawObject;
import com.kreative.paint.util.CursorUtils;
import com.kreative.paint.util.ImageUtils;

public class PaintDrawMarqueeTool extends AbstractPaintDrawSelectionTool
implements ToolOptions.DrawSquare, ToolOptions.DrawFromCenter {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,K,K,K,0,0,0,K,K,K,0,0,0,K,K,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,0,0,K,K,0,0,K,K,K,0,0,K,K,0,0,
					0,0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,K,0,K,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,0,K,0,0,0,0,0,0,0,0,0,0,0,K,
					0,0,0,K,0,0,0,0,0,0,0,0,0,K,0,0,
					0,0,0,K,K,0,0,K,K,K,0,0,K,K,0,0,
					0,K,0,0,0,0,0,0,0,0,0,0,0,0,0,K,
					0,K,K,K,0,0,0,K,K,K,0,0,0,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private boolean definingSelection = false;
	private boolean draggingSelection = false;
	
	private Rectangle2D makeShape(ToolEvent e, float sx, float sy, float x, float y) {
		return new Rectangle2D.Float(Math.min(x,sx), Math.min(y,sy), Math.abs(x-sx), Math.abs(y-sy));
	}
	
	private Rectangle2D makeShape1(ToolEvent e) {
		float sx = e.getCanvasPreviousClickedX();
		float sy = e.getCanvasPreviousClickedY();
		float x = e.getCanvasX();
		float y = e.getCanvasY();
		if (e.isShiftDown() != e.tc().drawSquare()) {
			float w = Math.abs(x-sx);
			float h = Math.abs(y-sy);
			float s = Math.max(w, h);
			if (y > sy) y = sy+s;
			else y = sy-s;
			if (x > sx) x = sx+s;
			else x = sx-s;
		}
		if (e.tc().drawFromCenter()) {
			sx -= (x-sx);
			sy -= (y-sy);
		}
		return makeShape(e, sx, sy, x, y);
	}
	
	private Rectangle2D makeShape2(ToolEvent e) {
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
		if (e.tc().drawFromCenter()) {
			sx -= (x-sx);
			sy -= (y-sy);
		}
		return makeShape(e, sx, sy, x, y);
	}

	public boolean toolDoubleClicked(ToolEvent e) {
		e.beginTransaction(ToolUtilities.messages.getString("marquee.SelectAll"));
		e.getCanvas().pushPaintSelection();
		e.getCanvas().setPaintSelection(new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight()));
		e.getCanvas().pushPaintSelection();
		for (DrawObject o : e.getDrawSurface()) {
			o.setSelected(true);
		}
		e.commitTransaction();
		return true;
	}
	
	public boolean mousePressed(ToolEvent e) {
		if (isInSelection(e)) {
			if (e.isAltDown()) {
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Duplicate"));
				e.getCanvas().popPaintSelection(false, true);
				DrawSurface d = e.getDrawSurface();
				List<DrawObject> sel = new Vector<DrawObject>();
				for (DrawObject o : d) {
					if (o.isSelected()) {
						o.setSelected(false);
						o = o.clone();
						o.setSelected(true);
						o.setVisible(true);
						o.setLocked(false);
						sel.add(o);
					}
				}
				d.addAll(sel);
			} else {
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Move"));
				if (!e.getCanvas().isPaintSelectionPopped()) {
					e.getCanvas().popPaintSelection(false, false);
				}
			}
			definingSelection = false;
			draggingSelection = true;
			return true;
		} else {
			e.beginTransaction(ToolUtilities.messages.getString("marquee.Select"));
			e.getCanvas().pushPaintSelection();
			definingSelection = true;
			draggingSelection = false;
			return true;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (draggingSelection) {
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(e.getCanvasX()-e.getCanvasPreviousX(), e.getCanvasY()-e.getCanvasPreviousY()));
			DrawSurface d = e.getDrawSurface();
			for (DrawObject o : d) {
				if (o.isSelected() && !o.isLocked()) {
					Point2D p = o.getLocation();
					p = new Point2D.Double(
							p.getX()+e.getCanvasX()-e.getCanvasPreviousX(),
							p.getY()+e.getCanvasY()-e.getCanvasPreviousY()
					);
					o.setLocation(p);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (draggingSelection) {
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(e.getCanvasX()-e.getCanvasPreviousX(), e.getCanvasY()-e.getCanvasPreviousY()));
			DrawSurface d = e.getDrawSurface();
			for (DrawObject o : d) {
				if (o.isSelected() && !o.isLocked()) {
					Point2D p = o.getLocation();
					p = new Point2D.Double(
							p.getX()+e.getCanvasX()-e.getCanvasPreviousX(),
							p.getY()+e.getCanvasY()-e.getCanvasPreviousY()
					);
					o.setLocation(p);
				}
			}
			e.commitTransaction();
			definingSelection = false;
			draggingSelection = false;
			return true;
		} else if (definingSelection) {
			Shape s = makeShape1(e);
			if (e.isCtrlDown() && e.isAltDown()) e.getCanvas().xorPaintSelection(s);
			else if (e.isCtrlDown()) e.getCanvas().addPaintSelection(s);
			else if (e.isAltDown()) e.getCanvas().subtractPaintSelection(s);
			else e.getCanvas().setPaintSelection(s);
			e.getCanvas().pushPaintSelection();
			DrawSurface d = e.getDrawSurface();
			Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
			Rectangle2D r = makeShape2(e);
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
			e.commitTransaction();
			definingSelection = false;
			draggingSelection = false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (definingSelection) {
			ToolUtilities.fillSelectionShape(e, g, makeShape1(e));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_CLEAR:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.DeselectAll"));
			e.getCanvas().clearPaintSelection();
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(false);
			}
			e.commitTransaction();
			return true;
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_DELETE:
			{
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Clear"));
				e.getCanvas().deletePaintSelection(false);
				if (!e.isCtrlDown() && !e.isAltDown()) e.getCanvas().clearPaintSelection();
				DrawSurface d = e.getDrawSurface();
				Map<DrawObject,DrawObject> sel = new IdentityHashMap<DrawObject,DrawObject>();
				for (DrawObject o : d) {
					if (o.isSelected() && !o.isLocked()) sel.put(o,o);
				}
				for (DrawObject o : sel.values()) {
					d.remove(o);
				}
				e.commitTransaction();
			}
			return true;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			if (e.isAltDown()) {
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Duplicate"));
				e.getCanvas().popPaintSelection(false, true);
				DrawSurface d = e.getDrawSurface();
				List<DrawObject> sel = new Vector<DrawObject>();
				for (DrawObject o : d) {
					if (o.isSelected()) {
						o.setSelected(false);
						o = o.clone();
						o.setSelected(true);
						o.setVisible(true);
						o.setLocked(false);
						Point2D p = o.getLocation();
						p = new Point2D.Double(p.getX()+8, p.getY()+8);
						o.setLocation(p);
						sel.add(o);
					}
				}
				d.addAll(sel);
			} else {
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Move"));
				if (!e.getCanvas().isPaintSelectionPopped()) {
					e.getCanvas().popPaintSelection(false, false);
				}
			}
			nudge(e);
			e.commitTransaction();
			return true;
		default:
			return false;
		}
	}
	
	public boolean respondsToCommand(ToolEvent e) {
		switch (e.getCommand()) {
		case CUT:
		case COPY:
		case PASTE:
		case CLEAR:
		case DUPLICATE:
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
		case CUT:
		case COPY:
		case CLEAR:
		case DUPLICATE:
			return isSelection(e);
		case PASTE:
			return ClipboardUtilities.clipboardHasDrawObjects() || ClipboardUtilities.clipboardHasImage() || ClipboardUtilities.clipboardHasString();
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
		case CUT:
			{
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Cut"));
				copy(e);
				e.getCanvas().deletePaintSelection(false);
				e.getCanvas().clearPaintSelection();
				DrawSurface d = e.getDrawSurface();
				Map<DrawObject,DrawObject> sel = new IdentityHashMap<DrawObject,DrawObject>();
				for (DrawObject o : d) {
					if (o.isSelected() && !o.isLocked()) sel.put(o,o);
				}
				for (DrawObject o : sel.values()) {
					d.remove(o);
				}
				e.commitTransaction();
			}
			return true;
		case COPY:
			copy(e);
			return true;
		case PASTE:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.Paste"));
			paste(e);
			e.commitTransaction();
			return true;
		case CLEAR:
			{
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Clear"));
				e.getCanvas().deletePaintSelection(false);
				e.getCanvas().clearPaintSelection();
				DrawSurface d = e.getDrawSurface();
				Map<DrawObject,DrawObject> sel = new IdentityHashMap<DrawObject,DrawObject>();
				for (DrawObject o : d) {
					if (o.isSelected() && !o.isLocked()) sel.put(o,o);
				}
				for (DrawObject o : sel.values()) {
					d.remove(o);
				}
				e.commitTransaction();
			}
			return true;
		case DUPLICATE:
			{
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Duplicate"));
				e.getCanvas().popPaintSelection(false, true);
				DrawSurface d = e.getDrawSurface();
				List<DrawObject> sel = new Vector<DrawObject>();
				for (DrawObject o : d) {
					if (o.isSelected()) {
						o.setSelected(false);
						o = o.clone();
						o.setSelected(true);
						o.setVisible(true);
						o.setLocked(false);
						sel.add(o);
					}
				}
				d.addAll(sel);
				e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(8, 8));
				for (DrawObject o : sel) {
					Point2D p = o.getLocation();
					p = new Point2D.Double(p.getX()+8, p.getY()+8);
					o.setLocation(p);
				}
				e.commitTransaction();
			}
			return true;
		case SELECT_ALL:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.SelectAll"));
			e.getCanvas().pushPaintSelection();
			e.getCanvas().setPaintSelection(new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight()));
			e.getCanvas().pushPaintSelection();
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(true);
			}
			e.commitTransaction();
			return true;
		case DESELECT_ALL:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.DeselectAll"));
			e.getCanvas().clearPaintSelection();
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(false);
			}
			e.commitTransaction();
			return true;
		case INVERT_SELECTION:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.InvertSelection"));
			e.getCanvas().pushPaintSelection();
			Area invertSelection = new Area(new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight()));
			invertSelection.exclusiveOr(new Area(e.getCanvas().getPaintSelection()));
			e.getCanvas().setPaintSelection(invertSelection);
			e.getCanvas().pushPaintSelection();
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(!o.isSelected());
			}
			e.commitTransaction();
			return true;
		default:
			return false;
		}
	}
	
	public boolean shiftConstrainsCoordinates() {
		return draggingSelection;
	}
	
	private boolean isSelection(ToolEvent e) {
		if (e.getCanvas().getPaintSelection() != null) return true;
		DrawSurface d = e.getDrawSurface();
		if (d == null) return false;
		for (DrawObject o : d) {
			if (o.isSelected() && !o.isLocked()) return true;
		}
		return false;
	}
	
	private boolean isInSelection(ToolEvent e) {
		Shape ps = e.getCanvas().getPaintSelection();
		if (ps != null && ps.contains(e.getCanvasX(), e.getCanvasY())) {
			return true;
		} else {
			DrawSurface d = e.getDrawSurface();
			ListIterator<DrawObject> oi;
			oi = d.listIterator(d.size());
			while (oi.hasPrevious()) {
				DrawObject o = oi.previous();
				if (o.isSelected() && o.contains(e.getCanvasX(), e.getCanvasY())) {
					return true;
				}
			}
			return false;
		}
	}
	
	private void nudge(ToolEvent e) {
		float n = e.isShiftDown() ? 8 : 1;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(-n, 0));
			nudge(e.getDrawSurface(), -n, 0);
			break;
		case KeyEvent.VK_RIGHT:
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(n, 0));
			nudge(e.getDrawSurface(), n, 0);
			break;
		case KeyEvent.VK_UP:
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(0, -n));
			nudge(e.getDrawSurface(), 0, -n);
			break;
		case KeyEvent.VK_DOWN:
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(0, n));
			nudge(e.getDrawSurface(), 0, n);
			break;
		}
	}
	
	private void nudge(DrawSurface d, float dx, float dy) {
		for (DrawObject o : d) {
			if (o.isSelected() && !o.isLocked()) {
				Point2D p = o.getLocation();
				o.setLocation(new Point2D.Double(p.getX()+dx, p.getY()+dy));
			}
		}
	}
	
	private void copy(ToolEvent e) {
		Canvas c = e.getCanvas();
		Layer l = e.getLayer();
		List<DrawObject> sel = new Vector<DrawObject>();
		if (c.getPaintSelection() != null) {
			AffineTransform lx = AffineTransform.getTranslateInstance(-l.getX(), -l.getY());
			sel.add(l.copyImageObject(lx.createTransformedShape(c.getPaintSelection())));
		}
		for (DrawObject o : e.getDrawSurface()) {
			if (o.isSelected()) sel.add(o);
		}
		ClipboardUtilities.setClipboardDrawObjects(sel);
	}
	
	private void paste(ToolEvent e) {
		e.getCanvas().clearPaintSelection();
		for (DrawObject o : e.getDrawSurface()) {
			o.setSelected(false);
		}
		if (ClipboardUtilities.clipboardHasDrawObjects()) {
			Collection<? extends DrawObject> c = ClipboardUtilities.getClipboardDrawObjects();
			boolean first = true;
			for (DrawObject o : c) {
				if (first && o instanceof ImageDrawObject) {
					ImageDrawObject ido = (ImageDrawObject)o;
					e.getCanvas().pastePaintSelection(ido.getImage(), ido.getTransform());
				} else {
					DrawObject oo = o.clone();
					oo.setSelected(true);
					e.getDrawSurface().add(oo);
				}
				first = false;
			}
		}
		else if (ClipboardUtilities.clipboardHasImage()) {
			Image i = ClipboardUtilities.getClipboardImage();
			if (ImageUtils.prepImage(i)) {
				int w = i.getWidth(null);
				int h = i.getHeight(null);
				if (e.isInPaintMode()) {
					AffineTransform tx = AffineTransform.getTranslateInstance(
						(int)(e.getPreviousClickedX() - w / 2),
						(int)(e.getPreviousClickedY() - h / 2));
					e.getCanvas().pastePaintSelection(i, tx);
				} else {
					PaintSettings ps = new PaintSettings(null, null);
					ImageDrawObject ido = ImageDrawObject.forGraphicsDrawImage(
						ps, i,
						(int)(e.getPreviousClickedX() - w / 2),
						(int)(e.getPreviousClickedY() - h / 2));
					ido.setSelected(true);
					e.getDrawSurface().add(ido);
				}
			} else {
				System.err.println("Error: Failed to paste image.");
			}
		}
		else if (ClipboardUtilities.clipboardHasString()) {
			TextDrawObject tdo = new TextDrawObject(
				e.getPaintSettings(),
				e.getPreviousClickedX(),
				e.getPreviousClickedY(),
				Integer.MAX_VALUE,
				ClipboardUtilities.getClipboardString()
			);
			tdo.setSelected(true);
			e.getDrawSurface().add(tdo);
		}
	}

	public Cursor getCursor(ToolEvent e) {
		if (isInSelection(e)) {
			if (e.isAltDown()) {
				return CursorUtils.CURSOR_ARROW_HALF_DOUBLE_HALLOW;
			} else {
				return CursorUtils.CURSOR_MOVE;
			}
		} else {
			return CursorUtils.CURSOR_SELECT;
		}
	}
}
