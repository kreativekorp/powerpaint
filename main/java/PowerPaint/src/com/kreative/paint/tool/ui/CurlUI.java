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

package com.kreative.paint.tool.ui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.form.BooleanOption;
import com.kreative.paint.form.DoubleOption;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.FormUI;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.tool.ToolUtilities;

public class CurlUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private ToolContext tc;
	private FormUI fui;
	
	public CurlUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		Form f = new Form();
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("options.Curl.Radius"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return 1; }
			public double getStep() { return 1; }
			public double getValue() { return CurlUI.this.tc.getCurlRadius(); }
			public void setValue(double v) { CurlUI.this.tc.setCurlRadius(v); }
		});
		f.add(new DoubleOption() {
			public String getName() { return ToolUtilities.messages.getString("options.Curl.Spacing"); }
			public double getMaximum() { return Double.MAX_VALUE; }
			public double getMinimum() { return 1; }
			public double getStep() { return 1; }
			public double getValue() { return CurlUI.this.tc.getCurlSpacing(); }
			public void setValue(double v) { CurlUI.this.tc.setCurlSpacing(v); }
		});
		f.add(new BooleanOption() {
			public String getName() { return ToolUtilities.messages.getString("options.Curl.Rotation"); }
			public boolean getValue() { return CurlUI.this.tc.getCurlCCW(); }
			public void setValue(boolean v) { CurlUI.this.tc.setCurlCCW(v); }
			public boolean useTrueFalseLabels() { return true; }
			public String getLabel(boolean v) { return ToolUtilities.messages.getString(v ? "options.Curl.Rotation.CCW" : "options.Curl.Rotation.CW"); }
		});
		setLayout(new GridLayout(1,1));
		add(fui = new FormUI(f, mini));
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_CURL) != 0L) {
			fui.update();
		}
	}
}
