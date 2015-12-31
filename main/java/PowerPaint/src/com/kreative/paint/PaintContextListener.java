package com.kreative.paint;

import com.kreative.paint.document.draw.PaintSettings;

public interface PaintContextListener extends PaintContextConstants {
	public void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta);
	public void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd);
}
