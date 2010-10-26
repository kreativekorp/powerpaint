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
import com.kreative.paint.Layer;

public class DebugTool implements Tool {
	private static final int K = 0xFF000000;
	private static final Image icon = ToolUtilities.makeIcon(
			16, 16,
			new int[] {
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,K,0,0,K,0,0,0,0,0,0,0,
					0,0,K,K,0,0,K,0,0,K,0,0,0,K,0,0,
					0,0,0,0,K,0,K,0,K,0,0,0,0,K,0,0,
					0,0,0,0,K,K,K,K,K,0,0,0,K,0,0,0,
					0,0,0,K,0,0,0,0,0,K,K,K,0,0,0,0,
					0,0,0,K,K,K,K,K,K,K,K,K,K,0,0,0,
					0,0,0,K,0,0,0,0,0,K,K,K,0,0,0,0,
					0,0,0,0,K,K,K,K,K,0,0,0,K,0,0,0,
					0,0,0,0,K,0,K,0,K,0,0,0,0,K,0,0,
					0,0,K,K,0,0,K,0,0,K,0,0,0,K,0,0,
					0,0,0,0,0,K,0,0,K,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
			}
	);
	
	public ToolCategory getCategory() {
		return ToolCategory.MISC;
	}

	public Image getIcon() {
		return icon;
	}

	public String getName() {
		return "Debug";
	}

	public boolean doCommand(ToolEvent e) {
		logEvent("doCommand", e);
		return false;
	}

	public boolean enableCommand(ToolEvent e) {
		logEvent("enableCommand", e);
		return false;
	}

	public boolean keyPressed(ToolEvent e) {
		logEvent("keyPressed", e);
		return false;
	}

	public boolean keyReleased(ToolEvent e) {
		logEvent("keyReleased", e);
		return false;
	}

	public boolean keyTyped(ToolEvent e) {
		logEvent("keyTyped", e);
		return false;
	}

	public boolean mouseClicked(ToolEvent e) {
		logEvent("mouseClicked", e);
		return false;
	}

	public boolean mouseDragged(ToolEvent e) {
		logEvent("mouseDragged", e);
		return false;
	}

	public boolean mouseEntered(ToolEvent e) {
		logEvent("mouseEntered", e);
		return false;
	}

	public boolean mouseExited(ToolEvent e) {
		logEvent("mouseExited", e);
		return false;
	}

	public boolean mouseHeld(ToolEvent e) {
		logEvent("mouseHeld", e);
		return false;
	}

	public boolean mouseMoved(ToolEvent e) {
		logEvent("mouseMoved", e);
		return false;
	}

	public boolean mousePressed(ToolEvent e) {
		logEvent("mousePressed", e);
		return false;
	}

	public boolean mouseReleased(ToolEvent e) {
		logEvent("mouseReleased", e);
		return false;
	}

	public boolean paintSettingsChanged(ToolEvent e) {
		logEvent("paintSettingsChanged", e);
		return false;
	}

	public boolean respondsToCommand(ToolEvent e) {
		logEvent("respondsToCommand", e);
		return false;
	}

	public boolean toolDeselected(ToolEvent e) {
		logEvent("toolDeselected", e);
		return false;
	}

	public boolean toolDoubleClicked(ToolEvent e) {
		logEvent("toolDoubleClicked", e);
		return false;
	}

	public boolean toolSelected(ToolEvent e) {
		logEvent("toolSelected", e);
		return false;
	}

	public boolean toolSettingsChanged(ToolEvent e) {
		logEvent("toolSettingsChanged", e);
		return false;
	}

	public boolean paintIntermediate(ToolEvent e, Graphics2D g) {
		return false;
	}

	public Cursor getCursor(ToolEvent e) {
		return Cursor.getDefaultCursor();
	}

	public boolean paintIntermediateUsesCanvasCoordinates() {
		return false;
	}
	
	public boolean pushPaintSelectionUponEntry() {
		return false;
	}
	
	public boolean clearDrawSelectionUponEntry() {
		return false;
	}

	public boolean showDrawSelection(ToolEvent e, Layer l) {
		return true;
	}

	public boolean showPaintSelection(ToolEvent e) {
		return true;
	}

	public int getMouseHeldInterval() {
		return 100;
	}

	public boolean shiftConstrainsCoordinates() {
		return false;
	}

	public boolean usesLetterKeys() {
		return false;
	}
	
	public boolean doubleClickForOptions() {
		return false;
	}

	public boolean validForDrawMode() {
		return true;
	}

	public boolean validForPaintMode() {
		return true;
	}
	
	private int generation = 0;
	private void logEvent(String method, ToolEvent event) {
		//  System.out.println("         1         2         3         4         5         6         7         8         9         0         1         2         3         4         5         6");
		//  System.out.println("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
		//  System.out.println("paintSetti PD CellularAu CellularAu TSDT PSDT ET cdCc..CPCX..CPCY..CvPX..CvPY..CvsX..CvsY..LPCX..LPCY..LyPX..LyPY..LyrX..LyrY CASKCde KChr INVERT_SEL");
		if ((generation % 48) == 0) {
			System.out.println("<------------------------------------------------------------------------149------------------------------------------------------------------------>");
			System.out.println("methodName PD PrevToolNm NextToolNm DlTS DlPS Ev CDcc  CPCX  CPCY   CPX   CPY    CX    CY  LPCX  LPCY   LPX   LPY    LX    LY Mod Key Char COMMAND_NM");
		}
		System.out.format("%-10.10s %c%c %-10.10s %-10.10s %04X %04X %2d %c%c%2d%6.1f%6.1f%6.1f%6.1f%6.1f%6.1f%6.1f%6.1f%6.1f%6.1f%6.1f%6.1f %c%c%c%4d %04X %-10.10s%n",
				method,
				(event.isInPaintMode() ? 'P' : ' '),
				(event.isInDrawMode() ? 'D' : ' '),
				(event.getPreviousTool() == null ? "" : event.getPreviousTool().getClass().getSimpleName()),
				(event.getNextTool() == null ? "" : event.getNextTool().getClass().getSimpleName()),
				event.getToolContextDelta(),
				event.getPaintSettingsDelta(),
				event.getEventType(),
				(event.isMouseOnCanvas() ? 'C' : ' '),
				(event.isMouseDown() ? 'D' : ' '),
				event.getClickCount(),
				event.getCanvasPreviousClickedX(),
				event.getCanvasPreviousClickedY(),
				event.getCanvasPreviousX(),
				event.getCanvasPreviousY(),
				event.getCanvasX(),
				event.getCanvasY(),
				event.getPreviousClickedX(),
				event.getPreviousClickedY(),
				event.getPreviousX(),
				event.getPreviousY(),
				event.getX(),
				event.getY(),
				(event.isCtrlDown() ? 'C' : ' '),
				(event.isAltDown() ? 'A' : ' '),
				(event.isShiftDown() ? 'S' : ' '),
				event.getKeyCode(),
				(int)event.getChar(),
				(event.getCommand() == null ? "" : event.getCommand().name())
		);
		generation++;
	}
}
