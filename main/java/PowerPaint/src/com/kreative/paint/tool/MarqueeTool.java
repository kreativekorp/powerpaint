package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import com.kreative.paint.Layer;
import com.kreative.paint.datatransfer.ClipboardUtilities;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.geom.BitmapShape;
import com.kreative.paint.util.CursorUtils;
import com.kreative.paint.util.ImageUtils;

public abstract class MarqueeTool extends AbstractPaintSelectionTool {
	private boolean definingSelection = false;
	private boolean draggingSelection = false;
	
	// interface to implementing classes
	protected boolean selectAllLasso() { return false; }
	protected boolean toolSelectedImpl(ToolEvent e) { return false; }
	protected boolean toolDeselectedImpl(ToolEvent e) { return false; }
	protected boolean mousePressedImpl(ToolEvent e) { return false; }
	protected boolean mouseHeldImpl(ToolEvent e) { return false; }
	protected boolean mouseClickedImpl(ToolEvent e) { return false; }
	protected boolean mouseDraggedImpl(ToolEvent e) { return false; }
	protected boolean mouseReleasedImpl(ToolEvent e) { return false; }
	protected boolean keyPressedImpl(ToolEvent e) { return false; }
	protected boolean keyTypedImpl(ToolEvent e) { return false; }
	protected boolean keyReleasedImpl(ToolEvent e) { return false; }
	protected boolean paintIntermediateImpl(ToolEvent e, Graphics2D g) { return false; }
	protected boolean shiftConstrainsCoordinatesImpl() { return false; }
	protected Cursor getDefaultCursor() { return CursorUtils.CURSOR_SELECT; }
	
	protected final void beginSelection(ToolEvent e) {
		e.beginTransaction(ToolUtilities.messages.getString("marquee.Select"));
		for (Layer l : e.getCanvas()) {
			for (DrawObject o : l) {
				if (o.isSelected()) o.setSelected(false);
			}
		}
		e.getCanvas().pushPaintSelection();
		definingSelection = true;
	}
	
	protected final void commitSelection(ToolEvent e, Shape s) {
		if (e.isCtrlDown() && e.isAltDown()) e.getCanvas().xorPaintSelection(s);
		else if (e.isCtrlDown()) e.getCanvas().addPaintSelection(s);
		else if (e.isAltDown()) e.getCanvas().subtractPaintSelection(s);
		else e.getCanvas().setPaintSelection(s);
		e.getCanvas().pushPaintSelection();
		e.commitTransaction();
		definingSelection = false;
	}
	
	protected final void rollbackSelection(ToolEvent e) {
		e.rollbackTransaction();
		definingSelection = false;
	}
	
	protected final boolean isSelectionInProgress() {
		return definingSelection;
	}
	
	// interface to Tool
	public final boolean paintIntermediateUsesCanvasCoordinates() {
		return true;
	}

	public final boolean toolSelected(ToolEvent e) {
		return toolSelectedImpl(e);
	}

	public final boolean toolDeselected(ToolEvent e) {
		return toolDeselectedImpl(e);
	}

