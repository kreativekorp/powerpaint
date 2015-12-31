package com.kreative.paint.tool;

import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import com.kreative.paint.document.draw.PaintSettings;

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
