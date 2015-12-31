package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import com.kreative.paint.datatransfer.ClipboardUtilities;
import com.kreative.paint.document.draw.ControlPoint;
import com.kreative.paint.document.draw.ControlPointType;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.DrawSurface;
import com.kreative.paint.document.draw.GroupDrawObject;
import com.kreative.paint.document.draw.ImageDrawObject;
import com.kreative.paint.document.draw.PaintSettings;
import com.kreative.paint.document.draw.PathDrawObject;
import com.kreative.paint.document.draw.ShapeDrawObject;
import com.kreative.paint.document.draw.TextDrawObject;
import com.kreative.paint.util.CursorUtils;
import com.kreative.paint.util.ImageUtils;

public class ArrowTool extends AbstractDrawSelectionTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,0,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,0,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,0,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,0,0,0,0,
					0,0,0,0,K,K,K,K,K,0,0,0,0,0,0,0,
					0,0,0,0,K,K,0,K,K,0,0,0,0,0,0,0,
					0,0,0,0,K,0,0,0,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,K,K,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	private DrawObject currentObject;
	private List<ControlPoint> originalControlPoints;
	private int originalCurrentControlPoint; // don't
	private int currentCurrentControlPoint; // ask
	private Map<DrawObject,Point2D> anchorPoints;
	private boolean startedDragRect;
	private TextDrawObject text;
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean toolSelected(ToolEvent e) {
		if (text != null) {
			text.setCursor(-1, -1);
			if (e.isTransactionInProgress()) e.commitTransaction();
		}
		currentObject = null;
		originalControlPoints = null;
		originalCurrentControlPoint = 0;
		currentCurrentControlPoint = 0;
		anchorPoints = null;
		startedDragRect = false;
		text = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		if (text != null) {
			text.setCursor(-1, -1);
			if (e.isTransactionInProgress()) e.commitTransaction();
		}
		currentObject = null;
		originalControlPoints = null;
		originalCurrentControlPoint = 0;
		currentCurrentControlPoint = 0;
		anchorPoints = null;
		startedDragRect = false;
		text = null;
		return false;
	}
	
	public boolean toolDoubleClicked(ToolEvent e) {
		if (text != null) {
			text.setCursor(-1, -1);
			if (e.isTransactionInProgress()) e.commitTransaction();
		}
		if (e.isTransactionInProgress()) {
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(true);
			}
		} else {
			e.beginTransaction(ToolUtilities.messages.getString("arrow.SelectAll"));
			e.getCanvas().clearPaintSelection();
			for (DrawObject o : e.getDrawSurface()) {
				o.setSelected(true);
			}
			e.commitTransaction();
		}
		currentObject = null;
		originalControlPoints = null;
		originalCurrentControlPoint = 0;
		currentCurrentControlPoint = 0;
		anchorPoints = null;
		startedDragRect = false;
		text = null;
		return false;
	}
	
	public boolean mouseMoved(ToolEvent e) {
		Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
		if (text != null && text.contains(p)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			return false;
		}
		DrawSurface d = e.getDrawSurface();
		ListIterator<DrawObject> oi;
		oi = d.listIterator(d.size());
		while (oi.hasPrevious()) {
			DrawObject o = oi.previous();
			if (o.isSelected()) {
				for (ControlPoint c : o.getControlPoints()) {
					if (c.getType() != ControlPointType.HIDDEN && c.distance(p) <= 3.0) {
						setCursor(getControlPointCursor(c));
						return false;
					}
				}
			}
		}
		oi = d.listIterator(d.size());
		while (oi.hasPrevious()) {
			DrawObject o = oi.previous();
			if (o.contains(p)) {
				if (e.isAltDown()) {
					setCursor(CursorUtils.CURSOR_ARROW_HALF_DOUBLE_HALLOW);
					return false;
				} else {
					setCursor(CursorUtils.CURSOR_ARROW_HALLOW);
					return false;
				}
			}
		}
		setCursor(CursorUtils.CURSOR_ARROW);
		return false;
	}
	
	public boolean mousePressed(ToolEvent e) {
		if (text != null) {
			if (text.contains(e.getX(), e.getY())) {
				int i = text.getCursorIndexOfLocation(e.getDrawGraphics(), e.getX(), e.getY());
				text.setCursor(i, i);
				return true;
			} else {
				text.setCursor(-1, -1);
				if (e.isTransactionInProgress()) e.commitTransaction();
			}
		}
		e.beginTransaction(getName());
		e.getCanvas().clearPaintSelection();
		Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
		DrawSurface d = e.getDrawSurface();
		ListIterator<DrawObject> oi;
		// clicked on a control point of a selected shape?
		oi = d.listIterator(d.size());
		while (oi.hasPrevious()) {
			DrawObject o = oi.previous();
			if (o.isSelected()) {
				List<ControlPoint> cp = o.getControlPoints();
				for (int i = 0; i < cp.size(); i++) {
					ControlPoint c = cp.get(i);
					if (c.getType() != ControlPointType.HIDDEN && c.distance(p) <= 3.0) {
						// yes, clicked on a control point of a selected shape
						// start a control point drag
						e.renameTransaction(ToolUtilities.messages.getString("arrow.ChangeControlPoint"));
						currentObject = o;
						originalControlPoints = cp;
						originalCurrentControlPoint = i;
						currentCurrentControlPoint = i;
						anchorPoints = null;
						startedDragRect = false;
						text = null;
						return true;
					}
				}
			}
		}
		if (e.isShiftDown()) {
			// clicked with shift key down
			// start a drag rectangle for inverse selection
			e.renameTransaction(ToolUtilities.messages.getString("arrow.Select"));
			currentObject = null;
			originalControlPoints = null;
			originalCurrentControlPoint = 0;
			currentCurrentControlPoint = 0;
			anchorPoints = null;
			startedDragRect = true;
			text = null;
			return true;
		} else {
			// clicked on a selected shape?
			oi = d.listIterator(d.size());
			while (oi.hasPrevious()) {
				DrawObject o = oi.previous();
				if (o.isSelected() && o.contains(p)) {
					// yes, clicked on a selected shape
					// double clicked on a text object?
					if (o instanceof TextDrawObject && e.getClickCount() > 1 && !o.isLocked()) {
						// yes, double clicked on a text object
						// start a text edit
						e.renameTransaction(ToolUtilities.messages.getString("arrow.TextEdit"));
						currentObject = null;
						originalControlPoints = null;
						originalCurrentControlPoint = 0;
						currentCurrentControlPoint = 0;
						anchorPoints = null;
						startedDragRect = false;
						text = (TextDrawObject)o;
						int i = text.getCursorIndexOfLocation(e.getDrawGraphics(), e.getX(), e.getY());
						text.setCursor(i, i);
						return true;
					} else {
						// no, single clicked on a text object or clicked on a different kind of object
						// start a translation
						if (e.isAltDown()) {
							e.renameTransaction(ToolUtilities.messages.getString("arrow.Duplicate"));
							List<DrawObject> clones = new Vector<DrawObject>();
							for (DrawObject o2 : d) {
								if (o2.isSelected()) {
									o2.setSelected(false);
									DrawObject clone = o2.clone();
									clone.setSelected(true);
									clone.setVisible(true);
									clone.setLocked(false);
									clones.add(clone);
								}
							}
							d.addAll(clones);
						} else {
							e.renameTransaction(ToolUtilities.messages.getString("arrow.Move"));
						}
						currentObject = null;
						originalControlPoints = null;
						originalCurrentControlPoint = 0;
						currentCurrentControlPoint = 0;
						anchorPoints = new IdentityHashMap<DrawObject,Point2D>();
						for (DrawObject o2 : d) {
							if (o2.isSelected()) anchorPoints.put(o2, (Point2D)o2.getLocation().clone());
						}
						startedDragRect = false;
						text = null;
						return true;
					}
				}
			}
			// didn't find a selected shape at the clicked location
			// deselect all shapes
			for (DrawObject o : d) {
				o.setSelected(false);
			}
			// clicked on an unselected shape?
			oi = d.listIterator(d.size());
			while (oi.hasPrevious()) {
				DrawObject o = oi.previous();
				if (o.contains(p)) {
					o.setSelected(true);
					// yes, clicked on an unselected shape
					// double clicked on a text object?
					if (o instanceof TextDrawObject && e.getClickCount() > 1 && !o.isLocked()) {
						// yes, double clicked on a text object
						// start a text edit
						e.renameTransaction(ToolUtilities.messages.getString("arrow.TextEdit"));
						currentObject = null;
						originalControlPoints = null;
						originalCurrentControlPoint = 0;
						currentCurrentControlPoint = 0;
						anchorPoints = null;
						startedDragRect = false;
						text = (TextDrawObject)o;
						int i = text.getCursorIndexOfLocation(e.getDrawGraphics(), e.getX(), e.getY());
						text.setCursor(i, i);
						return true;
					} else {
						// no, single clicked on a text object or clicked on a different kind of object
						// start a translation
						if (e.isAltDown()) {
							e.renameTransaction(ToolUtilities.messages.getString("arrow.Duplicate"));
							List<DrawObject> clones = new Vector<DrawObject>();
							for (DrawObject o2 : d) {
								if (o2.isSelected()) {
									o2.setSelected(false);
									DrawObject clone = o2.clone();
									clone.setSelected(true);
									clone.setVisible(true);
									clone.setLocked(false);
									clones.add(clone);
								}
							}
							d.addAll(clones);
						} else {
							e.renameTransaction(ToolUtilities.messages.getString("arrow.Move"));
						}
						currentObject = null;
						originalControlPoints = null;
						originalCurrentControlPoint = 0;
						currentCurrentControlPoint = 0;
						anchorPoints = new IdentityHashMap<DrawObject,Point2D>();
						for (DrawObject o2 : d) {
							if (o2.isSelected()) anchorPoints.put(o2, (Point2D)o2.getLocation().clone());
						}
						startedDragRect = false;
						text = null;
						return true;
					}
				}
			}
			// clicked nowhere
			// start a drag rectangle for selection
			e.renameTransaction(ToolUtilities.messages.getString("arrow.Select"));
			currentObject = null;
			originalControlPoints = null;
			originalCurrentControlPoint = 0;
			currentCurrentControlPoint = 0;
			anchorPoints = null;
			startedDragRect = true;
			text = null;
			return true;
		}
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (text != null) {
			int i = text.getCursorIndexOfLocation(e.getDrawGraphics(), e.getX(), e.getY());
			text.setCursorEnd(i);
			return true;
		} else {
			if (startedDragRect) {
				return false;
			}
			else if (currentObject != null && originalControlPoints != null) {
				// control point drag
				if (!currentObject.isLocked()) {
					float x = e.getX();
					float y = e.getY();
					if (e.isShiftDown()) {
						Point2D.Float p = controlPointConstrain(e.getPreviousClickedX(), e.getPreviousClickedY(), x, y);
						x = p.x; y = p.y;
					}
					currentCurrentControlPoint = currentObject.setControlPoint(currentCurrentControlPoint, new Point2D.Float(x,y));
					return true;
				} else {
					return false;
				}
			}
			else if (anchorPoints != null) {
				// translation
				float sx = e.getPreviousClickedX();
				float sy = e.getPreviousClickedY();
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
				for (Map.Entry<DrawObject,Point2D> me : anchorPoints.entrySet()) {
					DrawObject o = me.getKey();
					if (!o.isLocked()) {
						Point2D a = me.getValue();
						Point2D na = new Point2D.Double(a.getX()-sx+x, a.getY()-sy+y);
						o.setLocation(na);
					}
				}
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (text != null) {
			int i = text.getCursorIndexOfLocation(e.getDrawGraphics(), e.getX(), e.getY());
			text.setCursorEnd(i);
			return true;
		} else {
			if (startedDragRect) {
				DrawSurface d = e.getDrawSurface();
				Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
				Rectangle2D r = new Rectangle2D.Float(
						Math.min(e.getX(), e.getPreviousClickedX()),
						Math.min(e.getY(), e.getPreviousClickedY()),
						Math.abs(e.getX()-e.getPreviousClickedX()),
						Math.abs(e.getY()-e.getPreviousClickedY())
				);
				if (r.isEmpty()) {
					if (e.isShiftDown()) {
						ListIterator<DrawObject> oi = d.listIterator(d.size());
						while (oi.hasPrevious()) {
							DrawObject o = oi.previous();
							if (o.contains(p)) {
								o.setSelected(!o.isSelected());
								break;
							}
						}
					} else {
						ListIterator<DrawObject> oi = d.listIterator(d.size());
						while (oi.hasPrevious()) {
							DrawObject o = oi.previous();
							if (o.contains(p)) {
								o.setSelected(true);
								break;
							}
						}
					}
				} else {
					if (e.isShiftDown()) {
						for (DrawObject o : d) {
							if (o.intersects(r)) {
								o.setSelected(!o.isSelected());
							}
						}
					} else {
						for (DrawObject o : d) {
							o.setSelected(o.intersects(r));
						}
					}
				}
			}
			else if (currentObject != null && originalControlPoints != null) {
				// control point drag
				if (!currentObject.isLocked()) {
					float x = e.getX();
					float y = e.getY();
					if (e.isShiftDown()) {
						Point2D.Float p = controlPointConstrain(e.getPreviousClickedX(), e.getPreviousClickedY(), x, y);
						x = p.x; y = p.y;
					}
					currentCurrentControlPoint = currentObject.setControlPoint(currentCurrentControlPoint, new Point2D.Float(x,y));
				}
			}
			else if (anchorPoints != null) {
				// translation
				float sx = e.getPreviousClickedX();
				float sy = e.getPreviousClickedY();
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
				for (Map.Entry<DrawObject,Point2D> me : anchorPoints.entrySet()) {
					DrawObject o = me.getKey();
					if (!o.isLocked()) {
						Point2D a = me.getValue();
						Point2D na = new Point2D.Double(a.getX()-sx+x, a.getY()-sy+y);
						o.setLocation(na);
					}
				}
			}
			currentObject = null;
			originalControlPoints = null;
			originalCurrentControlPoint = 0;
			currentCurrentControlPoint = 0;
			anchorPoints = null;
			startedDragRect = false;
			text = null;
			e.commitTransaction();
			return true;
		}
	}
	
	private Point2D.Float controlPointConstrain(float sx, float sy, float x, float y) {
		ControlPointType t = originalControlPoints.get(originalCurrentControlPoint).getType();
		switch (t) {
		case NORTHWEST:
		case NORTHEAST:
		case SOUTHWEST:
		case SOUTHEAST:
			{
				// keep rectangles proportional
				double x1 = Double.NaN;
				double y1 = Double.NaN;
				double x2 = Double.NaN;
				double y2 = Double.NaN;
				for (ControlPoint cp : originalControlPoints) {
					switch (cp.getType()) {
					case NORTHWEST: x1 = cp.getX(); y1 = cp.getY(); break;
					case NORTHEAST: x2 = cp.getX(); y1 = cp.getY(); break;
					case SOUTHWEST: x1 = cp.getX(); y2 = cp.getY(); break;
					case SOUTHEAST: x2 = cp.getX(); y2 = cp.getY(); break;
					default: break;
					}
				}
				if (!(Double.isNaN(x1) || Double.isNaN(y1) || Double.isNaN(x2) || Double.isNaN(y2))) {
					double bx;
					double by;
					switch (t) {
					case NORTHWEST: bx = x2; by = y2; break;
					case NORTHEAST: bx = x1; by = y2; break;
					case SOUTHWEST: bx = x2; by = y1; break;
					case SOUTHEAST: bx = x1; by = y1; break;
					default: bx = x1; by = y1; break;
					}
					double bw = Math.abs(x2-x1);
					double bh = Math.abs(y2-y1);
					double w = (x-bx)*bh;
					double h = (y-by)*bw;
					double s = Math.max(Math.abs(w),Math.abs(h));
					x = (float)(bx+s/bh*Math.signum(w));
					y = (float)(by+s/bw*Math.signum(h));
					return new Point2D.Float(x,y);
				} else {
					// by default, constrain relative to the original location
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
					return new Point2D.Float(x,y);
				}
			}
		case RADIUS:
		case ANGLE:
			{
				// constrain relative to the CENTER
				for (ControlPoint cp : originalControlPoints) {
					if (cp.getType() == ControlPointType.CENTER) {
						sx = (float)cp.getX();
						sy = (float)cp.getY();
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
						return new Point2D.Float(x,y);
					}
				}
				// by default, constrain relative to the original location
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
				return new Point2D.Float(x,y);
			}
		default:
			{
				// by default, constrain relative to the original location
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
				return new Point2D.Float(x,y);
			}
		}
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (startedDragRect && e.isMouseDown()) {
			Shape dr = new Rectangle2D.Float(
					Math.min(e.getX(), e.getPreviousClickedX()),
					Math.min(e.getY(), e.getPreviousClickedY()),
					Math.abs(e.getX()-e.getPreviousClickedX()),
					Math.abs(e.getY()-e.getPreviousClickedY())
			);
			ToolUtilities.fillSelectionShape(e, g, dr);
		}
		return true;
	}
	
	public boolean paintSettingsChanged(ToolEvent e) {
		int delta = e.getPaintSettingsDelta();
		if (text != null) {
			if (delta == -1) {
				text.setPaintSettings(e.getPaintSettings());
			} else {
				PaintSettings ps = text.getPaintSettings();
				if ((delta & CHANGED_DRAW_COMPOSITE) != 0) ps = ps.deriveDrawComposite(e.getPaintSettings().drawComposite);
				if ((delta & CHANGED_DRAW_PAINT) != 0) ps = ps.deriveDrawPaint(e.getPaintSettings().drawPaint);
				if ((delta & CHANGED_FILL_COMPOSITE) != 0) ps = ps.deriveFillComposite(e.getPaintSettings().fillComposite);
				if ((delta & CHANGED_FILL_PAINT) != 0) ps = ps.deriveFillPaint(e.getPaintSettings().fillPaint);
				if ((delta & CHANGED_STROKE) != 0) ps = ps.deriveDrawStroke(e.getPaintSettings().drawStroke);
				if ((delta & CHANGED_FONT) != 0) ps = ps.deriveTextFont(e.getPaintSettings().textFont);
				if ((delta & CHANGED_TEXT_ALIGNMENT) != 0) ps = ps.deriveTextAlignment(e.getPaintSettings().textAlignment);
				if ((delta & CHANGED_ANTI_ALIASED) != 0) {
					ps = ps.deriveDrawAntiAliased(e.getPaintSettings().drawAntiAliased);
					ps = ps.deriveFillAntiAliased(e.getPaintSettings().fillAntiAliased);
					ps = ps.deriveTextAntiAliased(e.getPaintSettings().textAntiAliased);
				}
				text.setPaintSettings(ps);
			}
			return true;
		} else {
			DrawSurface d = e.getDrawSurface();
			if (e.isTransactionInProgress()) {
				for (DrawObject o : d) {
					if (o.isSelected() && !o.isLocked()) {
						if (delta == -1) {
							o.setPaintSettings(e.getPaintSettings());
						} else {
							PaintSettings ps = o.getPaintSettings();
							if ((delta & CHANGED_DRAW_COMPOSITE) != 0) ps = ps.deriveDrawComposite(e.getPaintSettings().drawComposite);
							if ((delta & CHANGED_DRAW_PAINT) != 0) ps = ps.deriveDrawPaint(e.getPaintSettings().drawPaint);
							if ((delta & CHANGED_FILL_COMPOSITE) != 0) ps = ps.deriveFillComposite(e.getPaintSettings().fillComposite);
							if ((delta & CHANGED_FILL_PAINT) != 0) ps = ps.deriveFillPaint(e.getPaintSettings().fillPaint);
							if ((delta & CHANGED_STROKE) != 0) ps = ps.deriveDrawStroke(e.getPaintSettings().drawStroke);
							if ((delta & CHANGED_FONT) != 0) ps = ps.deriveTextFont(e.getPaintSettings().textFont);
							if ((delta & CHANGED_TEXT_ALIGNMENT) != 0) ps = ps.deriveTextAlignment(e.getPaintSettings().textAlignment);
							if ((delta & CHANGED_ANTI_ALIASED) != 0) {
								ps = ps.deriveDrawAntiAliased(e.getPaintSettings().drawAntiAliased);
								ps = ps.deriveFillAntiAliased(e.getPaintSettings().fillAntiAliased);
								ps = ps.deriveTextAntiAliased(e.getPaintSettings().textAntiAliased);
							}
							o.setPaintSettings(ps);
						}
					}
				}
			} else {
				e.beginTransaction(ToolUtilities.messages.getString("arrow.ChangePaintSettings"));
				e.getCanvas().clearPaintSelection();
				for (DrawObject o : d) {
					if (o.isSelected() && !o.isLocked()) {
						if (delta == -1) {
							o.setPaintSettings(e.getPaintSettings());
						} else {
							PaintSettings ps = o.getPaintSettings();
							if ((delta & CHANGED_DRAW_COMPOSITE) != 0) ps = ps.deriveDrawComposite(e.getPaintSettings().drawComposite);
							if ((delta & CHANGED_DRAW_PAINT) != 0) ps = ps.deriveDrawPaint(e.getPaintSettings().drawPaint);
							if ((delta & CHANGED_FILL_COMPOSITE) != 0) ps = ps.deriveFillComposite(e.getPaintSettings().fillComposite);
							if ((delta & CHANGED_FILL_PAINT) != 0) ps = ps.deriveFillPaint(e.getPaintSettings().fillPaint);
							if ((delta & CHANGED_STROKE) != 0) ps = ps.deriveDrawStroke(e.getPaintSettings().drawStroke);
							if ((delta & CHANGED_FONT) != 0) ps = ps.deriveTextFont(e.getPaintSettings().textFont);
							if ((delta & CHANGED_TEXT_ALIGNMENT) != 0) ps = ps.deriveTextAlignment(e.getPaintSettings().textAlignment);
							if ((delta & CHANGED_ANTI_ALIASED) != 0) {
								ps = ps.deriveDrawAntiAliased(e.getPaintSettings().drawAntiAliased);
								ps = ps.deriveFillAntiAliased(e.getPaintSettings().fillAntiAliased);
								ps = ps.deriveTextAntiAliased(e.getPaintSettings().textAntiAliased);
							}
							o.setPaintSettings(ps);
						}
					}
				}
				e.commitTransaction();
			}
			return true;
		}
	}
	
	public boolean keyTyped(ToolEvent e) {
		if (text != null) {
			char ch = e.getChar();
			if (ch >= 0x0020 && ch <= 0xFFFD) {
				text.setSelectedText(Character.toString(ch));
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean keyPressed(ToolEvent e) {
		if (text != null) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ALT:
				return mouseMoved(e);
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_CLEAR:
				text.setCursor(-1, -1);
				if (e.isTransactionInProgress()) e.commitTransaction();
				text = null;
				return true;
			case KeyEvent.VK_BACK_SPACE:
				text.deleteBackward();
				return true;
			case KeyEvent.VK_DELETE:
				text.deleteForward();
				return true;
			case KeyEvent.VK_ENTER:
				text.setSelectedText("\n");
				return true;
			case KeyEvent.VK_UP:
				text.moveCursor(e.getDrawGraphics(), -1, 0, e.isShiftDown());
				return true;
			case KeyEvent.VK_LEFT:
				text.moveCursor(e.getDrawGraphics(), 0, -1, e.isShiftDown());
				return true;
			case KeyEvent.VK_RIGHT:
				text.moveCursor(e.getDrawGraphics(), 0, +1, e.isShiftDown());
				return true;
			case KeyEvent.VK_DOWN:
				text.moveCursor(e.getDrawGraphics(), +1, 0, e.isShiftDown());
				return true;
			default:
				return false;
			}
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ALT:
				return mouseMoved(e);
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				if (e.isTransactionInProgress()) {
					nudge(e);
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Nudge"));
					e.getCanvas().clearPaintSelection();
					nudge(e);
					e.commitTransaction();
				}
				return true;
			case KeyEvent.VK_BACK_SPACE:
			case KeyEvent.VK_DELETE:
				if (e.isTransactionInProgress()) {
					delete(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Clear"));
					e.getCanvas().clearPaintSelection();
					delete(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			default:
				return false;
			}
		}
	}
	
	public boolean keyReleased(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ALT:
			return mouseMoved(e);
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
		case GROUP:
		case UNGROUP:
		case BREAK_APART:
		case LOCK:
		case UNLOCK:
		case HIDE:
		case UNHIDE:
		case SEND_TO_BACK:
		case SEND_BACKWARD:
		case BRING_FORWARD:
		case BRING_TO_FRONT:
			return true;
		default:
			return false;
		}
	}
	
	public boolean enableCommand(ToolEvent e) {
		if (text != null) {
			switch (e.getCommand()) {
			case CUT:
			case COPY:
			case CLEAR:
				return (text.getCursorStart() != text.getCursorEnd());
			case PASTE:
				return ClipboardUtilities.clipboardHasString();
			case SELECT_ALL:
				return true;
			default:
				return false;
			}
		} else {
			switch (e.getCommand()) {
			case CUT:
			case CLEAR:
			case BREAK_APART:
			case SEND_TO_BACK:
			case SEND_BACKWARD:
			case BRING_FORWARD:
			case BRING_TO_FRONT:
				for (DrawObject o : e.getDrawSurface()) {
					if (o.isSelected() && !o.isLocked()) return true;
				}
				return false;
			case COPY:
			case DUPLICATE:
				for (DrawObject o : e.getDrawSurface()) {
					if (o.isSelected()) return true;
				}
				return false;
			case PASTE:
				return ClipboardUtilities.clipboardHasDrawObjects() || ClipboardUtilities.clipboardHasImage() || ClipboardUtilities.clipboardHasString();
			case SELECT_ALL:
			case DESELECT_ALL:
			case INVERT_SELECTION:
				return true;
			case GROUP:
				int cnt = 0;
				for (DrawObject o : e.getDrawSurface()) {
					if (o.isSelected()) cnt++;
				}
				return (cnt > 1);
			case UNGROUP:
				for (DrawObject o : e.getDrawSurface()) {
					if (o.isSelected() && o instanceof GroupDrawObject) return true;
				}
				return false;
			case LOCK:
				for (DrawObject o : e.getDrawSurface()) {
					if (o.isSelected() && !o.isLocked()) return true;
				}
				return false;
			case UNLOCK:
				for (DrawObject o : e.getDrawSurface()) {
					if (o.isSelected() && o.isLocked()) return true;
				}
				return false;
			case HIDE:
				for (DrawObject o : e.getDrawSurface()) {
					if (o.isSelected() && o.isVisible()) return true;
				}
				return false;
			case UNHIDE:
				for (DrawObject o : e.getDrawSurface()) {
					if (o.isSelected() && !o.isVisible()) return true;
				}
				return false;
			default:
				return false;
			}
		}
	}
	
	public boolean doCommand(ToolEvent e) {
		if (text != null) {
			switch (e.getCommand()) {
			case CUT:
				ClipboardUtilities.setClipboardString(text.getSelectedText());
				text.setSelectedText("");
				return true;
			case COPY:
				ClipboardUtilities.setClipboardString(text.getSelectedText());
				return false;
			case PASTE:
				if (ClipboardUtilities.clipboardHasString()) {
					text.setSelectedText(ClipboardUtilities.getClipboardString());
				}
				return true;
			case CLEAR:
				text.setSelectedText("");
				return true;
			case SELECT_ALL:
				text.setCursor(0, text.getText().length());
				return true;
			default:
				return false;
			}
		} else {
			switch (e.getCommand()) {
			case CUT:
				if (e.isTransactionInProgress()) {
					copy(e.getDrawSurface());
					delete(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Cut"));
					e.getCanvas().clearPaintSelection();
					copy(e.getDrawSurface());
					delete(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			case COPY:
				copy(e.getDrawSurface());
				return false;
			case PASTE:
				if (e.isTransactionInProgress()) {
					paste(e);
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Paste"));
					e.getCanvas().clearPaintSelection();
					paste(e);
					e.commitTransaction();
				}
				return true;
			case CLEAR:
				if (e.isTransactionInProgress()) {
					delete(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Clear"));
					e.getCanvas().clearPaintSelection();
					delete(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			case DUPLICATE:
				if (e.isTransactionInProgress()) {
					duplicate(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Duplicate"));
					e.getCanvas().clearPaintSelection();
					duplicate(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			case SELECT_ALL:
				if (e.isTransactionInProgress()) {
					for (DrawObject o : e.getDrawSurface()) {
						o.setSelected(true);
					}
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.SelectAll"));
					e.getCanvas().clearPaintSelection();
					for (DrawObject o : e.getDrawSurface()) {
						o.setSelected(true);
					}
					e.commitTransaction();
				}
				return true;
			case DESELECT_ALL:
				if (e.isTransactionInProgress()) {
					for (DrawObject o : e.getDrawSurface()) {
						o.setSelected(false);
					}
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.DeselectAll"));
					e.getCanvas().clearPaintSelection();
					for (DrawObject o : e.getDrawSurface()) {
						o.setSelected(false);
					}
					e.commitTransaction();
				}
				return true;
			case INVERT_SELECTION:
				if (e.isTransactionInProgress()) {
					for (DrawObject o : e.getDrawSurface()) {
						o.setSelected(!o.isSelected());
					}
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.InvertSelection"));
					e.getCanvas().clearPaintSelection();
					for (DrawObject o : e.getDrawSurface()) {
						o.setSelected(!o.isSelected());
					}
					e.commitTransaction();
				}
				return true;
			case GROUP:
				if (e.isTransactionInProgress()) {
					group(e.getDrawSurface(), e.getPaintSettings());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Group"));
					e.getCanvas().clearPaintSelection();
					group(e.getDrawSurface(), e.getPaintSettings());
					e.commitTransaction();
				}
				return true;
			case UNGROUP:
				if (e.isTransactionInProgress()) {
					ungroup(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Ungroup"));
					e.getCanvas().clearPaintSelection();
					ungroup(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			case BREAK_APART:
				if (e.isTransactionInProgress()) {
					breakApart(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.BreakApart"));
					e.getCanvas().clearPaintSelection();
					breakApart(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			case LOCK:
				if (e.isTransactionInProgress()) {
					for (DrawObject o : e.getDrawSurface()) {
						if (o.isSelected()) o.setLocked(true);
					}
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Lock"));
					e.getCanvas().clearPaintSelection();
					for (DrawObject o : e.getDrawSurface()) {
						if (o.isSelected()) o.setLocked(true);
					}
					e.commitTransaction();
				}
				return true;
			case UNLOCK:
				if (e.isTransactionInProgress()) {
					for (DrawObject o : e.getDrawSurface()) {
						if (o.isSelected()) o.setLocked(false);
					}
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Unlock"));
					e.getCanvas().clearPaintSelection();
					for (DrawObject o : e.getDrawSurface()) {
						if (o.isSelected()) o.setLocked(false);
					}
					e.commitTransaction();
				}
				return true;
			case HIDE:
				if (e.isTransactionInProgress()) {
					for (DrawObject o : e.getDrawSurface()) {
						if (o.isSelected()) o.setVisible(false);
					}
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Hide"));
					e.getCanvas().clearPaintSelection();
					for (DrawObject o : e.getDrawSurface()) {
						if (o.isSelected()) o.setVisible(false);
					}
					e.commitTransaction();
				}
				return true;
			case UNHIDE:
				if (e.isTransactionInProgress()) {
					for (DrawObject o : e.getDrawSurface()) {
						if (o.isSelected()) o.setVisible(true);
					}
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.Unhide"));
					e.getCanvas().clearPaintSelection();
					for (DrawObject o : e.getDrawSurface()) {
						if (o.isSelected()) o.setVisible(true);
					}
					e.commitTransaction();
				}
				return true;
			case BRING_TO_FRONT:
				if (e.isTransactionInProgress()) {
					bringToFront(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.BringToFront"));
					e.getCanvas().clearPaintSelection();
					bringToFront(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			case BRING_FORWARD:
				if (e.isTransactionInProgress()) {
					bringForward(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.BringForward"));
					e.getCanvas().clearPaintSelection();
					bringForward(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			case SEND_BACKWARD:
				if (e.isTransactionInProgress()) {
					sendBackward(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.SendBackward"));
					e.getCanvas().clearPaintSelection();
					sendBackward(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			case SEND_TO_BACK:
				if (e.isTransactionInProgress()) {
					sendToBack(e.getDrawSurface());
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("arrow.SendToBack"));
					e.getCanvas().clearPaintSelection();
					sendToBack(e.getDrawSurface());
					e.commitTransaction();
				}
				return true;
			default:
				return false;
			}
		}
	}
	
	private void nudge(ToolEvent e) {
		float n;
		if (!e.isCtrlDown() && !e.isAltDown() && !e.isShiftDown()) {
			n = 1;
		} else {
			n = 0;
			if (e.isCtrlDown()) n += 8;
			if (e.isAltDown()) n += 16;
			if (e.isShiftDown()) n += 32;
		}
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			nudge(e.getDrawSurface(), -n, 0);
			break;
		case KeyEvent.VK_RIGHT:
			nudge(e.getDrawSurface(), n, 0);
			break;
		case KeyEvent.VK_UP:
			nudge(e.getDrawSurface(), 0, -n);
			break;
		case KeyEvent.VK_DOWN:
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
	
	private void copy(DrawSurface d) {
		List<DrawObject> sel = new Vector<DrawObject>();
		for (DrawObject o : d) {
			if (o.isSelected()) sel.add(o);
		}
		ClipboardUtilities.setClipboardDrawObjects(sel);
	}
	
	private void paste(ToolEvent e) {
		for (DrawObject o : e.getDrawSurface()) {
			o.setSelected(false);
		}
		if (ClipboardUtilities.clipboardHasDrawObjects()) {
			Collection<? extends DrawObject> c = ClipboardUtilities.getClipboardDrawObjects();
			for (DrawObject o : c) {
				DrawObject oo = o.clone();
				oo.setSelected(true);
				e.getDrawSurface().add(oo);
			}
		}
		else if (ClipboardUtilities.clipboardHasImage()) {
			Image i = ClipboardUtilities.getClipboardImage();
			if (ImageUtils.prepImage(i)) {
				int w = i.getWidth(null);
				int h = i.getHeight(null);
				PaintSettings ps = new PaintSettings(null, null);
				ImageDrawObject ido = ImageDrawObject.forGraphicsDrawImage(
					ps, i,
					(int)(e.getPreviousClickedX() - w / 2),
					(int)(e.getPreviousClickedY() - h / 2));
				ido.setSelected(true);
				e.getDrawSurface().add(ido);
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
	
	private void delete(DrawSurface d) {
		Map<DrawObject,DrawObject> sel = new IdentityHashMap<DrawObject,DrawObject>();
		for (DrawObject o : d) {
			if (o.isSelected() && !o.isLocked()) sel.put(o,o);
		}
		for (DrawObject o : sel.values()) {
			d.remove(o);
		}
	}
	
	private void duplicate(DrawSurface d) {
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
	}
	
	private void group(DrawSurface d, PaintSettings ps) {
		int selindex = 0;
		List<DrawObject> sel = new Vector<DrawObject>();
		for (int i = 0; i < d.size(); i++) {
			DrawObject o = d.get(i);
			if (o.isSelected()) {
				sel.add(o);
				selindex = i+1;
			}
		}
		if (sel.size() > 1) {
			GroupDrawObject gdo = new GroupDrawObject(sel);
			gdo.setSelected(true);
			d.add(selindex, gdo);
			d.removeAll(sel);
		}
	}
	
	private void ungroup(DrawSurface d) {
		for (int i = d.size()-1; i >= 0; i--) {
			DrawObject o = d.get(i);
			if (o.isSelected() && o instanceof GroupDrawObject) {
				GroupDrawObject g = (GroupDrawObject)o;
				d.addAll(i+1, g.ungroup());
				d.remove(i);
			}
		}
	}
	
	private void breakApart(DrawSurface d) {
		for (int i = d.size()-1; i >= 0; i--) {
			DrawObject o = d.get(i);
			if (o.isSelected() && !o.isLocked()) {
				if (o instanceof GroupDrawObject) {
					GroupDrawObject g = (GroupDrawObject)o;
					d.addAll(i+1, g.ungroup());
					d.remove(i);
				} else if (o instanceof ShapeDrawObject) {
					ShapeDrawObject s1 = (ShapeDrawObject)o;
					ShapeDrawObject s2 = new PathDrawObject(s1.getPaintSettings(), s1.getShape());
					s2.setSelected(s1.isSelected());
					s2.setVisible(s1.isVisible());
					s2.setLocked(s1.isLocked());
					s2.setTransform(s1.getTransform());
					s2.setShadowSettings(s1.getShadowSettings());
					d.add(i+1, s2);
					d.remove(i);
				}
			}
		}
	}
	
	private void sendToBack(DrawSurface d) {
		List<DrawObject> sel = new Vector<DrawObject>();
		for (DrawObject o : d) {
			if (o.isSelected() && !o.isLocked()) sel.add(o);
		}
		d.removeAll(sel);
		d.addAll(0, sel);
	}
	
	private void sendBackward(DrawSurface d) {
		boolean first = true;
		int index = 0;
		List<DrawObject> sel = new Vector<DrawObject>();
		for (int i = 0; i < d.size(); i++) {
			DrawObject o = d.get(i);
			if (o.isSelected() && !o.isLocked()) {
				if (first) {
					first = false;
					index = i;
				}
				sel.add(o);
			}
		}
		index--;
		if (index < 0) index = 0;
		d.removeAll(sel);
		d.addAll(index, sel);
	}
	
	private void bringForward(DrawSurface d) {
		int index = d.size();
		List<DrawObject> sel = new Vector<DrawObject>();
		for (int i = 0; i < d.size(); i++) {
			DrawObject o = d.get(i);
			if (o.isSelected() && !o.isLocked()) {
				index = i;
				sel.add(o);
			}
		}
		index += 2;
		if (index > d.size()) index = d.size();
		d.removeAll(sel);
		index -= sel.size();
		d.addAll(index, sel);
	}
	
	private void bringToFront(DrawSurface d) {
		List<DrawObject> sel = new Vector<DrawObject>();
		for (DrawObject o : d) {
			if (o.isSelected() && !o.isLocked()) sel.add(o);
		}
		d.removeAll(sel);
		d.addAll(sel);
	}
	
	private Cursor cursor = CursorUtils.CURSOR_ARROW;
	
	private void setCursor(Cursor c) {
		cursor = c;
	}

	public Cursor getCursor(ToolEvent e) {
		return cursor;
	}
	
	public boolean usesLetterKeys() {
		return (text != null);
	}
	
	private static Cursor getControlPointCursor(ControlPoint cp) {
		switch (cp.getType()) {
			case NORTHWEST:
				return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			case NORTH:
				return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			case NORTHEAST:
				return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			case EAST:
				return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			case SOUTHEAST:
				return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
			case SOUTH:
				return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
			case SOUTHWEST:
				return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
			case WEST:
				return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
			case ENDPOINT:
			case CURVED_MIDPOINT:
			case STRAIGHT_MIDPOINT:
			case CONTROL_POINT:
			case PULL_TAB:
				return CursorUtils.CURSOR_ARROW_HALF;
			default:
				return CursorUtils.CURSOR_MOVE;
		}
	}
}
