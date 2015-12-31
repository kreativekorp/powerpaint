package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.datatransfer.ClipboardUtilities;
import com.kreative.paint.document.draw.TextDrawObject;

public class TextTool extends AbstractPaintDrawTool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,K,K,K,K,K,0,0,0,0,0,
					0,0,0,0,0,0,K,0,0,K,K,0,0,0,0,0,
					0,0,0,0,0,K,K,0,0,K,K,K,0,0,0,0,
					0,0,0,0,0,K,0,0,0,0,K,K,0,0,0,0,
					0,0,0,0,K,K,0,0,0,0,K,K,K,0,0,0,
					0,0,0,0,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,K,K,0,0,0,0,0,0,K,K,K,0,0,
					0,0,0,K,K,0,0,0,0,0,0,0,K,K,0,0,
					0,0,K,K,0,0,0,0,0,0,0,0,K,K,K,0,
					0,K,K,K,K,0,0,0,0,0,0,K,K,K,K,K,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	private static final Cursor curs = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
	
	public ToolCategory getCategory() {
		return ToolCategory.SHAPE;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	private TextDrawObject text = null;
	
	public boolean toolSelected(ToolEvent e) {
		text = null;
		return false;
	}
	
	public boolean toolDeselected(ToolEvent e) {
		if (text != null) {
			text.setCursor(-1, -1);
			if (e.isInDrawMode()) e.getDrawSurface().add(text);
			else text.paint(e.getPaintGraphics());
			e.commitTransaction();
			text = null;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean mousePressed(ToolEvent e) {
		if (text != null) {
			if (text.contains(e.getX(), e.getY())) {
				int i = text.getCursorIndexOfLocation(e.getDrawGraphics(), e.getX(), e.getY());
				text.setCursor(i, i);
				return true;
			} else {
				text.setCursor(-1, -1);
				if (e.isInDrawMode()) e.getDrawSurface().add(text);
				else text.paint(e.getPaintGraphics());
				e.commitTransaction();
			}
		}
		e.beginTransaction(getName());
		text = new TextDrawObject(e.getPaintSettings(), e.getX(), e.getY(), Double.MAX_VALUE, "");
		text.setCursor(0, 0);
		return false;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (text != null) {
			int i = text.getCursorIndexOfLocation(e.getDrawGraphics(), e.getX(), e.getY());
			text.setCursorEnd(i);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean mouseReleased(ToolEvent e) {
		if (text != null) {
			int i = text.getCursorIndexOfLocation(e.getDrawGraphics(), e.getX(), e.getY());
			text.setCursorEnd(i);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		if (text != null) {
			text.setPaintSettings(e.getPaintSettings());
			text.paint(g);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean paintSettingsChanged(ToolEvent e) {
		if (text != null) text.setPaintSettings(e.getPaintSettings());
		return false;
	}
	
	public boolean keyTyped(ToolEvent e) {
		if (text != null) {
			char ch = e.getChar();
			if (ch >= 0x0020 && ch <= 0xFFFD) {
				text.setSelectedText(""+ch);
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
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_CLEAR:
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
			return false;
		}
	}
	
	public boolean respondsToCommand(ToolEvent e) {
		switch (e.getCommand()) {
		case CUT:
		case COPY:
		case PASTE:
		case CLEAR:
		case SELECT_ALL:
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
			return false;
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
			return false;
		}
	}
	
	public Cursor getCursor(ToolEvent e) {
		return curs;
	}
	
	public boolean usesLetterKeys() {
		return true;
	}
}
