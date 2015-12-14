package com.kreative.paint.document.undo;

public interface Atom extends Undoable {
	public boolean canBuildUpon(Atom previousAtom);
	public Atom buildUpon(Atom previousAtom);
}
