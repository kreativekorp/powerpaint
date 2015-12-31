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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.kreative.paint.document.draw.DrawObject;
import com.kreative.paint.document.draw.GroupDrawObject;

public class DrawObjectSelection implements ClipboardOwner, Transferable {
	// I has a flavor.
	public static final DataFlavor drawObjectFlavor = new DataFlavor(GroupDrawObject.class, "Draw Objects");
	
	private GroupDrawObject myObjects;
	
	public DrawObjectSelection(DrawObject o) {
		List<DrawObject> objects = new ArrayList<DrawObject>();
		objects.add(o.clone());
		myObjects = new GroupDrawObject(objects);
	}
	
	public DrawObjectSelection(DrawObject[] o) {
		List<DrawObject> objects = new ArrayList<DrawObject>();
		for (DrawObject d : o) objects.add(d.clone());
		myObjects = new GroupDrawObject(objects);
	}
	
	public DrawObjectSelection(Collection<? extends DrawObject> o) {
		List<DrawObject> objects = new ArrayList<DrawObject>();
		for (DrawObject d : o) objects.add(d.clone());
		myObjects = new GroupDrawObject(objects);
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
