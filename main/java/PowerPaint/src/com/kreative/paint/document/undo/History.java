package com.kreative.paint.document.undo;

import java.util.ArrayList;
import java.util.List;

public class History implements Undoable {
	private int maxTransactions;
	private List<Transaction> transactions;
	private int transactionIndex;
	private Transaction inProgress;
	private List<HistoryListener> listeners;
	
	public History() {
		this.maxTransactions = 30;
		this.transactions = new ArrayList<Transaction>();
		this.transactionIndex = 0;
		this.inProgress = null;
		this.listeners = new ArrayList<HistoryListener>();
	}
	
	public void addHistoryListener(HistoryListener l) {
		listeners.add(l);
	}
	
	public void removeHistoryListener(HistoryListener l) {
		listeners.remove(l);
	}
	
	public HistoryListener[] getHistoryListeners() {
		return listeners.toArray(new HistoryListener[listeners.size()]);
	}
	
	protected void notifyHistoryListeners(HistoryEvent e) {
		switch (e.getID()) {
			case HistoryEvent.TRANSACTION_LIMIT_CHANGED: for (HistoryListener l : listeners) l.transactionLimitChanged(e); break;
			case HistoryEvent.TRANSACTION_BEGAN: for (HistoryListener l : listeners) l.transactionBegan(e); break;
			case HistoryEvent.TRANSACTION_CONTINUED: for (HistoryListener l : listeners) l.transactionContinued(e); break;
			case HistoryEvent.TRANSACTION_RENAMED: for (HistoryListener l : listeners) l.transactionRenamed(e); break;
			case HistoryEvent.TRANSACTION_COMMITTED: for (HistoryListener l : listeners) l.transactionCommitted(e); break;
			case HistoryEvent.TRANSACTION_ROLLED_BACK: for (HistoryListener l : listeners) l.transactionRolledBack(e); break;
			case HistoryEvent.TRANSACTION_REDONE: for (HistoryListener l : listeners) l.transactionRedone(e); break;
			case HistoryEvent.TRANSACTION_UNDONE: for (HistoryListener l : listeners) l.transactionUndone(e); break;
		}
	}
	
	public synchronized int getMaxTransactions() {
		return maxTransactions;
	}
	
	public synchronized void setMaxTransactions(int maxTransactions) {
		this.maxTransactions = maxTransactions;
		notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_LIMIT_CHANGED, this, null, null));
	}
	
	public synchronized void add(Atom a) {
		if (inProgress == null) {
			System.err.println("Warning: Atomic operation performed with no Transaction in progress.");
			inProgress = new Transaction(null);
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_BEGAN, this, inProgress, a));
		}
		inProgress.add(a);
		notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_CONTINUED, this, inProgress, a));
	}
	
	public synchronized void begin(String name) {
		if (inProgress != null) {
			String thisName = (name == null) ? "" : name;
			String thatName = (inProgress.getName() == null) ? "" : inProgress.getName();
			System.err.println("Warning: Transaction \"" + thisName + "\" began with another Transaction (\"" + thatName + "\") already in progress.");
			if (!inProgress.isEmpty()) {
				commit(inProgress);
				notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_COMMITTED, this, inProgress, null));
			}
		}
		inProgress = new Transaction(name);
		notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_BEGAN, this, inProgress, null));
	}
	
	public synchronized void rename(String name) {
		if (inProgress == null) {
			System.err.println("Warning: Transaction renamed with no Transaction in progress.");
		} else {
			inProgress.setName(name);
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_RENAMED, this, inProgress, null));
		}
	}
	
	public synchronized void commit() {
		if (inProgress == null) {
			System.err.println("Warning: Transaction committed with no Transaction in progress.");
		} else {
			if (!inProgress.isEmpty()) {
				commit(inProgress);
				notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_COMMITTED, this, inProgress, null));
			}
			inProgress = null;
		}
	}
	
	public synchronized void rollback() {
		if (inProgress == null) {
			System.err.println("Warning: Transaction rolled back with no Transaction in progress.");
		} else {
			if (!inProgress.isEmpty()) {
				inProgress.undo();
				notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_ROLLED_BACK, this, inProgress, null));
			}
			inProgress = null;
		}
	}
	
	private void commit(Transaction t) {
		transactions.subList(transactionIndex, transactions.size()).clear();
		transactions.add(t);
		transactionIndex++;
		if (maxTransactions > 0) {
			while (transactions.size() > maxTransactions) {
				transactions.remove(0);
				transactionIndex--;
			}
		}
	}
	
	public synchronized boolean isInProgress() {
		return (inProgress != null);
	}
	
	public synchronized boolean canRedo() {
		return (inProgress == null && transactionIndex < transactions.size());
	}
	
	public synchronized String getRedoName() {
		if (inProgress == null && transactionIndex < transactions.size()) {
			return transactions.get(transactionIndex).getName();
		} else {
			return null;
		}
	}
	
	public synchronized void redo() {
		if (inProgress != null) {
			String thatName = (inProgress.getName() == null) ? "" : inProgress.getName();
			System.err.println("Warning: Redo called with a Transaction (\"" + thatName + "\") in progress.");
		} else if (transactionIndex < transactions.size()) {
			Transaction t = transactions.get(transactionIndex);
			t.redo();
			transactionIndex++;
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_REDONE, this, t, null));
		}
	}
	
	public synchronized boolean canUndo() {
		return (inProgress == null && transactionIndex > 0);
	}
	
	public synchronized String getUndoName() {
		if (inProgress == null && transactionIndex > 0) {
			return transactions.get(transactionIndex - 1).getName();
		} else {
			return null;
		}
	}

	public synchronized void undo() {
		if (inProgress != null) {
			String thatName = (inProgress.getName() == null) ? "" : inProgress.getName();
			System.err.println("Warning: Undo called with a Transaction (\"" + thatName + "\") in progress.");
		} else if (transactionIndex > 0) {
			transactionIndex--;
			Transaction t = transactions.get(transactionIndex);
			t.undo();
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_UNDONE, this, t, null));
		}
	}
	
	public synchronized int getTransactionCount() {
		return transactions.size();
	}
	
	public synchronized String getTransactionName(int i) {
		return transactions.get(i).getName();
	}
	
	public synchronized int getTransactionIndex() {
		return transactionIndex;
	}
	
	public synchronized void setTransactionIndex(int i) {
		if (inProgress != null) {
			String thatName = (inProgress.getName() == null) ? "" : inProgress.getName();
			System.err.println("Warning: SetTransactionIndex called with a Transaction (\"" + thatName + "\") in progress.");
		} else if (i < 0 || i > transactions.size()) {
			throw new IllegalArgumentException("transactionIndex = " + i);
		} else if (transactionIndex > i) {
			while (transactionIndex > i) {
				undo();
			}
		} else if (transactionIndex < i) {
			while (transactionIndex < i) {
				redo();
			}
		}
	}
}
