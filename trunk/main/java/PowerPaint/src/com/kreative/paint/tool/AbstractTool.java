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

import java.awt.*;
import java.io.*;
import java.net.*;
import com.kreative.paint.Layer;

public abstract class AbstractTool implements Tool {
	public String getName() {
		try {
			return ToolUtilities.messages.getString(this.getClass().getSimpleName());
		} catch (Exception e) {
			return this.getClass().getSimpleName();
		}
	}
	
	public Image getIcon() {
		try {
			String useBWOnly = System.getProperty("com.kreative.paint.tools.bwicons");
			if (useBWOnly != null && useBWOnly.length() > 0) return getBWIcon();
		} catch (Exception e) {}
		
		try {
			Class<?> tc = this.getClass();
			URL u = tc.getResource(tc.getSimpleName()+".png");
			URLConnection uc = u.openConnection();
			InputStream ui = uc.getInputStream();
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			byte[] buff = new byte[16384];
			int blen;
			while ((blen = ui.read(buff)) >= 0) {
				bs.write(buff, 0, blen);
			}
			ui.close();
			bs.close();
			byte[] b = bs.toByteArray();
			Toolkit tk = Toolkit.getDefaultToolkit();
			Image i = tk.createImage(b);
			tk.prepareImage(i, -1, -1, null);
			return i;
		} catch (Exception e) {
			return getBWIcon();
		}
	}
	
	protected abstract Image getBWIcon();
	
	public int getMouseHeldInterval() { return 0; }
	public boolean shiftConstrainsCoordinates() { return false; }
	public boolean usesLetterKeys() { return false; }
	public boolean paintIntermediateUsesCanvasCoordinates() { return false; }
	public boolean doubleClickForOptions() { return false; }
	public boolean pushPaintSelectionUponEntry() { return true; }
	public boolean clearDrawSelectionUponEntry() { return true; }
	
	public boolean toolSelected(ToolEvent e) { return false; }
	public boolean toolDoubleClicked(ToolEvent e) { return false; }
	public boolean toolDeselected(ToolEvent e) { return false; }
	public boolean toolSettingsChanged(ToolEvent e) { return false; }
	public boolean paintSettingsChanged(ToolEvent e) { return false; }
	public boolean mouseEntered(ToolEvent e) { return false; }
	public boolean mouseMoved(ToolEvent e) { return false; }
	public boolean mousePressed(ToolEvent e) { return false; }
	public boolean mouseHeld(ToolEvent e) { return false; }
	public boolean mouseClicked(ToolEvent e) { return false; }
	public boolean mouseDragged(ToolEvent e) { return false; }
	public boolean mouseReleased(ToolEvent e) { return false; }
	public boolean mouseExited(ToolEvent e) { return false; }
	public boolean keyPressed(ToolEvent e) { return false; }
	public boolean keyTyped(ToolEvent e) { return false; }
	public boolean keyReleased(ToolEvent e) { return false; }
	public boolean respondsToCommand(ToolEvent e) { return false; }
	public boolean enableCommand(ToolEvent e) { return false; }
	public final boolean canDoCommand(ToolEvent e) { return false; }
	public boolean doCommand(ToolEvent e) { return false; }
	public boolean paintIntermediate(ToolEvent e, Graphics2D g) { return false; }
	public boolean showPaintSelection(ToolEvent e) { return e.isInPaintMode(); }
	public boolean showDrawSelection(ToolEvent e, Layer l) { return false; }
}