	public final boolean toolDoubleClicked(ToolEvent e) {
		if (!definingSelection && !draggingSelection) {
			e.beginTransaction(ToolUtilities.messages.getString("marquee.SelectAll"));
			for (Layer l : e.getCanvas()) {
				for (DrawObject o : l) {
					if (o.isSelected()) o.setSelected(false);
				}
			}
			e.getCanvas().pushPaintSelection();
			if (selectAllLasso()) {
				Layer l = e.getLayer();
				if (l == null) {
					e.getCanvas().setPaintSelection(null);
				} else {
					Rectangle r = new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight());
					boolean empty = true;
					int[] rgb = new int[r.width * r.height];
					l.getRGB(r.x-l.getX(), r.y-l.getY(), r.width, r.height, rgb, 0, r.width);
					for (int ay = 0; ay < rgb.length; ay += r.width) {
						for (int ax = 0; ax < r.width; ax++) {
							if ((rgb[ay+ax] & 0xFF000000) != 0) {
								empty = false;
								rgb[ay+ax] = 0xFF000000;
							} else {
								rgb[ay+ax] = 0;
							}
						}
					}
					if (empty) {
						e.getCanvas().setPaintSelection(null);
					} else {
						e.getCanvas().setPaintSelection(new BitmapShape(rgb, r.x, r.y, r.width, r.height));
					}
				}
			} else {
				e.getCanvas().setPaintSelection(new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight()));
			}
			e.getCanvas().pushPaintSelection();
			e.commitTransaction();
			return true;
		} else {
			return false;
		}
	}
	
	public final boolean toolSettingsChanged(ToolEvent e) {
		return false;
	}

	public final boolean paintSettingsChanged(ToolEvent e) {
		return false;
	}
	
	public final boolean mouseEntered(ToolEvent e) {
		return false;
	}
	
	public final boolean mouseMoved(ToolEvent e) {
		return false;
	}
	
	public final boolean mouseExited(ToolEvent e) {
		return false;
	}

	public final boolean mousePressed(ToolEvent e) {
		if (definingSelection) {
			return mousePressedImpl(e);
		} else if (draggingSelection) {
			return false;
		} else {
			Shape ps = e.getCanvas().getPaintSelection();
			if (ps != null && ps.contains(e.getCanvasX(), e.getCanvasY())) {
				if (e.isCtrlDown()) {
					e.beginTransaction(ToolUtilities.messages.getString("marquee.Select"));
					for (Layer l : e.getCanvas()) {
						for (DrawObject o : l) {
							if (o.isSelected()) o.setSelected(false);
						}
					}
					e.getCanvas().pushPaintSelection();
				} else if (e.isAltDown()) {
					e.beginTransaction(ToolUtilities.messages.getString("marquee.Duplicate"));
					for (Layer l : e.getCanvas()) {
						for (DrawObject o : l) {
							if (o.isSelected()) o.setSelected(false);
						}
					}
					e.getCanvas().popPaintSelection(false, true);
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("marquee.Move"));
					for (Layer l : e.getCanvas()) {
						for (DrawObject o : l) {
							if (o.isSelected()) o.setSelected(false);
						}
					}
					if (!e.getCanvas().isPaintSelectionPopped()) {
						e.getCanvas().popPaintSelection(false, false);
					}
				}
				draggingSelection = true;
				return true;
			} else {
				return mousePressedImpl(e);
			}
		}
	}

	public final boolean mouseHeld(ToolEvent e) {
		if (definingSelection) {
			return mouseHeldImpl(e);
		} else if (draggingSelection) {
			return false;
		} else {
			return mouseHeldImpl(e);
		}
	}

	public final boolean mouseClicked(ToolEvent e) {
		if (definingSelection) {
			return mouseClickedImpl(e);
		} else if (draggingSelection) {
			return false;
		} else {
			return mouseClickedImpl(e);
		}
	}

	public final boolean mouseDragged(ToolEvent e) {
		if (definingSelection) {
			return mouseDraggedImpl(e);
		} else if (draggingSelection) {
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(e.getCanvasX()-e.getCanvasPreviousX(), e.getCanvasY()-e.getCanvasPreviousY()));
			return true;
		} else {
			return mouseDraggedImpl(e);
		}
	}

	public final boolean mouseReleased(ToolEvent e) {
		if (definingSelection) {
			return mouseReleasedImpl(e);
		} else if (draggingSelection) {
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(e.getCanvasX()-e.getCanvasPreviousX(), e.getCanvasY()-e.getCanvasPreviousY()));
			e.commitTransaction();
			draggingSelection = false;
			return true;
		} else {
			return mouseReleasedImpl(e);
		}
	}

	public final boolean keyPressed(ToolEvent e) {
		if (definingSelection) {
			return keyPressedImpl(e);
		} else if (draggingSelection) {
			return false;
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_BACK_SPACE:
			case KeyEvent.VK_DELETE:
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Clear"));
				for (Layer l : e.getCanvas()) {
					for (DrawObject o : l) {
						if (o.isSelected()) o.setSelected(false);
					}
				}
				e.getCanvas().deletePaintSelection(false);
				if (!e.isCtrlDown() && !e.isAltDown()) e.getCanvas().clearPaintSelection();
				e.commitTransaction();
				return true;
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_CLEAR:
				e.beginTransaction(ToolUtilities.messages.getString("marquee.DeselectAll"));
				for (Layer l : e.getCanvas()) {
					for (DrawObject o : l) {
						if (o.isSelected()) o.setSelected(false);
					}
				}
				e.getCanvas().clearPaintSelection();
				e.commitTransaction();
				return true;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				if (e.isCtrlDown()) {
					e.beginTransaction(ToolUtilities.messages.getString("marquee.Select"));
					for (Layer l : e.getCanvas()) {
						for (DrawObject o : l) {
							if (o.isSelected()) o.setSelected(false);
						}
					}
					e.getCanvas().pushPaintSelection();
				} else if (e.isAltDown()) {
					e.beginTransaction(ToolUtilities.messages.getString("marquee.Duplicate"));
					for (Layer l : e.getCanvas()) {
						for (DrawObject o : l) {
							if (o.isSelected()) o.setSelected(false);
						}
					}
					e.getCanvas().popPaintSelection(false, true);
				} else {
					e.beginTransaction(ToolUtilities.messages.getString("marquee.Move"));
					for (Layer l : e.getCanvas()) {
						for (DrawObject o : l) {
							if (o.isSelected()) o.setSelected(false);
						}
					}
					if (!e.getCanvas().isPaintSelectionPopped()) {
						e.getCanvas().popPaintSelection(false, false);
					}
				}
				nudge(e);
				e.commitTransaction();
				return true;
			default:
				return keyPressedImpl(e);
			}
		}
	}

	public final boolean keyTyped(ToolEvent e) {
		if (definingSelection) {
			return keyTypedImpl(e);
		} else if (draggingSelection) {
			return false;
		} else {
			return keyTypedImpl(e);
		}
	}

	public final boolean keyReleased(ToolEvent e) {
		if (definingSelection) {
			return keyReleasedImpl(e);
		} else if (draggingSelection) {
			return false;
		} else {
			return keyReleasedImpl(e);
		}
	}
	
	public final boolean respondsToCommand(ToolEvent e) {
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

	public final boolean enableCommand(ToolEvent e) {
		switch (e.getCommand()) {
		case CUT:
		case COPY:
		case CLEAR:
		case DUPLICATE:
			return (e.getCanvas().getPaintSelection() != null);
		case PASTE:
			return ClipboardUtilities.clipboardHasImage();
		case SELECT_ALL:
		case DESELECT_ALL:
		case INVERT_SELECTION:
			return true;
		default:
			return false;
		}
	}

	public final boolean doCommand(ToolEvent e) {
		switch (e.getCommand()) {
		case CUT:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.Cut"));
			for (Layer l : e.getCanvas()) {
				for (DrawObject o : l) {
					if (o.isSelected()) o.setSelected(false);
				}
			}
			ClipboardUtilities.setClipboardImage(e.getCanvas().copyPaintSelection());
			e.getCanvas().deletePaintSelection(false);
			e.getCanvas().clearPaintSelection();
			e.commitTransaction();
			return true;
		case COPY:
			ClipboardUtilities.setClipboardImage(e.getCanvas().copyPaintSelection());
			return false;
		case PASTE:
			Image pasteImage = ClipboardUtilities.getClipboardImage();
			if (ImageUtils.prepImage(pasteImage)) {
				e.beginTransaction(ToolUtilities.messages.getString("marquee.Paste"));
				for (Layer l : e.getCanvas()) {
					for (DrawObject o : l) {
						if (o.isSelected()) o.setSelected(false);
					}
				}
				e.getCanvas().pastePaintSelection(pasteImage, AffineTransform.getTranslateInstance(Math.round(e.getCanvasPreviousClickedX()-pasteImage.getWidth(null)/2.0), Math.round(e.getCanvasPreviousClickedY()-pasteImage.getHeight(null)/2.0)));
				e.commitTransaction();
			} else {
				System.err.println("Error: Failed to paste image.");
			}
			return true;
		case CLEAR:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.Clear"));
			for (Layer l : e.getCanvas()) {
				for (DrawObject o : l) {
					if (o.isSelected()) o.setSelected(false);
				}
			}
			e.getCanvas().deletePaintSelection(false);
			e.getCanvas().clearPaintSelection();
			e.commitTransaction();
			return true;
		case DUPLICATE:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.Duplicate"));
			for (Layer l : e.getCanvas()) {
				for (DrawObject o : l) {
					if (o.isSelected()) o.setSelected(false);
				}
			}
			e.getCanvas().popPaintSelection(false, true);
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(8, 8));
			e.commitTransaction();
			return true;
		case SELECT_ALL:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.SelectAll"));
			for (Layer l : e.getCanvas()) {
				for (DrawObject o : l) {
					if (o.isSelected()) o.setSelected(false);
				}
			}
			e.getCanvas().pushPaintSelection();
			e.getCanvas().setPaintSelection(new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight()));
			e.getCanvas().pushPaintSelection();
			e.commitTransaction();
			return true;
		case DESELECT_ALL:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.DeselectAll"));
			for (Layer l : e.getCanvas()) {
				for (DrawObject o : l) {
					if (o.isSelected()) o.setSelected(false);
				}
			}
			e.getCanvas().clearPaintSelection();
			e.commitTransaction();
			return true;
		case INVERT_SELECTION:
			e.beginTransaction(ToolUtilities.messages.getString("marquee.InvertSelection"));
			for (Layer l : e.getCanvas()) {
				for (DrawObject o : l) {
					if (o.isSelected()) o.setSelected(false);
				}
			}
			e.getCanvas().pushPaintSelection();
			Area invertSelection = new Area(new Rectangle(0, 0, e.getCanvas().getWidth(), e.getCanvas().getHeight()));
			invertSelection.exclusiveOr(new Area(e.getCanvas().getPaintSelection()));
			e.getCanvas().setPaintSelection(invertSelection);
			e.getCanvas().pushPaintSelection();
			e.commitTransaction();
			return true;
		default:
			return false;
		}
	}

	public final boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		return paintIntermediateImpl(e,g);
	}
	
	public final boolean shiftConstrainsCoordinates() {
		return draggingSelection || shiftConstrainsCoordinatesImpl();
	}
	
	private void nudge(ToolEvent e) {
		float n = e.isShiftDown() ? 8 : 1;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(-n, 0));
			break;
		case KeyEvent.VK_RIGHT:
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(n, 0));
			break;
		case KeyEvent.VK_UP:
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(0, -n));
			break;
		case KeyEvent.VK_DOWN:
			e.getCanvas().transformPaintSelection(AffineTransform.getTranslateInstance(0, n));
			break;
		}
	}
	
	public final Cursor getCursor(ToolEvent e) {
		if (definingSelection) {
			return getDefaultCursor();
		} else if (draggingSelection) {
			if (e.isCtrlDown()) {
				return CursorUtils.CURSOR_MOVE_SELECT;
			} else if (e.isAltDown()) {
				return CursorUtils.CURSOR_ARROW_HALF_DOUBLE_HALLOW;
			} else {
				return CursorUtils.CURSOR_MOVE;
			}
		} else {
			Shape ps = e.getCanvas().getPaintSelection();
			if (ps != null && ps.contains(e.getCanvasX(), e.getCanvasY())) {
				if (e.isCtrlDown()) {
					return CursorUtils.CURSOR_MOVE_SELECT;
				} else if (e.isAltDown()) {
					return CursorUtils.CURSOR_ARROW_HALF_DOUBLE_HALLOW;
				} else {
					return CursorUtils.CURSOR_MOVE;
				}
			} else {
				return getDefaultCursor();
			}
		}
	}
}
