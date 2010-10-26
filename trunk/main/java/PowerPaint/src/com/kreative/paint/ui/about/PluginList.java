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

package com.kreative.paint.ui.about;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import com.kreative.paint.filter.Filter;
import com.kreative.paint.filter.FilterManager;
import com.kreative.paint.format.Format;
import com.kreative.paint.format.FormatManager;
import com.kreative.paint.tool.ToolManager;
import com.kreative.paint.ui.UIUtilities;

public class PluginList extends JList {
	private static final long serialVersionUID = 1L;
	
	public PluginList(FormatManager fmt, FilterManager ftr, ToolManager tm) {
		Vector<Format> fmts = new Vector<Format>();
		fmts.addAll(fmt.toSortedList());
		Collections.sort(fmts, new Comparator<Format>() {
			public int compare(Format o1, Format o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		Vector<Filter> ftrs = new Vector<Filter>();
		ftrs.addAll(ftr.getFilters());
		Collections.sort(ftrs, new Comparator<Filter>() {
			public int compare(Filter o1, Filter o2) {
				int cmp = o1.getCategory().compareToIgnoreCase(o2.getCategory());
				if (cmp != 0) return cmp;
				else return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		Vector<Object> things = new Vector<Object>();
		things.add(UIUtilities.messages.getString("about.plugins.formats"));
		things.addAll(fmts);
		things.add(UIUtilities.messages.getString("about.plugins.filters"));
		things.addAll(ftrs);
		things.add(UIUtilities.messages.getString("about.plugins.tools"));
		things.addAll(tm.getTools());
		setListData(things);
		setCellRenderer(new PluginListCellRenderer());
		setFixedCellHeight(20);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
}
