package com.kreative.paint.document.undo;

public class HistoryEvent {
	public static final int TRANSACTION_LIMIT_CHANGED = 1;
	public static final int TRANSACTION_BEGAN = 2;
	public static final int TRANSACTION_CONTINUED = 3;
	public static final int TRANSACTION_RENAMED = 4;
	public static final int TRANSACTION_COMMITTED = 5;
	public static final int TRANSACTION_ROLLED_BACK = 6;
	public static final int TRANSACTION_REDONE = 7;
	public static final int TRANSACTION_UNDONE = 8;
	
	private int id;
	private History history;
	private Transaction transaction;
	private Atom atom;
	
	public HistoryEvent(int id, History history, Transaction transaction, Atom atom) {
		this.id = id;
		this.history = history;
		this.transaction = transaction;
		this.atom = atom;
	}
	
	public int getID() {
		return id;
	}
	
	public History getHistory() {
		return history;
	}
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	public Atom getAtom() {
		return atom;
	}
}
