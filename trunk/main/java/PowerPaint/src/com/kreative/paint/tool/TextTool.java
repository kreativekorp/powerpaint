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
import java.awt.geom.Point2D;
import com.kreative.paint.datatransfer.ClipboardUtilities;
import com.kreative.paint.draw.TextDrawObject;

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
		text = new TextDrawObject(e.getX(), e.getY(), "", e.getPaintSettings());
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
				text.backspace();
				return true;
			case KeyEvent.VK_DELETE:
				text.delete();
				return true;
			case KeyEvent.VK_ENTER:
				text.setSelectedText("\n");
				return true;
			case KeyEvent.VK_UP:
				if (e.isShiftDown()) {
					int cs = text.getCursorStart();
					int ce = text.getCursorEnd();
					if (ce > 0) {
						Graphics2D g = e.getDrawGraphics();
						Point2D p = text.getLocationOfCursorIndex(g, ce);
						float x = (float)p.getX();
						float y = (float)p.getY() - g.getFontMetrics(text.getFont()).getHeight();
						ce = text.getCursorIndexOfLocation(g, x, y);
						text.setCursor(cs, ce);
					}
				}
				else {
					int cs = text.getCursorStart();
					if (cs > 0) {
						Graphics2D g = e.getDrawGraphics();
						Point2D p = text.getLocationOfCursorIndex(g, cs);
						float x = (float)p.getX();
						float y = (float)p.getY() - g.getFontMetrics(text.getFont()).getHeight();
						cs = text.getCursorIndexOfLocation(g, x, y);
					}
					text.setCursor(cs, cs);
				}
				return true;
			case KeyEvent.VK_LEFT:
				if (e.isShiftDown()) {
					int cs = text.getCursorStart();
					int ce = text.getCursorEnd();
					if (ce > 0) {
						ce--;
						text.setCursor(cs, ce);
					}
				}
				else {
					int cs = text.getCursorStart();
					if (cs > 0) {
						cs--;
					}
					text.setCursor(cs, cs);
				}
				return true;
			case KeyEvent.VK_RIGHT:
				if (e.isShiftDown()) {
					int cs = text.getCursorStart();
					int ce = text.getCursorEnd();
					if (ce < text.getText().length()) {
						ce++;
						text.setCursor(cs, ce);
					}
				}
				else {
					int ce = text.getCursorEnd();
					if (ce < text.getText().length()) {
						ce++;
					}
					text.setCursor(ce, ce);
				}
				return true;
			case KeyEvent.VK_DOWN:
				if (e.isShiftDown()) {
					int cs = text.getCursorStart();
					int ce = text.getCursorEnd();
					if (ce < text.getText().length()) {
						Graphics2D g = e.getDrawGraphics();
						Point2D p = text.getLocationOfCursorIndex(g, ce);
						float x = (float)p.getX();
						float y = (float)p.getY() + g.getFontMetrics(text.getFont()).getHeight();
						ce = text.getCursorIndexOfLocation(g, x, y);
						text.setCursor(cs, ce);
					}
				}
				else {
					int ce = text.getCursorEnd();
					if (ce < text.getText().length()) {
						Graphics2D g = e.getDrawGraphics();
						Point2D p = text.getLocationOfCursorIndex(g, ce);
						float x = (float)p.getX();
						float y = (float)p.getY() + g.getFontMetrics(text.getFont()).getHeight();
						ce = text.getCursorIndexOfLocation(g, x, y);
					}
					text.setCursor(ce, ce);
				}
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
