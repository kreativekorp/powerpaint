package com.kreative.paint.document.draw;

public interface DrawObjectListener {
	public void drawObjectPaintSettingsChanged(DrawObjectEvent e);
	public void drawObjectTransformChanged(DrawObjectEvent e);
	public void drawObjectVisibleChanged(DrawObjectEvent e);
	public void drawObjectLockedChanged(DrawObjectEvent e);
	public void drawObjectSelectedChanged(DrawObjectEvent e);
	public void drawObjectControlPointChanged(DrawObjectEvent e);
	public void drawObjectLocationChanged(DrawObjectEvent e);
	public void drawObjectImplementationPropertyChanged(DrawObjectEvent e);
}
