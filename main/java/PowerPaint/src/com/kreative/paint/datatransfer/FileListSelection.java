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

package com.kreative.paint.datatransfer;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

public class FileListSelection implements ClipboardOwner, Transferable {
	private java.util.List<File> myList;
	private boolean isCut;
	
	public FileListSelection(File f) {
		myList = new Vector<File>();
		myList.add(f);
		isCut = false;
	}
	
	public FileListSelection(File[] f) {
		myList = new Vector<File>();
		myList.addAll(Arrays.asList(f));
		isCut = false;
	}
	
	public FileListSelection(Collection<File> f) {
		myList = new Vector<File>();
		myList.addAll(f);
		isCut = false;
	}
	
	public FileListSelection(File f, boolean cut) {
		myList = new Vector<File>();
		myList.add(f);
		isCut = cut;
	}
	
	public FileListSelection(File[] f, boolean cut) {
		myList = new Vector<File>();
		myList.addAll(Arrays.asList(f));
		isCut = cut;
	}
	
	public FileListSelection(Collection<File> f, boolean cut) {
		myList = new Vector<File>();
		myList.addAll(f);
		isCut = cut;
	}
	
	public boolean isCutOperation() {
		return isCut;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (DataFlavor.javaFileListFlavor.equals(flavor)) {
			return myList;
		} else if (DataFlavor.stringFlavor.equals(flavor)) {
			String s = "";
			for (File f : myList) {
				s += "\n"+f.getAbsolutePath();
			}
			return ((s.length() > 0) ? s.substring(1) : s);
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (DataFlavor.javaFileListFlavor.equals(flavor) || DataFlavor.stringFlavor.equals(flavor));
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		//nothing
	}
}
