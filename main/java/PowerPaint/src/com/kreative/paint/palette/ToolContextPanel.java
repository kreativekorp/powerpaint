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
 * @since PowerTool 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.paint.palette;

import javax.swing.JPanel;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.tool.Tool;

public abstract class ToolContextPanel extends JPanel implements ToolContextListener, ToolContextConstants {
	private static final long serialVersionUID = 1L;
	protected ToolContext tc;
	protected long eventMask;
	
	public ToolContextPanel(ToolContext tc, long eventMask) {
		tc.addToolContextListener(this);
		this.tc = tc;
		this.eventMask = eventMask;
	}
	
	public final ToolContext getToolContext() {
		return tc;
	}
	
	public final void setToolContext(ToolContext pc) {
		this.tc.removeToolContextListener(this);
		pc.addToolContextListener(this);
		this.tc = pc;
		pcChanged(pc);
	}
	
	protected void pcChanged(ToolContext pc) {}
	
	public final void modeChanged(ToolContext src, boolean drawMode) {
		if ((eventMask & CHANGED_MODE) != 0) update();
	}

	public final void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {
		if ((eventMask & CHANGED_TOOL) != 0) update();
	}
	
	public final void toolDoubleClicked(ToolContext src, Tool tool) {
		if ((eventMask & DOUBLE_CLICKED_TOOL) != 0) update();
	}

	public final void toolSettingsChanged(ToolContext src, long delta) {
		if ((eventMask & delta) != 0L) update();
	}
	
	protected abstract void update();
}
