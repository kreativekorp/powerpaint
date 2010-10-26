/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

public class HistoryEvent {
	public static final int TRANSACTION_LIMIT_CHANGED = 1;
	public static final int TRANSACTION_BEGAN = 2;
	public static final int TRANSACTION_CONTINUED = 3;
	public static final int TRANSACTION_RENAMED = 4;
	public static final int TRANSACTION_COMMITTED = 5;
	public static final int TRANSACTION_ROLLEDBACK = 6;
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
