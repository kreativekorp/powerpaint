package com.kreative.paint.document.draw;

import java.awt.Graphics2D;
import java.util.List;

public interface DrawSurface extends List<DrawObject> {
	public boolean hasSelection();
	public int getFirstSelectedIndex();
	public int getLastSelectedIndex();
	public List<DrawObject> getSelection();
	public Graphics2D createDrawGraphics();
}
