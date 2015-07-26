/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.PaintSettings;

public class AlphabetStampTool extends AbstractPaintDrawTool implements ToolOptions.Alphabets {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,K,0,K,K,K,0,K,0,0,0,0,
					0,0,0,0,0,K,0,0,0,0,0,K,0,0,0,0,
					0,0,0,0,0,K,0,0,0,0,0,K,0,0,0,0,
					0,0,0,0,0,0,K,0,0,0,K,0,0,0,0,0,
					0,0,0,0,0,0,0,K,K,K,0,0,0,0,0,0,
					0,0,0,0,0,0,0,K,0,K,0,0,0,0,0,0,
					0,0,0,K,K,K,K,K,0,K,K,K,K,K,0,0,
					0,0,K,0,0,0,0,K,K,K,0,0,0,0,K,0,
					0,K,0,0,0,0,0,0,K,0,0,0,0,0,0,K,
					0,K,K,K,K,0,0,K,0,K,0,0,K,K,K,K,
					0,K,0,0,0,0,0,K,K,K,0,0,0,0,0,K,
					0,K,0,0,0,0,K,0,0,0,K,0,0,0,0,K,
					0,K,0,0,0,0,K,0,0,0,K,0,0,0,0,K,
					0,0,K,K,K,K,K,K,K,K,K,K,K,K,K,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}
	
	protected Image getBWIcon() {
		return icon;
	}
	
	public boolean mousePressed(ToolEvent e) {
		e.beginTransaction(getName());
		PaintSettings ps = e.getPaintSettings();
		Graphics2D g = e.isInDrawMode() ? e.getDrawGraphics() : e.getPaintGraphics();
		ps.applyFill(g);
		g.setFont(e.tc().getLetterFont());
		FontMetrics fm = g.getFontMetrics();
		int w = fm.charWidth(e.tc().getLetter());
		g.drawString(String.valueOf(Character.toChars(e.tc().getLetter())), e.getX()-w/2.0f, e.getY());
		return true;
	}
	
	public boolean mouseDragged(ToolEvent e) {
		if (e.isCtrlDown()) {
			PaintSettings ps = e.getPaintSettings();
			Graphics2D g = e.isInDrawMode() ? e.getDrawGraphics() : e.getPaintGraphics();
			ps.applyFill(g);
			g.setFont(e.tc().getLetterFont());
			FontMetrics fm = g.getFontMetrics();
			int w = fm.charWidth(e.tc().getLetter());
			g.drawString(String.valueOf(Character.toChars(e.tc().getLetter())), e.getX()-w/2.0f, (int)e.getY());
			return true;
		}
		return false;
	}
	
	public boolean mouseReleased(ToolEvent e) {
		e.commitTransaction();
		return false;
	}
	
	public boolean keyTyped(ToolEvent e) {
		char ch = e.getChar();
		if (isPrintable(ch)) {
			e.tc().setLetter(ch);
		}
		return false;
	}
	
	public boolean keyPressed(ToolEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			e.tc().prevLetter();
			break;
		case KeyEvent.VK_RIGHT:
			e.tc().nextLetter();
			break;
		case KeyEvent.VK_UP:
			e.tc().prevAlphabet();
			break;
		case KeyEvent.VK_DOWN:
			e.tc().nextAlphabet();
			break;
		}
		return false;
	}
	
	public Cursor getCursor(ToolEvent e) {
		return e.tc().getLetterCursor();
	}
	
	public boolean shiftConstrainsCoordinates() {
		return true;
	}
	
	public boolean usesLetterKeys() {
		return true;
	}
	
	public boolean doubleClickForOptions() {
		return true;
	}
	
	private static boolean isPrintable(int ch) {
		// Low end filter.
		if (ch < 0x21) return false;
		if (ch < 0x7F) return true;
		if (ch < 0xA1) return false;
		// High end filter.
		if (ch >= 0x10FFFE) return false;
		if ((ch & 0xFFFF) >= 0xFFFE) return false;
		if (ch == 0xFFEF) return false;
		// The rest.
		if (Character.isWhitespace(ch)) return false;
		if (Character.isSpaceChar(ch)) return false;
		return true;
	}
}
