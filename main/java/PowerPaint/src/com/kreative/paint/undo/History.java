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

package com.kreative.paint.undo;

import java.util.ArrayList;
import java.util.List;

public class History implements Undoable {
	private int maxTransactions = 30;
	private List<Transaction> transactions = new ArrayList<Transaction>();
	private int transactionIndex = 0;
	private Transaction inProgress = null;
	private List<HistoryListener> listeners = new ArrayList<HistoryListener>();
	
	public void addHistoryListener(HistoryListener l) {
		listeners.add(l);
	}
	
	public void removeHistoryListener(HistoryListener l) {
		listeners.remove(l);
	}
	
	public HistoryListener[] getHistoryListeners() {
		return listeners.toArray(new HistoryListener[0]);
	}
	
	protected void notifyHistoryListeners(HistoryEvent e) {
		switch (e.getID()) {
		case HistoryEvent.TRANSACTION_LIMIT_CHANGED: for (HistoryListener l : listeners) l.transactionLimitChanged(e); break;
		case HistoryEvent.TRANSACTION_BEGAN: for (HistoryListener l : listeners) l.transactionBegan(e); break;
		case HistoryEvent.TRANSACTION_CONTINUED: for (HistoryListener l : listeners) l.transactionContinued(e); break;
		case HistoryEvent.TRANSACTION_RENAMED: for (HistoryListener l : listeners) l.transactionRenamed(e); break;
		case HistoryEvent.TRANSACTION_COMMITTED: for (HistoryListener l : listeners) l.transactionCommitted(e); break;
		case HistoryEvent.TRANSACTION_ROLLEDBACK: for (HistoryListener l : listeners) l.transactionRolledback(e); break;
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
			Transaction t = new Transaction(null);
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_BEGAN, this, t, a));
			t.add(a);
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_CONTINUED, this, t, a));
			transactions.subList(transactionIndex, transactions.size()).clear();
			transactions.add(t);
			transactionIndex++;
			if (maxTransactions > 0) {
				while (transactions.size() > maxTransactions) {
					transactions.remove(0);
					transactionIndex--;
				}
			}
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_COMMITTED, this, t, a));
		} else {
			inProgress.add(a);
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_CONTINUED, this, inProgress, a));
		}
	}
	
	public synchronized void begin(String name) {
		if (inProgress != null) {
			System.err.println("Warning: Transaction \""+(name==null?"":name)+"\" began with another Transaction (\""+(inProgress.getName()==null?"":inProgress.getName())+"\") already in progress.");
			if (!inProgress.isEmpty()) {
				transactions.subList(transactionIndex, transactions.size()).clear();
				transactions.add(inProgress);
				transactionIndex++;
				if (maxTransactions > 0) {
					while (transactions.size() > maxTransactions) {
						transactions.remove(0);
						transactionIndex--;
					}
				}
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
				transactions.subList(transactionIndex, transactions.size()).clear();
				transactions.add(inProgress);
				transactionIndex++;
				if (maxTransactions > 0) {
					while (transactions.size() > maxTransactions) {
						transactions.remove(0);
						transactionIndex--;
					}
				}
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
				notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_ROLLEDBACK, this, inProgress, null));
			}
			inProgress = null;
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
			System.err.println("Warning: Redo called with a Transaction (\""+(inProgress.getName()==null?"":inProgress.getName())+"\") in progress.");
		} else if (transactionIndex < transactions.size()) {
			transactions.get(transactionIndex).redo();
			transactionIndex++;
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_REDONE, this, transactions.get(transactionIndex-1), null));
		}
	}
	
	public synchronized boolean canUndo() {
		return (inProgress == null && transactionIndex > 0);
	}
	
	public synchronized String getUndoName() {
		if (inProgress == null && transactionIndex > 0) {
			return transactions.get(transactionIndex-1).getName();
		} else {
			return null;
		}
	}

	public synchronized void undo() {
		if (inProgress != null) {
			System.err.println("Warning: Undo called with a Transaction (\""+(inProgress.getName()==null?"":inProgress.getName())+"\") in progress.");
		} else if (transactionIndex > 0) {
			transactionIndex--;
			transactions.get(transactionIndex).undo();
			notifyHistoryListeners(new HistoryEvent(HistoryEvent.TRANSACTION_UNDONE, this, transactions.get(transactionIndex), null));
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
			System.err.println("Warning: SetTransactionIndex called with a Transaction (\""+(inProgress.getName()==null?"":inProgress.getName())+"\") in progress.");
		} else if (i < 0 || i > transactions.size()) {
			throw new IllegalArgumentException("transactionIndex = "+i);
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
