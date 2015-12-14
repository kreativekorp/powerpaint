package com.kreative.paint.document.undo;

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
		if (!atoms.isEmpty()) {
			Atom b = atoms.getLast();
			if (a.canBuildUpon(b)) {
				a = a.buildUpon(b);
				atoms.removeLast();
			}
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
		while (i.hasNext()) i.next().redo();
	}
	
	public synchronized void undo() {
		ListIterator<Atom> i = atoms.listIterator(atoms.size());
		while (i.hasPrevious()) i.previous().undo();
	}
}
