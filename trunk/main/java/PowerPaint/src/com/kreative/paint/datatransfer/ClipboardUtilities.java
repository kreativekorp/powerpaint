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

package com.kreative.paint.datatransfer;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Collection;
import java.util.Vector;
import com.kreative.paint.draw.DrawObject;

public class ClipboardUtilities implements ClipboardOwner {
	private static final ClipboardUtilities self = new ClipboardUtilities();
	private ClipboardUtilities() {}
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	
	public static void setClipboard(Transferable t) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(t, self);
	}
	
	public static void setClipboardString(String s) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new StringSelection(s), self);
	}
	
	public static void setClipboardImage(Image i) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new ImageSelection(i), self);
	}
	
	public static void setClipboardFile(File f) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new FileListSelection(f), self);
	}
	
	public static void setClipboardFiles(Collection<File> f) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new FileListSelection(f), self);
	}
	
	public static void setClipboardDrawObject(DrawObject d) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new DrawObjectSelection(d), self);
	}
	
	public static void setClipboardDrawObjects(Collection<? extends DrawObject> d) {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(new DrawObjectSelection(d), self);
	}
	
	public static boolean clipboardHasString() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		return (cb.isDataFlavorAvailable(DataFlavor.stringFlavor));
	}
	
	public static boolean clipboardHasImage() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		return (cb.isDataFlavorAvailable(DataFlavor.imageFlavor));
	}
	
	public static boolean clipboardHasFiles() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		return (cb.isDataFlavorAvailable(DataFlavor.javaFileListFlavor));
	}
	
	public static boolean clipboardHasDrawObjects() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		return (cb.isDataFlavorAvailable(DrawObjectSelection.drawObjectFlavor));
	}
	
	public static Transferable getClipboard() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		return cb.getContents(null);
	}
	
	public static String getClipboardString() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (cb.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			try {
				return (String)cb.getData(DataFlavor.stringFlavor);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static Image getClipboardImage() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (cb.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
			try {
				return (Image)cb.getData(DataFlavor.imageFlavor);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static Collection<File> getClipboardFiles() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (cb.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)) {
			try {
				Collection<File> v = new Vector<File>();
				Collection<?> c = (Collection<?>)cb.getData(DataFlavor.javaFileListFlavor);
				for (Object o : c) v.add((File)o);
				return v;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static Collection<DrawObject> getClipboardDrawObjects() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (cb.isDataFlavorAvailable(DrawObjectSelection.drawObjectFlavor)) {
			try {
				Collection<DrawObject> v = new Vector<DrawObject>();
				Collection<?> c = (Collection<?>)cb.getData(DrawObjectSelection.drawObjectFlavor);
				for (Object o : c) v.add((DrawObject)o);
				return v;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
}
