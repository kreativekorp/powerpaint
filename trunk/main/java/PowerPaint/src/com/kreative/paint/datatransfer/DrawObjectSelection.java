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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import com.kreative.paint.draw.DrawObject;
import com.kreative.paint.draw.GroupDrawObject;

public class DrawObjectSelection implements ClipboardOwner, Transferable {
	// I has a flavor.
	public static final DataFlavor drawObjectFlavor = new DataFlavor(GroupDrawObject.class, "Draw Objects");
	
	private GroupDrawObject myObjects;
	
	public DrawObjectSelection(DrawObject o) {
		myObjects = new GroupDrawObject();
		myObjects.add(o.clone());
	}
	
	public DrawObjectSelection(DrawObject[] o) {
		myObjects = new GroupDrawObject();
		for (DrawObject d : o) myObjects.add(d.clone());
	}
	
	public DrawObjectSelection(Collection<? extends DrawObject> o) {
		myObjects = new GroupDrawObject();
		for (DrawObject d : o) myObjects.add(d.clone());
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (drawObjectFlavor.equals(flavor)) {
			return myObjects;
		} else if (DataFlavor.imageFlavor.equals(flavor)) {
			Rectangle b = myObjects.getBounds();
			BufferedImage bi = new BufferedImage(b.width, b.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			myObjects.paint(g, -b.x, -b.y);
			g.dispose();
			return bi;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{drawObjectFlavor, DataFlavor.imageFlavor};
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (drawObjectFlavor.equals(flavor) || DataFlavor.imageFlavor.equals(flavor));
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// nothing
	}
}
