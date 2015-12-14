package com.kreative.paint.document.undo;

public interface HistoryListener {
	public void transactionLimitChanged(HistoryEvent e);
	public void transactionBegan(HistoryEvent e);
	public void transactionContinued(HistoryEvent e);
	public void transactionRenamed(HistoryEvent e);
	public void transactionCommitted(HistoryEvent e);
	public void transactionRolledBack(HistoryEvent e);
	public void transactionRedone(HistoryEvent e);
	public void transactionUndone(HistoryEvent e);
}
