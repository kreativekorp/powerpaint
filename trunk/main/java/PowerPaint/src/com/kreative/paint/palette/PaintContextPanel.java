/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.paint.palette;

import javax.swing.JPanel;
import com.kreative.paint.PaintContext;
import com.kreative.paint.PaintContextConstants;
import com.kreative.paint.PaintContextListener;
import com.kreative.paint.PaintSettings;

public abstract class PaintContextPanel extends JPanel implements PaintContextListener, PaintContextConstants {
	private static final long serialVersionUID = 1L;
	protected PaintContext pc;
	protected int eventMask;
	
	public PaintContextPanel(PaintContext pc, int eventMask) {
		pc.addPaintContextListener(this);
		this.pc = pc;
		this.eventMask = eventMask;
	}
	
	public final PaintContext getPaintContext() {
		return pc;
	}
	
	public final void setPaintContext(PaintContext pc) {
		this.pc.removePaintContextListener(this);
		pc.addPaintContextListener(this);
		this.pc = pc;
		pcChanged(pc);
	}
	
	protected void pcChanged(PaintContext pc) {}
	
	public final void paintSettingsChanged(PaintContext src, PaintSettings ps, int delta) {
		if ((eventMask & delta) != 0) update();
	}

	public final void editingChanged(PaintContext src, boolean editingStroke, boolean editingBkgnd) {
		if ((eventMask & CHANGED_EDITING) != 0) update();
	}
	
	protected abstract void update();
}
