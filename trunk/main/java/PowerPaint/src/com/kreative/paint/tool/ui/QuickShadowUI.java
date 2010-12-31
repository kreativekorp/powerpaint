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

package com.kreative.paint.tool.ui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import com.kreative.paint.ToolContext;
import com.kreative.paint.ToolContextConstants;
import com.kreative.paint.ToolContextListener;
import com.kreative.paint.draw.QuickShadowDrawObject;
import com.kreative.paint.form.Form;
import com.kreative.paint.form.FormUI;
import com.kreative.paint.form.IntegerEnumOption;
import com.kreative.paint.form.IntegerOption;
import com.kreative.paint.tool.Tool;
import com.kreative.paint.tool.ToolUtilities;

public class QuickShadowUI extends JPanel implements ToolContextListener {
	private static final long serialVersionUID = 1L;
	private ToolContext tc;
	private FormUI fui;
	
	public QuickShadowUI(ToolContext tc, boolean mini) {
		this.tc = tc;
		Form f = new Form();
		f.add(new IntegerEnumOption() {
			public String getName() {
				return ToolUtilities.messages.getString("options.ShadowType");
			}
			public int getValue() {
				return QuickShadowUI.this.tc.getShadowType();
			}
			public void setValue(int v) {
				QuickShadowUI.this.tc.setShadowType(v);
			}
			public int[] values() {
				return new int[]{
						QuickShadowDrawObject.SHADOW_TYPE_NONE,
						QuickShadowDrawObject.SHADOW_TYPE_STROKE,
						QuickShadowDrawObject.SHADOW_TYPE_FILL,
						QuickShadowDrawObject.SHADOW_TYPE_BLACK,
						QuickShadowDrawObject.SHADOW_TYPE_GRAY,
						QuickShadowDrawObject.SHADOW_TYPE_WHITE
				};
			}
			public String getLabel(int v) {
				switch (v) {
				case QuickShadowDrawObject.SHADOW_TYPE_NONE: return ToolUtilities.messages.getString("options.ShadowType.NONE");
				case QuickShadowDrawObject.SHADOW_TYPE_STROKE: return ToolUtilities.messages.getString("options.ShadowType.STROKE");
				case QuickShadowDrawObject.SHADOW_TYPE_FILL: return ToolUtilities.messages.getString("options.ShadowType.FILL");
				case QuickShadowDrawObject.SHADOW_TYPE_BLACK: return ToolUtilities.messages.getString("options.ShadowType.BLACK");
				case QuickShadowDrawObject.SHADOW_TYPE_GRAY: return ToolUtilities.messages.getString("options.ShadowType.GRAY");
				case QuickShadowDrawObject.SHADOW_TYPE_WHITE: return ToolUtilities.messages.getString("options.ShadowType.WHITE");
				default: return null;
				}
			}
		});
		f.add(new IntegerOption() {
			public String getName() {
				return ToolUtilities.messages.getString("options.ShadowOpacity");
			}
			public int getMaximum() {
				return 255;
			}
			public int getMinimum() {
				return 0;
			}
			public int getStep() {
				return 1;
			}
			public int getValue() {
				return QuickShadowUI.this.tc.getShadowOpacity();
			}
			public void setValue(int v) {
				QuickShadowUI.this.tc.setShadowOpacity(v);
			}
			public boolean useSlider() {
				return true;
			}
		});
		f.add(new IntegerOption() {
			public String getName() {
				return ToolUtilities.messages.getString("options.ShadowXOffset");
			}
			public int getMaximum() {
				return Integer.MAX_VALUE;
			}
			public int getMinimum() {
				return 0;
			}
			public int getStep() {
				return 1;
			}
			public int getValue() {
				return QuickShadowUI.this.tc.getShadowXOffset();
			}
			public void setValue(int v) {
				QuickShadowUI.this.tc.setShadowXOffset(v);
			}
			public boolean useSlider() {
				return false;
			}
		});
		f.add(new IntegerOption() {
			public String getName() {
				return ToolUtilities.messages.getString("options.ShadowYOffset");
			}
			public int getMaximum() {
				return Integer.MAX_VALUE;
			}
			public int getMinimum() {
				return 0;
			}
			public int getStep() {
				return 1;
			}
			public int getValue() {
				return QuickShadowUI.this.tc.getShadowYOffset();
			}
			public void setValue(int v) {
				QuickShadowUI.this.tc.setShadowYOffset(v);
			}
			public boolean useSlider() {
				return false;
			}
		});
		setLayout(new GridLayout(1,1));
		add(fui = new FormUI(f, mini));
		tc.addToolContextListener(this);
	}
	
	public void modeChanged(ToolContext src, boolean drawMode) {}
	
	public void toolChanged(ToolContext src, Tool previousTool, Tool nextTool) {}
	
	public void toolDoubleClicked(ToolContext src, Tool tool) {}
	
	public void toolSettingsChanged(ToolContext src, long delta) {
		if ((delta & ToolContextConstants.CHANGED_QUICKSHADOW) != 0L) {
			fui.update();
		}
	}
}
