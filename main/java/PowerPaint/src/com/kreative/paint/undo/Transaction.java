/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

import java.util.LinkedList;
import java.util.ListIterator;

public class Transaction implements Undoable {
	private String name;
	private LinkedList<Atom> atoms;
	
	public Transaction(String name) {
		this.name = name;
		this.atoms = new LinkedList<Atom>();
	}
	
	public synchronized void add(Atom a) {
		if (atoms.size() > 0 && a.canBuildUpon(atoms.getLast())) {
			a = a.buildUpon(atoms.getLast());
			atoms.removeLast();
		}
		atoms.add(a);
	}
	
	public synchronized boolean isEmpty() {
		return atoms.isEmpty();
	}
	
	public synchronized String getName() {
		return name;
	}
	
	public synchronized void setName(String name) {
		this.name = name;
	}
	
	public synchronized void redo() {
		ListIterator<Atom> i = atoms.listIterator();
		while (i.hasNext()) {
			i.next().redo();
		}
	}

	public synchronized void undo() {
		ListIterator<Atom> i = atoms.listIterator(atoms.size());
		while (i.hasPrevious()) {
			i.previous().undo();
		}
	}
}
