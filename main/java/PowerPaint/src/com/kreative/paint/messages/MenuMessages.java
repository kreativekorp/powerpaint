/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.messages;

import java.awt.event.KeyEvent;
import java.util.ListResourceBundle;
import javax.swing.KeyStroke;
import com.kreative.paint.util.InputUtils;

public class MenuMessages extends ListResourceBundle {
	private Object[][] contents = {
			// names, accelerators (which Java calls mnemonics),
			// and shortcuts (which Java calls accelerators)
			// for menus and menu items
			{ "File", "File" },
			{ "File.a", KeyEvent.VK_F },
			{ "File.New", "New..." },
			{ "File.New.a", KeyEvent.VK_N },
			{ "File.New.s", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputUtils.META_MASK) },
			{ "File.NewFromClipboard", "New From Clipboard" },
			{ "File.NewFromClipboard.a", KeyEvent.VK_C },
			{ "File.NewFromClipboard.s", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputUtils.META_SHIFT_MASK) },
			{ "File.Open", "Open..." },
			{ "File.Open.a", KeyEvent.VK_O },
			{ "File.Open.s", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputUtils.META_MASK) },
			{ "File.Close", "Close Window" },
			{ "File.Close.a", KeyEvent.VK_W },
			{ "File.Close.s", KeyStroke.getKeyStroke(KeyEvent.VK_W, InputUtils.META_MASK) },
			{ "File.Import", "Import..." },
			{ "File.Import.a", KeyEvent.VK_I },
			{ "File.Import.s", false },
			{ "File.Export", "Export..." },
			{ "File.Export.a", KeyEvent.VK_E },
			{ "File.Export.s", false },
			{ "File.Save", "Save" },
			{ "File.Save.a", KeyEvent.VK_S },
			{ "File.Save.s", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputUtils.META_MASK) },
			{ "File.SaveAs", "Save As..." },
			{ "File.SaveAs.a", KeyEvent.VK_A },
			{ "File.SaveAs.s", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputUtils.META_SHIFT_MASK) },
			{ "File.Revert", "Revert" },
			{ "File.Revert.a", KeyEvent.VK_R },
			{ "File.Revert.s", false },
			{ "File.PageSetup", "Page Setup..." },
			{ "File.PageSetup.a", KeyEvent.VK_U },
			{ "File.PageSetup.s", KeyStroke.getKeyStroke(KeyEvent.VK_P, InputUtils.META_SHIFT_MASK) },
			{ "File.Print", "Print..." },
			{ "File.Print.a", KeyEvent.VK_P },
			{ "File.Print.s", KeyStroke.getKeyStroke(KeyEvent.VK_P, InputUtils.META_MASK) },
			{ "File.Quit", "Quit" },
			{ "File.Quit.a", KeyEvent.VK_Q },
			{ "File.Quit.s", KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputUtils.META_MASK) },
			{ "File.Exit", "Exit" },
			{ "File.Exit.a", KeyEvent.VK_X },
			{ "File.Exit.s", KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputUtils.META_MASK) },
			{ "Edit", "Edit" },
			{ "Edit.a", KeyEvent.VK_E },
			{ "Edit.Undo", "Undo" },
			{ "Edit.UndoAction", "Undo $" },
			{ "Edit.Undo.a", KeyEvent.VK_U },
			{ "Edit.Undo.s", KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputUtils.META_MASK) },
			{ "Edit.Redo", "Redo" },
			{ "Edit.RedoAction", "Redo $" },
			{ "Edit.Redo.a", KeyEvent.VK_R },
			{ "Edit.Redo.s", KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputUtils.META_SHIFT_MASK) },
			{ "Edit.Cut", "Cut" },
			{ "Edit.Cut.a", KeyEvent.VK_T },
			{ "Edit.Cut.s", KeyStroke.getKeyStroke(KeyEvent.VK_X, InputUtils.META_MASK) },
			{ "Edit.Copy", "Copy" },
			{ "Edit.Copy.a", KeyEvent.VK_C },
			{ "Edit.Copy.s", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputUtils.META_MASK) },
			{ "Edit.Paste", "Paste" },
			{ "Edit.Paste.a", KeyEvent.VK_P },
			{ "Edit.Paste.s", KeyStroke.getKeyStroke(KeyEvent.VK_V, InputUtils.META_MASK) },
			{ "Edit.Clear", "Clear" },
			{ "Edit.Clear.a", KeyEvent.VK_E },
			{ "Edit.Clear.s", false },
			{ "Edit.Duplicate", "Duplicate" },
			{ "Edit.Duplicate.a", KeyEvent.VK_D },
			{ "Edit.Duplicate.s", KeyStroke.getKeyStroke(KeyEvent.VK_D, InputUtils.META_SHIFT_MASK) },
			{ "Edit.SelectAll", "Select All" },
			{ "Edit.SelectAll.a", KeyEvent.VK_A },
			{ "Edit.SelectAll.s", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputUtils.META_MASK) },
			{ "Edit.DeselectAll", "Deselect All" },
			{ "Edit.DeselectAll.a", KeyEvent.VK_S },
			{ "Edit.DeselectAll.s", KeyStroke.getKeyStroke(KeyEvent.VK_D, InputUtils.META_MASK) },
			{ "Edit.InvertSelection", "Invert Selection" },
			{ "Edit.InvertSelection.a", KeyEvent.VK_I },
			{ "Edit.InvertSelection.s", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputUtils.META_SHIFT_MASK) },
			{ "View", "View" },
			{ "View.a", KeyEvent.VK_V },
			{ "View.Close", "Close Window" },
			{ "View.Close.a", KeyEvent.VK_W },
			{ "View.Close.s", KeyStroke.getKeyStroke(KeyEvent.VK_W, InputUtils.META_MASK) },
			{ "View.Minimize", "Minimize Window" },
			{ "View.Minimize.a", KeyEvent.VK_M },
			{ "View.Minimize.s", KeyStroke.getKeyStroke(KeyEvent.VK_M, InputUtils.META_MASK) },
			{ "View.Zoom", "Zoom Window" },
			{ "View.Zoom.a", KeyEvent.VK_Z },
			{ "View.Zoom.s", KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, InputUtils.META_MASK) },
			{ "View.ActualSize", "Actual Size" },
			{ "View.ActualSize.a", KeyEvent.VK_A },
			{ "View.ActualSize.s", KeyStroke.getKeyStroke(KeyEvent.VK_0, InputUtils.META_MASK) },
			{ "View.ZoomIn", "Zoom In" },
			{ "View.ZoomIn.a", KeyEvent.VK_I },
			{ "View.ZoomIn.s", KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputUtils.META_MASK) },
			{ "View.ZoomOut", "Zoom Out" },
			{ "View.ZoomOut.a", KeyEvent.VK_O },
			{ "View.ZoomOut.s", KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputUtils.META_MASK) },
			{ "Draw", "Draw" },
			{ "Draw.a", KeyEvent.VK_D },
			{ "Draw.Group", "Group" },
			{ "Draw.Group.a", KeyEvent.VK_G },
			{ "Draw.Group.s", KeyStroke.getKeyStroke(KeyEvent.VK_G, InputUtils.META_MASK) },
			{ "Draw.Ungroup", "Ungroup" },
			{ "Draw.Ungroup.a", KeyEvent.VK_U },
			{ "Draw.Ungroup.s", KeyStroke.getKeyStroke(KeyEvent.VK_G, InputUtils.META_SHIFT_MASK) },
			{ "Draw.BreakApart", "Break Apart" },
			{ "Draw.BreakApart.a", KeyEvent.VK_B },
			{ "Draw.BreakApart.s", KeyStroke.getKeyStroke(KeyEvent.VK_B, InputUtils.META_SHIFT_MASK) },
			{ "Draw.Lock", "Lock" },
			{ "Draw.Lock.a", KeyEvent.VK_L },
			{ "Draw.Lock.s", KeyStroke.getKeyStroke(KeyEvent.VK_L, InputUtils.META_MASK) },
			{ "Draw.Unlock", "Unlock" },
			{ "Draw.Unlock.a", KeyEvent.VK_N },
			{ "Draw.Unlock.s", KeyStroke.getKeyStroke(KeyEvent.VK_L, InputUtils.META_SHIFT_MASK) },
			{ "Draw.Hide", "Hide" },
			{ "Draw.Hide.a", KeyEvent.VK_H },
			{ "Draw.Hide.s", KeyStroke.getKeyStroke(KeyEvent.VK_J, InputUtils.META_MASK) },
			{ "Draw.Unhide", "Unhide" },
			{ "Draw.Unhide.a", KeyEvent.VK_I },
			{ "Draw.Unhide.s", KeyStroke.getKeyStroke(KeyEvent.VK_J, InputUtils.META_SHIFT_MASK) },
			{ "Draw.SendToBack", "Send to Back" },
			{ "Draw.SendToBack.a", KeyEvent.VK_S },
			{ "Draw.SendToBack.s", KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, InputUtils.META_SHIFT_MASK) },
			{ "Draw.SendBackward", "Send Backward" },
			{ "Draw.SendBackward.a", KeyEvent.VK_K },
			{ "Draw.SendBackward.s", KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, InputUtils.META_MASK) },
			{ "Draw.BringForward", "Bring Forward" },
			{ "Draw.BringForward.a", KeyEvent.VK_W },
			{ "Draw.BringForward.s", KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, InputUtils.META_MASK) },
			{ "Draw.BringToFront", "Bring to Front" },
			{ "Draw.BringToFront.a", KeyEvent.VK_F },
			{ "Draw.BringToFront.s", KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, InputUtils.META_SHIFT_MASK) },
			{ "Filter", "Filter" },
			{ "Filter.a", KeyEvent.VK_L },
			{ "Filter.Last", "Apply Last Filter Again" },
			{ "Filter.LastAction", "Apply $ Again" },
			{ "Filter.Last.a", KeyEvent.VK_A },
			{ "Filter.Last.s", KeyStroke.getKeyStroke(KeyEvent.VK_F, InputUtils.META_MASK) },
			{ "Help", "Help" },
			{ "Help.a", KeyEvent.VK_H },
			{ "Help.Contents", "Contents" },
			{ "Help.Contents.a", KeyEvent.VK_C },
			{ "Help.Contents.s", KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, InputUtils.META_SHIFT_MASK) },
			{ "Help.About", "About Kreative PowerPaint" },
			{ "Help.About.a", KeyEvent.VK_A },
			{ "Help.About.s", false },
	};
	
	protected Object[][] getContents() {
		return contents;
	}
}
