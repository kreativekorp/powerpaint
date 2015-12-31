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
import com.kreative.paint.document.draw.DrawObject;

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
