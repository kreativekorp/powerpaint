package com.kreative.paint.document.undo;

public interface Recordable {
	public History getHistory();
	public void setHistory(History history);
}
