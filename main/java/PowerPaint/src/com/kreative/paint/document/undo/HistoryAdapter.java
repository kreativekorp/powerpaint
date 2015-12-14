package com.kreative.paint.document.undo;

public class HistoryAdapter implements HistoryListener {
	@Override public void transactionLimitChanged(HistoryEvent e) {}
	@Override public void transactionBegan(HistoryEvent e) {}
	@Override public void transactionContinued(HistoryEvent e) {}
	@Override public void transactionRenamed(HistoryEvent e) {}
	@Override public void transactionCommitted(HistoryEvent e) {}
	@Override public void transactionRolledBack(HistoryEvent e) {}
	@Override public void transactionRedone(HistoryEvent e) {}
	@Override public void transactionUndone(HistoryEvent e) {}
}
