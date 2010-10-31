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

package com.kreative.paint.filter;

import java.util.List;
import java.util.Vector;
import java.util.Collections;
import java.util.Comparator;
import com.kreative.paint.res.MaterialsManager;

public class FilterManager {
	private List<Filter> filters;
	
	public FilterManager(MaterialsManager mm) {
		filters = new Vector<Filter>();
		filters.add(new BlurFilter());
		filters.add(new BrightenFilter());
		filters.add(new BrightnessContrastFilter());
		filters.add(new ChannelMixerFilter());
		filters.add(new ColorTransformFilter());
		filters.add(new DarkenFilter());
		filters.add(new DiffuseFilter());
		filters.add(new DitherFilter(mm));
		filters.add(new FauxDarkenFilter());
		filters.add(new FauxDesaturateFilter());
		filters.add(new FauxLightenFilter());
		filters.add(new FauxTranslucentFilter());
		filters.add(new GameOfLifeFilter());
		filters.add(new GrayscaleFilter());
		filters.add(new HueSaturationFilter());
		filters.add(new InvertFilter());
		filters.add(new InvertGraysFilter());
		filters.add(new LightenFilter());
		filters.add(new MosaicFilter());
		filters.add(new OffsetFilter());
		filters.add(new RippleFilter());
		filters.add(new WireWorldFilter());
		filters.addAll(mm.getPluginFilters());
		Collections.sort(filters, new Comparator<Filter>() {
			public int compare(Filter o1, Filter o2) {
				int i = o1.getCategory().compareToIgnoreCase(o2.getCategory());
				if (i == 0) i = o1.getName().compareToIgnoreCase(o2.getName());
				return i;
			}
		});
	}
	
	public List<Filter> getFilters() {
		return filters;
	}
}
